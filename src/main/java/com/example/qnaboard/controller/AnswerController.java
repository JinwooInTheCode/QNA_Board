package com.example.qnaboard.controller;

import com.example.qnaboard.dto.AnswerForm;
import com.example.qnaboard.entity.Question;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.service.AnswerService;
import com.example.qnaboard.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/answer")
@Controller
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    public AnswerController(QuestionService questionService, AnswerService answerService){
        this.questionService = questionService;
        this.answerService = answerService;
    }

    // 답변 작성
    @PostMapping("/new/{id}")
    @PreAuthorize("isAuthenticated()")
    public String addAnswer(Model model, @PathVariable("id") Long id, @RequestParam(value="content") String content){
        Question question = questionService.getQuestionById(id);
        answerService.add(question, content);
        return String.format("redirect:/question/detail/%s", id);
    }
}
