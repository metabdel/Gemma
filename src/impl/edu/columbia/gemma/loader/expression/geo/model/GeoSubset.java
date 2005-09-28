/*
 * The Gemma project
 * 
 * Copyright (c) 2005 Columbia University
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
package edu.columbia.gemma.loader.expression.geo.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class GeoSubset extends GeoData {

    private GeoDataset owningDataset;
    private String description;
    private Collection<GeoSample> samples;
    private String type;

    public GeoSubset() {
        this.samples = new HashSet<GeoSample>();
    }

    public void addToDescription( String s ) {
        this.description = this.description + " " + s;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * @return Returns the sample.
     */
    public Collection getSamples() {
        return this.samples;
    }

    /**
     * @param sample The sample to set.
     */
    public void setSample( Collection<GeoSample> samples ) {
        this.samples = samples;
    }

    public void addSample( GeoSample sample ) {
        this.samples.add( sample );
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type The type to set.
     */
    public void setType( String type ) {
        this.type = type;
    }

    /**
     * @return Returns the owningDataset.
     */
    public GeoDataset getOwningDataset() {
        return this.owningDataset;
    }

    /**
     * @param owningDataset The owningDataset to set.
     */
    public void setOwningDataset( GeoDataset owningDataset ) {
        this.owningDataset = owningDataset;
    }

}
