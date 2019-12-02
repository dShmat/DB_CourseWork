package com.ibagroup.collectme.service;

import com.ibagroup.collectme.domain.UserManager;
import com.ibagroup.collectme.repository.UserManagerRepository;
import com.ibagroup.collectme.service.dto.PeriodDTO;
import com.ibagroup.collectme.service.dto.UserDTO;
import com.ibagroup.collectme.service.dto.UserManagerDTO;
import com.ibagroup.collectme.service.mapper.UserManagerMapper;
import com.ibagroup.collectme.service.mapper.UserMapper;
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
 * Service Implementation for managing UserManager.
 */
@Service
@Transactional
public class UserManagerService {

    private final Logger log = LoggerFactory.getLogger(UserManagerService.class);

    private final UserManagerRepository userManagerRepository;

    private final UserManagerMapper userManagerMapper;

    private final UserMapper userMapper;

    private PeriodService periodService;

    public UserManagerService(UserManagerRepository userManagerRepository, UserManagerMapper userManagerMapper, UserMapper userMapper, PeriodService periodService) {
        this.userManagerRepository = userManagerRepository;
        this.userManagerMapper = userManagerMapper;
        this.userMapper = userMapper;
        this.periodService = periodService;
    }

    /**
     * Save a userManager.
     *
     * @param userManagerDTO the entity to save
     * @return the persisted entity
     */
    public UserManagerDTO save(UserManagerDTO userManagerDTO) {
        log.debug("Request to save UserManager : {}", userManagerDTO);
        UserManager userManager = userManagerMapper.toEntity(userManagerDTO);
        userManager = userManagerRepository.save(userManager);
        return userManagerMapper.toDto(userManager);
    }

    /**
     * Get all the userManagers.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<UserManagerDTO> findAll() {
        log.debug("Request to get all UserManagers");
        return userManagerRepository.findAll().stream()
            .map(userManagerMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one userManager by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<UserManagerDTO> findOne(Long id) {
        log.debug("Request to get UserManager : {}", id);
        return userManagerRepository.findById(id)
            .map(userManagerMapper::toDto);
    }

    /**
     * Delete the userManager by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UserManager : {}", id);
        userManagerRepository.deleteById(id);
    }


    public void generateForNextMonth(PeriodDTO periodDTO) {
        log.debug("(Scheduled) Generating monthly UserManagers...");
        userManagerRepository.findAllByPeriod(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))
            .forEach(userManager -> {
                UserManagerDTO userManagerDTO = userManagerMapper.toDto(userManager);
                if (userManagerDTO.getUser() != null &&
                    userManagerDTO.getManager() != null) {
                    UserManagerDTO userManagerDTONextMonth = new UserManagerDTO();
                    userManagerDTONextMonth.setUser(userManagerDTO.getUser());
                    userManagerDTONextMonth.setManager(userManagerDTO.getManager());
                    userManagerDTONextMonth.setPeriod(periodDTO);
                    save(userManagerDTONextMonth);
                }
            });
    }
    @Transactional(readOnly = true)
    public List<UserDTO> findUsersByManagerAndPeriod(Long managerId, Long periodId) {
        log.debug("Request to get Users by manager id");
        return userManagerRepository.findUsersByManagerAndPeriod(managerId, periodId).stream()
            .map(userMapper::userToUserDTO)
            .collect(Collectors.toCollection(LinkedList::new));

    }

    public UserManagerDTO saveNextMonth(UserManagerDTO userManagerDTO) {
        log.debug("Request to save ManagerProject : {}", userManagerDTO);
        UserManagerDTO result = save(userManagerDTO);
        periodService.findNext().ifPresent(period -> {
            if(!userManagerRepository.findOneWithCredentials(period.getId(), result.getUser().getId(), result.getManager().getId())
                .isPresent()){
                UserManagerDTO userManager = new UserManagerDTO();
                userManager.setManager(result.getManager());
                userManager.setUser(result.getUser());
                userManager.setPeriod(period);
                save(userManager);
            }
        });
        return result;
    }

    public void deleteWithCredentials(Long periodId, Long userId, Long managerId) {
        userManagerRepository.findOneWithCredentials(periodId, userId, managerId).ifPresent(userManager -> {
            delete(userManager.getId());
        });
    }
}
