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
package ubic.gemma.loader.expression.geo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.util.CancellationException;
import ubic.basecode.util.FileTools;
import ubic.basecode.util.StringUtil;
import ubic.gemma.loader.expression.geo.model.GeoChannel;
import ubic.gemma.loader.expression.geo.model.GeoContact;
import ubic.gemma.loader.expression.geo.model.GeoData;
import ubic.gemma.loader.expression.geo.model.GeoDataset;
import ubic.gemma.loader.expression.geo.model.GeoPlatform;
import ubic.gemma.loader.expression.geo.model.GeoReplication;
import ubic.gemma.loader.expression.geo.model.GeoSample;
import ubic.gemma.loader.expression.geo.model.GeoSeries;
import ubic.gemma.loader.expression.geo.model.GeoSubset;
import ubic.gemma.loader.expression.geo.model.GeoVariable;
import ubic.gemma.loader.util.parser.Parser;

/**
 * Class for parsing GSE and GDS files from NCBI GEO. See
 * {@link http://www.ncbi.nlm.nih.gov/projects/geo/info/soft2.html} for format information.
 * 
 * @author keshav
 * @author pavlidis
 * @version $Id$
 */
public class GeoFamilyParser implements Parser {

    /**
     * 
     */
    private static final char FIELD_DELIM = '\t';
    private static Log log = LogFactory.getLog( GeoFamilyParser.class.getName() );
    private String currentDatasetAccession;
    private String currentPlatformAccession;

    private String currentSampleAccession;

    private String currentSeriesAccession;
    private String currentSubsetAccession;
    private int dataSetDataLines = 0;

    private boolean haveReadPlatformHeader = false;
    private boolean haveReadSampleDataHeader = false;

    private boolean inDatabase = false;
    private boolean inDataset = false;
    private boolean inDatasetTable = false;

    private boolean inPlatform = false;

    private boolean inPlatformTable = false;
    private boolean inSample = false;
    private boolean inSampleTable = false;

    private boolean inSeries = false;

    private boolean inSeriesTable = false;

    private boolean inSubset = false;

    private int parsedLines;

    private int platformLines = 0;

    private GeoParseResult results = new GeoParseResult();

    private int sampleDataLines = 0;

    private int seriesDataLines = 0;

