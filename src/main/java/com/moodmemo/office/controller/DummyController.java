package com.moodmemo.office.controller;

import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class DummyController {

    private final UserService userService;
    private final StampService stampService;

    @PostMapping("/add-user")
    public UserDto.Response createUser(@Valid @RequestBody UserDto.Dummy request) {
        return userService.createUser(request);
    }

    @PostMapping("/add-stamp")
    public StampDto.Response createStamp(@Valid @RequestBody StampDto.Dummy request) {
        return stampService.createStamp(request);
    }
}
