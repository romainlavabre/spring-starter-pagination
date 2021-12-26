package com.replace.replace.api.pagination.exception;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class NotSupportedOperator extends Exception {

    public NotSupportedOperator( final String operator ) {
        super( "Not supported operator " + operator );
    }
}
