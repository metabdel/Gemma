/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.gemma.analysis.preprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.io.ByteArrayConverter;
import ubic.gemma.analysis.service.ExpressionExperimentVectorManipulatingService;
import ubic.gemma.model.common.quantitationtype.PrimitiveType;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignService;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssay.BioAssayService;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimensionService;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.RawExpressionDataVector;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;

/**
 * Tackles the problem of concatenating DesignElementDataVectors for a single experiment. This is necessary When a study
 * uses two or more similar array designs without 'replication'. Typical of the genre is GSE60 ("Diffuse large B-cell
 * lymphoma"), with 31 BioAssays on GPL174, 35 BioAssays on GPL175, and 66 biomaterials. A more complex one: GSE3500,
 * with 13 ArrayDesigns. In that (and others) case, there are quantitation types which do not appear on all array
 * designs, leaving gaps in the vectors that have to be filled in.
 * <p>
 * The algorithm for dealing with this is a preprocessing step:
 * <ol>
 * <li>Generate a merged set of vectors for each of the (important) quantitation types.</li>
 * <li>Create a merged BioAssayDimension</li>
 * <li>Persist the new vectors, which are now tied to a <em>single DesignElement</em>. This is, strictly speaking,
 * incorrect, but because the design elements used in the vector all point to the same sequence, there is no major
 * problem in analyzing this. However, there is a potential loss of information.
 * <li>Cleanup: remove old vectors and BioAssayDimensions.
 * </ol>
 * <p>
 * Vectors which are empty (all missing values) are not persisted. If problems are found during merging, an exception
 * will be thrown, though this may leave things in a bad state requiring a reload of the data.
 * 
 * @spring.bean id="vectorMergingService"
 * @spring.property name="expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name="arrayDesignService" ref="arrayDesignService"
 * @spring.property name="bioAssayDimensionService" ref="bioAssayDimensionService"
 * @spring.property name="bioAssayService" ref="bioAssayService"
 * @author pavlidis
 * @version $Id$
 * @see ExpressionDataMatrixBuilder
 */
public class VectorMergingService extends ExpressionExperimentVectorManipulatingService {

    private static Log log = LogFactory.getLog( VectorMergingService.class.getName() );

    private ExpressionExperimentService expressionExperimentService;

    private ArrayDesignService arrayDesignService;

    private BioAssayDimensionService bioAssayDimensionService;

    private BioAssayService bioAssayService;

    /**
     * Merge the vectors for the given experiment.
     * 
     * @param expressionExperiment
     */
    public void mergeVectors( ExpressionExperiment expressionExperiment ) {
        this.mergeVectors( expressionExperiment, null );

    }

