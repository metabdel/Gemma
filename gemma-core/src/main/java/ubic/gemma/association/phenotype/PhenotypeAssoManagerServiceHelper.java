package ubic.gemma.association.phenotype;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ubic.gemma.loader.entrez.pubmed.PubMedXMLFetcher;
import ubic.gemma.model.association.phenotype.ExperimentalEvidence;
import ubic.gemma.model.association.phenotype.ExternalDatabaseEvidence;
import ubic.gemma.model.association.phenotype.GenericEvidence;
import ubic.gemma.model.association.phenotype.GenericExperiment;
import ubic.gemma.model.association.phenotype.LiteratureEvidence;
import ubic.gemma.model.association.phenotype.PhenotypeAssociation;
import ubic.gemma.model.association.phenotype.UrlEvidence;
import ubic.gemma.model.association.phenotype.service.PhenotypeAssociationService;
import ubic.gemma.model.common.description.BibliographicReference;
import ubic.gemma.model.common.description.BibliographicReferenceService;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.common.description.CharacteristicService;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.model.genome.gene.phenotype.valueObject.CharacteristicValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.DiffExpressionEvidenceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.EvidenceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.ExperimentalEvidenceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.ExternalDatabaseEvidenceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.GenericEvidenceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.LiteratureEvidenceValueObject;
import ubic.gemma.model.genome.gene.phenotype.valueObject.UrlEvidenceValueObject;

/** This helper class is responsible to convert all types of EvidenceValueObjects to their corresponding entity */
@Component
public class PhenotypeAssoManagerServiceHelper {

    @Autowired
    private BibliographicReferenceService bibliographicReferenceService;

    @Autowired
    CharacteristicService characteristicService;

    @Autowired
    private PhenotypeAssociationService phenotypeAssociationService;

    private PubMedXMLFetcher pubMedXmlFetcher = new PubMedXMLFetcher();

    /**
     * Changes all type of evidenceValueObject to their corresponding entities
     * 
     * @param evidence the value object to change in an entity
     * @return PhenotypeAssociation the entity created from the ValueObject
     */
    public PhenotypeAssociation valueObject2Entity( EvidenceValueObject evidence ) {

        if ( evidence instanceof LiteratureEvidenceValueObject ) {
            return conversion2LiteratureEvidence( ( LiteratureEvidenceValueObject ) evidence );
        } else if ( evidence instanceof ExperimentalEvidenceValueObject ) {
            return conversion2ExperimentalEvidence( ( ExperimentalEvidenceValueObject ) evidence );
        } else if ( evidence instanceof GenericEvidenceValueObject ) {
            return conversion2GenericEvidence( ( GenericEvidenceValueObject ) evidence );
        } else if ( evidence instanceof UrlEvidenceValueObject ) {
            return conversion2UrlEvidence( ( UrlEvidenceValueObject ) evidence );
        } else if ( evidence instanceof DiffExpressionEvidenceValueObject ) {
            // TODO
            // return conversion2DifferentialExpressionEvidence (( DiffExpressionEvidenceValueObject ) evidence );
        } else if ( evidence instanceof ExternalDatabaseEvidenceValueObject ) {
            // TODO
            return conversion2ExternalDatabaseEvidence( ( ExternalDatabaseEvidenceValueObject ) evidence );
        }

        return null;

    }

    // TODO
    /**
     * @param evidenceValueObject the evidence we want to convert
     * @return PhenotypeAssociation the entity created from the ValueObject
     */
    private PhenotypeAssociation conversion2ExternalDatabaseEvidence(
            ExternalDatabaseEvidenceValueObject evidenceValueObject ) {

        // create the entity to populate
        ExternalDatabaseEvidence externalDatabaseEvidence = ExternalDatabaseEvidence.Factory.newInstance();

        // populate common field to all evidences
        populatePhenotypeAssociation( externalDatabaseEvidence, evidenceValueObject );

        // populate specific fields for this evidence
        externalDatabaseEvidence.setEvidenceSource( null );

        // TODO unsure
        // DatabaseEntry databaseEntry = DatabaseEntry.Factory.newInstance( accession, accessionVersion, Uri,
        // externalDatabase );

        return externalDatabaseEvidence;
    }

    /**
     * @param evidenceValueObject the evidence we want to convert
     * @return PhenotypeAssociation the entity created from the ValueObject
     */
    private PhenotypeAssociation conversion2UrlEvidence( UrlEvidenceValueObject evidenceValueObject ) {

        // create the entity to populate
        UrlEvidence urlEvidence = UrlEvidence.Factory.newInstance();

        // populate common field to all evidences
        populatePhenotypeAssociation( urlEvidence, evidenceValueObject );

        // populate specific fields for this evidence
        urlEvidence.setUrl( evidenceValueObject.getUrl() );

        return urlEvidence;
    }

    /**
     * @param evidenceValueObject the evidence we want to convert
     * @return PhenotypeAssociation the entity created from the ValueObject
     */
    private PhenotypeAssociation conversion2GenericEvidence( GenericEvidenceValueObject evidenceValueObject ) {

        // create the entity to populate
        GenericEvidence genericEvidence = GenericEvidence.Factory.newInstance();

        // populate common field to all evidences
        populatePhenotypeAssociation( genericEvidence, evidenceValueObject );

        return genericEvidence;
    }

