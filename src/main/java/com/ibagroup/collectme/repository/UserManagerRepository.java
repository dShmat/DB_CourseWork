package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.User;
import com.ibagroup.collectme.domain.UserManager;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the UserManager entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserManagerRepository extends JpaRepository<UserManager, Long>, JpaSpecificationExecutor<UserManager> {

    @Query("select user_manager from UserManager user_manager where user_manager.user.login = ?#{principal.username}")
    List<UserManager> findByUserIsCurrentUser();

    @Query("select user_manager from UserManager user_manager where user_manager.period.month = :month")
    List<UserManager> findAllByPeriod(@Param("month") LocalDate month);

    @Query("select jhi_user from User jhi_user inner join UserManager user_manager on jhi_user.id = user_manager.user.id " +
        "where user_manager.manager.id = :managerId and user_manager.period.id = :periodId")
    List<User> findUsersByManagerAndPeriod(@Param("managerId") Long managerId, @Param("periodId") Long periodId);

    @Query("select user_manager from UserManager user_manager where user_manager.period.id = :periodId and user_manager.user.id = :userId and user_manager.manager.id = :managerId")
    Optional<UserManager> findOneWithCredentials(@Param("periodId") Long periodId, @Param("userId") Long userId, @Param("managerId") Long managerId);
}
