package com.ibagroup.collectme.web.rest;
import com.ibagroup.collectme.domain.Search_history;
import com.ibagroup.collectme.repository.Search_historyRepository;
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
 * REST controller for managing Search_history.
 */
@RestController
@RequestMapping("/api")
public class Search_historyResource {

    private final Logger log = LoggerFactory.getLogger(Search_historyResource.class);

    private static final String ENTITY_NAME = "search_history";

    private final Search_historyRepository search_historyRepository;

    public Search_historyResource(Search_historyRepository search_historyRepository) {
        this.search_historyRepository = search_historyRepository;
    }

    /**
     * POST  /search-histories : Create a new search_history.
     *
     * @param search_history the search_history to create
     * @return the ResponseEntity with status 201 (Created) and with body the new search_history, or with status 400 (Bad Request) if the search_history has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/search-histories")
    public ResponseEntity<Search_history> createSearch_history(@RequestBody Search_history search_history) throws URISyntaxException {
        log.debug("REST request to save Search_history : {}", search_history);
        if (search_history.getId() != null) {
            throw new BadRequestAlertException("A new search_history cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Search_history result = search_historyRepository.save(search_history);
        return ResponseEntity.created(new URI("/api/search-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /search-histories : Updates an existing search_history.
     *
     * @param search_history the search_history to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated search_history,
     * or with status 400 (Bad Request) if the search_history is not valid,
     * or with status 500 (Internal Server Error) if the search_history couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/search-histories")
    public ResponseEntity<Search_history> updateSearch_history(@RequestBody Search_history search_history) throws URISyntaxException {
        log.debug("REST request to update Search_history : {}", search_history);
        if (search_history.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Search_history result = search_historyRepository.save(search_history);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, search_history.getId().toString()))
            .body(result);
    }

    /**
     * GET  /search-histories : get all the search_histories.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of search_histories in body
     */
    @GetMapping("/search-histories")
    public List<Search_history> getAllSearch_histories() {
        log.debug("REST request to get all Search_histories");
        return search_historyRepository.findAll();
    }

    /**
     * GET  /search-histories/:id : get the "id" search_history.
     *
     * @param id the id of the search_history to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the search_history, or with status 404 (Not Found)
     */
    @GetMapping("/search-histories/{id}")
    public ResponseEntity<Search_history> getSearch_history(@PathVariable Long id) {
        log.debug("REST request to get Search_history : {}", id);
        Optional<Search_history> search_history = search_historyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(search_history);
    }

    /**
     * DELETE  /search-histories/:id : delete the "id" search_history.
     *
     * @param id the id of the search_history to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/search-histories/{id}")
    public ResponseEntity<Void> deleteSearch_history(@PathVariable Long id) {
        log.debug("REST request to delete Search_history : {}", id);
        search_historyRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
