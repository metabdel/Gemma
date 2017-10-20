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
package ubic.gemma.core.datastructure.matrix;

import cern.colt.matrix.ObjectMatrix1D;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.io.ByteArrayConverter;
import ubic.gemma.model.common.quantitationtype.PrimitiveType;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.designElement.CompositeSequence;

import java.util.*;

/**
 * Matrix of booleans mapped from an ExpressionExperiment.
 *
 * @author pavlidis
 *
 */
public class ExpressionDataBooleanMatrix extends BaseExpressionDataMatrix<Boolean> {

    private static final long serialVersionUID = 1L;
    private ObjectMatrixImpl<CompositeSequence, Integer, Boolean> matrix;

    public ExpressionDataBooleanMatrix( Collection<? extends DesignElementDataVector> vectors ) {
        init();

        for ( DesignElementDataVector dedv : vectors ) {
            if ( !dedv.getQuantitationType().getRepresentation().equals( PrimitiveType.BOOLEAN ) ) {
                throw new IllegalStateException( "Cannot convert non-boolean quantitation types into boolean matrix" );
            }
        }

        selectVectors( vectors );
        vectorsToMatrix( vectors );
    }

    public ExpressionDataBooleanMatrix( Collection<? extends DesignElementDataVector> vectors,
            List<QuantitationType> qtypes ) {
        init();
        Collection<DesignElementDataVector> selectedVectors = selectVectors( vectors, qtypes );
        vectorsToMatrix( selectedVectors );
    }

    @Override
    public int columns() {
        return matrix.columns();
    }

    @Override
    public Boolean get( CompositeSequence designElement, BioAssay bioAssay ) {
        return this.matrix.get( matrix.getRowIndexByName( designElement ),
                matrix.getColIndexByName( this.columnAssayMap.get( bioAssay ) ) );
    }

    @Override
    public Boolean get( int row, int column ) {
        return matrix.get( row, column );
    }

