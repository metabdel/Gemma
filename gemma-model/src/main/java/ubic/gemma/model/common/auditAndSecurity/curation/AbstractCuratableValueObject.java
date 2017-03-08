package ubic.gemma.model.common.auditAndSecurity.curation;

import ubic.gemma.model.common.auditAndSecurity.AuditEvent;

import java.util.Date;

/**
 * Created by tesarst on 07/03/17.
 *
 * Abstract curatable value object that provides variables and methods for data stored in CurationDetails objects on
 * curatable objects.
 */
public abstract class AbstractCuratableValueObject {

    protected Date lastUpdated;

    protected Boolean troubled;

    protected AuditEvent lastTroubledEvent;

    protected Boolean needsAttention;

    protected AuditEvent lastNeedsAttentionEvent;

    protected String curationNote;

    protected AuditEvent lastCurationNoteEvent;

    public AbstractCuratableValueObject() {
    }

    public AbstractCuratableValueObject( Date lastUpdated, Boolean troubled, AuditEvent lastTroubledEvent,
            Boolean needsAttention, AuditEvent lastNeedsAttentionEvent, String curationNote,
            AuditEvent lastCurationNoteEvent ) {
        this.lastUpdated = lastUpdated;
        this.troubled = troubled;
        this.lastTroubledEvent = lastTroubledEvent;
        this.needsAttention = needsAttention;
        this.lastNeedsAttentionEvent = lastNeedsAttentionEvent;
        this.curationNote = curationNote;
        this.lastCurationNoteEvent = lastCurationNoteEvent;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated ) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getTroubled() {
        return troubled;
    }

    public void setTroubled( Boolean troubled ) {
        this.troubled = troubled;
    }

    public AuditEvent getLastTroubledEvent() {
        return lastTroubledEvent;
    }

    public void setLastTroubledEvent( AuditEvent lastTroubledEvent ) {
        this.lastTroubledEvent = lastTroubledEvent;
    }

    public Boolean getNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention( Boolean needsAttention ) {
        this.needsAttention = needsAttention;
    }

    public AuditEvent getLastNeedsAttentionEvent() {
        return lastNeedsAttentionEvent;
    }

    public void setLastNeedsAttentionEvent( AuditEvent lastNeedsAttentionEvent ) {
        this.lastNeedsAttentionEvent = lastNeedsAttentionEvent;
    }

    public String getCurationNote() {
        return curationNote;
    }

    public void setCurationNote( String curationNote ) {
        this.curationNote = curationNote;
    }

    public AuditEvent getLastCurationNoteEvent() {
        return lastCurationNoteEvent;
    }

    public void setLastCurationNoteEvent( AuditEvent lastCurationNoteEvent ) {
        this.lastCurationNoteEvent = lastCurationNoteEvent;
    }
}
