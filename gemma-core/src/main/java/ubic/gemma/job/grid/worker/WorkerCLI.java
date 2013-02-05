/*
 * The Gemma projec
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
package ubic.gemma.job.grid.worker;

import org.quartz.impl.StdScheduler;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import ubic.gemma.job.RemoteTaskRunningService;
import ubic.gemma.util.AbstractSpringAwareCLI;
import ubic.gemma.util.QuartzUtils;
import ubic.gemma.util.SpringContextUtil;

/**
 * Generic tool for starting a remote worker.
 *
 * @author keshav
 * @version $Id$
 */
public class WorkerCLI extends AbstractSpringAwareCLI {

    private JmsTemplate amqWorkerJmsTemplate;
    private RemoteTaskRunningService taskRunningService;

    public class ShutdownHook extends Thread {
        @Override
        public void run() {
            log.info( "Remote task executor is shutting down...");
            log.info( "Attempting to cancel all running tasks...");
            taskRunningService.shutdown();
            log.info( "Shutdown sequence completed.");
        }
    }

    public static void main( String[] args ) {
        WorkerCLI me = new WorkerCLI();
        me.doWork(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractSpringAwareCLI#getShortDesc()
     */
    @Override
    public String getShortDesc() {
        return "Start worker application for processing tasks sent by Gemma webapp.";
    }

    @SuppressWarnings("static-access")
    @Override
    protected void buildOptions() {
//        Option mmtxOption = OptionBuilder.withDescription( "Set to force MMTX to be initialized" ).create( "mmtx" );
//        super.addOption( mmtxOption );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractCLI#doWork(java.lang.String[])
     */
    @Override
    protected Exception doWork( String[] args ) {
        Exception commandArgumentErrors = processCommandLine( this.getClass().getName(), args );
        if ( commandArgumentErrors != null ) {
            return commandArgumentErrors;
        }

        try {
            init();
        } catch ( Exception e ) {
            log.error( e, e );
        }
        return commandArgumentErrors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractSpringAwareCLI#processOptions()
     */
    @Override
    protected void processOptions() {
        super.processOptions();
        //FIXME
//        if ( this.hasOption( "mmtx" ) ) {
//            ExpressionExperimentAnnotator eeAnnotator = this.getBean( ExpressionExperimentAnnotator.class );
//            eeAnnotator.init();
//        }
    }

    /**
     * Adds a shutdown hook.
     * 
     * @throws Exception
     */
    private void init() throws Exception {
        ShutdownHook shutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook( shutdownHook );

        amqWorkerJmsTemplate = ctx.getBean( JmsTemplate.class );
        taskRunningService = ctx.getBean( RemoteTaskRunningService.class );
//        startHeartbeatThread();
    }

    @Override
    protected void createSpringContext() {
        ctx = SpringContextUtil.getRemoteWorkerApplicationContext( hasOption( "testing" ) );

        try {
            QuartzUtils.disableQuartzScheduler( this.getBean( StdScheduler.class ) );
        } catch ( NoSuchBeanDefinitionException exception) {
            //This is ok. I've removed quartz from worker/cli context.
            log.info( exception.getMessage() );
        }

        /*
         * Important to ensure that threads get permissions from their context - not global!
         */
        SecurityContextHolder.setStrategyName( SecurityContextHolder.MODE_INHERITABLETHREADLOCAL );
    }
}
