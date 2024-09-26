package com.example.qnaboard.repository;

import com.example.qnaboard.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findByTitle(String title);
    Question findByTitleAndContent(String title, String content);
    List<Question> findByContentLike(String content);
    //Page<Question> findAll(Pageable pageable);
}
