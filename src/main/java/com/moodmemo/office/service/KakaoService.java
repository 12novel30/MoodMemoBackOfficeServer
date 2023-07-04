package com.moodmemo.office.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmemo.office.domain.Stamps;
import com.moodmemo.office.dto.StampDto;
import com.moodmemo.office.dto.UserDto;
import com.moodmemo.office.exception.OfficeException;
import com.moodmemo.office.repository.StampRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.moodmemo.office.code.KakaoCode.*;
import static com.moodmemo.office.code.OfficeCode.SEASON_3_FOLDER;
import static com.moodmemo.office.code.OfficeErrorCode.NO_STAMP;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    private final StampRepository stampRepository;
    private final UserService userService;
    private final StampService stampService;
    private final S3UploaderService s3UploaderService;

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

    public Boolean validateTimeIs3AMtoMidnight(LocalTime time) {
        if (time.isAfter(LocalTime.of(3, 0).minusNanos(1))
                && time.isBefore(LocalTime.of(0, 0).minusNanos(1)))
            return true;
        else
            return false;
    }

    public StampDto.Dummy getTimeChangedStampParams(Map<String, Object> params) throws JsonProcessingException {
        Map<String, Object> action_params = getParamsFromAction(params);

        // get time from params
        String strTime = getParamFromDetailParams(params, PARAMS_TIME.getDescription());
        LocalDateTime localDateTime = getLocalDateTimeBy3AM(
                LocalDate.now(),
                LocalTime.parse(strTime.substring(1, strTime.length() - 1)));

        // get stamp from params
        String stamp = getParamFromDetailParams(params, PARAMS_EMOTION.getDescription());
        stamp = stamp.substring(1, stamp.length() - 1);

        return StampDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .dateTime(localDateTime)
                .stamp(stamp)
                .memoLet(action_params.get(PARAMS_MEMOLET.getDescription()).toString())
                .build();
    }

    public StampDto.Dummy getStampWithImageParams(Map<String, Object> params) throws JsonProcessingException, MalformedURLException {
        Map<String, Object> action_params = getParamsFromAction(params);

        // get stamp from params
        String stamp = getParamFromDetailParams(params, PARAMS_EMOTION.getDescription());
        stamp = stamp.substring(1, stamp.length() - 1);

        // get imageUrl from params
        String kakaoImageUrl = getParamFromDetailParams(params, "imageUrl");
        kakaoImageUrl = kakaoImageUrl.substring(1, kakaoImageUrl.length() - 1);





        // todo - download file from image url (kakao)

//        // create method that convert URL to  MultipartFile
//        URL url = new URL(kakaoImageUrl);
//        File file = new File(url.getFile());

        String imageURL = "https://blog.kakaocdn.net/dn/VIxFi/btqZqqf3QFS/n2otuLtHQo8TQVOwMAmmbk/img.png";

        try {
            URL imgURL = new URL(imageURL);
            String extension = imageURL.substring(imageURL.lastIndexOf(".")+1); // 확장자
            String fileName = "나를_업로드_해봐"; // 이미지 이름

            BufferedImage image = ImageIO.read(imgURL);
            File file = new File("myImage/" + fileName + "." + extension);
            if(!file.exists()) { // 해당 경로의 폴더가 존재하지 않을 경우
                file.mkdirs(); // 해당 경로의 폴더 생성
            }

            ImageIO.write(image, extension, file); // image를 file로 업로드
            System.out.println("이미지 업로드 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }



        // todo - save image file to S3
        // todo - save image url (kakao -> s3 change) to DB
//        return StampDto.Dummy.builder()
//                .kakaoId(getKakaoIdParams(params))
//                .dateTime(LocalDateTime.now())
//                .stamp(stamp)
//                .memoLet(action_params.get(PARAMS_MEMOLET.getDescription()).toString())
//                .imageUrl(s3UploaderService.upload(file, SEASON_3_FOLDER.getDescription()))
//                .build();
        return StampDto.Dummy.builder()
                .kakaoId(getKakaoIdParams(params))
                .dateTime(LocalDateTime.now())
                .stamp(stamp)
                .memoLet(action_params.get(PARAMS_MEMOLET.getDescription()).toString())
                .imageUrl(kakaoImageUrl)
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

    public static String getParamFromDetailParams(
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

    public static Map getMapConvert(Map<String, Object> detailParams, String code) {
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
            DateTimeFormatter
                    .ofPattern("E HH:mm")
                    .withLocale(Locale.forLanguageTag("ko"));

    public String getTextFormatForStampList(List<Stamps> stampList) {
        String stampListText = "";
        for (Stamps stamp : stampList)
            stampListText +=
                    "[" + stamp.getDateTime().format(stampListToBotFormat) + "] "
                            + stamp.getStamp() + " : "
                            + stamp.getMemoLet()
                            + "\n";
        return "🥬 오늘 남긴 스탬프들이다무! 🥬" +
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
        if (getByKakaoIdAndLocalDateTime(kakaoId, time, today).size() >= 1)
            return "SUCCESS";
        else
            return "FAIL";
    }

    @Transactional(readOnly = true)
    private List<Stamps> getByKakaoIdAndLocalDateTime(
            String kakaoId, String time, LocalDate today) {

        LocalDateTime localDateTime = getLocalDateTimeBy3AM(
                today,
                LocalTime.parse(time));

        return stampRepository.findByKakaoIdAndDateTimeBetween(
                kakaoId, localDateTime.minusNanos(1), localDateTime.plusMinutes(1).minusNanos(1));
    }

    private LocalDateTime getLocalDateTimeBy3AM(LocalDate today, LocalTime localTime) {
        /* if 찾는 시간: 03:00~23:59 ->
         *   if 바꾸려는 시간: 03:00~23:59 -> -
         *   if 바꾸려는 시간: 00:00~02:59 -> 어제 날짜에서 찾기
         * if 찾는 시간: 00:00~02:59 ->
         *   if 바꾸려는 시간: 03:00~23:59 -> 어제 날짜에서 찾기
         *   if 바꾸려는 시간: 00:00~02:59 -> -
         *
         * (찾는 시간이 03:00~23:59 사이인가?) == (바꾸려는 시간이 03:00~23:59 사이인가?)
         * true=(n,n)(y,y) -> 오늘 날짜 그대로!
         * false=(n,y)(y,n) -> 어제 날짜로!
         * */
        if (validateTimeIs3AMtoMidnight(LocalTime.now())
                == validateTimeIs3AMtoMidnight(localTime))
            return LocalDateTime.of(today, localTime);
        else
            return LocalDateTime.of(today.minusDays(1), localTime);
    }


    public Stamps getStampByTime(
            String kakaoId, String time, LocalDate today) {
        List<Stamps> stampsList = getByKakaoIdAndLocalDateTime(kakaoId, time, today);
        if (stampsList.size() < 1) throw new OfficeException(NO_STAMP);
        else
            return stampsList.get(0);
    }

    public HttpStatus updateStampTime(Map<String, Object> params,
                                      String sys_time,
                                      LocalDate nowDate,
                                      String strEditTime) {

        Stamps targetStamp = getStampByTime(getKakaoIdParams(params), sys_time, nowDate);
        targetStamp.setDateTime(getLocalDateTimeBy3AM(
                nowDate,
                LocalTime.parse(strEditTime)));

        return ResponseEntity.ok(stampRepository.save(targetStamp)).getStatusCode();
    }

    public HttpStatus updateStampEmo(Map<String, Object> params,
                                     String sys_time,
                                     LocalDate nowDate) throws JsonProcessingException {

        Stamps targetStamp = getStampByTime(getKakaoIdParams(params), sys_time, nowDate);

        String edit_emo = getParamFromDetailParams(params, "edit_emo");
        edit_emo = edit_emo.substring(1, edit_emo.length() - 1);
        targetStamp.setStamp(edit_emo);

        return ResponseEntity.ok(stampRepository.save(targetStamp)).getStatusCode();
    }

    public HttpStatus updateStampMemolet(Map<String, Object> params,
                                         String sys_time,
                                         LocalDate nowDate) throws JsonProcessingException {

        Stamps targetStamp = getStampByTime(getKakaoIdParams(params), sys_time, nowDate);
        Map<String, Object> action_params = getParamsFromAction(params);
        targetStamp.setMemoLet(action_params.get("edit_memolet").toString());

        return ResponseEntity.ok(stampRepository.save(targetStamp)).getStatusCode();
    }

    public void deleteStamp(Map<String, Object> params,
                            String sys_time,
                            LocalDate nowDate) throws JsonProcessingException {
        String kakaoId = getKakaoIdParams(params);

        Stamps targetStamp = getStampByTime(kakaoId, sys_time, nowDate);
        stampRepository.delete(targetStamp);

        userService.updateWeekCount(kakaoId, stampService.validateWeek(), -1);
    }

    public String kakaoImageTest(String imageUrl) {

        // todo - download file from image url (kakao)

        try {
            URL imgURL = new URL(imageUrl);
            String fileName = "나를_업로드_해봐"; // 이미지 이름
            BufferedImage image = ImageIO.read(imgURL);
            File file = new File("myImage/" + fileName + ".jpg");
            if(!file.exists()) { // 해당 경로의 폴더가 존재하지 않을 경우
                file.mkdirs(); // 해당 경로의 폴더 생성
            }

            ImageIO.write(image, "jpg", file); // image를 file로 업로드
            System.out.println("이미지 업로드 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrl;
    }
}
