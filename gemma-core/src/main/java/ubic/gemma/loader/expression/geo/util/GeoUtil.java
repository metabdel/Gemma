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
package ubic.gemma.loader.expression.geo.util;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import ubic.basecode.util.NetUtils;


/**
 * @author pavlidis
 * @version $Id$
 */
public class GeoUtil {
    protected static final Log log = LogFactory.getLog( GeoUtil.class );
    private static String host;
    private static String login;
    private static String password;

    static {
        Configuration config = null;
        try {
            config = new PropertiesConfiguration( "Gemma.properties" );
        } catch ( ConfigurationException e ) {
            log.error( e );
        }
        host = ( String ) config.getProperty( "geo.host" );
        login = ( String ) config.getProperty( "geo.login" );
        password = ( String ) config.getProperty( "geo.password" );
    }

    /**
     * Convenient method to get a FTP connection.
     * 
     * @param mode
     * @return
     * @throws SocketException
     * @throws IOException
     */
    public static FTPClient connect( int mode ) throws SocketException, IOException {
        return NetUtils.connect( mode, host, login, password );
    }

}
