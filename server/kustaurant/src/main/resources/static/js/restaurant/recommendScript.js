document.addEventListener('DOMContentLoaded', () => {
    // 초기 지역 설정
    let restaurantLocation = "전체";
    const selectedCuisines = new Set();
    const initialSelectedCuisines = ["전체"];

    // 자주 사용하는 DOM 요소들을 미리 선택하여 변수에 저장
    const cellButtons = document.querySelectorAll('.cell-button');
    const recommendBtn = document.getElementById('recommendBtn');
    const restartBtn = document.getElementById('restartBtn');
    const restartDirectBtn = document.getElementById('restartDirectBtn');
    const resultPage = document.getElementById('resultPage');
    const recommendPage = document.getElementById('recommendPage');
    const dropdownMenuButton = document.querySelector('#dropdownMenuButton span');
    const dropdownItems = document.querySelectorAll('.dropdown-menu .dropdown-item');
    const resultInfoPage = document.getElementById('resultInfoPage');
    const processingTitle = document.getElementById('processingTitle');
    const storeHref = document.getElementById('storeHref');
    const resultImgSlideBar = document.querySelector('.result-img-slideBar');
    const resultImg = document.getElementById('resultImg');
    const storeInfo = document.getElementById('storeInfo');
    const selectedBox = document.getElementById('SelectedBox');


    // 초기 선택된 카테고리 설정
    cellButtons.forEach(button => {
        if (initialSelectedCuisines.includes(button.dataset.cuisine)) {
            button.classList.add('selected');
        }
    });


    // 음식 카테고리 버튼 클릭 이벤트 리스너 추가
    cellButtons.forEach(button => {
        button.addEventListener('click', function () {
            const cuisine = this.dataset.cuisine;

            if (cuisine === "전체") {
                cellButtons.forEach(btn => btn.classList.add('selected'));
            } else {
                document.querySelector('.cell-button[data-cuisine="전체"]').classList.remove('selected');
                this.classList.toggle('selected');
            }
        });
    });


    // 드롭다운 메뉴 아이템 클릭 이벤트 리스너 추가
    dropdownItems.forEach(item => {
        item.addEventListener('click', function () {
            restaurantLocation = this.textContent.trim();
            dropdownMenuButton.textContent = '지역 : ' + restaurantLocation;
        });
    });


    // "뽑기" 버튼 클릭 이벤트 리스너
    recommendBtn.addEventListener('click', () => {
        selectedCuisines.clear();
        cellButtons.forEach(button => {
            if (button.classList.contains('selected')) {
                selectedCuisines.add(button.dataset.cuisine);
            }
        });

        if (selectedCuisines.size === 0) {
            alert("최소 하나의 음식 카테고리를 선택해야 합니다.");
            return;
        }

        const apiUrl = `/web/api/recommend?cuisine=${Array.from(selectedCuisines).join('-')}&location=${restaurantLocation}`;

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (!data || data.length === 0) {
                    alert("해당 조건에 맞는 맛집이 존재하지 않습니다.");
                    window.location.reload();
                    return;
                }

                // 결과 이미지 표시 함수 호출
                displayResultImages(data);

                // UI 업데이트: 추천 페이지 숨기고 결과 페이지 표시
                document.querySelector(".dropdown").classList.add('hidden');
                recommendPage.classList.add('hidden');
                recommendBtn.classList.add('hidden');
                resultPage.classList.remove('hidden');

                // 결과 이미지 슬라이드바로 스크롤 이동
                resultImgSlideBar.scrollIntoView({ block: 'center', inline: 'center' });
                restartBtn.classList.remove('hidden');
                restartDirectBtn.classList.remove('hidden');

                // 일정 시간 후 결과 정보 표시
                setTimeout(() => {
                    matchingData();
                    resultInfoPage.classList.remove('hidden');
                    resultInfoPage.style.opacity = 1;
                    processingTitle.textContent = '사진을 누르면 해당 가게 페이지로 이동합니다';

                    setTimeout(() => {
                        restartBtn.style.opacity = 1;
                        restartDirectBtn.style.opacity = 1;
                        resultImgSlideBar.style.opacity = 0;

                        setTimeout(() => {
                            resultImgSlideBar.classList.add('hidden');
                        }, 500);
                    }, 500);
                }, 2300);
            })
            .catch(error => {
                console.error("데이터를 가져오는 중 오류 발생:", error);
            });

        storeHref.removeAttribute('href');
    });


    // 결과 이미지를 표시하는 함수
    function displayResultImages(restaurantList) {
        const imgDivs = document.querySelectorAll('.result-img-list');
        imgDivs.forEach(imgDiv => {
            imgDiv.innerHTML = '';
            restaurantList.forEach(restaurant => {
                const imgElement = document.createElement('img');
                imgElement.src = (restaurant.restaurantImgUrl && restaurant.restaurantImgUrl !== "no_img") ? restaurant.restaurantImgUrl : "/img/recommend/no_img.png";
                imgElement.dataset.id = restaurant.restaurantId;
                imgDiv.appendChild(imgElement);
            });
        });
    }


    // 선택된 이미지와 가장 가까운 이미지의 데이터를 가져오는 함수
    function matchingData() {
        const image = findClosestImageToSelectBox();
        const restaurantId = image.dataset.id;

        fetch(`/web/api/recommend/restaurant?restaurantId=${restaurantId}`)
            .then(response => response.json())
            .then(matchedData => {
                if (matchedData) {
                    const {
                        restaurantId,
                        restaurantCuisine,
                        restaurantName,
                        restaurantType,
                        restaurantImgUrl,
                        restaurantScoreSum,
                        restaurantEvaluationCount
                    } = matchedData;

                    let score = 0.0;
                    if (restaurantEvaluationCount > 0) {
                        score = restaurantScoreSum / restaurantEvaluationCount;
                    }
                    const formattedScore = score.toFixed(1) + "/5.0";

                    resultImg.src = (restaurantImgUrl && restaurantImgUrl !== "no_img" && restaurantImgUrl !== "no_restaurant") ? restaurantImgUrl : "/img/recommend/no_img.png";
                    storeHref.href = `/restaurants/${restaurantId}`;

                    if (restaurantEvaluationCount === 0) {
                        storeInfo.innerHTML = `
                            <div class="pt-30px bg-white text-center alt-font">
                                <span class="d-inline-block text-dark-gray fs-19 fw-600">${restaurantName}</span>
                                <div class="w-100">
                                    <span class="d-inline-block align-middle">${restaurantType}</span>
                                </div>
                            </div>`;
                    } else {
                        storeInfo.innerHTML = `
                            <div class="pt-30px bg-white text-center alt-font">
                                <span class="d-inline-block text-dark-gray fs-19 fw-600">${restaurantName}</span>
                                <div class="w-100">
                                    <span class="d-inline-block align-middle">${restaurantType}</span>
                                    <span class="d-inline-block align-middle ms-10px me-10px fs-12 opacity-5">◍</span>
                                    <span class="d-inline-block align-middle">${formattedScore}</span>
                                </div>
                            </div>`;
                    }

                    selectedBox.style.backgroundImage = `url('${restaurantImgUrl}')`;
                }
            })
            .catch(error => {
                console.error("식당 데이터를 가져오는 중 오류 발생:", error);
            });
    }


    // 선택 박스와 가장 가까운 이미지를 찾는 함수(결과 표시를 위한)
    function findClosestImageToSelectBox() {
        const selectBox = document.querySelector('#SelectedBox');
        const images = document.querySelectorAll('.result-img-list > img');
        const selectBoxRect = selectBox.getBoundingClientRect();

        let closestImage = null;
        let minDistance = Infinity;

        images.forEach(image => {
            const imageRect = image.getBoundingClientRect();
            const distance = Math.hypot(imageRect.x - selectBoxRect.x, imageRect.y - selectBoxRect.y);

            if (distance < minDistance) {
                closestImage = image;
                minDistance = distance;
            }
        });

        return closestImage;
    }


    // "재설정" 버튼 클릭 이벤트 리스너 추가
    restartBtn.addEventListener('click', () => {
        location.reload();
    });


    // "다시 뽑기" 버튼 클릭 이벤트 리스너 추가
    restartDirectBtn.addEventListener('click', () => {
        selectedBox.style.backgroundImage = '';
        processingTitle.innerHTML = '맛집을 고르는 중... <img class="title-icon" src="/img/recommend/wondering.png" alt="wondering_img">';

        const apiUrl = `/web/api/recommend?cuisine=${Array.from(selectedCuisines).join('-')}&location=${restaurantLocation}`;

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                if (!data || data.length === 0) {
                    alert("해당 조건에 맞는 맛집이 존재하지 않습니다.");
                    window.location.reload();
                    return;
                }

                displayResultImages(data);

                resultInfoPage.classList.add('hidden');
                resultImgSlideBar.classList.remove('hidden');
                resultImgSlideBar.style.opacity = 1;
                resultImgSlideBar.scrollIntoView({ block: 'center', inline: 'center' });

                restartBtn.classList.add('hidden');
                restartDirectBtn.classList.add('hidden');

                storeHref.removeAttribute('href');

                setTimeout(() => {
                    matchingData();
                    resultInfoPage.classList.remove('hidden');
                    resultInfoPage.style.opacity = 1;
                    processingTitle.textContent = '사진을 누르면 해당 가게 페이지로 이동합니다';

                    setTimeout(() => {
                        restartBtn.classList.remove('hidden');
                        restartDirectBtn.classList.remove('hidden');
                        resultImgSlideBar.style.opacity = 0;

                        setTimeout(() => {
                            resultImgSlideBar.classList.add('hidden');
                        }, 500);
                    }, 500);
                }, 2300);
            })
            .catch(error => {
                console.error("데이터를 가져오는 중 오류 발생:", error);
            });
    });
});
