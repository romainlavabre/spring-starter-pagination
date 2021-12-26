package com.replace.replace.api.pagination;

import com.replace.replace.api.pagination.exception.NotSupportedKey;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public final class Constraint {
    public static void assertValidKey( final String key ) throws NotSupportedKey {
        if ( !key.matches( "^[a-zA-Z0-9_-]+$" ) ) {
            throw new NotSupportedKey( key );
        }
    }
}
