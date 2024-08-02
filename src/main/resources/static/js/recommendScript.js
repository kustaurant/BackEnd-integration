document.addEventListener('DOMContentLoaded', function () {
    // 기본값 설정
    var restaurantLocation = "전체";
    const selectedCuisines = [];
    // 초기에 선택되어야 하는 음식 카테고리 리스트
    const initialSelectedCuisines = ["한식", "일식", "중식", "양식", "아시안", "고기", "치킨", "햄버거", "분식", "해산물"];

    // 모든 cell-button을 순회하며, 이미지 경로 초기화 및 선택 상태 설정
    document.querySelectorAll('.cell-button').forEach(button => {
        // 초기에 선택된 카테고리에 해당하는 버튼이면, 선택된 상태로 변경
        if (initialSelectedCuisines.includes(button.dataset.cuisine)) {
            button.classList.add('selected');
        }
    });

    // 음식 종류에 대한 클릭 리스너
    document.querySelectorAll('.cell-button').forEach(button => {
        button.addEventListener('click', function () {
            const img = this.querySelector('img');

            // "전체" 버튼 클릭 시 특별 처리
            if (this.dataset.cuisine === "전체") {
                    // 모든 다른 버튼의 "selected" 클래스 제거 및 기본 이미지로 변경
                    document.querySelectorAll('#mainContents .cell-button').forEach(otherButton => {
                        otherButton.classList.add('selected');
                    });
                // if (this.classList.contains('selected')) {
                //     // "전체" 버튼이 이미 선택된 상태였다면, 해당 버튼만 선택 해제하고 기본 이미지로 변경
                //     this.classList.remove('selected');
                //     return; // 추가 처리 없이 함수 종료
                // } else {
                //     // 모든 다른 버튼의 "selected" 클래스 제거 및 기본 이미지로 변경
                //     document.querySelectorAll('.cell-button').forEach(otherButton => {
                //         otherButton.classList.remove('selected');
                //     });
                // }

            } else {
                // 다른 카테고리 버튼 클릭 시 "전체" 버튼의 "selected" 클래스 제거 및 기본 이미지로 변경
                const allButton = document.querySelector('.cell-button[data-cuisine="전체"]');
                allButton.classList.remove('selected');
                const allImg = allButton.querySelector('img');
            }

            // 버튼의 "selected" 클래스를 토글 및 이미지 src 변경
            if (this.classList.contains('selected')) {
                this.classList.remove('selected');
            } else {
                this.classList.add('selected');
            }
        });
    });

// 드롭다운 항목에 대한 클릭 이벤트 리스너를 추가합니다.
    document.querySelectorAll('.dropdown-menu .dropdown-item').forEach(item => {
        item.addEventListener('click', function () {
            // 선택된 항목의 텍스트를 변수에 저장합니다.
            restaurantLocation = this.textContent.trim()

            // 버튼의 텍스트를 선택된 항목의 텍스트로 업데이트합니다.
            // '지역 : ' 접두사를 추가하여 사용자가 선택한 지역을 명확하게 표시합니다.
            document.querySelector('#dropdownMenuButton span').textContent = '지역 : ' + restaurantLocation;
            console.log(restaurantLocation)

        });
    });


    // 뽑기 버튼 리스너
    document.getElementById('recommendBtn').addEventListener('click', function () {


        document.querySelectorAll('.cell-button').forEach(button => {
            if (button.classList.contains('selected')) {
                selectedCuisines.push(button.dataset.cuisine);
            }

        });


        if (selectedCuisines.length === 0) {
            // 비어있다면 경고창을 띄우고 함수 종료
            alert("최소 하나의 음식 카테고리를 선택해야 합니다.");
            return;
        }

        const apiUrl = "/api/recommend?cuisine=" + selectedCuisines.join('-') + "&location=" + restaurantLocation

        fetch(apiUrl, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`${response.status}: ${response.message}`);
                }
                return response.json();
            })

            .then(data => {
                // 데이터가 비어 있는 경우
                if (!data || data.length === 0) {
                    alert("해당 조건에 맞는 맛집이 존재하지 않습니다.");
                    window.location.reload(); // 페이지 새로고침
                    return; // 이후 로직을 실행하지 않기 위해 함수에서 빠르게 탈출
                }
                var restaurantList = data

                const imgDivs = document.querySelectorAll('.result-img-list');
                let imgCount = 0;
                imgDivs.forEach(imgDiv => {
                    restaurantList.forEach(restaurant => {
                        const imgElement = document.createElement('img');
                        if (restaurant.restaurantImgUrl !== "no_img") {
                            imgElement.src = restaurant.restaurantImgUrl;
                        } else {
                            imgElement.src = "/img/recommend/no_img.png";
                        }


                        // 레스토랑 id를 data 속성으로 추가
                        imgElement.setAttribute('data-id', restaurant.restaurantId);

                        imgDiv.appendChild(imgElement);
                    });
                });


                document.querySelector(".dropdown").classList.add('hidden')
                document.getElementById('recommendPage').classList.add('hidden');
                document.getElementById('recommendBtn').classList.add('hidden');
                document.getElementById('resultPage').classList.remove('hidden');
                // 스크롤 이미지슬라이드바로 올리기
                const element = document.querySelector('.result-img-slideBar');
                element.scrollIntoView({
                    block: 'center', // 수직 방향으로 중앙에 위치
                    inline: 'center' // 수평 방향으로 중앙에 위치 (필요한 경우)
                });
                document.getElementById('restartBtn').classList.remove('hidden');
                document.getElementById('restartDirectBtn').classList.remove('hidden');


            })
            .catch(error => {
                console.error("Error adding comment:", error);
            });

        const storeHref = document.getElementById('storeHref');
        storeHref.removeAttribute('href');

        setTimeout(function () {
            matchingdata();
            document.getElementById('resultInfoPage').classList.remove('hidden');
            const resultInfoPage = document.getElementById('resultInfoPage');
            resultInfoPage.style.opacity = 1;
            const processingTitle = document.getElementById('processingTitle');
            processingTitle.textContent = '사진을 누르면 해당 가게 페이지로 이동합니다';

            setTimeout(function () {
                const restartBtn = document.getElementById('restartBtn');
                restartBtn.style.opacity = 1;
                const restartDirectBtn = document.getElementById('restartDirectBtn');
                restartDirectBtn.style.opacity = 1;

                const resultImgSlideBar = document.querySelector('.result-img-slideBar');
                resultImgSlideBar.style.opacity = 0;


                setTimeout(function () {
                    resultImgSlideBar.classList.add('hidden');
                }, 500);

            }, 500);

        }, 2300);


    });

