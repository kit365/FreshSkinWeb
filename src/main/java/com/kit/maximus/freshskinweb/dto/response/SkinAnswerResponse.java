package com.kit.maximus.freshskinweb.dto.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.maximus.freshskinweb.entity.AbstractEntity;
import com.kit.maximus.freshskinweb.entity.SkinQuestionsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SkinAnswerResponse implements Serializable {

    Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String option;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long score;

}
