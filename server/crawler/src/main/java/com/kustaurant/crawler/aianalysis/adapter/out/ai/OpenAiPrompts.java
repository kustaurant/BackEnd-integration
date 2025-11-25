package com.kustaurant.crawler.aianalysis.adapter.out.ai;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.kustaurant.crawler.aianalysis.domain.model.Sentiment;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;

public abstract class OpenAiPrompts {

    public static Prompt getMainScorePrompt(List<Integer> scores, String review) {
        return new Prompt(SCORE_USER_PROMPT + "리뷰: " + review,
                OpenAiChatOptions.builder()
                        .responseFormat(
                                new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA,
                                getScoreJsonSchema(scores))
                        )
                        .build());
    }

    public static Prompt getSituationPrompt(List<String> candidates, String review) {
        return new Prompt(SITUATION_USER_PROMPT + "리뷰: " + review,
                OpenAiChatOptions.builder()
                        .responseFormat(
                                new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA,
                                getSituationJsonSchema(candidates))
                        )
                        .build());
    }

    private static final String SCORE_USER_PROMPT = """
            다음은 음식점 리뷰의 만족도 점수 규칙이다.
            이 규칙을 기반으로 이 리뷰가 나타내는 점수와 SENTIMENT를 구하라.
            
            [점수]
            - 5점: 인생 최고의 맛집, 강력 추천, 극찬, 별다섯, 최고의 경험
            - 4점: 다시 오고 싶다, 재방문 의사, 이미 여러 번 방문, 대체로 매우 만족
            - 3점: 적당히/무난히 맛있다, 괜찮다, 보통 이상의 만족
            - 2점: 아쉬움/불만족 요소가 뚜렷함(맛/서비스/가격/위생/대기 등), 재방문은 망설임
            - 1점: 다시 오지 않겠다, 강한 불만/실망/최악의 경험
            판단 규칙:
            - 리뷰 내 긍/부정 신호(“최고/최악/다시는/또 올게요/무난함” 등)와 강도(강조, 빈도, 전반적 톤)를 함께 고려하라.
            - 일부 항목 불만 + 전반적 만족 → 3점 또는 4점 (불만 강하면 3, 약하면 4)
            - 칭찬/불만이 혼재하면 최종 톤에 따라 가중치 부여.
            - 별점/이모지/횟수 표현(“N번째 방문”, “⭐️⭐️⭐️⭐️⭐️”)도 신호로 인정.
            - 모호할 때는 3점을 기본 기준으로, 뚜렷한 긍/부정에만 1단계 상하 조정.
            
            [SENTIMENT]
            - positive: 극찬/재방문 의사/강한 추천
            - neutral: 무난/애매/호불호 혼재(강한 찬반 없음)
            - negative: 실망/불만/다시는 안 옴
            판단 규칙:
            - score는 1~5에서 선택, sentiment는 위 세 값 중 하나
            
            
            """;

    private static final String SITUATION_USER_PROMPT = """
            다음은 음식점 리뷰에서 추출할 수 있는 situation 선택 규칙이다.
            
            - 주어진 candidates(가능한 situation 목록) 중에서 리뷰와 관련된 것만 고른다.
            - 최대 3개까지 선택할 수 있다.
            - 리뷰와 관련 없는 경우는 아무것도 고르지 않아도 된다.
            - 각 선택된 situation에 대해 간단한 이유를 함께 적어라.
            - 반드시 아래 JSON 스키마에 맞는 한 객체로만 응답한다.
            - 스키마 외 텍스트/설명/코드블록은 금지한다.
            
            
            """;

    private static String getScoreJsonSchema(List<Integer> scores) {
        String enumJson = scores.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String sentiments = Arrays.stream(Sentiment.values())
                .map(Enum::name)
                .map(OpenAiPrompts::quoteJson)
                .collect(Collectors.joining(", "));
        return """
                {
                    "type": "object",
                    "additionalProperties": false,
                    "properties": {
                      "score": {
                        "type": "integer",
                        "enum": [%s],
                        "description": "평가 점수"
                      },
                      "sentiment": {
                        "type": "string",
                        "enum": [%s],
                        "description": "리뷰 전반의 극성"
                      },
                      "reason": { "type": "string", "description": "점수 근거 요약" }
                    },
                    "required": ["score", "sentiment", "reason"]
                }
                """.formatted(enumJson, sentiments);
    }

    private static String getSituationJsonSchema(List<String> candidates) {
        String enumJson = candidates.stream()
                .map(OpenAiPrompts::quoteJson)
                .collect(Collectors.joining(", "));
        return """
                {
                    "type": "object",
                    "properties": {
                      "situations": {
                        "type": "array",
                        "description": "선택된 상황 목록",
                        "items": {
                          "type": "string",
                          "enum": [%s]
                        }
                      },
                      "reason": {
                        "type": "string",
                        "description": "상황별 선택 근거 요약"
                      }
                    },
                    "required": ["situations", "reason"],
                    "additionalProperties": false
                }
                """.formatted(enumJson);
    }

    static String quoteJson(String s) {
        var enc = JsonStringEncoder.getInstance();
        return "\"" + new String(enc.quoteAsString(s)) + "\"";
    }
}
