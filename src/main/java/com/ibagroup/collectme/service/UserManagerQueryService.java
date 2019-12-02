package com.ibagroup.collectme.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.ibagroup.collectme.domain.UserManager;
import com.ibagroup.collectme.domain.*; // for static metamodels
import com.ibagroup.collectme.repository.UserManagerRepository;
import com.ibagroup.collectme.service.dto.UserManagerCriteria;
import com.ibagroup.collectme.service.dto.UserManagerDTO;
import com.ibagroup.collectme.service.mapper.UserManagerMapper;

/**
 * Service for executing complex queries for UserManager entities in the database.
 * The main input is a {@link UserManagerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserManagerDTO} or a {@link Page} of {@link UserManagerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserManagerQueryService extends QueryService<UserManager> {

    private final Logger log = LoggerFactory.getLogger(UserManagerQueryService.class);

    private final UserManagerRepository userManagerRepository;

    private final UserManagerMapper userManagerMapper;

    public UserManagerQueryService(UserManagerRepository userManagerRepository, UserManagerMapper userManagerMapper) {
        this.userManagerRepository = userManagerRepository;
        this.userManagerMapper = userManagerMapper;
    }

    /**
     * Return a {@link List} of {@link UserManagerDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserManagerDTO> findByCriteria(UserManagerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserManager> specification = createSpecification(criteria);
        return userManagerMapper.toDto(userManagerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserManagerDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserManagerDTO> findByCriteria(UserManagerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserManager> specification = createSpecification(criteria);
        return userManagerRepository.findAll(specification, page)
            .map(userManagerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserManagerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserManager> specification = createSpecification(criteria);
        return userManagerRepository.count(specification);
    }

    /**
     * Function to convert UserManagerCriteria to a {@link Specification}
     */
    private Specification<UserManager> createSpecification(UserManagerCriteria criteria) {
        Specification<UserManager> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), UserManager_.id));
            }
            if (criteria.getManagerId() != null) {
                specification = specification.and(buildSpecification(criteria.getManagerId(),
                    root -> root.join(UserManager_.manager, JoinType.LEFT).get(Manager_.id)));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(UserManager_.user, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getPeriodId() != null) {
                specification = specification.and(buildSpecification(criteria.getPeriodId(),
                    root -> root.join(UserManager_.period, JoinType.LEFT).get(Period_.id)));
            }
        }
        return specification;
    }
}
