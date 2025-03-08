package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SearchKeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<SearchKeywordEntity, Long>, JpaSpecificationExecutor<SearchKeywordEntity> {
    SearchKeywordEntity findByKeyword(String keyword);

}
