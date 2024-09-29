package com.example.qnaboard.service;

import com.example.qnaboard.Role;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JoinService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User joinProcess(String username, String password, String email){
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setState(Role.USER);

        userRepository.save(user);
        return user;
    }

    public User getUser(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
            return user.get();
        else{
            throw new IllegalArgumentException("해당 사용자는 없습니다.");
        }
    }
}
