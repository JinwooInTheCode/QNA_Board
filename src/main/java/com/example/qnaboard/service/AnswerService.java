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
import java.util.Optional;

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
    public Answer add(Question question, String content, User author){
        // 댓글을 작성할 질문을 찾는다.
//        Question question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setContent(content);
        answer.setAuthor(author);
        answer.setCreatedAt(LocalDateTime.now());
        answerRepository.save(answer);
        return answer;
    }
    // 댓글 조회
    public Answer getAnswer(Long id){
        Optional<Answer> answer = answerRepository.findById(id);
        if(answer.isPresent())
            return answer.get();
        else
            throw new IllegalStateException("해당 댓글이 없습니다.");
    }

    // 댓글 수정
    @Transactional
    public void edit(Answer answer, String newContent) {
        answer.setContent(newContent);
        answer.setCreatedAt(LocalDateTime.now());
        answerRepository.save(answer);
    }
    // 댓글 삭제
    @Transactional
    public void delete(Answer answer){
        answerRepository.delete(answer);
    }
}
