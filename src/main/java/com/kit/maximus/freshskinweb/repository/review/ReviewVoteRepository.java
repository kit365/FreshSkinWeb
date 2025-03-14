package com.kit.maximus.freshskinweb.repository.review;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.entity.review.ReviewVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ReviewVoteRepository extends JpaRepository<ReviewVoteEntity, Long> {


}
