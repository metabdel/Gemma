/*
 * The Gemma project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package edu.columbia.gemma.loader.loaderutils;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.columbia.gemma.common.auditAndSecurity.Contact;
import edu.columbia.gemma.common.auditAndSecurity.ContactDao;
import edu.columbia.gemma.common.auditAndSecurity.Person;
import edu.columbia.gemma.common.auditAndSecurity.PersonDao;
import edu.columbia.gemma.common.description.Characteristic;
import edu.columbia.gemma.common.description.DatabaseEntry;
import edu.columbia.gemma.common.description.DatabaseEntryDao;
import edu.columbia.gemma.common.description.ExternalDatabase;
import edu.columbia.gemma.common.description.ExternalDatabaseDao;
import edu.columbia.gemma.common.description.LocalFile;
import edu.columbia.gemma.common.description.LocalFileDao;
import edu.columbia.gemma.common.description.OntologyEntry;
import edu.columbia.gemma.common.description.OntologyEntryDao;
import edu.columbia.gemma.common.protocol.Hardware;
import edu.columbia.gemma.common.protocol.HardwareApplication;
import edu.columbia.gemma.common.protocol.HardwareDao;
import edu.columbia.gemma.common.protocol.Protocol;
import edu.columbia.gemma.common.protocol.ProtocolApplication;
import edu.columbia.gemma.common.protocol.ProtocolDao;
import edu.columbia.gemma.common.protocol.Software;
import edu.columbia.gemma.common.protocol.SoftwareApplication;
import edu.columbia.gemma.common.protocol.SoftwareDao;
import edu.columbia.gemma.common.quantitationtype.QuantitationType;
import edu.columbia.gemma.common.quantitationtype.QuantitationTypeDao;
import edu.columbia.gemma.expression.arrayDesign.ArrayDesign;
import edu.columbia.gemma.expression.arrayDesign.ArrayDesignDao;
import edu.columbia.gemma.expression.bioAssay.BioAssay;
import edu.columbia.gemma.expression.bioAssay.BioAssayDao;
import edu.columbia.gemma.expression.bioAssayData.DesignElementDataVector;
import edu.columbia.gemma.expression.biomaterial.BioMaterial;
import edu.columbia.gemma.expression.biomaterial.BioMaterialDao;
import edu.columbia.gemma.expression.biomaterial.Compound;
import edu.columbia.gemma.expression.biomaterial.CompoundDao;
import edu.columbia.gemma.expression.biomaterial.Treatment;
import edu.columbia.gemma.expression.designElement.CompositeSequence;
import edu.columbia.gemma.expression.designElement.DesignElement;
import edu.columbia.gemma.expression.designElement.DesignElementDao;
import edu.columbia.gemma.expression.designElement.Reporter;
import edu.columbia.gemma.expression.experiment.ExperimentalDesign;
import edu.columbia.gemma.expression.experiment.ExperimentalFactor;
import edu.columbia.gemma.expression.experiment.ExpressionExperiment;
import edu.columbia.gemma.expression.experiment.ExpressionExperimentDao;
import edu.columbia.gemma.expression.experiment.ExpressionExperimentSubSet;
import edu.columbia.gemma.expression.experiment.FactorValue;
import edu.columbia.gemma.genome.Gene;
import edu.columbia.gemma.genome.GeneDao;
import edu.columbia.gemma.genome.Taxon;
import edu.columbia.gemma.genome.TaxonDao;
import edu.columbia.gemma.genome.biosequence.BioSequence;

/**
 * A generic class to persist Gemma-domain objects. (work in progress)
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 * @spring.bean id="persisterHelper"
 * @spring.property name="ontologyEntryDao" ref="ontologyEntryDao"
 * @spring.property name="personDao" ref="personDao"
 * @spring.property name="expressionExperimentDao" ref="expressionExperimentDao"
 * @spring.property name="bioMaterialDao" ref="bioMaterialDao"
 * @spring.property name="arrayDesignDao" ref="arrayDesignDao"
 * @spring.property name="designElementDao" ref="designElementDao"
 * @spring.property name="protocolDao" ref="protocolDao"
 * @spring.property name="softwareDao" ref="softwareDao"
 * @spring.property name="hardwareDao" ref="hardwareDao"
 * @spring.property name="geneDao" ref="geneDao"
 * @spring.property name="taxonDao" ref="taxonDao"
 * @spring.property name="localFileDao" ref="localFileDao"
 * @spring.property name="bioAssayDao" ref="bioAssayDao"
 * @spring.property name="externalDatabaseDao" ref="externalDatabaseDao"
 * @spring.property name="quantitationTypeDao" ref="quantitationTypeDao"
 * @spring.property name="compoundDao" ref="compoundDao"
 * @spring.property name="databaseEntryDao" ref="databaseEntryDao"
 * @spring.property name="contactDao" ref="contactDao"
 */
