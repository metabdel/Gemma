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
package ubic.gemma.loader.expression.arrayDesign;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.loader.util.parser.BasicLineMapParser;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.designElement.Reporter;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.model.genome.biosequence.PolymerType;
import ubic.gemma.model.genome.biosequence.SequenceType;

/**
 * Reads Affymetrix Probe files, including exon arrays.
 * <p>
 * Expected format is tabbed, NOT FASTA. A one-line header starting with the word "Probe" is REQUIRED. In later versions
 * of the format the second field of the file (column) is omitted.
 * </p>
 * <p>
 * For 3' arrays, here is an example:
 *</p>
 * 
 * <pre>
 * 1494_f_at 1 325 359 1118 TCCCCATGAGTTTGGCCCGCAGAGT Antisense
 * </pre>
 * <p>
 * For exon arrays, the format is described in the README files that come with the sequence Zips. As in the 3' array
 * files, the probes are arranged 5' -> 3' so 'collapsing' can proceed as it does for the 3' arrays.
 * </p>
 * 
 * <pre>
 *    The probe tabular data file contains all probe sequences from the
 *    array in tab-delimited format. Column headers are indicated in the
 *    first line.
 * 
 *    I.B.1. Column header line
 * 
 *       Column Name                        Description
 *     -----------------       ------------------------------------------
 *       Probe ID               Probe identifier (integer)
 *       Probe Set ID           Probe set identifier (integer)
 *       probe x                X coordinate for probe location on array
 *       probe y                Y coordinate for probe location on array
 *       assembly               Genome assembly version from array design time
 *       seqname                Sequence name for genomic location of probe 
 *       start                  Starting coordinate of probe genomic location (1-based)
 *       stop                   Ending coordinate of probe genomic location (1-based)
 *       strand                 Sequence strand of probe genomic location (+ or -)
 *       probe sequence         Probe sequence
 *       target strandedness    Strandedness of the target which the probe detects
 *       category               Array design category of the probe (described below)
 * 
 * 
 *    I.B.2. Example entry
 * 
 *    Shown is an example column header line and data line from the human
 *    exon array. 
 * 
 * Probe ID    Probe Set ID    probe x probe y assembly    seqname start   stop    strand  probe sequence  target strandedness category
 * 494998  2315101 917 193 build-34/hg16   chr1    1788    1812    +   CACGGGAAGTCTGGGCTAAGAGACA   Sense   main
 * </pre>
 * 
 * @author pavlidis
 * @version $Id$
 */
public class AffyProbeReader extends BasicLineMapParser<String, CompositeSequence> {

    protected static final Log log = LogFactory.getLog( AffyProbeReader.class );

    private int sequenceField = 4;

    private Map<String, CompositeSequence> results = new HashMap<String, CompositeSequence>();

    /*
     * (non-Javadoc)
     * @see baseCode.io.reader.BasicLineParser#parseOneLine(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public CompositeSequence parseOneLine( String line ) {
        String[] sArray = line.split( "\t" );
        if ( sArray.length == 0 )
            throw new IllegalArgumentException( "Line format is not valid (not tab-delimited or no fields found)" );

        String probeSetId = sArray[0];
        if ( probeSetId.startsWith( "Probe" ) ) {
            if ( sArray[1].equals( "Probe Set ID" ) || sArray[1].equals( "Transcript Cluster ID" ) ) {
                log.info( "Exon array format detected" );
                sequenceField = 9;
            }
            // skip header row.
            return null;
        }

        if ( sArray.length < sequenceField + 1 ) {
            throw new IllegalArgumentException( "Too few fields in line, expected at least " + ( sequenceField + 1 )
                    + " but got " + sArray.length );
        }

        String sequence = sArray[sequenceField];
        String xcoord;
        String ycoord;
        String startInSequence;
        String index = null;
        if ( sequenceField == 4 ) {
            xcoord = sArray[1];
            ycoord = sArray[2];
            startInSequence = sArray[3];
        } else if ( sequenceField == 9 ) {
            // Exon array
            probeSetId = sArray[1];
            startInSequence = sArray[6]; // 7 is end, 8 is strand, 9 is sequence
            xcoord = sArray[2];
            ycoord = sArray[3];

        } else {
            index = sArray[1];
            xcoord = sArray[2];
            ycoord = sArray[3];
            startInSequence = sArray[sequenceField - 1];
        }

        Reporter reporter = Reporter.Factory.newInstance();

        try {
            reporter.setRow( Integer.parseInt( xcoord ) );
            reporter.setCol( Integer.parseInt( ycoord ) );

        } catch ( NumberFormatException e ) {
            log.warn( "Invalid row: could not parse coordinates: " + xcoord + ", " + ycoord );
            return null;
        }

        try {
            reporter.setStartInBioChar( Long.parseLong( startInSequence ) );
        } catch ( NumberFormatException e ) {
            log.warn( "Invalid row: could not parse start in sequence: " + startInSequence );
            return null;
        }

        String reporterName = probeSetId + ( index == null ? "" : "#" + index ) + ":" + xcoord + ":" + ycoord;
        reporter.setName( reporterName );
        BioSequence immobChar = BioSequence.Factory.newInstance();
        immobChar.setSequence( sequence );
        immobChar.setIsApproximateLength( false );
        immobChar.setLength( new Long( sequence.length() ) );
        immobChar.setType( SequenceType.AFFY_PROBE );
        immobChar.setPolymerType( PolymerType.DNA );

        reporter.setImmobilizedCharacteristic( immobChar );

        CompositeSequence probeSet = get( probeSetId );

        if ( probeSet == null ) {
            probeSet = CompositeSequence.Factory.newInstance();
        }
        probeSet.setName( probeSetId );

        if ( probeSet.getComponentReporters() == null ) {
            probeSet.setComponentReporters( new HashSet() );
        }

        reporter.setCompositeSequence( probeSet );
        probeSet.getComponentReporters().add( reporter );
        return probeSet;

    }

    /*
     * (non-Javadoc)
     * @see baseCode.io.reader.BasicLineMapParser#getKey(java.lang.Object)
     */
    @Override
    protected String getKey( CompositeSequence newItem ) {
        return newItem.getName();
    }

    /**
     * Set the index (starting from zero) of the field where the sequence is found. This varies in the
     * Affymetrix-provided files.
     * 
     * @param sequenceField
     */
    public void setSequenceField( int sequenceField ) {
        this.sequenceField = sequenceField;
    }

    @Override
    public CompositeSequence get( String key ) {
        return results.get( key );
    }

    @Override
    public Collection<CompositeSequence> getResults() {
        return new HashSet<CompositeSequence>( results.values() ); // make sure we don't get a HashMap$values
    }

    @Override
    protected void put( String key, CompositeSequence value ) {
        results.put( key, value );
    }

    @Override
    public boolean containsKey( String key ) {
        return results.containsKey( key );
    }

    @Override
    public Collection<String> getKeySet() {
        return results.keySet();
    }

}
