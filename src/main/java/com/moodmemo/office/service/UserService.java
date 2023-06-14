package com.moodmemo.office.service;

import com.moodmemo.office.domain.Users;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto.Response createUser(UserDto.Dummy request) {

        return UserDto.Response.fromDocuments(
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

    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto.Response::fromDocuments)
                .collect(Collectors.toList());
    }
}
