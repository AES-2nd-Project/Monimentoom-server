package com.example.monimentoom.domain.user.service;

import com.example.monimentoom.domain.room.dto.RoomBasicResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.dto.*;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.s3.event.S3ImageDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserResponse createUser(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        Room defaultRoom = Room.builder()
                .name(user.getNickname() + "님의 첫 번째 방")
                .user(user)
                .build();
        roomRepository.save(defaultRoom);

        user.setMainRoom(defaultRoom);

        return UserResponse.from(user);
    }

    // jwt 토큰 생성, 반환은 컨트롤러에서 처리
    public UserResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_LOGIN_INPUT_VALUE));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_INPUT_VALUE);
        }
        return UserResponse.from(user);
    }

    // TODO : jwt 토큰 만료 처리
    public void logoutUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void deleteUser(Long userId, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.validateOwnership(userId);
        userRepository.delete(user);
    }

    @Transactional
    public RoomBasicResponse updateMainRoom(Long userId, Long roomId) {
        Room newMainRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        newMainRoom.validateOwnership(userId);
        User user = newMainRoom.getUser();
        user.setMainRoom(newMainRoom);
        return RoomBasicResponse.from(newMainRoom);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이름을 수정하는 경우에, 기존 닉네임과 다른 닉네임인지 확인.
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if(userRepository.existsByNickname(request.getNickname())){
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }
        String oldImageUrl = user.getProfileImageUrl();
        String newImageUrl = request.getProfileImageUrl();
        if (oldImageUrl != null && newImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            eventPublisher.publishEvent(new S3ImageDeleteEvent(oldImageUrl));
        }

        user.updateUserProfile(request);
        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }
}
