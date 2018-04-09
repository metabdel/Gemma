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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ubic.gemma.core.apps.GemmaCLI.CommandGroup;
import ubic.gemma.core.loader.expression.arrayDesign.ArrayDesignSequenceAlignmentService;
import ubic.gemma.core.loader.genome.BlatResultParser;
import ubic.gemma.core.util.AbstractCLI;
import ubic.gemma.core.util.AbstractCLIContextCLI;
import ubic.gemma.model.common.auditAndSecurity.eventType.ArrayDesignSequenceAnalysisEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.TechnologyType;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.sequenceAnalysis.BlatResult;
import ubic.gemma.persistence.service.genome.taxon.TaxonService;

/**
 * Command line interface to run blat on the sequences for a microarray; the results are persisted in the DB. You must
 * start the BLAT server first before using this.
 *
 * @author pavlidis
 */
public class ArrayDesignBlatCli extends ArrayDesignSequenceManipulatingCli {

    public static void main( String[] args ) {
        ArrayDesignBlatCli p = new ArrayDesignBlatCli();
        AbstractCLIContextCLI.tryDoWorkNoExit( p, args );
    }

    private Taxon taxon;
    private ArrayDesignSequenceAlignmentService arrayDesignSequenceAlignmentService;
    private String blatResultFile = null;
    private Double blatScoreThreshold = Blat.DEFAULT_BLAT_SCORE_THRESHOLD;

    private boolean sensitive = false;

    @Override
    public CommandGroup getCommandGroup() {
        return CommandGroup.PLATFORM;
    }

    @Override
    public String getCommandName() {
        return "blatPlatform";
    }

    @Override
    public String getShortDesc() {
        return "Run BLAT on the sequences for a platform; the results are persisted in the DB.";
    }

    @SuppressWarnings("static-access")
    @Override
    protected void buildOptions() {
        super.buildOptions();

        OptionBuilder.hasArg();
        OptionBuilder.withArgName( "PSL file" );
        OptionBuilder.withDescription(
                "Blat result file in PSL format (if supplied, BLAT will not be run; will not work with settings that indicate multiple platforms to run); -t option overrides" );
        OptionBuilder
                .withLongOpt( "blatfile" );
        Option blatResultOption = OptionBuilder.create( 'b' );

        OptionBuilder.hasArg();
        OptionBuilder.withArgName( "Blat score threshold" );
        OptionBuilder.withDescription(
                "Threshold (0-1.0) for acceptance of BLAT alignments [Default = " + this.blatScoreThreshold + "]" );
        OptionBuilder
                .withLongOpt( "scoreThresh" );
        Option blatScoreThresholdOption = OptionBuilder.create( 's' );

        OptionBuilder.withDescription( "Run on more sensitive server, if available" );
        this.addOption(
                OptionBuilder.create( "sensitive" ) );

        OptionBuilder.hasArg();
        OptionBuilder.withArgName( "taxon" );
        OptionBuilder.withDescription(
                "Taxon common name (e.g., human); if platform name not given (analysis will be restricted to sequences on that platform for taxon given), blat will be run for all ArrayDesigns from that taxon (overrides -a and -b)" );
        Option taxonOption = OptionBuilder
                .create( 't' );

        this.addOption( taxonOption );
        this.addThreadsOption();
        this.addOption( blatScoreThresholdOption );
        this.addOption( blatResultOption );
    }

