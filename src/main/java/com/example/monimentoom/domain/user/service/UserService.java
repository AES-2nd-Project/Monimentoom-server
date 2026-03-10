package com.example.monimentoom.domain.user.service;

import com.example.monimentoom.domain.room.dto.RoomResponse;
import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.dto.UserLoginRequest;
import com.example.monimentoom.domain.user.dto.UserResponse;
import com.example.monimentoom.domain.user.dto.UserSignupRequest;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoomRepository roomRepository;

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
    public RoomResponse updateMainRoom(Long userId, Long roomId) {
        Room newMainRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        newMainRoom.validateOwnership(userId);
        User user = newMainRoom.getUser();
        user.setMainRoom(newMainRoom);
        return RoomResponse.from(newMainRoom);
    }

    // 닉네임 방문
    @Transactional(readOnly = true)
    public RoomResponse visitByNickname(Long userId, String nickname) {
        Room room = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getMainRoom();
//        내 방인지 일치하게 컬럼 넣어줘야함 response에...
        return RoomResponse.from(room);
    }

}
