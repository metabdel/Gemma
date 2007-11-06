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
package ubic.gemma.analysis.diff;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.analysis.Analysis;
import ubic.gemma.model.expression.analysis.ExpressionAnalysis;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;

/**
 * A spring loaded differential expression service to run the differential expression analysis (and persist the results
 * using the appropriate data access objects).
 * 
 * @spring.bean id="differentialExpressionAnalysisService"
 * @spring.property name="expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name="differentialExpressionAnalysis" ref="differentialExpressionAnalysis"
 * @author keshav
 * @version $Id$
 */
public class DifferentialExpressionAnalysisService {

    private Log log = LogFactory.getLog( this.getClass() );
    ExpressionExperimentService expressionExperimentService = null;

    DifferentialExpressionAnalysis differentialExpressionAnalysis = null;

    /**
     * Finds the persistent expression experiment. If there are no associated analyses with this experiment, the
     * differential expression analysis is first run, the analysis is persisted and then returned.
     * 
     * @param expressionExperiment
     * @return
     */
    public Collection<Analysis> getPersistentAnalyses( ExpressionExperiment expressionExperiment ) {

        Collection<Analysis> analyses = expressionExperiment.getAnalyses();

        if ( analyses == null || analyses.isEmpty() ) {
            log
                    .warn( "Experiment "
                            + expressionExperiment.getShortName()
                            + " does not have any associated analyses.  Running differenial expression analysis and persisting results.  This may take some time." );

            analyses = new HashSet<Analysis>();

            differentialExpressionAnalysis.analyze( expressionExperiment );

            ExpressionAnalysis expressionAnalysis = differentialExpressionAnalysis.getExpressionAnalysis();

            Collection<ExpressionExperiment> experimentsAnalyzed = new HashSet<ExpressionExperiment>();
            experimentsAnalyzed.add( expressionExperiment );

            expressionAnalysis.setExperimentsAnalyzed( experimentsAnalyzed );

            analyses.add( expressionAnalysis );

            expressionExperiment.setAnalyses( analyses );

            expressionExperimentService.update( expressionExperiment );

        }

        return analyses;
    }

    /**
     * Finds the persistent expression experiment by the shortName and returns the analyses. If the expression
     * experiment does not exist, returns null.
     * 
     * @param shortName
     * @return
     */
    public Collection<Analysis> getPersistentAnalyses( String shortName ) {

        ExpressionExperiment ee = expressionExperimentService.findByShortName( shortName );

        if ( ee == null ) return null;

        return this.getPersistentAnalyses( ee );

    }

    /**
     * @param expressionExperimentService
     */
    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    /**
     * @param differentialExpressionAnalysis
     */
    public void setDifferentialExpressionAnalysis( DifferentialExpressionAnalysis differentialExpressionAnalysis ) {
        this.differentialExpressionAnalysis = differentialExpressionAnalysis;
    }
}
