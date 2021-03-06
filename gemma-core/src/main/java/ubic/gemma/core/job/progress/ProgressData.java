/* Copyright (c) 2006 University of British Columbia
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

package ubic.gemma.core.job.progress;

import java.io.Serializable;

/**
 * @author klc
 */
@SuppressWarnings("unused") // Possible external use
public class ProgressData implements Serializable {

    private static final long serialVersionUID = -4303625064082352461L;

    private int percent = 0;
    private String description = "";
    private boolean done = false;
    private String forwardingURL;
    private String taskId;
    private boolean failed = false;

    @SuppressWarnings("WeakerAccess") // dwr doesn't work right without blank constructor
    public ProgressData() {
    }

    public ProgressData( String taskId, int per, String description ) {
        this( taskId, per, description, false );
    }

    /**
     * @param per         int value of percent
     * @param description string a description of the progress
     * @param finished    finished
     * @param taskId      task id
     */
    public ProgressData( String taskId, int per, String description, boolean finished ) {
        percent = per;
        this.taskId = taskId;
        this.description = description;
        done = finished;
    }

    public String getDescription() {
        return description;
    }

    /**
     * @param description string a description of the progress
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * @return the forwardingURL
     */
    public String getForwardingURL() {
        return forwardingURL;
    }

    /**
     * @param forwardingURL the forwardingURL to set
     */
    public void setForwardingURL( String forwardingURL ) {
        this.forwardingURL = forwardingURL;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent( int percent ) {
        this.percent = percent;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return this.taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId( String taskId ) {
        this.taskId = taskId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        this.done = done;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed( boolean failed ) {
        this.failed = failed;
    }

}