    @Override
    protected Exception doWork( String[] args ) {
        Exception err = this.processCommandLine( args );
        if ( err != null )
            return err;

        final Date skipIfLastRunLaterThan = this.getLimitingDate();

        if ( !this.arrayDesignsToProcess.isEmpty() ) {

            if ( this.blatResultFile != null && this.arrayDesignsToProcess.size() > 1 ) {
                throw new IllegalArgumentException(
                        "Cannot provide a blat result file when multiple arrays are being analyzed" );
            }

            for ( ArrayDesign arrayDesign : this.arrayDesignsToProcess ) {
                if ( !this.shouldRun( skipIfLastRunLaterThan, arrayDesign ) ) {
                    AbstractCLI.log.warn( arrayDesign + " was last run more recently than " + skipIfLastRunLaterThan );
                    return null;
                }

                arrayDesign = this.thaw( arrayDesign );
                Collection<BlatResult> persistedResults;
                try {
                    if ( this.blatResultFile != null ) {
                        Collection<BlatResult> blatResults = this.getBlatResultsFromFile( arrayDesign );

                        if ( blatResults == null || blatResults.size() == 0 ) {
                            throw new IllegalStateException( "No blat results in file!" );
                        }

                        AbstractCLI.log.info( "Got " + blatResults.size() + " blat records" );
                        persistedResults = arrayDesignSequenceAlignmentService
                                .processArrayDesign( arrayDesign, taxon, blatResults );
                        this.audit( arrayDesign, "BLAT results read from file: " + blatResultFile );
                        this.updateMergedOrSubsumed( arrayDesign );

                    } else {
                        // Run blat from scratch.
                        persistedResults = arrayDesignSequenceAlignmentService
                                .processArrayDesign( arrayDesign, this.sensitive );
                        this.audit( arrayDesign, "Based on a fresh alignment analysis; BLAT score threshold was "
                                + this.blatScoreThreshold + "; sensitive mode was " + this.sensitive );
                        this.updateMergedOrSubsumed( arrayDesign );
                    }
                    AbstractCLI.log.info( "Persisted " + persistedResults.size() + " results" );
                } catch ( IOException e ) {
                    this.errorObjects.add( e );
                }
            }

        } else if ( taxon != null ) {

            Collection<ArrayDesign> allArrayDesigns = arrayDesignService.findByTaxon( taxon );
            AbstractCLI.log.warn( "*** Running BLAT for all " + taxon.getCommonName() + " Array designs *** ["
                    + allArrayDesigns.size() + " items]" );

            final SecurityContext context = SecurityContextHolder.getContext();

            // split over multiple threads so we can multiplex. Put the array designs in a queue.

            /*
             * Here is our task runner.
             */
            class BlatCliConsumer extends Consumer {

                private BlatCliConsumer( BlockingQueue<ArrayDesign> q ) {
                    super( q, context );
                }

                @Override
                void consume( ArrayDesign x ) {
                    if ( !shouldRun( skipIfLastRunLaterThan, x ) ) {
                        return;
                    }
                    x = arrayDesignService.thaw( x );

                    ArrayDesignBlatCli.this.processArrayDesign( x );

                }
            }

            BlockingQueue<ArrayDesign> arrayDesigns = new ArrayBlockingQueue<>( allArrayDesigns.size() );
            arrayDesigns.addAll( allArrayDesigns );

            Collection<Thread> threads = new ArrayList<>();
            for ( int i = 0; i < this.numThreads; i++ ) {
                Consumer c1 = new BlatCliConsumer( arrayDesigns );
                Thread k = new Thread( c1 );
                threads.add( k );
                k.start();
            }

            this.waitForThreadPoolCompletion( threads );

            /*
             * All done
             */
            this.summarizeProcessing();

        } else {
            this.bail( ErrorCode.MISSING_ARGUMENT );
        }

        return null;
    }

    @Override
    protected void processOptions() {
        super.processOptions();

        if ( this.hasOption( "sensitive" ) ) {
            this.sensitive = true;
        }

        if ( this.hasOption( 'b' ) ) {
            this.blatResultFile = this.getOptionValue( 'b' );
        }

        if ( this.hasOption( AbstractCLI.THREADS_OPTION ) ) {
            this.numThreads = this.getIntegerOptionValue( "threads" );
        }

        if ( this.hasOption( 's' ) ) {
            this.blatScoreThreshold = this.getDoubleOptionValue( 's' );
        }

        TaxonService taxonService = this.getBean( TaxonService.class );

        if ( this.hasOption( 't' ) ) {
            String taxonName = this.getOptionValue( 't' );
            this.taxon = taxonService.findByCommonName( taxonName );
            if ( taxon == null ) {
                throw new IllegalArgumentException( "No taxon named " + taxonName );
            }
        }

        arrayDesignSequenceAlignmentService = this.getBean( ArrayDesignSequenceAlignmentService.class );

    }

