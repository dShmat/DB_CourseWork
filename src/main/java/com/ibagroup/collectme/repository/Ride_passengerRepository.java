package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.Ride_passenger;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Ride_passenger entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Ride_passengerRepository extends JpaRepository<Ride_passenger, Long> {

}
