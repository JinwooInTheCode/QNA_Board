package com.example.qnaboard.service;

import com.example.qnaboard.entity.Question;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.repository.QuestionRepository;
import com.example.qnaboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository){
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public List<Question> getAllQuestions(){
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id){
        Optional<Question> question = questionRepository.findById(id);
        if(question.isPresent())
            return question.get();
        else
            throw new IllegalStateException("해당 질문이 없습니다.");
    }
    private User getLoggedInUser(){
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if(principal instanceof UserDetails){
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
    // 글 작성
    @Transactional
    public void create(String title, String content){
        User loggedInUser = getLoggedInUser();
        if(loggedInUser == null){
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setAuthor(loggedInUser);
        question.setCreatedAt(LocalDateTime.now());
        questionRepository.save(question);
    }
    // 글 수정
    @Transactional
    public void edit(Long questionId, String newTitle, String newContent){
        Optional<Question> questionOptional = questionRepository.findById(questionId);

        if(questionOptional.isPresent()){
            Question question = questionOptional.get();
            User loggedInUser = getLoggedInUser();

            // 현재 로그인한 사용자가 해당 글의 작성자인지 혹은 관리자인지 확인
            if(loggedInUser != null && (question.getAuthor().equals(loggedInUser.getUsername()) || loggedInUser.getState().value().equals("ROLE_ADMIN"))) {
                question.setTitle(newTitle);
                question.setContent(newContent);
                question.setUpdatedAt(LocalDateTime.now());
                questionRepository.save(question);
            }
            else {
                throw new SecurityException("수정 권한이 없습니다.");
            }
        }
        else {
            throw new IllegalStateException("해당 질문이 없습니다.");
        }
    }
    // 글 삭제
    @Transactional
    public void delete(Long questionId){
        Optional<Question> questionOptional = questionRepository.findById(questionId);

        if(questionOptional.isPresent()){
            Question question = questionOptional.get();
            User loggedInUser = getLoggedInUser();

            // 현재 로그인한 사용자가 해당 글의 작성자인지 혹은 관리자인지 확인
            if(loggedInUser != null && (question.getAuthor().equals(loggedInUser.getUsername()) || loggedInUser.getState().value().equals("ROLE_ADMIN"))){
                questionRepository.delete(question);
            }
            else {
                throw new SecurityException("삭제 권한이 없습니다.");
            }
        }
        else {
            throw new IllegalStateException("해당 질문이 없습니다.");
        }
    }

    public User getUserByUsername(String name) {
        return userRepository.findByUsername(name).orElse(null);
    }
}
