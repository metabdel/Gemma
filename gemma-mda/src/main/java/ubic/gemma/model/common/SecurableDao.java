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
package ubic.gemma.model.common;

import java.util.Collection;

import org.springframework.security.acl.AclEntry;

/**
 * Defines methods that apply to all Securable objects. Security on objects is controlled by authorization based on
 * access control lists (ACL). ACL information is stored separately from the entity. An ACL entry for an entity defines
 * the permissions on the object.
 * <p>
 * Importantly, in general each Securable does not have its own permissions. Instead, the permissions exist in a
 * hierarchy. Generally the permissions for a securable are inherited from either a 'public' ACL, a 'private' ACL or a
 * 'user-specific' ACL.
 * 
 * @see ubic.gemma.model.common.Securable
 */
public interface SecurableDao<T extends Securable> {

    public final static int TRANSFORM_NONE = 0;

    /**
     * Get the id of the acl_object_identity for this object. This allows us to use other services to manipulate the ACL
     * entry for the object.
     * 
     * @param securable
     * @return the id for the ACL entry, or null if the securable has no ACL entry. Technically all Securables should
     *         have an ACL entry, so this should always return a non-null value.
     */
    public java.lang.Long getAclObjectIdentityId( ubic.gemma.model.common.Securable securable );

    /**
     * Get a collection of AclEntries for a securable.
     * 
     * @param target
     * @return
     */
    public Collection<AclEntry> getAclEntries( final Securable target );

    /**
     * Get the id of the acl_object_identity which the given securable's ACL entry inherits from, in the ACL hierarchy.
     * 
     * @param securable
     * @return the id for the ACL entry of the parent, or null if the securable has no ACL entry or if the parent lacks
     *         one. Technically all Securables should have an ACL entry, and a rule in the system is that all ACLs have
     *         a parent, except for a special control node. So this should always return a non-null value.
     */
    public Integer getAclObjectIdentityParentId( Securable securable );

}
