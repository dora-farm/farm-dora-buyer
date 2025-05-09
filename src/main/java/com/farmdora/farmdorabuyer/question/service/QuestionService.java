package com.farmdora.farmdorabuyer.question.service;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Question;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.repository.SaleRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import com.farmdora.farmdorabuyer.question.dto.QuestionRequestDTO;
import com.farmdora.farmdorabuyer.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final SaleRepository saleRepository;

    public void addQuestion(Integer userId, Integer saleId, QuestionRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        Question question = request.toEntity(user, sale, request);
        questionRepository.save(question);
    }
}
