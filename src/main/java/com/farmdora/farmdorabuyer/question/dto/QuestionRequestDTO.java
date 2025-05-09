package com.farmdora.farmdorabuyer.question.dto;

import com.farmdora.farmdorabuyer.entity.Question;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuestionRequestDTO {
    private String title;
    private String content;

    public Question toEntity(User user, Sale sale, QuestionRequestDTO request) {
        return Question.builder()
                .user(user)
                .sale(sale)
                .title(request.title)
                .content(request.content)
                .answer(null)
                .isProcess(false)
                .isBlind(false)
                .build();
    }
}
