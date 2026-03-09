package com.example.monimentoom.domain.user.service;

import com.example.monimentoom.domain.user.dto.UserLoginRequest;
import com.example.monimentoom.domain.user.dto.UserSignupRequest;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(UserSignupRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
    }

    public boolean loginUser(UserLoginRequest request){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encodedPassword);
        User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }
        // jwt 토큰 생성, 반환?
        return true;
    }

    public void logoutUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        // TODO : jwt 토큰 만료 처리
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
