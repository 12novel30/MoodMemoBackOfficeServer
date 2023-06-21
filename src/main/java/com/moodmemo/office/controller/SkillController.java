package com.moodmemo.office.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmemo.office.service.KakaoService;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api/kakao")
public class SkillController {

    private final KakaoService kakaoService;
    private final StampService stampService;
    private final UserService userService;


    @PostMapping("/userInfo")
    public HashMap<String, Object> callUserInfoAPI(@Valid @RequestBody Map<String, Object> params) throws JsonProcessingException {
        userService.createUser(kakaoService.getInfoParams(params));
        // TODO - 여기에서 에러처리하고 그 메세지를 보내는 방향으로 수정할 것
        // TODO - 이미 저장된 정보면 그거에 관련한 에러처리 보내기
        return kakaoService.getStringObjectHashMap("userInfo 발화리턴");
        // TODO - 다른 모든 메소드 에러처리
    }

    @PostMapping("/stamp")
    public HashMap<String, Object> callStampAPI(@Valid @RequestBody Map<String, Object> params) throws JsonProcessingException {
        // save stamp
        String kakaoId =
                stampService.createStamp(
                        kakaoService.getStampParams(params)).getKakaoId();
        // update week n 의 스탬프 개수
        userService.updateWeekCount(kakaoId, stampService.validateWeek());
        // TODO - 여기에서 에러처리하고 그 메세지를 보내는 방향으로 수정할 것
        return kakaoService.getStringObjectHashMap("memolet 발화리턴");
    }

    @PostMapping("/timeChange-stamp")
    public HashMap<String, Object> callTimeChangedStampAPI(@Valid @RequestBody Map<String, Object> params) throws JsonProcessingException {
        // save stamp
        String kakaoId =
                stampService.createStamp(
                        kakaoService.getTimeChangedStampParams(params)).getKakaoId();
        // update week n 의 스탬프 개수
        userService.updateWeekCount(kakaoId, stampService.validateWeek());
        // TODO - 여기에서 에러처리하고 그 메세지를 보내는 방향으로 수정할 것
        return kakaoService.getStringObjectHashMap("시간 변경 버전 memolet 발화리턴");
    }

    // TODO - 기타 스탬프 처리하는 방법 생각해보기
    @PostMapping("/stampList") // TODO - checking
    public HashMap<String, Object> callStampListAPI(@Valid @RequestBody Map<String, Object> params) throws JsonProcessingException {
        // TODO - 시간순으로 정렬하기 (최신순)
        return kakaoService.getStringObjectHashMap(
                kakaoService.getTextFormatForStampList(
                        stampService.getStampList(
                                kakaoService.getKakaoIdParams(params))));
    }

    @PostMapping("/userRank") // TODO
    public HashMap<String, Object> callUserRankAPI(@Valid @RequestBody Map<String, Object> params) throws JsonProcessingException {
        return kakaoService.getStringObjectHashMap(
                userService.getMyRanking(
                        kakaoService.getKakaoIdParams(params)));
    }


    // 아래는 예제 코드
    @RequestMapping(value = "/kkoChat/v1", method = {RequestMethod.POST, RequestMethod.GET}, headers = {"Accept=application/json"})
    public String callAPI(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(params);
            System.out.println(jsonInString);
            System.out.println();

            String userRequest = mapper.writeValueAsString(params.get("userRequest"));
            System.out.println(userRequest);
            int x = 0;
        } catch (Exception e) {

        }
        return "index";
    }

    //카카오톡 오픈빌더로 리턴할 스킬 API
    @RequestMapping(value = "/kkoChat/v2", method = {RequestMethod.POST, RequestMethod.GET}, headers = {"Accept=application/json"})
    public HashMap<String, Object> callAPI2(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {

        HashMap<String, Object> resultJson = new HashMap<>();

        try {

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(params);
            System.out.println(jsonInString);

            List<HashMap<String, Object>> outputs = new ArrayList<>();
            HashMap<String, Object> template = new HashMap<>();
            HashMap<String, Object> simpleText = new HashMap<>();
            HashMap<String, Object> text = new HashMap<>();

            text.put("text", "코딩32 발화리턴입니다.");
            simpleText.put("simpleText", text);
            outputs.add(simpleText);

            template.put("outputs", outputs);

            resultJson.put("version", "2.0");
            resultJson.put("template", template);


        } catch (Exception e) {

        }

        return resultJson;
    }

    @PostMapping("/tmp")
    public void tmp(@Valid @RequestBody Map<String, Object> params) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
//        String jsonInString = mapper.writeValueAsString(params);
//        System.out.println(jsonInString);
//        System.out.println();
//
//        String userRequest = mapper.writeValueAsString(params.get("userRequest"));
//        System.out.println(userRequest);
//        System.out.println();
//
//        String detailParams = mapper.writeValueAsString(mapper.convertValue(params.get("action"), Map.class).get("detailParams"));
//        System.out.println(detailParams);
//        System.out.println();
//
//        String kakaoid2 = mapper.convertValue(mapper.convertValue(params.get("userRequest"), Map.class).get("user"), Map.class).get("id").toString();
//        System.out.println(kakaoid2);
//        System.out.println();

        String tmp = mapper.writeValueAsString(
                mapper.convertValue(
                                mapper.convertValue(params.get("action"), Map.class)
                                        .get("detailParams"), Map.class)
                        .get("age"));
        System.out.println(tmp);

        String tmp2 = mapper.writeValueAsString(
                mapper.convertValue(
                                mapper.convertValue(
                                                mapper.convertValue(params.get("action"), Map.class)
                                                        .get("detailParams"), Map.class)
                                        .get("age"), Map.class)
                        .get("origin"));
        System.out.println(tmp2);

    }
}
