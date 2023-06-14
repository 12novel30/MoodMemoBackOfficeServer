package com.moodmemo.office.service;

import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.repository.StampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StampService {
    private final StampRepository stampRepository;

    public StampDto.Response createStamp(StampDto.Dummy request) {

        return StampDto.Response.fromDocument(
                stampRepository.save(
                        Stamps.builder()
                                .kakaoId(request.getKakaoId())
                                .date(request.getDate())
                                .stamp(request.getStamp())
                                .memoLet(request.getMemoLet())
                                .build())
        );

    }
}
