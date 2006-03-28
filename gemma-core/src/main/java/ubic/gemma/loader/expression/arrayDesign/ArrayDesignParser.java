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
package ubic.gemma.loader.expression.arrayDesign;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.util.StringUtil;
import ubic.gemma.loader.util.parser.BasicLineParser;
import ubic.gemma.model.common.auditAndSecurity.Contact;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;

/**
 * Parse ArrayDesigns from a flat file. This is used to seed the system from our legacy data.
 * <p>
 * Format:
 * <ol>
 * <li>Murine Genome U74A Array --- platform name
 * <li>Affymetrix -- Manufacturer name
 * <li>MG-U74A --- short name
 * <li>MOUSE --- taxon
 * <li>10044 --- advertised number of design elements
 * <li>(Masked) Affymetrix GeneChip expression probe array... --- Description
 * </ol>
 * <hr>
 * <p>
 * Copyright (c) 2004 - 2006 University of British Columbia
 * 
 * @author keshav
 * @version $Id$
 */
public class ArrayDesignParser extends BasicLineParser {
    protected static final Log log = LogFactory.getLog( ArrayDesignParser.class );

    public Object parseOneLine( String line ) {
        ArrayDesign ad = ArrayDesign.Factory.newInstance();
        String[] fields = StringUtil.splitPreserveAllTokens( line, '\t' );
        ad.setName( fields[0] );
        ad.setDescription( fields[5] );

        Contact manufacturer = Contact.Factory.newInstance();
        manufacturer.setName( fields[1] );
        ad.setDesignProvider( manufacturer );

        ad.setAdvertisedNumberOfDesignElements( Integer.parseInt( fields[4] ) );
        return ad;
    }

}
