package com.moodmemo.office.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.service.KakaoService;
import com.moodmemo.office.service.S3UploaderService;
import com.moodmemo.office.service.StampService;
import com.moodmemo.office.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.time.LocalDate;
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
    private final S3UploaderService s3UploaderService;

    @PostMapping("/userInfo")
    public HashMap<String, Object> callUserInfoAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        log.info(kakaoService.getParameterToString(params));

        String returnText;
        UserDto.Dummy userDto = kakaoService.getInfoParams(params);

        if (userService.validateUserAlreadyExist(userDto.getKakaoId())) { // return true
            log.info(userDto.getKakaoId());
            returnText = "이미 등록한 정보가 있습니다!" +
                    "\n수정을 원하신다면, [문의] 키워드를 통해 운영진에게 문의해주세요!";
        } else { // return false
            userService.createUser(userDto);
            returnText = "정보가 입력되었습니다!";
        }

        return kakaoService.getStringObjectHashMap(returnText);
    }

    @PostMapping("/stamp")
    public HashMap<String, Object> callStampAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // save stamp
        String kakaoId = stampService.createStamp(
                kakaoService.getStampParams(params)).getKakaoId();

        // update week n 의 스탬프 개수
        userService.updateWeekCount(kakaoId, stampService.validateWeek(), 1);

        // TODO - 여기에서 에러처리하고 그 메세지를 보내는 방향으로 수정할 것
        return kakaoService.getStringObjectHashMap("memolet 발화리턴");
    }

    @PostMapping("/timeChange-stamp")
    public HashMap<String, Object> callTimeChangedStampAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // save stamp
        String kakaoId = stampService.createStamp(
                kakaoService.getTimeChangedStampParams(params)).getKakaoId();

        // update week n 의 스탬프 개수
        userService.updateWeekCount(kakaoId, stampService.validateWeek(), 1);

        // TODO - 여기에서 에러처리하고 그 메세지를 보내는 방향으로 수정할 것
        return kakaoService.getStringObjectHashMap("시간 변경 버전 memolet 발화리턴");
    }

    @PostMapping("/validate/memolet")
    public HashMap<String, Object> validateMemoletAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        log.info(kakaoService.getParameterToString(params));

        HashMap<String, Object> tmp =
                kakaoService.getValidatetHashMap(
                        kakaoService.validateMemoletLength(
                                params.get("utterance").toString()));
        return tmp;
    }

    @PostMapping("/stampList")
    public HashMap<String, Object> callStampListAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        return kakaoService.getStringObjectHashMap(
                kakaoService.getTextFormatForStampList(
                        stampService.getStampListByDate(
                                kakaoService.getKakaoIdParams(params))));
    }

    @PostMapping("/userRank")
    public HashMap<String, Object> callUserRankAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        log.info(kakaoService.getParameterToString(params));

        return kakaoService.getStringObjectHashMap(
                userService.tmpRnag(
                        kakaoService.getKakaoIdParams(params)));
    }

    @PostMapping("/prize/postWeek")
    public HashMap<String, Object> callPrizePostWeekAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        log.info(kakaoService.getParameterToString(params));

        return kakaoService.getStringObjectHashMap(
                userService.getPrizePostWeek(
                        kakaoService.getKakaoIdParams(params)));
    }

    @PostMapping("/dailyReport/yesterday")
    public HashMap<String, Object> callYesterdayDRAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        log.info(kakaoService.getParameterToString(params));

        // 자정이 넘은 뒤, 어제의 DR 을 가져오는 것으로 생각함.
        return kakaoService.getStringObjectHashMap(
                userService.getUserDRYesterday(
                        kakaoService.getKakaoIdParams(params),
                        LocalDate.now().minusDays(1)));
    }

    @PostMapping("/validate/stamp")
    public HashMap<String, Object> validateStampByTimeAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {
        // 변경/수정하려고 하는 스탬프가 존재하지 않을 때 검증 API

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // 오늘의 스탬프를 수정하는 것으로 생각함.
        return kakaoService.getValidatetHashMap(
                kakaoService.validateStampByTime(
                        kakaoService.getMapConvert(params, "user").get("id").toString(),
                        params.get("utterance").toString(),
                        LocalDate.now()));
    }

    // TODO - 디자인 패턴 활용할 것
    @PostMapping("/edit/time")
    public HashMap<String, Object> callEditTimeAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // get time parameter
        String sys_time = kakaoService.getParamFromDetailParams(params, "sys_time");
        sys_time = sys_time.substring(1, sys_time.length() - 1);
        String edit_time = kakaoService.getParamFromDetailParams(params, "edit_time");
        edit_time = edit_time.substring(1, edit_time.length() - 1);

        // updateStampTime
        log.info(kakaoService.updateStampTime(
                params, sys_time, LocalDate.now(), edit_time).toString());

        return kakaoService.getStringObjectHashMap("시간 변경 발화리턴");
    }

    @PostMapping("/edit/emo")
    public HashMap<String, Object> callEditEmoAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // get time parameter
        String sys_time = kakaoService.getParamFromDetailParams(params, "sys_time");
        sys_time = sys_time.substring(1, sys_time.length() - 1);

        // updateStampTime
        log.info(kakaoService.updateStampEmo(
                params, sys_time, LocalDate.now()).toString());

        return kakaoService.getStringObjectHashMap("감정 변경 발화리턴");
    }

    @PostMapping("/edit/memolet")
    public HashMap<String, Object> callEditMemoletAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // get time parameter
        String sys_time = kakaoService.getParamFromDetailParams(params, "sys_time");
        sys_time = sys_time.substring(1, sys_time.length() - 1);

        // updateStampTime
        log.info(kakaoService.updateStampMemolet(
                params, sys_time, LocalDate.now()).toString());

        return kakaoService.getStringObjectHashMap("memolet 변경 발화리턴");
    }

    @PostMapping("/delete")
    public HashMap<String, Object> callDeleteStampAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException {

        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // get time parameter
        String sys_time = kakaoService.getParamFromDetailParams(params, "sys_time");
        sys_time = sys_time.substring(1, sys_time.length() - 1);

        // updateStampTime & update week n 의 스탬프 개수 (minus)
        kakaoService.deleteStamp(params, sys_time, LocalDate.now());

        return kakaoService.getStringObjectHashMap("스탬프 삭제 발화리턴");
    }


    @PostMapping("/image")
    public HashMap<String, Object> callStampWithImageAPI(
            @Valid @RequestBody Map<String, Object> params)
            throws JsonProcessingException, MalformedURLException {
        // parameter logging
        log.info(kakaoService.getParameterToString(params));

        // save stamp - 일단 시간 변경은 안했다고 가정
        // todo - 시간 변경이 되었다면, if null 처리하기
        String kakaoId = stampService.createStamp(
                kakaoService.getStampWithImageParams(params)).getKakaoId();



//        Map<String, Object> action_params = getParamsFromAction(params);
//        targetStamp.setMemoLet(action_params.get("edit_memolet").toString());

//        String objectURL = s3UploaderService.upload(file, "office");
//        System.out.println(objectURL);
//        return ResponseEntity.ok(userservice.updateUserImage(userId, objectURL));






        // update week n 의 스탬프 개수
        userService.updateWeekCount(kakaoId, stampService.validateWeek(), 1);

        return kakaoService.getStringObjectHashMap("사진 입력 버전 memolet 발화리턴");
    }


    /*----------아래는 예제 코드----------*/
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

        String tmp = mapper.writeValueAsString(mapper.convertValue(mapper.convertValue(params.get("action"), Map.class).get("detailParams"), Map.class).get("age"));
        System.out.println(tmp);

        String tmp2 = mapper.writeValueAsString(mapper.convertValue(mapper.convertValue(mapper.convertValue(params.get("action"), Map.class).get("detailParams"), Map.class).get("age"), Map.class).get("origin"));
        System.out.println(tmp2);

    }
}
