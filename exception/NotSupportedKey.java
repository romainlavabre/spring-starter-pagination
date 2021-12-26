package com.replace.replace.api.pagination.exception;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class NotSupportedKey extends Exception {

    public NotSupportedKey( final String key ) {
        super( "Not supported key " + key );
    }
}
