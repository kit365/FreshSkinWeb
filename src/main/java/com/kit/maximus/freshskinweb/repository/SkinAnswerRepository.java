package com.kit.maximus.freshskinweb.repository;

import com.kit.maximus.freshskinweb.entity.SkinAnswerEntity;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkinAnswerRepository extends JpaRepository<SkinAnswerEntity, Long> {
}
