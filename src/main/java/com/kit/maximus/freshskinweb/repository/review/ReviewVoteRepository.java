package com.kit.maximus.freshskinweb.repository.review;

import com.kit.maximus.freshskinweb.dto.request.review.ReviewVoteRequest;
import com.kit.maximus.freshskinweb.entity.review.ReviewVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ReviewVoteRepository extends JpaRepository<ReviewVoteEntity, Long> {

}
