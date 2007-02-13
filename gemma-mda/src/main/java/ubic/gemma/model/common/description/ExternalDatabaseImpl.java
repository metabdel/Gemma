/*
 * The Gemma project.
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package ubic.gemma.model.common.description;

/**
 * @see ubic.gemma.model.common.description.ExternalDatabase
 */
public class ExternalDatabaseImpl extends ubic.gemma.model.common.description.ExternalDatabase {

    /**
     * 
     */
    private static final long serialVersionUID = 5857412688940867544L;

    @Override
    public boolean equals( Object object ) {
        if ( !( object instanceof ExternalDatabase ) ) return false;

        ExternalDatabase that = ( ExternalDatabase ) object;
        if ( this.getId() != null && that.getId() != null ) return super.equals( object );

        return this.getName().equals( that.getName() );
    }

    @Override
    public int hashCode() {
        if ( this.getId() != null ) return super.hashCode();

        return this.getName().hashCode();
    }

}