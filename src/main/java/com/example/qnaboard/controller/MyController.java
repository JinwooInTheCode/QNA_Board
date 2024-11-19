package com.example.qnaboard.controller;

import com.example.qnaboard.DataNotFoundException;
import com.example.qnaboard.dto.UserUpdateForm;
import com.example.qnaboard.entity.Answer;
import com.example.qnaboard.entity.Question;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.service.AnswerService;
import com.example.qnaboard.service.MyService;
import com.example.qnaboard.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class MyController {
    private final MyService myService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final JavaMailSender javaMailSender;

    public MyController(MyService myService, QuestionService questionService, AnswerService answerService, JavaMailSender javaMailSender){
        this.myService = myService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.javaMailSender = javaMailSender;
    }

    public static class PasswordGenerate {
        private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
        private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String NUMBERS = "0123456789";
        private static final String SPECIAL_CHARACTERS = "!@#$%^&*_=+-/";
        private static final String ALL = LOWER_CASE + UPPER_CASE + NUMBERS + SPECIAL_CHARACTERS;
        private static final int LENGTH = 8;

        public static String generate() {
            if(LENGTH < 1)
                throw new IllegalArgumentException("비밀번호 길이는 최소 1자 이상이어야 합니다.");
            StringBuilder password = new StringBuilder(LENGTH);
            Random random = new SecureRandom();
            for (int i = 0; i < LENGTH; i++) {
                int rndCharAt = random.nextInt(ALL.length());
                char rndChar = ALL.charAt(rndCharAt);
                password.append(ALL.charAt(rndChar));
            }
            return password.toString();
        }
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public String my(UserUpdateForm form,
                     @RequestParam(value="question-page", defaultValue="0") int questionPage,
                     @RequestParam(value="answer-page", defaultValue="0") int answerPage,
                     Principal principal, Model model){
        User user = myService.getUserByUsername(principal.getName());
        Page<Question> wroteQuestions = questionService.getAllQuestionsByAuthor(questionPage, user);
        Page<Answer> wroteAnswers = answerService.getAllAnswersByAuthor(answerPage, user);

        model.addAttribute("wroteQuestions", wroteQuestions);
        model.addAttribute("wroteAnswers", wroteAnswers);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "my";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/my")
    public String update(@Valid UserUpdateForm form, BindingResult bindingResult, Principal principal,
                         Model model){
        User user = myService.getUserByUsername(principal.getName());
        Page<Question> wroteQuestions = questionService.getAllQuestionsByAuthor(0, user);
        Page<Answer> wroteAnswers = answerService.getAllAnswersByAuthor(0, user);

        model.addAttribute("wroteQuestions", wroteQuestions);
        model.addAttribute("wroteAnswers", wroteAnswers);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userEmail", user.getEmail());

        if(bindingResult.hasErrors()){
            return "my";
        }

        if(!myService.isMatch(form.getOriginalPassword(), user.getPassword())){
            bindingResult.rejectValue("originalPassword", "passwordInCorrect", "기존 비밀번호가 일치하지 않습니다.");
            return "my";
        }
        if(!form.getNewPassword().equals(form.getNewPasswordCheck())){
            bindingResult.rejectValue("newPasswordCheck", "passwordInCorrect", "확인 비밀번호가 일치하지 않습니다.");
            return "my";
        }
        try{
            myService.update(user, form.getNewPassword());
        } catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("updateFailed", e.getMessage());
        }
        return "my";
    }

    @GetMapping("/find-email")
    public String findEmail(Model model){
        model.addAttribute("sendConfirm", false);
        model.addAttribute("error", false);
        return "find_email";
    }

    @PostMapping("/find-email")
    public String findEmail(Model model, @RequestParam(value = "email") String email){
        try {
            User user = myService.getUserByEmail(email);
            model.addAttribute("sendConfirm", true);
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("error", false);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("비밀번호 찾기 이메일");
            StringBuffer sb = new StringBuffer();

            String newPassword = PasswordGenerate.generate();
            sb.append(user.getUsername()).append("님의 새로운 비밀번호는 ").append(newPassword).append("입니다.\n").append("로그인 후 비밀번호를 변경해주세요.");
            message.setText(sb.toString());

            myService.update(user, newPassword);
            new Thread(() -> javaMailSender.send(message)).start();
        } catch (DataNotFoundException e){
            model.addAttribute("sendConfirm", false);
            model.addAttribute("error", true);
        }
        return "find_email";
    }
}
