/*
 * The Gemma project.
 *
 * Copyright (c) 2006-2012 University of British Columbia
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

package ubic.gemma.model.analysis.expression.diff;

import org.hibernate.HibernateException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <p>
 * Represents the direction of a change e.g. in expression. "Either" is needed because a gene/probe could be changed in
 * two directions with respect to different conditions.
 * </p>
 */
public final class DirectionEnum extends Direction implements org.hibernate.usertype.EnhancedUserType {
    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = 3327245550772860236L;
    private static final int[] SQL_TYPES = { Types.VARCHAR };

    /**
     * Default constructor. Hibernate needs the default constructor to retrieve an instance of the enum from a JDBC
     * resultset. The instance will be converted to the correct enum instance in
     * {@link #nullSafeGet(java.sql.ResultSet, String[], Object)}.
     */
    public DirectionEnum() {
        super();
    }

    /**
     * @see org.hibernate.usertype.EnhancedUserType#objectToSQLString(Object object)
     */
    @Override
    public String objectToSQLString( Object object ) {
        return String.valueOf( ( ( Direction ) object ).getValue() );
    }

    /**
     * @see org.hibernate.usertype.EnhancedUserType#toXMLString(Object object)
     */
    @Override
    public String toXMLString( Object object ) {
        return String.valueOf( ( ( Direction ) object ).getValue() );
    }

    /**
     * @see org.hibernate.usertype.EnhancedUserType#fromXMLString(String string)
     */
    @Override
    public Object fromXMLString( String string ) {
        return Direction.fromString( String.valueOf( string ) );
    }

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    @Override
    public int[] sqlTypes() {
        return DirectionEnum.SQL_TYPES;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @Override
    public Class<?> returnedClass() {
        return Direction.class;
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(Object, Object)
     */
    @Override
    public boolean equals( Object x, Object y ) throws HibernateException {
        return ( x == y ) || ( y != null && y.equals( x ) );
    }

    /**
     * @see org.hibernate.usertype.UserType#hashCode(Object value)
     */
    @Override
    public int hashCode( Object value ) {
        return value.hashCode();
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, String[], Object)
     */
    @Override
    public Object nullSafeGet( ResultSet resultSet, String[] values, Object owner )
            throws HibernateException, SQLException {
        final String value = ( String ) resultSet.getObject( values[0] );
        return resultSet.wasNull() ? null : Direction.fromString( value );
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, Object, int)
     */
    @Override
    public void nullSafeSet( PreparedStatement statement, Object value, int index )
            throws HibernateException, SQLException {
        if ( value == null ) {
            statement.setNull( index, Types.VARCHAR );
        } else {
            statement.setObject( index, String.valueOf( String.valueOf( value ) ) );
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#deepCopy(Object)
     */
    @Override
    public Object deepCopy( Object value ) throws HibernateException {
        // Enums are immutable - nothing to be done to deeply clone it
        return value;
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    @Override
    public boolean isMutable() {
        // Enums are immutable
        return false;
    }

    /**
     * @see org.hibernate.usertype.UserType#disassemble(Object value)
     */
    @Override
    public java.io.Serializable disassemble( Object value ) {
        return ( java.io.Serializable ) value;
    }

    /**
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable cached, Object owner)
     */
    @Override
    public Object assemble( java.io.Serializable cached, Object owner ) {
        return cached;
    }

    /**
     * @see org.hibernate.usertype.UserType#replace(Object original, Object target, Object owner)
     */
    @Override
    public Object replace( Object original, Object target, Object owner ) {
        return original;
    }
}