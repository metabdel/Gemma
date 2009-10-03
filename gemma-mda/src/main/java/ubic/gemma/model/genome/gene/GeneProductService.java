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
package ubic.gemma.model.genome.gene;

/**
 * 
 */
public interface GeneProductService extends ubic.gemma.model.common.AuditableService {

    /**
     * 
     */
    public java.lang.Integer countAll();

    /**
     * 
     */
    public ubic.gemma.model.genome.gene.GeneProduct create( ubic.gemma.model.genome.gene.GeneProduct geneProduct );

    /**
     * 
     */
    public void delete( ubic.gemma.model.genome.gene.GeneProduct geneProduct );

    /**
     * 
     */
    public ubic.gemma.model.genome.gene.GeneProduct find( ubic.gemma.model.genome.gene.GeneProduct gProduct );

    /**
     * 
     */
    public ubic.gemma.model.genome.gene.GeneProduct findOrCreate( ubic.gemma.model.genome.gene.GeneProduct geneProduct );

    /**
     * 
     */
    public java.util.Collection getGenesByName( java.lang.String search );

    /**
     * 
     */
    public java.util.Collection getGenesByNcbiId( java.lang.String search );

    /**
     * 
     */
    public ubic.gemma.model.genome.gene.GeneProduct load( java.lang.Long id );

    /**
     * <p>
     * loads geneProducts specified by the given ids.
     * </p>
     */
    public java.util.Collection loadMultiple( java.util.Collection ids );

    /**
     * 
     */
    public void update( ubic.gemma.model.genome.gene.GeneProduct geneProduct );

}
