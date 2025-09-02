var navbarTitles = document.querySelectorAll('.navbar-title');


// -------------navBar 선택 및 보이기 로직 -------------------
navbarTitles.forEach(function(navbarTitle, index) {
    navbarTitle.addEventListener('click', function() {
        // 선택된 탭에 해당하는 인덱스 계산 (인덱스는 0부터 시작하므로 +1 해줍니다)
        var selectedIndex = index + 1;

        // 모든 탭에 대해 selected 클래스를 제거
        navbarTitles.forEach(function(title) {
            title.classList.remove('selected');
        });

        // 클릭된 탭에 selected 클래스 추가
        navbarTitle.classList.add('selected');

        // 모든 contents 영역 숨기기
        var areas = document.querySelectorAll('#mainContents > div');
        areas.forEach(function(area) {
            area.classList.add('hidden');
        });

        // 선택된 contents 영역 보이기
        var selectedArea = document.getElementById('Area' + selectedIndex);
        if (selectedArea) {
            selectedArea.classList.remove('hidden');
        }

    });
});

// -------------navBar 드래그로 옮기기 로직-------------------
var navbar = document.getElementById('navBar');

var isDragging = false;
var startX;
var scrollLeft;

navbar.addEventListener('touchstart', function(e) {
    isDragging = true;
    startX = e.touches[0].clientX - navbar.offsetLeft;
    scrollLeft = navbar.scrollLeft;
});

navbar.addEventListener('touchend', function() {
    isDragging = false;
});

navbar.addEventListener('touchmove', function(e) {
    if (!isDragging) return;
    e.preventDefault();
    var x = e.touches[0].clientX - navbar.offsetLeft;
    var walk = x - startX;
    navbar.scrollLeft = scrollLeft - walk;
});

// ------------- 내정보(Area1) nickname바꾸기 로직 -------------------
document.getElementById('saveBtn').addEventListener('click',function (){
    var newNickname = document.getElementById("nickname").value;
    var newPhoneNum = document.getElementById("phoneNumber").value;

    // 서버로 전송할 데이터 객체 생성
    var dataToSend = {};

    if (newNickname && newNickname.trim() !== '') {
        dataToSend.nickname = newNickname;
    }
    
    if (newPhoneNum && newPhoneNum.trim() !== '') {
        dataToSend.phoneNumber = newPhoneNum;
    }


    // 변경된 데이터가 없으면 요청을 보내지 않음
    if (Object.keys(dataToSend).length === 0) {
        alert("변경된 내용이 없습니다.");
        return;
    }

    // CSRF 토큰 가져오기
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 서버로 전송
    fetch("/user/api/myPage/updateProfile", {
        method: "PATCH",
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(dataToSend), // JSON 형태로 데이터 전송
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage);
                });
            }
        })
        .then(data => {
            alert("변경사항 저장 성공");
            // 페이지 새로고침하여 변경된 정보 반영
            location.reload();
        })
        .catch(error => {
            alert(error.message);
        })
})

//logout
document.getElementById('logoutBtn').addEventListener('click', function (event) {
    event.preventDefault(); // 기본 폼 제출 막기

    // CSRF 토큰 가져오기
    var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 로그아웃 요청 보내기
    fetch('/user/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken  // CSRF 토큰을 헤더에 포함
        }
    })
        .then(response => {
            if (response.ok) {
                window.location.href = "/";  // 로그아웃 후 리디렉션할 페이지
            } else {
                throw new Error('로그아웃 실패');
            }
        })
        .catch(error => {
            console.error('로그아웃 중 오류 발생:', error);
        });
});