    private boolean processPlatformsOnly;

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.loaderutils.Parser#getResults()
     */
    public Collection<Object> getResults() {
        Collection<Object> r = new HashSet<Object>();
        r.add( this.results );
        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.loaderutils.Parser#parse(java.io.File)
     */
    public void parse( File f ) throws IOException {
        this.parse( new FileInputStream( f ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.loaderutils.Parser#parse(java.io.InputStream)
     */
    public void parse( InputStream is ) throws IOException {
        if ( is == null ) {
            throw new IOException( "Inputstream was null" );
        }

        if ( is.available() == 0 ) {
            throw new IOException( "No bytes to read from the input stream." );
        }

        final BufferedReader dis = new BufferedReader( new InputStreamReader( is ) );

        log.debug( "Parsing...." );

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        FutureTask<Boolean> future = new FutureTask<Boolean>( new Callable<Boolean>() {
            @SuppressWarnings("synthetic-access")
            public Boolean call() throws Exception {
                try {
                    return doParse( dis );
                } catch ( Exception e ) {
                    log.error( e, e );
                    return Boolean.FALSE;
                }

            }
        } );

        executor.execute( future );
        executor.shutdown();

        while ( !future.isDone() ) {
            try {
                TimeUnit.SECONDS.sleep( 1L );
            } catch ( InterruptedException e ) {
                throw new RuntimeException( e );
            }
            log.info( parsedLines + " lines parsed." );
        }

        try {
            Boolean ok = future.get();
            if ( !ok ) {
                throw new RuntimeException( "Something bad happened during parsing." );
            }
        } catch ( ExecutionException e ) {
            throw new RuntimeException( "Parse failed", e.getCause() );
        } catch ( CancellationException e ) {
            throw new RuntimeException( "Parse was cancelled", e.getCause() );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Parse was interrupted", e.getCause() );
        }

        executor.shutdownNow();

        assert future.isDone();
        assert executor.isTerminated();

        log.info( "Done parsing." );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.loaderutils.Parser#parse(java.lang.String)
     */
    public void parse( String fileName ) throws IOException {
        InputStream is = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        parse( is );
    }

    /**
     * @param b
     */
    public void setProcessPlatformsOnly( boolean b ) {
        this.processPlatformsOnly = b;
    }

    /**
     * Add a new sample to the results.
     * 
     * @param sampleAccession
     */
    private void addNewSample( String sampleAccession ) {
        if ( log.isDebugEnabled() ) log.debug( "Adding new sample " + sampleAccession );
        GeoSample newSample = new GeoSample();
        newSample.setGeoAccession( sampleAccession );
        results.getSampleMap().put( sampleAccession, newSample );
    }

    /**
     * @param value
     */
    private void addSeriesSample( String value ) {
        if ( !results.getSampleMap().containsKey( value ) ) {
            log.debug( "New sample (for series): " + value );
            addNewSample( value );
        }
        log.debug( "Adding sample: " + value + " to series " + currentSeriesAccession );
        results.getSeriesMap().get( currentSeriesAccession ).addSample( results.getSampleMap().get( value ) );
    }

    /**
     * @param target
     * @param property
     * @param value
     */
    private void addTo( Object target, String property, Object value ) {
        try {
            Method adder = target.getClass().getMethod( "addTo" + StringUtil.upperCaseFirstLetter( property ),
                    new Class[] { value.getClass() } );
            adder.invoke( target, new Object[] { value } );
        } catch ( SecurityException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param contact
     * @param property
     * @param value
     */
    private void contactSet( GeoContact contact, String property, Object value ) {
        if ( contact == null ) throw new IllegalArgumentException();
        try {
            BeanUtils.setProperty( contact, property, value );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param object
     * @param property
     * @param value
     */
    private void contactSet( Object object, String property, Object value ) {
        if ( object instanceof GeoContact ) {
            contactSet( ( GeoContact ) object, property, value );
        } else if ( object instanceof GeoData ) {
            GeoContact contact = ( ( GeoData ) object ).getContact();
            contactSet( contact, property, value );
        }
    }

    private GeoDataset currentDataset() {
        return this.results.getDatasetMap().get( currentDatasetAccession );
    }

    private GeoPlatform currentPlatform() {
        return this.results.getPlatformMap().get( currentPlatformAccession );
    }

    private GeoSample currentSample() {
        return this.results.getSampleMap().get( currentSampleAccession );
    }

    private GeoSeries currentSeries() {
        return this.results.getSeriesMap().get( currentSeriesAccession );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void datasetSet( String accession, String property, Object value ) {
        GeoDataset dataset = results.getDatasetMap().get( accession );
        if ( dataset == null ) throw new IllegalArgumentException( "Unknown dataset " + accession );

        if ( property.equals( "experimentType" ) ) {
            value = GeoDataset.convertStringToExperimentType( ( String ) value );
        } else if ( property.equals( "platformType" ) ) {
            value = GeoDataset.convertStringToPlatformType( ( String ) value );
        } else if ( property.equals( "sampleType" ) ) {
            value = GeoDataset.convertStringToSampleType( ( String ) value );
        } else if ( property.equals( "valueType" ) ) {
            value = GeoDataset.convertStringToValueType( ( String ) value );
        }

        try {
            BeanUtils.setProperty( dataset, property, value );
        } catch ( IllegalAccessException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param dis
     * @throws IOException
     */
    private Boolean doParse( BufferedReader dis ) {
        haveReadPlatformHeader = false;
        haveReadSampleDataHeader = false;
        String line = "";
        parsedLines = 0;

        try {

            while ( ( line = dis.readLine() ) != null ) {
                if ( StringUtils.isBlank( line ) ) {
                    log.error( "Empty or null line" );
                    continue;
                }
                parseLine( line );
                parsedLines++;
            }
        } catch ( Exception e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }

        log.debug( "Parsed " + parsedLines + " lines." );
        log.debug( this.platformLines + " platform  lines" );
        log.debug( this.seriesDataLines + " series data lines" );
        log.debug( this.dataSetDataLines + " data set data lines" );
        log.debug( this.sampleDataLines + " sample data lines" );
        return Boolean.TRUE;
    }

    /**
     * @param line
     * @return
     */
    private int extractChannelNumber( String line ) {
        int chIndex = line.lastIndexOf( "_ch" );
        if ( chIndex < 0 ) return 1; // that's okay, there is only one channel.
        String candidateInt = line.substring( chIndex + 3, chIndex + 4 );
        try {
            return Integer.parseInt( candidateInt );
        } catch ( NumberFormatException e ) {
            return 1;
        }
    }

    /**
     * Turns a line in the format #key = value into a column name and description. This is used to handle lines such as
     * (in a platform section of a GSE file):
     * 
     * <pre>
     *                                                    #SEQ_LEN = Sequence length
     * </pre>
     * 
     * @param line
     * @param dataToAddTo GeoData object, must not be null.
     */
    private void extractColumnIdentifier( String line, GeoData dataToAddTo ) {
        if ( dataToAddTo == null ) throw new IllegalArgumentException( "Data cannot be null" );
        Map<String, String> res = extractKeyValue( line );
        String key = res.keySet().iterator().next();
        dataToAddTo.getColumnNames().add( key );
        dataToAddTo.getColumnDescriptions().add( res.get( key ) );
    }

    /**
     * Extract a key and value pair from a line in the format #key = value.
     * 
     * @param line.
     * @return Map containing the String key and String value.
     * @throws Exception if the line doesn't contain a =.
     */
    private Map<String, String> extractKeyValue( String line ) {
        if ( !line.startsWith( "#" ) ) throw new IllegalArgumentException( "Wrong type of line" );
        Map<String, String> result = new HashMap<String, String>();
        String fixed = line.substring( line.indexOf( '#' ) + 1 );

        String[] tokens = fixed.split( "=", 2 );
        if ( tokens.length != 2 ) {
            throw new IllegalArgumentException( "Wrong type of line: " + line );
        }
        String key = tokens[0];
        String value = tokens[1];
        key = StringUtils.strip( key );
        value = StringUtils.strip( value );
        result.put( key, value );
        return result;
    }

    /**
     * Extract a value from a line in the format xxxx=value.
     * 
     * @param line
     * @return String following the first occurrence of '=', or null if there is no '=' in the String.
     */
    private String extractValue( String line ) {
        int eqIndex = line.indexOf( '=' );
        if ( eqIndex < 0 ) {
            return null; // that's okay, there are lines that just indicate the end of sections.
        }

        return StringUtils.strip( line.substring( eqIndex + 1 ) );
    }

    /**
     * Parse a line to extract an integer <em>n</em> from the a variable description line like "!Series_variable_[n] =
     * age"
     * 
     * @param line
     * @return int
     * @throws Exception if the line doesn't fit the format.
     */
    private int extractVariableNumber( String line ) {
        Pattern p = Pattern.compile( "_(\\d+)$" );
        Matcher m = p.matcher( line );
        if ( m.matches() ) {
            try {
                return Integer.parseInt( line.substring( m.start( 1 ) ) );
            } catch ( NumberFormatException e ) {
                throw new IllegalArgumentException( "Wrong kind of string: " + line );
            }
        }
        throw new IllegalArgumentException( "Wrong kind of string: " + line );
    }

    /**
     * Parse the column identifier strings from a GDS or GSE file.
     * <p>
     * In GSE files, in a 'platform' section, these become column descriptions for the platform descriptors.
     * <p>
     * For samples in GSE files, they become values for the data in the sample. For example
     * 
     * <pre>
     *                                                                                            #ID_REF = probe id
     *                                                                                            #VALUE = RMA value
     * </pre>
     * 
     * <p>
     * For subsets, these lines are ignored (do they even occur?). In 'series' sections of GSE files, the data are kept
     * (but does this occur?) FIXME.
     * <p>
     * In GDS files, if we are in a 'dataset' section, these become "titles" for the samples if they aren't already
     * provided. Here is an example.
     * 
     * <pre>
     *                                                                                            #GSM549 = Value for GSM549: lexA vs. wt, before UV treatment, MG1655; src: 0' wt, before UV treatment, 25 ug total RNA, 2 ug pdN6&lt;-&gt;0' lexA, before UV 25 ug total RNA, 2 ug pdN6
     *                                                                                            #GSM542 = Value for GSM542: lexA 20' after NOuv vs. 0', MG1655; src: 0', before UV treatment, 25 ug total RNA, 2 ug pdN6&lt;-&gt;lexA 20 min after NOuv, 25 ug total RNA, 2 ug pdN6
     *                                                                                            #GSM543 = Value for GSM543: lexA 60' after NOuv vs. 0', MG1655; src: 0', before UV treatment, 25 ug total RNA, 2 ug pdN6&lt;-&gt;lexA 60 min after NOuv, 25 ug total RNA, 2 ug pdN6
     * </pre>
     * 
     * @param line
     */
    private void parseColumnIdentifier( String line ) {
        if ( inPlatform ) {
            extractColumnIdentifier( line, currentPlatform() );
        } else if ( inSample ) {
            if ( !processPlatformsOnly ) extractColumnIdentifier( line, currentSample() );
        } else if ( inSeries ) {
            if ( !processPlatformsOnly ) extractColumnIdentifier( line, currentSeries() );
        } else if ( inSubset ) {
            // nothing.
        } else if ( inDataset ) {
            if ( processPlatformsOnly ) return;

            extractColumnIdentifier( line, currentDataset() );
            Map<String, String> res = extractKeyValue( line );
            String potentialSampleAccession = res.keySet().iterator().next();
            String potentialTitle = res.get( potentialSampleAccession );

            // First add the sample if we haven't seen it before.
            if ( potentialSampleAccession.startsWith( "GSM" )
                    && !results.getSampleMap().containsKey( potentialSampleAccession ) ) {
                this.addNewSample( potentialSampleAccession );
            }

            // Set the title, if it hasn't been set before.
            if ( potentialSampleAccession.startsWith( "GSM" ) && !StringUtils.isBlank( potentialTitle )
                    && StringUtils.isBlank( results.getSampleMap().get( potentialSampleAccession ).getTitle() ) ) {
                potentialTitle = potentialTitle.substring( potentialTitle.indexOf( ':' ) + 2 ); // throw out the "Value
                // for GSM1949024:"
                // part.
                log.debug( potentialSampleAccession + " " + potentialTitle );
                sampleSet( potentialSampleAccession, "title", potentialTitle );
            }

        } else {
            throw new IllegalStateException( "Wrong state to deal with '" + line + "'" );
        }
    }

    /**
     * Parse a line in a 'dataset' section of a GDS file. This is metadata about the experiment.
     * 
     * @param line
     * @param value
     */
    private void parseDatasetLine( String line, String value ) {
        if ( this.processPlatformsOnly ) return;
        /***************************************************************************************************************
         * DATASET
         **************************************************************************************************************/
        if ( startsWithIgnoreCase( line, "!Dataset_title" ) ) {
            datasetSet( currentDatasetAccession, "title", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_description" ) ) {
            datasetSet( currentDatasetAccession, "title", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_maximum_probes" ) ) {
            datasetSet( currentDatasetAccession, "numProbes", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_order" ) ) {
            datasetSet( currentDatasetAccession, "order", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_organism" ) ) { // note, no longer used?
            datasetSet( currentDatasetAccession, "organism", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_platform_organism" ) ) { // redundant, we get this from the
            // series
            // results.getPlatformMap().get( currentDatasetPlatformAccession ).addToOrganisms( value );
        } else if ( startsWithIgnoreCase( line, "!dataset_platform_technology_type" ) ) {
            // results.getPlatformMap().get( currentDatasetPlatformAccession ).setTechnology( value ); // we also get
            // this
            // // from the platform
            // // directly.
        } else if ( startsWithIgnoreCase( line, "!dataset_platform =" ) ) {
            if ( !results.getPlatformMap().containsKey( value ) ) {
                results.getPlatformMap().put( value, new GeoPlatform() );
                results.getPlatformMap().get( value ).setGeoAccession( value );
            }
            results.getDatasetMap().get( currentDatasetAccession ).setPlatform( results.getPlatformMap().get( value ) );
            // currentDatasetPlatformAccession = value;
        } else if ( startsWithIgnoreCase( line, "!dataset_probe_type" ) ) { // obsolete
            datasetSet( currentDatasetAccession, "platformType", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_platform_technology_type" ) ) {
            datasetSet( currentDatasetAccession, "platformType", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_reference_series" ) ) {
            if ( !results.getSeriesMap().containsKey( value ) ) {
                log.debug( "Adding series " + value );
                results.getSeriesMap().put( value, new GeoSeries() );
                results.getSeriesMap().get( value ).setGeoAccession( value );
            }

            // FIXME this is really a bug: the same series comes up more than once, but empty in some case.
            GeoSeries series = results.getSeriesMap().get( value );
            if ( !results.getDatasetMap().get( currentDatasetAccession ).getSeries().contains( series ) ) {
                log.warn( currentDatasetAccession + " already has reference to series " + value );
            }

            if ( series.getSamples() != null && series.getSamples().size() > 0 ) {
                results.getDatasetMap().get( currentDatasetAccession ).addSeries( series );
            } else {
                log.warn( "Empty series " + series );
            }

        } else if ( startsWithIgnoreCase( line, "!dataset_total_samples" ) ) {
            datasetSet( currentDatasetAccession, "numSamples", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_sample_count" ) ) { // is this the same as "total_samples"?
            datasetSet( currentDatasetAccession, "numSamples", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_update_date" ) ) {
            datasetSet( currentDatasetAccession, "updateDate", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_value_type" ) ) {
            datasetSet( currentDatasetAccession, "valueType", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_completeness" ) ) {
            datasetSet( currentDatasetAccession, "completeness", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_experiment_type" ) ) {
            datasetSet( currentDatasetAccession, "experimentType", value ); // this is now "platform type"? in new GEO
            // files?
        } else if ( startsWithIgnoreCase( line, "!dataset_type" ) ) {
            datasetSet( currentDatasetAccession, "datasetType", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_feature_count" ) ) {
            datasetSet( currentDatasetAccession, "featureCount", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_sample_organism" ) ) {
            datasetSet( currentDatasetAccession, "organism", value ); // note, redundant with 'organism'.
        } else if ( startsWithIgnoreCase( line, "!dataset_sample_type" ) ) {
            datasetSet( currentDatasetAccession, "sampleType", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_pubmed_id" ) ) {
            datasetSet( currentDatasetAccession, "pubmedId", value );
        } else if ( startsWithIgnoreCase( line, "!dataset_table_begin" ) ) {
            this.inDatasetTable = true;
            // haveReadDatasetDataHeader = false;
        } else if ( startsWithIgnoreCase( line, "!dataset_table_end" ) ) {
            this.inDatasetTable = false;
        } else if ( startsWithIgnoreCase( line, "!dataset_channel_count" ) ) {
            datasetSet( currentDatasetAccession, "channelCount", new Integer( Integer.parseInt( value ) ) );
        } else {
            log.error( "Unknown flag in dataset: " + line );
        }
    }

    /**
     * @param line
     */
    private void parseLine( String line ) {
        if ( StringUtils.isBlank( line ) ) return;
        if ( line.startsWith( "^" ) ) {
            if ( startsWithIgnoreCase( line, "^DATABASE" ) ) {
                inDatabase = true;
                inSubset = false;
                inDataset = false;
                inSample = false;
                inPlatform = false;
                inSeries = false;
            } else if ( startsWithIgnoreCase( line, "^SAMPLE" ) ) {
                inSample = true;
                inSubset = false;
                inDataset = false;
                inDatabase = false;
                inPlatform = false;
                inSeries = false;
                if ( this.processPlatformsOnly ) return;
                String value = extractValue( line );
                currentSampleAccession = value;
                if ( results.getSampleMap().containsKey( value ) ) return;
                addNewSample( value );
                log.debug( "Starting new sample " + value );
            } else if ( startsWithIgnoreCase( line, "^PLATFORM" ) ) {
                inPlatform = true;
                inSubset = false;
                inDataset = false;
                inDatabase = false;
                inSample = false;
                inSeries = false;
                String value = extractValue( line );
                currentPlatformAccession = value;
                if ( results.getPlatformMap().containsKey( value ) ) return;
                GeoPlatform platform = new GeoPlatform();
                platform.setGeoAccession( value );
                results.getPlatformMap().put( value, platform );
                log.info( "Starting platform " + platform );
            } else if ( startsWithIgnoreCase( line, "^SERIES" ) ) {
                inSeries = true;
                inSubset = false;
                inDataset = false;
                inPlatform = false;
                inSample = false;
                inDatabase = false;
                if ( this.processPlatformsOnly ) return;
                String value = extractValue( line );
                currentSeriesAccession = value;
                if ( results.getSeriesMap().containsKey( value ) ) return;
                GeoSeries series = new GeoSeries();
                series.setGeoAccession( value );
                results.getSeriesMap().put( value, series );
                log.debug( "In series " + series );
            } else if ( startsWithIgnoreCase( line, "^DATASET" ) ) {
                inDataset = true;
                inSubset = false;
                inSeries = false;
                inPlatform = false;
                inSample = false;
                inDatabase = false;
                if ( this.processPlatformsOnly ) return;
                String value = extractValue( line );
                currentDatasetAccession = value;
                if ( results.getDatasetMap().containsKey( value ) ) return;
                GeoDataset ds = new GeoDataset();
                ds.setGeoAccession( value );
                results.getDatasetMap().put( value, ds );
                log.debug( "In dataset " + ds );
            } else if ( startsWithIgnoreCase( line, "^SUBSET" ) ) {
                inSubset = true;
                inDataset = false;
                inSeries = false;
                inPlatform = false;
                inSample = false;
                inDatabase = false;
                if ( this.processPlatformsOnly ) return;
                String value = extractValue( line );
                currentSubsetAccession = value;
                if ( results.getSubsetMap().containsKey( value ) ) return;
                GeoSubset ss = new GeoSubset();
                ss.setGeoAccession( value );
                ss.setOwningDataset( results.getDatasetMap().get( this.currentDatasetAccession ) );
                results.getDatasetMap().get( this.currentDatasetAccession ).addSubset( ss );
                results.getSubsetMap().put( value, ss );
                log.debug( "In subset " + ss );
            } else {
                log.error( "Unknown flag in subset: " + line );
            }
        } else {
            parseRegularLine( line );
        }
    }

    /**
     * If a line does not have the same number of fields as the column headings, it is skipped.
     * 
     * @param line
     */
    private void parsePlatformLine( String line ) {

        if ( !haveReadPlatformHeader ) {
            haveReadPlatformHeader = true;
            return;
        }

        String[] tokens = StringUtil.splitPreserveAllTokens( line, FIELD_DELIM );

        int numColumns = results.getPlatformMap().get( currentPlatformAccession ).getColumnNames().size();

        if ( numColumns != tokens.length ) {
            log.error( "Wrong number of tokens in line (" + tokens.length + ", expected " + numColumns
                    + "), line was '" + line + "'; Possible corrupt file or invalid format?" );
            return;
        }

        GeoPlatform platform = results.getPlatformMap().get( currentPlatformAccession );

        for ( int i = 0; i < tokens.length; i++ ) {
            String token = tokens[i];
            String columnName = results.getPlatformMap().get( currentPlatformAccession ).getColumnNames().get( i );
            platform.addToColumnData( columnName, token );
        }
        platformLines++;
    }

    /**
     * Parse a line in a 'platform' section of a GSE file. This deals with meta-data about the platform.
     * 
     * @param line
     * @param value
     */
    private void parsePlatformLine( String line, String value ) {
        /***************************************************************************************************************
         * PLATFORM
         **************************************************************************************************************/
        if ( startsWithIgnoreCase( line, "!Platform_title" ) ) {
            platformSet( currentPlatformAccession, "title", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_geo_accession" ) ) {
            currentPlatformAccession = value;
        } else if ( startsWithIgnoreCase( line, "!Platform_status" ) ) {
            platformSet( currentPlatformAccession, "status", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_manufacturer" ) ) {
            platformSet( currentPlatformAccession, "manufacturer", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_manufacture_protocol" ) ) {
            platformSet( currentPlatformAccession, "manufactureProtocol", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_submission_date" ) ) {
            platformSet( currentPlatformAccession, "submissionDate", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_technology" ) ) {
            platformSet( currentPlatformAccession, "technology", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_distribution" ) ) {
            platformSet( currentPlatformAccession, "distribution", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_organism" ) ) {
            platformAddTo( currentPlatformAccession, "organisms", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_description" ) ) {
            platformAddTo( currentPlatformAccession, "description", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_name" ) ) {
            platformContactSet( currentPlatformAccession, "name", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_email" ) ) {
            platformContactSet( currentPlatformAccession, "email", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_institute" ) ) {
            platformContactSet( currentPlatformAccession, "institute", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_laboratory" ) ) {
            platformContactSet( currentPlatformAccession, "laboratory", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_department" ) ) {
            platformContactSet( currentPlatformAccession, "department", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_address" ) ) { // may not be used any more.
            platformContactSet( currentPlatformAccession, "address", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_city" ) ) {
            platformContactSet( currentPlatformAccession, "city", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_zip/postal_code" ) ) {
            platformContactSet( currentPlatformAccession, "postCode", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_state" ) ) {
            platformContactSet( currentPlatformAccession, "state", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_country" ) ) {
            platformContactSet( currentPlatformAccession, "country", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_phone" ) ) {
            platformContactSet( currentPlatformAccession, "phone", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_web_link" ) ) {
            platformContactSet( currentPlatformAccession, "webLink", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_support" ) ) {
            // FIXME, use this (maybe)
        } else if ( startsWithIgnoreCase( line, "!Platform_coating" ) ) {
            // FIXME use this (maybe)
        } else if ( startsWithIgnoreCase( line, "!Platform_contact_fax" ) ) {
            platformContactSet( currentSeriesAccession, "fax", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_web_link" ) ) {
            platformSet( currentPlatformAccession, "webLink", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_sample_id" ) ) {
            platformSet( currentPlatformAccession, "id", value );
        } else if ( startsWithIgnoreCase( line, "!Platform_table_begin" ) ) {
            inPlatformTable = true;
            haveReadPlatformHeader = false;
        } else if ( startsWithIgnoreCase( line, "!Platform_table_end" ) ) {
            inPlatformTable = false;
        } else if ( startsWithIgnoreCase( line, "!Platform_contributor" ) ) {
            ;// noop. This is the name of the person who submitted the platform.
        } else if ( startsWithIgnoreCase( line, "!Platform_series_id" ) ) {
            // no-op. This identifies which series were run on this platform. We don't care to get this
            // information this way.
        } else if ( startsWithIgnoreCase( line, "!Platform_data_row_count" ) ) {
            ; // nothing.
        } else if ( startsWithIgnoreCase( line, "!Platform_catalog_number" ) ) {
            // do nothing TODO we might want this.
        } else if ( startsWithIgnoreCase( line, "!Platform_last_update_date" ) ) {
            platformLastUpdateDate( currentPlatformAccession, value );
        } else if ( startsWithIgnoreCase( line, "!Platform_supplementary_file" ) ) {
            platformSupplementaryFileSet( currentPlatformAccession, value );
        } else {
            log.error( "Unknown flag in platform: " + line );
        }
    }

    /**
     * @param accession
     * @param string
     */
    public void sampleTypeSet( String accession, String string ) {
        GeoSample sample = results.getSampleMap().get( accession );
        if ( string.equalsIgnoreCase( "cDNA" ) ) {
            sample.setType( "RNA" );
        } else if ( string.equalsIgnoreCase( "RNA" ) ) {
            sample.setType( "RNA" );
        } else if ( string.equalsIgnoreCase( "genomic" ) ) {
            sample.setType( "genomic" );
        } else if ( string.equalsIgnoreCase( "protein" ) ) {
            sample.setType( "protein" );
        } else if ( string.equalsIgnoreCase( "mixed" ) ) {
            sample.setType( "mixed" );
        } else if ( string.equalsIgnoreCase( "SAGE" ) ) {
            sample.setType( "SAGE" );
        } else if ( string.equalsIgnoreCase( "MPSS" ) ) {
            sample.setType( "MPSS" );
        } else if ( string.equalsIgnoreCase( "SARST" ) ) {
            sample.setType( "protein" );
        } else {
            throw new IllegalArgumentException( "Unknown sample type " + string );
        }
    }

    /**
     * Parse lines in GSE and GDS files. Lines are classified into three types:
     * <ul>
     * <li>Starting with "!". These indicate meta data.
     * <li>Starting with "#". These indicate descriptions of columns in a data table.
     * <li>Starting with anything else, primarily (only?) data tables (expression data or platform probe annotations).
     * </ul>
     * 
     * @param line
     */
    private void parseRegularLine( String line ) {
        if ( line.startsWith( "!" ) ) {
            String value = extractValue( line );
            if ( inSample ) {
                parseSampleLine( line, value );
            } else if ( inSeries ) {
                parseSeriesLine( line, value );
            } else if ( inDatabase ) {
                // we are going to ignore these lines.
            } else if ( inPlatform ) {
                parsePlatformLine( line, value );
            } else if ( inDataset ) {
                inDatasetTable = true;
                parseDatasetLine( line, value );
            } else if ( inSubset ) {
                parseSubsetLine( line, value );
            } else {
                throw new IllegalStateException( "Unknown flag: " + line );
            }
        } else if ( line.startsWith( "#" ) ) {
            parseColumnIdentifier( line );
        } else {
            if ( inPlatformTable ) {
                parsePlatformLine( line );
            } else if ( inSampleTable ) {
                parseSampleDataLine( line );
            } else if ( inSeriesTable ) {
                // we ignore this and use the sample data instead.
                // parseSeriesDataLine( line );
            } else if ( inDatasetTable ) {
                // parseDataSetDataLine( line ); // we ignore this and use the sample data instead.
            } else if ( inSubset ) {
                // do nothing.
            } else {
                // throw new IllegalStateException( "Wrong state to deal with '" + line + "'" );
            }
        }

    }

    /**
     * The data for one sample is all the values for each quantitation type.
     * <p>
     * Important implementation note: In the sample table sections of GSEXXX_family files, the first column is always
     * ID_REF, according to the kind folks at NCBI. If this changes, this code will BREAK.
     * <p>
     * Similarly, the column names between the different samples are not necessarily the same, but we trust that they
     * all refer to the same quantitation types in the same order. That is, the nth column for this sample 'means' the
     * same thing as the nth column for another sample in this series. If that isn't true, this will be BROKEN.
     * 
     * @param line
     */
    private void parseSampleDataLine( String line ) {

        if ( StringUtils.isBlank( line ) ) return;

        if ( !haveReadSampleDataHeader ) {
            haveReadSampleDataHeader = true;
            return;
        }

        String[] tokens = StringUtil.splitPreserveAllTokens( line, FIELD_DELIM );

        assert tokens != null;

        /*
         * This can happen in some files -- we have to ignore it.
         */
        if ( tokens.length <= 1 ) {
            log.error( "Parse error, sample data line has too few elements (" + tokens.length + "), line was '" + line
                    + "'" );
            return;
        }

        GeoSample sample = results.getSampleMap().get( currentSampleAccession );

        String designElement = tokens[0]; // ID_REF.

        for ( int i = 1; i < tokens.length; i++ ) {
            String token = tokens[i];
            String quantitationType = results.getSampleMap().get( currentSampleAccession ).getColumnNames().get( i );
            sample.addDatum( designElement, quantitationType, token );
        }

        sampleDataLines++;
    }

    /**
     * Parse a line from a sample section of a GSE file. These contain details about the samples and the 'raw' data for
     * the sample.
     * 
     * @param line
     * @param value
     */
    private void parseSampleLine( String line, String value ) {
        if ( this.processPlatformsOnly ) return;

        /***************************************************************************************************************
         * SAMPLE
         **************************************************************************************************************/
        if ( startsWithIgnoreCase( line, "!sample_table_begin" ) ) {
            inSampleTable = true;
            haveReadSampleDataHeader = false;
        } else if ( startsWithIgnoreCase( line, "!sample_table_end" ) ) {
            inSampleTable = false;
        } else if ( startsWithIgnoreCase( line, "!Sample_title" ) ) {
            if ( StringUtils.isBlank( currentSample().getTitle() ) ) {
                sampleSet( currentSampleAccession, "title", value );
            } else {
                log.info( "Sample " + currentSample() + " already has title " + currentSample().getTitle() );
            }
        } else if ( startsWithIgnoreCase( line, "!Sample_geo_accession" ) ) {
            currentSampleAccession = value;
            if ( !results.getSampleMap().containsKey( currentSampleAccession ) ) {
                log.debug( "New sample " + currentSampleAccession );
                results.getSampleMap().put( currentSampleAccession, new GeoSample() );
            }
        } else if ( startsWithIgnoreCase( line, "!Sample_status" ) ) {
            sampleSet( currentSampleAccession, "status", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_submission_date" ) ) {
            sampleSet( currentSampleAccession, "submissionDate", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_channel_count" ) ) {
            int numExtraChannelsNeeded = Integer.parseInt( value ) - 1;
            for ( int i = 0; i < numExtraChannelsNeeded; i++ ) {
                results.getSampleMap().get( currentSampleAccession ).addChannel();
            }
            sampleSet( currentSampleAccession, "channelCount", new Integer( Integer.parseInt( value ) ) );
        } else if ( startsWithIgnoreCase( line, "!Sample_source_name" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelSet( currentSampleAccession, "sourceName", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_organism" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelSet( currentSampleAccession, "organism", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_biomaterial_provider" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelSet( currentSampleAccession, "bioMaterialProvider", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_treatment_protocol" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelAddTo( currentSampleAccession, "treatmentProtocol", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_molecule" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelSet( currentSampleAccession, "molecule", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_growth_protocol" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelAddTo( currentSampleAccession, "growthProtocol", channel, value );
        } else if ( startsWithIgnoreCase( line, "!sample_extract_protocol" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelAddTo( currentSampleAccession, "extractProtocol", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_hyb_protocol" ) ) {
            sampleAddTo( currentSampleAccession, "hybProtocol", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_scan_protocol" ) ) {
            sampleAddTo( currentSampleAccession, "scanProtocol", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_data_processing" ) ) {
            sampleAddTo( currentSampleAccession, "dataProcessing", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_description" ) ) {
            sampleAddTo( currentSampleAccession, "description", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_label" ) ) {
            int channel = extractChannelNumber( line );
            sampleChannelSet( currentSampleAccession, "label", channel, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_characteristics" ) ) {
            int channel = extractChannelNumber( line );
            GeoSample sample = results.getSampleMap().get( currentSampleAccession );
            sample.getChannel( channel ).addCharacteristic( value );
        } else if ( startsWithIgnoreCase( line, "!Sample_platform_id" ) ) {
            sampleSet( currentSampleAccession, "id", value );
            if ( results.getPlatformMap().containsKey( value ) ) {
                results.getSampleMap().get( currentSampleAccession )
                        .addPlatform( results.getPlatformMap().get( value ) );
            }
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_name" ) ) {
            sampleContactSet( currentSampleAccession, "name", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_email" ) ) {
            sampleContactSet( currentSampleAccession, "email", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_institute" ) ) {
            sampleContactSet( currentSampleAccession, "institute", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_laboratory" ) ) {
            sampleContactSet( currentSampleAccession, "laboratory", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_department" ) ) {
            sampleContactSet( currentSampleAccession, "department", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_address" ) ) {
            sampleContactSet( currentSampleAccession, "address", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_city" ) ) {
            sampleContactSet( currentSampleAccession, "city", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_state" ) ) {
            sampleContactSet( currentSampleAccession, "state", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_country" ) ) {
            sampleContactSet( currentSampleAccession, "country", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_zip/postal_code" ) ) {
            sampleContactSet( currentSampleAccession, "postCode", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_phone" ) ) {
            sampleContactSet( currentSampleAccession, "phone", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_web_link" ) ) {
            sampleContactSet( currentSampleAccession, "webLink", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_contact_fax" ) ) {
            sampleContactSet( currentSeriesAccession, "fax", value );
        } else if ( startsWithIgnoreCase( line, "!Sample_series_id" ) ) {
            if ( results.getSeriesMap().containsKey( value ) ) {
                results.getSeriesMap().get( value ).addSample( results.getSampleMap().get( currentSampleAccession ) );
            }
            seriesSet( currentSeriesAccession, "seriesId", value ); // can be many?
        } else if ( startsWithIgnoreCase( line, "!Sample_supplementary_file" ) ) {
            sampleSupplementaryFileSet( currentSampleAccession, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_last_update_date" ) ) {
            sampleLastUpdateDate( currentSampleAccession, value );
        } else if ( startsWithIgnoreCase( line, "!Sample_data_row_count" ) ) {
            // nooop.
        } else if ( startsWithIgnoreCase( line, "!Sample_type" ) ) {
            sampleTypeSet( currentSampleAccession, value );
        } else {
            log.error( "Unknown flag in sample: " + line );
        }
    }

    /**
     * Parse a line from the "series" section of a GSE file. This contains annotations about the series.
     * 
     * @param line
     * @param value
     */
    private void parseSeriesLine( String line, String value ) {
        if ( this.processPlatformsOnly ) return;
        /***************************************************************************************************************
         * SERIES
         **************************************************************************************************************/
        if ( startsWithIgnoreCase( line, "!Series_title" ) ) {
            seriesSet( currentSeriesAccession, "title", value );
        } else if ( startsWithIgnoreCase( line, "!Series_geo_accession" ) ) {
            currentSeriesAccession = value;
        } else if ( startsWithIgnoreCase( line, "!Series_status" ) ) {
            seriesSet( currentSeriesAccession, "status", value );
        } else if ( startsWithIgnoreCase( line, "!Series_submission_date" ) ) {
            seriesSet( currentSeriesAccession, "submissionDate", value );
        } else if ( startsWithIgnoreCase( line, "!Series_pubmed_id" ) ) {
            seriesAddTo( currentSeriesAccession, "pubmedIds", value );
        } else if ( startsWithIgnoreCase( line, "!Series_overall_design" ) ) {
            // FIXME add support for this description.
        } else if ( startsWithIgnoreCase( line, "!Series_summary" ) ) {
            if ( value.toLowerCase().contains( "keyword" ) ) {
                String keyword = extractValue( value );
                seriesAddTo( currentSeriesAccession, "keyWords", keyword );
            } else {
                seriesAddTo( currentSeriesAccession, "summary", value );
            }
        } else if ( startsWithIgnoreCase( line, "!Series_type" ) ) {
            seriesSet( currentSeriesAccession, "overallDesign", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contributor" ) ) {
            GeoContact contributer = new GeoContact();
            String[] nameFields = StringUtils.split( value, "," );
            contributer.setName( StringUtils.join( nameFields, " " ) );
            results.getSeriesMap().get( currentSeriesAccession ).addContributer( contributer );
        } else if ( startsWithIgnoreCase( line, "!Series_sample_id" ) ) {
            addSeriesSample( value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_name" ) ) {
            seriesContactSet( currentSeriesAccession, "name", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_email" ) ) {
            seriesContactSet( currentSeriesAccession, "email", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_institute" ) ) {
            seriesContactSet( currentSeriesAccession, "institute", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_laboratory" ) ) {
            seriesContactSet( currentSeriesAccession, "laboratory", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_department" ) ) {
            seriesContactSet( currentSeriesAccession, "department", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_address" ) ) { // may not be used any longer.
            seriesContactSet( currentSeriesAccession, "address", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_state" ) ) { // new
            seriesContactSet( currentSeriesAccession, "state", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_zip/postal_code" ) ) { // new
            seriesContactSet( currentSeriesAccession, "postCode", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_country" ) ) { // new
            seriesContactSet( currentSeriesAccession, "country", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_city" ) ) {
            seriesContactSet( currentSeriesAccession, "city", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_phone" ) ) {
            seriesContactSet( currentSeriesAccession, "phone", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_fax" ) ) {
            seriesContactSet( currentSeriesAccession, "fax", value );
        } else if ( startsWithIgnoreCase( line, "!Series_contact_web_link" ) ) {
            seriesContactSet( currentSeriesAccession, "webLink", value );
        } else if ( startsWithIgnoreCase( line, "!series_platform_id" ) ) {
            seriesSet( currentSeriesAccession, "platformId", value );
        } else if ( startsWithIgnoreCase( line, "!series_table_begin" ) ) {
            inSeriesTable = true;
        } else if ( startsWithIgnoreCase( line, "!series_table_end" ) ) {
            inSeriesTable = false;
        } else if ( startsWithIgnoreCase( line, "!Series_variable_description_" ) ) {
            Integer variableId = new Integer( extractVariableNumber( line ) );
            results.getSeriesMap().get( currentSeriesAccession ).getVariables().get( variableId )
                    .setDescription( value );
        } else if ( startsWithIgnoreCase( line, "!Series_variable_sample_list_" ) ) {
            parseSeriesVariableSampleListLine( line, value );
        } else if ( startsWithIgnoreCase( line, "!Series_variable_repeats_" ) ) {
            Integer variableId = new Integer( extractVariableNumber( line ) );
            results.getSeriesMap().get( currentSeriesAccession ).getReplicates().get( variableId ).setRepeats(
                    GeoReplication.convertStringToRepeatType( value ) );
        } else if ( startsWithIgnoreCase( line, "!Series_variable_repeats_sample_list" ) ) {
            parseSeriesVariableRepeatsSampleListLine( line, value );
        } else if ( startsWithIgnoreCase( line, "!Series_web_link" ) ) {
            // seriesSet( currentSeriesAccession, "platformId", value );
        } else if ( startsWithIgnoreCase( line, "!Series_variable_" ) ) {
            Integer variableId = new Integer( extractVariableNumber( line ) );
            GeoVariable v = new GeoVariable();
            v.setType( GeoVariable.convertStringToType( value ) );
            results.getSeriesMap().get( currentSeriesAccession ).addToVariables( variableId, v );
        } else if ( startsWithIgnoreCase( line, "!Series_supplementary_file" ) ) {
            seriesSupplementaryFileSet( currentSeriesAccession, value );
        } else if ( startsWithIgnoreCase( line, "!Series_last_update_date" ) ) {
            seriesLastUpdateDate( currentSeriesAccession, value );
        } else {
            log.error( "Unknown flag in series: " + line );
        }
    }

    /**
     * @param accession
     * @param value
     */
    private void sampleLastUpdateDate( String accession, String value ) {
        GeoSample sample = results.getSampleMap().get( accession );
        lastUpdateDateSet( sample, value );
    }

    /**
     * @param accession
     * @param value
     */
    private void platformLastUpdateDate( String accession, String value ) {
        GeoPlatform platform = results.getPlatformMap().get( accession );
        lastUpdateDateSet( platform, value );
    }

    /**
     * @param accession
     * @param value
     */
    private void seriesLastUpdateDate( String accession, String value ) {
        GeoSeries series = results.getSeriesMap().get( accession );
        lastUpdateDateSet( series, value );
    }

    /**
     * @param series
     * @param value
     */
    private void lastUpdateDateSet( Object object, String value ) {

        if ( object instanceof GeoPlatform )
            ( ( GeoPlatform ) object ).setLastUpdateDate( value );

        else if ( object instanceof GeoSeries )
            ( ( GeoSeries ) object ).setLastUpdateDate( value );

        else if ( object instanceof GeoSample ) ( ( GeoSample ) object ).setLastUpdateDate( value );
    }

    /**
     * @param accession
     * @param value
     */
    private void sampleSupplementaryFileSet( String accession, String value ) {
        GeoSample sample = results.getSampleMap().get( accession );
        supplementaryFileSet( sample, value );
    }

    /**
     * @param accession
     * @param value
     */
    private void platformSupplementaryFileSet( String accession, String value ) {
        GeoPlatform platform = results.getPlatformMap().get( accession );
        supplementaryFileSet( platform, value );
    }

    /**
     * @param accession
     * @param value
     */
    private void seriesSupplementaryFileSet( String accession, String value ) {
        GeoSeries series = results.getSeriesMap().get( accession );
        supplementaryFileSet( series, value );
    }

    /**
     * @param series
     * @param value
     */
    private void supplementaryFileSet( Object object, String value ) {

        if ( object instanceof GeoPlatform )
            ( ( GeoPlatform ) object ).setSupplementaryFile( value );

        else if ( object instanceof GeoSeries )
            ( ( GeoSeries ) object ).setSupplementaryFile( value );

        else if ( object instanceof GeoSample ) ( ( GeoSample ) object ).setSupplementaryFile( value );

    }

    /**
     * @param line
     * @param value
     */
    private void parseSeriesVariableRepeatsSampleListLine( String line, String value ) {
        Integer variableId = new Integer( extractVariableNumber( line ) );
        GeoReplication var = currentSeries().getReplicates().get( variableId );
        Collection<String> samples = Arrays.asList( StringUtils.split( value, ", " ) );
        for ( String string : samples ) {
            GeoSample sam = results.getSampleMap().get( string );
            var.addToRepeatsSampleList( sam );
            sam.addReplication( var );
        }
    }

    /**
     * @param line
     * @param value
     */
    private void parseSeriesVariableSampleListLine( String line, String value ) {
        Integer variableId = new Integer( extractVariableNumber( line ) );
        GeoVariable var = currentSeries().getVariables().get( variableId );
        Collection<String> samples = Arrays.asList( StringUtils.split( value, "," ) );
        for ( String string : samples ) {
            GeoSample sam = results.getSampleMap().get( string );
            var.addToVariableSampleList( sam );
            sam.addVariable( var );
        }
    }

    /**
     * Parse a line from a "subset" section of a GDS file. This section contains information about experimental subsets
     * within a dataset. These usually correspond to different factor values such as "drug-treated" vs. "placebo".
     * 
     * @param line
     * @param value
     */
    private void parseSubsetLine( String line, String value ) {
        /***************************************************************************************************************
         * SUBSET
         **************************************************************************************************************/
        if ( startsWithIgnoreCase( line, "!Dataset_title" ) ) {
            subsetSet( currentSubsetAccession, "title", value );
        } else if ( startsWithIgnoreCase( line, "!subset_dataset_id" ) ) {
            subsetSet( currentSubsetAccession, "dataSet", value );
        } else if ( startsWithIgnoreCase( line, "!subset_description" ) ) {
            subsetAddTo( currentSubsetAccession, "description", value );
        } else if ( startsWithIgnoreCase( line, "!subset_sample_id" ) ) {
            // This should yield a list of samples we have already seen.
            String[] values = value.split( "," );
            for ( int i = 0; i < values.length; i++ ) {
                String sampleAccession = values[i];

                if ( !results.getSampleMap().containsKey( sampleAccession ) ) {
                    addNewSample( sampleAccession );
                }

                if ( log.isDebugEnabled() )
                    log.debug( "Adding sample: " + sampleAccession + " to subset " + currentSubsetAccession );

                results.getSubsetMap().get( currentSubsetAccession ).addSample(
                        results.getSampleMap().get( sampleAccession ) );
            }

        } else if ( startsWithIgnoreCase( line, "!subset_type" ) ) {
            subsetSet( currentSubsetAccession, "type", value );
        } else {
            log.error( "Unknown flag: " + line );
        }
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void platformAddTo( String accession, String property, Object value ) {
        GeoPlatform platform = results.getPlatformMap().get( accession );
        if ( platform == null ) throw new IllegalArgumentException( "Unknown platform " + accession );
        addTo( platform, property, value );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void platformContactSet( String accession, String property, Object value ) {
        GeoPlatform platform = results.getPlatformMap().get( accession );
        contactSet( platform, property, value );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void platformSet( String accession, String property, Object value ) {
        GeoPlatform platform = results.getPlatformMap().get( accession );
        if ( platform == null ) throw new IllegalArgumentException( "Unknown platform " + accession );

        if ( property.equals( "technology" ) ) {
            assert value instanceof String;
            value = GeoDataset.convertStringToPlatformType( ( String ) value );
        }

        try {
            BeanUtils.setProperty( platform, property, value );
        } catch ( IllegalAccessException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param currentSampleAccession2
     * @param string
     * @param value
     */
    private void sampleAddTo( String accession, String property, Object value ) {
        GeoSample sample = results.getSampleMap().get( accession );
        if ( sample == null ) throw new IllegalArgumentException( "Unknown sample " + accession );
        addTo( sample, property, value );
    }

    /**
     * @param currentSampleAccession2
     * @param string
     * @param channel
     * @param value
     */
    private void sampleChannelAddTo( String sampleAccession, String property, int channel, String value ) {
        GeoSample sample = results.getSampleMap().get( sampleAccession );
        this.addTo( sample.getChannel( channel ), property, value );
    }

    /**
     * @param currentSampleAccession2
     * @param string
     * @param channel
     * @param value
     */
    private void sampleChannelSet( String sampleAccession, String property, int channel, Object value ) {
        GeoSample sample = results.getSampleMap().get( sampleAccession );

        if ( property.equals( "molecule" ) ) {
            value = GeoChannel.convertStringToMolecule( ( String ) value );
        }

        try {
            BeanUtils.setProperty( sample.getChannel( channel ), property, value );
        } catch ( IllegalAccessException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void sampleContactSet( String accession, String property, Object value ) {
        GeoSample sample = results.getSampleMap().get( accession );
        contactSet( sample, property, value );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void sampleSet( String accession, String property, Object value ) {
        GeoSample sample = results.getSampleMap().get( accession );
        if ( sample == null ) throw new IllegalArgumentException( "Unknown sample " + accession );
        try {
            BeanUtils.setProperty( sample, property, value );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void seriesAddTo( String accession, String property, Object value ) {
        GeoSeries series = results.getSeriesMap().get( accession );
        if ( series == null ) throw new IllegalArgumentException( "Unknown series " + accession );
        addTo( series, property, value );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void seriesContactSet( String accession, String property, Object value ) {
        GeoSeries series = results.getSeriesMap().get( accession );
        contactSet( series, property, value );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void seriesSet( String accession, String property, Object value ) {
        GeoSeries series = results.getSeriesMap().get( accession );
        if ( series == null ) throw new IllegalArgumentException( "Unknown series " + accession );
        try {
            BeanUtils.setProperty( series, property, value );
        } catch ( IllegalAccessException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param line
     * @param string
     * @return
     */
    private boolean startsWithIgnoreCase( String string, String pattern ) {
        return string.toUpperCase().startsWith( pattern.toUpperCase() );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void subsetAddTo( String accession, String property, Object value ) {
        GeoSubset subset = results.getSubsetMap().get( accession );
        if ( subset == null ) throw new IllegalArgumentException( "Unknown subset " + accession );
        addTo( subset, property, value );
    }

    /**
     * @param accession
     * @param property
     * @param value
     */
    private void subsetSet( String accession, String property, Object value ) {
        GeoSubset subset = results.getSubsetMap().get( accession );
        if ( subset == null ) throw new IllegalArgumentException( "Unknown subset " + accession );

        if ( property.equals( "type" ) ) {
            value = GeoVariable.convertStringToType( ( String ) value );
        }

        try {
            BeanUtils.setProperty( subset, property, value );
        } catch ( IllegalAccessException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }
}

/**
 * This simply holds the results obtained from parsing.
 */
class GeoParseResult {
    private Map<String, GeoDataset> datasetMap;

    private Map<String, GeoPlatform> platformMap;

    private Map<String, GeoSample> sampleMap;

    private Map<String, GeoSeries> seriesMap;

    private Map<String, GeoSubset> subsetMap;

    public GeoParseResult() {
        sampleMap = new HashMap<String, GeoSample>();
        platformMap = new HashMap<String, GeoPlatform>();
        seriesMap = new HashMap<String, GeoSeries>();

        subsetMap = new HashMap<String, GeoSubset>();

        datasetMap = new HashMap<String, GeoDataset>();
    }

    /**
     * @return Returns the datasetMap.
     */
    public Map<String, GeoDataset> getDatasetMap() {
        return this.datasetMap;
    }

    /**
     * @return
     */
    public Map<String, GeoDataset> getDatasets() {
        return this.datasetMap;
    }

    /**
     * @return Returns the platformMap.
     */
    public Map<String, GeoPlatform> getPlatformMap() {
        return this.platformMap;
    }

    /**
     * @return
     */
    public Map<String, GeoPlatform> getPlatforms() {
        return this.platformMap;
    }

    /**
     * @return Returns the sampleMap.
     */
    public Map<String, GeoSample> getSampleMap() {
        return this.sampleMap;
    }

    /**
     * @return
     */
    public Map<String, GeoSample> getSamples() {
        return this.sampleMap;
    }

    /**
     * @return
     */
    public Map<String, GeoSeries> getSeries() {
        return this.seriesMap;
    }

    /**
     * @return Returns the seriesMap.
     */
    public Map<String, GeoSeries> getSeriesMap() {
        return this.seriesMap;
    }

    /**
     * @return Returns the subsetMap.
     */
    public Map<String, GeoSubset> getSubsetMap() {
        return this.subsetMap;
    }

}
