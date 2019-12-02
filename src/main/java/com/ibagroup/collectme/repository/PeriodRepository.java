package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Period entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {

    @Query("select period from Period period where period.month = :month")
    Optional<Period> findByMonth(@Param("month") LocalDate month);

    List<Period> findTop3ByOrderByIdDesc();

}
