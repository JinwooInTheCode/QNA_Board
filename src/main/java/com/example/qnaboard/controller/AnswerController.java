package com.example.qnaboard.controller;

import com.example.qnaboard.dto.AnswerForm;
import com.example.qnaboard.entity.Question;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.service.AnswerService;
import com.example.qnaboard.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/answer")
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
    public String addAnswer(Model model, @PathVariable("id") Long id,
                            @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal){
        Question question = questionService.getQuestionById(id);
        User author = questionService.getUserByUsername(principal.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "question_detail";
        }
        answerService.add(question.getId(), answerForm.getContent(), author);
        return "redirect:/question/detail/" + id;
    }
}
