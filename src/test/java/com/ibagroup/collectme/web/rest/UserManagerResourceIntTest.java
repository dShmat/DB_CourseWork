package com.ibagroup.collectme.web.rest;

import com.ibagroup.collectme.CollectmeApp;

import com.ibagroup.collectme.domain.UserManager;
import com.ibagroup.collectme.domain.Manager;
import com.ibagroup.collectme.domain.User;
import com.ibagroup.collectme.domain.Period;
import com.ibagroup.collectme.repository.UserManagerRepository;
import com.ibagroup.collectme.service.ManagerService;
import com.ibagroup.collectme.service.PeriodService;
import com.ibagroup.collectme.service.UserManagerService;
import com.ibagroup.collectme.service.dto.UserManagerDTO;
import com.ibagroup.collectme.service.mapper.UserManagerMapper;
import com.ibagroup.collectme.web.rest.errors.ExceptionTranslator;
import com.ibagroup.collectme.service.UserManagerQueryService;

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
 * Test class for the UserManagerResource REST controller.
 *
 * @see UserManagerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectmeApp.class)
public class UserManagerResourceIntTest {

    @Autowired
    private UserManagerRepository userManagerRepository;

    @Autowired
    private UserManagerMapper userManagerMapper;

    @Autowired
    private UserManagerService userManagerService;

    @Autowired
    private UserManagerQueryService userManagerQueryService;

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

    private MockMvc restUserManagerMockMvc;

    private UserManager userManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserManagerResource userManagerResource = new UserManagerResource(userManagerService, userManagerQueryService, managerService, periodService);
        this.restUserManagerMockMvc = MockMvcBuilders.standaloneSetup(userManagerResource)
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
    public static UserManager createEntity(EntityManager em) {
        UserManager userManager = new UserManager();
        return userManager;
    }

