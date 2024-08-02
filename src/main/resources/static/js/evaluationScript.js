document.addEventListener("DOMContentLoaded", function () {
    // ----------(first) 메인 평가 선택 로직---------- //

    var stars = document.querySelectorAll(".stars");
    let isDragging = false;
    var comment = document.querySelector("#ratingComment");

    stars.forEach(function (star, index) {
        // 클릭했을 경우
        star.addEventListener("click", function () {
            setMainRating(index);
        });
        // 마우스를 클릭했을 경우 - 드래그 시작
        star.addEventListener('mousedown', function() {
            isDragging = true;
            setMainRating(index);
        });
        // 마우스가 드래그 중일 때 별위로 마우스가 올라갔을 경우
        star.addEventListener('mouseenter', function() {
            if (isDragging) {
                setMainRating(index);
            }
        });
        // 모바일 환경 - 터치 시작
        star.addEventListener('touchstart', (event) => {
            isDragging = true;
            setMainRating(index);
        });
        // 모바일 환경 - 터치 이동
        star.addEventListener('touchmove', (event) => {
            event.preventDefault();
            const touch = event.touches[0];
            const target = document.elementFromPoint(touch.clientX, touch.clientY);
            const index = Array.from(stars).indexOf(target);
            if (index !== -1) {
                setMainRating(index);
            }
        });
        // 모바일 환경 - 터치 종료
        star.addEventListener('touchend', () => {
            isDragging = false;
        });
    });

    document.addEventListener('mouseup', function() {
        isDragging = false;
    })

    function setMainRating(selectedIndex) {
        stars.forEach(function (star, index) {
            if (index <= selectedIndex) {
                star.src = "/img/evaluation/star-filled.png";
            } else {
                star.src = "/img/evaluation/star-empty.png";
            }
            evaluationData.starRating = selectedIndex + 1
        });

        if (selectedIndex > 5) {
            comment.style.color = '#0F6341';
            comment.style.fontWeight = 'bold';
        } else if (selectedIndex > 4) {
            comment.style.color = '#0F6341';
            comment.style.fontWeight = '500';
        } else if (selectedIndex > 3) {
            comment.style.color = '#000';
            comment.style.fontWeight = 'normal';
        } else {
            comment.style.color = '#555';
            comment.style.fontWeight = 'normal';
        }

        if (selectedIndex == 0) {
            comment.textContent = "다시 올 일은 없을 것 같습니다";
        } else if (selectedIndex == 1) {
            comment.textContent = "음.. 다시 오고싶다는 생각은 딱히.. ";
        } else if (selectedIndex == 2) {
            comment.textContent = "조금 아쉬운 가게에요";
        } else if (selectedIndex == 3) {
            comment.textContent = "평범한 음식점이에요";
        } else if (selectedIndex == 4) {
            comment.textContent = "괜찮아요. 다시 방문은 할겁니다";
        } else if (selectedIndex == 5) {
            comment.textContent = "제법 괜찮아요. 무조건 재방문 합니다";
        } else {
            comment.textContent = "내 마음속 최고의 가게!";
        }

    }

// ---------- (second) 버튼 선택 효과 로직---------- //

    var categoryBtns = document.querySelectorAll(".categoryBtn");

    categoryBtns.forEach(function (button) {
        button.addEventListener("click", function () {
            toggleCategoryBtnsClass(button);
        });
    });

    function toggleCategoryBtnsClass(button) {
        button.classList.toggle("unselected");
        button.classList.toggle("selected");
    }


    // ---------- (third) 버튼 선택 효과 로직---------- //

    document.querySelectorAll(".keywordBtn").forEach(function (button, index) {
        button.addEventListener("click", function () {
            toggleKeywordsBtnClass(button);
            togglekeywordsRatingArea(index)
        });
    });

    function toggleKeywordsBtnClass(button) {
        button.classList.toggle("unselected");
        button.classList.toggle("selected");
    }

    function togglekeywordsRatingArea(index) {
        var targetDiv = document.querySelector('#keywordEvaluateSection .keywordEvaluateArea:nth-child(' + (index + 1) + ')');

        targetDiv.classList.toggle('hidden');
        if (targetDiv.classList.contains('hidden')) {
            resetRatingInArea(targetDiv);
        }
    }

    // 키워드 평가 태그 두번 눌러서 숨겨질 때 데이터 초기화하기
    function resetRatingInArea(area) {
        // 모든 선택 포인트에서 'picked' 클래스 제거
        var selectPoints = area.querySelectorAll('.select-point .circle');
        selectPoints.forEach(function (point) {
            point.classList.remove('picked');
        });

        // 모든 평가 텍스트에서 'bold' 클래스 제거
        var ratingParagraphs = area.querySelectorAll('.bar-comment-area p');
        ratingParagraphs.forEach(function (p) {
            p.classList.remove('bold');
        });

        // 평가 데이터에서 해당 키워드 평가 점수를 리셋
        var keywordIndex = Array.from(document.querySelectorAll('.keywordEvaluateArea')).indexOf(area);
        evaluationData.barRatings[keywordIndex] = undefined;

        // 바 색깔 원상태로
        var evaluationBars = area.querySelectorAll('.picked-bar');
        evaluationBars.forEach(function (bar) {
            bar.classList.remove('picked-bar');
        })
        var evaluationCircles = area.querySelectorAll('.color-circle');
        evaluationCircles.forEach(function (bar) {
            bar.classList.remove('color-circle');
        })
    }

    // ---- 바형 ui에서 선택 효과 로직 ---- //

    var keywordEvaluateAreas = document.querySelectorAll('.keywordEvaluateArea');

    keywordEvaluateAreas.forEach(function (keyword) {
        var selectPoints = keyword.querySelectorAll('.select-point');

        selectPoints.forEach(function (point, index) {
            // 클릭했을 경우
            point.addEventListener("click", function () {
                toggleSelectPoint(index + 1, keyword);
            });
            // 마우스를 클릭했을 경우 - 드래그 시작
            point.addEventListener('mousedown', function() {
                isDragging = true;
                toggleSelectPoint(index + 1, keyword);
            });
            // 마우스가 드래그 중일 때 별위로 마우스가 올라갔을 경우
            point.addEventListener('mouseenter', function() {
                if (isDragging) {
                    toggleSelectPoint(index + 1, keyword);
                }
            });
            // 모바일 환경 - 터치 시작
            point.addEventListener('touchstart', (event) => {
                isDragging = true;
                toggleSelectPoint(index + 1, keyword);
            });
            // 모바일 환경 - 터치 이동
            point.addEventListener('touchmove', (event) => {
                event.preventDefault();
                const touch = event.touches[0];
                selectPoints.forEach(function(element, elementIndex) {
                    const rect = element.getBoundingClientRect();
                    if (touch.clientX >= rect.left && touch.clientX <= rect.right - 1 &&
                        touch.clientY >= rect.top && touch.clientY <= rect.bottom) {
                        toggleSelectPoint(elementIndex + 1, keyword);
                        return;
                    }
                })
            });
            // 모바일 환경 - 터치 종료
            point.addEventListener('touchend', () => {
                isDragging = false;
            });
        });
    });
    document.addEventListener('mouseup', function() {
        isDragging = false;
    })

    function toggleSelectPoint(rating, keyword) {
        var selectPoints = keyword.querySelectorAll('.select-point .circle');
        var leftBars = keyword.querySelectorAll('.left-bar');
        var rightBars = keyword.querySelectorAll('.right-bar');
        var ratingParagraphs = keyword.querySelectorAll('.bar-comment-area p');

        // 해당 keyword 내에서 모든 select-point에서 picked 클래스 제거
        selectPoints.forEach(function (point) {
            point.classList.remove('picked');
            point.classList.remove('color-circle');
        });
        leftBars.forEach(function (point) {
            point.classList.remove('picked-bar');
        });
        rightBars.forEach(function (point) {
            point.classList.remove('picked-bar');
        });

        // 클릭된 select-point에 picked 클래스 추가
        selectPoints.forEach(function(circle, index) {
            if (index < rating - 1) {
                circle.classList.add("color-circle");
            } else if (index === rating - 1) {
                circle.classList.add('picked');
            }
        })
        // 클릭된 부분 앞의 bar에 picked-bar 클래스 추가
        leftBars.forEach(function(bar, index) {
            if (index < rating - 1) {
                bar.classList.add('picked-bar');
            }
        })
        rightBars.forEach(function(bar, index) {
            if (index < rating - 1) {
                bar.classList.add('picked-bar');
            }
        })

        // 각 p 태그에 bold 클래스를 toggle
        ratingParagraphs.forEach(function (p, index) {
            p.classList.toggle('bold', index + 1 === rating);
        });
        var keywordIndex = Array.from(keywordEvaluateAreas).indexOf(keyword);
        evaluationData.barRatings[keywordIndex] = rating;
    }
    // 전송 데이터 초기화
    var evaluationData = {
        starRating: 0,
        barRatings: [],
        restaurantId: 0
    }
    // 데이터가 제대로 입력 됐는지 체크
    function checkData() {
        if (evaluationData.starRating === 0) {
            return false;
        }
        let areas = document.querySelectorAll('.keywordEvaluateArea');
        let dataIndex = [];
        areas.forEach(function(area, index) {
            if (!area.classList.contains('hidden')) {
                dataIndex.push(index);
            }
        });
        if (dataIndex.length === 0 && evaluationData.barRatings.length === 0)
            return true;
        for (let i = 0; i < evaluationData.barRatings.length; i++) {
            if (dataIndex.length === 0 && evaluationData.barRatings[i]) {
                return false;
            }

            if (dataIndex[0] === i && !evaluationData.barRatings[i]) {
                return false;
            } else if (dataIndex[0] !== i && evaluationData.barRatings[i]) {
                return false;
            } else if (dataIndex[0] === i) {
                dataIndex.shift();
            }
        }
        return dataIndex.length <= 0;
    }
    // ----------제출 버튼 눌림효과 로직---------- //
    
    var submitBtn = document.getElementById('submitBtn');
    
    // 평가하기 버튼 눌렀을 때
    submitBtn.addEventListener('click', function () {
        if (!checkData()) {
            alert('모든 항목을 평가해주세요.');
            return;
        }

        var restaurantId = extractRestaurantIdFromUrl();
        evaluationData.restaurantId = restaurantId;

        fetch("/api/evaluation", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(evaluationData)
        })
            .then(response =>{
                if(!response.ok){
                    throw new Error("로그인이 되지 않았습니다");
                }
                return response;
            })
            .then(data => {
                if (window.history.length > 1) {
                    window.history.back();
                } else {
                    window.location.href = "/restaurants/"+restaurantId;
                }
            }
        )
            .catch(error =>{
                if (window.history.length > 1) {
                    window.history.back();
                } else {
                    window.location.href = "/restaurants/"+restaurantId;
                }
            })

    })

    function extractRestaurantIdFromUrl() {
        // URL에서 레스토랑 ID 추출하는 로직
        // 예시: http://example.com/restaurant/123 -> 123 반환
        var urlSegments = window.location.pathname.split('/');
        return urlSegments[urlSegments.length - 1]; // 마지막 세그먼트가 ID라고 가정
    }



    /* 이전에 평가한 데이터가 있을 경우 버튼을 누르게 함. */
    var situationJsonElement = document.getElementById('situationJson');
    var situationJson = situationJsonElement.getAttribute('data-situationJson');
    const situationData = JSON.parse(situationJson);
    const mainScore = situationJsonElement.getAttribute('data-mainScore');
    var mainStars = document.querySelectorAll('.stars');
    mainStars[mainScore - 1].click();
    var situationButtons = document.querySelectorAll('#keywordList button');
    var lvContainers = document.querySelectorAll('.lv-container');
    for (var key in situationData) {
        if (situationData.hasOwnProperty(key)) {
            situationButtons[key - 1].click();
            lvContainers[key - 1].querySelector('.lv' + situationData[key]).click();
        }
    }
});