    /**
     * A main entry point for this class.
     * 
     * @param expExp
     */
    public void mergeVectors( ExpressionExperiment expExp, Long dimId ) {

        expressionExperimentService.thawLite( expExp );
        Collection<QuantitationType> qts = expressionExperimentService.getQuantitationTypes( expExp );

        Collection<ArrayDesign> arrayDesigns = expressionExperimentService.getArrayDesignsUsed( expExp );

        if ( arrayDesigns.size() > 1 ) {
            throw new IllegalArgumentException( "Cannot cope with more than one platform" );
        }

        ArrayDesign arrayDesign = arrayDesigns.iterator().next();
        arrayDesignService.thawLite( arrayDesign );

        log.info( qts.size() + " quantitation types" );

        /*
         * Load all the bioassay dimensions, which will be merged.
         */
        Collection<BioAssayDimension> allOldBioAssayDims = new HashSet<BioAssayDimension>();
        for ( BioAssay ba : expExp.getBioAssays() ) {
            allOldBioAssayDims.addAll( bioAssayService.findBioAssayDimensions( ba ) );
        }
        log.info( allOldBioAssayDims.size() + " bioassaydimensions to merge" );
        List<BioAssayDimension> sortedOldDims = sortedBioAssayDimensions( allOldBioAssayDims );

        BioAssayDimension newBioAd = getNewBioAssayDimension( dimId, sortedOldDims );
        int totalBioAssays = newBioAd.getBioAssays().size();
        assert totalBioAssays == expExp.getBioAssays().size() : "experiment has " + expExp.getBioAssays().size()
                + " but new bioassaydimension has " + totalBioAssays;

        for ( QuantitationType type : qts ) {

            Collection<? extends DesignElementDataVector> oldVectors = getVectorsForOneQuantitationType( type );

            if ( oldVectors.size() == 0 ) {
                log.warn( "No vectors for " + type + "!" );
                continue;
            }

            log.info( "Processing " + type + ", " + oldVectors.size() + " vectors" );

            Map<DesignElement, Collection<DesignElementDataVector>> deVMap = getDevMap( oldVectors );

            Collection<DesignElementDataVector> newVectors = new HashSet<DesignElementDataVector>();
            int numAllMissing = 0;
            for ( DesignElement de : deVMap.keySet() ) {

                DesignElementDataVector vector = initializeNewVector( expExp, newBioAd, type, de );
                Collection<DesignElementDataVector> dedvs = deVMap.get( de );

                /*
                 * these ugly nested loops are to ENSURE that we get the vector reconstructed properly. For each of the
                 * old bioassayDimensions, find the designelementdatavector that uses it. If there isn't one, fill in
                 * the values for that dimension with missing data. We go through the dimensions in the same order that
                 * we joined them up.
                 */

                List<Object> data = new ArrayList<Object>();
                int totalMissingInVector = makeMergedData( sortedOldDims, newBioAd, type, de, dedvs, data );
                if ( totalMissingInVector == totalBioAssays ) {
                    numAllMissing++;
                    continue; // we don't save data that is all missing.
                }

                if ( data.size() != totalBioAssays ) {
                    throw new IllegalStateException( "Wrong number of values for " + de + " / " + type + ", expected "
                            + totalBioAssays + ", got " + data.size() );
                }

                byte[] newDataAr = converter.toBytes( data.toArray() );

                vector.setData( newDataAr );

                newVectors.add( vector );
            }

            // print( newVectors ); // debugging

            if ( newVectors.size() > 0 ) {
                log.info( "Creating " + newVectors.size() + " new vectors for " + type );
                designElementDataVectorService.create( newVectors );
            } else {
                throw new IllegalStateException( "Unexpectedly, no new vectors for " + type );
            }

            if ( numAllMissing > 0 ) {
                log.info( numAllMissing + " vectors had all missing values and were junked" );
            }

            log.info( "Removing " + oldVectors.size() + " old vectors for " + type );
            designElementDataVectorService.remove( oldVectors );

        }
        // remove the old BioAssayDimensions
        for ( BioAssayDimension oldDim : allOldBioAssayDims ) {
            // careful, the 'new' bioassaydimension might be one of the old ones that we're reusing.
            if ( oldDim.equals( newBioAd ) ) continue;
            bioAssayDimensionService.remove( oldDim );
        }
    }

    public void setArrayDesignService( ArrayDesignService arrayDesignService ) {
        this.arrayDesignService = arrayDesignService;
    }

    public void setBioAssayDimensionService( BioAssayDimensionService bioAssayDimensionService ) {
        this.bioAssayDimensionService = bioAssayDimensionService;
    }

