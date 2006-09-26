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
package ubic.gemma.externalDb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.MappingSqlQuery;

import ubic.gemma.model.common.description.DatabaseEntry;
import ubic.gemma.model.common.description.DatabaseType;
import ubic.gemma.model.common.description.ExternalDatabase;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.biosequence.BioSequence;
import ubic.gemma.model.genome.biosequence.PolymerType;
import ubic.gemma.model.genome.biosequence.SequenceType;

/**
 * Class to handle dumping data from Goldenpath into Gemma.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class GoldenPathDumper extends GoldenPath {

    // these are provided for testing (switch off ones not using)
    private boolean DO_EST = true;
    private boolean DO_MRNA = true;
    private boolean DO_REFSEQ = true;

    public GoldenPathDumper( int port, String databaseName, String host, String user, String password )
            throws SQLException {
        super( port, databaseName, host, user, password );
    }

    public GoldenPathDumper( Taxon taxon ) throws SQLException {
        super( taxon );
    }

    public GoldenPathDumper() throws SQLException {
        super();
    }

    ExternalDatabase genbank;

    /**
     * Get all ESTs and mRNAs for a taxon. This will return a very large collection.
     * 
     * @param limit per table, for testing purposes. Set to value <=0 to ignore.
     * @param queue to store the results in, to be consumed by another method.
     */
    public void dumpTranscriptBioSequences( int limit, BlockingQueue<BioSequence> queue ) {
        initGenbank();

        double loadFactor = 0.5;

        int batchSize = ( int ) Math.floor( ( queue.remainingCapacity() + queue.size() ) * loadFactor );

        assert batchSize > 0;

        String limitSuffix = "";
        if ( limit > 0 ) {
            limitSuffix = "limit " + limit;
        } else {
            limitSuffix = " limit " + batchSize;
        }

        // FIXME - repeated code.
        log.info( "starting ests" );
        int offset = 0;
        int numInput = 0;
        while ( DO_EST && !( limit > 0 && numInput >= limit ) ) {
            try {
                Collection<BioSequence> sequences = loadSequencesByQuery( "all_est", SequenceType.EST, limitSuffix
                        + " offset " + offset );
                if ( sequences.size() == 0 ) {
                    break;
                }
                for ( BioSequence sequence : sequences ) {
                    queue.put( sequence );
                    if ( ++numInput % 1000 == 0 ) {
                        log.info( "Input " + numInput + " from goldenpath db" );
                    }
                }
                offset += batchSize;
            } catch ( Exception e ) {
                log.info( e );
                break;
            }
        }

        log.info( "starting mrnas" );
        offset = 0;
        numInput = 0;
        while ( DO_MRNA && !( limit > 0 && numInput >= limit ) ) {
            try {
                Collection<BioSequence> sequences = loadSequencesByQuery( "all_mrna", SequenceType.mRNA, limitSuffix
                        + " offset " + offset );
                if ( sequences.size() == 0 ) {
                    break;
                }
                for ( BioSequence sequence : sequences ) {
                    queue.put( sequence );
                    if ( ++numInput % 1000 == 0 ) {
                        log.info( "Input " + numInput + " from goldenpath db" );
                    }
                }
                offset += batchSize;
            } catch ( Exception e ) {
                log.info( e );
                break;
            }
        }

        log.info( "starting refseq" );
        offset = 0;
        numInput = 0;
        while ( DO_REFSEQ && !( limit > 0 && numInput >= limit ) ) {
            try {
                Collection<BioSequence> sequences = loadRefseqByQuery( limitSuffix + " offset " + offset );
                if ( sequences.size() == 0 ) {
                    break;
                }
                for ( BioSequence sequence : sequences ) {
                    queue.put( sequence );
                    if ( ++numInput % 1000 == 0 ) {
                        log.info( "Input " + numInput + " from goldenpath db" );
                    }
                }
                offset += batchSize;
            } catch ( Exception e ) {
                log.info( e );
                break;
            }
        }

    }

    private void initGenbank() {
        // if ( externalDatabaseService != null ) {
        // genbank = externalDatabaseService.find( "Genbank" );
        // } else {
        genbank = ExternalDatabase.Factory.newInstance();
        genbank.setName( "Genbank" );
        genbank.setType( DatabaseType.SEQUENCE );
        // }
    }

    private class BioSequenceMappingQuery extends MappingSqlQuery {

        SequenceType type;

        public BioSequenceMappingQuery( DriverManagerDataSource ds, String table, SequenceType type, String limit ) {
            super( ds, "SELECT qName, qSize FROM " + table + " " + limit );
            this.type = type;
            compile();
        }

        public Object mapRow( ResultSet rs, int rowNumber ) throws SQLException {
            BioSequence bioSequence = BioSequence.Factory.newInstance();

            DatabaseEntry de = DatabaseEntry.Factory.newInstance();

            String name = rs.getString( "qName" );
            Long length = rs.getLong( "qSize" );
            bioSequence.setName( name );
            bioSequence.setLength( length );
            bioSequence.setIsApproximateLength( false );
            bioSequence.setPolymerType( PolymerType.DNA );
            bioSequence.setIsCircular( false );

            de.setAccession( name );
            de.setExternalDatabase( genbank );

            bioSequence.setType( type );
            bioSequence.setSequenceDatabaseEntry( de );

            return bioSequence;
        }

    }

    private class BioSequenceRefseqMappingQuery extends MappingSqlQuery {

        public BioSequenceRefseqMappingQuery( DriverManagerDataSource ds, String query ) {
            super( ds, query );
            compile();
        }

        public Object mapRow( ResultSet rs, int rowNumber ) throws SQLException {
            BioSequence bioSequence = BioSequence.Factory.newInstance();

            DatabaseEntry de = DatabaseEntry.Factory.newInstance();

            String name = rs.getString( "name" );
            bioSequence.setName( name );
            bioSequence.setPolymerType( PolymerType.DNA );
            bioSequence.setIsCircular( false );

            de.setAccession( name );
            de.setExternalDatabase( genbank );

            bioSequence.setType( SequenceType.REFSEQ );
            bioSequence.setSequenceDatabaseEntry( de );

            return bioSequence;
        }

    }

    /**
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    private Collection<BioSequence> loadSequencesByQuery( String table, SequenceType type, String limit ) {

        BioSequenceMappingQuery bsQuery = new BioSequenceMappingQuery( dataSource, table, type, limit );

        return bsQuery.execute();

    }

    /**
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    private Collection<BioSequence> loadRefseqByQuery( String limitSuffix ) {

        String query = "SELECT name FROM refgene " + limitSuffix;

        BioSequenceRefseqMappingQuery bsQuery = new BioSequenceRefseqMappingQuery( dataSource, query );

        return bsQuery.execute();

    }

}
