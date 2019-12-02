package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.ManagerProject;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the ManagerProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ManagerProjectRepository extends JpaRepository<ManagerProject, Long>, JpaSpecificationExecutor<ManagerProject> {

    @Query("select manager_project from ManagerProject manager_project where manager_project.period.month = :period")
    List<ManagerProject> findAllByPeriod(@Param("period") LocalDate period);

    @Query("select manager_project from ManagerProject manager_project where manager_project.project.id = :projectId " +
        "and manager_project.period.id = :periodId and manager_project.manager.id = :managerId")
    Optional<ManagerProject> findManagerProjectByProjectIdAndManagerIdAndPeriodId(@Param("projectId") Long projectId,
                                                                                  @Param("managerId") Long managerId,
                                                                                  @Param("periodId") Long periodId);
}
