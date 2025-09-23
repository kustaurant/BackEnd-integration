$(document).ready(function () {
    // --------------- 클릭된 종류 버튼 효과 ----------------------------
    // 현재 URL에서 쿼리 스트링 추출
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    let cuisineParam = urlParams.get('cuisines');
    if (!cuisineParam) cuisineParam = 'ALL';
    let situationParam = urlParams.get('situations');
    if (!situationParam) situationParam = 'ALL';
    let locationParam = urlParams.get('locations');
    if (!locationParam) locationParam = 'ALL';

    document.querySelectorAll('.category').forEach(btn => {
        btn.addEventListener('click', function() {
            if (btn.dataset.cuisine) {
                let cuisineList = cuisineParam.split(',').map(item => item.trim());
                const newCuisine = btn.dataset.cuisine
                if (newCuisine === 'ALL') {
                    cuisineList = ['ALL']
                } else if (newCuisine === 'JH') {
                    cuisineList = ['JH']
                } else {
                    if (btn.classList.contains('unselected')) {
                        if (cuisineList.includes('ALL') || cuisineList.includes('JH')) {
                            cuisineList = [newCuisine];
                        } else {
                            cuisineList.push(newCuisine)
                        }
                    } else if (btn.classList.contains('selected')) {
                        cuisineList = cuisineList.filter(item => item !== newCuisine);
                    }
                }
                if (cuisineList.length === 0) {
                    cuisineList = ['ALL']
                }
                var apiUrl = `/tier?cuisines=${cuisineList.join(',')}&situations=${situationParam}&locations=${locationParam}`;
                window.location.href = apiUrl;
            } else if (btn.dataset.situation) {
                let situationList = situationParam.split(',').map(item => item.trim());
                const newSituation = btn.dataset.situation
                if (newSituation === 'ALL') {
                    situationList = ['ALL']
                } else {
                    if (btn.classList.contains('unselected')) {
                        if (situationList.includes('ALL')) {
                            situationList = [newSituation];
                        } else {
                            situationList.push(newSituation)
                        }
                    } else if (btn.classList.contains('selected')) {
                        situationList = situationList.filter(item => item !== newSituation);
                    }
                }
                if (situationList.length === 0) {
                    situationList = ['ALL']
                }
                var apiUrl = `/tier?cuisines=${cuisineParam}&situations=${situationList.join(',')}&locations=${locationParam}`;
                window.location.href = apiUrl;
            } else if (btn.dataset.location) {
                let locationList = locationParam.split(',').map(item => item.trim());
                const newLocation = btn.dataset.location
                if (newLocation === 'ALL') {
                    locationList = ['ALL']
                } else {
                    if (btn.classList.contains('unselected')) {
                        if (locationList.includes('ALL')) {
                            locationList = [newLocation];
                        } else {
                            locationList.push(newLocation)
                        }
                    } else if (btn.classList.contains('selected')) {
                        locationList = locationList.filter(item => item !== newLocation);
                    }
                }
                if (locationList.length === 0) {
                    locationList = ['ALL']
                }
                var apiUrl = `/tier?cuisines=${cuisineParam}&situations=${situationParam}&locations=${locationList.join(',')}`;
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
        document.querySelectorAll("#tierTableBody tr.tr-main").forEach(function (tr) {
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
});

