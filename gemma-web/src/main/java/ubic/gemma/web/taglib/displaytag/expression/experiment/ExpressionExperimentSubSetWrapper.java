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
package ubic.gemma.web.taglib.displaytag.expression.experiment;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import ubic.gemma.model.expression.experiment.ExpressionExperimentSubSet;

/**
 * Used to generate hyperlinks in displaytag tables.
 * <p>
 * See http://displaytag.sourceforge.net/10/tut_decorators.html and http://displaytag.sourceforge.net/10/tut_links.html
 * for explanation of how this works.
 * 
 * @author joseph
 * @version $Id$
 */
public class ExpressionExperimentSubSetWrapper extends TableDecorator {

    Log log = LogFactory.getLog( this.getClass() );

    /**
     * @return String
     */
    public String getNameLink() {
        ExpressionExperimentSubSet object = ( ExpressionExperimentSubSet ) getCurrentRowObject();
        String name = object.getName();
        if ( name == null ) {
            name = "No name";
        }
        return "<a href=\"showExpressionExperimentSubSet.html?id=" + object.getId() + "\"> "+ name + "</a>";
    }

}
