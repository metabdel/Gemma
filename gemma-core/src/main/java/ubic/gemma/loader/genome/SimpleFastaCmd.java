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
package ubic.gemma.loader.genome;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.util.ConfigUtils;

/**
 * Simple implementation of methods for fetching sequences from blast-formatted databases.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class SimpleFastaCmd implements FastaCmd {

    private static Log log = LogFactory.getLog( SimpleFastaCmd.class.getName() );

    private static String fastaCmdExecutable = ConfigUtils.getString( "fastaCmd.exe" );

    private static String blastDbHome = System.getenv( "BLASTDB" );

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getBatchAccessions(java.util.Collection, java.lang.String)
     */
    public Collection<BioSequence> getBatchAccessions( Collection<String> accessions, String database, String blastHome ) {
        try {
            return this.getMultiple( accessions, database, blastHome );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getBatchIdentifiers(java.util.Collection, java.lang.String)
     */
    public Collection<BioSequence> getBatchIdentifiers( Collection<Integer> identifiers, String database,
            String blastHome ) {
        try {
            return this.getMultiple( identifiers, database, blastHome );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getByAccesion(java.lang.String, java.lang.String)
     */
    public BioSequence getByAccession( String accession, String database, String blastHome ) {
        try {
            return getSingle( accession, database, blastHome );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param keys
     * @param database
     * @param blastHome
     * @return
     * @throws IOException
     */
    private Collection<BioSequence> getMultiple( Collection<? extends Object> keys, String database, String blastHome )
            throws IOException {

        File tmp = File.createTempFile( "sequenceIds", ".txt" );
        Writer tmpOut = new FileWriter( tmp );

        for ( Object object : keys ) {
            tmpOut.write( object.toString() + "\n" );
        }

        tmpOut.close();
        String[] opts = new String[] { "BLASTDB=" + blastHome };
        Process pr = Runtime.getRuntime().exec(
                fastaCmdExecutable + " -d " + database + " -i " + tmp.getAbsolutePath(), opts );

        InputStream is = new BufferedInputStream( pr.getInputStream() );
        FastaParser parser = new FastaParser();
        parser.parse( is );
        Collection<BioSequence> sequences = parser.getResults();
        tmp.delete();
        return sequences;

    }

    /**
     * @param accession
     * @param database
     * @blastHome
     * @throws IOException
     */
    private BioSequence getSingle( Object key, String database, String blastHome ) throws IOException {

        String[] opts = new String[] { "BLASTDB=" + blastHome };
        Process pr = Runtime.getRuntime().exec( fastaCmdExecutable + " -d " + database + " -s " + key.toString(), opts );

        InputStream is = new BufferedInputStream( pr.getInputStream() );
        FastaParser parser = new FastaParser();
        parser.parse( is );
        Collection<BioSequence> sequences = parser.getResults();
        if ( sequences.size() == 0 ) {
            return null;
        }
        if ( sequences.size() == 1 ) {
            return sequences.iterator().next();
        }
        throw new IllegalStateException( "Got more than one sequence!" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getByIdentifier(int, java.lang.String)
     */
    public BioSequence getByIdentifier( int identifier, String database ) {
        try {
            return getSingle( identifier, database, blastDbHome );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getByIdentifier(int, java.lang.String, java.lang.String)
     */
    public BioSequence getByIdentifier( int identifier, String database, String blastHome ) {
        try {
            return getSingle( identifier, database, blastHome );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getBatchAccessions(java.util.Collection, java.lang.String)
     */
    public Collection<BioSequence> getBatchAccessions( Collection<String> accessions, String database ) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getBatchIdentifiers(java.util.Collection, java.lang.String)
     */
    public Collection<BioSequence> getBatchIdentifiers( Collection<Integer> identifiers, String database ) {
        return this.getBatchIdentifiers( identifiers, database, blastDbHome );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.genome.FastaCmd#getByAccesion(java.lang.String, java.lang.String)
     */
    public BioSequence getByAccesion( String accession, String database ) {
        return this.getByAccession( accession, database, blastDbHome );
    }

}
