document.addEventListener("DOMContentLoaded", function () {
    var starImage = document.querySelector(".stars");
    var comment = document.querySelector("#ratingComment");
    var isDragging = false; // 드래그 여부를 확인하는 변수
    var maxRating = 10; // 별점 최대값 (0.5 단위로 5까지 가능)
    var starImageWidth = starImage.clientWidth; // 별점 이미지의 전체 너비

    // 초기 별점 설정
    var initialRating = parseFloat(document.getElementById("situationJson").getAttribute("data-mainScore")) || 0;
    var initialRatingIndex = initialRating > 0 ? initialRating * 2 : 0; // 기존 평가가 있다면 별점 인덱스 설정, 아니면 0으로 설정
    var initialComment = document.getElementById("situationJson").getAttribute("data-comment") || "";

    var evaluationData = {
        starRating: initialRating, // 초기값 설정
        selectedSituations: [], // 선택된 상황
        restaurantId: 0, // 식당 ID
        evaluationComment: initialComment, // 평가 코멘트
        newImage: null // 새로운 이미지
    };

    // 기존 평가 별점이 있는 경우 문구 업데이트
    setMainRating(initialRatingIndex);

    // 별점 이미지 클릭 이벤트
    starImage.addEventListener("click", function (event) {
        var selectedIndex = Math.max(calculateStarRating(event), 1); // 최소 별점 0.5(인덱스 1)로 설정
        setMainRating(selectedIndex); // 별점 설정
    });

    // 드래그 시작 이벤트
    starImage.addEventListener('mousedown', function (event) {
        isDragging = true; // 드래그 시작
        var selectedIndex = Math.max(calculateStarRating(event), 1); // 최소 별점 0.5로 설정
        setMainRating(selectedIndex); // 별점 설정
    });

    // 드래그 중 이벤트
    starImage.addEventListener('mousemove', function (event) {
        if (isDragging) { // 드래그 중일 때만 처리
            var selectedIndex = Math.max(calculateStarRating(event), 1); // 최소 별점 0.5로 설정
            setMainRating(selectedIndex); // 별점 설정
        }
    });

    // 드래그 종료 이벤트
    document.addEventListener('mouseup', function () {
        isDragging = false; // 드래그 종료
    });

    // 터치 시작 이벤트 (모바일 환경)
    starImage.addEventListener('touchstart', function (event) {
        isDragging = true; // 터치 시작
        var selectedIndex = Math.max(calculateStarRating(event.touches[0]), 1); // 최소 별점 0.5로 설정
        setMainRating(selectedIndex); // 별점 설정
    });

    // 터치 이동 이벤트 (모바일 환경)
    starImage.addEventListener('touchmove', function (event) {
        event.preventDefault();
        if (isDragging) {
            var selectedIndex = Math.max(calculateStarRating(event.touches[0]), 1); // 최소 별점 0.5로 설정
            setMainRating(selectedIndex); // 별점 설정
        }
    });

    // 터치 종료 이벤트 (모바일 환경)
    starImage.addEventListener('touchend', function () {
        isDragging = false; // 터치 종료
    });

    // 별점 계산 함수
    function calculateStarRating(event) {
        var rect = starImage.getBoundingClientRect(); // 별점 이미지의 경계 정보
        var offsetX = event.clientX - rect.left; // 클릭 또는 터치 위치의 X 좌표 계산
        var selectedIndex = Math.round((offsetX / starImageWidth) * maxRating); // 반점 단위로 별점 계산
        return Math.max(0, Math.min(selectedIndex, maxRating)); // 최소값과 최대값 사이의 값으로 제한
    }

    // 별점 설정 함수
    function setMainRating(selectedIndex) {
        starImage.src = `https://kustaurant.s3.ap-northeast-2.amazonaws.com/evaluation/star-${selectedIndex}.png`; // 별점 이미지 업데이트
        evaluationData.starRating = selectedIndex / 2; // 평가 데이터에 별점 저장 (0.5 단위로 변환)

        // 숨겨진 필드에 별점 값 설정
        document.getElementById('starRatingInput').value = evaluationData.starRating;

        // 별점에 따른 평가 코멘트 설정
        switch (selectedIndex) {
            case 0:
                comment.textContent = "아직 선택하지 않으셨습니다.";
                break;
            case 1:
                comment.textContent = "별로였어요, 두번 다시 오지는 않을 것 같아요";
                break;
            case 2:
                comment.textContent = "많이 부족한 가게에요";
                break;
            case 3:
                comment.textContent = "뭔가 부족한 가게에요";
                break;
            case 4:
                comment.textContent = "조금만 더 분발해주면 좋을것 같아요";
                break;
            case 5:
                comment.textContent = "평범 무난한 음식점 이에요";
                break;
            case 6:
                comment.textContent = "평범한데 살짝 더 나은 정도에요";
                break;
            case 7:
                comment.textContent = "꽤 괜찮았어요, 다시 올 의향 있어요";
                break;
            case 8:
                comment.textContent = "맛있어요! 꼭 다시 오고 싶어요";
                break;
            case 9:
                comment.textContent = "별5개에 살짝 못미치지만 훌륭했어요!";
                break;
            case 10:
                comment.textContent = "내 마음속 최고의 가게!";
                break;
        }
    }

    // 데이터가 올바르게 입력되었는지 체크하는 함수
    function checkData() {
        if (evaluationData.starRating === 0) {
            return false;
        }
        return true;
    }
});
