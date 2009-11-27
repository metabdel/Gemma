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
package ubic.gemma.model.expression.bioAssayData;

import org.springframework.security.access.annotation.Secured;

import ubic.gemma.model.common.quantitationtype.QuantitationType;

/**
 * @author Paul
 * @version $Id$
 */
public interface DesignElementDataVectorService {

    /**
     * 
     */
    public java.lang.Integer countAll();

    /**
     * 
     */
    @Secured( { "GROUP_ADMIN" })
    public java.util.Collection<? extends DesignElementDataVector> create(
            java.util.Collection<? extends DesignElementDataVector> vectors );

    /**
     * 
     */
    public java.util.Collection<? extends DesignElementDataVector> find(
            java.util.Collection<QuantitationType> quantitationTypes );

    /**
     * 
     */
    public java.util.Collection<? extends DesignElementDataVector> find(
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * 
     */
    public java.util.Collection<? extends DesignElementDataVector> find(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * 
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDataVector load( java.lang.Long id );

    /**
     * 
     */
    @Secured( { "GROUP_ADMIN" })
    public void remove( java.util.Collection<? extends DesignElementDataVector> vectors );

    /**
     * 
     */
    @Secured( { "GROUP_ADMIN" })
    public void remove( ubic.gemma.model.expression.bioAssayData.RawExpressionDataVector designElementDataVector );

    /**
     * <p>
     * remove Design Element Data Vectors and Probe2ProbeCoexpression entries for a specified CompositeSequence.
     * </p>
     */
    @Secured( { "GROUP_ADMIN" })
    public void removeDataForCompositeSequence(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Removes the DesignElementDataVectors and Probe2ProbeCoexpressions for a quantitation type, given a
     * QuantitationType (which always comes from a specific ExpressionExperiment)
     * </p>
     */
    @Secured( { "GROUP_ADMIN" })
    public void removeDataForQuantitationType(
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * 
     */
    public void thaw( java.util.Collection<? extends DesignElementDataVector> designElementDataVectors );

    /**
     * <p>
     * Thaws associations of the given DesignElementDataVector
     * </p>
     */
    public void thaw( RawExpressionDataVector designElementDataVector );

    /**
     * <p>
     * updates a collection of designElementDataVectors
     * </p>
     */
    @Secured( { "GROUP_USER" })
    public void update( java.util.Collection<? extends DesignElementDataVector> dedvs );

    /**
     * <p>
     * updates an already existing dedv
     * </p>
     */
    @Secured( { "GROUP_USER" })
    public void update( RawExpressionDataVector dedv );

}
