package com.moodmemo.office.service;

import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.dto.DailyReportDto;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.repository.StampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public DailyReportDto createDailyReport(String kakaoId) {
        // Todo - 날짜 일치하는거 확인하는 메소드 필요함
        // Todo - 그래서 dateformat 한 번 갈아야 한다...
        return DailyReportDto.builder()
                .kakaoId(kakaoId)
                .date(LocalDate.now())
                .username("이하은")
                .title("오늘의 일기")
                .bodyText("진짜 .. 진짜 자고 싶다 공부 너무 하기 싫다 아악 ... 시험 왜 보냐 진짜 ...")
                .keyword(List.of("지겨움", "시험"))
                .build();
    }
}
