package com.moodmemo.office.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KakaoCode {
    USER_REQUEST("userRequest"),
    ACTION("action"),
    USER_REQUEST_USER("user"),
    ACTION_PARAMS("params"),
    PARAMS_AGE("sys_number_age"),
    PARAMS_GENDER("gender"),
    PARAMS_JOB("job"),
    PARAMS_MEMOLET("memolet"),
    PARAMS_EMOTION("emotion"),
    USER_ID("id"),
    ;
    private final String description;
}

/*{"intent":
            {"id":"8xkgos3uoy0epwv8g4na9qfg","name":"블록 이름"},
            "userRequest":{"timezone":"Asia/Seoul","params":{"ignoreMe":"true"},"block":{"id":"8xkgos3uoy0epwv8g4na9qfg","name":"블록 이름"},"utterance":"발화 내용","lang":null,"user":{"id":"602828","type":"accountId","properties":{}}},"bot":{"id":"6482a5f80860704c5c685e37","name":"봇 이름"},"action":{"name":"3ia9ryjdj8","clientExtra":null,"params":{},"id":"w1qkt1fsg8umyvjqf7i79zl1","detailParams":{}}}
         */

/*
            {
                "response": {
                    "template": {
                        "outputs": [
                            {
                                "simpleText": {"text": "코딩32 발화리턴입니다."}
                            }
                         ]
                     },
                    "version": "2.0"
                }
            }
*/

/*{"intent":{"id":"8xkgos3uoy0epwv8g4na9qfg","name":"블록 이름"},
"userRequest":{"timezone":"Asia/Seoul","params":{"ignoreMe":"true"},
"block":{"id":"8xkgos3uoy0epwv8g4na9qfg","name":"블록 이름"},
"utterance":"발화 내용","lang":null,
"user":{"id":"602828","type":"accountId","properties":{}}},
"bot":{"id":"6482a5f80860704c5c685e37","name":"봇 이름"},
"action":{"name":"3ia9ryjdj8","clientExtra":null,
"params":{"age":23,"gender":false,"job":"대학생"},
"id":"w1qkt1fsg8umyvjqf7i79zl1",
"detailParams":{"age":{"origin":23,"value":23,"groupName":""},"gender":{"origin":false,"value":false,"groupName":""},"job":{"origin":"대학생","value":"대학생","groupName":""}}}}*/

/*
{"bot":{"id":"6482a5f80860704c5c685e37!","name":"Moo"},
"intent":{"id":"64904a35b467836b9d8691b3","name":"사용자 정보 입력","extra":{"reason":{"code":1,"message":"OK"}}},
"action":{"id":"64902930c28bd5131c9cf634",
        "name":"사용자 기본 정보 DB 저장",
        "params":{"sys_number_age":"sys.number.age","gender":"여자","job":"직장인"},
        "detailParams":{"sys_number_age":{"groupName":"","origin":"23세","value":"sys.number.age"},
                        "gender":{"groupName":"","origin":"여자","value":"여자"},
                        "job":{"groupName":"","origin":"직장인","value":"직장인"}},
                        "clientExtra":{}},
        "userRequest":{"block":{"id":"64904a35b467836b9d8691b3","name":"사용자 정보 입력"},
        "user":{"id":"e45270b14c1ccedb6025319a78813dca5e66153c725816e8a9cd091c07666929aa",
                "type":"botUserKey",
                "properties":{"botUserKey":"e45270b14c1ccedb6025319a78813dca5e66153c725816e8a9cd091c07666929aa",
                                "bot_user_key":"e45270b14c1ccedb6025319a78813dca5e66153c725816e8a9cd091c07666929aa"}},
        "utterance":"23세/여자/직장인",
        "params":{"ignoreMe":"true","surface":"BuilderBotTest"},
        "lang":"ko",
        "timezone":"Asia/Seoul"},
"contexts":[]}*/ // 나이 성별 직업

/*
 * {"bot":{"id":"6482a5f80860704c5c685e37!","name":"Moo"},
 * "intent":{"id":"648c4a55c28bd5131c9cc28b","name":"스탬프 입력 완료","extra":{"reason":{"code":0,"message":"OK"}}},
 * action":{"id":"64902930c28bd5131c9cf634",
 *           "name":"사용자 기본 정보 DB 저장",
 *           "params":{"memolet":"되나?","emotion":"기쁨"},
 *           "detailParams":{"memolet":{"groupName":"","origin":"되나?","value":"되나?"},"emotion":{"groupName":"","origin":"기쁨","value":"기쁨"}},"clientExtra":{}},
 * "userRequest":{"block":{"id":"648c4a55c28bd5131c9cc28b","name":"스탬프 입력 완료"},
 * "user":{"id":"e45270b14c1ccedb6025319a78813dca5e66153c725816e8a9cd091c07666929aa","type":"botUserKey","properties":{"botUserKey":"e45270b14c1ccedb6025319a78813dca5e66153c725816e8a9cd091c07666929aa","bot_user_key":"e45270b14c1ccedb6025319a78813dca5e66153c725816e8a9cd091c07666929aa"}},
 * "utterance":"기쁨",
 * "params":{"ignoreMe":"true","surface":"BuilderBotTest"},"lang":"ko","timezone":"Asia/Seoul"},"contexts":[]}
 */ // memolet emotion