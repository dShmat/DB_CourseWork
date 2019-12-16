package com.ibagroup.collectme.web.rest;

import com.ibagroup.collectme.CollectmeApp;

import com.ibagroup.collectme.domain.Ride_passenger;
import com.ibagroup.collectme.repository.Ride_passengerRepository;
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
 * Test class for the Ride_passengerResource REST controller.
 *
 * @see Ride_passengerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectmeApp.class)
public class Ride_passengerResourceIntTest {

    @Autowired
    private Ride_passengerRepository ride_passengerRepository;

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

    private MockMvc restRide_passengerMockMvc;

    private Ride_passenger ride_passenger;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final Ride_passengerResource ride_passengerResource = new Ride_passengerResource(ride_passengerRepository);
        this.restRide_passengerMockMvc = MockMvcBuilders.standaloneSetup(ride_passengerResource)
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
    public static Ride_passenger createEntity(EntityManager em) {
        Ride_passenger ride_passenger = new Ride_passenger();
        return ride_passenger;
    }

    @Before
    public void initTest() {
        ride_passenger = createEntity(em);
    }

    @Test
    @Transactional
    public void createRide_passenger() throws Exception {
        int databaseSizeBeforeCreate = ride_passengerRepository.findAll().size();

        // Create the Ride_passenger
        restRide_passengerMockMvc.perform(post("/api/ride-passengers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ride_passenger)))
            .andExpect(status().isCreated());

        // Validate the Ride_passenger in the database
        List<Ride_passenger> ride_passengerList = ride_passengerRepository.findAll();
        assertThat(ride_passengerList).hasSize(databaseSizeBeforeCreate + 1);
        Ride_passenger testRide_passenger = ride_passengerList.get(ride_passengerList.size() - 1);
    }

    @Test
    @Transactional
    public void createRide_passengerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ride_passengerRepository.findAll().size();

        // Create the Ride_passenger with an existing ID
        ride_passenger.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRide_passengerMockMvc.perform(post("/api/ride-passengers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ride_passenger)))
            .andExpect(status().isBadRequest());

        // Validate the Ride_passenger in the database
        List<Ride_passenger> ride_passengerList = ride_passengerRepository.findAll();
        assertThat(ride_passengerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllRide_passengers() throws Exception {
        // Initialize the database
        ride_passengerRepository.saveAndFlush(ride_passenger);

        // Get all the ride_passengerList
        restRide_passengerMockMvc.perform(get("/api/ride-passengers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ride_passenger.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getRide_passenger() throws Exception {
        // Initialize the database
        ride_passengerRepository.saveAndFlush(ride_passenger);

        // Get the ride_passenger
        restRide_passengerMockMvc.perform(get("/api/ride-passengers/{id}", ride_passenger.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ride_passenger.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRide_passenger() throws Exception {
        // Get the ride_passenger
        restRide_passengerMockMvc.perform(get("/api/ride-passengers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRide_passenger() throws Exception {
        // Initialize the database
        ride_passengerRepository.saveAndFlush(ride_passenger);

        int databaseSizeBeforeUpdate = ride_passengerRepository.findAll().size();

        // Update the ride_passenger
        Ride_passenger updatedRide_passenger = ride_passengerRepository.findById(ride_passenger.getId()).get();
        // Disconnect from session so that the updates on updatedRide_passenger are not directly saved in db
        em.detach(updatedRide_passenger);

        restRide_passengerMockMvc.perform(put("/api/ride-passengers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRide_passenger)))
            .andExpect(status().isOk());

        // Validate the Ride_passenger in the database
        List<Ride_passenger> ride_passengerList = ride_passengerRepository.findAll();
        assertThat(ride_passengerList).hasSize(databaseSizeBeforeUpdate);
        Ride_passenger testRide_passenger = ride_passengerList.get(ride_passengerList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingRide_passenger() throws Exception {
        int databaseSizeBeforeUpdate = ride_passengerRepository.findAll().size();

        // Create the Ride_passenger

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRide_passengerMockMvc.perform(put("/api/ride-passengers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ride_passenger)))
            .andExpect(status().isBadRequest());

        // Validate the Ride_passenger in the database
        List<Ride_passenger> ride_passengerList = ride_passengerRepository.findAll();
        assertThat(ride_passengerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRide_passenger() throws Exception {
        // Initialize the database
        ride_passengerRepository.saveAndFlush(ride_passenger);

        int databaseSizeBeforeDelete = ride_passengerRepository.findAll().size();

        // Delete the ride_passenger
        restRide_passengerMockMvc.perform(delete("/api/ride-passengers/{id}", ride_passenger.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Ride_passenger> ride_passengerList = ride_passengerRepository.findAll();
        assertThat(ride_passengerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ride_passenger.class);
        Ride_passenger ride_passenger1 = new Ride_passenger();
        ride_passenger1.setId(1L);
        Ride_passenger ride_passenger2 = new Ride_passenger();
        ride_passenger2.setId(ride_passenger1.getId());
        assertThat(ride_passenger1).isEqualTo(ride_passenger2);
        ride_passenger2.setId(2L);
        assertThat(ride_passenger1).isNotEqualTo(ride_passenger2);
        ride_passenger1.setId(null);
        assertThat(ride_passenger1).isNotEqualTo(ride_passenger2);
    }
}
