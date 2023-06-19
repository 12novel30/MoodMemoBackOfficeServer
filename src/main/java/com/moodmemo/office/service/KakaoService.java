package com.moodmemo.office.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moodmemo.office.code.KakaoCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    public static HashMap<String, Object> getStringObjectHashMap(String showText) {
        HashMap<String, Object> resultJson = new HashMap<>();
        HashMap<String, Object> template = new HashMap<>();
        List<HashMap<String, Object>> outputs = new ArrayList<>();
        HashMap<String, Object> simpleText = new HashMap<>();
        HashMap<String, Object> text = new HashMap<>();

        resultJson.put("version", "2.0");

        text.put("text", showText);
        simpleText.put("simpleText", text);
        outputs.add(simpleText);
        template.put("outputs", outputs);
        resultJson.put("template", template);

        return resultJson;
    }

    public StampDto.Dummy getStampParams(Map<String, Object> params) {
        Map<String, Object> action_params = getParamsFromAction(params);
        return StampDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .dateTime(LocalDateTime.now())
                .stamp(action_params.get(PARAMS_EMOTION.getDescription()).toString())
                .memoLet(action_params.get(PARAMS_MEMOLET.getDescription()).toString())
                .build();
    }

    public UserDto.Dummy getInfoParams(Map<String, Object> params) throws JsonProcessingException {

        // TODO - mapper 삭제하고 age 파트 더 깔끔히 작성
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> action_params = getParamsFromAction(params);

        return UserDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .username("test bot")
                .age(convertAge(
                        mapper.writeValueAsString(
                                mapper.convertValue(
                                                mapper.convertValue(
                                                                mapper.convertValue(params.get("action"), Map.class)
                                                                        .get("detailParams"), Map.class)
                                                        .get("age"), Map.class)
                                        .get("origin"))))
                .gender(convertGender(
                        action_params.get(PARAMS_GENDER.getDescription()).toString()))
                .job(action_params.get(PARAMS_JOB.getDescription()).toString())
                .build();
    }

    private static Map getParamsFromAction(Map<String, Object> params) {
        return getMapConvert(
                getMapConvert(params, ACTION.getDescription()), ACTION_PARAMS.getDescription());
    }

    private static int convertAge(String age) {
        return Integer.parseInt(age.replaceAll("[^0-9]", ""));
    }

    private static boolean convertGender(String gender) {
        if (gender.charAt(0) == '남') {
            return true;
        } else {
            return false;
        }
    }

    private static Map getMapConvert(Map<String, Object> detailParams, String code) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(detailParams.get(code), Map.class);
    }

    public String getKakaoIdParams(Map<String, Object> params) {
        return getMapConvert(
                getMapConvert(params, USER_REQUEST.getDescription()),
                USER_REQUEST_USER.getDescription())
                .get(USER_ID.getDescription())
                .toString();
    }

    private static String getParameterToString(Object params) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String userRequest = mapper.writeValueAsString(params);
        return userRequest;
    }
}
