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
package ubic.gemma.datastructure.matrix;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * Base class for ExpressionDataMatrix implementations.
 * 
 * @author pavlidis
 * @version $Id$
 */
abstract public class BaseExpressionDataMatrix implements ExpressionDataMatrix {

    private Log log = LogFactory.getLog( ExpressionDataDoubleMatrix.class );
    protected LinkedHashSet<DesignElement> rowElements;
    protected Collection<BioAssayDimension> bioAssayDimensions;
    protected Map<BioAssay, Integer> columnAssayMap;
    protected Map<BioMaterial, Integer> columnBioMaterialMap;
    protected Map<Integer, Collection<BioAssay>> columnBioAssayMapByInteger;
    protected Map<Integer, BioMaterial> columnBioMaterialMapByInteger;
    protected Map<DesignElement, Integer> rowDesignElementMap;

    protected void init() {
        rowElements = new LinkedHashSet<DesignElement>();
        rowDesignElementMap = new HashMap<DesignElement, Integer>();
        bioAssayDimensions = new HashSet<BioAssayDimension>();
        columnAssayMap = new LinkedHashMap<BioAssay, Integer>();
        columnBioMaterialMap = new LinkedHashMap<BioMaterial, Integer>();
        columnBioMaterialMapByInteger = new LinkedHashMap<Integer, BioMaterial>();
        columnBioAssayMapByInteger = new LinkedHashMap<Integer, Collection<BioAssay>>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getBioAssayForColumn(int)
     */
    public Collection<BioAssay> getBioAssaysForColumn( int index ) {
        return this.columnBioAssayMapByInteger.get( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getBioMaterialForColumn(int)
     */
    public BioMaterial getBioMaterialForColumn( int index ) {
        return this.columnBioMaterialMapByInteger.get( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.datastructure.matrix.ExpressionDataMatrix#getRowElements()
     */
    public Collection<DesignElement> getRowElements() {
        return this.rowElements;
    }

    public int getColumnIndex( BioMaterial bioMaterial ) {
        return columnBioMaterialMap.get( bioMaterial );
    }

    public int getRowIndex( DesignElement designElement ) {
        return rowDesignElementMap.get( designElement );
    }

    /**
     * @param designElements
     * @param quantitationType
     * @return Collection<DesignElementDataVector>
     */
    protected Collection<DesignElementDataVector> selectVectors( Collection<DesignElement> designElements,
            QuantitationType quantitationType ) {
        Collection<DesignElementDataVector> vectorsOfInterest = new HashSet<DesignElementDataVector>();
        int i = 0;
        for ( DesignElement designElement : designElements ) {
            DesignElementDataVector vectorOfInterest = null;
            Collection<DesignElementDataVector> vectors = designElement.getDesignElementDataVectors();
            for ( DesignElementDataVector vector : vectors ) {
                QuantitationType vectorQuantitationType = vector.getQuantitationType();
                if ( vectorQuantitationType.equals( quantitationType ) ) {
                    vectorOfInterest = vector;
                    vectorsOfInterest.add( vectorOfInterest );
                    rowElements.add( designElement );
                    bioAssayDimensions.add( vector.getBioAssayDimension() );
                    rowDesignElementMap.put( designElement, i );
                    i++; // only increment if we actually added a row.
                    break;
                }
            }
            if ( vectorOfInterest == null ) {
                log.warn( "Vector not found for quantitation type " + quantitationType.getType() + ".  Skipping ..." );
                continue;
            }

        }
        return vectorsOfInterest;
    }

    /**
     * @param expressionExperiment
     * @param quantitationTypes
     * @return
     */
    protected Collection<DesignElementDataVector> selectVectors( ExpressionExperiment expressionExperiment,
            Collection<QuantitationType> quantitationTypes ) {
        Collection<DesignElementDataVector> selected = new HashSet<DesignElementDataVector>();
        Collection<DesignElementDataVector> vectors = expressionExperiment.getDesignElementDataVectors();
        for ( QuantitationType type : quantitationTypes ) {
            selected.addAll( this.selectVectors( type, vectors ) );
        }
        return selected;
    }

    /**
     * @param expressionExperiment
     * @param quantitationType
     * @return Collection<DesignElementDataVector>
     */
    protected Collection<DesignElementDataVector> selectVectors( ExpressionExperiment expressionExperiment,
            QuantitationType quantitationType ) {
        Collection<DesignElementDataVector> vectors = expressionExperiment.getDesignElementDataVectors();
        return selectVectors( quantitationType, vectors );
    }

    /**
     * @param quantitationType
     * @param vectors
     * @return Collection<DesignElementDataVector>
     */
    protected Collection<DesignElementDataVector> selectVectors( QuantitationType quantitationType,
            Collection<DesignElementDataVector> vectors ) {
        Collection<DesignElementDataVector> vectorsOfInterest = new LinkedHashSet<DesignElementDataVector>();
        int i = 0;
        for ( DesignElementDataVector vector : vectors ) {
            QuantitationType vectorQuantitationType = vector.getQuantitationType();
            if ( vectorQuantitationType.equals( quantitationType ) ) {
                vectorsOfInterest.add( vector );
                rowElements.add( vector.getDesignElement() );
                bioAssayDimensions.add( vector.getBioAssayDimension() );
                rowDesignElementMap.put( vector.getDesignElement(), i );
                i++; // only increment if we actually added a row.
            }
        }
        return vectorsOfInterest;
    }

    /**
     * @param quantitationType
     * @param bioAssayDimension
     * @param vectors
     * @return
     */
    protected Collection<DesignElementDataVector> selectVectors( ExpressionExperiment expressionExperiment,
            QuantitationType quantitationType, BioAssayDimension bioAssayDimension ) {
        Collection<DesignElementDataVector> vectors = expressionExperiment.getDesignElementDataVectors();
        Collection<DesignElementDataVector> vectorsOfInterest = new LinkedHashSet<DesignElementDataVector>();
        int i = 0;
        for ( DesignElementDataVector vector : vectors ) {
            QuantitationType vectorQuantitationType = vector.getQuantitationType();
            BioAssayDimension cand = vector.getBioAssayDimension();
            if ( vectorQuantitationType.equals( quantitationType ) && cand.equals( bioAssayDimension ) ) {
                vectorsOfInterest.add( vector );
                rowElements.add( vector.getDesignElement() );
                bioAssayDimensions.add( vector.getBioAssayDimension() );
                rowDesignElementMap.put( vector.getDesignElement(), i );
                i++; // only increment if we actually added a row.
            }
        }
        return vectorsOfInterest;
    }

    /**
     * @param expressionExperiment
     * @param quantitationTypes
     * @param soughtBioAssayDimensions in the same order as the quantitation types
     * @return
     */
    protected Collection<DesignElementDataVector> selectVectors( ExpressionExperiment expressionExperiment,
            List<QuantitationType> quantitationTypes, List<BioAssayDimension> soughtBioAssayDimensions ) {

        if ( quantitationTypes.size() != soughtBioAssayDimensions.size() )
            throw new IllegalArgumentException(
                    "Must have the same number of quantitation types and bioassay dimensions" );

        Collection<DesignElementDataVector> vectorsOfInterest = new LinkedHashSet<DesignElementDataVector>();

        int j = 0;
        for ( int i = 0; i < quantitationTypes.size(); i++ ) {
            QuantitationType soughtType = quantitationTypes.get( i );
            BioAssayDimension soughtDim = soughtBioAssayDimensions.get( i );
            assert soughtType != null && soughtDim != null;
            for ( DesignElementDataVector vector : expressionExperiment.getDesignElementDataVectors() ) {
                QuantitationType vectorQuantitationType = vector.getQuantitationType();
                BioAssayDimension cand = vector.getBioAssayDimension();
                if ( vectorQuantitationType.equals( soughtType ) && cand.equals( soughtDim ) ) {
                    vectorsOfInterest.add( vector );
                    rowElements.add( vector.getDesignElement() );
                    this.bioAssayDimensions.add( vector.getBioAssayDimension() );
                    rowDesignElementMap.put( vector.getDesignElement(), j );
                    j++; // only increment if we actually added a row.
                }
            }
        }

        return vectorsOfInterest;
    }

    /**
     * Deals with the fact that the bioassay dimensions can vary in size, and don't even need to overlap in the
     * biomaterials used. In the case where there is a single bioassaydimension this reduces to simply associating each
     * column with a bioassay (though we are forced to use an integer under the hood).
     * <p>
     * For example, in the following diagram "-" indicates a biomaterial, while "*" indicates a bioassay. Each row of
     * "*" indicates samples run on a different microarray design (a different bio assay material). In the examples we
     * assume there is just a single biomaterial dimension.
     * 
     * <pre>
     *                                                                                            ----------------
     *                                                                                            ******              -- only a few samples run on this platform
     *                                                                                              **********        -- ditto
     *                                                                                                        ****    -- these samples were not run on any of the other platforms (rare but possible).
     * </pre>
     * 
     * <p>
     * A simpler case:
     * </p>
     * 
     * <pre>
     *                                                                                            ----------------
     *                                                                                            ****************
     *                                                                                            ************
     *                                                                                            ********
     * </pre>
     * 
     * <p>
     * A more typical and easy case (one microarray design used):
     * </p>
     * 
     * <pre>
     *                                                                                            -----------------
     *                                                                                            *****************
     * </pre>
     * 
     * <p>
     * If every sample was run on two different array designs:
     * </p>
     * 
     * <pre>
     *                                                                                            -----------------
     *                                                                                            *****************
     *                                                                                            *****************
     * </pre>
     * 
     * <p>
     * Clearly the first case is the only challenge. Because there can be limited or no overlap between the bioassay
     * dimensions,we cannot assume the dimensions of the matrix will be defined by the longest bioassaydimension.
     * </p>
     * 
     * @return int
     */
    protected int setUpColumnElements() {
        log.debug( "Setting up column elements" );
        assert this.bioAssayDimensions != null && this.bioAssayDimensions.size() > 0 : "No bioAssayDimensions defined";

        /*
         * build a map of biomaterials to bioassays. Because there can be more than one biomaterial used per bioassay,
         * we group them together. Each bioMaterialGroup corresponds to a single column in the matrix.
         */
        Map<BioMaterial, Collection<BioAssay>> bioMaterialMap = new LinkedHashMap<BioMaterial, Collection<BioAssay>>();
        Collection<Collection<BioMaterial>> bioMaterialGroups = new LinkedHashSet<Collection<BioMaterial>>();
        for ( BioAssayDimension dimension : this.bioAssayDimensions ) {
            log.debug( "Processing: " + dimension );
            for ( BioAssay ba : dimension.getBioAssays() ) {
                log.debug( " Processing " + ba );
                Collection<BioMaterial> bioMaterials = ba.getSamplesUsed();

                log.debug( " .... " + bioMaterials );
                if ( !alreadySeenGroup( bioMaterialGroups, bioMaterials ) ) {
                    log.debug( "New group " + bioMaterials );
                    bioMaterialGroups.add( bioMaterials );
                }

                for ( BioMaterial material : bioMaterials ) {
                    log.debug( "  Processing " + material );
                    if ( !bioMaterialMap.containsKey( material ) ) {
                        bioMaterialMap.put( material, new HashSet<BioAssay>() );
                    }
                    bioMaterialMap.get( material ).add( ba );
                }
            }
        }

        int column = 0;
        for ( Collection<BioMaterial> bms : bioMaterialGroups ) {
            for ( BioMaterial bioMaterial : bms ) {
                for ( BioAssay assay : bioMaterialMap.get( bioMaterial ) ) {
                    if ( this.columnBioMaterialMap.containsKey( bioMaterial ) ) {
                        int columnIndex = columnBioMaterialMap.get( bioMaterial );
                        this.columnAssayMap.put( assay, columnIndex );
                        log.debug( assay + " --> column " + columnIndex );

                        if ( columnBioAssayMapByInteger.get( columnIndex ) == null ) {
                            columnBioAssayMapByInteger.put( columnIndex, new HashSet<BioAssay>() );
                        }
                        columnBioAssayMapByInteger.get( columnIndex ).add( assay );
                    } else {
                        log.debug( bioMaterial + " --> column " + column );
                        log.debug( assay + " --> column " + column );
                        this.columnBioMaterialMap.put( bioMaterial, column );
                        this.columnAssayMap.put( assay, column );
                        if ( columnBioAssayMapByInteger.get( column ) == null ) {
                            columnBioAssayMapByInteger.put( column, new HashSet<BioAssay>() );
                        }

                        // FIXME This should be a collection of biomaterials. See bug 629.
                        columnBioMaterialMapByInteger.put( column, bioMaterial );
                        columnBioAssayMapByInteger.get( column ).add( assay );
                    }
                }

            }
            column++;
        }

        assert bioMaterialGroups.size() == columnBioMaterialMapByInteger.keySet().size();
        return columnBioMaterialMapByInteger.keySet().size();
    }

    /**
     * Determine if the bioMaterial group has already been seen; this is necessary because it is possible to have
     * multiple bioassays use the same biomaterials.
     * <p>
     * FIXME this does not work.
     * 
     * @param bioMaterialGroups
     * @param candidateGroup
     * @return
     */
    private boolean alreadySeenGroup( Collection<Collection<BioMaterial>> bioMaterialGroups,
            Collection<BioMaterial> candidateGroup ) {
        assert candidateGroup.size() > 0;
        for ( Collection<BioMaterial> existingGroup : bioMaterialGroups ) {
            boolean alreadyIn = true;
            for ( BioMaterial candidateMember : candidateGroup ) {
                boolean contained = false;
                for ( BioMaterial existing : existingGroup ) {
                    if ( existing.equals( candidateMember ) ) {
                        contained = true;
                        break;
                    }
                }
                if ( !contained ) {
                    // if ( !existingGroup.contains( candidateMember ) ) { // for some reason this does not work.
                    // log.debug( existingGroup + " does not contain " + candidateMember );
                    alreadyIn = false; // try the next group.
                    break;
                }
            }
            if ( alreadyIn ) return true;
        }
        return false;
    }

    protected abstract void vectorsToMatrix( Collection<DesignElementDataVector> vectors );
}
