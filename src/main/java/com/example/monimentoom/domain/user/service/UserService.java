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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        // 닉네임 입력을 정규화: 앞뒤 공백 제거 후, 공백만 있는 경우 null로 처리.
        String newNickname = request.getNickname();
        if (newNickname != null) {
            newNickname = newNickname.trim();
            if (newNickname.isBlank()) {
                newNickname = null;
            }
        }

        // 이름을 수정하는 경우에, 기존 닉네임과 다른 닉네임인지 확인.
        if (newNickname != null && !newNickname.equals(user.getNickname())) {
            if (userRepository.existsByNickname(newNickname)) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }
        String oldImageUrl = user.getProfileImageUrl();
        String newImageUrl = request.getProfileImageUrl();

        if (newImageUrl != null && !newImageUrl.isBlank()) {


            try {
                URI uri = URI.create(newImageUrl);
                String host = uri.getHost();
                String path = uri.getPath();
                boolean validHost = host != null && host.endsWith("amazonaws.com");
                boolean validPath = path != null && path.startsWith("/profile/");
                if (!validHost || !validPath) {
                    throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
                }
            } catch (IllegalArgumentException e) {

                throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
            }
        }

        if (oldImageUrl != null && newImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            eventPublisher.publishEvent(new S3ImageDeleteEvent(oldImageUrl));
        }

        user.updateUserProfile(
                newNickname,
                request.getDescription(),
                request.getProfileImageUrl()
        );

        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }
}