    /**
     * @param bioAssayService the bioAssayService to set
     */
    public void setBioAssayService( BioAssayService bioAssayService ) {
        this.bioAssayService = bioAssayService;
    }

    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    /**
     * Create a new one or use an existing one. (an existing one might be found if this process was started once before
     * and aborted partway through).
     * 
     * @param oldDims in the sort order to be used.
     * @return
     */
    private BioAssayDimension combineBioAssayDimensions( List<BioAssayDimension> oldDims ) {

        List<BioAssay> bioAssays = new ArrayList<BioAssay>();
        for ( BioAssayDimension bioAd : oldDims ) {
            for ( BioAssay bioAssay : bioAd.getBioAssays() ) {
                if ( bioAssays.contains( bioAssay ) ) {
                    throw new IllegalStateException( "Duplicate bioassay for biodimension: " + bioAssay );
                }
                bioAssays.add( bioAssay );

            }
        }

        // first see if we already have an equivalent one.
        boolean found = true;
        for ( BioAssayDimension newDim : oldDims ) {
            // size should be the same.
            List<BioAssay> assaysInExisting = ( List<BioAssay> ) newDim.getBioAssays();
            if ( assaysInExisting.size() != bioAssays.size() ) {
                continue;
            }

            for ( int i = 0; i < bioAssays.size(); i++ ) {
                if ( !assaysInExisting.get( i ).equals( bioAssays.get( i ) ) ) {
                    found = false;
                    break;
                }
            }
            if ( !found ) continue;
            log.info( "Already have a dimension created that fits the bill - removing it from the 'old' list." );
            oldDims.remove( newDim );
            return newDim;
        }

        BioAssayDimension newBioAd = BioAssayDimension.Factory.newInstance();
        newBioAd.setName( "" );
        newBioAd.setDescription( "Generated by the merger of " + oldDims.size() + " dimensions: " );

        for ( BioAssayDimension bioAd : oldDims ) {
            newBioAd.setName( newBioAd.getName() + bioAd.getName() + " " );
            newBioAd.setDescription( newBioAd.getDescription() + bioAd.getName() + " " );
        }

        newBioAd.setName( StringUtils.abbreviate( newBioAd.getName(), 255 ) );
        newBioAd.setBioAssays( bioAssays );

        newBioAd = bioAssayDimensionService.create( newBioAd );
        log.info( "Created new bioAssayDimension with " + newBioAd.getBioAssays().size() + " bioassays." );
        return newBioAd;
    }

    /**
     * @param de
     * @param data
     * @param oldDim
     * @param representation
     * @return The number of missing values which were added.
     */
    private int fillMissingValues( DesignElement de, List<Object> data, BioAssayDimension oldDim,
            PrimitiveType representation ) {
        int nullsNeeded = oldDim.getBioAssays().size();
        for ( int i = 0; i < nullsNeeded; i++ ) {
            // FIXME this code taken from GeoConverter
            if ( representation.equals( PrimitiveType.DOUBLE ) ) {
                data.add( Double.NaN );
            } else if ( representation.equals( PrimitiveType.STRING ) ) {
                data.add( "" );
            } else if ( representation.equals( PrimitiveType.INT ) ) {
                data.add( 0 );
            } else if ( representation.equals( PrimitiveType.BOOLEAN ) ) {
                data.add( false );
            } else {
                throw new UnsupportedOperationException( "Missing values in data vectors of type " + representation
                        + " not supported (when processing " + de );
            }
        }
        return nullsNeeded;
    }

    /**
     * @param oldVectors
     * @return map of design element to vectors.
     */
    private Map<DesignElement, Collection<DesignElementDataVector>> getDevMap(
            Collection<? extends DesignElementDataVector> oldVectors ) {
        Map<DesignElement, Collection<DesignElementDataVector>> deVMap = new HashMap<DesignElement, Collection<DesignElementDataVector>>();
        for ( DesignElementDataVector vector : oldVectors ) {
            if ( !deVMap.containsKey( vector.getDesignElement() ) ) {
                deVMap.put( vector.getDesignElement(), new HashSet<DesignElementDataVector>() );
            }
            deVMap.get( vector.getDesignElement() ).add( vector );
        }
        return deVMap;
    }

    private BioAssayDimension getNewBioAssayDimension( Long dimId, List<BioAssayDimension> sortedOldDims ) {
        BioAssayDimension newBioAd;
        if ( dimId != null ) {
            newBioAd = bioAssayDimensionService.load( dimId );
            if ( newBioAd == null ) {
                throw new IllegalArgumentException( "No bioAssayDimension with id " + dimId );
            }
            log.info( "Using existing bioassaydimension" );
        } else {
            newBioAd = combineBioAssayDimensions( sortedOldDims );
        }
        return newBioAd;
    }

