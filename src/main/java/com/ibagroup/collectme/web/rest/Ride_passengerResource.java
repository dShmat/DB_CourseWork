package com.ibagroup.collectme.web.rest;
import com.ibagroup.collectme.domain.Ride_passenger;
import com.ibagroup.collectme.repository.Ride_passengerRepository;
import com.ibagroup.collectme.web.rest.errors.BadRequestAlertException;
import com.ibagroup.collectme.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Ride_passenger.
 */
@RestController
@RequestMapping("/api")
public class Ride_passengerResource {

    private final Logger log = LoggerFactory.getLogger(Ride_passengerResource.class);

    private static final String ENTITY_NAME = "ride_passenger";

    private final Ride_passengerRepository ride_passengerRepository;

    public Ride_passengerResource(Ride_passengerRepository ride_passengerRepository) {
        this.ride_passengerRepository = ride_passengerRepository;
    }

    /**
     * POST  /ride-passengers : Create a new ride_passenger.
     *
     * @param ride_passenger the ride_passenger to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ride_passenger, or with status 400 (Bad Request) if the ride_passenger has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ride-passengers")
    public ResponseEntity<Ride_passenger> createRide_passenger(@RequestBody Ride_passenger ride_passenger) throws URISyntaxException {
        log.debug("REST request to save Ride_passenger : {}", ride_passenger);
        if (ride_passenger.getId() != null) {
            throw new BadRequestAlertException("A new ride_passenger cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ride_passenger result = ride_passengerRepository.save(ride_passenger);
        return ResponseEntity.created(new URI("/api/ride-passengers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ride-passengers : Updates an existing ride_passenger.
     *
     * @param ride_passenger the ride_passenger to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ride_passenger,
     * or with status 400 (Bad Request) if the ride_passenger is not valid,
     * or with status 500 (Internal Server Error) if the ride_passenger couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ride-passengers")
    public ResponseEntity<Ride_passenger> updateRide_passenger(@RequestBody Ride_passenger ride_passenger) throws URISyntaxException {
        log.debug("REST request to update Ride_passenger : {}", ride_passenger);
        if (ride_passenger.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Ride_passenger result = ride_passengerRepository.save(ride_passenger);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ride_passenger.getId().toString()))
            .body(result);
    }

    /**
     * GET  /ride-passengers : get all the ride_passengers.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of ride_passengers in body
     */
    @GetMapping("/ride-passengers")
    public List<Ride_passenger> getAllRide_passengers() {
        log.debug("REST request to get all Ride_passengers");
        return ride_passengerRepository.findAll();
    }

    /**
     * GET  /ride-passengers/:id : get the "id" ride_passenger.
     *
     * @param id the id of the ride_passenger to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ride_passenger, or with status 404 (Not Found)
     */
    @GetMapping("/ride-passengers/{id}")
    public ResponseEntity<Ride_passenger> getRide_passenger(@PathVariable Long id) {
        log.debug("REST request to get Ride_passenger : {}", id);
        Optional<Ride_passenger> ride_passenger = ride_passengerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ride_passenger);
    }

    /**
     * DELETE  /ride-passengers/:id : delete the "id" ride_passenger.
     *
     * @param id the id of the ride_passenger to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ride-passengers/{id}")
    public ResponseEntity<Void> deleteRide_passenger(@PathVariable Long id) {
        log.debug("REST request to delete Ride_passenger : {}", id);
        ride_passengerRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
