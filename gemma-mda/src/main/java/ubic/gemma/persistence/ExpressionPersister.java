/*
 * The Gemma project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.gemma.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import ubic.gemma.model.common.description.LocalFile;
import ubic.gemma.model.common.description.OntologyEntry;
import ubic.gemma.model.common.protocol.ProtocolApplication;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssay.BioAssayService;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimensionService;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorService;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.biomaterial.BioMaterialService;
import ubic.gemma.model.expression.biomaterial.Compound;
import ubic.gemma.model.expression.biomaterial.CompoundService;
import ubic.gemma.model.expression.biomaterial.Treatment;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.expression.experiment.ExperimentalDesign;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.expression.experiment.ExpressionExperimentSubSet;
import ubic.gemma.model.expression.experiment.FactorValue;
import ubic.gemma.model.expression.experiment.FactorValueService;

/**
 * @spring.property name="factorValueService" ref="factorValueService"
 * @spring.property name="designElementDataVectorService" ref="designElementDataVectorService"
 * @spring.property name="bioAssayDimensionService" ref="bioAssayDimensionService"
 * @spring.property name="expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name="bioMaterialService" ref="bioMaterialService"
 * @spring.property name="bioAssayService" ref="bioAssayService"
 * @spring.property name="compoundService" ref="compoundService"
 * @author pavlidis
 * @version $Id$
 */
abstract public class ExpressionPersister extends ArrayDesignPersister {

    private DesignElementDataVectorService designElementDataVectorService;

    private ExpressionExperimentService expressionExperimentService;

    private BioAssayDimensionService bioAssayDimensionService;

    private BioAssayService bioAssayService;

    private BioMaterialService bioMaterialService;

    private FactorValueService factorValueService;

    private CompoundService compoundService;

    Map<String, BioAssayDimension> bioAssayDimensionCache = new HashMap<String, BioAssayDimension>();

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.util.persister.Persister#persist(java.lang.Object)
     */
    public Object persist( Object entity ) {
        if ( entity == null ) return null;

        if ( log.isDebugEnabled() ) {
            log.debug( "Persisting: " + entity.getClass().getSimpleName() + " (" + entity + ")" );
        }

        if ( entity instanceof ExpressionExperiment ) {
            return persistExpressionExperiment( ( ExpressionExperiment ) entity );
        } else if ( entity instanceof BioAssayDimension ) {
            return persistBioAssayDimension( ( BioAssayDimension ) entity );
        } else if ( entity instanceof BioMaterial ) {
            return persistBioMaterial( ( BioMaterial ) entity );
        } else if ( entity instanceof BioAssay ) {
            return persistBioAssay( ( BioAssay ) entity );
        } else if ( entity instanceof Compound ) {
            return persistCompound( ( Compound ) entity );
        } else if ( entity instanceof DesignElementDataVector ) {
            return persistDesignElementDataVector( ( DesignElementDataVector ) entity );
        }
        return super.persist( entity );

    }

    /**
     * @param vect
     */
    private BioAssayDimension fillInDesignElementDataVectorAssociations( DesignElementDataVector vect ) {
        DesignElement designElement = vect.getDesignElement();

        assert designElement != null;

        ArrayDesign ad = designElement.getArrayDesign();
        assert ad != null;

        ad = cacheArrayDesign( ad ); // possibly avoid doing this check each time

        String key = designElement.getName() + DESIGN_ELEMENT_KEY_SEPARATOR + ad.getName();

        if ( designElementCache.containsKey( key ) ) {
            designElement = designElementCache.get( key );
        } else {
            // means the array design is lacking it.
            // if ( log.isTraceEnabled() ) log.trace( ad + " does not contain " + designElement + ", adding it" );
            designElement = addNewDesignElementToPersistentArrayDesign( ad, designElement );
        }

        assert designElement != null && designElement.getId() != null;
        vect.setDesignElement( designElement ); // shouldn't have to do this. Some kind of hibernate weirdness.s

        BioAssayDimension baDim = checkBioAssayDimensionCache( vect );

        assert vect.getQuantitationType() != null;
        vect.setQuantitationType( persistQuantitationType( vect.getQuantitationType() ) );

        return baDim;
    }

