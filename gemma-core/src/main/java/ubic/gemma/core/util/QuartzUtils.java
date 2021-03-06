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
package ubic.gemma.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.impl.StdScheduler;

/**
 * @author keshav
 */
public class QuartzUtils {
    private static final Log log = LogFactory.getLog( QuartzUtils.class );

    /**
     * Turn off a scheduler.
     *
     * @param stdScheduler the scheduler
     */
    public static void disableQuartzScheduler( StdScheduler stdScheduler ) {

        QuartzUtils.log.debug( "Shutting down quartz" );
        try {
            stdScheduler.shutdown( true );
            if ( stdScheduler.isShutdown() ) {
                QuartzUtils.log.debug( "Scheduler shutdown successful" );
            } else {
                QuartzUtils.log.warn( "Scheduler could not be shutdown for some reason" );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Cannot shutdown quartz. Error is: " + e );
        }
    }
}
