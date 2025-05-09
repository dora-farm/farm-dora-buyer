package com.farmdora.farmdorabuyer.question.repository;

import com.farmdora.farmdorabuyer.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