    /**
     * @param entity
     */
    private Collection<BioAssay> fillInExpressionExperimentDataVectorAssociations( ExpressionExperiment entity ) {

        log.info( "Filling in DesignElementDataVectors" );

        Session sess = this.getCurrentSession();

        Collection<BioAssay> bioAssays = new HashSet<BioAssay>();

        int count = 0;
        for ( DesignElementDataVector vect : entity.getDesignElementDataVectors() ) {

            BioAssayDimension baDim = fillInDesignElementDataVectorAssociations( vect );
            bioAssays.addAll( baDim.getBioAssays() );

            if ( ++count % 2000 == 0 ) {
                log.info( "Filled in " + count + " DesignElementDataVectors" );
            }

            if ( ++count % 100 == 0 ) {
                sess.flush();
                sess.clear();
            }
        }
        log.info( "Done, filled in " + count + " DesignElementDataVectors, " + bioAssays.size() + " bioassays" );
        return bioAssays;
    }

    /**
     * @param assay
     */
    private BioAssay persistBioAssay( BioAssay assay ) {

        if ( assay == null ) return null;

        fillInBioAssayAssociations( assay );

        if ( !isTransient( assay ) ) return assay;
        log.info( "Persisting " + assay );

        return bioAssayService.findOrCreate( assay );
    }

    /**
     * @param assay
     */
    private void fillInBioAssayAssociations( BioAssay assay ) {

        ArrayDesign arrayDesign = assay.getArrayDesignUsed();
        assert arrayDesign != null;

        arrayDesign = cacheArrayDesign( arrayDesign );
        assert arrayDesign.getId() != null;
        assay.setArrayDesignUsed( arrayDesign );
        assert assay.getArrayDesignUsed().getId() != null;

        for ( BioMaterial material : assay.getSamplesUsed() ) {
            for ( FactorValue factorValue : material.getFactorValues() ) {
                // factors are not compositioned in any more, but by association with the ExperimentalFactor.
                fillInFactorValueAssociations( factorValue );
                factorValue = persistFactorValue( factorValue );
            }
        }
        // DatabaseEntries are persisted by composition, so we just need to fill in the ExternalDatabase.
        if ( assay.getAccession() != null ) {
            assay.getAccession().setExternalDatabase(
                    persistExternalDatabase( assay.getAccession().getExternalDatabase() ) );
        }

        if ( log.isInfoEnabled() ) log.info( assay.getSamplesUsed().size() + " bioMaterials for " + assay );

        persistCollectionElements( assay.getSamplesUsed() );

        if ( assay.getRawDataFile() != null ) {
            assay.setRawDataFile( persistLocalFile( assay.getRawDataFile() ) );
        }

        for ( LocalFile file : assay.getDerivedDataFiles() ) {
            file = persistLocalFile( file );
        }

    }

    /**
     * @param bioAssayDimension
     * @return
     */
    private BioAssayDimension persistBioAssayDimension( BioAssayDimension bioAssayDimension ) {
        if ( bioAssayDimension == null ) return null;
        if ( !isTransient( bioAssayDimension ) ) return bioAssayDimension;

        List<BioAssay> persistedBioAssays = new ArrayList<BioAssay>();
        for ( BioAssay bioAssay : bioAssayDimension.getBioAssays() ) {
            persistedBioAssays.add( persistBioAssay( bioAssay ) );
        }
        bioAssayDimension.setBioAssays( persistedBioAssays );
        return bioAssayDimensionService.findOrCreate( bioAssayDimension );
    }

    /**
     * @param entity
     */
    private BioMaterial persistBioMaterial( BioMaterial entity ) {
        if ( entity == null ) return null;
        log.debug( "Persisting " + entity );
        if ( !isTransient( entity ) ) return entity;

        assert entity.getSourceTaxon() != null;

        entity.setExternalAccession( persistDatabaseEntry( entity.getExternalAccession() ) );
        entity.setMaterialType( persistOntologyEntry( entity.getMaterialType() ) );
        entity.setSourceTaxon( persistTaxon( entity.getSourceTaxon() ) );

        for ( Treatment treatment : entity.getTreatments() ) {

            OntologyEntry action = treatment.getAction();
            treatment.setAction( persistOntologyEntry( action ) );
            log.debug( treatment + " action: " + action );

            for ( ProtocolApplication protocolApplication : treatment.getProtocolApplications() ) {
                fillInProtocolApplication( protocolApplication );
            }
        }

        fillInOntologyEntries( entity.getCharacteristics() ); // characteristics themselves should cascade

        return bioMaterialService.findOrCreate( entity );
    }

