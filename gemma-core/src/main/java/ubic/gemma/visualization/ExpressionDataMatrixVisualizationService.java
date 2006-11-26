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
package ubic.gemma.visualization;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix2DNamed;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.gui.ColorMatrix;
import ubic.basecode.gui.JMatrixDisplay;
import ubic.gemma.datastructure.matrix.ExpressionDataMatrix;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.DesignElement;

/**
 * A service to generate visualizations. Can be used to generate a color mosaic and x y line charts.
 * 
 * @author keshav
 * @version $Id$
 * @spring.bean name="expressionDataMatrixVisualizationService"
 */
public class ExpressionDataMatrixVisualizationService {

    private Log log = LogFactory.getLog( this.getClass() );

    private static final int NUM_PROFILES_TO_DISPLAY = 3;

    /**
     * Generates a color mosaic (also known as a heat map).
     * 
     * @param title
     * @param expressionDataMatrix
     * @return JMatrixDisplay
     */
    public JMatrixDisplay createHeatMap( String title, ExpressionDataMatrix expressionDataMatrix ) {

        if ( expressionDataMatrix == null )
            throw new RuntimeException( "Cannot create color matrix due to null ExpressionDataMatrix" );

        ColorMatrix colorMatrix = createColorMatrix( expressionDataMatrix );

        JMatrixDisplay display = new JMatrixDisplay( colorMatrix );

        display.setCellSize( new Dimension( 10, 10 ) );

        return display;
    }

    /**
     * Generates an x y line chart (also known as a profile).
     * 
     * @param title
     * @param dataCol
     * @param numProfiles
     * @return JFreeChart
     */
    public JFreeChart createXYLineChart( String title, Collection<double[]> dataCol, int numProfiles ) {
        if ( dataCol == null ) throw new RuntimeException( "dataCol cannot be " + null );

        if ( dataCol.size() < numProfiles ) {
            log.info( "Collection smaller than number of elements.  Will display first " + NUM_PROFILES_TO_DISPLAY
                    + " profiles." );
            numProfiles = NUM_PROFILES_TO_DISPLAY;
        }

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        Iterator iter = dataCol.iterator();
        for ( int j = 0; j < numProfiles; j++ ) {
            double[] data = ( double[] ) iter.next();
            XYSeries series = new XYSeries( j, true, true );
            for ( int i = 0; i < data.length; i++ ) {
                series.add( i, data[i] );
            }
            xySeriesCollection.addSeries( series );
        }

        JFreeChart chart = ChartFactory.createXYLineChart( title, "Microarray", "Expression Value", xySeriesCollection,
                PlotOrientation.VERTICAL, false, false, false );
        chart.addSubtitle( new TextTitle( "(Raw data values)", new Font( "SansSerif", Font.BOLD, 14 ) ) );

        return chart;
    }

    /**
     * @param expressionDataMatrix
     * @return ColorMatrix
     */
    @SuppressWarnings("unchecked")
    private ColorMatrix createColorMatrix( ExpressionDataMatrix expressionDataMatrix ) {

        Collection<DesignElement> rowElements = expressionDataMatrix.getRowElements();

        Collection<BioMaterial> colElements = new LinkedHashSet<BioMaterial>();

        if ( expressionDataMatrix == null || rowElements.size() == 0 ) {
            throw new IllegalArgumentException( "ExpressionDataMatrix apparently has no data" );
        }

        double[][] data = new double[rowElements.size()][];
        int i = 0;
        for ( DesignElement designElement : rowElements ) {
            Double[] row = ( Double[] ) expressionDataMatrix.getRow( designElement );
            data[i] = ArrayUtils.toPrimitive( row );
            i++;
        }

        for ( int j = 0; j < data[0].length; j++ ) {
            BioMaterial bm = expressionDataMatrix.getBioMaterialForColumn( j );

            colElements.add( bm );

        }

        return createColorMatrix( data, rowElements, colElements );
    }

    /**
     * @param data
     * @param rowElements
     * @param colElements
     * @return ColorMatrix
     */
    private ColorMatrix createColorMatrix( double[][] data, Collection<DesignElement> rowElements,
            Collection<BioMaterial> colElements ) {

        assert rowElements != null && colElements != null : "Labels cannot be set";

        List<String> rowLabels = new ArrayList<String>();

        List<String> colLabels = new ArrayList<String>();

        for ( DesignElement de : rowElements ) {
            rowLabels.add( de.getName() );
        }

        for ( BioMaterial bm : colElements ) {
            colLabels.add( bm.getName() );
        }

        DoubleMatrixNamed matrix = new DenseDoubleMatrix2DNamed( data );

        matrix.setRowNames( rowLabels );

        matrix.setColumnNames( colLabels );

        ColorMatrix colorMatrix = new ColorMatrix( matrix );

        return colorMatrix;
    }
}
