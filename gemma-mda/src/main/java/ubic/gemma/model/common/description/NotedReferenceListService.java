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
package ubic.gemma.model.common.description;

/**
 * 
 */
public interface NotedReferenceListService {

    /**
     * 
     */
    public void addReferenceToList( ubic.gemma.model.common.description.NotedReferenceList notedReferenceList,
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference );

    /**
     * 
     */
    public ubic.gemma.model.common.description.NotedReferenceList createNewList( java.lang.String name,
            ubic.gemma.model.common.auditAndSecurity.User owner );

    /**
     * 
     */
    public java.util.Collection getAllReferencesForList(
            ubic.gemma.model.common.description.NotedReferenceList notedReferenceList );

    /**
     * 
     */
    public void removeList( ubic.gemma.model.common.description.NotedReferenceList notedReferenceList );

    /**
     * 
     */
    public void removeReferenceFromList( ubic.gemma.model.common.description.NotedReference notedReference,
            ubic.gemma.model.common.description.NotedReferenceList notedReferenceList );

    /**
     * <p>
     * Set the comment for a reference.
     * </p>
     */
    public void setComment( java.lang.String comment, ubic.gemma.model.common.description.NotedReference notedReference );

    /**
     * 
     */
    public void setListDescription( java.lang.String description,
            ubic.gemma.model.common.description.NotedReferenceList notedReferenceList );

    /**
     * 
     */
    public void setRating( java.lang.Integer rating, ubic.gemma.model.common.description.NotedReference notedReference );

}
