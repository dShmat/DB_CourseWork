package com.ibagroup.collectme.web.rest;

import com.ibagroup.collectme.CollectmeApp;

import com.ibagroup.collectme.domain.ManagerProject;
import com.ibagroup.collectme.domain.Manager;
import com.ibagroup.collectme.domain.Project;
import com.ibagroup.collectme.domain.Period;
import com.ibagroup.collectme.repository.ManagerProjectRepository;
import com.ibagroup.collectme.service.*;
import com.ibagroup.collectme.service.dto.ManagerProjectDTO;
import com.ibagroup.collectme.service.mapper.ManagerMapper;
import com.ibagroup.collectme.service.mapper.ManagerProjectMapper;
import com.ibagroup.collectme.service.mapper.ProjectMapper;
import com.ibagroup.collectme.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.ibagroup.collectme.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ManagerProjectResource REST controller.
 *
 * @see ManagerProjectResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectmeApp.class)
public class ManagerProjectResourceIntTest {

    @Autowired
    private ManagerProjectRepository managerProjectRepository;

    @Autowired
    private ManagerProjectMapper managerProjectMapper;

    @Autowired
    private ManagerProjectService managerProjectService;

    @Autowired
    private ManagerProjectQueryService managerProjectQueryService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restManagerProjectMockMvc;
    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectMapper projectMapper;

