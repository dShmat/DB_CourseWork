package com.ibagroup.collectme.service;

import com.ibagroup.collectme.domain.Report;
import com.ibagroup.collectme.repository.ReportRepository;
import com.ibagroup.collectme.service.dto.ManagerDTO;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.service.dto.ReportDTO;
import com.ibagroup.collectme.service.mapper.ReportMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Report.
 */
@Service
@Transactional
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;

    private final ReportMapper reportMapper;

    private PeriodService periodService;

    private ManagerService managerService;

    private ProjectService projectService;

    public ReportService(ReportRepository reportRepository, ReportMapper reportMapper, PeriodService periodService, ManagerService managerService, ProjectService projectService) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.periodService = periodService;
        this.managerService = managerService;
        this.projectService = projectService;
    }

    /**
     * Save a report.
     *
     * @param reportDTO the entity to save
     * @return the persisted entity
     */
    public ReportDTO save(ReportDTO reportDTO) {
        log.debug("Request to save Report : {}", reportDTO);
        Report report = reportMapper.toEntity(reportDTO);
        report = reportRepository.save(report);
        return reportMapper.toDto(report);
    }

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ReportDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reports");
        return reportRepository.findAll(pageable)
            .map(reportMapper::toDto);
    }


    /**
     * Get one report by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ReportDTO> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findById(id)
            .map(reportMapper::toDto);
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        reportRepository.deleteById(id);
    }

    public void generateForNextMonth(PeriodDTO periodDTO) {
        log.debug("(Scheduled) Generating monthly UserManagers...");
        reportRepository.findAllByPeriod(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))
            .forEach(report -> {
                ReportDTO reportDTO = reportMapper.toDto(report);
                if (reportDTO.getProject() != null
                    && reportDTO.getUser() != null) {
                    ReportDTO reportDTONextMonth = new ReportDTO();
                    reportDTONextMonth.setProject(reportDTO.getProject());
                    reportDTONextMonth.setPeriod(periodDTO);
                    reportDTONextMonth.setUser(reportDTO.getUser());
                    save(reportDTONextMonth);
                }
            });
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> findAllByUsersAndPeriod(List<Long> userIds, Long periodId) {
        log.debug("Request to get Reports by UserIds : {}", userIds);
        ManagerDTO managerDTO = managerService.findCurrentManager().get();
        List<Long> projectIds = projectService.findAllByManagerAndPeriod(managerDTO.getId(), periodId).stream().map(p->p.getId()).collect(Collectors.toList());
        if(userIds.size() == 0 || periodId == null || projectIds.size() == 0){
            return null;
        }
        return reportRepository.findAllByUsersAndPeriod(userIds, periodId, projectIds).stream()
            .map(reportMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all User reports.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ReportDTO> getReportsByUserPeriodId(Long periodId) {
        log.debug("Request to get all User Reports");
        return reportRepository.getReportsByUserPeriodId(periodId).stream()
            .map(reportMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public ReportDTO saveNextMonth(ReportDTO reportDTO) {
        ReportDTO result = save(reportDTO);
        periodService.findNext().ifPresent(period -> {
            if(!reportRepository.findOneWithCredentials(period.getId(), result.getProject().getId(), result.getUser().getId()).isPresent()){
                ReportDTO report = new ReportDTO();
                report.setProject(result.getProject());
                report.setUser(result.getUser());
                report.setPeriod(period);
                save(report);
            }
        });
        return result;
    }
}