    @Override
    public Boolean[][] get( List<CompositeSequence> designElements, List<BioAssay> bioAssays ) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] getColumn( BioAssay bioAssay ) {
        int index = this.columnAssayMap.get( bioAssay );
        return getColumn( index );
    }

    @Override
    public Boolean[] getColumn( Integer index ) {
        ObjectMatrix1D rawResult = this.matrix.viewColumn( index );
        Boolean[] res = new Boolean[rawResult.size()];
        int i = 0;
        for ( Object o : rawResult.toArray() ) {
            res[i] = ( Boolean ) o;
            i++;
        }
        return res;
    }

    @Override
    public Boolean[][] getColumns( List<BioAssay> bioAssays ) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[][] getRawMatrix() {
        Boolean[][] dMatrix = new Boolean[matrix.rows()][matrix.columns()];
        for ( int i = 0; i < matrix.rows(); i++ ) {
            Object[] rawRow = matrix.getRow( i );
            for ( int j = 0; j < rawRow.length; j++ ) {
                dMatrix[i][j] = ( Boolean ) rawRow[i];
            }
        }

        return dMatrix;
    }

    @Override
    public Boolean[] getRow( CompositeSequence designElement ) {
        Integer row = this.rowElementMap.get( designElement );
        if ( row == null )
            return null;
        Object[] rawRow = matrix.getRow( row );
        Boolean[] result = new Boolean[rawRow.length];
        for ( int i = 0, k = rawRow.length; i < k; i++ ) {
            assert rawRow[i] instanceof Boolean : "Got a " + rawRow[i].getClass().getName();
            result[i] = ( Boolean ) rawRow[i];
        }
        return result;
    }

    @Override
    public Boolean[] getRow( Integer index ) {
        return matrix.getRow( index );
    }

    @Override
    public Boolean[][] getRows( List<CompositeSequence> designElements ) {
        if ( designElements == null ) {
            return null;
        }

        Boolean[][] result = new Boolean[designElements.size()][];
        int i = 0;
        for ( CompositeSequence element : designElements ) {
            Boolean[] rowResult = getRow( element );
            result[i] = rowResult;
            i++;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.core.datastructure.matrix.ExpressionDataMatrix#hasMissingValues()
     */
    @Override
    public boolean hasMissingValues() {
        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                if ( matrix.get( i, j ) == null ) return true;
            }
        }
        return false;
    }

    @Override
    public int rows() {
        return matrix.rows();
    }

    @Override
    public void set( int row, int column, Boolean value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void vectorsToMatrix( Collection<? extends DesignElementDataVector> vectors ) {
        if ( vectors == null || vectors.size() == 0 ) {
            throw new IllegalArgumentException();
        }

        int maxSize = setUpColumnElements();

        this.matrix = createMatrix( vectors, maxSize );

    }

    /**
     * Fill in the data
     */
    private ObjectMatrixImpl<CompositeSequence, Integer, Boolean> createMatrix(
            Collection<? extends DesignElementDataVector> vectors, int maxSize ) {
        ObjectMatrixImpl<CompositeSequence, Integer, Boolean> mat = new ObjectMatrixImpl<>( vectors.size(), maxSize );

        // initialize the matrix to false
        for ( int i = 0; i < mat.rows(); i++ ) {
            for ( int j = 0; j < mat.columns(); j++ ) {
                mat.set( i, j, Boolean.FALSE );
            }
        }
        for ( int j = 0; j < mat.columns(); j++ ) {
            mat.addColumnName( j );
        }

        ByteArrayConverter bac = new ByteArrayConverter();
        Map<Integer, CompositeSequence> rowNames = new TreeMap<>();

        for ( DesignElementDataVector vector : vectors ) {
            BioAssayDimension dimension = vector.getBioAssayDimension();
            byte[] bytes = vector.getData();

            CompositeSequence designElement = vector.getDesignElement();

            Integer rowIndex = this.rowElementMap.get( designElement );
            assert rowIndex != null;

            rowNames.put( rowIndex, designElement );

            boolean[] vals = getVals( bac, vector, bytes );

            Collection<BioAssay> bioAssays = dimension.getBioAssays();

            if ( bioAssays.size() != vals.length ) {
                throw new IllegalStateException(
                        "Expected " + vals.length + " bioassays at design element " + designElement + ", got "
                                + bioAssays.size() );
            }

            Iterator<BioAssay> it = bioAssays.iterator();
            setMatBioAssayValues( mat, rowIndex, ArrayUtils.toObject( vals ), bioAssays, it );
        }

        for ( int i = 0; i < mat.rows(); i++ ) {
            mat.addRowName( rowNames.get( i ) );
        }

        assert mat.getRowNames().size() == mat.rows();

        return mat;
    }

    /**
     * Note that if we have trouble interpreting the data, it gets left as false.
     */
    private boolean[] getVals( ByteArrayConverter bac, DesignElementDataVector vector, byte[] bytes ) {
        boolean[] vals = null;
        if ( vector.getQuantitationType().getRepresentation().equals( PrimitiveType.BOOLEAN ) ) {
            vals = bac.byteArrayToBooleans( bytes );
        } else if ( vector.getQuantitationType().getRepresentation().equals( PrimitiveType.CHAR ) ) {
            char[] charVals = bac.byteArrayToChars( bytes );
            vals = new boolean[charVals.length];
            int j = 0;
            for ( char c : charVals ) {
                if ( c == 'P' ) {
                    vals[j] = true;
                } else if ( c == 'M' ) {
                    vals[j] = false;
                } else if ( c == 'A' ) {
                    vals[j] = false;
                } else {
                    vals[j] = false;
                }
                j++;
            }
        } else if ( vector.getQuantitationType().getRepresentation().equals( PrimitiveType.STRING ) ) {
            String val = bac.byteArrayToAsciiString( bytes );
            String[] fields = StringUtils.split( val, '\t' );
            vals = new boolean[fields.length];
            int j = 0;
            for ( String c : fields ) {
                switch ( c ) {
                    case "P":
                        vals[j] = true;
                        break;
                    case "M":
                        vals[j] = false;
                        break;
                    case "A":
                        vals[j] = false;
                        break;
                    default:
                        vals[j] = false;
                        break;
                }
                j++;
            }
        }
        return vals;
    }

}
