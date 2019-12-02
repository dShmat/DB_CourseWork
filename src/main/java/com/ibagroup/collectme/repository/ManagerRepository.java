package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Manager entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long>, JpaSpecificationExecutor<Manager> {

    @Query("select manager from Manager manager where manager.user.login = :email")
    Optional<Manager> findManagerByCurrentUser(@Param("email") String email);

    @Query("select manager from Manager manager where manager.user.id = :userId")
    Optional<Manager> findByUser(@Param("userId") Long userId);
}
