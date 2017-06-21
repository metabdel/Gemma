/*
 * The Gemma project.
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
package ubic.gemma.persistence.service.common.auditAndSecurity;

import gemma.gsec.AuthorityConstants;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ubic.gemma.model.common.auditAndSecurity.GroupAuthority;
import ubic.gemma.model.common.auditAndSecurity.User;
import ubic.gemma.model.common.auditAndSecurity.UserGroup;

import java.util.Collection;
import java.util.Iterator;

/**
 * @see ubic.gemma.model.common.auditAndSecurity.UserGroup
 */
@Repository
public class UserGroupDaoImpl extends UserGroupDaoBase {

    @Autowired
    public UserGroupDaoImpl( SessionFactory sessionFactory ) {
        super.setSessionFactory( sessionFactory );
    }

    @Override
    public void addAuthority( UserGroup group, String authority ) {
        for ( gemma.gsec.model.GroupAuthority ga : group.getAuthorities() ) {
            if ( ga.getAuthority().equals( authority ) ) {
                return;
            }
        }
        GroupAuthority ga = GroupAuthority.Factory.newInstance();
        ga.setAuthority( authority );
        group.getAuthorities().add( ga );
        super.update( group );
    }

    @Override
    public void addToGroup( UserGroup group, User user ) {
        group.getGroupMembers().add( user );
        super.update( group );
    }

    @Override
    public UserGroup create( final UserGroup userGroup ) {
        if ( userGroup == null ) {
            throw new IllegalArgumentException( "UserGroup.create - 'userGroup' can not be null" );
        }
        if ( userGroup.getName().equals( AuthorityConstants.USER_GROUP_NAME ) || userGroup.getName()
                .equals( AuthorityConstants.ADMIN_GROUP_NAME ) || userGroup.getName()
                .equals( AuthorityConstants.AGENT_GROUP_NAME ) ) {
            throw new IllegalArgumentException( "Cannot create group with that name: " + userGroup.getName() );
        }
        this.getHibernateTemplate().save( userGroup );
        return userGroup;
    }

    @Override
    public Collection<UserGroup> findGroupsForUser( User user ) {
        //noinspection unchecked
        return this.getSession()
                .createQuery( "select ug from UserGroup ug inner join ug.groupMembers memb where memb = :user" )
                .setParameter( "user", user ).list();
    }

    @Override
    public void remove( Long id ) {
        UserGroup userGroup = this.load( id );
        // this check is done higher up as well...
        if ( userGroup.getName().equals( AuthorityConstants.USER_GROUP_NAME ) || userGroup.getName()
                .equals( AuthorityConstants.ADMIN_GROUP_NAME ) || userGroup.getName()
                .equals( AuthorityConstants.AGENT_GROUP_NAME ) ) {
            throw new IllegalArgumentException( "Cannot remove group: " + userGroup );
        }
        this.getSession().delete( userGroup );
    }

    @Override
    public void remove( UserGroup userGroup ) {
        this.remove( userGroup.getId() );
    }

    @Override
    public void removeAuthority( UserGroup group, String authority ) {
        for ( Iterator<gemma.gsec.model.GroupAuthority> iterator = group.getAuthorities().iterator(); iterator
                .hasNext(); ) {
            gemma.gsec.model.GroupAuthority ga = iterator.next();
            if ( ga.getAuthority().equals( authority ) ) {
                iterator.remove();
            }
        }
        this.getHibernateTemplate().update( group );
    }

    @Override
    public void update( UserGroup userGroup ) {
        UserGroup groupToUpdate = this.load( userGroup.getId() );
        String name = groupToUpdate.getName();
        if ( !name.equals( userGroup.getName() ) && ( name.equals( AuthorityConstants.USER_GROUP_NAME ) || name
                .equals( AuthorityConstants.ADMIN_GROUP_NAME ) || name
                .equals( AuthorityConstants.AGENT_GROUP_NAME ) ) ) {
            throw new IllegalArgumentException( "Cannot change name of group: " + groupToUpdate.getName() );
        }
        super.update( userGroup );
    }

    @Override
    public UserGroup find( UserGroup entity ) {
        return null;
    }

    @Override
    public UserGroup findOrCreate( UserGroup entity ) {
        UserGroup found = this.find( entity );
        return found != null ? found : this.create( entity );
    }

    @Override
    public Integer countAll() {
        return this.loadAll().size();
    }

    @Override
    public void thaw( UserGroup entity ) {
    }

}