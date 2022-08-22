package com.replace.replace.api.pagination.rt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Repository
public interface RealTimeJpa extends JpaRepository< RealTime, Long > {
}