    private ManagerProject managerProject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ManagerProjectResource managerProjectResource = new ManagerProjectResource(managerProjectService, managerProjectQueryService, managerService,
            managerMapper, periodService, projectService, projectMapper);
        this.restManagerProjectMockMvc = MockMvcBuilders.standaloneSetup(managerProjectResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ManagerProject createEntity(EntityManager em) {
        ManagerProject managerProject = new ManagerProject();
        return managerProject;
    }

    @Before
    public void initTest() {
        managerProject = createEntity(em);
    }

    @Test
    @Transactional
    public void createManagerProject() throws Exception {
        int databaseSizeBeforeCreate = managerProjectRepository.findAll().size();

        // Create the ManagerProject
        ManagerProjectDTO managerProjectDTO = managerProjectMapper.toDto(managerProject);
        restManagerProjectMockMvc.perform(post("/api/manager-projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(managerProjectDTO)))
            .andExpect(status().isCreated());

        // Validate the ManagerProject in the database
        List<ManagerProject> managerProjectList = managerProjectRepository.findAll();
        assertThat(managerProjectList).hasSize(databaseSizeBeforeCreate + 1);
        ManagerProject testManagerProject = managerProjectList.get(managerProjectList.size() - 1);
    }

    @Test
    @Transactional
    public void createManagerProjectWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = managerProjectRepository.findAll().size();

        // Create the ManagerProject with an existing ID
        managerProject.setId(1L);
        ManagerProjectDTO managerProjectDTO = managerProjectMapper.toDto(managerProject);

        // An entity with an existing ID cannot be created, so this API call must fail
        restManagerProjectMockMvc.perform(post("/api/manager-projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(managerProjectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ManagerProject in the database
        List<ManagerProject> managerProjectList = managerProjectRepository.findAll();
        assertThat(managerProjectList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllManagerProjects() throws Exception {
        // Initialize the database
        managerProjectRepository.saveAndFlush(managerProject);

        // Get all the managerProjectList
        restManagerProjectMockMvc.perform(get("/api/manager-projects?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(managerProject.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getManagerProject() throws Exception {
        // Initialize the database
        managerProjectRepository.saveAndFlush(managerProject);

        // Get the managerProject
        restManagerProjectMockMvc.perform(get("/api/manager-projects/{id}", managerProject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(managerProject.getId().intValue()));
    }

    @Test
    @Transactional
    public void getAllManagerProjectsByManagerIsEqualToSomething() throws Exception {
        // Initialize the database
        Manager manager = ManagerResourceIntTest.createEntity(em);
        em.persist(manager);
        em.flush();
        managerProject.setManager(manager);
        managerProjectRepository.saveAndFlush(managerProject);
        Long managerId = manager.getId();

        // Get all the managerProjectList where manager equals to managerId
        defaultManagerProjectShouldBeFound("managerId.equals=" + managerId);

        // Get all the managerProjectList where manager equals to managerId + 1
        defaultManagerProjectShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }


    @Test
    @Transactional
    public void getAllManagerProjectsByProjectIsEqualToSomething() throws Exception {
        // Initialize the database
        Project project = ProjectResourceIntTest.createEntity(em);
        em.persist(project);
        em.flush();
        managerProject.setProject(project);
        managerProjectRepository.saveAndFlush(managerProject);
        Long projectId = project.getId();

        // Get all the managerProjectList where project equals to projectId
        defaultManagerProjectShouldBeFound("projectId.equals=" + projectId);

        // Get all the managerProjectList where project equals to projectId + 1
        defaultManagerProjectShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }


    @Test
    @Transactional
    public void getAllManagerProjectsByPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        Period period = PeriodResourceIntTest.createEntity(em);
        em.persist(period);
        em.flush();
        managerProject.setPeriod(period);
        managerProjectRepository.saveAndFlush(managerProject);
        Long periodId = period.getId();

        // Get all the managerProjectList where period equals to periodId
        defaultManagerProjectShouldBeFound("periodId.equals=" + periodId);

        // Get all the managerProjectList where period equals to periodId + 1
        defaultManagerProjectShouldNotBeFound("periodId.equals=" + (periodId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultManagerProjectShouldBeFound(String filter) throws Exception {
        restManagerProjectMockMvc.perform(get("/api/manager-projects?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(managerProject.getId().intValue())));

        // Check, that the count call also returns 1
        restManagerProjectMockMvc.perform(get("/api/manager-projects/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultManagerProjectShouldNotBeFound(String filter) throws Exception {
        restManagerProjectMockMvc.perform(get("/api/manager-projects?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restManagerProjectMockMvc.perform(get("/api/manager-projects/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingManagerProject() throws Exception {
        // Get the managerProject
        restManagerProjectMockMvc.perform(get("/api/manager-projects/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateManagerProject() throws Exception {
        // Initialize the database
        managerProjectRepository.saveAndFlush(managerProject);

        int databaseSizeBeforeUpdate = managerProjectRepository.findAll().size();

        // Update the managerProject
        ManagerProject updatedManagerProject = managerProjectRepository.findById(managerProject.getId()).get();
        // Disconnect from session so that the updates on updatedManagerProject are not directly saved in db
        em.detach(updatedManagerProject);
        ManagerProjectDTO managerProjectDTO = managerProjectMapper.toDto(updatedManagerProject);

        restManagerProjectMockMvc.perform(put("/api/manager-projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(managerProjectDTO)))
            .andExpect(status().isOk());

        // Validate the ManagerProject in the database
        List<ManagerProject> managerProjectList = managerProjectRepository.findAll();
        assertThat(managerProjectList).hasSize(databaseSizeBeforeUpdate);
        ManagerProject testManagerProject = managerProjectList.get(managerProjectList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingManagerProject() throws Exception {
        int databaseSizeBeforeUpdate = managerProjectRepository.findAll().size();

        // Create the ManagerProject
        ManagerProjectDTO managerProjectDTO = managerProjectMapper.toDto(managerProject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restManagerProjectMockMvc.perform(put("/api/manager-projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(managerProjectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ManagerProject in the database
        List<ManagerProject> managerProjectList = managerProjectRepository.findAll();
        assertThat(managerProjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteManagerProject() throws Exception {
        // Initialize the database
        managerProjectRepository.saveAndFlush(managerProject);

        int databaseSizeBeforeDelete = managerProjectRepository.findAll().size();

        // Delete the managerProject
        restManagerProjectMockMvc.perform(delete("/api/manager-projects/{id}", managerProject.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ManagerProject> managerProjectList = managerProjectRepository.findAll();
        assertThat(managerProjectList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ManagerProject.class);
        ManagerProject managerProject1 = new ManagerProject();
        managerProject1.setId(1L);
        ManagerProject managerProject2 = new ManagerProject();
        managerProject2.setId(managerProject1.getId());
        assertThat(managerProject1).isEqualTo(managerProject2);
        managerProject2.setId(2L);
        assertThat(managerProject1).isNotEqualTo(managerProject2);
        managerProject1.setId(null);
        assertThat(managerProject1).isNotEqualTo(managerProject2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ManagerProjectDTO.class);
        ManagerProjectDTO managerProjectDTO1 = new ManagerProjectDTO();
        managerProjectDTO1.setId(1L);
        ManagerProjectDTO managerProjectDTO2 = new ManagerProjectDTO();
        assertThat(managerProjectDTO1).isNotEqualTo(managerProjectDTO2);
        managerProjectDTO2.setId(managerProjectDTO1.getId());
        assertThat(managerProjectDTO1).isEqualTo(managerProjectDTO2);
        managerProjectDTO2.setId(2L);
        assertThat(managerProjectDTO1).isNotEqualTo(managerProjectDTO2);
        managerProjectDTO1.setId(null);
        assertThat(managerProjectDTO1).isNotEqualTo(managerProjectDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(managerProjectMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(managerProjectMapper.fromId(null)).isNull();
    }
}