    private DesignElementDataVector initializeNewVector( ExpressionExperiment expExp, BioAssayDimension newBioAd,
            QuantitationType type, DesignElement de ) {
        DesignElementDataVector vector = RawExpressionDataVector.Factory.newInstance();
        vector.setBioAssayDimension( newBioAd );
        vector.setDesignElement( de );
        vector.setQuantitationType( type );
        vector.setExpressionExperiment( expExp );
        return vector;
    }

    /**
     * @param sortedOldDims
     * @param newBioAd
     * @param type
     * @param de
     * @param dedvs
     * @param data
     * @return
     */
    private int makeMergedData( List<BioAssayDimension> sortedOldDims, BioAssayDimension newBioAd,
            QuantitationType type, DesignElement de, Collection<DesignElementDataVector> dedvs, List<Object> data ) {
        int totalMissingInVector = 0;
        for ( BioAssayDimension oldDim : sortedOldDims ) {
            // careful, the 'new' bioassaydimension might be one of the old ones that we're reusing.
            if ( oldDim.equals( newBioAd ) ) continue;
            boolean found = false;
            PrimitiveType representation = type.getRepresentation();
            for ( DesignElementDataVector oldV : dedvs ) {
                if ( oldV.getBioAssayDimension().equals( oldDim ) ) {
                    found = true;
                    convertFromBytes( data, representation, oldV );
                    break;
                }
            }
            if ( !found ) {
                int missing = fillMissingValues( de, data, oldDim, representation );
                totalMissingInVector += missing;
            }
        }
        return totalMissingInVector;
    }

    /**
     * Just for debugging.
     * 
     * @param newVectors
     */
    @SuppressWarnings("unused")
    private void print( Collection<DesignElementDataVector> newVectors ) {
        StringBuilder buf = new StringBuilder();
        ByteArrayConverter conv = new ByteArrayConverter();
        for ( DesignElementDataVector vector : newVectors ) {
            buf.append( vector.getDesignElement() );
            QuantitationType qtype = vector.getQuantitationType();
            if ( qtype.getRepresentation().equals( PrimitiveType.DOUBLE ) ) {
                double[] vals = conv.byteArrayToDoubles( vector.getData() );
                for ( double d : vals ) {
                    buf.append( "\t" + d );
                }
            } else if ( qtype.getRepresentation().equals( PrimitiveType.INT ) ) {
                int[] vals = conv.byteArrayToInts( vector.getData() );
                for ( int i : vals ) {
                    buf.append( "\t" + i );
                }
            } else if ( qtype.getRepresentation().equals( PrimitiveType.BOOLEAN ) ) {
                boolean[] vals = conv.byteArrayToBooleans( vector.getData() );
                for ( boolean d : vals ) {
                    buf.append( "\t" + d );
                }
            } else if ( qtype.getRepresentation().equals( PrimitiveType.STRING ) ) {
                String[] vals = conv.byteArrayToStrings( vector.getData() );
                for ( String d : vals ) {
                    buf.append( "\t" + d );
                }

            }
            buf.append( "\n" );
        }

        log.info( "\n" + buf );
    }

    /**
     * Provide a sorted list of bioassaydimensions for merging. The actual order doesn't matter, just so long as we are
     * consistent further on.
     * 
     * @param oldBioAssayDims
     * @return
     */
    private List<BioAssayDimension> sortedBioAssayDimensions( Collection<BioAssayDimension> oldBioAssayDims ) {
        List<BioAssayDimension> sortedOldDims = new ArrayList<BioAssayDimension>();
        sortedOldDims.addAll( oldBioAssayDims );
        Collections.sort( sortedOldDims, new Comparator<BioAssayDimension>() {
            public int compare( BioAssayDimension o1, BioAssayDimension o2 ) {
                return o1.getId().compareTo( o2.getId() );
            }
        } );
        return sortedOldDims;
    }
}
