package com.ibagroup.collectme.web.rest;

import com.ibagroup.collectme.CollectmeApp;

import com.ibagroup.collectme.domain.Search_history;
import com.ibagroup.collectme.repository.Search_historyRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


import static com.ibagroup.collectme.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the Search_historyResource REST controller.
 *
 * @see Search_historyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectmeApp.class)
public class Search_historyResourceIntTest {

    private static final String DEFAULT_ORIGIN = "AAAAAAAAAA";
    private static final String UPDATED_ORIGIN = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_TIME = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private Search_historyRepository search_historyRepository;

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

    private MockMvc restSearch_historyMockMvc;

    private Search_history search_history;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final Search_historyResource search_historyResource = new Search_historyResource(search_historyRepository);
        this.restSearch_historyMockMvc = MockMvcBuilders.standaloneSetup(search_historyResource)
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
    public static Search_history createEntity(EntityManager em) {
        Search_history search_history = new Search_history()
            .origin(DEFAULT_ORIGIN)
            .start_time(DEFAULT_START_TIME);
        return search_history;
    }

    @Before
    public void initTest() {
        search_history = createEntity(em);
    }

    @Test
    @Transactional
    public void createSearch_history() throws Exception {
        int databaseSizeBeforeCreate = search_historyRepository.findAll().size();

        // Create the Search_history
        restSearch_historyMockMvc.perform(post("/api/search-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(search_history)))
            .andExpect(status().isCreated());

        // Validate the Search_history in the database
        List<Search_history> search_historyList = search_historyRepository.findAll();
        assertThat(search_historyList).hasSize(databaseSizeBeforeCreate + 1);
        Search_history testSearch_history = search_historyList.get(search_historyList.size() - 1);
        assertThat(testSearch_history.getOrigin()).isEqualTo(DEFAULT_ORIGIN);
        assertThat(testSearch_history.getStart_time()).isEqualTo(DEFAULT_START_TIME);
    }

    @Test
    @Transactional
    public void createSearch_historyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = search_historyRepository.findAll().size();

        // Create the Search_history with an existing ID
        search_history.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSearch_historyMockMvc.perform(post("/api/search-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(search_history)))
            .andExpect(status().isBadRequest());

        // Validate the Search_history in the database
        List<Search_history> search_historyList = search_historyRepository.findAll();
        assertThat(search_historyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllSearch_histories() throws Exception {
        // Initialize the database
        search_historyRepository.saveAndFlush(search_history);

        // Get all the search_historyList
        restSearch_historyMockMvc.perform(get("/api/search-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(search_history.getId().intValue())))
            .andExpect(jsonPath("$.[*].origin").value(hasItem(DEFAULT_ORIGIN.toString())))
            .andExpect(jsonPath("$.[*].start_time").value(hasItem(DEFAULT_START_TIME.toString())));
    }
    
    @Test
    @Transactional
    public void getSearch_history() throws Exception {
        // Initialize the database
        search_historyRepository.saveAndFlush(search_history);

        // Get the search_history
        restSearch_historyMockMvc.perform(get("/api/search-histories/{id}", search_history.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(search_history.getId().intValue()))
            .andExpect(jsonPath("$.origin").value(DEFAULT_ORIGIN.toString()))
            .andExpect(jsonPath("$.start_time").value(DEFAULT_START_TIME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSearch_history() throws Exception {
        // Get the search_history
        restSearch_historyMockMvc.perform(get("/api/search-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSearch_history() throws Exception {
        // Initialize the database
        search_historyRepository.saveAndFlush(search_history);

        int databaseSizeBeforeUpdate = search_historyRepository.findAll().size();

        // Update the search_history
        Search_history updatedSearch_history = search_historyRepository.findById(search_history.getId()).get();
        // Disconnect from session so that the updates on updatedSearch_history are not directly saved in db
        em.detach(updatedSearch_history);
        updatedSearch_history
            .origin(UPDATED_ORIGIN)
            .start_time(UPDATED_START_TIME);

        restSearch_historyMockMvc.perform(put("/api/search-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSearch_history)))
            .andExpect(status().isOk());

        // Validate the Search_history in the database
        List<Search_history> search_historyList = search_historyRepository.findAll();
        assertThat(search_historyList).hasSize(databaseSizeBeforeUpdate);
        Search_history testSearch_history = search_historyList.get(search_historyList.size() - 1);
        assertThat(testSearch_history.getOrigin()).isEqualTo(UPDATED_ORIGIN);
        assertThat(testSearch_history.getStart_time()).isEqualTo(UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingSearch_history() throws Exception {
        int databaseSizeBeforeUpdate = search_historyRepository.findAll().size();

        // Create the Search_history

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSearch_historyMockMvc.perform(put("/api/search-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(search_history)))
            .andExpect(status().isBadRequest());

        // Validate the Search_history in the database
        List<Search_history> search_historyList = search_historyRepository.findAll();
        assertThat(search_historyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSearch_history() throws Exception {
        // Initialize the database
        search_historyRepository.saveAndFlush(search_history);

        int databaseSizeBeforeDelete = search_historyRepository.findAll().size();

        // Delete the search_history
        restSearch_historyMockMvc.perform(delete("/api/search-histories/{id}", search_history.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Search_history> search_historyList = search_historyRepository.findAll();
        assertThat(search_historyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Search_history.class);
        Search_history search_history1 = new Search_history();
        search_history1.setId(1L);
        Search_history search_history2 = new Search_history();
        search_history2.setId(search_history1.getId());
        assertThat(search_history1).isEqualTo(search_history2);
        search_history2.setId(2L);
        assertThat(search_history1).isNotEqualTo(search_history2);
        search_history1.setId(null);
        assertThat(search_history1).isNotEqualTo(search_history2);
    }
}