    @Before
    public void initTest() {
        userManager = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserManager() throws Exception {
        int databaseSizeBeforeCreate = userManagerRepository.findAll().size();

        // Create the UserManager
        UserManagerDTO userManagerDTO = userManagerMapper.toDto(userManager);
        restUserManagerMockMvc.perform(post("/api/user-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userManagerDTO)))
            .andExpect(status().isCreated());

        // Validate the UserManager in the database
        List<UserManager> userManagerList = userManagerRepository.findAll();
        assertThat(userManagerList).hasSize(databaseSizeBeforeCreate + 1);
        UserManager testUserManager = userManagerList.get(userManagerList.size() - 1);
    }

    @Test
    @Transactional
    public void createUserManagerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userManagerRepository.findAll().size();

        // Create the UserManager with an existing ID
        userManager.setId(1L);
        UserManagerDTO userManagerDTO = userManagerMapper.toDto(userManager);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserManagerMockMvc.perform(post("/api/user-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userManagerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserManager in the database
        List<UserManager> userManagerList = userManagerRepository.findAll();
        assertThat(userManagerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUserManagers() throws Exception {
        // Initialize the database
        userManagerRepository.saveAndFlush(userManager);

        // Get all the userManagerList
        restUserManagerMockMvc.perform(get("/api/user-managers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userManager.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getUserManager() throws Exception {
        // Initialize the database
        userManagerRepository.saveAndFlush(userManager);

        // Get the userManager
        restUserManagerMockMvc.perform(get("/api/user-managers/{id}", userManager.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userManager.getId().intValue()));
    }

    @Test
    @Transactional
    public void getAllUserManagersByManagerIsEqualToSomething() throws Exception {
        // Initialize the database
        Manager manager = ManagerResourceIntTest.createEntity(em);
        em.persist(manager);
        em.flush();
        userManager.setManager(manager);
        userManagerRepository.saveAndFlush(userManager);
        Long managerId = manager.getId();

        // Get all the userManagerList where manager equals to managerId
        defaultUserManagerShouldBeFound("managerId.equals=" + managerId);

        // Get all the userManagerList where manager equals to managerId + 1
        defaultUserManagerShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }


    @Test
    @Transactional
    public void getAllUserManagersByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        userManager.setUser(user);
        userManagerRepository.saveAndFlush(userManager);
        Long userId = user.getId();

        // Get all the userManagerList where user equals to userId
        defaultUserManagerShouldBeFound("userId.equals=" + userId);

        // Get all the userManagerList where user equals to userId + 1
        defaultUserManagerShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllUserManagersByPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        Period period = PeriodResourceIntTest.createEntity(em);
        em.persist(period);
        em.flush();
        userManager.setPeriod(period);
        userManagerRepository.saveAndFlush(userManager);
        Long periodId = period.getId();

        // Get all the userManagerList where period equals to periodId
        defaultUserManagerShouldBeFound("periodId.equals=" + periodId);

        // Get all the userManagerList where period equals to periodId + 1
        defaultUserManagerShouldNotBeFound("periodId.equals=" + (periodId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultUserManagerShouldBeFound(String filter) throws Exception {
        restUserManagerMockMvc.perform(get("/api/user-managers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userManager.getId().intValue())));

        // Check, that the count call also returns 1
        restUserManagerMockMvc.perform(get("/api/user-managers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultUserManagerShouldNotBeFound(String filter) throws Exception {
        restUserManagerMockMvc.perform(get("/api/user-managers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserManagerMockMvc.perform(get("/api/user-managers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingUserManager() throws Exception {
        // Get the userManager
        restUserManagerMockMvc.perform(get("/api/user-managers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserManager() throws Exception {
        // Initialize the database
        userManagerRepository.saveAndFlush(userManager);

        int databaseSizeBeforeUpdate = userManagerRepository.findAll().size();

        // Update the userManager
        UserManager updatedUserManager = userManagerRepository.findById(userManager.getId()).get();
        // Disconnect from session so that the updates on updatedUserManager are not directly saved in db
        em.detach(updatedUserManager);
        UserManagerDTO userManagerDTO = userManagerMapper.toDto(updatedUserManager);

        restUserManagerMockMvc.perform(put("/api/user-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userManagerDTO)))
            .andExpect(status().isOk());

        // Validate the UserManager in the database
        List<UserManager> userManagerList = userManagerRepository.findAll();
        assertThat(userManagerList).hasSize(databaseSizeBeforeUpdate);
        UserManager testUserManager = userManagerList.get(userManagerList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingUserManager() throws Exception {
        int databaseSizeBeforeUpdate = userManagerRepository.findAll().size();

        // Create the UserManager
        UserManagerDTO userManagerDTO = userManagerMapper.toDto(userManager);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserManagerMockMvc.perform(put("/api/user-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userManagerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserManager in the database
        List<UserManager> userManagerList = userManagerRepository.findAll();
        assertThat(userManagerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserManager() throws Exception {
        // Initialize the database
        userManagerRepository.saveAndFlush(userManager);

        int databaseSizeBeforeDelete = userManagerRepository.findAll().size();

        // Delete the userManager
        restUserManagerMockMvc.perform(delete("/api/user-managers/{id}", userManager.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserManager> userManagerList = userManagerRepository.findAll();
        assertThat(userManagerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserManager.class);
        UserManager userManager1 = new UserManager();
        userManager1.setId(1L);
        UserManager userManager2 = new UserManager();
        userManager2.setId(userManager1.getId());
        assertThat(userManager1).isEqualTo(userManager2);
        userManager2.setId(2L);
        assertThat(userManager1).isNotEqualTo(userManager2);
        userManager1.setId(null);
        assertThat(userManager1).isNotEqualTo(userManager2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserManagerDTO.class);
        UserManagerDTO userManagerDTO1 = new UserManagerDTO();
        userManagerDTO1.setId(1L);
        UserManagerDTO userManagerDTO2 = new UserManagerDTO();
        assertThat(userManagerDTO1).isNotEqualTo(userManagerDTO2);
        userManagerDTO2.setId(userManagerDTO1.getId());
        assertThat(userManagerDTO1).isEqualTo(userManagerDTO2);
        userManagerDTO2.setId(2L);
        assertThat(userManagerDTO1).isNotEqualTo(userManagerDTO2);
        userManagerDTO1.setId(null);
        assertThat(userManagerDTO1).isNotEqualTo(userManagerDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userManagerMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userManagerMapper.fromId(null)).isNull();
    }
}
