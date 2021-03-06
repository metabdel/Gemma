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

package ubic.gemma.core.apps;

import org.apache.commons.cli.Option;
import ubic.gemma.core.loader.genome.gene.ExternalFileGeneLoaderService;
import ubic.gemma.core.util.AbstractCLIContextCLI;

import java.io.IOException;

/**
 * CLI for loading genes from a non NCBI files. A taxon and gene file should be supplied as command line arguments. File
 * should be in tab delimited format containing gene symbol, gene name, uniprot id in that order.
 *
 * @author ldonnison
 */
public class ExternalFileGeneLoaderCLI extends AbstractCLIContextCLI {

    private String directGeneInputFileName = null;
    private String taxonName;

    @SuppressWarnings({ "unused", "WeakerAccess" }) // Possible external use
    public ExternalFileGeneLoaderCLI() {
        super();
    }

    public static void main( String[] args ) {
        // super constructor calls build options
        ExternalFileGeneLoaderCLI p = new ExternalFileGeneLoaderCLI();
        AbstractCLIContextCLI.executeCommand( p, args );
    }

    @Override
    public String getShortDesc() {
        return "loading genes from a non-NCBI files; only used for species like salmon";
    }

    /**
     * This method is called at the end of processCommandLine
     */
    @Override
    protected void processOptions() {
        super.processOptions();
        if ( this.hasOption( 'f' ) ) {
            directGeneInputFileName = this.getOptionValue( 'f' );
            if ( directGeneInputFileName == null ) {
                throw new IllegalArgumentException( "No gene input file provided " );
            }
        }
        if ( this.hasOption( 't' ) ) {
            this.taxonName = this.getOptionValue( 't' );
            if ( taxonName == null ) {
                throw new IllegalArgumentException( "No taxon name supplied " );
            }
        }

    }

    @Override
    public String getCommandName() {
        return "loadGenesFromFile";
    }

    @SuppressWarnings("static-access")
    @Override
    protected void buildOptions() {
        Option directGene = Option.builder( "f" )
                .desc( "Tab delimited format containing gene symbol, gene name, uniprot id in that order" )
                .hasArg().argName( "file" ).build();
        this.addOption( directGene );

        Option taxonNameOption = Option.builder( "t" ).hasArg()
                .desc( "Taxon common name e.g. 'salmonoid'; does not have to be a species " ).build();
        this.addOption( taxonNameOption );

        this.requireLogin();
    }

    @Override
    protected Exception doWork( String[] args ) {
        Exception err = this.processCommandLine( args );
        if ( err != null )
            return err;
        this.processGeneList();
        return null;
    }

    /**
     * Main entry point to service class which reads a gene file and persists the genes in that file.
     */
    @SuppressWarnings({ "unused", "WeakerAccess" }) // Possible external use
    public void processGeneList() {

        ExternalFileGeneLoaderService loader = this.getBean( ExternalFileGeneLoaderService.class );

        try {
            int count = loader.load( directGeneInputFileName, taxonName );
            System.out.println( count + " genes loaded successfully " );
        } catch ( IOException e ) {
            System.out.println( "File could not be read: " + e.getMessage() );
            throw new RuntimeException( e );
        } catch ( IllegalArgumentException e ) {
            System.out.println(
                    "One of the programme arguments were incorrect check gene file is in specified location and taxon is in system."
                            + e.getMessage() );
            throw new RuntimeException( e );
        } catch ( Exception e ) {
            System.out.println( "Gene file persisting error: " + e.getMessage() );
            throw new RuntimeException( e );
        }

    }

    @Override
    public GemmaCLI.CommandGroup getCommandGroup() {
        return GemmaCLI.CommandGroup.SYSTEM;
    }

}
