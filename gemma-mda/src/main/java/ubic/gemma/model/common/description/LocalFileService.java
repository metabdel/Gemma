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
public interface LocalFileService {

    /**
     * 
     */
    public ubic.gemma.model.common.description.LocalFile copyFile(
            ubic.gemma.model.common.description.LocalFile sourceFile,
            ubic.gemma.model.common.description.LocalFile targetFile );

    /**
     * 
     */
    public void deleteFile( ubic.gemma.model.common.description.LocalFile localFile );

    /**
     * 
     */
    public ubic.gemma.model.common.description.LocalFile find( ubic.gemma.model.common.description.LocalFile localFile );

    /**
     * 
     */
    public ubic.gemma.model.common.description.LocalFile findByPath( java.lang.String path );

    /**
     * 
     */
    public ubic.gemma.model.common.description.LocalFile findOrCreate(
            ubic.gemma.model.common.description.LocalFile localFile );

    /**
     * 
     */
    public ubic.gemma.model.common.description.LocalFile save( ubic.gemma.model.common.description.LocalFile localFile );

    /**
     * 
     */
    public void update( ubic.gemma.model.common.description.LocalFile localFile );

}
