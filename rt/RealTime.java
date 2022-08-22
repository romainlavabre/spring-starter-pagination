package com.replace.replace.api.pagination.rt;

import javax.persistence.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Entity
@Table( name = "pagination_real_time" )
public class RealTime {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;

    private long subjectId;

    @Column( nullable = false )
    private String subjectTable;

    @Column( nullable = false )
    private ZonedDateTime updatedAt;


    public RealTime() {
    }


    public RealTime( long subjectId, String subjectTable ) {
        this.subjectId    = subjectId;
        this.subjectTable = subjectTable;
        updatedAt         = ZonedDateTime.now( ZoneOffset.UTC );
    }
}
