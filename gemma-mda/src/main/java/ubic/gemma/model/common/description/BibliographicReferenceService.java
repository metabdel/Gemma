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
public interface BibliographicReferenceService extends ubic.gemma.model.common.AuditableService {

    /**
     * <p>
     * Adds a document (in PDF format) for the reference.
     * </p>
     */
    public void addPDF( ubic.gemma.model.common.description.LocalFile pdfFile,
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference );

    /**
     * 
     */
    public ubic.gemma.model.common.description.BibliographicReference create(
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference );

    /**
     * <p>
     * check to see if the object already exists
     * </p>
     */
    public ubic.gemma.model.common.description.BibliographicReference find(
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference );

    /**
     * <p>
     * Get a reference by the unqualified external id.
     * </p>
     */
    public ubic.gemma.model.common.description.BibliographicReference findByExternalId( java.lang.String id );

    /**
     * <p>
     * Retrieve a reference by identifier, qualified by the database name (such as 'pubmed').
     * </p>
     */
    public ubic.gemma.model.common.description.BibliographicReference findByExternalId( java.lang.String id,
            java.lang.String databaseName );

    /**
     * 
     */
    public ubic.gemma.model.common.description.BibliographicReference findByTitle( java.lang.String title );

    /**
     * 
     */
    public ubic.gemma.model.common.description.BibliographicReference findOrCreate(
            ubic.gemma.model.common.description.BibliographicReference BibliographicReference );

    /**
     * <p>
     * Return all the BibRefs that are linked to ExpressionExperiments.
     * </p>
     */
    public java.util.Collection getAllExperimentLinkedReferences();

    /**
     * <p>
     * Get the ExpressionExperiments, if any, that are linked to the given reference.
     * </p>
     */
    public java.util.Collection getRelatedExperiments(
            ubic.gemma.model.common.description.BibliographicReference bibliographicReference );

    /**
     * 
     */
    public ubic.gemma.model.common.description.BibliographicReference load( java.lang.Long id );

    /**
     * 
     */
    public java.util.Collection loadMultiple( java.util.Collection ids );

    /**
     * 
     */
    public void remove( ubic.gemma.model.common.description.BibliographicReference BibliographicReference );

    /**
     * 
     */
    public void update( ubic.gemma.model.common.description.BibliographicReference bibliographicReference );

}
