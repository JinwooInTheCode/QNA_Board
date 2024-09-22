package com.example.qnaboard.service;

import com.example.qnaboard.entity.Answer;
import com.example.qnaboard.entity.Question;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.repository.AnswerRepository;
import com.example.qnaboard.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository){
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    // 댓글 작성
    @Transactional
    public void add(Long questionId, String content, User author) {
        // 댓글을 작성할 질문을 찾는다.
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setContent(content);
        answer.setAuthor(author);
        answer.setCreatedAt(LocalDateTime.now());
        answerRepository.save(answer);
    }
    // 댓글 수정
    @Transactional
    public void edit(Long answerId, String newContent, User author) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        // 작성자 확인
        if(!answer.getAuthor().equals(author)){
            throw new IllegalStateException("작성자만 수정할 수 있습니다.");
        }
        answer.setContent(newContent);
        answerRepository.save(answer);
    }
    // 댓글 삭제
    @Transactional
    public void delete(Long answerId, User author){
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        // 작성자 확인
        if(!answer.getAuthor().equals(author)){
            throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
        }

        answerRepository.delete(answer);
    }
}
