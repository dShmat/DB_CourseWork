package com.ibagroup.collectme.web.rest;
import com.ibagroup.collectme.service.*;
import com.ibagroup.collectme.service.dto.*;
import com.ibagroup.collectme.web.rest.errors.BadRequestAlertException;
import com.ibagroup.collectme.web.rest.util.HeaderUtil;
import com.ibagroup.collectme.web.rest.util.PaginationUtil;
import com.ibagroup.collectme.service.dto.ProjectDTO;
import com.ibagroup.collectme.service.dto.ProjectCriteria;
import com.ibagroup.collectme.service.ProjectQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.util.Optionals.ifPresentOrElse;

/**
 * REST controller for managing Project.
 */
@RestController
@RequestMapping("/api")
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "project";

    private final ProjectService projectService;

    private final ProjectQueryService projectQueryService;

    private final ManagerService managerService;

    private final PeriodService periodService;

    private final ManagerProjectService managerProjectService;

    public ProjectResource(ProjectService projectService, ProjectQueryService projectQueryService, ManagerService managerService, PeriodService periodService, ManagerProjectService managerProjectService) {
        this.projectService = projectService;
        this.projectQueryService = projectQueryService;
        this.managerService = managerService;
        this.periodService = periodService;
        this.managerProjectService = managerProjectService;
    }

    /**
     * POST  /projects : Create a new project.
     *
     * @param projectDTO the projectDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectDTO, or with status 400 (Bad Request) if the project has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.created(new URI("/api/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /projects : Updates an existing project.
     *
     * @param projectDTO the projectDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectDTO,
     * or with status 400 (Bad Request) if the projectDTO is not valid,
     * or with status 500 (Internal Server Error) if the projectDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/projects")
    public ResponseEntity<ProjectDTO> updateProject(@RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to update Project : {}", projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, projectDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /projects : get all the projects.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of projects in body
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(ProjectCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Projects by criteria: {}", criteria);
        Page<ProjectDTO> page = projectQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/projects");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /projects/count : count all the projects.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/projects/count")
    public ResponseEntity<Long> countProjects(ProjectCriteria criteria) {
        log.debug("REST request to count Projects by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /projects/:id : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectDTO, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        log.debug("REST request to get Project : {}", id);
        Optional<ProjectDTO> projectDTO = projectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * DELETE  /projects/:id : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        projectService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }


    @GetMapping("/projects/getManagerProjectsByPeriod")
    public ResponseEntity<List<ProjectDTO>> getManagerProjectsByPeriod(@RequestParam Long periodId) {
        log.debug("REST request to get current Projects by periodId ");
        final ManagerDTO[] managerDTO = {new ManagerDTO()};
        ifPresentOrElse(managerService.findCurrentManager(),
            manager ->managerDTO[0] = manager,
            ()-> log.debug("Cannot find current manager"));
        List<ProjectDTO> projectList = projectService.findAllByManagerAndPeriod(managerDTO[0].getId(), periodId);
        return ResponseEntity.ok().body(projectList);
    }

    /**
     * GET  /projects : get all the projects.
     *
     * @param projectIds the projectIds which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of projects in body
     */
    @GetMapping("/projects/getProjectsByIds")
    public ResponseEntity<List<ProjectDTO>> getProjectsByIds(@RequestParam List<Long> projectIds) {
        log.debug("REST request to get Projects by id from Reports: {}");
        List<ProjectDTO> projectList = new ArrayList<>();
        for (Long projectId: projectIds) {
            Optional<ProjectDTO> projectDTO = projectService.findOne(projectId);
            projectDTO.ifPresent(projectList::add);
        }
        return ResponseEntity.ok().body(projectList);
    }

}
