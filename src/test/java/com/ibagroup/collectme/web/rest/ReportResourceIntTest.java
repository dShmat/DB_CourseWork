package com.ibagroup.collectme.web.rest;

import com.ibagroup.collectme.CollectmeApp;

import com.ibagroup.collectme.domain.Report;
import com.ibagroup.collectme.domain.User;
import com.ibagroup.collectme.domain.Project;
import com.ibagroup.collectme.domain.Period;
import com.ibagroup.collectme.repository.ReportRepository;
import com.ibagroup.collectme.service.*;
import com.ibagroup.collectme.service.dto.ReportDTO;
import com.ibagroup.collectme.service.mapper.ReportMapper;
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
 * Test class for the ReportResource REST controller.
 *
 * @see ReportResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectmeApp.class)
public class ReportResourceIntTest {

    private static final String DEFAULT_HOURS = "AAAAAAAAAA";
    private static final String UPDATED_HOURS = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIVITIES = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVITIES = "BBBBBBBBBB";

    private static final Integer DEFAULT_DAYS_ABSENT = 1;
    private static final Integer UPDATED_DAYS_ABSENT = 2;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportQueryService reportQueryService;

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

    private MockMvc restReportMockMvc;

    private Report report;
    @Autowired
    private MailService mailService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private PeriodService periodService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ReportResource reportResource = new ReportResource(reportService, reportQueryService, mailService, userManagerService, managerService, periodService);
        this.restReportMockMvc = MockMvcBuilders.standaloneSetup(reportResource)
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
    public static Report createEntity(EntityManager em) {
        Report report = new Report()
            .hours(DEFAULT_HOURS)
            .activities(DEFAULT_ACTIVITIES)
            .daysAbsent(DEFAULT_DAYS_ABSENT);
        return report;
    }

    @Before
    public void initTest() {
        report = createEntity(em);
    }

