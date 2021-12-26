package com.replace.replace.api.pagination.exception;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class NotSupportedValue extends Exception {

    public NotSupportedValue( final String key, final String value ) {
        super( "Not supported value " + value + " for key " + key );
    }
}

