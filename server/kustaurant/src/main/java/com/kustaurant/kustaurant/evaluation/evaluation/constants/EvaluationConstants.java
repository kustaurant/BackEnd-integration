package com.kustaurant.kustaurant.evaluation.evaluation.constants;

import java.util.List;

public class EvaluationConstants {

    public static final List<StarComment> STAR_COMMENTS = List.of(
            new StarComment(0.5, "다시 갈 것 같진 않아요."),
            new StarComment(1.0, "많이 아쉬워요"),
            new StarComment(1.5, "다른데 갈껄"),
            new StarComment(2.0, "조금 아쉬워요."),
            new StarComment(2.5, "무난했어요."),
            new StarComment(3.0, "괜찮았어요."),
            new StarComment(3.5, "만족스러웠어요."),
            new StarComment(4.0, "무조건 한번 더 올 것 같아요."),
            new StarComment(4.5, "행복했습니다."),
            new StarComment(5.0, "인생 최고의 식당입니다.")
    );
}
