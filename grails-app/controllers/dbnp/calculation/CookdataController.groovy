package dbnp.calculation

import org.apache.jasper.compiler.Node.ParamsAction
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import grails.plugin.springsecurity.annotation.Secured
import dbnp.authentication.AuthenticationService
import dbnp.studycapturing.Study
import dbnp.studycapturing.SamplingEvent
import dbnp.studycapturing.EventGroup
import dbnp.studycapturing.Sample
import grails.converters.JSON
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.servlet.http.HttpServletResponse
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Cookdata Controller
 * Allows the user to select datasets, combine these with calculations,
 * and download the results.
 * 
 * Uses ajaxflow for the wizard.
 */
@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class CookdataController {
    // the pluginManager is used to check if the Grom
    // plugin is available so we can 'Grom' development
    // notifications to the unified notifications daemon
    // (see http://www.grails.org/plugin/grom)
    def pluginManager
    def authenticationService
    def moduleCommunicationService
    def cookdataService

    /**
     * index method, redirect to the webflow
     * @void
     */
    def index = {
        // Grom a development message
        if (pluginManager.getGrailsPlugin('grom')) "redirecting into the webflow".grom()

        redirect(action: 'pages')
    }

    /**
     * WebFlow definition
     * @void
     */
    def pagesFlow = {
        // start the flow
        onStart {
            // Grom a development message
            if (pluginManager.getGrailsPlugin('grom')) "entering the WebFlow".grom()

            // define variables in the flow scope which is availabe
            // throughout the complete webflow also have a look at
            // the Flow Scopes section on http://www.grails.org/WebFlow
            //
            // The following flow scope variables are used to generate
            // wizard tabs. Also see common/_tabs.gsp for more information
            flow.page = 0
            flow.pages = [
                    [title: 'Select Assays'],
                    [title: 'Select Sampling Events'],
                    [title: 'Build Datasets'],
                    [title: 'Select Download Format'],
                    [title: 'Done']
            ]
            flow.cancel = true;
            flow.quickSave = true;

            flow.studies = Study.giveReadableStudies(authenticationService.getLoggedInUser())

            success()
        }

        // render the main wizard page which immediately
        // triggers the 'next' action (hence, the main
        // page dynamically renders the study template
        // and makes the flow jump to the study logic)
        mainPage {
            render(view: "/cookdata/index")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) "rendering the main Ajaxflow page (index.gsp)".grom()

                // let the view know we're in page 1
                flow.page = 1
                success()
            }
            on("next").to "pageOne"
        }

        // first wizard page
        pageOne {
            render(view: "_page_one")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial: pages/_page_one.gsp".grom()

				// The user should select a study
                flow.user = authenticationService.getLoggedInUser()
                flow.studies = Study.giveReadableStudies(flow.user)
                // TODO: check if readable for this user

                flow.page = 1
                success()
            }
            on("next") {
                // The user has selected a study
                flow.study = Study.get(params.selectStudy)
				
				// Retrieve the study's metadata, so that the user can refine their
				// Selection on the next page
                flow.eventGroups = flow.study.eventGroups // retain order of event groups as defined in study
                flow.assays = []
				
				// Only copy those assays that the user has selected on the previous page
                flow.study.assays.each{
                    if(params["assay_"+it.id].equals("on")) {
                        flow.assays.add(it)
                    }
                }
                flow.samplingEvents = flow.study.samplingEvents // sampling events order will be retained as defined in study
                flow.samplingEventTemplates = flow.samplingEvents*.template.unique() // Has meaningful order
                flow.samplingEventFields = cookdataService.retrieveInterestingFieldsList(flow.samplingEvents)

            }.to "pageTwo"
        }

        // second wizard page
        pageTwo {
            render(view: "_page_two")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial: pages/_page_two.gsp".grom()

                flow.page = 2
                success()
            }
            on("next") {
				/* User has chosen which sampling event and
				 * event group pairs they wish to compose datasets with
				 * The user has selected pairs of sampling events and event groups
				 */
				
				
				/* selectedSamplingEvents will contain only sampling events that occur 
				 * in selections. It is used for getting the interesting assays later on.
				 * Does not have a meaningful order.
				 */
				List selectedSamplingEvents = []
				
				/* We will compose a list of selection triples for use later in the flow,
				 * based ib the user's input
				 * Each triple will consist of a sampling event, an event group and the 
				 * number of samples in that selection 
				 */
				flow.selectionTriples = []
				
				params.each{ key, val ->
                    if(val=="on"){
                        def splitKey = key.split("_")
                        def se = Integer.valueOf(splitKey[0]) // sampl. ev.
                        def eg = Integer.valueOf(splitKey[1]) // ev. group
                        def numItems = Sample.createCriteria().get {
                            projections {
                                count('id')
                            }
                            eq("parentEvent", flow.samplingEvents[se])
                            eq("parentEventGroup", flow.eventGroups[eg])
                        }
                        flow.selectionTriples.add(
                                [
                                        se,
                                        eg,
                                        numItems
                                ]
                        )
                        selectedSamplingEvents.add(flow.samplingEvents[se])
                    }
                }
                flow.selectedSamplingEvents = selectedSamplingEvents.unique()

                // Update samplingEvents list
                flow.samplingEventTemplates = []
                flow.selectionTriples.each{
                    flow.samplingEventTemplates.add(flow.samplingEvents[it[0]].template)
                }
                flow.samplingEventTemplates = flow.samplingEventTemplates.unique()


                // Reset page three contents, as these may well be out of date
                flow.pageThreeDatasetTableHtml = null
                flow.pageThreeDatasetCounter = null
            }.to "pageThree"
            on("previous"){
                flow.mapSelectionSets = [:]
                flow.selectionTriples = []
                flow.study = null
                flow.eventGroups = []
                flow.samplingEvents = []
                flow.samplingEventFields = []
                flow.samplingEventTemplates = []
            }.to "pageOne"
        }

        // second wizard page
        pageThree {
            render(view: "_page_three")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_page_three.gsp".grom()

                flow.page = 3
                success()
            }
            on("next"){
                // Saving some of the contents, so that these can be placed in the page when the user goes back to it
                if(params.datasetTableHtml)   {
                    flow.pageThreeDatasetTableHtml = params.datasetTableHtml
                }
                if(params.datasetCounter)   {
                    flow.pageThreeDatasetCounter = params.datasetCounter
                }

	            flash.wizardErrors = []

				List listToBeComputed = []
                List samples = [] /* Will be compiled based on the user's 
                	selections, so that we can retrieve all interesting 
                	measurements in one call per module */
				
                if(params.dataset_equa.class == String) {
                    // In case only one dataset was selected, we will now have a String
                    // We need a list
                    params.dataset_name = [params.dataset_name]
                    params.dataset_equa = [params.dataset_equa]
                    params.dataset_aggr = [params.dataset_aggr]
                    params.dataset_grpA = [params.dataset_grpA]
                    params.dataset_grpB = [params.dataset_grpB]
                }
				
                
				
				int numItems = params.dataset_equa.size()
                flow.mapSelectionSets = ["A":[], "B":[]]
                flow.mapEquations = [:]

				// Parse the user's input, gather requested items
				try {
					// For each dataset and equation, gather the required samples
					// Package everything up in a map and add the map to listToBeComputed
	                for(int k = 0; k < numItems; k++){
						
						/* Some aggregation types require both dataset groups
							to actually contain content.
						*/
						boolean blnRestrictiveAggrType = false
						if(params.dataset_aggr[k].equals("average") ||
							params.dataset_aggr[k].equals("median") ||
							params.dataset_aggr[k].equals("pairwise")){
							blnRestrictiveAggrType = true
						}
							
						// Get the samples, per group
	                    List samplesA = cookdataService.getSamplesForDatasetGroup(params.dataset_grpA[k], 
							blnRestrictiveAggrType, flow.selectionTriples, flow.samplingEvents, flow.eventGroups)
		                List samplesB = cookdataService.getSamplesForDatasetGroup(params.dataset_grpB[k], 
							blnRestrictiveAggrType, flow.selectionTriples, flow.samplingEvents, flow.eventGroups)

						// Add the samples here so that we can request their measurements in one call
						samples.addAll(samplesA)
						samples.addAll(samplesB)
						
						// Queue what we just gathered for processing
	                    Map mapInfo = [
	                            "datasetName" : params.dataset_name[k],
	                            "equation" : params.dataset_equa[k],
	                            "aggr" : params.dataset_aggr[k],
	                            "samplesA" : samplesA,
	                            "samplesB" : samplesB
	                    ]
	                    listToBeComputed.add(mapInfo)
	                }
	
		            // Check if samples are present
		            if (!samples) {
			            throw new IllegalArgumentException("Based on your selections, no samples could be found. Because of that, there was nothing to compute.")
		            }

                    /* Uniquefy the list of samples. We used the datasets to build up this list, and samples may be
                        present in more that one dataset. The samples list, however, should not contain duplicates
                        because it will be used to request data from modules */
                    samples = samples.unique()

		            // Check which assays we need.
	                flow.assays = cookdataService.getInterestingAssays(flow.study, flow.selectedSamplingEvents, flow.assays)
					
					// Get the measurements, per sampletoken, per feature
                    Map mapSampleTokenToMeasurementPerFeature = cookdataService.getDataFromModules(flow.assays, samples)
					
					/* The flow.results format is as follows:
					* - list of result sets (pairs)
					* 	|- dataset item information (list)
					* 	|	|- datasetName
					* 	|	|- aggr
					* 	|	|- ...
					*	|
					* 	|- list of dataset item results (pairs)
					* 		|- feature
					*		|- value
					* To reach the dataset name of a specific resultset, you would do
					* flow.results[someIndex][0].datasetName
					* To reach a feature-measurement pair, you would do
					* flow.results[someIndex][1][someIndex]
					*/
	                flow.results = cookdataService.getResults(listToBeComputed, mapSampleTokenToMeasurementPerFeature)

		            success()
                }
                catch (Exception e) {
                    log.error("CookdataController: Error caught while computing results: ${e.getMessage()}")
                    flash.wizardErrors << e
                    error()
                }
            }.to "pageFour"
            on("previous"){
                // Reset samplingEvent selection, otherwise some options on page two will have become inaccessible
                flow.samplingEventTemplates = flow.samplingEvents*.template.unique()
            }.to "pageTwo"
        }

        // second wizard page
        pageFour {
            render(view: "_page_four")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_page_four.gsp".grom()
				session.results = null
                flow.page = 4
                success()
            }
            on("previous").to "pageThree"
			on("downloadOneResultAsExcel"){
				session.results = flow.results[Integer.valueOf(params.downloadResultId)]
				redirect(action: 'downloadExcel')
			}.to "pageFour"
			on("downloadAllResultsAsZip"){
				session.results = flow.results
                session.studyCode = flow.study.code
				redirect(action: 'downloadExcelsInZip')
			}.to "pageFour"
            on("downloadMeanAndMedianResults"){
                session.results = flow.results
                redirect(action: 'downloadMeanAndMedianResults')
            }.to "pageFour"
        }
		
        // render errors
        error {
            render(view: "_error")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_error.gsp".grom()

                // set page to the number of pages minus one, so that the 
				// navigation works (it is disabled on the final page)
                flow.page = 3
            }
            on("next").to "save"
            on("previous").to "pageFour"
            on("toPageOne").to "pageOne"
            on("toPageTwo").to "pageTwo"
            on("toPageThree").to "pageThree"
            on("toPageFour").to "pageFour"
            on("toPageFive").to "save"

        }

        // last wizard page
        finalPage {
            render(view: "_final_page")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_final_page.gsp".grom()

                success()
            }
        }
    }
	
	/**
	 * Writes an excel file containing computation results for one dataset, with
	 * additional information on the first row, and sends it back to the client
	 * as an attachment. 
	 */
	def downloadExcel = {
        def datasetName = session.results[0].datasetName
        response.setHeader "Content-disposition", "attachment;filename=\""+datasetName+".xlsx\""
        response.setContentType "application/octet-stream"
        def type = session.results[0].aggr
        if(type=="values"){
            cookdataService.writeValuesToStream(response.getOutputStream(), session.results[1])
        }
        if(type=="average" || type=="median"){
            cookdataService.writeAverageOrMedianToStream(response.getOutputStream(), session.results[1], datasetName)
        }
        if(type=="pairwise"){
            cookdataService.writePairwiseToStream(response.getOutputStream(), session.results[1])
        }

        response.outputStream.flush()
        session.results = null
	}

    /**
     * Writes an excel file containing the computation results for the "average" and "median" aggregation types, for
     * each dataset. Sends it back to the client as an attachment.
     */
    def downloadMeanAndMedianResults = {
        response.setHeader "Content-disposition", "attachment;filename=\"median_and_average.xlsx\""
        response.setContentType "application/octet-stream"
        cookdataService.writeMeanAndMedianResultsToStream(response.getOutputStream(), session.results)
        response.outputStream.flush()
        session.results = null
    }

  /**
	* Writes a zip file containing excel files with measurements, and sends it 
	* back to the client as an attachment.
	* It is possible that the zip file will end up containing just one file.
	* TODO: when "pairwise" is implemented, write these results to files of their own.
	* <p>
	* <pre>
	* The session.results format is as follows:
	* - list of result sets (pairs)
	* 	|- dataset item information (list)
	* 	|	|- datasetName
	* 	|	|- aggr
	* 	|	|- ...
	*	|
	* 	|- list of dataset item results (pairs) 
	* 		|- feature
	*		|- value
	* </pre>
	* <p>
	* To reach the dataset name of a specific resultset, you would do
	* <br>
	* session.results[someIndex][0].datasetName
	* <p>
	* To reach a feature-measurement pair, you would do
	* <br>
	* session.results[someIndex][1][someIndex]
	*/
    def downloadExcelsInZip = {
		// Prepare for writing to zip
		ByteArrayOutputStream zipByteArrOutStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutStream = new ZipOutputStream(zipByteArrOutStream);
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("EEE_d_MMM_yyyy_HH_mm_ss");
        String strFilename = session.studyCode+"_"+df.format(date)+".zip"
		
		// Let the service write to the stream
		cookdataService.writeZipOfAllResults(zipOutStream, session.results)
		
		// Finish up, send attachment to client
		session.results = null
        session.studyCode = null
		zipOutStream.close();
		response.setHeader "Content-disposition", "attachment;filename=\"${strFilename}\""
		response.setContentType "application/octet-stream"
		response.outputStream.write(zipByteArrOutStream.toByteArray())
		response.outputStream.flush()
	}

    def testEquation = {
        println "testEquation params: "+params
        // Tests if an equation can be parsed
        // Uses arbitrary values for testing purposes.
        boolean success = true
        String equation = params.equation.replaceAll("\\s",""); // No whitespace
        try{
            double res = cookdataService.computeWithVals(equation, 5.0, 10.0)
        } catch (Exception e){
            // No joy
            log.error("CookdataController: testEquation: " + e)
            success = false
        }
        Map mapResults = [:]
        mapResults.put("status", success)
        render mapResults as JSON
    }

    def getAssays = {
        // Get the assays of a study

        def study = Study.get(params.selectStudy)
        def assayList = [];
        study.assays.each{
            assayList.add([name: it.name, assayUUID: it.UUID, modulename: it.module.name, id: it.id]);
        }

        Map mapResults = [:]
        mapResults.put("assays", assayList)
        mapResults.put("studyId", study.id)
        render mapResults as JSON
    }
}
