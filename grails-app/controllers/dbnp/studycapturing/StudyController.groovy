package dbnp.studycapturing

import grails.plugin.springsecurity.annotation.Secured
import grails.converters.JSON
import dbnp.authentication.SecUser
import org.dbnp.gdt.*

/**
 * Controller class for studies
 */
class StudyController {
    def authenticationService
    def grailsApplication
    def datatablesService
    def studyEditService

    //static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    /**
     * Shows all studies where the user has access to
     */
    def list = {
        def user = authenticationService.getLoggedInUser()
        def max = Math.min(params.max ? params.int('max') : 10, 100)
        def offset = params.offset ? params.int( 'offset' ) : 0
        def studies = Study.giveReadableStudies( user, max, offset );
        [studyInstanceList: studies, studyInstanceTotal: Study.countReadableStudies( user ), loggedInUser: user]
    }

    /**
     * Returns a list of study ids and titles that the current user can read
     * 
     * For each study, the id and title are returned. The output format is JSON
     */
    def json() {
        def studies = Study.giveReadableStudies( authenticationService.getLoggedInUser() ).collect { study ->
            [
                id: study.id,
                title: study.title
            ]
        }

        render studies as JSON
    }

    /**
     * Shows studies for which the logged in user is the owner
     */
    @Secured(['IS_AUTHENTICATED_REMEMBERED'])
    def myStudies() {
        def user = authenticationService.getLoggedInUser()
        def max = Math.min(params.max ? params.int('max') : 10, 100)
        def offset = params.offset ? params.int( 'offset' ) : 0

        def studies = Study.findAllByOwner(user, [max:max,offset: offset]);
        render( view: "list", model: [studyInstanceList: studies, studyInstanceTotal: studies.size(), loggedInUser: user] )
    }

    
    /***********************************************
     *
     * Different parts of the view process
     *
     ***********************************************/

    /**
     * Shows the properties page to edit study details
     * @return
     */
    def show() {
        def study = getStudyFromRequest( params )

        [ study: study, loggedInUser: authenticationService.getLoggedInUser() ]
    }

    /**
     * Shows the overview page to view subject details.
     * @return
     */
    def subjects() {
        prepareDataForDatatableView( Subject )
    }

    /**
     * Shows the design page in a given study
     */
    def design() {
        def study = getStudyFromRequest( params )
        [study: study, loggedInUser: authenticationService.getLoggedInUser()]
    }

    /**
     * Shows the sample overview page a given study
     * @return
     */
    def samples() {
        prepareDataForDatatableView( Sample )
    }

    /**
     * Shows the assay page in the show process
     */
    def assays() {
        prepareDataForDatatableView( Assay )
    }

    /**
     * Returns data for a templated datatable. The type of entities is based on the template given.
     * @return
     */
    def dataTableEntities() {
        def datatableData = getTemplatedDatatablesData()
        render datatableData as JSON
    }
    
    /**
     * Returns data for a templated datatable. The type of entities is based on the template given.
     * @return
     */
    def dataTableAssays() {
        def datatableData = getTemplatedDatatablesData({ assay ->
            def data = datatablesService.defaultEntityFormatter(assay)
            
            // Remove the first column (ID) and add a link to this assay for the details column
            data.tail() + g.link( url: assay.module.baseUrl + "/assay/showByToken/" + assay.UUID, "Details" ) 
        })
        
        render datatableData as JSON
    }

    /**
     * Returns data for a templated datatable. The type of entities is based on the template given.
     * @return
     */
    protected def getTemplatedDatatablesData(Closure formatter = null) {
        def template = Template.read( params.long( "template" ) )
        def study = Study.read( params.long( "id" ) )

        if( !study ) {
            render dataTableError( "Invalid study given: " + study ) as JSON
            return
        }

        if( !template ) {
            render dataTableError( "Invalid template given: " + template ) as JSON
            return
        }

        def searchParams = datatablesService.parseParams( params )

        // Retrieve the data itself
        def data = studyEditService.getEntitiesForTemplate( searchParams, study, template )

        // Format the data to be used in the datatable. If a custom formatter is given, use that one
        def datatableData
        if( formatter ) {
            datatableData = datatablesService.createDatatablesOutput( data, params, formatter )
        } else {
            datatableData = datatablesService.createDatatablesOutputForEntities( data, params )
            
            // Remove the IDs, as they are irrelevant for now
            datatableData.aaData = datatableData.aaData.collect { it.tail() }
        }
        
        datatableData
    }
    
