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
package ubic.gemma.security.principal;

import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common methods for authenticating users.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class AuthenticationUtils {

    private static Log log = LogFactory.getLog( AuthenticationUtils.class.getName() );

    /**
     * @param username
     */
    public static void anonymousAuthenticate( String username, AuthenticationManager manager ) {
        log.debug( "No authentication object in context, providing anonymous authentication" );
        org.acegisecurity.Authentication authRequest = new AnonymousAuthenticationToken( "anonymous", username,
                new GrantedAuthority[] { new GrantedAuthority() {
                    public String getAuthority() {
                        return "ROLE_ANONYMOUS";
                    }
                } } );
        authRequest = manager.authenticate( authRequest );
        assert authRequest.isAuthenticated();
        SecurityContextHolder.getContext().setAuthentication( authRequest );
    }
}
