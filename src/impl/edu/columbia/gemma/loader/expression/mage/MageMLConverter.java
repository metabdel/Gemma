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
package edu.columbia.gemma.loader.expression.mage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.biomage.QuantitationType.QuantitationType;
import org.dom4j.Document;

import edu.columbia.gemma.expression.bioAssay.BioAssay;
import edu.columbia.gemma.expression.designElement.DesignElement;
import edu.columbia.gemma.loader.loaderutils.Converter;

/**
 * Class to parse MAGE-ML files and convert them into Gemma domain objects SDO.
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 * @spring.bean id="mageMLConverter" singleton="false"
 * @spring.property name="mageMLConverterHelper" ref="mageMLConverterHelper"
 */
public class MageMLConverter implements Converter {

    private static Log log = LogFactory.getLog( MageMLConverter.class.getName() );

    private Collection<Object> convertedResult;
    private boolean isConverted = false;
    private String[] mageClasses;

    private MageMLConverterHelper mageConverterHelper;

    private Document simplifiedXml;

    /**
     * @return Returns the simplifiedXml.
     */
    public Document getSimplifiedXml() {
        return simplifiedXml;
    }

    /**
     * @param simplifiedXml The simplifiedXml to set. TODO do not make MageMLConverterHelper available to spring. Remove
     *        the call to getMageMLConverterHelper()
     */
    public void setSimplifiedXml( Document simplifiedXml ) {
        this.simplifiedXml = simplifiedXml;
        getMageMLConverterHelper().setSimplifiedXml( this.simplifiedXml ); // will be null if you put in
        // constructor
    }

    /**
     * default constructor
     */
    public MageMLConverter() {
        super();
        ResourceBundle rb = ResourceBundle.getBundle( "mage" );
        String mageClassesString = rb.getString( "mage.classes" ); // FIXME : use config array
        mageClasses = mageClassesString.split( ", " );
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.columbia.gemma.loader.loaderutils.Converter#convert(java.util.Collection)
     */
    public Collection<Object> convert( Collection<Object> sourceDomainObjects ) {
        Package[] allPackages = Package.getPackages();
        if ( convertedResult == null ) {
            convertedResult = new ArrayList<Object>();
        } else {
            convertedResult.clear();
        }

        // this is a little inefficient because it tries every possible package and class. - fix is to get just
        // the mage
        // packages!
        for ( int i = 0; i < allPackages.length; i++ ) {

            String name = allPackages[i].getName();
            if ( !name.startsWith( "org.biomage." ) || name.startsWith( "org.biomage.tools." )
                    || name.startsWith( "org.biomage.Interface" ) ) continue;

            for ( int j = 0; j < mageClasses.length; j++ ) {
                try {
                    Class c = Class.forName( name + "." + mageClasses[j] );
                    Collection<Object> convertedObjects = getConvertedDataForType( c, sourceDomainObjects );
                    if ( convertedObjects != null && convertedObjects.size() > 0 ) {
                        log.info( "Adding " + convertedObjects.size() + " converted " + name + "." + mageClasses[j]
                                + "s" );
                        convertedResult.addAll( convertedObjects );
                    }
                } catch ( ClassNotFoundException e ) {
                }
            }
        }
        this.isConverted = true;
        return convertedResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.columbia.gemma.loader.expression.mage.MageMLConverter#getBioAssayDesignElementDimension(org.biomage.BioAssay.BioAssay)
     */
    public List<DesignElement> getBioAssayDesignElementDimension( BioAssay bioAssay ) {
        assert isConverted;
        return this.mageConverterHelper.getBioAssayDesignElementDimension( bioAssay );
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.columbia.gemma.loader.expression.mage.MageMLConverter#getBioAssayQuantitationTypeDimension(org.biomage.BioAssay.BioAssay)
     */
    public List<QuantitationType> getBioAssayQuantitationTypeDimension( BioAssay bioAssay ) {
        assert isConverted;
        return this.mageConverterHelper.getBioAssayQuantitationTypeDimension( bioAssay );
    }

    /**
     * @return all the converted BioAssay objects.
     */
    public List<BioAssay> getConvertedBioAssays() {
        assert isConverted;
        List<BioAssay> result = new ArrayList<BioAssay>();
        for ( Object object : convertedResult ) {
            if ( object instanceof BioAssay ) {
                result.add( ( BioAssay ) object );
            }
        }
        log.info( "Found " + result.size() + " bioassays" );
        return result;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Map<String, Integer> tally = new HashMap<String, Integer>();
        for ( Object element : convertedResult ) {
            String clazz = element.getClass().getName();
            if ( !tally.containsKey( clazz ) ) {
                tally.put( clazz, new Integer( 0 ) );
            }
            tally.put( clazz, new Integer( ( tally.get( clazz ) ).intValue() + 1 ) );
        }

        for ( String clazz : tally.keySet() ) {
            buf.append( tally.get( clazz ) + " " + clazz + "s\n" );
        }

        return buf.toString();
    }

    /**
     * Generic method to extract desired data, converted to the Gemma domain objects.
     * 
     * @param type
     * @return
     */
    private Collection<Object> getConvertedDataForType( Class type, Collection<Object> mageDomainObjects ) {
        if ( mageDomainObjects == null ) return null;

        Collection<Object> localResult = new ArrayList<Object>();

        for ( Object element : mageDomainObjects ) {
            if ( element == null ) continue;
            if ( !( element.getClass().isAssignableFrom( type ) ) ) continue;

            Object converted = convert( element );
            if ( converted != null ) localResult.add( mageConverterHelper.convert( element ) );
        }
        return localResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.columbia.gemma.loader.loaderutils.Converter#convert(java.lang.Object)
     */
    public Object convert( Object mageObject ) {
        return mageConverterHelper.convert( mageObject );
    }

    /**
     * @return Returns the mageConverterHelper.
     */
    public MageMLConverterHelper getMageMLConverterHelper() {
        return mageConverterHelper;
    }

    /**
     * @param mageConverterHelper The mageConverterHelper to set.
     */
    public void setMageMLConverterHelper( MageMLConverterHelper mageConverterHelper ) {
        this.mageConverterHelper = mageConverterHelper;
    }

}
