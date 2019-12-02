package com.ibagroup.collectme.service;

import com.ibagroup.collectme.domain.ManagerProject;
import com.ibagroup.collectme.repository.ManagerProjectRepository;
import com.ibagroup.collectme.repository.PeriodRepository;
import com.ibagroup.collectme.service.dto.ManagerProjectDTO;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.service.mapper.ManagerProjectMapper;
import com.ibagroup.collectme.service.mapper.PeriodMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing ManagerProject.
 */
@Service
@Transactional
public class ManagerProjectService {

    private final Logger log = LoggerFactory.getLogger(ManagerProjectService.class);

    private final ManagerProjectRepository managerProjectRepository;

    private final ManagerProjectMapper managerProjectMapper;

    private final PeriodService periodService;

    private PeriodMapper periodMapper;

    public ManagerProjectService(ManagerProjectRepository managerProjectRepository,
                                 ManagerProjectMapper managerProjectMapper,
                                 PeriodRepository periodRepository,
                                 PeriodService periodService,
                                 PeriodMapper periodMapper) {
        this.managerProjectRepository = managerProjectRepository;
        this.managerProjectMapper = managerProjectMapper;
        this.periodService = periodService;
        this.periodMapper = periodMapper;
    }

    /**
     * Save a managerProject.
     *
     * @param managerProjectDTO the entity to save
     * @return the persisted entity
     */
    public ManagerProjectDTO save(ManagerProjectDTO managerProjectDTO) {
        log.debug("Request to save ManagerProject : {}", managerProjectDTO);
        ManagerProject managerProject = managerProjectMapper.toEntity(managerProjectDTO);
        managerProject = managerProjectRepository.save(managerProject);
        return managerProjectMapper.toDto(managerProject);
    }

    public ManagerProjectDTO saveNextMonth(ManagerProjectDTO managerProjectDTO) {
        log.debug("Request to save ManagerProject : {}", managerProjectDTO);
        ManagerProjectDTO result = save(managerProjectDTO);
        periodService.findNextToPeriod(result.getPeriod()).ifPresent(period -> {
            ManagerProjectDTO managerProject = new ManagerProjectDTO();
            managerProject.setManager(result.getManager());
            managerProject.setProject(result.getProject());
            managerProject.setPeriod(period);
            save(managerProject);
        });
        return result;
    }


    /**
     * Get all the managerProjects.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ManagerProjectDTO> findAll() {
        log.debug("Request to get all ManagerProjects");
        return managerProjectRepository.findAll().stream()
            .map(managerProjectMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one managerProject by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ManagerProjectDTO> findOne(Long id) {
        log.debug("Request to get ManagerProject : {}", id);
        return managerProjectRepository.findById(id)
            .map(managerProjectMapper::toDto);
    }

    /**
     * Delete the managerProject by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ManagerProject : {}", id);
        managerProjectRepository.deleteById(id);
    }

    public void generateForNextMonth(PeriodDTO periodDTO) {
        log.debug("(Scheduled) Generating monthly UserManagers...");
        managerProjectRepository.findAllByPeriod(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))
            .forEach(managerProject -> {
                ManagerProjectDTO managerProjectDTO = managerProjectMapper.toDto(managerProject);
                if (managerProject.getManager() != null
                    && managerProject.getProject() != null) {
                    ManagerProjectDTO newManagerProjectDto = new ManagerProjectDTO();
                    newManagerProjectDto.setPeriod(periodDTO);
                    newManagerProjectDto.setProject(managerProjectDTO.getProject());
                    newManagerProjectDto.setManager(managerProjectDTO.getManager());
                    save(newManagerProjectDto);
                }
            });
    }

    @Transactional(readOnly = true)
    public Optional<ManagerProjectDTO> findCurrentWithProjectIdAndManagerId(Long projectId, Long managerId, Long periodId) {
        log.debug("Request to get ManagerProject : {}");
        return managerProjectRepository.findManagerProjectByProjectIdAndManagerIdAndPeriodId(projectId, managerId, periodId)
            .map(managerProjectMapper::toDto);
    }

}
