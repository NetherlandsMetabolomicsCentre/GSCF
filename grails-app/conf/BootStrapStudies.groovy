/**
 * @Author kees
 * @Since Jun 25, 2010
 *
 * Revision information:
 * $Rev: $
 * $Author: $
 * $Date: $
 */

import dbnp.studycapturing.*
import dbnp.data.Term
import dbnp.data.Ontology

class BootStrapStudies {

	/**
	 * Add example studies. This function is meant to be called only in development mode
	 */

	public static void addExampleStudies() {

		// Look up the used ontologies which should be in the database by now
		def speciesOntology = Ontology.getOrCreateOntologyByNcboId(1132)
		def brendaOntology = Ontology.getOrCreateOntologyByNcboId(1005)
		def nciOntology = Ontology.getOrCreateOntologyByNcboId(1032)
		def chebiOntology = Ontology.getOrCreateOntologyByNcboId(1007)

		// Look up the used templates which should also be in the database by now
		def studyTemplate = Template.findByName("Academic study")
		def mouseTemplate = Template.findByName("Mouse")
		def humanTemplate = Template.findByName("Human")
		def dietTreatmentTemplate = Template.findByName("Diet treatment")
		def boostTreatmentTemplate = Template.findByName("Compound challenge")
		def liverSamplingEventTemplate = Template.findByName("Liver extraction")
		def fastingTreatmentTemplate = Template.findByName("Fasting treatment")
		def bloodSamplingEventTemplate = Template.findByName("Blood extraction")
		def humanBloodSampleTemplate = Template.findByName("Human blood sample")
		

		// Add terms manually, to avoid having to do many HTTP requests to the BioPortal website
		println ".adding terms"


		def mouseTerm = new Term(
			name: 'Mus musculus',
			ontology: speciesOntology,
			accession: '10090'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def humanTerm = new Term(
			name: 'Homo sapiens',
			ontology: speciesOntology,
			accession: '9606'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def arabTerm = new Term(
			name: 'Arabidopsis thaliana',
			ontology: speciesOntology,
			accession: '3702'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def tomatoTerm = new Term(
			name: 'Solanum lycopersicum',
			ontology: speciesOntology,
			accession: '4081'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def potatoTerm = new Term(
			name: 'Solanum tuberosum',
			ontology: speciesOntology,
			accession: '0000'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def bloodTerm = new Term(
			name: 'blood plasma',
			ontology: brendaOntology,
			accession: 'BTO:0000131'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def c57bl6Term = new Term(
			name: 'C57BL/6 Mouse',
			ontology: nciOntology,
			accession: 'C14424'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def glucoseTerm = new Term(
			name: 'Glucose',
			ontology: chebiOntology,
			accession: 'CHEBI:17234'
		).with { if (!validate()) { errors.each { println it} } else save()}

		// Create a few persons, roles and Affiliations
		println ".adding persons, roles and affiliations"
		def affiliation1 = new PersonAffiliation(
			institute: "Science Institute NYC",
			department: "Department of Mathematics"
		).save();
		def affiliation2 = new PersonAffiliation(
			institute: "InfoStats GmbH, Hamburg",
			department: "Life Sciences"
		).save();
		def role1 = new PersonRole(
			name: "Principal Investigator"
		).save();
		def role2 = new PersonRole(
			name: "Statician"
		).save();

		// Create persons
		def person1 = new Person(
			lastName: "Scientist",
			firstName: "John",
			gender: "Male",
			initials: "J.R.",
			email: "john@scienceinstitute.com",
			phone: "1-555-3049",
			address: "First street 2,NYC"
		)
		.addToAffiliations( affiliation1 )
		.addToAffiliations( affiliation2 )
		.save();

		def person2 = new Person(
			lastName: "Statician",
			firstName: "Jane",
			gender: "Female",
			initials: "W.J.",
			email: "jane@statisticalcompany.de",
			phone: "49-555-8291",
			address: "Dritten strasse 38, Hamburg, Germany"
		)
		.addToAffiliations( affiliation2 )
		.save();

		// Create 30 persons to test pagination
		def personCounter = 1;
		30.times { new Person( firstName: "Person #${personCounter}", lastName: "Testperson", email: "email${personCounter++}@testdomain.com" ).save() }

		// Create a few publications
		println ".adding publications"
		def publication1 = new Publication(
			title: "Postnatal development of hypothalamic leptin receptors",
			authorsList: "Cottrell EC, Mercer JG, Ozanne SE.",
			pubMedID: "20472140",
			comments: "Not published yet",
			DOI: "unknown"
		)
		.save();

		def publication2 = new Publication(
			title: "Induction of regulatory T cells decreases adipose inflammation and alleviates insulin resistance in ob/ob mice",
			authorsList: "Ilan Y, Maron R, Tukpah AM, Maioli TU, Murugaiyan G, Yang K, Wu HY, Weiner HL.",
			pubMedID: "20445103",
			comments: "",
			DOI: ""
		)
		.save();

		// Add example mouse study
		println ".adding NuGO PPS3 leptin example study..."
		def mouseStudy = new Study(
			template: studyTemplate,
			title:"NuGO PPS3 mouse study leptin module",
			code:"PPS3_leptin_module",
			researchQuestion:"Leptin etc.",
			ecCode:"2007117.c",
			startDate: Date.parse('yyyy-MM-dd','2008-01-02'),
		)
		.with { if (!validate()) { errors.each { println it} } else save()}

		mouseStudy.setFieldValue('Description', "C57Bl/6 mice were fed a high fat (45 en%) or low fat (10 en%) diet after a four week run-in on low fat diet.");// After 1 week 10 mice that received a low fat diet were given an IP leptin challenge and 10 mice of the low-fat group received placebo injections. The same procedure was performed with mice that were fed the high-fat diet. After 4 weeks the procedure was repeated. In total 80 mice were culled." )
		mouseStudy.save()

		def evLF = new Event(
			startTime: 3600,
			endTime: 3600 +7 * 24 * 3600,
			template: dietTreatmentTemplate
		)
		.setFieldValue( 'Diet','low fat')
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evHF = new Event(
			startTime: 3600,
			endTime: 3600 +7 * 24 * 3600,
			template: dietTreatmentTemplate
		)
		.setFieldValue( 'Diet','high fat' )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evBV = new Event(
			startTime: 3600,
			endTime: 3600 +7 * 24 * 3600,
			template: boostTreatmentTemplate
		)
		.setFieldValue( 'Control','true' )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evBL = new Event(
			startTime: 3600,
			endTime: 3600 +7 * 24 * 3600,
			template: boostTreatmentTemplate
		)
		.setFieldValue( 'Control','false' )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evLF4 = new Event(
			startTime: 3600,
			endTime: 3600 + 4 * 7 * 24 * 3600,
			template: dietTreatmentTemplate
		)
		.setFieldValue( 'Diet','low fat')
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evHF4 = new Event(
			startTime: 3600,
			endTime: 3600 + 4 * 7 * 24 * 3600,
			template: dietTreatmentTemplate
		)
		.setFieldValue( 'Diet','high fat' )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evBV4 = new Event(
			startTime: 3600,
			endTime: 3600 + 4 * 7 * 24 * 3600,
			template: boostTreatmentTemplate
		)
		.setFieldValue( 'Control','true' )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evBL4 = new Event(
			startTime: 3600,
			endTime: 3600 + 4 * 7 * 24 * 3600,
			template: boostTreatmentTemplate
		)
		.setFieldValue( 'Control','false' )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evS = new SamplingEvent(
			startTime: 3600 +7 * 24 * 3600,
			endTime: 3600 +7 * 24 * 3600,
			template: liverSamplingEventTemplate)
		.setFieldValue('Sample weight',5F)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def evS4 = new SamplingEvent(
			startTime: 3600 +7 * 24 * 3600,
			endTime: 3600 +7 * 24 * 3600,
			template: liverSamplingEventTemplate)
		.setFieldValue('Sample weight',5F)
		.with { if (!validate()) { errors.each { println it} } else save()}

		// Add events to study
		mouseStudy
		.addToEvents(evLF)
		.addToEvents(evHF)
		.addToEvents(evBV)
		.addToEvents(evBL)
		.addToEvents(evLF4)
		.addToEvents(evHF4)
		.addToEvents(evBV4)
		.addToEvents(evBL4)
		.addToSamplingEvents(evS)
		.addToSamplingEvents(evS4)
		.save()

		def LFBV1 = new EventGroup(name:"10% fat + vehicle for 1 week")
		.addToEvents(evLF)
		.addToEvents(evBV)
		.addToEvents(evS)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def LFBL1 = new EventGroup(name:"10% fat + leptin for 1 week")
		.addToEvents(evLF)
		.addToEvents(evBL)
		.addToEvents(evS)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def HFBV1 = new EventGroup(name:"45% fat + vehicle for 1 week")
		.addToEvents(evHF)
		.addToEvents(evBV)
		.addToEvents(evS)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def HFBL1 = new EventGroup(name:"45% fat + leptin for 1 week")
		.addToEvents(evHF)
		.addToEvents(evBL)
		.addToEvents(evS)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def LFBV4 = new EventGroup(name:"10% fat + vehicle for 4 weeks")
		.addToEvents(evLF4)
		.addToEvents(evBV4)
		.addToEvents(evS4)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def LFBL4 = new EventGroup(name:"10% fat + leptin for 4 weeks")
		.addToEvents(evLF4)
		.addToEvents(evBL4)
		.addToEvents(evS4)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def HFBV4 = new EventGroup(name:"45% fat + vehicle for 4 weeks")
		.addToEvents(evHF4)
		.addToEvents(evBV4)
		.addToEvents(evS4)
		.with { if (!validate()) { errors.each { println it} } else save()}

		def HFBL4 = new EventGroup(name:"45% fat + leptin for 4 weeks")
		.addToEvents(evHF4)
		.addToEvents(evBL4)
		.addToEvents(evS4)
		.with { if (!validate()) { errors.each { println it} } else save()}

        // Add subjects and samples and compose EventGroups
		def x=1
		20.times {
			def currentSubject = new Subject(
				name: "A" + x++,
				species: mouseTerm,
				template: mouseTemplate,
			)
			.setFieldValue("Gender", "Male")
			.setFieldValue("Genotype", c57bl6Term)
			.setFieldValue("Age", 17)
			.setFieldValue("Cage", "" + (int)(x/2))
			.with { if (!validate()) { errors.each { println it} } else save(flush:true)}

			mouseStudy.addToSubjects(currentSubject)
			.with { if (!validate()) { errors.each { println it} } else save()}

			// Add subject to appropriate EventGroup
			if (x > 70) { HFBL4.addToSubjects(currentSubject).save() }
			else if (x > 60) { HFBV4.addToSubjects(currentSubject).save() }
			else if (x > 50) { LFBL4.addToSubjects(currentSubject).save() }
			else if (x > 40) { LFBV4.addToSubjects(currentSubject).save() }
			else if (x > 30) { HFBL1.addToSubjects(currentSubject).save() }
			else if (x > 20) { HFBV1.addToSubjects(currentSubject).save() }
			else if (x > 10) { LFBL1.addToSubjects(currentSubject).save() }
			else             { LFBV1.addToSubjects(currentSubject).save() }

			// Create sample
			def currentSample = new Sample(
				name: currentSubject.name + '_B',
				material: bloodTerm,
                                        template: humanBloodSampleTemplate,
				parentSubject: currentSubject,
				parentEvent: x > 40 ? evS4 : evS
			);
                        currentSample.setFieldValue( "Text on vial", "T" + (Math.random() * 100L) )

			mouseStudy.addToSamples(currentSample).with { if (!validate()) { errors.each { println it} } else save()}
		}

		// Add EventGroups to study
		mouseStudy
		.addToEventGroups(LFBV1)
		.addToEventGroups(LFBL1)
		.addToEventGroups(HFBV1)
		.addToEventGroups(HFBL1)
		.addToEventGroups(LFBV4)
		.addToEventGroups(LFBL4)
		.addToEventGroups(HFBV4)
		.addToEventGroups(HFBL4)

		// Add persons and publications to study
		def studyperson1 = new StudyPerson( person: person1, role: role1 ).save();
		def studyperson2 = new StudyPerson( person: person2, role: role2 ).save();

		mouseStudy
		.addToPersons( studyperson1 )
		.addToPersons( studyperson2 )
        .addToPublications( publication1 )
        .addToPublications( publication2 )
		.save()

		// Add example human study
		println ".adding NuGO PPSH example study..."

		def humanStudy = new Study(
			template: studyTemplate,
			title:"NuGO PPS human study",
			code:"PPSH",
			researchQuestion:"How much are fasting plasma and urine metabolite levels affected by prolonged fasting ?",
			description:"Human study",
			ecCode:"unknown",
			startDate: Date.parse('yyyy-MM-dd','2008-01-14'),
		)
		.setFieldValue( 'Description', "Human study performed at RRI; centres involved: RRI, IFR, TUM, Maastricht U." )
		.with { if (!validate()) { errors.each { println it} } else save()}

		def rootGroup = new EventGroup(name: 'Root group');

		def fastingEvent = new Event(
			startTime: 3 * 24 * 3600 + 22 * 3600,
			endTime: 3 * 24 * 3600 + 30 * 3600,
			template: fastingTreatmentTemplate)
		.setFieldValue('Fasting period','8h');


		def bloodSamplingEvent = new SamplingEvent(
			startTime: 3 * 24 * 3600 + 30 * 3600,
			endTime: 3 * 24 * 3600 + 30 * 3600,
			template: bloodSamplingEventTemplate)
		.setFieldValue('Sample volume',4.5F);

		rootGroup.addToEvents fastingEvent
		rootGroup.addToEvents bloodSamplingEvent
		rootGroup.save()

		def y = 1
		11.times {
			def currentSubject = new Subject(
				name: "" + y++,
				species: humanTerm,
				template: humanTemplate
			)
			.setFieldValue("Gender", (Math.random() > 0.5) ? "Male" : "Female")
			.setFieldValue("DOB", new java.text.SimpleDateFormat("dd-mm-yy").parse("01-02-19" + (10 + (int) (Math.random() * 80))))
			.setFieldValue("Age", 30)
			.setFieldValue("Height", Math.random() * 2F)
			.setFieldValue("Weight", Math.random() * 150F)
			.setFieldValue("BMI", 20 + Math.random() * 10F)
			.with { if (!validate()) { errors.each { println it} } else save()}

			rootGroup.addToSubjects currentSubject
			 rootGroup.save()

			def currentSample = new Sample(
				name: currentSubject.name + '_B',
				material: bloodTerm,
                                        template: humanBloodSampleTemplate,
				parentSubject: currentSubject,
				parentEvent: bloodSamplingEvent
			);
                                currentSample.setFieldValue( "Text on vial", "T" + (Math.random() * 100L) )

			humanStudy.addToSubjects(currentSubject).addToSamples(currentSample).with { if (!validate()) { errors.each { println it} } else save()}
		}

		humanStudy.addToEvents(fastingEvent)
		humanStudy.addToSamplingEvents(bloodSamplingEvent)
		humanStudy.addToEventGroups rootGroup


		// Add persons to study
		def studyperson3 = new StudyPerson( person: person1, role: role2 ).save();

		humanStudy
		.addToPersons( studyperson3 )
                        .addToPublications( publication2 )
		.save()

		// Add clinical data       ==> to be moved to SAM

		def lipidAssay = new dbnp.clinicaldata.ClinicalAssay(
			name: 'Lipid profile',
			approved: true
		).with { if (!validate()) { errors.each { println it} } else save()}

		def ldlMeasurement = new dbnp.clinicaldata.ClinicalMeasurement(
			name: 'LDL',
			unit: 'mg/dL',
			type: dbnp.data.FeatureType.QUANTITATIVE,
			referenceValues: '100 mg/dL',
			detectableLimit: 250,
			isDrug: false, isIntake: true, inSerum: true
		).with { if (!validate()) { errors.each { println it} } else save()}

		def hdlMeasurement = new dbnp.clinicaldata.ClinicalMeasurement(
			name: 'HDL',
			unit: 'mg/dL',
			type: dbnp.data.FeatureType.QUANTITATIVE,
			referenceValues: '50 mg/dL',
			detectableLimit: 100,
			isDrug: false, isIntake: true, inSerum: true
		).with { if (!validate()) { errors.each { println it} } else save()}

		lipidAssay.addToMeasurements ldlMeasurement
		lipidAssay.addToMeasurements hdlMeasurement

		def lipidAssayInstance = new dbnp.clinicaldata.ClinicalAssayInstance(
			assay: lipidAssay
		).with { if (!validate()) { errors.each { println it} } else save()}

		humanStudy.samples*.each {
			new dbnp.clinicaldata.ClinicalFloatData(
				assay: lipidAssayInstance,
				measurement: ldlMeasurement,
				sample: it.name,
				value: Math.round(Math.random()*ldlMeasurement.detectableLimit)
			).with { if (!validate()) { errors.each { println it} } else save()}

			new dbnp.clinicaldata.ClinicalFloatData(
				assay: lipidAssayInstance,
				measurement: hdlMeasurement,
				sample: it.name,
				value: Math.round(Math.random()*hdlMeasurement.detectableLimit)
			).with { if (!validate()) { errors.each { println it} } else save()}
		}

		// Add assay to study capture module

		def clinicalModule = new AssayModule(
			name: 'SAM module for clinical data',
			type: AssayType.SIMPLE_ASSAY,
			platform: 'clinical measurements',
			url: 'http://localhost:8182/sam'
		).with { if (!validate()) { errors.each { println it} } else save()}

		def lipidAssayRef = new Assay(
			name: 'Lipid profiling',
			module: clinicalModule,
			externalAssayID: 1
		)

		mouseStudy.samples*.each {
			lipidAssayRef.addToSamples(it)
		}

	        mouseStudy.addToAssays(lipidAssayRef);
		mouseStudy.save()

		lipidAssayRef.with { if (!validate()) { errors.each { println it} } else save()}

		def  glucoseAssay2Ref = new Assay(
			name: 'Glucose assay 2',
			module: clinicalModule,
			externalAssayID: 2
		)

		def  glucoseAssay3Ref = new Assay(
			name: 'Glucose assay 3',
			module: clinicalModule,
			externalAssayID: 3
		)

		humanStudy.addToAssays(glucoseAssay2Ref)
		humanStudy.addToAssays(glucoseAssay3Ref)
		humanStudy.save()

		humanStudy.samples*.each {
			glucoseAssay2Ref.addToSamples(it)
			glucoseAssay3Ref.addToSamples(it)
		}

		glucoseAssay2Ref.with { if (!validate()) { errors.each { println it} } else save()}
		glucoseAssay3Ref.with { if (!validate()) { errors.each { println it} } else save()}

	}

}