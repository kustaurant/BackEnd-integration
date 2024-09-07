document.addEventListener("DOMContentLoaded", function () {
    var starImage = document.querySelector(".stars");
    var comment = document.querySelector("#ratingComment");
    var isDragging = false; // 드래그 여부를 확인하는 변수
    var maxRating = 10; // 별점 최대값 (0.5 단위로 10까지 가능)
    var starImageWidth = starImage.clientWidth; // 별점 이미지의 전체 너비
    var evaluationData = {
        starRating: 0, // 선택된 별점 초기값
        selectedSituations: [], // 선택된 상황
        restaurantId: 0, // 식당 ID
        evaluationComment: "", // 평가 코멘트
        newImage: null // 새로운 이미지
    };




    // 별점 이미지 클릭 이벤트
    starImage.addEventListener("click", function (event) {
        var selectedIndex = calculateStarRating(event); // 클릭 위치로부터 별점 계산
        setMainRating(selectedIndex); // 별점 설정
    });

    // 드래그 시작 이벤트
    starImage.addEventListener('mousedown', function (event) {
        isDragging = true; // 드래그 시작
        var selectedIndex = calculateStarRating(event); // 드래그 시작 위치로부터 별점 계산
        setMainRating(selectedIndex); // 별점 설정
    });

    // 드래그 중 이벤트
    starImage.addEventListener('mousemove', function (event) {
        if (isDragging) { // 드래그 중일 때만 처리
            var selectedIndex = calculateStarRating(event); // 드래그 위치로부터 별점 계산
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
        var selectedIndex = calculateStarRating(event.touches[0]); // 터치 시작 위치로부터 별점 계산
        setMainRating(selectedIndex); // 별점 설정
    });

    // 터치 이동 이벤트 (모바일 환경)
    starImage.addEventListener('touchmove', function (event) {
        event.preventDefault();
        if (isDragging) {
            var selectedIndex = calculateStarRating(event.touches[0]); // 터치 이동 위치로부터 별점 계산
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
        starImage.src = `/img/evaluation/star-${selectedIndex}.png`; // 별점 이미지 업데이트
        evaluationData.starRating = (selectedIndex + 1) / 2; // 평가 데이터에 별점 저장 (0.5 단위로 변환)

        // 별점에 따른 평가 코멘트 설정
        switch (selectedIndex) {
            case 0:
                comment.textContent = "다시 올 일은 없을 것 같습니다";
                break;
            case 1:
                comment.textContent = "이 곳에 다시 올 일은 거의 없을 것 같아요";
                break;
            case 2:
                comment.textContent = "조금 실망스러운 가게에요";
                break;
            case 3:
                comment.textContent = "별로였어요, 다시 오지는 않을 것 같아요";
                break;
            case 4:
                comment.textContent = "보통이에요, 크게 기대하지 마세요";
                break;
            case 5:
                comment.textContent = "그럭저럭 괜찮았어요";
                break;
            case 6:
                comment.textContent = "괜찮아요, 다시 올 수도 있을 것 같아요";
                break;
            case 7:
                comment.textContent = "꽤 마음에 들었어요, 다시 와도 좋을 것 같아요";
                break;
            case 8:
                comment.textContent = "좋아요! 다시 방문할 것 같아요";
                break;
            case 9:
                comment.textContent = "매우 만족스러웠어요, 꼭 다시 올 거에요!";
                break;
            case 10:
                comment.textContent = "내 마음속 최고의 가게!";
                break;
        }
    }

    // ---------- (second) 상황 버튼 선택 효과 로직 ---------- //
    var situationButtons = document.querySelectorAll(".keywordBtn");

    situationButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            toggleSituationButton(button);
        });
    });

    // 상황 버튼의 선택 상태 토글 함수
    function toggleSituationButton(button) {
        button.classList.toggle("unselected");
        button.classList.toggle("selected");

        var situationId = button.id.replace('situation', '');
        if (evaluationData.selectedSituations.includes(situationId)) {
            evaluationData.selectedSituations = evaluationData.selectedSituations.filter(function (id) {
                return id !== situationId;
            });
        } else {
            evaluationData.selectedSituations.push(situationId);
        }
    }

    // ---------- (third) 이미지 및 코멘트 첨부 로직 ---------- //
    var imageInput = document.getElementById('newImage');
    var commentInput = document.getElementById('evaluationComment');

    // 커서가 들어오면 placeholder를 지우고 나가면 다시 설정하는 이벤트 리스너
    commentInput.addEventListener('focus', function () {
        this.setAttribute('data-placeholder', this.placeholder);
        this.placeholder = '';
    });

    commentInput.addEventListener('blur', function () {
        this.placeholder = this.getAttribute('data-placeholder');
    });


    if (imageInput) {
        imageInput.addEventListener('change', function () {
            evaluationData.newImage = imageInput.files[0]; // 선택된 이미지 파일 저장
        });
    }

    if (commentInput) {
        commentInput.addEventListener('input', function () {
            evaluationData.evaluationComment = commentInput.value; // 입력된 코멘트 저장
        });
    }

    // ---------- 제출 버튼 눌림 효과 로직 ---------- //
    var submitBtn = document.getElementById('submitBtn');

    submitBtn.addEventListener('click', function () {
        if (!checkData()) {
            alert('모든 항목을 평가해주세요.');
            return;
        }
        var restaurantId = extractRestaurantIdFromUrl();
        evaluationData.restaurantId = restaurantId;

        var formData = new FormData();
        formData.append("starRating", evaluationData.starRating);
        formData.append("selectedSituations", JSON.stringify(evaluationData.selectedSituations));
        formData.append("evaluationComment", evaluationData.evaluationComment);

        if (evaluationData.newImage) {
            formData.append("newImage", evaluationData.newImage);
        }

        fetch(`/api/evaluation?restaurantId=${restaurantId}`, { // restaurantId를 쿼리 파라미터로 추가
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("로그인이 되지 않았습니다");
                }
                return response.text(); // 응답 텍스트를 확인
            })
            .then(data => {
                console.log(data); // 응답 확인용
                if (window.history.length > 1) {
                    window.history.back();
                } else {
                    window.location.href = "/restaurants/" + restaurantId;
                }
            })
            .catch(error => {
                console.error(error); // 에러 메시지 로그
                if (window.history.length > 1) {
                    window.history.back();
                } else {
                    window.location.href = "/restaurants/" + restaurantId;
                }
            });
    });

    // URL에서 식당 ID 추출 함수
    function extractRestaurantIdFromUrl() {
        var urlSegments = window.location.pathname.split('/');
        return urlSegments[urlSegments.length - 1]; // 마지막 세그먼트를 ID로 가정
    }

    // 데이터가 올바르게 입력되었는지 체크하는 함수
    function checkData() {
        if (evaluationData.starRating === 0) {
            return false;
        }


        return true;
    }
});
