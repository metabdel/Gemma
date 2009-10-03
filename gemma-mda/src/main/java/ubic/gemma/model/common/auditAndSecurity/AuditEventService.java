/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2007 University of British Columbia
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
package ubic.gemma.model.common.auditAndSecurity;

/**
 * 
 */
public interface AuditEventService {

    /**
     * <p>
     * Returns a collection of Auditables created since the date given.
     * </p>
     */
    public java.util.Collection getNewSinceDate( java.util.Date date );

    /**
     * <p>
     * Returns a collection of Auditable objects that were updated since the date entered.
     * </p>
     */
    public java.util.Collection getUpdatedSinceDate( java.util.Date date );

    /**
     * 
     */
    public void thaw( ubic.gemma.model.common.auditAndSecurity.AuditEvent auditEvent );

}
