package com.example.qnaboard.service;

import com.example.qnaboard.dto.JoinDTO;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public void joinProcess(JoinDTO joinDTO){
        //DB에 이미 동일한 username을 가진 user가 존재여부 확인
        boolean isUser = userRepository.existsByUsername(joinDTO.getUsername());
        if(isUser){
            return;
        }

        User data = new User();
        data.setUsername(joinDTO.getUsername());
        data.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}
