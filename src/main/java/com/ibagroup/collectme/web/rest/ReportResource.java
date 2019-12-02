package com.ibagroup.collectme.web.rest;

import com.ibagroup.collectme.domain.UserReports;
import com.ibagroup.collectme.service.*;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.service.dto.ReportCriteria;
import com.ibagroup.collectme.service.dto.ReportDTO;
import com.ibagroup.collectme.web.rest.errors.BadRequestAlertException;
import com.ibagroup.collectme.web.rest.util.HeaderUtil;
import com.ibagroup.collectme.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * REST controller for managing Report.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    private static final String ENTITY_NAME = "report";

    private final ReportService reportService;

    private final ReportQueryService reportQueryService;

    private MailService mailService;

    private UserManagerService userManagerService;

    private ManagerService managerService;

    private PeriodService periodService;


    public ReportResource(ReportService reportService, ReportQueryService reportQueryService, MailService mailService, UserManagerService userManagerService, ManagerService managerService, PeriodService periodService) {
        this.reportService = reportService;
        this.reportQueryService = reportQueryService;
        this.mailService = mailService;
        this.userManagerService = userManagerService;
        this.managerService = managerService;
        this.periodService = periodService;
    }

    /**
     * POST  /reports : Create a new report.
     *
     * @param reportDTO the reportDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reportDTO, or with status 400 (Bad Request) if the report has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reports")
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDTO) throws URISyntaxException {
        log.debug("REST request to save Report : {}", reportDTO);
        if (reportDTO.getId() != null) {
            throw new BadRequestAlertException("A new report cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReportDTO result = reportService.save(reportDTO);
        return ResponseEntity.created(new URI("/api/reports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reports : Updates an existing report.
     *
     * @param reportDTO the reportDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reportDTO,
     * or with status 400 (Bad Request) if the reportDTO is not valid,
     * or with status 500 (Internal Server Error) if the reportDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reports")
    public ResponseEntity<ReportDTO> updateReport(@RequestBody ReportDTO reportDTO) throws URISyntaxException {
        log.debug("REST request to update Report : {}", reportDTO);
        if (reportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ReportDTO result = reportService.save(reportDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, reportDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reports : get all the reports.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     */
    @GetMapping("/reports")
    public ResponseEntity<List<ReportDTO>> getAllReports(ReportCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Reports by criteria: {}", criteria);
        Page<ReportDTO> page = reportQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /reports/count : count all the reports.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping("/reports/count")
    public ResponseEntity<Long> countReports(ReportCriteria criteria) {
        log.debug("REST request to count Reports by criteria: {}", criteria);
        return ResponseEntity.ok().body(reportQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /reports/:id : get the "id" report.
     *
     * @param id the id of the reportDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reportDTO, or with status 404 (Not Found)
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable Long id) {
        log.debug("REST request to get Report : {}", id);
        Optional<ReportDTO> reportDTO = reportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reportDTO);
    }

    /**
     * DELETE  /reports/:id : delete the "id" report.
     *
     * @param id the id of the reportDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reports/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        log.debug("REST request to delete Report : {}", id);
        reportService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/reports/getReportsByUsersAndPeriod")
    public ResponseEntity<List<ReportDTO>> getReportsByUsersAndPeriod(@RequestParam Long periodId, @RequestParam List<Long> userIds) {
        log.debug("REST request to get Reports by userIds: {}");
        List<ReportDTO> reportList = reportService.findAllByUsersAndPeriod(userIds, periodId);
        return ResponseEntity.ok().body(reportList);
    }


    /**
     * GET  /reports/getReportsByUserId : get all user reports.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of user reports in body
     */
    @GetMapping("/reports/getReportsByUserPeriodId")
    public ResponseEntity<List<ReportDTO>> getReportsByUserPeriodId(@RequestParam Long periodId) {
        log.debug("REST request to get all UserReports");
        List<ReportDTO> reportDTOList = reportService.getReportsByUserPeriodId(periodId);
        return ResponseEntity.ok().body(reportDTOList);
    }

    @PostMapping("/reports/createAutomaticallyForNextMonth")
    public ResponseEntity<ReportDTO> createAutomaticallyForNextMonth(@RequestBody ReportDTO reportDTO) throws URISyntaxException {
        log.debug("REST request to save Report : {}", reportDTO);
        if (reportDTO.getId() != null) {
            throw new BadRequestAlertException("A new report cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReportDTO result = reportService.saveNextMonth(reportDTO);
        return ResponseEntity.created(new URI("/api/reports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/reports/sendReports/{id}/{subject}")
    public void sendReports(@RequestBody String[] emails, @PathVariable Long id, @PathVariable String subject) throws Docx4JException, MessagingException, ParseException, UnsupportedEncodingException {
        subject = URLDecoder.decode(subject, "UTF-8");
        PeriodDTO periodDTO = periodService.findOne(id).get();
        List<UserReports> userReportsList = new ArrayList<>();
        userManagerService.findUsersByManagerAndPeriod(
            managerService.findCurrentManager().get().getId(), periodDTO.getId()
        ).forEach(userDTO -> {
            List<ReportDTO> reports = reportService.findAllByUsersAndPeriod(Collections.singletonList(userDTO.getId()),periodDTO.getId());
            UserReports userReports = new UserReports();
            userReports.setUser(userDTO);
            userReports.setReports(reports);
            userReportsList.add(userReports);
        });
        userReportsList.sort((a,b)->{
           return a.getUser().getFirstName().compareTo(b.getUser().getFirstName());
        });
        mailService.sendReportsFromTemplate(userReportsList, managerService.findCurrentManager().get(), periodDTO,emails, subject);
    }
}
