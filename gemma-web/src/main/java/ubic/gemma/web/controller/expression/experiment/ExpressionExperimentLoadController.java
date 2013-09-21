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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ubic.gemma.job.executor.webapp.TaskRunningService;
import ubic.gemma.tasks.analysis.expression.ExpressionExperimentLoadTaskCommand;

/**
 * Handles loading of Expression data into the system when the source is GEO or ArrayExpress, via Spring MVC or AJAX,
 * either in the webapp or in a javaspaces grid. The choice depends on how the system and client is configured.
 * 
 * @author pavlidis
 * @author keshav
 * @version $Id$
 * @see ubic.gemma.web.controller.expression.experiment.ExpressionDataFileUploadController for how flat-file data is
 *      loaded.
 */
@Controller
public class ExpressionExperimentLoadController {

    @Autowired
    private TaskRunningService taskRunningService;

    public ExpressionExperimentLoadController() {
        super();
    }

    /**
     * Main entry point for AJAX calls.
     * 
     * @param command
     * @return
     */
    public String load( ExpressionExperimentLoadTaskCommand command ) {
        // remove stray whitespace.
        command.setAccession( StringUtils.strip( command.getAccession() ) );

        if ( StringUtils.isBlank( command.getAccession() ) ) {
            throw new IllegalArgumentException( "Must provide an accession" );
        }

        return taskRunningService.submitRemoteTask( command );
    }

    @RequestMapping("/admin/loadExpressionExperiment.html")
    @SuppressWarnings("unused")
    public ModelAndView show( HttpServletRequest request, HttpServletResponse response ) {
        return new ModelAndView( "/admin/loadExpressionExperimentForm" );
    }
}
