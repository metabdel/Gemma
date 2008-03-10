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
package ubic.gemma.apps;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.StopWatch;

import ubic.gemma.analysis.expression.coexpression.links.LinkAnalysisConfig;
import ubic.gemma.analysis.expression.coexpression.links.LinkAnalysisService;
import ubic.gemma.analysis.preprocess.InsufficientProbesException;
import ubic.gemma.analysis.preprocess.filter.FilterConfig;
import ubic.gemma.analysis.preprocess.filter.InsufficientSamplesException;
import ubic.gemma.analysis.report.ExpressionExperimentReportService;
import ubic.gemma.model.common.auditAndSecurity.AuditTrailService;
import ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType;
import ubic.gemma.model.common.auditAndSecurity.eventType.FailedLinkAnalysisEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.LinkAnalysisEvent;
import ubic.gemma.model.common.auditAndSecurity.eventType.TooSmallDatasetLinkAnalysisEvent;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * Commandline tool to conduct link analysis
 * 
 * @author xiangwan
 * @author paul (refactoring)
 * @version $Id$
 */
public class LinkAnalysisCli extends ExpressionExperimentManipulatingCLI {

    @Override
    public String getShortDesc() {
        return "Analyze expression data sets for coexpressed genes";
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        LinkAnalysisCli analysis = new LinkAnalysisCli();
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Exception ex = analysis.doWork( args );
            if ( ex != null ) {
                ex.printStackTrace();
            }
            watch.stop();
            log.info( "Elapsed time: " + watch.getTime() / 1000 + " seconds" );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private ExpressionExperimentReportService expressionExperimentReportService;

    private LinkAnalysisService linkAnalysisService;

    private FilterConfig filterConfig = new FilterConfig();

    private LinkAnalysisConfig linkAnalysisConfig = new LinkAnalysisConfig();

    @SuppressWarnings("static-access")
    @Override
    protected void buildOptions() {
        super.buildOptions();

        super.addDateOption();

        Option geneFileOption = OptionBuilder.hasArg().withArgName( "dataSet" ).withDescription(
                "Short name of the expression experiment to analyze (default is to analyze all found in the database)" )
                .withLongOpt( "dataSet" ).create( 'e' );
        addOption( geneFileOption );

        Option cdfCut = OptionBuilder.hasArg().withArgName( "Tolerance Thresold" ).withDescription(
                "The tolerance threshold for coefficient value" ).withLongOpt( "cdfcut" ).create( 'c' );
        addOption( cdfCut );

        Option tooSmallToKeep = OptionBuilder.hasArg().withArgName( "Cache Threshold" ).withDescription(
                "The threshold for coefficient cache" ).withLongOpt( "cachecut" ).create( 'k' );
        addOption( tooSmallToKeep );

        Option fwe = OptionBuilder.hasArg().withArgName( "Family Wise Error Rate" ).withDescription(
                "The setting for family wise error control" ).withLongOpt( "fwe" ).create( 'w' );
        addOption( fwe );

        buildFilterConfigOptions();

        Option absoluteValue = OptionBuilder.withDescription( "If using absolute value in expression file" )
                .withLongOpt( "abs" ).create( 'a' );
        addOption( absoluteValue );

        Option useDB = OptionBuilder.withDescription( "Don't save the results in the database (i.e., testing)" )
                .withLongOpt( "nodb" ).create( 'd' );
        addOption( useDB );

        Option textOutOpt = OptionBuilder.withDescription( "Output links as text to STOUT" ).create( "text" );
        addOption( textOutOpt );

        Option metricOption = OptionBuilder.hasArg().withArgName( "metric" ).withDescription(
                "Similarity metric {pearson|spearman}, default is pearson" ).create( "metric" );
        addOption( metricOption );

        Option imagesOption = OptionBuilder.withDescription( "Suppress the generation of correlation matrix images" )
                .create( "noimages" );
        addOption( imagesOption );

        addForceOption();
        addAutoOption();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Exception doWork( String[] args ) {
        Exception err = processCommandLine( "Link Analysis Data Loader", args );
        if ( err != null ) {
            return err;
        }

        this.linkAnalysisService = ( LinkAnalysisService ) this.getBean( "linkAnalysisService" );

        for ( ExpressionExperiment ee : expressionExperiments ) {
            processExperiment( ee );
        }

        summarizeProcessing();
        return null;
    }

    /**
     * @param ee
     */
    private void processExperiment( ExpressionExperiment ee ) {
        eeService.thawLite( ee );

        if ( !force && !needToRun( ee, LinkAnalysisEvent.class ) ) {
            return;
        }

        try {
            linkAnalysisService.process( ee, filterConfig, linkAnalysisConfig );
            successObjects.add( ee.toString() );
            if ( !linkAnalysisConfig.isTextOut() && linkAnalysisConfig.isUseDb() ) {
                audit( ee, "", LinkAnalysisEvent.Factory.newInstance() );
            }
        } catch ( Exception e ) {
            errorObjects.add( ee + ": " + e.getMessage() );
            logFailure( ee, e );
            log.error( "**** Exception while processing " + ee + ": " + e.getMessage() + " ********" );
            log.error( e, e );
        }
    }

    @Override
    protected void processOptions() {
        super.processOptions();

        if ( hasOption( 'c' ) ) {
            this.linkAnalysisConfig.setCdfCut( Double.parseDouble( getOptionValue( 'c' ) ) );
        }
        if ( hasOption( 'k' ) ) {
            this.linkAnalysisConfig.setCorrelationCacheThreshold( Double.parseDouble( getOptionValue( 'k' ) ) );
        }
        if ( hasOption( 'w' ) ) {
            this.linkAnalysisConfig.setFwe( Double.parseDouble( getOptionValue( 'w' ) ) );
        }

        getFilterConfigOptions();

        if ( hasOption( 'a' ) ) {
            this.linkAnalysisConfig.setAbsoluteValue( true );
        }
        if ( hasOption( 'd' ) ) {
            this.linkAnalysisConfig.setUseDb( false );
        }
        if ( hasOption( "metric" ) ) {
            this.linkAnalysisConfig.setMetric( getOptionValue( "metric" ) );
        }
        if ( hasOption( "text" ) ) {
            this.linkAnalysisConfig.setTextOut( true );
        }

        if ( hasOption( "knownGenesOnly" ) ) {
            linkAnalysisConfig.setKnownGenesOnly( true );
        }
        if ( hasOption( "force" ) ) {
            this.force = true;
        }
        if ( hasOption( "noimages" ) ) {
            linkAnalysisConfig.setMakeSampleCorrMatImages( false );
        }

        this.expressionExperimentReportService = ( ExpressionExperimentReportService ) this
                .getBean( "expressionExperimentReportService" );
        this.auditTrailService = ( AuditTrailService ) this.getBean( "auditTrailService" );
    }

    /**
     * @param arrayDesign
     */
    private void audit( ExpressionExperiment ee, String note, AuditEventType eventType ) {
        if ( linkAnalysisConfig.isUseDb() ) {
            expressionExperimentReportService.generateSummaryObject( ee.getId() );
            auditTrailService.addUpdateEvent( ee, eventType, note );
        }
    }

    @SuppressWarnings("static-access")
    private void buildFilterConfigOptions() {
        Option minPresentFraction = OptionBuilder.hasArg().withArgName( "Missing Value Threshold" ).withDescription(
                "Fraction of data points that must be present in a profile to be retained , default="
                        + FilterConfig.DEFAULT_MINPRESENT_FRACTION ).withLongOpt( "missingcut" ).create( 'm' );
        addOption( minPresentFraction );

        Option lowExpressionCut = OptionBuilder.hasArg().withArgName( "Expression Threshold" ).withDescription(
                "Fraction of expression vectors to reject based on low values, default="
                        + FilterConfig.DEFAULT_LOWEXPRESSIONCUT ).withLongOpt( "lowcut" ).create( 'l' );
        addOption( lowExpressionCut );

        Option lowVarianceCut = OptionBuilder.hasArg().withArgName( "Variance Threshold" ).withDescription(
                "Fraction of expression vectors to reject based on low variance (or coefficient of variation), default="
                        + FilterConfig.DEFAULT_LOWVARIANCECUT ).withLongOpt( "lowvarcut" ).create( "lv" );
        addOption( lowVarianceCut );

        Option knownGenesOnlyOption = OptionBuilder.withDescription(
                "Only save (or print) results for links between 'known genes'" ).create( "knownGenesOnly" );
        addOption( knownGenesOnlyOption );

    }

    private void getFilterConfigOptions() {
        if ( hasOption( 'm' ) ) {
            filterConfig.setMinPresentFraction( Double.parseDouble( getOptionValue( 'm' ) ) );
        }
        if ( hasOption( 'l' ) ) {
            filterConfig.setLowExpressionCut( Double.parseDouble( getOptionValue( 'l' ) ) );
        }
        if ( hasOption( "lv" ) ) {
            filterConfig.setLowVarianceCut( Double.parseDouble( getOptionValue( "lv" ) ) );
        }

    }

    /**
     * @param expressionExperiment
     * @param e
     */
    private void logFailure( ExpressionExperiment expressionExperiment, Exception e ) {
        if ( e instanceof InsufficientSamplesException ) {
            audit( expressionExperiment, e.getMessage(), TooSmallDatasetLinkAnalysisEvent.Factory.newInstance() );
        } else if ( e instanceof InsufficientProbesException ) {
            audit( expressionExperiment, e.getMessage(), TooSmallDatasetLinkAnalysisEvent.Factory.newInstance() );
        } else {
            audit( expressionExperiment, ExceptionUtils.getFullStackTrace( e ), FailedLinkAnalysisEvent.Factory
                    .newInstance() );
        }
    }
}
