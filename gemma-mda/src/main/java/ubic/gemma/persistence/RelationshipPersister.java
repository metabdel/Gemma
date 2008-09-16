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
package ubic.gemma.persistence;

import ubic.gemma.model.analysis.expression.ExpressionExperimentSet;
import ubic.gemma.model.analysis.expression.ExpressionExperimentSetService;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysis;
import ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisService;
import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionAnalysis;
import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionAnalysisService;
import ubic.gemma.model.analysis.expression.coexpression.ProbeCoexpressionAnalysis;
import ubic.gemma.model.analysis.expression.coexpression.ProbeCoexpressionAnalysisService;
import ubic.gemma.model.association.Gene2GOAssociation;
import ubic.gemma.model.association.Gene2GOAssociationService;

/**
 * Persist objects like Gene2GOAssociation.
 * 
 * @author pavlidis
 * @version $Id$
 * @spring.property name="gene2GOAssociationService" ref="gene2GOAssociationService"
 * @spring.property name="probeCoexpressionAnalysisService" ref="probeCoexpressionAnalysisService"
 * @spring.property name="differentialExpressionAnalysisService" ref="differentialExpressionAnalysisService"
 * @spring.property name="geneCoexpressionAnalysisService" ref="geneCoexpressionAnalysisService"
 * @spring.property name="expressionExperimentSetService" ref="expressionExperimentSetService"
 */
public class RelationshipPersister extends ExpressionPersister {

    private Gene2GOAssociationService gene2GOAssociationService;

    private ProbeCoexpressionAnalysisService probeCoexpressionAnalysisService;

    private DifferentialExpressionAnalysisService differentialExpressionAnalysisService;

    private GeneCoexpressionAnalysisService geneCoexpressionAnalysisService;

    private ExpressionExperimentSetService expressionExperimentSetService;

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.loader.util.persister.Persister#persist(java.lang.Object)
     */
    @Override
    public Object persist( Object entity ) {
        if ( entity == null ) return null;

        if ( entity instanceof Gene2GOAssociation ) {
            return persistGene2GOAssociation( ( Gene2GOAssociation ) entity );
        } else if ( entity instanceof ProbeCoexpressionAnalysis ) {
            return persistProbeCoexpressionAnalysis( ( ProbeCoexpressionAnalysis ) entity );
        } else if ( entity instanceof DifferentialExpressionAnalysis ) {
            return persistDifferentialExpressionAnalysis( ( DifferentialExpressionAnalysis ) entity );
        } else if ( entity instanceof GeneCoexpressionAnalysis ) {
            return persistGeneCoexpressionAnalysis( ( GeneCoexpressionAnalysis ) entity );
        } else if ( entity instanceof ExpressionExperimentSet ) {
            return persistExpressionExperimentSet( ( ExpressionExperimentSet ) entity );
        }
        return super.persist( entity );

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.persistence.CommonPersister#persistOrUpdate(java.lang.Object)
     */
    @Override
    public Object persistOrUpdate( Object entity ) {
        if ( entity == null ) return null;
        return super.persistOrUpdate( entity );
    }

    /**
     * @param association
     * @return
     */
    protected Gene2GOAssociation persistGene2GOAssociation( Gene2GOAssociation association ) {
        if ( association == null ) return null;
        if ( !isTransient( association ) ) return association;

        association.setGene( persistGene( association.getGene() ) );
        return gene2GOAssociationService.findOrCreate( association );
    }

    /**
     * @param entity
     * @return
     */
    protected ProbeCoexpressionAnalysis persistProbeCoexpressionAnalysis( ProbeCoexpressionAnalysis entity ) {
        if ( entity == null ) return null;
        if ( !isTransient( entity ) ) return entity;
        entity.setProtocol( persistProtocol( entity.getProtocol() ) );

        entity.setExpressionExperimentSetAnalyzed( persistExpressionExperimentSet( entity
                .getExpressionExperimentSetAnalyzed() ) );

        return probeCoexpressionAnalysisService.create( entity );
    }

    /**
     * @param entity
     * @return
     */
    protected GeneCoexpressionAnalysis persistGeneCoexpressionAnalysis( GeneCoexpressionAnalysis entity ) {
        if ( entity == null ) return null;
        if ( !isTransient( entity ) ) return entity;
        entity.setProtocol( persistProtocol( entity.getProtocol() ) );
        entity.setExpressionExperimentSetAnalyzed( persistExpressionExperimentSet( entity
                .getExpressionExperimentSetAnalyzed() ) );

        return geneCoexpressionAnalysisService.create( entity );
    }

    /**
     * @param entity
     * @return
     */
    protected DifferentialExpressionAnalysis persistDifferentialExpressionAnalysis(
            DifferentialExpressionAnalysis entity ) {
        if ( entity == null ) return null;
        if ( !isTransient( entity ) ) return entity;
        entity.setProtocol( persistProtocol( entity.getProtocol() ) );
        entity.setExpressionExperimentSetAnalyzed( persistExpressionExperimentSet( entity
                .getExpressionExperimentSetAnalyzed() ) );
        return differentialExpressionAnalysisService.create( entity );
    }

    protected ExpressionExperimentSet persistExpressionExperimentSet( ExpressionExperimentSet entity ) {
        if ( !isTransient( entity ) ) return entity;
        if ( entity.getExperiments().size() == 0 ) {
            throw new IllegalArgumentException( "Attempt to create an empty ExpressionExperimentSet." );
        }
        return expressionExperimentSetService.create( entity );
    }

    /**
     * @param gene2GOAssociationService the gene2GOAssociationService to set
     */
    public void setGene2GOAssociationService( Gene2GOAssociationService gene2GOAssociationService ) {
        this.gene2GOAssociationService = gene2GOAssociationService;
    }

    public void setProbeCoexpressionAnalysisService( ProbeCoexpressionAnalysisService probeCoexpressionAnalysisService ) {
        this.probeCoexpressionAnalysisService = probeCoexpressionAnalysisService;
    }

    public void setDifferentialExpressionAnalysisService(
            DifferentialExpressionAnalysisService differentialExpressionAnalysisService ) {
        this.differentialExpressionAnalysisService = differentialExpressionAnalysisService;
    }

    public void setGeneCoexpressionAnalysisService( GeneCoexpressionAnalysisService geneCoexpressionAnalysisService ) {
        this.geneCoexpressionAnalysisService = geneCoexpressionAnalysisService;
    }

    public void setExpressionExperimentSetService( ExpressionExperimentSetService expressionExperimentSetService ) {
        this.expressionExperimentSetService = expressionExperimentSetService;
    }

}
