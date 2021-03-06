/*
 * The Gemma project
 *
 * Copyright (c) 2011 University of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.gemma.model.common.description;

import ubic.gemma.model.IdentifiableValueObject;

import java.io.Serializable;

/**
 * ValueObject for database entry
 */
@SuppressWarnings("WeakerAccess") // Used in frontend
public class DatabaseEntryValueObject extends IdentifiableValueObject<DatabaseEntry> implements Serializable {

    private static final long serialVersionUID = -527323410580090L;
    private String accession;
    private ExternalDatabaseValueObject externalDatabase;

    public DatabaseEntryValueObject( DatabaseEntry de ) {
        super( de.getId() );
        this.accession = de.getAccession();
        this.externalDatabase =
                de.getExternalDatabase() != null ? new ExternalDatabaseValueObject( de.getExternalDatabase() ) : null;
    }

    public DatabaseEntryValueObject( long id ) {
        super( id );
    }

    /**
     * Required when using the class as a spring bean.
     */
    public DatabaseEntryValueObject() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( this.accession == null ) ? 0 : this.accession.hashCode() );
        result = prime * result + ( ( this.externalDatabase == null ) ? 0 : this.externalDatabase.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( this.getClass() != obj.getClass() )
            return false;
        DatabaseEntryValueObject other = ( DatabaseEntryValueObject ) obj;
        if ( this.accession == null ) {
            if ( other.accession != null )
                return false;
        } else if ( !this.accession.equals( other.accession ) )
            return false;
        if ( this.externalDatabase == null ) {
            return other.externalDatabase == null;
        } else
            return this.externalDatabase.equals( other.externalDatabase );
    }

    public String getAccession() {
        return this.accession;
    }

    public void setAccession( String accession ) {
        this.accession = accession;
    }

    public ExternalDatabaseValueObject getExternalDatabase() {
        return this.externalDatabase;
    }

    public void setExternalDatabase( ExternalDatabaseValueObject externalDatabase ) {
        this.externalDatabase = externalDatabase;
    }

}
