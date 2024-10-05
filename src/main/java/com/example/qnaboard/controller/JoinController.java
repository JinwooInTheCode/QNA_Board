package com.example.qnaboard.controller;

import com.example.qnaboard.dto.JoinForm;
import com.example.qnaboard.service.JoinService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService){
        this.joinService = joinService;
    }

    @GetMapping("/join")
    public String join(JoinForm joinForm){
        return "join_form";
    }
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "join_form";
        }

        if (!joinForm.getPassword().equals(joinForm.getPasswordRepeated())) {
            bindingResult.rejectValue("passwordRepeated", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "join_form";
        }

        try {
            joinService.joinProcess(joinForm.getUsername(),
                    joinForm.getEmail(), joinForm.getPassword());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupError", "이미 등록된 사용자 ID입니다.");
            return "join_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupError", e.getMessage());
            return "join_form";
        }
        return "redirect:/";
    }
}
