package com.ibagroup.collectme.web.rest;
import com.ibagroup.collectme.service.*;
import com.ibagroup.collectme.service.dto.*;
import com.ibagroup.collectme.service.mapper.ManagerMapper;
import com.ibagroup.collectme.service.mapper.ProjectMapper;
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

import static org.springframework.data.util.Optionals.ifPresentOrElse;

/**
 * REST controller for managing ManagerProject.
 */
@RestController
@RequestMapping("/api")
public class ManagerProjectResource {

    private final Logger log = LoggerFactory.getLogger(ManagerProjectResource.class);

    private static final String ENTITY_NAME = "managerProject";

    private final ManagerProjectService managerProjectService;

    private final ManagerProjectQueryService managerProjectQueryService;

    private final ManagerService managerService;

    private final ManagerMapper managerMapper;

    private final PeriodService periodService;

    private final ProjectService projectService;

    private final ProjectMapper projectMapper;

    public ManagerProjectResource(ManagerProjectService managerProjectService, ManagerProjectQueryService managerProjectQueryService, ManagerService managerService, ManagerMapper managerMapper, PeriodService periodService, ProjectService projectService, ProjectMapper projectMapper) {
        this.managerProjectService = managerProjectService;
        this.managerProjectQueryService = managerProjectQueryService;
        this.managerService = managerService;
        this.managerMapper = managerMapper;
        this.periodService = periodService;
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    /**
     * POST  /manager-projects : Create a new managerProject.
     *
     * @param managerProjectDTO the managerProjectDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new managerProjectDTO, or with status 400 (Bad Request) if the managerProject has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/manager-projects")
    public ResponseEntity<ManagerProjectDTO> createManagerProject(@RequestBody ManagerProjectDTO managerProjectDTO) throws URISyntaxException {
        log.debug("REST request to save ManagerProject : {}", managerProjectDTO);
        if (managerProjectDTO.getId() != null) {
            throw new BadRequestAlertException("A new managerProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ManagerProjectDTO result = managerProjectService.save(managerProjectDTO);
        return ResponseEntity.created(new URI("/api/manager-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /manager-projects : Updates an existing managerProject.
     *
     * @param managerProjectDTO the managerProjectDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated managerProjectDTO,
     * or with status 400 (Bad Request) if the managerProjectDTO is not valid,
     * or with status 500 (Internal Server Error) if the managerProjectDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/manager-projects")
    public ResponseEntity<ManagerProjectDTO> updateManagerProject(@RequestBody ManagerProjectDTO managerProjectDTO) throws URISyntaxException {
        log.debug("REST request to update ManagerProject : {}", managerProjectDTO);
        if (managerProjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ManagerProjectDTO result = managerProjectService.save(managerProjectDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, managerProjectDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /manager-projects : get all the managerProjects.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of managerProjects in body
     */
    @GetMapping("/manager-projects")
    public ResponseEntity<List<ManagerProjectDTO>> getAllManagerProjects(ManagerProjectCriteria criteria) {
        log.debug("REST request to get ManagerProjects by criteria: {}", criteria);
        List<ManagerProjectDTO> entityList = managerProjectQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
    * GET  /manager-projects/count : count all the managerProjects.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/manager-projects/count")
    public ResponseEntity<Long> countManagerProjects(ManagerProjectCriteria criteria) {
        log.debug("REST request to count ManagerProjects by criteria: {}", criteria);
        return ResponseEntity.ok().body(managerProjectQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /manager-projects/:id : get the "id" managerProject.
     *
     * @param id the id of the managerProjectDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the managerProjectDTO, or with status 404 (Not Found)
     */
    @GetMapping("/manager-projects/{id}")
    public ResponseEntity<ManagerProjectDTO> getManagerProject(@PathVariable Long id) {
        log.debug("REST request to get ManagerProject : {}", id);
        Optional<ManagerProjectDTO> managerProjectDTO = managerProjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(managerProjectDTO);
    }

    /**
     * DELETE  /manager-projects/:id : delete the "id" managerProject.
     *
     * @param id the id of the managerProjectDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/manager-projects/{id}")
    public ResponseEntity<Void> deleteManagerProject(@PathVariable Long id) {
        log.debug("REST request to delete ManagerProject : {}", id);
        managerProjectService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @DeleteMapping("/manager-projects/deleteByProjectAndPeriod")
    public ResponseEntity<Void> deleteByProjectAndPeriod(@RequestParam Long projectId, @RequestParam Long periodId) {
        log.debug("REST request to delete ManagerProject from projectId");
        final ManagerDTO[] managerDTO = {new ManagerDTO()};
        ifPresentOrElse(managerService.findCurrentManager(),
            manager ->managerDTO[0] = manager,
            ()-> log.debug("Cannot find current manager"));
        ManagerProjectDTO managerProjectDTO = managerProjectService.findCurrentWithProjectIdAndManagerId(projectId, managerDTO[0].getId(), periodId).get();
        managerProjectService.delete(managerProjectDTO.getId());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, projectId.toString())).build();
    }

    @PostMapping("/manager-projects/createManagerProjectAndProject")
    public ResponseEntity<ManagerProjectDTO> createManagerProjectAndProject(@RequestBody ManagerProjectDTO managerProjectDTO) throws URISyntaxException {
        log.debug("REST request to save ManagerProject : {}", managerProjectDTO);
        if (managerProjectDTO.getId() != null) {
            throw new BadRequestAlertException("A new managerProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectDTO projectDTO = projectService.save(managerProjectDTO.getProject());
        managerService.findCurrentManager().ifPresent(managerDTO -> managerProjectDTO.setManager(managerDTO));
        managerProjectDTO.setProject(projectDTO);
        ManagerProjectDTO result = managerProjectService.saveNextMonth(managerProjectDTO);
        return ResponseEntity.created(new URI("/api/manager-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
