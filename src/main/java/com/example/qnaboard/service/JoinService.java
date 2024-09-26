package com.example.qnaboard.service;

import com.example.qnaboard.Role;
import com.example.qnaboard.dto.JoinDTO;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JoinService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public void joinProcess(JoinDTO joinDTO){
        //DB에 이미 동일한 username을 가진 user가 존재여부 확인
        boolean isUser = userRepository.existsByUsername(joinDTO.getUsername());
        if(isUser){
            return;
        }

        User data = new User();
        data.setUsername(joinDTO.getUsername());
        data.setPassword(passwordEncoder.encode(joinDTO.getPassword()));
        data.setState(Role.USER);

        userRepository.save(data);
    }
}
