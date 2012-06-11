/*
 * The Gemma project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.gemma.analysis.expression.coexpression.links;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ubic.gemma.analysis.expression.coexpression.ProbeLinkCoexpressionAnalyzer;
import ubic.gemma.analysis.expression.coexpression.links.LinkAnalysisConfig.SingularThreshold;
import ubic.gemma.analysis.preprocess.ProcessedExpressionDataVectorCreateService;
import ubic.gemma.analysis.preprocess.filter.FilterConfig;
import ubic.gemma.genome.gene.service.GeneService;
import ubic.gemma.model.analysis.expression.coexpression.CoexpressionCollectionValueObject;
import ubic.gemma.model.analysis.expression.coexpression.CoexpressionProbe;
import ubic.gemma.model.analysis.expression.coexpression.CoexpressionValueObject;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.persistence.TableMaintenanceUtilImpl;
import ubic.gemma.persistence.TableMaintenenceUtil;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author paul
 * @version $Id$
 */
public class LinkAnalysisServiceTest extends BaseSpringContextTest {

    private FilterConfig filterConfig = new FilterConfig();

    private LinkAnalysisConfig linkAnalysisConfig = new LinkAnalysisConfig();

    @Autowired
    private LinkAnalysisService linkAnalysisService;

    @Autowired
    private ProcessedExpressionDataVectorCreateService processedExpressionDataVectorCreateService;

    @Autowired
    private ProbeLinkCoexpressionAnalyzer probeLinkCoexpressionAnalyzer;

    @Autowired
    private TableMaintenenceUtil tableMaintenenceUtil;

    @Autowired
    private GeneService geneService;

    @Before
    public void setup() {
        super.setTestCollectionSize( 100 );
    }

    @After
    public void tearDown() {
        super.resetTestCollectionSize();
    }

    @Test
    public void testProcess() {
        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperimentWithSequences();
        processedExpressionDataVectorCreateService.computeProcessedExpressionData( ee );
        linkAnalysisConfig.setCdfCut( 0.1 );
        linkAnalysisConfig.setSingularThreshold( SingularThreshold.cdfcut );
        linkAnalysisConfig.setProbeDegreeThreshold( 25 );
        LinkAnalysis result = linkAnalysisService.process( ee.getId(), filterConfig, linkAnalysisConfig );

        Collection<CoexpressionProbe> probesUsed = result.getAnalysisObj().getProbesUsed();
        assertEquals( 132, probesUsed.size() );

        for ( CoexpressionProbe cp : probesUsed ) {
            assertNotNull( cp.getNodeDegree() );
            assertNotNull( cp.getNodeDegreeRank() );
            assertNotNull( cp.getProbe() );
        }

    }

    @Test
    public void testLoadAnalyzeSaveAndCoexpSearch() {
        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperimentWithSequences();
        processedExpressionDataVectorCreateService.computeProcessedExpressionData( ee );
        linkAnalysisConfig.setCdfCut( 0.1 );
        linkAnalysisConfig.setSingularThreshold( SingularThreshold.cdfcut );
        linkAnalysisConfig.setProbeDegreeThreshold( 25 );

        ExpressionExperiment eeTemp = linkAnalysisService.loadDataForAnalysis( ee.getId() );
        LinkAnalysis la = linkAnalysisService.doAnalysis( eeTemp, linkAnalysisConfig, filterConfig );
        linkAnalysisService.saveResults( eeTemp, la, linkAnalysisConfig, filterConfig );

        Collection<CoexpressionProbe> probesUsed = la.getAnalysisObj().getProbesUsed();
        assertEquals( 132, probesUsed.size() );

        for ( CoexpressionProbe cp : probesUsed ) {
            assertNotNull( cp.getNodeDegree() );
            assertNotNull( cp.getNodeDegreeRank() );
            assertNotNull( cp.getProbe() );
        }
        ( ( TableMaintenanceUtilImpl ) tableMaintenenceUtil ).disableEmail();
        tableMaintenenceUtil.updateGene2CsEntries();
        Collection<ExpressionExperiment> ees = new HashSet<ExpressionExperiment>();
        ees.add( ee );
        boolean foundOne = false;
        for ( Gene gene : geneService.loadAll() ) {

            CoexpressionCollectionValueObject links = probeLinkCoexpressionAnalyzer.linkAnalysis( gene, ees, 1, 100 );

            List<CoexpressionValueObject> coexps = links.getAllGeneCoexpressionData( 0 );

            if ( coexps.isEmpty() ) {
                continue;
            }

            log.info( coexps.size() + " hits for " + gene );
            for ( CoexpressionValueObject coex : coexps ) {
                log.info( coex );
            }
            foundOne = true;
            break;
        }

        assertTrue( foundOne );
    }

}
