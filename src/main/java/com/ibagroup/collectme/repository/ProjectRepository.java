package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.Project;
import com.ibagroup.collectme.service.dto.ProjectDTO;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Query("select project from Project project inner join ManagerProject manager_project on project.id = manager_project.project.id " +
        "where manager_project.manager.id = :managerId and manager_project.period.id = :periodId")
    List<Project> findAllByManagerAndPeriod(@Param("managerId") Long managerId, @Param("periodId") Long periodId);
}
