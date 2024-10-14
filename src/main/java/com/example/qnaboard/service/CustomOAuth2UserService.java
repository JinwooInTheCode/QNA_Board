package com.example.qnaboard.service;

import com.example.qnaboard.Role;
import com.example.qnaboard.dto.*;
import com.example.qnaboard.entity.User;
import com.example.qnaboard.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    public CustomOAuth2UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("naver")){
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if(registrationId.equals("google")){
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if(registrationId.equals("kakao")){
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else if(registrationId.equals("facebook")){
            oAuth2Response = new FacebookResponse(oAuth2User.getAttributes());
        }
        else{
            return null;
        }
        
        // 구현
        String providerId = oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();

        User existData = userRepository.findByProviderId(providerId);

        Role role = null;

        // 유저 정보가 없을 경우(처음 로그인) 유저 정보 추가
        if(existData == null){
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider(registrationId);
            user.setProviderId(providerId);
            user.setRole(Role.USER);

            userRepository.save(user);
        }
        // 유저 정보가 있을 경우(로그인 이력 존재) 유저 정보 업데이트
        else{
            role = existData.getRole();
            existData.setEmail(oAuth2Response.getEmail());

            userRepository.save(existData);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
