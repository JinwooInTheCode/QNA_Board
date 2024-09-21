package com.example.qnaboard.repository;

import com.example.qnaboard.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long>{
}
