package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.QuestionGroupEntity;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface QuestionGroupRepository extends JpaRepository<QuestionGroupEntity, Long>, JpaSpecificationExecutor<QuestionGroupEntity> {
}