    /**
     * @param evidenceValueObject the evidence we want to convert
     * @return PhenotypeAssociation the entity created from the ValueObject
     */
    private PhenotypeAssociation conversion2LiteratureEvidence( LiteratureEvidenceValueObject evidenceValueObject ) {

        // create the entity to populate
        LiteratureEvidence literatureEvidence = LiteratureEvidence.Factory.newInstance();

        // populate common field to all evidences
        populatePhenotypeAssociation( literatureEvidence, evidenceValueObject );

        // populate specific fields for this evidence
        String pubmedId = evidenceValueObject.getPubmedID();
        literatureEvidence.setCitation( findOrCreateBibliographicReference( pubmedId ) );
        return literatureEvidence;

    }

    /**
     * @param evidenceValueObject the evidence we want to convert
     * @return PhenotypeAssociation the entity created from the ValueObject
     */
    private PhenotypeAssociation conversion2ExperimentalEvidence( ExperimentalEvidenceValueObject evidenceValueObject ) {

        // create the entity to populate
        ExperimentalEvidence experimentalEvidence = ExperimentalEvidence.Factory.newInstance();

        // populate common field to all evidences
        populatePhenotypeAssociation( experimentalEvidence, evidenceValueObject );

        // populate specific fields for this evidence

        // create the GenericExperiment
        GenericExperiment genericExperiment = GenericExperiment.Factory.newInstance();

        // find all pubmed id from the value object
        String primaryPubmedId = evidenceValueObject.getPrimaryPublication();
        Collection<String> relevantPubmedId = evidenceValueObject.getRelevantPublication();
        // creates or find those Bibliographic Reference and add them to the GenericExperiment
        genericExperiment.setPrimaryPublication( findOrCreateBibliographicReference( primaryPubmedId ) );
        genericExperiment.setOtherRelevantPublications( findOrCreateBibliographicReference( relevantPubmedId ) );

        // characteristic
        Collection<Characteristic> characteristics = null;

        if ( evidenceValueObject.getTags() != null ) {

            characteristics = new HashSet<Characteristic>();

            for ( CharacteristicValueObject chaValueObject : evidenceValueObject.getTags() ) {

                Characteristic tag = Characteristic.Factory.newInstance();

                tag.setCategory( chaValueObject.getCategory() );
                tag.setValue( chaValueObject.getValue() );

                characteristics.add( tag );
            }
        }

        genericExperiment.setCharacteristics( characteristics );

        phenotypeAssociationService.createGenericExperiment( genericExperiment );

        experimentalEvidence.setExperiment( genericExperiment );
        return experimentalEvidence;

    }

    /**
     * Sets the fields that are the same for any evidences.
     * 
     * @param phe The phenotype association (parent class of an evidence) we are interested in populating
     * @param evidenceValueObject the value object representing a phenotype
     */
    private void populatePhenotypeAssociation( PhenotypeAssociation phe, EvidenceValueObject evidenceValueObject ) {

        // TODO
        phe.setDescription( evidenceValueObject.getDescription() );
        phe.setEvidenceCode( evidenceValueObject.getEvidenceCode() );
        phe.setIsNegativeEvidence( evidenceValueObject.getIsNegativeEvidence() );
        phe.setName( evidenceValueObject.getName() );

        // TODO how to set up correct AssociationType
        VocabCharacteristic associationType = VocabCharacteristic.Factory.newInstance();
        associationType.setValue( evidenceValueObject.getCharacteristic() );
        // we need a category ???? how does it work
        phe.setAssociationType( associationType );

        // here lets add the phenotypes
        Collection<Characteristic> myPhenotypes = new HashSet<Characteristic>();

        for ( String phenotype : evidenceValueObject.getPhenotypes() ) {

            // TODO how to set up correct phenotype
            VocabCharacteristic myPhenotype = VocabCharacteristic.Factory.newInstance();

            // example http://purl.org/obo/owl/DOID#DOID_1969
            myPhenotype.setValueUri( phenotype );
            myPhenotype.setValue( phenotype.substring( phenotype.indexOf( "#" ) + 1 ) );

            myPhenotypes.add( myPhenotype );
        }

        phe.setPhenotypes( myPhenotypes );
    }

    /** calls findOrCreateBibliographicReference for a Collection */
    private Collection<BibliographicReference> findOrCreateBibliographicReference( Collection<String> pubMedIds ) {

        Collection<BibliographicReference> bibliographicReferences = null;

        if ( pubMedIds != null && pubMedIds.size() != 0 ) {

            bibliographicReferences = new HashSet<BibliographicReference>();

            for ( String pubmedId : pubMedIds ) {

                bibliographicReferences.add( findOrCreateBibliographicReference( pubmedId ) );
            }
        }
        return bibliographicReferences;
    }

    /**
     * Creates a BibliographicReference if it doesn't exist in the database
     * 
     * @param pubMedId the pubmedID of the reference
     * @param BibliographicReference the BibliogrphicReference
     */
    private BibliographicReference findOrCreateBibliographicReference( String pubMedId ) {

        // check if already in the database
        BibliographicReference bibRef = bibliographicReferenceService.findByExternalId( pubMedId );

        if ( bibRef == null ) {

            // creates a new BibliographicReference
            bibRef = this.pubMedXmlFetcher.retrieveByHTTP( Integer.parseInt( pubMedId ) );
            // TODO make sure those pubmed id exists

            bibliographicReferenceService.create( bibRef );
        }

        return bibRef;
    }

}
