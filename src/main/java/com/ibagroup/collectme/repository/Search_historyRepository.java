package com.ibagroup.collectme.repository;

import com.ibagroup.collectme.domain.Search_history;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Search_history entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Search_historyRepository extends JpaRepository<Search_history, Long> {

}
