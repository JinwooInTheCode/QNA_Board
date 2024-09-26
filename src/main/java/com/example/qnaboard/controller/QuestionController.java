package com.example.qnaboard.controller;
import com.example.qnaboard.dto.AnswerForm;
import com.example.qnaboard.dto.QuestionForm;
import com.example.qnaboard.entity.Question;
import com.example.qnaboard.repository.QuestionRepository;
import com.example.qnaboard.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@Controller
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/list")
    public String QuestionList(Model model) {
        List<Question> questionList = questionService.getAllQuestions();
        model.addAttribute("questionList", questionList);
        return "question_list";
    }

    @GetMapping(value ="/detail/{id}")
    public String QuestionDetail(Model model, @PathVariable("id") Long id) {
        Question question = questionService.getQuestionById(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String createQuestion(QuestionForm questionForm) {
        return "question_form";
    }

    @PostMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "question_form";
        questionService.create(questionForm.getTitle(), questionForm.getContent());
        return "redirect:/question/list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editQuestion(QuestionForm questionForm, @PathVariable("id") Long id, Principal principal) {
        Question question = questionService.getQuestionById(id);
        questionForm.setTitle(question.getTitle());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult,
                               Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors())
            return "question_form";
        Question question = questionService.getQuestionById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new SecurityException("본인이 작성한 글만 수정할 수 있습니다.");
        }
        questionService.edit(id, questionForm.getTitle(), questionForm.getContent());
        return "redirect:/question/detail/" + id;
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteQuestion(@PathVariable Long id, Principal principal) {
        Question question = questionService.getQuestionById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new SecurityException("본인이 작성한 글만 삭제할 수 있습니다.");
        }
        questionService.delete(id);
        return "redirect:/question";
    }
}
