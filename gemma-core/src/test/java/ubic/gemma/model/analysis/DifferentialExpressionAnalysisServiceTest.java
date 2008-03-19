/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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

package ubic.gemma.model.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisService;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author klc
 * @version $Id$
 */
public class DifferentialExpressionAnalysisServiceTest extends BaseSpringContextTest {

    private DifferentialExpressionAnalysisService analysisService;
    private ExpressionExperimentService expressionExperimentService;

    // Test Data
    DifferentialExpressionAnalysis eAnalysis1;
    DifferentialExpressionAnalysis eAnalysis2;
    DifferentialExpressionAnalysis eAnalysis3;
    DifferentialExpressionAnalysis eAnalysis4;

    ExpressionExperiment e1;
    ExpressionExperiment e2;
    ExpressionExperiment e3;

    public void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();

        this.analysisService = ( DifferentialExpressionAnalysisService ) getBean( "differentialExpressionAnalysisService" );

        e1 = ExpressionExperiment.Factory.newInstance();
        e1.setName( "test e1" );
        e1 = expressionExperimentService.create( e1 );

        e2 = ExpressionExperiment.Factory.newInstance();
        e2.setName( "test e2" );
        e2 = expressionExperimentService.create( e2 );

        e3 = ExpressionExperiment.Factory.newInstance();
        e3.setName( "test e3" );
        e3 = expressionExperimentService.create( e3 );

        Collection<ExpressionExperiment> investigations = new HashSet<ExpressionExperiment>();

        eAnalysis1 = DifferentialExpressionAnalysis.Factory.newInstance();
        investigations.add( e1 );
        eAnalysis1.setExperimentsAnalyzed( investigations );
        eAnalysis1.setName( "TestAnalysis1" );
        eAnalysis1.setDescription( "An analysis Test 1" );
        eAnalysis1 = analysisService.create( eAnalysis1 );

        eAnalysis2 = DifferentialExpressionAnalysis.Factory.newInstance();
        investigations = new HashSet<ExpressionExperiment>();
        investigations.add( e1 );
        investigations.add( e2 );
        eAnalysis2.setExperimentsAnalyzed( investigations );
        eAnalysis2.setName( "TestAnalysis2" );
        eAnalysis2.setDescription( "An analysis Test 2" );
        eAnalysis2 = analysisService.create( eAnalysis2 );

        eAnalysis4 = DifferentialExpressionAnalysis.Factory.newInstance();
        investigations = new HashSet<ExpressionExperiment>();
        investigations.add( e1 );
        investigations.add( e2 );
        investigations.add( e3 );

        eAnalysis4.setExperimentsAnalyzed( investigations );
        eAnalysis4.setName( "Test" );
        eAnalysis4.setDescription( "An analysis Test 4" );
        eAnalysis4 = analysisService.create( eAnalysis4 );

        eAnalysis3 = DifferentialExpressionAnalysis.Factory.newInstance();
        investigations = new HashSet<ExpressionExperiment>();
        investigations.add( e1 );
        investigations.add( e2 );
        investigations.add( e3 );

        eAnalysis3.setExperimentsAnalyzed( investigations );
        eAnalysis3.setName( "TestAnalysis3" );
        eAnalysis3.setDescription( "An analysis Test 3" );
        eAnalysis3 = analysisService.create( eAnalysis3 );

    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void testFindByInvestigations() {
        Collection<ExpressionExperiment> investigations = new ArrayList<ExpressionExperiment>();
        investigations.add( e1 );
        investigations.add( e2 );

        Map<ExpressionExperiment, Collection<DifferentialExpressionAnalysis>> results = analysisService
                .findByInvestigations( investigations );
        assertEquals( 2, results.keySet().size() );

        assertEquals( 4, results.get( e1 ).size() );
        assertEquals( 3, results.get( e2 ).size() );
    }

    /**
     * 
     */
    public void testFindByInvestigation() {

        Collection results = analysisService.findByInvestigation( e1 );
        assertEquals( 4, results.size() );

        results = analysisService.findByInvestigation( e2 );
        assertEquals( 3, results.size() );

        results = analysisService.findByInvestigation( e3 );
        assertEquals( 2, results.size() );

    }

    /**
     * 
     */
    public void testFindByUniqueInvestigations() {
        Collection<Investigation> investigations = new ArrayList<Investigation>();
        investigations.add( e1 );
        investigations.add( e2 );

        Analysis results = analysisService.findByUniqueInvestigations( investigations );
        assertEquals( eAnalysis2.getId(), results.getId() );

    }

    public void testFindByNameExact() {

        Analysis result = analysisService.findByName( "Test" );
        assertEquals( "Test", result.getName() );
    }

    public void testFindByNameRecent() {

        Analysis result = analysisService.findByName( "TestA" );
        assertEquals( "TestAnalysis3", result.getName() );
    }

    /**
     * @param ees the expressionExperimentService to set
     */
    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

}
