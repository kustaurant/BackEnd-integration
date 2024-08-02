$(document).ready(function () {
    // --------------- 클릭된 종류 버튼 효과 ----------------------------
    // 현재 URL에서 쿼리 스트링 추출
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    let cuisineParam = urlParams.get('cuisine');
    if (!cuisineParam) cuisineParam = '전체';
    let situationParam = urlParams.get('situation');
    if (!situationParam) situationParam = '전체';
    let positionParam = urlParams.get('position');
    if (!positionParam) positionParam = '전체';

    document.querySelectorAll('.category').forEach(btn => {
        btn.addEventListener('click', function() {
            if (btn.dataset.cuisine) {
                var apiUrl = `/tier?cuisine=${btn.dataset.cuisine}&situation=${situationParam}&position=${positionParam}`;
                window.location.href = apiUrl;
            } else if (btn.dataset.situation) {
                var apiUrl = `/tier?cuisine=${cuisineParam}&situation=${btn.dataset.situation}&position=${positionParam}`;
                window.location.href = apiUrl;
            } else if (btn.dataset.position) {
                var apiUrl = `/tier?cuisine=${cuisineParam}&situation=${situationParam}&position=${btn.dataset.position}`;
                window.location.href = apiUrl;
            }
        })
    });

// pc에서도 카테고리의 가로 스크롤을 마우스 드래그로 할 수 있게 해주는 부분
    var scrollableElements = document.querySelectorAll('.scrollable');
    scrollableElements.forEach(function (scrollableElement) {
        var isMouseDown = false;
        var startX, scrollLeft;

        scrollableElement.addEventListener('mousedown', function (e) {
            isMouseDown = true;
            startX = e.pageX - scrollableElement.offsetLeft;
            scrollLeft = scrollableElement.scrollLeft;
        });

        scrollableElement.addEventListener('mouseleave', function () {
            isMouseDown = false;
        });

        scrollableElement.addEventListener('mouseup', function () {
            isMouseDown = false;
        });

        scrollableElement.addEventListener('mousemove', function (e) {
            if (!isMouseDown) return;
            e.preventDefault();
            var x = e.pageX - scrollableElement.offsetLeft;
            var walk = (x - startX) * 1.5; // 스크롤 속도 조절을 위한 계수
            scrollableElement.scrollLeft = scrollLeft - walk;
        });
    });

    setMouseHover();
    function setMouseHover() {
        // 표 위에 마우스 올렸을 때 색상 변경
        document.querySelectorAll("#tierTableBody tr").forEach(function (tr) {
            // 마우스를 올렸을 때
            tr.addEventListener("mouseenter", function () {
                this.style.backgroundColor = "#eee";
                this.querySelectorAll("*").forEach(function (child) {
                    child.style.backgroundColor = "#eee";
                });
            });
            // 마우스를 빼앗았을 때
            tr.addEventListener("mouseleave", function () {
                this.style.backgroundColor = "";
                this.querySelectorAll("*").forEach(function (child) {
                    child.style.backgroundColor = "";
                });
            });
        });
    }


    // 검색 로직
    const pageController = document.getElementById('pageController');
    const tierTableBody = document.getElementById('tierTableBody');
    const searchInput = document.getElementById('searchInput');
    searchInput.value = '';
    const spinner = document.getElementById('spinner');
    var prevInput = '';
    var currentUrl = window.location.href;
    var baseUrl = window.location.origin;
    var relativeUrl = currentUrl.replace(baseUrl, '');
    var lastInputType = 0; // 0이면 빈칸, 1이면 입력
    let timer;
    function showSpinner() { // Spinner 표시
        const spinnerDisplay = spinner.style.display;
        if (spinnerDisplay === 'none') {
            spinner.style.display = 'inline-block';
        }
    }
    function hideSpinner() { // Spinner 안보이게
        spinner.style.display = 'none';
    }
    // 검색 데이터 미리 불러오기
    let newTrElements;
    let isRestaurantDataLoading = true;
    loadRestaurantList();
    function loadRestaurantList() {
        const apiUrl = "/api/list" + relativeUrl;
        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                newTrElements = doc.querySelectorAll('tr');
                isRestaurantDataLoading = false;
            })
            .catch(error => {
                // 오류 처리
                console.error('Error fetching data:', error);
                location.reload();
            });
    }

    searchInput.addEventListener('input', function(event) {
        let inputValue = event.target.value;
        showSpinner();
        if (prevInput !== '' && inputValue === '') { // 이전에 검색창에 내용이 있었다가 다지워서 빈칸이 된 경우 -> page 원상복귀
            tierTableBody.innerHTML = '';
            lastInputType = 0;
            const apiUrl = "/api/page" + relativeUrl;
            fetch(apiUrl)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(html => {
                    if (html && lastInputType === 0) {
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(html, 'text/html');
                        const tbodyContent = doc.querySelector('tbody').innerHTML;
                        tierTableBody.innerHTML = tbodyContent;

                        hideSpinner();
                        setMouseHover();
                    }
                })
                .catch(error => {
                    // 오류 처리
                    console.error('Error fetching data:', error);
                });

            pageController.style.display = 'flex'; // 페이지 컨트롤러 다시 보이게
        } else {
            lastInputType = 1;
            timer = setTimeout(function() {
                if (lastInputType !== 0) {
                    pageController.style.display = 'none';
                    tierTableBody.innerHTML = '';
                    lastInputType = 1;
                    if (lastInputType === 1 && !isRestaurantDataLoading) {
                        filterTableBody(newTrElements, inputValue);
                    } else if (isRestaurantDataLoading) {
                        // 0.1초마다 체크하여 filterTableBody 함수 실행
                        let interval = setInterval(function() {
                            if (!isRestaurantDataLoading) {
                                clearInterval(interval); // 데이터 로딩이 완료되면 setInterval 종료
                                if (lastInputType === 1) {
                                    filterTableBody(newTrElements, searchInput.value);
                                }
                            }
                        }, 100);
                    }
                } // 검색 결과를 보여주는건 0.4초가 지나야 필터링 해줌.
            }, 400);
        }
        scheduleBlurEvent();
        prevInput = inputValue;
    });

    // 검색어가 이름이나 type에 들어가 있는 행만 보여줌
    function filterTableBody(trList, inputValue) {
        // DocumentFragment 생성
        const fragment = document.createDocumentFragment()

        // 첫 번째 tr은 데이터가 아니어서 1번부터
        for (let i = 1; i < trList.length; i++) {
            let td = trList[i].getElementsByTagName("td")[2];
            let spanList = td.getElementsByTagName("span");
            if (td) {
                let restaurantName = spanList[0].textContent || spanList[0].innerText;
                let restaurantType = spanList[1].textContent || spanList[1].innerText;
                if (
                    restaurantName.includes(inputValue) || restaurantType.includes(inputValue)
                ) {
                    trList[i].style.display = "table-row";
                    // DocumentFragment에 tr 요소 추가
                    fragment.appendChild(trList[i]);
                } else {
                    trList[i].style.display = "none";
                }
            }
        }
        // DocumentFragment를 tierTableBody에 추가
        tierTableBody.appendChild(fragment);

        setMouseHover();
        hideSpinner();
    }
    let timerForBlur;
    function searchInputBlur() {
        searchInput.blur();
    }
    function scheduleBlurEvent() {
        // 이전 타이머가 있다면 취소
        if (timerForBlur) {
            clearTimeout(timerForBlur);
        }
        // 새로운 타이머 설정
        timerForBlur = setTimeout(searchInputBlur, 2000);
    }
});

