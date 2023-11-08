package com.replace.replace.api.pagination.condition;

import com.fairfair.data_repository.api.pagination.Constraint;
import com.fairfair.data_repository.api.pagination.exception.NotSupportedKey;
import com.fairfair.data_repository.api.pagination.exception.NotSupportedOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class Condition {

    protected static final Map< String, String > OPERATOR;

    static {
        OPERATOR = new HashMap<>();
        OPERATOR.put( "eq", "=" );
        OPERATOR.put( "ne", "!=" );
        OPERATOR.put( "sup", ">" );
        OPERATOR.put( "inf", "<" );
        OPERATOR.put( "supeq", ">=" );
        OPERATOR.put( "infeq", "<=" );
        OPERATOR.put( "contains", "LIKE" );
        OPERATOR.put( "necontains", "NOT LIKE" );
        OPERATOR.put( "startwith", "LIKE" );
        OPERATOR.put( "jsoncontains", "JSON_CONTAINS({column},{value})" );
        OPERATOR.put( "nejsoncontains", "!JSON_CONTAINS({column},{value})" );
    }

    private final String key;

    private final String operator;

    private final List< String > values;

    private final Map< String, String > parameters;


    public Condition( final String key, final String operator ) {
        this.key        = key;
        this.operator   = operator;
        this.values     = new ArrayList<>();
        this.parameters = new HashMap<>();
    }


    public boolean isKey( final String key ) {
        return this.key.equals( key );
    }


    public String getKey() {
        return this.key;
    }


    public boolean isOperator( final String operator ) {
        return this.operator.equals( operator );
    }


    public void addValue( final String value ) {
        this.values.add( value );
    }


    public Map< String, String > getParameters() {
        return this.parameters;
    }


    public String consume( final int startIncrement, String customKey ) throws NotSupportedOperator, NotSupportedKey {

        Constraint.assertValidKey( this.key );

        final StringBuilder condition    = new StringBuilder( "( " );
        int                 keyIncrement = startIncrement * 1000;

        if ( this.values.size() > 1 ) {
            for ( int i = 0; i < this.values.size(); i++ ) {
                final String parameter = "key" + keyIncrement++;

                if ( !isFunction( operator ) ) {
                    final String operator = this.getSqlOperator( this.values.get( i ), parameter );
                    condition.append( ( customKey != null ? customKey : this.key ) + " " + operator + " " + this.getParameter( this.values.get( i ), operator, parameter ) );
                } else {
                    condition.append( getFunction( values.get( i ), parameter, customKey ) );
                }


                if ( i < this.values.size() - 1 ) {
                    condition.append( " OR " );
                }
            }

            return condition.append( " )" ).toString();
        }

        return !isFunction( operator )
                ? ( customKey != null ? customKey : this.key ) + " " + this.getSqlOperator( this.values.get( 0 ), "key" + keyIncrement ) + " " + this.getParameter( this.values.get( 0 ), this.operator, "key" + keyIncrement )
                : getFunction( values.get( 0 ), "key" + keyIncrement, customKey );
    }


    private String getSqlOperator( final String value, String parameter ) throws NotSupportedOperator {
        if ( !Condition.OPERATOR.containsKey( this.operator ) ) {
            throw new NotSupportedOperator( this.operator );
        }

        if ( value.equals( "null" ) ) {
            if ( this.operator.equals( "ne" ) ) {
                return "IS NOT NULL";
            } else {
                return "IS NULL";
            }
        }

        return Condition.OPERATOR.get( this.operator );
    }


    private String getParameter( final String value, final String operator, final String parameter ) {
        if ( operator.contains( "contains" ) ) {
            this.parameters.put( parameter, "%" + value + "%" );
            return ":" + parameter;
        } else if ( operator.contains( "startwith" ) ) {
            this.parameters.put( parameter, value + "%" );
            return ":" + parameter;
        } else if ( !value.toUpperCase().equals( "NULL" ) ) {
            this.parameters.put( parameter, value );
            return ":" + parameter;
        }

        return "";
    }


    private String getFunction( String value, String parameter, String customKey ) throws NotSupportedOperator {
        if ( !Condition.OPERATOR.containsKey( this.operator ) ) {
            throw new NotSupportedOperator( this.operator );
        }

        this.parameters.put( parameter, value );
        return Condition.OPERATOR.get( this.operator ).replace( "{column}", customKey != null ? customKey : this.key ).replace( "{value}", ":" + parameter );
    }


    private boolean isFunction( String operator ) {
        return operator.contains( "json" );
    }
}
