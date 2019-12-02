package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data  repository for the Report entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @Query("select report from Report report where report.user.login = ?#{principal.username}")
    List<Report> findByUserIsCurrentUser();


    @Query("select report from Report report where report.period.month = :month")
    List<Report> findAllByPeriod(@Param("month") LocalDate month);

    @Query("select distinct report from Report report where report.user.id in :userIds and report.period.id = :periodId and report.project.id in :projectIds")
    Set<Report> findAllByUsersAndPeriod(@Param("userIds") List<Long> userIds, @Param("periodId") Long periodId, @Param("projectIds") List<Long> projectIds);

    @Query("select report from Report report where report.user.login = ?#{principal.username} and report.period.id = :period_id")
    List<Report> getReportsByUserPeriodId(@Param("period_id")Long periodId);

    @Query("select report from Report report where report.period.id = :periodId and report.project.id = :projectId and report.user.id = :userId")
    Optional<Report> findOneWithCredentials(@Param("periodId") Long periodId,@Param("projectId") Long projectId,@Param("userId") Long userId);
}