    // /**
    // * @param bioMaterialDimension
    // * @return
    // */
    // private BioMaterialDimension persistBioMaterialDimension( BioMaterialDimension bioMaterialDimension ) {
    // assert bioMaterialDimensionService != null;
    // List<BioMaterial> persistentBioMaterials = new ArrayList<BioMaterial>();
    // for ( BioMaterial bioMaterial : bioMaterialDimension.getBioMaterials() ) {
    // persistentBioMaterials.add( persistBioMaterial( bioMaterial ) );
    // }
    // bioMaterialDimension.setBioMaterials( persistentBioMaterials );
    // return bioMaterialDimensionService.findOrCreate( bioMaterialDimension );
    // }

    /**
     * @param entity
     * @return
     */
    private ExpressionExperiment persistExpressionExperiment( ExpressionExperiment entity ) {

        log.info( "Persisting " + entity );

        if ( entity == null ) return null;
        if ( !isTransient( entity ) ) return entity;

        ExpressionExperiment existing = expressionExperimentService.findByName( entity.getName() );
        if ( existing != null ) {
            log.warn( "Expression experiment with same name exists (" + existing
                    + "), returning it (persister does not handle updates)" );
            return existing;
        }

        if ( entity.getOwner() == null ) {
            entity.setOwner( defaultOwner );
        }

        if ( entity.getAccession() != null ) {
            entity.setAccession( persistDatabaseEntry( entity.getAccession() ) );
        }

        if ( entity.getExperimentalDesigns() != null ) {
            ExperimentalDesign experimentalDesign = entity.getExperimentalDesigns();
            persistCollectionElements( experimentalDesign.getTypes() );

            for ( ExperimentalFactor experimentalFactor : experimentalDesign.getExperimentalFactors() ) {

                persistCollectionElements( experimentalFactor.getAnnotations() );

                OntologyEntry category = experimentalFactor.getCategory();
                if ( category != null ) {
                    experimentalFactor.setCategory( persistOntologyEntry( category ) );
                }

                // factorvalue is cascaded.
                for ( FactorValue factorValue : experimentalFactor.getFactorValues() ) {
                    fillInFactorValueAssociations( factorValue );
                }
            }
        }

        if ( log.isInfoEnabled() ) log.info( entity.getBioAssays().size() + " bioAssays in " + entity );

        Collection<BioAssay> alreadyFilled = new HashSet<BioAssay>();

        if ( entity.getDesignElementDataVectors().size() > 0 ) {
            alreadyFilled = fillInExpressionExperimentDataVectorAssociations( entity );
            // these are persistent! So we don't use the cascade.
            entity.setBioAssays( alreadyFilled );
        } else {
            for ( BioAssay bA : entity.getBioAssays() ) {
                fillInBioAssayAssociations( bA );
                alreadyFilled.add( bA );
        }
        }

        for ( ExpressionExperimentSubSet subset : entity.getSubsets() ) {
            for ( BioAssay bA : subset.getBioAssays() ) {
                bA.setId( persistBioAssay( bA ).getId() );
                assert bA.getArrayDesignUsed().getId() != null;
                if ( !alreadyFilled.contains( bA ) ) {
                    throw new IllegalStateException( bA + " not in the experiment?" );
                }
                // fillInBioAssayAssociations( bA );
                // alreadyFilled.add( bA );
            }
        }

        return expressionExperimentService.create( entity );
    }

    /**
     * @param factorValue
     * @return
     */
    private FactorValue persistFactorValue( FactorValue factorValue ) {
        if ( factorValue == null ) return null;
        if ( !isTransient( factorValue ) ) return factorValue;

        fillInFactorValueAssociations( factorValue );

        return factorValueService.findOrCreate( factorValue );

    }

