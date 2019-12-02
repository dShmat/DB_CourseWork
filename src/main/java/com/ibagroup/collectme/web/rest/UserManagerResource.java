package com.ibagroup.collectme.web.rest;
import com.ibagroup.collectme.domain.Manager;
import com.ibagroup.collectme.service.ManagerService;
import com.ibagroup.collectme.service.PeriodService;
import com.ibagroup.collectme.service.UserManagerService;
import com.ibagroup.collectme.service.dto.*;
import com.ibagroup.collectme.web.rest.errors.BadRequestAlertException;
import com.ibagroup.collectme.web.rest.util.HeaderUtil;
import com.ibagroup.collectme.service.UserManagerQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import static org.springframework.data.util.Optionals.ifPresentOrElse;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UserManager.
 */
@RestController
@RequestMapping("/api")
public class UserManagerResource {

    private final Logger log = LoggerFactory.getLogger(UserManagerResource.class);

    private static final String ENTITY_NAME = "userManager";

    private final UserManagerService userManagerService;

    private final UserManagerQueryService userManagerQueryService;

    private final ManagerService managerService;

    private final PeriodService periodService;

    public UserManagerResource(UserManagerService userManagerService, UserManagerQueryService userManagerQueryService, ManagerService managerService, PeriodService periodService) {
        this.userManagerService = userManagerService;
        this.userManagerQueryService = userManagerQueryService;
        this.managerService = managerService;
        this.periodService = periodService;
    }

    /**
     * POST  /user-managers : Create a new userManager.
     *
     * @param userManagerDTO the userManagerDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userManagerDTO, or with status 400 (Bad Request) if the userManager has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-managers")
    public ResponseEntity<UserManagerDTO> createUserManager(@RequestBody UserManagerDTO userManagerDTO) throws URISyntaxException {
        log.debug("REST request to save UserManager : {}", userManagerDTO);
        if (userManagerDTO.getId() != null) {
            throw new BadRequestAlertException("A new userManager cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserManagerDTO result = userManagerService.save(userManagerDTO);
        return ResponseEntity.created(new URI("/api/user-managers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-managers : Updates an existing userManager.
     *
     * @param userManagerDTO the userManagerDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userManagerDTO,
     * or with status 400 (Bad Request) if the userManagerDTO is not valid,
     * or with status 500 (Internal Server Error) if the userManagerDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-managers")
    public ResponseEntity<UserManagerDTO> updateUserManager(@RequestBody UserManagerDTO userManagerDTO) throws URISyntaxException {
        log.debug("REST request to update UserManager : {}", userManagerDTO);
        if (userManagerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserManagerDTO result = userManagerService.save(userManagerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userManagerDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-managers : get all the userManagers.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of userManagers in body
     */
    @GetMapping("/user-managers")
    public ResponseEntity<List<UserManagerDTO>> getAllUserManagers(UserManagerCriteria criteria) {
        log.debug("REST request to get UserManagers by criteria: {}", criteria);
        List<UserManagerDTO> entityList = userManagerQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
    * GET  /user-managers/count : count all the userManagers.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/user-managers/count")
    public ResponseEntity<Long> countUserManagers(UserManagerCriteria criteria) {
        log.debug("REST request to count UserManagers by criteria: {}", criteria);
        return ResponseEntity.ok().body(userManagerQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /user-managers/:id : get the "id" userManager.
     *
     * @param id the id of the userManagerDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userManagerDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-managers/{id}")
    public ResponseEntity<UserManagerDTO> getUserManager(@PathVariable Long id) {
        log.debug("REST request to get UserManager : {}", id);
        Optional<UserManagerDTO> userManagerDTO = userManagerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userManagerDTO);
    }

    /**
     * DELETE  /user-managers/:id : delete the "id" userManager.
     *
     * @param id the id of the userManagerDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-managers/{id}")
    public ResponseEntity<Void> deleteUserManager(@PathVariable Long id) {
        log.debug("REST request to delete UserManager : {}", id);
        userManagerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/user-managers/getManagerUsersByPeriod")
    public ResponseEntity<List<UserDTO>> getManagerUsersByPeriod(@RequestParam Long periodId) {
        log.debug("REST request to get current Manager Users : {}", periodId);
        final ManagerDTO[] managerDTO = {new ManagerDTO()};
        ifPresentOrElse(managerService.findCurrentManager(),
            manager ->managerDTO[0] = manager,
            ()-> log.debug("Cannot find current manager"));
        List<UserDTO> userList = userManagerService.findUsersByManagerAndPeriod(managerDTO[0].getId(), periodId);
        return ResponseEntity.ok().body(userList);
    }

    @PostMapping("/user-managers/createUserManagerFromUser")
    public ResponseEntity<UserManagerDTO> createUserManagerFromUser(@RequestBody UserManagerDTO userManagerDTO) throws URISyntaxException {
        log.debug("REST request to save UserManager : {}");
        this.managerService.findCurrentManager().ifPresent(managerDTO -> {
            userManagerDTO.setManager(managerDTO);
        });
        UserManagerDTO result = userManagerService.saveNextMonth(userManagerDTO);
        return ResponseEntity.created(new URI("/api/user-managers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @DeleteMapping("/user-managers/deleteByUserAndPeriod")
    public ResponseEntity<Void> deleteByUserAndPeriod( @RequestParam Long periodId, @RequestParam Long userId) {
        log.debug("REST request to delete UserManager : {}");
        ManagerDTO managerDTO = managerService.findCurrentManager().get();
        userManagerService.deleteWithCredentials(periodId, userId, managerDTO.getId());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, userId.toString())).build();
    }

}