public class PersisterHelper implements Persister {
    private static Log log = LogFactory.getLog( PersisterHelper.class.getName() );

    private ArrayDesignDao arrayDesignDao;

    private BioAssayDao bioAssayDao;

    private BioMaterialDao bioMaterialDao;

    private CompoundDao compoundDao;

    private ContactDao contactDao;

    private DatabaseEntryDao databaseEntryDao;

    private Person defaultOwner = null;

    private DesignElementDao designElementDao;

    private ExpressionExperimentDao expressionExperimentDao;

    private ExternalDatabaseDao externalDatabaseDao;

    private GeneDao geneDao;

    private HardwareDao hardwareDao;

    private LocalFileDao localFileDao;

    private OntologyEntryDao ontologyEntryDao;

    private PersonDao personDao;

    private ProtocolDao protocolDao;

    private QuantitationTypeDao quantitationTypeDao;

    private SoftwareDao softwareDao;

    private TaxonDao taxonDao;

    /*
     * @see edu.columbia.gemma.loader.loaderutils.Loader#create(java.util.Collection)
     */
    public Collection<Object> persist( Collection<Object> col ) {
        if ( defaultOwner == null ) initializeDefaultOwner();
        try {
            log.debug( "Entering + " + this.getClass().getName() + ".create() with " + col.size() + " objects." );
            for ( Object entity : col ) {
                persist( entity );
            }
        } catch ( Exception e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
        return col;
    }

    /*
     * (non-Javadoc) TODO: finish implementing this.
     * 
     * @see edu.columbia.gemma.loader.loaderutils.Loader#create(edu.columbia.gemma.genome.Gene)
     */
    public Object persist( Object entity ) {
        if ( entity == null ) return null;
        log.debug( "Persisting " + entity.getClass().getName() + " " + entity );
        if ( entity instanceof ExpressionExperiment ) {
            return persistExpressionExperiment( ( ExpressionExperiment ) entity );
        } else if ( entity instanceof ArrayDesign ) {
            return persistArrayDesign( ( ArrayDesign ) entity );
        } else if ( entity instanceof BioSequence ) {
            return null;
            // deal with in cascade from array design? Do nothing, probably.
        } else if ( entity instanceof Protocol ) {
            return null;
        } else if ( entity instanceof CompositeSequence ) {
            return null;
            // cascade from array design, do nothing
        } else if ( entity instanceof Reporter ) {
            return null;
            // cascade from array design, do nothing
        } else if ( entity instanceof Hardware ) {
            return null;
        } else if ( entity instanceof QuantitationType ) {
            return persistQuantitationType( ( QuantitationType ) entity );
        } else if ( entity instanceof BioMaterial ) {
            return persistBioMaterial( ( BioMaterial ) entity );
        } else if ( entity instanceof ExternalDatabase ) {
            return persistExternalDatabase( ( ExternalDatabase ) entity );
        } else if ( entity instanceof LocalFile ) {
            return persistLocalFile( ( LocalFile ) entity );
        } else if ( entity instanceof BioAssay ) {
            return persistBioAssay( ( BioAssay ) entity );
        } else if ( entity instanceof OntologyEntry ) {
            return persistOntologyEntry( ( OntologyEntry ) entity );
        } else if ( entity instanceof Gene ) {
            return persistGene( ( Gene ) entity );
        } else if ( entity instanceof Compound ) {
            return persistCompound( ( Compound ) entity );
        } else {
            log.error( "Can't deal with " + entity.getClass().getName() );
            return null;
        }
    }

    /**
     * @param assay
     */
    @SuppressWarnings("unchecked")
    public BioAssay persistBioAssay( BioAssay assay ) {

        if ( assay == null ) return null;

        for ( FactorValue factorValue : ( Collection<FactorValue> ) assay.getFactorValues() ) {
            for ( OntologyEntry value : ( Collection<OntologyEntry> ) factorValue.getValue() ) {
                value = persistOntologyEntry( value );
            }
        }

        for ( ArrayDesign arrayDesign : ( Collection<ArrayDesign> ) assay.getArrayDesignsUsed() ) {
            persistArrayDesign( arrayDesign );
        }

        for ( LocalFile file : ( Collection<LocalFile> ) assay.getDerivedDataFiles() ) {
            file = persistLocalFile( file );
        }

        for ( BioMaterial bioMaterial : ( Collection<BioMaterial> ) assay.getSamplesUsed() ) {
            bioMaterial = persistBioMaterial( bioMaterial );
        }

        LocalFile f = assay.getRawDataFile();
        if ( f != null ) {
            LocalFile persistentLocalFile = persistLocalFile( f );
            if ( persistentLocalFile != null ) {
                f = persistentLocalFile;
            } else {
                log.error( "Null local file for " + f.getLocalURI() );
                throw new RuntimeException( "Null local file for" + f.getLocalURI() );
            }
        }

        return bioAssayDao.findOrCreate( assay );
    }

    /**
     * @param arrayDesignDao The arrayDesignDao to set.
     */
    public void setArrayDesignDao( ArrayDesignDao arrayDesignDao ) {
        this.arrayDesignDao = arrayDesignDao;
    }

    /**
     * @param bioAssayDao The bioAssayDao to set.
     */
    public void setBioAssayDao( BioAssayDao bioAssayDao ) {
        this.bioAssayDao = bioAssayDao;
    }

    /**
     * @param bioMaterialDao The bioMaterialDao to set.
     */
    public void setBioMaterialDao( BioMaterialDao bioMaterialDao ) {
        this.bioMaterialDao = bioMaterialDao;
    }

    /**
     * @param compoundDao The compoundDao to set.
     */
    public void setCompoundDao( CompoundDao compoundDao ) {
        this.compoundDao = compoundDao;
    }

    /**
     * @param contactDao The contactDao to set.
     */
    public void setContactDao( ContactDao contactDao ) {
        this.contactDao = contactDao;
    }

    /**
     * @param databaseEntryDao The databaseEntryDao to set.
     */
    public void setDatabaseEntryDao( DatabaseEntryDao databaseEntryDao ) {
        this.databaseEntryDao = databaseEntryDao;
    }

    /**
     * @param designElementDao The designElementDao to set.
     */
    public void setDesignElementDao( DesignElementDao designElementDao ) {
        this.designElementDao = designElementDao;
    }

    /**
     * @param expressionExperimentDao The expressionExperimentDao to set.
     */
    public void setExpressionExperimentDao( ExpressionExperimentDao expressionExperimentDao ) {
        this.expressionExperimentDao = expressionExperimentDao;
    }

    /**
     * @param externalDatabaseDao The externalDatabaseDao to set.
     */
    public void setExternalDatabaseDao( ExternalDatabaseDao externalDatabaseDao ) {
        this.externalDatabaseDao = externalDatabaseDao;
    }

    /**
     * @param geneDao The geneDao to set.
     */
    public void setGeneDao( GeneDao geneDao ) {
        this.geneDao = geneDao;
    }

    /**
     * @param hardwareDao The hardwareDao to set.
     */
    public void setHardwareDao( HardwareDao hardwareDao ) {
        this.hardwareDao = hardwareDao;
    }

    /**
     * @param localFileDao The localFileDao to set.
     */
    public void setLocalFileDao( LocalFileDao localFileDao ) {
        this.localFileDao = localFileDao;
    }

    /**
     * @param ontologyEntryDao
     */
    public void setOntologyEntryDao( OntologyEntryDao ontologyEntryDao ) {
        this.ontologyEntryDao = ontologyEntryDao;
    }

    /**
     * @param personDao
     */
    public void setPersonDao( PersonDao personDao ) {
        this.personDao = personDao;
    }

    /**
     * @param protocolDao The protocolDao to set
     */
    public void setProtocolDao( ProtocolDao protocolDao ) {
        this.protocolDao = protocolDao;
    }

    /**
     * @param quantitationTypeDao The quantitationTypeDao to set.
     */
    public void setQuantitationTypeDao( QuantitationTypeDao quantitationTypeDao ) {
        this.quantitationTypeDao = quantitationTypeDao;
    }

    /**
     * @param softwareDao The softwareDao to set.
     */
    public void setSoftwareDao( SoftwareDao softwareDao ) {
        this.softwareDao = softwareDao;
    }

    /**
     * @param taxonDao The taxonDao to set.
     */
    public void setTaxonDao( TaxonDao taxonDao ) {
        this.taxonDao = taxonDao;
    }

    /**
     * @param bioSequence
     */
    private void fillInBioSequence( BioSequence bioSequence ) {
        if ( bioSequence == null ) return;
        Taxon t = bioSequence.getTaxon();
        if ( t == null ) throw new IllegalArgumentException( "BioSequence Taxon cannot be null" );
        bioSequence.setTaxon( taxonDao.findOrCreate( t ) );
    }

    /**
     * Fill in the categoryTerm and valueTerm associations of a
     * 
     * @param Characteristics Collection of Characteristics
     */
    private void fillInOntologyEntries( Collection<Characteristic> Characteristics ) {
        for ( Characteristic Characteristic : Characteristics ) {
            persistOntologyEntry( Characteristic.getCategoryTerm() );
            persistOntologyEntry( Characteristic.getValueTerm() );
        }
    }

    /**
     * @param databaseEntry
     */
    private DatabaseEntry fillInPersistentExternalDatabase( DatabaseEntry databaseEntry ) {
        assert externalDatabaseDao != null;
        ExternalDatabase externalDatabase = databaseEntry.getExternalDatabase();
        if ( externalDatabase == null ) {
            log.debug( "No externalDatabase" );
            return null;
        }
        databaseEntry.setExternalDatabase( externalDatabaseDao.findOrCreate( externalDatabase ) );
        return databaseEntry;
    }

    /**
     * @param ontologyEntry
     */
    @SuppressWarnings("unchecked")
    private void fillInPersistentExternalDatabase( OntologyEntry ontologyEntry ) {
        this.fillInPersistentExternalDatabase( ( DatabaseEntry ) ontologyEntry );
        for ( OntologyEntry associatedOntologyEntry : ( Collection<OntologyEntry> ) ontologyEntry.getAssociations() ) {
            fillInPersistentExternalDatabase( associatedOntologyEntry );
        }
    }

    /**
     * @param protocol
     */
    @SuppressWarnings("unchecked")
    private void fillInProtocol( Protocol protocol ) {
        if ( protocol == null ) {
            log.warn( "Null protocol" );
            return;
        }
        OntologyEntry type = protocol.getType();
        persistOntologyEntry( type );
        protocol.setType( type );

        for ( Software software : ( Collection<Software> ) protocol.getSoftwareUsed() ) {
            software = softwareDao.findOrCreate( software );
        }

        for ( Hardware hardware : ( Collection<Hardware> ) protocol.getHardwares() ) {
            hardware = hardwareDao.findOrCreate( hardware );
        }
    }

    /**
     * @param protocolApplication
     */
    @SuppressWarnings("unchecked")
    private void fillInProtocolApplication( ProtocolApplication protocolApplication ) {
        if ( protocolApplication == null ) return;

        log.debug( "Filling in protocolApplication" );

        Protocol protocol = protocolApplication.getProtocol();
        if ( protocol == null )
            throw new IllegalStateException( "Must have protocol associated with ProtocolApplication" );

        if ( protocol.getName() == null ) throw new IllegalStateException( "Protocol must have a name" );

        fillInProtocol( protocol );
        protocolApplication.setProtocol( protocolDao.findOrCreate( protocol ) );

        for ( Person performer : ( Collection<Person> ) protocolApplication.getPerformers() ) {
            log.debug( "Filling in performer" );
            performer.setId( personDao.findOrCreate( performer ).getId() );
        }

        for ( SoftwareApplication softwareApplication : ( Collection<SoftwareApplication> ) protocolApplication
                .getSoftwareApplications() ) {
            Software software = softwareApplication.getSoftware();
            if ( software == null )
                throw new IllegalStateException( "Must have software associated with SoftwareApplication" );

            OntologyEntry type = software.getType();
            persistOntologyEntry( type );
            software.setType( type );

            softwareApplication.setSoftware( softwareDao.findOrCreate( software ) );

        }

        for ( HardwareApplication HardwareApplication : ( Collection<HardwareApplication> ) protocolApplication
                .getHardwareApplications() ) {
            Hardware hardware = HardwareApplication.getHardware();
            if ( hardware == null )
                throw new IllegalStateException( "Must have hardware associated with HardwareApplication" );

            OntologyEntry type = hardware.getType();
            persistOntologyEntry( type );
            hardware.setType( type );

            HardwareApplication.setHardware( hardwareDao.findOrCreate( hardware ) );
        }
    }

    /**
     * Fetch the fallback owner to use for newly-imported data.
     */
    @SuppressWarnings("unchecked")
    private void initializeDefaultOwner() {
        Collection<Person> matchingPersons = personDao.findByFullName( "nobody", "nobody", "nobody" );

        assert matchingPersons.size() == 1;

        defaultOwner = matchingPersons.iterator().next();

        if ( defaultOwner == null ) throw new NullPointerException( "Default Person 'nobody' not found in database." );
    }

    /**
     * @param entity
     */
    @SuppressWarnings("unchecked")
    private ArrayDesign persistArrayDesign( ArrayDesign entity ) {

        entity.setDesignProvider( persistContact( entity.getDesignProvider() ) );
        ArrayDesign existing = arrayDesignDao.find( entity );

        if ( existing != null ) {
            entity = existing;
        }

        Collection<DesignElement> existingDesignElements = entity.getDesignElements();
        if ( existingDesignElements.size() == entity.getDesignElements().size() ) {
            log.warn( "Number of design elements in existing version "
                    + "is the same. No further processing will be done." );
            return entity;
        } else if ( entity.getDesignElements().size() == 0 ) {
            log.warn( entity + ": No design elements in newly supplied version, no further processing will be done." );
            return entity;
        }

        log.debug( "Filling in design elements for " + entity );
        int i = 0;
        for ( DesignElement designElement : ( Collection<DesignElement> ) entity.getDesignElements() ) {
            designElement.setArrayDesign( entity );
            if ( designElement instanceof CompositeSequence ) {
                CompositeSequence cs = ( CompositeSequence ) designElement;
                fillInBioSequence( cs.getBiologicalCharacteristic() );
            } else if ( designElement instanceof Reporter ) {
                Reporter reporter = ( Reporter ) designElement;
                fillInBioSequence( reporter.getImmobilizedCharacteristic() );
            }
            i++;
            if ( i % 1000 == 0 ) log.info( i + " design elements examined" );
        }

        return arrayDesignDao.findOrCreate( entity );
    }

    /**
     * @param entity
     */
    @SuppressWarnings("unchecked")
    private BioMaterial persistBioMaterial( BioMaterial entity ) {

        entity.setExternalAccession( persistDatabaseEntry( entity.getExternalAccession() ) );

        OntologyEntry materialType = entity.getMaterialType();
        if ( materialType != null ) {
            entity.setMaterialType( ontologyEntryDao.findOrCreate( materialType ) );
        }

        for ( Treatment treatment : ( Collection<Treatment> ) entity.getTreatments() ) {
            OntologyEntry action = treatment.getAction();
            persistOntologyEntry( action );

            for ( ProtocolApplication protocolApplication : ( Collection<ProtocolApplication> ) treatment
                    .getProtocolApplications() ) {
                fillInProtocolApplication( protocolApplication );
            }
        }

        fillInOntologyEntries( entity.getCharacteristics() );

        return bioMaterialDao.findOrCreate( entity );
    }

    /**
     * @param compound
     * @return
     */
    private Compound persistCompound( Compound compound ) {
        if ( compound == null ) return null;
        persistOntologyEntry( compound.getCompoundIndices() );
        if ( compound.getIsSolvent() == null )
            throw new IllegalArgumentException( "Compound must have 'isSolvent' value set." );
        return compoundDao.findOrCreate( compound );
    }

    /**
     * @param designProvider
     */
    private Contact persistContact( Contact designProvider ) {
        return this.contactDao.findOrCreate( designProvider );
    }

    /**
     * @param databaseEntry
     * @return
     */
    private DatabaseEntry persistDatabaseEntry( DatabaseEntry databaseEntry ) {
        databaseEntry.setExternalDatabase( persistExternalDatabase( databaseEntry.getExternalDatabase() ) );
        return databaseEntryDao.findOrCreate( databaseEntry );
    }

    /**
     * @param entity
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private ExpressionExperiment persistExpressionExperiment( ExpressionExperiment entity ) {

        if ( entity == null ) return null;

        if ( entity.getOwner() == null ) {
            entity.setOwner( defaultOwner );
        }

        if ( entity.getAccession() != null && entity.getAccession().getExternalDatabase() != null ) {
            entity.setAccession( persistDatabaseEntry( entity.getAccession() ) );
        } else {
            log.warn( "Null accession for expressionExperiment" );
        }

        // this is very annoying code.
        // the following ontology entries must be persisted manually.
        // manually persist: experimentaldesign->experimentalFactor->annotation, category
        // manually persist: experimentaldesign->experimentalFactor->FactorValue->value
        // experimentaldesign->type
        for ( ExperimentalDesign experimentalDesign : ( Collection<ExperimentalDesign> ) entity
                .getExperimentalDesigns() ) {

            // type
            for ( OntologyEntry type : ( Collection<OntologyEntry> ) experimentalDesign.getTypes() ) {
                persistOntologyEntry( type );
            }

            for ( ExperimentalFactor experimentalFactor : ( Collection<ExperimentalFactor> ) experimentalDesign
                    .getExperimentalFactors() ) {
                for ( OntologyEntry annotation : ( Collection<OntologyEntry> ) experimentalFactor.getAnnotations() ) {
                    persistOntologyEntry( annotation );
                }

                OntologyEntry category = experimentalFactor.getCategory();
                if ( category == null ) {
                    log.debug( "No 'category' for ExperimentalDesign" );
                } else {
                    persistOntologyEntry( category );
                    log.debug( "ExperimentalDesign.category=" + category.getId() );
                }

                for ( FactorValue factorValue : ( Collection<FactorValue> ) experimentalFactor.getFactorValues() ) {

                    OntologyEntry value = factorValue.getValue();

                    if ( value == null ) {
                        log.debug( "No 'value' for FactorValue" ); // that's okay, it can be a measurement.
                        if ( factorValue.getMeasurement() == null ) {
                            throw new IllegalStateException( "FactorValue must have either a measurement or a value" );
                        }
                    } else {
                        if ( factorValue.getMeasurement() != null ) {
                            throw new IllegalStateException( "FactorValue cannot have both a measurement and a value" );
                        }
                        persistOntologyEntry( value );
                        // factorValue.setValue( value );
                        log.debug( "factorValue.value=" + value.getId() );
                    }
                }
            }
        }

        // manually persist: experimentaldesign->bioassay->factorvalue->value and bioassay->arraydesign
        for ( BioAssay bA : ( Collection<BioAssay> ) entity.getBioAssays() ) {
            if ( bA == null ) continue;
            bA.setId( persistBioAssay( bA ).getId() );
        }

        for ( ExpressionExperimentSubSet subset : ( Collection<ExpressionExperimentSubSet> ) entity.getSubsets() ) {
            for ( BioAssay bA : ( Collection<BioAssay> ) subset.getBioAssays() ) {
                if ( bA == null ) continue;
                bA.setId( persistBioAssay( bA ).getId() );
            }
        }

        // manually persist expressionExperiment-->designElementDataVector-->DesignElement
        for ( DesignElementDataVector vect : ( Collection<DesignElementDataVector> ) entity
                .getDesignElementDataVectors() ) {
            DesignElement persistentDesignElement = designElementDao.find( vect.getDesignElement() );
            if ( persistentDesignElement == null ) {
                log.error( vect.getDesignElement() + " does not have a persistent version" );
                continue;
            }

            ArrayDesign ad = persistentDesignElement.getArrayDesign();
            ad.setId( this.persistArrayDesign( ad ).getId() );

            vect.setDesignElement( persistentDesignElement );
        }

        return expressionExperimentDao.findOrCreate( entity );
    }

    /**
     * @param database
     */
    private ExternalDatabase persistExternalDatabase( ExternalDatabase database ) {
        if ( database == null ) return null;
        return externalDatabaseDao.findOrCreate( database );
    }

    /**
     * @param gene
     */
    private Object persistGene( Gene gene ) {
        return geneDao.findOrCreate( gene );
    }

    /**
     * @param file
     */
    private LocalFile persistLocalFile( LocalFile file ) {
        return localFileDao.findOrCreate( file );
    }

    /**
     * Ontology entr
     * 
     * @param ontologyEntry
     */
    @SuppressWarnings("unchecked")
    private OntologyEntry persistOntologyEntry( OntologyEntry ontologyEntry ) {
        if ( ontologyEntry == null ) return null;
        fillInPersistentExternalDatabase( ontologyEntry );
        ontologyEntry.setId( ontologyEntryDao.findOrCreate( ontologyEntry ).getId() );
        for ( OntologyEntry associatedOntologyEntry : ( Collection<OntologyEntry> ) ontologyEntry.getAssociations() ) {
            persistOntologyEntry( associatedOntologyEntry );
        }
        return ontologyEntry;
    }

    /**
     * @param entity
     */
    private QuantitationType persistQuantitationType( QuantitationType entity ) {
        return quantitationTypeDao.findOrCreate( entity );
    }

}
