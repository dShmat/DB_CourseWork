package com.ibagroup.collectme.web.rest;
import com.ibagroup.collectme.service.PeriodService;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.web.rest.errors.BadRequestAlertException;
import com.ibagroup.collectme.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Period.
 */
@RestController
@RequestMapping("/api")
public class PeriodResource {

    private final Logger log = LoggerFactory.getLogger(PeriodResource.class);

    private static final String ENTITY_NAME = "period";

    private final PeriodService periodService;

    public PeriodResource(PeriodService periodService) {
        this.periodService = periodService;
    }

    /**
     * POST  /periods : Create a new period.
     *
     * @param periodDTO the periodDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new periodDTO, or with status 400 (Bad Request) if the period has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/periods")
    public ResponseEntity<PeriodDTO> createPeriod(@RequestBody PeriodDTO periodDTO) throws URISyntaxException {
        log.debug("REST request to save Period : {}", periodDTO);
        if (periodDTO.getId() != null) {
            throw new BadRequestAlertException("A new period cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PeriodDTO result = periodService.save(periodDTO);
        return ResponseEntity.created(new URI("/api/periods/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /periods : Updates an existing period.
     *
     * @param periodDTO the periodDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated periodDTO,
     * or with status 400 (Bad Request) if the periodDTO is not valid,
     * or with status 500 (Internal Server Error) if the periodDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/periods")
    public ResponseEntity<PeriodDTO> updatePeriod(@RequestBody PeriodDTO periodDTO) throws URISyntaxException {
        log.debug("REST request to update Period : {}", periodDTO);
        if (periodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PeriodDTO result = periodService.save(periodDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, periodDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /periods : get all the periods.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of periods in body
     */
    @GetMapping("/periods")
    public List<PeriodDTO> getAllPeriods() {
        log.debug("REST request to get all Periods");
        return periodService.findAll();
    }

    /**
     * GET  /periods/:id : get the "id" period.
     *
     * @param id the id of the periodDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the periodDTO, or with status 404 (Not Found)
     */
    @GetMapping("/periods/{id}")
    public ResponseEntity<PeriodDTO> getPeriod(@PathVariable Long id) {
        log.debug("REST request to get Period : {}", id);
        Optional<PeriodDTO> periodDTO = periodService.findOne(id);
        return ResponseUtil.wrapOrNotFound(periodDTO);
    }

    /**
     * DELETE  /periods/:id : delete the "id" period.
     *
     * @param id the id of the periodDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/periods/{id}")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        log.debug("REST request to delete Period : {}", id);
        periodService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/periods/findCurrentPeriod")
    public ResponseEntity<PeriodDTO> findCurrentPeriod() {
        log.debug("REST request to get current Period : {}");
        PeriodDTO periodDTO = periodService.findCurrent().get();
        return ResponseEntity.ok().body(periodDTO);
    }

    @GetMapping("/periods/findByMonth")
    public ResponseEntity<PeriodDTO> findByMonth(@RequestParam LocalDate month) {
        log.debug("REST request to get Period by month : {}", month);
        Optional<PeriodDTO> periodDTO = periodService.findByMonth(month);
        return ResponseUtil.wrapOrNotFound(periodDTO);
    }
}
