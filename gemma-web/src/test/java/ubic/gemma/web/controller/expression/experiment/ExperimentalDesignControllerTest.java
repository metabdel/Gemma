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
package ubic.gemma.web.controller.expression.experiment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import ubic.gemma.model.expression.experiment.ExperimentalDesign;
import ubic.gemma.model.expression.experiment.ExperimentalFactorValueObject;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.testing.BaseSpringWebTest;
import ubic.gemma.web.remote.EntityDelegator;

/**
 * @author Kiran Keshav
 * @version $Id$
 */
public class ExperimentalDesignControllerTest extends BaseSpringWebTest {

    @Autowired
    private ExperimentalDesignController experimentalDesignController;

    /**
     * Tests showing an experimentalDesign which is implemented in
     * {@link ubic.gemma.model.web.controller.expression.experiment.ExpressionExperimentController} in method
     * {@link #handleRequest(HttpServletRequest request, HttpServletResponse response)}.
     * 
     * @throws Exception
     */
    @Test
    public void testShowExperimentalDesign() throws Exception {

        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperiment( true ); // readonly

        MockHttpServletRequest req = super.newGet( "/experimentalDesign/showExperimentalDesign.html" );

        ExperimentalDesign ed = ee.getExperimentalDesign();

        assertTrue( ed != null && ee.getId() != null );

        req.addParameter( "name", "Experimental Design 0" );

        req.addParameter( "eeid", String.valueOf( ee.getId() ) );

        req.setRequestURI( "/experimentalDesign/showExperimentalDesign.html" );

        ModelAndView mav = experimentalDesignController.show( req, ( HttpServletResponse ) null );

        Map<String, Object> m = mav.getModel();
        assertNotNull( m.get( "expressionExperiment" ) );

        assertEquals( mav.getViewName(), "experimentalDesign.detail" );

    }

    @Test
    public void testGetExperimentalFactors() throws Exception {

        ExpressionExperiment ee = this.getTestPersistentCompleteExpressionExperiment( true ); // readonly

        Collection<ExperimentalFactorValueObject> experimentalFactors = experimentalDesignController
                .getExperimentalFactors( new EntityDelegator( ee.getExperimentalDesign() ) );
        assertTrue( !experimentalFactors.isEmpty() );

    }
}
