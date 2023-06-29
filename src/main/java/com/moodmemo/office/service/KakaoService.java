package com.moodmemo.office.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.repository.StampRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moodmemo.office.code.KakaoCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    private final StampRepository stampRepository;

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
    public static HashMap<String, Object> getValidatetHashMap(String showText) {
        HashMap<String, Object> resultJson = new HashMap<>();
        resultJson.put("status", showText);
        return resultJson;
    }

    public StampDto.Dummy getStampParams(Map<String, Object> params) throws JsonProcessingException {
        Map<String, Object> action_params = getParamsFromAction(params);
        String stamp = getParamFromDetailParams(params, PARAMS_EMOTION.getDescription());
        stamp = stamp.substring(1, stamp.length() - 1);
        return StampDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .dateTime(LocalDateTime.now())
                .stamp(stamp)
                .memoLet(action_params.get(PARAMS_MEMOLET.getDescription()).toString())
                .build();
    }

    public StampDto.Dummy getTimeChangedStampParams(Map<String, Object> params) throws JsonProcessingException {
        Map<String, Object> action_params = getParamsFromAction(params);

        // get timeStamp from params
        String time = getParamFromDetailParams(params, PARAMS_TIME.getDescription());
        String[] times = time.substring(1, time.length() - 1).split(":");

        String stamp = getParamFromDetailParams(params, PARAMS_EMOTION.getDescription());
        stamp = stamp.substring(1, stamp.length() - 1);

        return StampDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .dateTime(LocalDateTime.now()
                        .withHour(Integer.parseInt(times[0]))
                        .withMinute(Integer.parseInt(times[1])))
                .stamp(stamp)
                .memoLet(action_params.get(PARAMS_MEMOLET.getDescription()).toString())
                .build();
    }

    public UserDto.Dummy getInfoParams(Map<String, Object> params) throws JsonProcessingException {

        Map<String, Object> action_params = getParamsFromAction(params);

        return UserDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .username(action_params.get("name").toString())
                .age(convertAge(
                        getParamFromDetailParams(params, "age")))
                .gender(convertGender(
                        action_params.get(PARAMS_GENDER.getDescription()).toString()))
                .job(action_params.get(PARAMS_JOB.getDescription()).toString())
                .build();
    }

    private static String getParamFromDetailParams(
            Map<String, Object> params, String entity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(
                mapper.convertValue(
                                mapper.convertValue(
                                                mapper.convertValue(params.get("action"), Map.class)
                                                        .get("detailParams"), Map.class)
                                        .get(entity), Map.class)
                        .get("origin"));
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

    public static String getParameterToString(Object params) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String userRequest = mapper.writeValueAsString(params);
        return userRequest;
    }

    private final DateTimeFormatter stampListToBotFormat =
            DateTimeFormatter.ofPattern("HH:mm");

    public String getTextFormatForStampList(List<Stamps> stampList) {
        String stampListText = "";
        for (Stamps stamp : stampList)
            stampListText +=
                    "[" + stamp.getDateTime().format(stampListToBotFormat) + "] "
                            + stamp.getStamp() + " : "
                            + stamp.getMemoLet()
                            + "\n";
        return "오늘 남긴 스탬프리스트입니다!" +
                "\n" + "=========="
                + "\n" + stampListText;
    }

    public String validateMemoletLength(String memolet) {
        if (memolet.length() < 10) {
            return "FAIL";
        } else {
            return "SUCCESS";
        }
    }

    public String validateStampByTime(String kakaoId, String time, LocalDate today) {
        if (stampRepository.findByKakaoIdAndLocalTimeAndLocalDate(
                kakaoId, LocalTime.parse(time), today)
                .isPresent())
            return "SUCCESS";
        else
            return "FAIL";
    }
}
