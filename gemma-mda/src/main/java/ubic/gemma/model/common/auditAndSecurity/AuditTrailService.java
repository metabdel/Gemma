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

import java.util.Collection;
import java.util.Map;

/**
 * 
 */
public interface AuditTrailService {

    /**
     * 
     */
    public void addComment( ubic.gemma.model.common.Auditable auditable, java.lang.String comment,
            java.lang.String detail );

    /**
     * 
     */
    public void addOkFlag( ubic.gemma.model.common.Auditable auditable, java.lang.String comment,
            java.lang.String detail );

    /**
     * 
     */
    public void addTroubleFlag( ubic.gemma.model.common.Auditable auditable, java.lang.String comment,
            java.lang.String detail );

    /**
     * <p>
     * Add an update event defined by the given parameters, to the given auditable. Returns the generated event.
     * </p>
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditEvent addUpdateEvent(
            ubic.gemma.model.common.Auditable auditable, java.lang.String note );

    /**
     * 
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditEvent addUpdateEvent(
            ubic.gemma.model.common.Auditable auditable,
            ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType auditEventType, java.lang.String note );

    /**
     * 
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditEvent addUpdateEvent(
            ubic.gemma.model.common.Auditable auditable,
            ubic.gemma.model.common.auditAndSecurity.eventType.AuditEventType auditEventType, java.lang.String note,
            java.lang.String detail );

    /**
     * 
     */
    public void addValidatedFlag( ubic.gemma.model.common.Auditable auditable, java.lang.String comment,
            java.lang.String detail );

    /**
     * 
     */
    public void audit( ubic.gemma.model.common.Auditable entity,
            ubic.gemma.model.common.auditAndSecurity.AuditEvent auditEvent );

    /**
     * 
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditTrail create(
            ubic.gemma.model.common.auditAndSecurity.AuditTrail auditTrail );

    /**
     * <p>
     * Return the last 'trouble' event (if any), if it was after the last 'ok' or 'validated' event (if any). Return
     * null otherwise (indicating there is no trouble).
     * </p>
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditEvent getLastTroubleEvent(
            ubic.gemma.model.common.Auditable auditable );

    /**
     * <p>
     * Return the last validation event (if any).
     * </p>
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditEvent getLastValidationEvent(
            ubic.gemma.model.common.Auditable auditable );

    /**
     * 
     */
    public void thaw( ubic.gemma.model.common.Auditable auditable );

    /**
     * <p>
     * Thaws the given audit trail to prevent lazy load errors
     * </p>
     */
    public void thaw( ubic.gemma.model.common.auditAndSecurity.AuditTrail auditTrail );

}
