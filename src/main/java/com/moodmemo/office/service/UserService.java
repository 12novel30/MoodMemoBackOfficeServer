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

    public void updateWeekCount(String kakaoId, int weekNum) {
        // TODO - 에러처리하기
        Users user = userRepository.findByKakaoId(kakaoId);

        if (weekNum == 1)
            user.setWeek1(user.getWeek1() + 1);
        else if (weekNum == 2)
            user.setWeek2(user.getWeek2() + 1);
        else if (weekNum == 3)
            user.setWeek3(user.getWeek3() + 1);
        else if (weekNum == 4)
            user.setWeek4(user.getWeek4() + 1);

        userRepository.save(user);
    }
}
