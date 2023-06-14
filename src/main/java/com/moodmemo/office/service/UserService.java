package com.moodmemo.office.service;

import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto.Response createUser(UserDto.Dummy request) {

        return UserDto.Response.fromDocument(
                userRepository.save(
                        Users.builder()
                                .age(request.getAge())
                                .kakaoId(request.getKakaoId())
                                .username(request.getUsername())
                                .job(request.getJob())
                                .gender(request.isGender())
                                .build())
        );

    }
}