    /**
     * @param factorValue
     */
    private void fillInFactorValueAssociations( FactorValue factorValue ) {
        if ( factorValue.getOntologyEntry() != null ) {
            if ( factorValue.getMeasurement() != null ) {
                throw new IllegalStateException(
                        "FactorValue can only have one of a value, ontology entry, or measurement." );
            }
            OntologyEntry ontologyEntry = factorValue.getOntologyEntry();
            factorValue.setOntologyEntry( persistOntologyEntry( ontologyEntry ) );
        } else if ( factorValue.getValue() != null ) {
            if ( factorValue.getMeasurement() != null || factorValue.getOntologyEntry() != null ) {
                throw new IllegalStateException(
                        "FactorValue can only have one of a value, ontology entry, or measurement." );
            }

        }
    }

    /**
     * This is used when creating vectors "one by one" rather than by composition with an ExpressionExperiment.
     * 
     * @param vector
     * @return FIXME we may not want to use this, and always do it with an update of the ExpressionExperiment instead.
     */
    private DesignElementDataVector persistDesignElementDataVector( DesignElementDataVector vector ) {
        if ( vector == null ) return null;
        this.fillInDesignElementDataVectorAssociations( vector );
        vector.setExpressionExperiment( persistExpressionExperiment( vector.getExpressionExperiment() ) );
        return designElementDataVectorService.findOrCreate( vector );
    }

    /**
     * @param bioAssayDimensionCache
     * @param vect
     */
    private BioAssayDimension checkBioAssayDimensionCache( DesignElementDataVector vect ) {
        if ( !isTransient( vect.getBioAssayDimension() ) ) return vect.getBioAssayDimension();
        assert bioAssayDimensionCache != null;
        String dimensionName = vect.getBioAssayDimension().getName();
        if ( bioAssayDimensionCache.containsKey( dimensionName ) ) {
            vect.setBioAssayDimension( bioAssayDimensionCache.get( dimensionName ) );
        } else {
            BioAssayDimension bAd = persistBioAssayDimension( vect.getBioAssayDimension() );
            bioAssayDimensionCache.put( dimensionName, bAd );
            vect.setBioAssayDimension( bAd );
        }
        return bioAssayDimensionCache.get( dimensionName );
    }

    /**
     * @param compound
     * @return
     */
    private Compound persistCompound( Compound compound ) {
        if ( compound == null ) return null;
        compound.setCompoundIndices( persistOntologyEntry( compound.getCompoundIndices() ) );
        if ( compound.getIsSolvent() == null )
            throw new IllegalArgumentException( "Compound must have 'isSolvent' value set." );
        return compoundService.findOrCreate( compound );
    }

    /**
     * @param bioAssayDimensionService The bioAssayDimensionService to set.
     */
    public void setBioAssayDimensionService( BioAssayDimensionService bioAssayDimensionService ) {
        this.bioAssayDimensionService = bioAssayDimensionService;
    }

    /**
     * @param bioAssayService The bioAssayService to set.
     */
    public void setBioAssayService( BioAssayService bioAssayService ) {
        this.bioAssayService = bioAssayService;
    }

    // /**
    // * @param bioMaterialDimensionService The bioMaterialDimensionService to set.
    // */
    // public void setBioMaterialDimensionService( BioMaterialDimensionService bioMaterialDimensionService ) {
    // this.bioMaterialDimensionService = bioMaterialDimensionService;
    // }

    /**
     * @param bioMaterialService The bioMaterialService to set.
     */
    public void setBioMaterialService( BioMaterialService bioMaterialService ) {
        this.bioMaterialService = bioMaterialService;
    }

    /**
     * @param compoundService The compoundService to set.
     */
    public void setCompoundService( CompoundService compoundService ) {
        this.compoundService = compoundService;
    }

    /**
     * @param designElementDataVectorService The designElementDataVectorService to set.
     */
    public void setDesignElementDataVectorService( DesignElementDataVectorService designElementDataVectorService ) {
        this.designElementDataVectorService = designElementDataVectorService;
    }

    /**
     * @param expressionExperimentService The expressionExperimentService to set.
     */
    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    /**
     * @param factorValueService The factorValueService to set.
     */
    public void setFactorValueService( FactorValueService factorValueService ) {
        this.factorValueService = factorValueService;
    }

}