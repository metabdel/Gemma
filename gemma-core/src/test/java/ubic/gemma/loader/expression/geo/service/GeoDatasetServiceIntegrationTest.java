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
package ubic.gemma.loader.expression.geo.service;

import java.util.Collection;
import java.util.HashSet;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.gemma.analysis.preprocess.ExpressionDataMatrixBuilder;
import ubic.gemma.datastructure.matrix.ExpressionDataMatrix;
import ubic.gemma.loader.expression.geo.GeoDomainObjectGenerator;
import ubic.gemma.loader.expression.geo.GeoDomainObjectGeneratorLocal;
import ubic.gemma.loader.util.AlreadyExistsInSystemException;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignDao;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignService;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.testing.AbstractGeoServiceTest;
import ubic.gemma.util.ConfigUtils;

/**
 * This is an integration test.
 * <p>
 * We don't recommend running this frequently, as it is slow. It can also fail due to database-state issues having
 * nothing to do with the test itself.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class GeoDatasetServiceIntegrationTest extends AbstractGeoServiceTest {
    ExpressionExperimentService eeService;
    ExpressionExperiment ee;
    ArrayDesignService adService;
    Collection<ArrayDesign> ads;
    protected AbstractGeoService geoService;

    @SuppressWarnings("unchecked")
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        ads = new HashSet<ArrayDesign>();

        eeService = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        adService = ( ArrayDesignService ) this.getBean( "arrayDesignService" );

        init();
        endTransaction();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        super.onTearDown();
        try {
            if ( ee != null ) {
                eeService.delete( ee );
            }
        } catch ( Exception e ) {
            log.error( "Error while deleting test data: " + ee, e );
        }

        ArrayDesign ad = adService.findByShortName( "GPL97" );
        if ( ad != null ) adService.remove( ad );

        ad = adService.findByShortName( "GPL96" );
        if ( ad != null ) adService.remove( ad );

        ad = adService.findByShortName( "GPL91" );
        if ( ad != null ) adService.remove( ad );

        ad = adService.findByShortName( "GPL875" );
        if ( ad != null ) adService.remove( ad );

    }

    // ////////////////////////////////////////////////////////////
    // Full tests - only run if you have lots of time to kill
    // ////////////////////////////////////////////////////////////

    // /**
    // * This test uses just one dataset, one series (only about 900 probes), just the platform.
    // */
    // public void testFetchAndLoadOneSeriesPlatform() throws Exception {
    // geoService.setLoadPlatformOnly( true );
    // geoService.fetchAndLoad( "GSE2" );
    // }
    //
    // /**
    // * This test uses just one dataset, one series (only about 900 probes)
    // */
    // public void testFetchAndLoadOneSeries() throws Exception {
    // geoService.fetchAndLoad( "GSE2" );
    // }

    // /**
    // * One platform but many quantitation types.
    // */
    // public void testFetchAndLoadB() throws Exception {
    // geoService.fetchAndLoad( "GDS942" );
    // }
    //
    // /**
    // * two platforms.
    // */
    // public void testFetchAndLoadC() throws Exception {
    // geoService.fetchAndLoad( "GDS100" );
    // }
    //
    // public void testFetchAndLoadD() throws Exception {
    // geoService.fetchAndLoad( "GDS1033" );
    // }
    //

    /*
     * Please leave this here, we use it to load data sets for chopping.
     */
    // @SuppressWarnings("unchecked")
    // public void testFetchASeries() throws Exception {
    // endTransaction();
    // geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGenerator() );
    // geoService.fetchAndLoad( "GSE3500", false, false, false );
    // }
    /**
     * Has multiple species (mouse and human, one and two platforms respectively), also test publication entry.
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGSE1133() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "gse1133Short" ) );
        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GSE1133", false, true, false );
        ee = results.iterator().next(); // fixme, need to delete both.
        eeService.thawLite( ee );
        assertNotNull( ee.getPrimaryPublication() );
        assertEquals( "6062-7", ee.getPrimaryPublication().getPages() );
        assertEquals( 2, results.size() );

    }

    /**
     * GSE3434 has no dataset. It's small so okay to download.
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadSeriesOnly() throws Exception {
        try {
            geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGenerator() );
            Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                    "GSE3434", false, true, false );
            ee = results.iterator().next();
        } catch ( AlreadyExistsInSystemException e ) {
            ee = ( ExpressionExperiment ) e.getData();
        }
        eeService.thawLite( ee );
        assertNotNull( ee );
        assertNotNull( ee.getBioAssays() );
        assertEquals( 4, ee.getBioAssays().size() );
        assertEquals( 526, ee.getDesignElementDataVectors().size() ); // 3 quantitation types 532?
        //
        // int actualValue = ( ( ArrayDesignDao ) this.getBean( "arrayDesignDao" ) ).numCompositeSequences( ad.getId()
        // );
        // assertEquals( 532, actualValue );
    }

    // ////////////////////////////////////////////////////////////
    // Small tests, should run reasonably quickly.
    // ////////////////////////////////////////////////////////////
    /**
     * Original reason for test: yields audit trail errors.
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGDS775() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "gds775short" ) );
        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GDS775", false, true, false );

        ee = results.iterator().next();
        eeService.thawLite( ee );
        assertEquals( 4, ee.getBioAssays().size() );
        assertEquals( 300, ee.getDesignElementDataVectors().size() ); // 3 quantitation types
        //
        // int actualValue = ( ( ArrayDesignDao ) this.getBean( "arrayDesignDao" ) ).numCompositeSequences( ad.getId()
        // );
        // assertEquals( 107, actualValue );
    }

    /**
     * problem case GDS22 - has lots of missing values and number format issues.
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGDS22() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "gds22Short" ) );

        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GDS22", false, true, false );

        ee = results.iterator().next();
        eeService.thawLite( ee );
        assertEquals( 80, ee.getBioAssays().size() );
        assertEquals( 400, ee.getDesignElementDataVectors().size() );
        ArrayDesign ad = ee.getBioAssays().iterator().next().getArrayDesignUsed();
        ads.add( ad );
        int actualValue = ( ( ArrayDesignDao ) this.getBean( "arrayDesignDao" ) ).numCompositeSequences( ad.getId() );
        assertEquals( 10, actualValue );
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGSE60() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "gse60Short" ) );

        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GSE60", false, true, false );
        ee = results.iterator().next();
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGSE3500() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "gse3500Short" ) );

        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GSE3500", false, false, true );
        ee = results.iterator().next();
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGDS994() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "gds994Short" ) );
        try {
            Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                    "GDS994", false, true, false );
            ee = results.iterator().next();
            assertEquals( 12, ee.getBioAssays().size() );
            assertEquals( 300, ee.getDesignElementDataVectors().size() ); // 3 quantitation types
            ArrayDesign ad = ee.getBioAssays().iterator().next().getArrayDesignUsed();
            ads.add( ad );
            int actualValue = ( ( ArrayDesignDao ) this.getBean( "arrayDesignDao" ) )
                    .numCompositeSequences( ad.getId() );
            assertEquals( 100, actualValue );
        } catch ( AlreadyExistsInSystemException e ) {
            log.warn( "Skipping test, data already exists in system" );
        }

    }

    @SuppressWarnings("unchecked")
    public void testFetchAndLoadMultiChipPerSeriesShort() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "shortTest" ) );

        /*
         * HG-U133A. GDS473 is for the other chip (B). Series is GSE674. see
         * http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gds&term=GSE674[Accession]&cmd=search
         */
        ExpressionExperiment newee;
        try {
            Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                    "GDS472", false, true, false );
            newee = results.iterator().next();

        } catch ( AlreadyExistsInSystemException e ) {
            newee = ( ExpressionExperiment ) e.getData();
        }
        assertNotNull( newee );
        ExpressionExperimentService expressionExperimentService = ( ExpressionExperimentService ) this
                .getBean( "expressionExperimentService" );
        expressionExperimentService.thaw( newee );

        // get the data back out.
        ExpressionExperimentService ees = ( ExpressionExperimentService ) getBean( "expressionExperimentService" );

        ee = ees.findByName( "Normal Muscle - Female , Effect of Age" );

        /*
         * Test for bug 468 (merging of subsets across GDS's)
         */
        ExperimentalFactor factor = newee.getExperimentalDesign().getExperimentalFactors().iterator().next();
        assertEquals( 2, factor.getFactorValues().size() ); // otherwise get 4.
        // for ( FactorValue s : factor.getFactorValues() ) {
        // if ( s.getValue().equals( "20-29 years" ) ) assertEquals( 14, s..size() );
        // }

        // Collection<QuantitationType> qTypes = expressionExperimentService.getQuantitationTypes( ee );

        ExpressionDataMatrixBuilder builder = new ExpressionDataMatrixBuilder( newee.getDesignElementDataVectors() );

        // QuantitationType qt = null;
        // for ( QuantitationType c : qTypes ) {
        // if ( c.getIsPreferred() && c.getScale().equals( ScaleType.LINEAR ) ) {
        // qt = c;
        // break;
        // }
        // }
        //
        // assertNotNull( qt );
        // assertNotNull( ee );
        // assertEquals( newee, ee );
        //
        // ExpressionDataMatrix matrix = new ExpressionDataDoubleMatrix( newee, qt );

        ExpressionDataMatrix matrix = builder.getPreferredData();

        assertTrue( matrix != null );
        // if ( log.isDebugEnabled() ) {
        // log.info( matrix.toString() );
        // }

        assertTrue( matrix != null );

        assertEquals( 31, matrix.rows() );

        assertEquals( 15, matrix.columns() );

        // GSM10363 = D1-U133B
        testMatrixValue( newee, matrix, "200000_s_at", "GSM10363", 5722.0 );

        // GSM10380 = C7-U133A
        testMatrixValue( newee, matrix, "1007_s_at", "GSM10380", 1272.0 );
    }

    @SuppressWarnings("unchecked")
    public void testLoadDeleteLoadGSE3434() throws Exception {
        String path = getTestFileBasePath();
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path + GEO_TEST_DATA_ROOT
                + "GSE3434" ) );
        try {
            Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                    "GSE3434", false, true, false );
            ee = results.iterator().next();
        } catch ( AlreadyExistsInSystemException e ) {
            ee = ( ExpressionExperiment ) e.getData();
        }

        assertNotNull( ee );
        eeService.delete( ee );
        ee = null;

        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GSE3434", false, true, false );
        ee = results.iterator().next();

        // this has no fail condition, just check that we can delete...
    }

    /**
     * @param ee
     * @param matrix
     * @param probeToTest
     * @param sampleToTest
     * @param expectedValue
     */
    private void testMatrixValue( ExpressionExperiment ee, ExpressionDataMatrix matrix, String probeToTest,
            String sampleToTest, double expectedValue ) {

        DesignElement soughtDesignElement = null;
        BioAssay soughtBioAssay = null;
        Collection<DesignElementDataVector> vectors = ee.getDesignElementDataVectors();
        for ( DesignElementDataVector vector : vectors ) {
            DesignElement de = vector.getDesignElement();
            if ( de.getName().equals( probeToTest ) ) {
                soughtDesignElement = de;
            }

            BioAssayDimension bad = vector.getBioAssayDimension();
            for ( BioAssay ba : bad.getBioAssays() ) {
                if ( ba.getAccession().getAccession().equals( sampleToTest ) ) {
                    soughtBioAssay = ba;
                }
            }

        }
        if ( soughtDesignElement == null || soughtBioAssay == null ) fail( "didn't find values for " + sampleToTest );

        Double actualValue = ( Double ) matrix.get( soughtDesignElement, soughtBioAssay );
        assertNotNull( "No value for " + soughtBioAssay, actualValue );
        assertEquals( expectedValue, actualValue, 0.00001 );

    }

    /**
     * This is a important and not uncommon case: when a sample is in more then one Series, we have to make sure we
     * don't input it more than once.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGSE3193() throws Exception {
        String path = ConfigUtils.getString( "gemma.home" );

        // First load the data set that has overlapping samples with GSE3193, GSE61.
        try {

            assert path != null;
            geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path
                    + AbstractGeoServiceTest.GEO_TEST_DATA_ROOT + "GSE61Short" ) );
            geoService.fetchAndLoad( "GSE61", false, true, false );
        } catch ( AlreadyExistsInSystemException e ) {
            // ok
        }

        // it is important for this test that GSE3193 not already be in the database.
        ExpressionExperiment eeold = this.eeService.findByShortName( "GSE3193" );
        if ( eeold != null ) {
            eeService.delete( eeold );
        }

        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path
                + AbstractGeoServiceTest.GEO_TEST_DATA_ROOT + "GSE3193Short" ) );
        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GSE3193", false, true, false );
        ee = results.iterator().next();

        assertNotNull( ee );

        // FIXME: check that we haven't loaded samples twice.
    }

    /**
     * Left out quantitation types due to bug in how quantitation types were cached during persisting, if the QTs didn't
     * have descriptions.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testFetchAndLoadGSE5949() throws Exception {
        String path = ConfigUtils.getString( "gemma.home" );
        geoService.setGeoDomainObjectGenerator( new GeoDomainObjectGeneratorLocal( path
                + AbstractGeoServiceTest.GEO_TEST_DATA_ROOT + "GSE5949short" ) );
        Collection<ExpressionExperiment> results = ( Collection<ExpressionExperiment> ) geoService.fetchAndLoad(
                "GSE5949", false, true, false );
        ee = results.iterator().next();
        eeService.thawLite( ee );
        Collection qts = eeService.getQuantitationTypes( ee );
        assertEquals( 3, qts.size() );
    }

    /**
     * @param matrix
     */
    @SuppressWarnings( { "unchecked", "unused" })
    private void printMatrix( DoubleMatrixNamed matrix ) {
        StringBuilder buf = new StringBuilder();
        buf.append( "probe" );
        for ( Object columnName : ( Collection<Object> ) matrix.getColNames() ) {
            buf.append( "\t" + columnName );
        }
        buf.append( "\n" );
        for ( Object rowName : ( Collection<Object> ) matrix.getRowNames() ) {
            buf.append( rowName );
            double[] array = matrix.getRowByName( rowName );
            for ( int i = 0; i < array.length; i++ ) {
                double array_element = array[i];
                buf.append( "\t" + array_element );
            }
            buf.append( "\n" );
        }
        log.debug( buf.toString() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.expression.geo.service.AbstractGeoServiceTest#init()
     */
    @Override
    protected void init() {
        geoService = ( AbstractGeoService ) this.getBean( "geoDatasetService" );
    }

}