    /**
     * Prepares the data for the datatable view
     * @param entityClass       Class for the type of entities to show. E.g. Subject
     * @return  a list of data to return to the view
     */
    protected def prepareDataForDatatableView( entityClass ) {
        def study = getStudyFromRequest( params )
        if( !study ) {
            redirect action: "add"
            return
        }

        // Check the distinct templates for these entities, without loading all
        // entities for efficiency reasons
        def templates = entityClass.executeQuery("select distinct s.template from " + entityClass.simpleName + " s WHERE s.parent = ?", [study ])

        [
            study: study,
            templates: templates,
            domainFields: entityClass.domainFields,
            loggedInUser: authenticationService.getLoggedInUser()
        ]

    }

    /**
     * Returns an error response for the datatable
     * @param error
     * @return
     */
    protected def dataTableError( error ) {
        return [
            sEcho:                params.sEcho,
            iTotalRecords:        0,
            iTotalDisplayRecords: 0,
            aaData:               [],
            errorMessage:         error
        ]
    }

    /**
     * Retrieves the required study from the database or return an empty Study object if
     * no id is given
     *
     * @param params    Request parameters with params.id being the ID of the study to be retrieved
     * @return                  A study from the database or an empty study if no id was given
     */
    protected Study getStudyFromRequest(params) {
        SecUser user = authenticationService.getLoggedInUser();
        Study study  = (params.containsKey('id')) ? Study.findById(params.get('id')) : new Study(title: "New study", owner: user);

        // got a study?
        if (!study) {
            flash.error = "No study found with given id";
            redirect controller: "study", action: "list"
        } else if(!study.canRead(user)) {
            flash.error = "No authorization to view this study."
            study = null;
            redirect controller: "study", action: "list"
        }

        return study;
    }
    
    
    /**
     * Shows a comparison of multiple studies using the show view
     * 
     */
    def list_extended = {
        def id = (params.containsKey('id')) ? params.get('id') : 0;
        def numberOfStudies = Study.count()
        def studyList;

        // do we have a study id?
        if (id == 0) {
            // no, go back to the overview
            redirect(action: 'list');
        } else if (id instanceof String) {
            // yes, one study. Show it
            redirect(action: 'show', id: id)
        } else {
            // multiple studies, compare them
            def c = Study.createCriteria()
            studyList = c {
                'in'("id", id.collect { Long.parseLong(it) })
            }
            render(view:'show',model:[studyList: studyList, studyInstanceTotal: numberOfStudies, multipleStudies:(studyList instanceof ArrayList)])
        }
    }

    def showByToken = {
        def studyInstance = Study.findWhere(UUID: params.id)
        if (!studyInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
            redirect(action: "list")
        }
        else {
            // Check whether the user may see this study
            def loggedInUser = authenticationService.getLoggedInUser()
            if( !studyInstance.canRead(loggedInUser) ) {
                flash.message = "You have no access to this study"
                redirect(action: "list")
            }

            redirect(action: "show", id: studyInstance.id)
        }
    }

    def delete = {
        def studyInstance = Study.get(params.long("id"))
        if (studyInstance) {
            try {
                studyInstance.clearSAMDependencies()
                studyInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
            redirect(action: "list")
        }
    }

    /**
     * Renders assay names and id's as JSON
     */
    def ajaxGetAssays = {
        def study = Study.read(params.id)

        // set output header to json
        response.contentType = 'application/json'

        render ((study?.assays?.collect{[name: it.name, id: it.id]} ?: []) as JSON)
    }

    /**
     * Exports all data from the given studies to excel. This is done using a redirect to the 
     * assay controller
     * 
     * @param	ids				ids of the studies to export
     * @param	params.format	"list" in order to export all assays in one big excel sheet
     * 							"sheets" in order to export every assay on its own sheet (default)
     * @see		AssayController.exportToExcel
     */
    def exportToExcel = {
        def ids = params.list( 'ids' ).findAll { it.isLong() }.collect { Long.valueOf( it ) };
        def tokens = params.list( 'tokens' );

        if( !ids && !tokens ) {
            flash.errorMessage = "No study ids given";
            redirect( controller: "assay", action: "errorPage" );
            return;
        }

        // Find all assay ids for these studies
        def assayIds = [];
        ids.each { id ->
            def study = Study.get( id );
            if( study ) {
                assayIds += study.assays.collect { assay -> assay.id }
            }
        }

        // Also accept tokens for defining studies
        tokens.each { token ->
            def study = Study.findWhere(UUID: token)
            if( study )
                assayIds += study.assays.collect { assay -> assay.id }
        }

        if( !assayIds ) {
            flash.errorMessage = "No assays found for the given studies";
            redirect( controller: "assay", action: "errorPage" );
            return;
        }

        // Create url to redirect to
        def format = params.get( "format", "sheets" )
        redirect( controller: "assay", action: "exportToExcel", params: [ "format": format, "ids": assayIds ] );
    }
}
