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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.loader.util.parser.BasicLineMapParser;
import ubic.gemma.model.expression.designElement.Reporter;
import ubic.gemma.model.genome.biosequence.BioSequence;

/**
 * Parse an Illumina "manifest.txt" file (tab-delimited). A one-line header is permitted.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class IlluminaProbeReader extends BasicLineMapParser {

    protected static final Log log = LogFactory.getLog( IlluminaProbeReader.class );

    private Map<String, Reporter> results = new HashMap<String, Reporter>();

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.io.reader.BasicLineMapParser#parseOneLine(java.lang.String)
     */
    @Override
    public Object parseOneLine( String line ) {
        String[] sArray = line.split( "\t" );
        if ( sArray.length == 0 ) throw new IllegalArgumentException( "Line format is not valid" );

        String probeId = sArray[0];

        if ( probeId == null || probeId.length() == 0 ) throw new IllegalArgumentException( "Probe id invalid" );

        if ( probeId.startsWith( "Search" ) ) return null;

        if ( sArray.length < 10 ) throw new IllegalArgumentException( "Line format is not valid" );

        String sequence = sArray[9];
        if ( sequence == null || sequence.length() == 0 ) throw new IllegalArgumentException( "Sequence is invalid" );

        Reporter ap = Reporter.Factory.newInstance();

        BioSequence immobChar = BioSequence.Factory.newInstance();
        immobChar.setSequence( sequence );
        ap.setName( probeId );
        ap.setImmobilizedCharacteristic( immobChar );

        return ap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see baseCode.io.reader.BasicLineMapParser#getKey(java.lang.Object)
     */
    @Override
    protected Object getKey( Object newItem ) {
        return ( ( Reporter ) newItem ).getName();
    }

    @Override
    public Reporter get( Object key ) {
        return results.get( key );
    }

    @Override
    public Collection<Reporter> getResults() {
        return results.values();
    }

    @Override
    protected void put( Object key, Object value ) {
        results.put( ( String ) key, ( Reporter ) value );
    }

    @Override
    public boolean containsKey( Object key ) {
        return results.containsKey( key );
    }

    @Override
    public Collection getKeySet() {
        return results.keySet();
    }
}
