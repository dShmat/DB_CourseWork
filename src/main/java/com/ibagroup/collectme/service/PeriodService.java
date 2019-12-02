package com.ibagroup.collectme.service;

import com.ibagroup.collectme.domain.Period;
import com.ibagroup.collectme.repository.PeriodRepository;
import com.ibagroup.collectme.service.dto.PeriodDTO;
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
 * Service Implementation for managing Period.
 */
@Service
@Transactional
public class PeriodService {

    private final Logger log = LoggerFactory.getLogger(PeriodService.class);

    private final PeriodRepository periodRepository;

    private final PeriodMapper periodMapper;

    public PeriodService(PeriodRepository periodRepository, PeriodMapper periodMapper) {
        this.periodRepository = periodRepository;
        this.periodMapper = periodMapper;
    }

    /**
     * Save a period.
     *
     * @param periodDTO the entity to save
     * @return the persisted entity
     */
    public PeriodDTO save(PeriodDTO periodDTO) {
        log.debug("Request to save Period : {}", periodDTO);
        Period period = periodMapper.toEntity(periodDTO);
        period = periodRepository.save(period);
        return periodMapper.toDto(period);
    }

    /**
     * Get all the periods.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<PeriodDTO> findAll() {
        log.debug("Request to get all Periods");
        return periodRepository.findAll().stream()
            .map(periodMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one period by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<PeriodDTO> findOne(Long id) {
        log.debug("Request to get Period : {}", id);
        return periodRepository.findById(id)
            .map(periodMapper::toDto);
    }

    /**
     * Delete the period by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Period : {}", id);
        periodRepository.deleteById(id);
    }

    public PeriodDTO generateForNextMonth() {
        log.debug("(Scheduled) Generating new month period...");
        PeriodDTO nextPeriod = new PeriodDTO();
        nextPeriod.setMonth(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()));
        PeriodDTO result = save(nextPeriod);
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<PeriodDTO> findCurrent() {
        LocalDate now = LocalDate.now();
        log.debug("Request to get Period : {}");
        return periodRepository.findByMonth(now.with(TemporalAdjusters.firstDayOfMonth())).map(periodMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PeriodDTO> findByMonth(LocalDate month) {
        log.debug("Request to get Period by month : {}");
        return periodRepository.findByMonth(month).map(periodMapper::toDto);

    }

    @Transactional(readOnly = true)
    public Optional<PeriodDTO> findNext() {
        return periodRepository.findByMonth(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth())).map(periodMapper::toDto);

    }

    @Transactional(readOnly = true)
    public Optional<PeriodDTO> findNextToPeriod(PeriodDTO periodDTO) {
        return periodRepository.findByMonth(periodDTO.getMonth().with(TemporalAdjusters.firstDayOfNextMonth())).map(periodMapper::toDto);

    }
}