    @Test
    @Transactional
    public void createReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);
        restReportMockMvc.perform(post("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportDTO)))
            .andExpect(status().isCreated());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testReport.getActivities()).isEqualTo(DEFAULT_ACTIVITIES);
        assertThat(testReport.getDaysAbsent()).isEqualTo(DEFAULT_DAYS_ABSENT);
    }

    @Test
    @Transactional
    public void createReportWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report with an existing ID
        report.setId(1L);
        ReportDTO reportDTO = reportMapper.toDto(report);

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportMockMvc.perform(post("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllReports() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS.toString())))
            .andExpect(jsonPath("$.[*].activities").value(hasItem(DEFAULT_ACTIVITIES.toString())))
            .andExpect(jsonPath("$.[*].daysAbsent").value(hasItem(DEFAULT_DAYS_ABSENT)));
    }
    
    @Test
    @Transactional
    public void getReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(report.getId().intValue()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS.toString()))
            .andExpect(jsonPath("$.activities").value(DEFAULT_ACTIVITIES.toString()))
            .andExpect(jsonPath("$.daysAbsent").value(DEFAULT_DAYS_ABSENT));
    }

    @Test
    @Transactional
    public void getAllReportsByHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where hours equals to DEFAULT_HOURS
        defaultReportShouldBeFound("hours.equals=" + DEFAULT_HOURS);

        // Get all the reportList where hours equals to UPDATED_HOURS
        defaultReportShouldNotBeFound("hours.equals=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    public void getAllReportsByHoursIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where hours in DEFAULT_HOURS or UPDATED_HOURS
        defaultReportShouldBeFound("hours.in=" + DEFAULT_HOURS + "," + UPDATED_HOURS);

        // Get all the reportList where hours equals to UPDATED_HOURS
        defaultReportShouldNotBeFound("hours.in=" + UPDATED_HOURS);
    }

    @Test
    @Transactional
    public void getAllReportsByHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where hours is not null
        defaultReportShouldBeFound("hours.specified=true");

        // Get all the reportList where hours is null
        defaultReportShouldNotBeFound("hours.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByActivitiesIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where activities equals to DEFAULT_ACTIVITIES
        defaultReportShouldBeFound("activities.equals=" + DEFAULT_ACTIVITIES);

        // Get all the reportList where activities equals to UPDATED_ACTIVITIES
        defaultReportShouldNotBeFound("activities.equals=" + UPDATED_ACTIVITIES);
    }

    @Test
    @Transactional
    public void getAllReportsByActivitiesIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where activities in DEFAULT_ACTIVITIES or UPDATED_ACTIVITIES
        defaultReportShouldBeFound("activities.in=" + DEFAULT_ACTIVITIES + "," + UPDATED_ACTIVITIES);

        // Get all the reportList where activities equals to UPDATED_ACTIVITIES
        defaultReportShouldNotBeFound("activities.in=" + UPDATED_ACTIVITIES);
    }

    @Test
    @Transactional
    public void getAllReportsByActivitiesIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where activities is not null
        defaultReportShouldBeFound("activities.specified=true");

        // Get all the reportList where activities is null
        defaultReportShouldNotBeFound("activities.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByDaysAbsentIsEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where daysAbsent equals to DEFAULT_DAYS_ABSENT
        defaultReportShouldBeFound("daysAbsent.equals=" + DEFAULT_DAYS_ABSENT);

        // Get all the reportList where daysAbsent equals to UPDATED_DAYS_ABSENT
        defaultReportShouldNotBeFound("daysAbsent.equals=" + UPDATED_DAYS_ABSENT);
    }

    @Test
    @Transactional
    public void getAllReportsByDaysAbsentIsInShouldWork() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where daysAbsent in DEFAULT_DAYS_ABSENT or UPDATED_DAYS_ABSENT
        defaultReportShouldBeFound("daysAbsent.in=" + DEFAULT_DAYS_ABSENT + "," + UPDATED_DAYS_ABSENT);

        // Get all the reportList where daysAbsent equals to UPDATED_DAYS_ABSENT
        defaultReportShouldNotBeFound("daysAbsent.in=" + UPDATED_DAYS_ABSENT);
    }

    @Test
    @Transactional
    public void getAllReportsByDaysAbsentIsNullOrNotNull() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where daysAbsent is not null
        defaultReportShouldBeFound("daysAbsent.specified=true");

        // Get all the reportList where daysAbsent is null
        defaultReportShouldNotBeFound("daysAbsent.specified=false");
    }

    @Test
    @Transactional
    public void getAllReportsByDaysAbsentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where daysAbsent greater than or equals to DEFAULT_DAYS_ABSENT
        defaultReportShouldBeFound("daysAbsent.greaterOrEqualThan=" + DEFAULT_DAYS_ABSENT);

        // Get all the reportList where daysAbsent greater than or equals to UPDATED_DAYS_ABSENT
        defaultReportShouldNotBeFound("daysAbsent.greaterOrEqualThan=" + UPDATED_DAYS_ABSENT);
    }

    @Test
    @Transactional
    public void getAllReportsByDaysAbsentIsLessThanSomething() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reportList where daysAbsent less than or equals to DEFAULT_DAYS_ABSENT
        defaultReportShouldNotBeFound("daysAbsent.lessThan=" + DEFAULT_DAYS_ABSENT);

        // Get all the reportList where daysAbsent less than or equals to UPDATED_DAYS_ABSENT
        defaultReportShouldBeFound("daysAbsent.lessThan=" + UPDATED_DAYS_ABSENT);
    }


    @Test
    @Transactional
    public void getAllReportsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        report.setUser(user);
        reportRepository.saveAndFlush(report);
        Long userId = user.getId();

        // Get all the reportList where user equals to userId
        defaultReportShouldBeFound("userId.equals=" + userId);

        // Get all the reportList where user equals to userId + 1
        defaultReportShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllReportsByProjectIsEqualToSomething() throws Exception {
        // Initialize the database
        Project project = ProjectResourceIntTest.createEntity(em);
        em.persist(project);
        em.flush();
        report.setProject(project);
        reportRepository.saveAndFlush(report);
        Long projectId = project.getId();

        // Get all the reportList where project equals to projectId
        defaultReportShouldBeFound("projectId.equals=" + projectId);

        // Get all the reportList where project equals to projectId + 1
        defaultReportShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }


    @Test
    @Transactional
    public void getAllReportsByPeriodIsEqualToSomething() throws Exception {
        // Initialize the database
        Period period = PeriodResourceIntTest.createEntity(em);
        em.persist(period);
        em.flush();
        report.setPeriod(period);
        reportRepository.saveAndFlush(report);
        Long periodId = period.getId();

        // Get all the reportList where period equals to periodId
        defaultReportShouldBeFound("periodId.equals=" + periodId);

        // Get all the reportList where period equals to periodId + 1
        defaultReportShouldNotBeFound("periodId.equals=" + (periodId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultReportShouldBeFound(String filter) throws Exception {
        restReportMockMvc.perform(get("/api/reports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)))
            .andExpect(jsonPath("$.[*].activities").value(hasItem(DEFAULT_ACTIVITIES)))
            .andExpect(jsonPath("$.[*].daysAbsent").value(hasItem(DEFAULT_DAYS_ABSENT)));

        // Check, that the count call also returns 1
        restReportMockMvc.perform(get("/api/reports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultReportShouldNotBeFound(String filter) throws Exception {
        restReportMockMvc.perform(get("/api/reports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReportMockMvc.perform(get("/api/reports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).get();
        // Disconnect from session so that the updates on updatedReport are not directly saved in db
        em.detach(updatedReport);
        updatedReport
            .hours(UPDATED_HOURS)
            .activities(UPDATED_ACTIVITIES)
            .daysAbsent(UPDATED_DAYS_ABSENT);
        ReportDTO reportDTO = reportMapper.toDto(updatedReport);

        restReportMockMvc.perform(put("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportDTO)))
            .andExpect(status().isOk());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testReport.getActivities()).isEqualTo(UPDATED_ACTIVITIES);
        assertThat(testReport.getDaysAbsent()).isEqualTo(UPDATED_DAYS_ABSENT);
    }

    @Test
    @Transactional
    public void updateNonExistingReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportMockMvc.perform(put("/api/reports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        int databaseSizeBeforeDelete = reportRepository.findAll().size();

        // Delete the report
        restReportMockMvc.perform(delete("/api/reports/{id}", report.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Report> reportList = reportRepository.findAll();
        assertThat(reportList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Report.class);
        Report report1 = new Report();
        report1.setId(1L);
        Report report2 = new Report();
        report2.setId(report1.getId());
        assertThat(report1).isEqualTo(report2);
        report2.setId(2L);
        assertThat(report1).isNotEqualTo(report2);
        report1.setId(null);
        assertThat(report1).isNotEqualTo(report2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReportDTO.class);
        ReportDTO reportDTO1 = new ReportDTO();
        reportDTO1.setId(1L);
        ReportDTO reportDTO2 = new ReportDTO();
        assertThat(reportDTO1).isNotEqualTo(reportDTO2);
        reportDTO2.setId(reportDTO1.getId());
        assertThat(reportDTO1).isEqualTo(reportDTO2);
        reportDTO2.setId(2L);
        assertThat(reportDTO1).isNotEqualTo(reportDTO2);
        reportDTO1.setId(null);
        assertThat(reportDTO1).isNotEqualTo(reportDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(reportMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(reportMapper.fromId(null)).isNull();
    }
}
