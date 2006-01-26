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
package edu.columbia.gemma.loader.expression.geo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.configuration.ConfigurationException;

import edu.columbia.gemma.common.description.LocalFile;

/**
 * A fetcher that only looks locally.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class LocalSeriesFetcher extends SeriesFetcher {

    private final String localPath;

    /**
     * @throws ConfigurationException
     */
    public LocalSeriesFetcher( String localPath ) throws ConfigurationException {
        super();
        this.localPath = localPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.columbia.gemma.loader.expression.geo.SeriesFetcher#fetch(java.lang.String)
     */
    @Override
    public Collection<LocalFile> fetch( String accession ) {
        log.info( "Seeking GSE  file for " + accession );
        assert localPath != null;
        String seekFileName = localPath + "/" + accession + "_family.soft.gz";
        File seekFile = new File( seekFileName );

        if ( seekFile.canRead() ) {
            LocalFile file = fetchedFile( seekFileName );
            log.info( "Found " + seekFileName + " for experiment(set) " + accession + "." );
            Collection<LocalFile> result = new HashSet<LocalFile>();
            result.add( file );
            return result;
        }

        throw new RuntimeException( "Failed to find " + seekFileName );
    }

    /**
     * @param seekFile
     * @return
     */
    protected LocalFile fetchedFile( String seekFile ) {
        LocalFile file = LocalFile.Factory.newInstance();
        file.setVersion( new SimpleDateFormat().format( new Date() ) );
        file.setRemoteURI( seekFile );
        file.setLocalURI( "file://" + seekFile );
        return file;
    }

}
