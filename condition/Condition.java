package com.replace.replace.api.pagination.condition;

import com.replace.replace.api.pagination.Constraint;
import com.replace.replace.api.pagination.exception.NotSupportedKey;
import com.replace.replace.api.pagination.exception.NotSupportedOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class Condition {

    protected static final Map< String, String > OPERATOR = Map.of(
            "eq", "=",
            "ne", "!=",
            "sup", ">",
            "inf", "<",
            "supeq", ">=",
            "infeq", "<=",
            "contains", "LIKE",
            "necontains", "NOT LIKE"
    );

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


    public boolean isOperator( final String operator ) {
        return this.operator.equals( operator );
    }


    public void addValue( final String value ) {
        this.values.add( value );
    }


    public Map< String, String > getParameters() {
        return this.parameters;
    }


    public String consume( final int startIncrement ) throws NotSupportedOperator, NotSupportedKey {

        Constraint.assertValidKey( this.key );

        final StringBuilder condition    = new StringBuilder( "( " );
        int                 keyIncrement = startIncrement * 1000;

        if ( this.values.size() > 1 ) {
            for ( int i = 0; i < this.values.size(); i++ ) {
                final String operator  = this.getSqlOperator( this.values.get( i ) );
                final String parameter = "key" + keyIncrement++;

                condition.append( this.key + " " + operator + " " + this.getParameter( this.values.get( i ), operator, parameter ) );


                if ( i < this.values.size() - 1 ) {
                    condition.append( " OR " );
                }
            }

            return condition.append( " )" ).toString();
        }


        return this.key + " " + this.getSqlOperator( this.values.get( 0 ) ) + " " + this.getParameter( this.values.get( 0 ), this.operator, "key" + keyIncrement );
    }


    private String getSqlOperator( final String value ) throws NotSupportedOperator {
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
        } else if ( !value.toUpperCase().equals( "NULL" ) ) {
            this.parameters.put( parameter, value );
            return ":" + parameter;
        }

        return "";
    }
}
