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

import com.ibagroup.collectme.domain.ManagerProject;
import com.ibagroup.collectme.domain.*; // for static metamodels
import com.ibagroup.collectme.repository.ManagerProjectRepository;
import com.ibagroup.collectme.service.dto.ManagerProjectCriteria;
import com.ibagroup.collectme.service.dto.ManagerProjectDTO;
import com.ibagroup.collectme.service.mapper.ManagerProjectMapper;

/**
 * Service for executing complex queries for ManagerProject entities in the database.
 * The main input is a {@link ManagerProjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ManagerProjectDTO} or a {@link Page} of {@link ManagerProjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ManagerProjectQueryService extends QueryService<ManagerProject> {

    private final Logger log = LoggerFactory.getLogger(ManagerProjectQueryService.class);

    private final ManagerProjectRepository managerProjectRepository;

    private final ManagerProjectMapper managerProjectMapper;

    public ManagerProjectQueryService(ManagerProjectRepository managerProjectRepository, ManagerProjectMapper managerProjectMapper) {
        this.managerProjectRepository = managerProjectRepository;
        this.managerProjectMapper = managerProjectMapper;
    }

    /**
     * Return a {@link List} of {@link ManagerProjectDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ManagerProjectDTO> findByCriteria(ManagerProjectCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ManagerProject> specification = createSpecification(criteria);
        return managerProjectMapper.toDto(managerProjectRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ManagerProjectDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ManagerProjectDTO> findByCriteria(ManagerProjectCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ManagerProject> specification = createSpecification(criteria);
        return managerProjectRepository.findAll(specification, page)
            .map(managerProjectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ManagerProjectCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ManagerProject> specification = createSpecification(criteria);
        return managerProjectRepository.count(specification);
    }

    /**
     * Function to convert ManagerProjectCriteria to a {@link Specification}
     */
    private Specification<ManagerProject> createSpecification(ManagerProjectCriteria criteria) {
        Specification<ManagerProject> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ManagerProject_.id));
            }
            if (criteria.getManagerId() != null) {
                specification = specification.and(buildSpecification(criteria.getManagerId(),
                    root -> root.join(ManagerProject_.manager, JoinType.LEFT).get(Manager_.id)));
            }
            if (criteria.getProjectId() != null) {
                specification = specification.and(buildSpecification(criteria.getProjectId(),
                    root -> root.join(ManagerProject_.project, JoinType.LEFT).get(Project_.id)));
            }
            if (criteria.getPeriodId() != null) {
                specification = specification.and(buildSpecification(criteria.getPeriodId(),
                    root -> root.join(ManagerProject_.period, JoinType.LEFT).get(Period_.id)));
            }
        }
        return specification;
    }
}
