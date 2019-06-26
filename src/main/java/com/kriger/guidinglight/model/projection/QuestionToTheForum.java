package com.kriger.guidinglight.model.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionToTheForum {

    private Long id;
    private String title;
    private String content;
    private Integer answerSize;
    private LocalDateTime submissionTime;

}