    private void audit( ArrayDesign arrayDesign, String note ) {
        arrayDesignReportService.generateArrayDesignReport( arrayDesign.getId() );
        AuditEventType eventType = ArrayDesignSequenceAnalysisEvent.Factory.newInstance();
        auditTrailService.addUpdateEvent( arrayDesign, eventType, note );
    }

    /**
     * Process blat file which must be for one taxon.
     */
    private Collection<BlatResult> getBlatResultsFromFile( ArrayDesign arrayDesign ) throws IOException {
        Taxon arrayDesignTaxon;
        File f = new File( blatResultFile );
        if ( !f.canRead() ) {
            AbstractCLI.log.error( "Cannot read from " + blatResultFile );
            this.bail( ErrorCode.INVALID_OPTION );
        }
        // check being running for just one taxon
        arrayDesignTaxon = arrayDesignSequenceAlignmentService.validateTaxaForBlatFile( arrayDesign, taxon );

        AbstractCLI.log.info( "Reading blat results in from " + f.getAbsolutePath() );
        BlatResultParser parser = new BlatResultParser();
        parser.setScoreThreshold( this.blatScoreThreshold );
        parser.setTaxon( arrayDesignTaxon );
        parser.parse( f );
        return parser.getResults();
    }

    private void processArrayDesign( ArrayDesign design ) {

        AbstractCLI.log.info( "============== Start processing: " + design + " ==================" );
        try {
            // thawRawAndProcessed is already done.
            arrayDesignSequenceAlignmentService.processArrayDesign( design, this.sensitive );
            successObjects.add( design.getName() );
            this.audit( design, "Part of a batch job; BLAT score threshold was " + this.blatScoreThreshold );
            this.updateMergedOrSubsumed( design );

        } catch ( Exception e ) {
            errorObjects.add( design + ": " + e.getMessage() );
            AbstractCLI.log.error( "**** Exception while processing " + design + ": " + e.getMessage() + " ****" );
            AbstractCLI.log.error( e, e );
        }
    }

    /**
     * When we analyze a platform that has mergees or subsumed platforms, we can treat them as if they were analyzed as
     * well. We simply add an audit event, and update the report for the platform.
     *
     * @param design
     * @param eventType
     */
    private void updateMergedOrSubsumed( ArrayDesign design ) {
        /*
         * Update merged or subsumed platforms.
         */

        Collection<ArrayDesign> toUpdate = getRelatedDesigns( design );
        for ( ArrayDesign ad : toUpdate ) {
            log.info( "Marking subsumed or merged design as completed, updating report: " + ad );
            this.audit( ad, "Parent design was processed (merged or subsumed by this)" );
            arrayDesignReportService.generateArrayDesignReport( ad.getId() );
        }
    }

    /**
     * @param skipIfLastRunLaterThan
     * @param design
     */
    private boolean shouldRun( Date skipIfLastRunLaterThan, ArrayDesign design ) {
        if ( design.getTechnologyType().equals( TechnologyType.NONE ) ) {
            AbstractCLI.log.warn( design + " is not a microarray platform, it will not be run" );
            // not really an error, but nice to get notification.
            errorObjects.add( design + ": " + "Skipped because it is not a microarray platform." );
            return false;
        }

        if ( this.hasOption( "force" ) ) return true;

        if ( this.isSubsumedOrMerged( design ) ) {
            AbstractCLI.log.warn( design + " is subsumed or merged into another design, it will not be run; instead process the 'parent' platform" );

            // not really an error, but nice to get notification.
            errorObjects.add( design + ": " + "Skipped because it is subsumed by or merged into another design." );
            return false;
        }

        if ( !this.needToRun( skipIfLastRunLaterThan, design, ArrayDesignSequenceAnalysisEvent.class ) ) {
            if ( skipIfLastRunLaterThan != null ) {
                AbstractCLI.log.warn( design + " was last run more recently than " + skipIfLastRunLaterThan );
                errorObjects.add( design + ": " + "Skipped because it was last run after " + skipIfLastRunLaterThan );
            } else {
                AbstractCLI.log.warn( design + " seems to be up to date or is not ready to run" );
                errorObjects.add( design + " seems to be up to date or is not ready to run" );
            }
            return false;
        }
        return true;
    }

}