// 선택된 img의 data-id를 통해 해당 id와 일치하는 식당정보 가져오기
    function matchingdata() {
        var image = findClosestImageToSelectBox()
        var restaurantId = image.dataset.id; // 이미지 URL을 기준으로 정보 조회

        const apiUrl = "/api/recommend/restaurant?restaurantId=" + restaurantId

        fetch(apiUrl, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
        })
            .then(response => response.json())
            .then(data => {
                const matchedData = data;
                if (matchedData) {
                    const restaurantId = matchedData.restaurantId;
                    const restaurantCuisine = matchedData.restaurantCuisine;
                    const restaurantName = matchedData.restaurantName;
                    const restaurantType = matchedData.restaurantType;
                    const restaurantImageUrl = matchedData.restaurantImgUrl;


                    // 식당 점수
                    let score = 0.0
                    score = (matchedData.restaurantScoreSum / matchedData.restaurantEvaluationCount) / 7.0 * 10.0;
                    var formattedScore = score.toFixed(1) + "/10.0";


                    // 이미지 URL과 링크 설정
                    const resultImg = document.getElementById('resultImg');
                    const storeHref = document.getElementById('storeHref');
                    const restaurantUrl = `/restaurants/${restaurantId}`;

                    // 뽑힌 식당의 이미지 url 이 있으면 설정, 없으면 임시이미지
                    if (matchedData.restaurantImgUrl !== "no_img" && matchedData.restaurantImgUrl !== "no_restaurant") {
                        resultImg.src = matchedData.restaurantImgUrl;
                    } else {
                        resultImg.src = "/img/recommend/no_img.png";
                    }
                    storeHref.href = restaurantUrl;

                    // 식당 정보 삽입
                    const storeInfo = document.getElementById('storeInfo');
                    // 불러온 식당의 평가데이터가 하나도 없는 경우
                    if (matchedData.restaurantEvaluationCount === 0) {
                        storeInfo.innerHTML = `<div class="pt-30px bg-white text-center alt-font">
                    <span class="d-inline-block text-dark-gray fs-19 fw-600">${restaurantName}</span>
                    <div class="w-100">
                        <span class="d-inline-block align-middle">${restaurantType}</span>
                    </div>
                </div>`
                    } // 최소 하나 이상 평가 데이터가 있는 경우 
                    else {
                        storeInfo.innerHTML = `<div class="pt-30px bg-white text-center alt-font">
                    <span class="d-inline-block text-dark-gray fs-19 fw-600">${restaurantName}</span>
                    <div class="w-100">
                        <span class="d-inline-block align-middle">${restaurantType}</span>
                        <span class="d-inline-block align-middle ms-10px me-10px fs-12 opacity-5">◍</span>
                        <span class="d-inline-block align-middle">${formattedScore}</span>
                    </div>
                </div>`
                    }


                    const selectedBox = document.getElementById('SelectedBox');
                    selectedBox.style.backgroundImage = `url('${restaurantImageUrl}')`;

                }
            })
            .catch(error => {
                console.error("Error:", error);
            });


    //재설정 버튼 로직
        document.getElementById('restartBtn').addEventListener('click', function () {
            location.reload(); // 현재 URL로 페이지 새로고침
        });


    //바로 다시하기 버튼 로직
        document.getElementById('restartDirectBtn').addEventListener('click', function () {
            //select 박스 초기화
            const selectedBox = document.getElementById('SelectedBox');
            selectedBox.style.backgroundImage = '';
            const processingTitle = document.getElementById('processingTitle');

            processingTitle.innerHTML = '맛집을 고르는 중... <img class="title-icon" src="/img/recommend/wondering.png" alt="wondering_img">'


            const apiUrl = "/api/recommend?cuisine=" + selectedCuisines.join('-') + "&location=" + restaurantLocation;

            fetch(apiUrl, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`${response.status}: ${response.message}`);
                    }
                    return response.json();
                })

                .then(data => {
                    // 데이터가 비어 있는 경우
                    if (!data || data.length === 0) {
                        alert("해당 조건에 맞는 맛집이 존재하지 않습니다.");
                        window.location.reload(); // 페이지 새로고침
                        return;
                    }
                    var restaurantList = data
                    const imgDivs = document.querySelectorAll('.result-img-list');
                    imgDivs.forEach(imgDiv => {
                        while (imgDiv.firstChild) {
                            imgDiv.removeChild(imgDiv.firstChild);
                        }
                        restaurantList.forEach(restaurant => {
                            const imgElement = document.createElement('img');
                            if (restaurant.restaurantImgUrl !== "no_img" && restaurant.restaurantImgUrl !== "no_restaurant") {
                                imgElement.src = restaurant.restaurantImgUrl;
                            } else {
                                imgElement.src = "/img/recommend/no_img.png";
                            }


                            // 레스토랑 id를 data 속성으로 추가
                            imgElement.setAttribute('data-id', restaurant.restaurantId);

                            imgDiv.appendChild(imgElement);
                        });
                    });


                    // 결과 페이지 가리기
                    document.getElementById('resultInfoPage').classList.add('hidden');

                    // 슬라이더 시작
                    const resultImgSlideBar = document.querySelector('.result-img-slideBar');
                    resultImgSlideBar.style.opacity = 1;

                    // 스크롤 이미지슬라이드바로 올리기
                    const element = document.querySelector('.result-img-slideBar');
                    element.classList.remove('hidden');
                    element.scrollIntoView({
                        block: 'center', // 수직 방향으로 중앙에 위치
                        inline: 'center' // 수평 방향으로 중앙에 위치 (필요한 경우)
                    });
                    // 버튼 삭제
                    const restartDirectBtn = document.getElementById('restartDirectBtn');
                    const restartBtn = document.getElementById('restartBtn');

                    restartDirectBtn.classList.add("hidden")
                    restartBtn.classList.add("hidden")


                })
                .catch(error => {
                    console.error("Error adding comment:", error);
                });

            const storeHref = document.getElementById('storeHref');
            storeHref.removeAttribute('href');

            // 멈춘 뒤 멈춘 자리의 사진 데이터 정보 띄우기
            setTimeout(function () {
                matchingdata();
                document.getElementById('resultInfoPage').classList.remove('hidden');
                const resultInfoPage = document.getElementById('resultInfoPage');
                resultInfoPage.style.opacity = 1;
                const processingTitle = document.getElementById('processingTitle');
                processingTitle.textContent = '사진을 누르면 해당 가게 페이지로 이동합니다';

                setTimeout(function () {
                    const restartBtn = document.getElementById('restartBtn');
                    restartBtn.classList.remove("hidden")
                    const restartDirectBtn = document.getElementById('restartDirectBtn');
                    restartDirectBtn.classList.remove("hidden")

                    const resultImgSlideBar = document.querySelector('.result-img-slideBar');
                    resultImgSlideBar.style.opacity = 0;


                    setTimeout(function () {
                        resultImgSlideBar.classList.add('hidden');
                    }, 500);

                }, 500);

            }, 2300);
        });

// 셀렉 박스 위치와 가장 가까운 이미지 가져오기
        function findClosestImageToSelectBox() {
            const selectBox = document.querySelector('#SelectedBox'); // .select-box 요소 선택
            const images = document.querySelectorAll('.result-img-list > img'); // 모든 .img_box 요소 선택
            const selectBoxRect = selectBox.getBoundingClientRect(); // .select-box의 위치 정보

            let closestImage = null;
            let minDistance = Infinity;

            images.forEach((image) => {
                const imageRect = image.getBoundingClientRect(); // 각 이미지의 위치 정보
                // .select-box와 이미지 중심점 사이의 거리 계산
                const distance = Math.sqrt(Math.pow(imageRect.x - selectBoxRect.x, 2) + Math.pow(imageRect.y - selectBoxRect.y, 2));

                if (distance < minDistance) {
                    closestImage = image; // 가장 가까운 이미지 업데이트
                    minDistance = distance;
                }
            });


            return closestImage
        }

    }
})



