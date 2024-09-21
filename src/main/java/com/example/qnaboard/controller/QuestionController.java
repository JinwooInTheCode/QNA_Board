package com.example.qnaboard.controller;
import com.example.qnaboard.service.QuestionService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qna")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService){
        this.questionService = questionService;
    }
    @GetMapping("/list")
    public String QuestionList(){
        return "question_list";
    }
    @PostMapping("/new")
    public String createQuestion(@RequestParam String title, @RequestParam String content){
        try {
            questionService.createQuestion(title, content);
            return "redirect:/qna";
        } catch(IllegalArgumentException e){
            return "redirect:/login";
        }
    }
    @PostMapping("/edit/{id}")
    public String editQuestion(@PathVariable Long id, @RequestParam String title, @RequestParam String content, Model model){
        try {
            questionService.editQuestion(id, title, content);
            return "redirect:/qna";
        } catch(SecurityException e){
            model.addAttribute("error", "본인이 작성한 글만 수정할 수 있습니다.");
            return "error";
        }
    }
    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, Model model){
        try {
            questionService.deleteQuestion(id);
            return "redirect:/qna";
        } catch(SecurityException e){
            model.addAttribute("error", "본인이 작성한 글만 삭제할 수 있습니다.");
            return "error";
        }
    }
}
