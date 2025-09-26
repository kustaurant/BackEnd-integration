const searchArea = document.getElementById('searchArea');
const backdrop = document.getElementById('searchBackdrop');
const mainSearchInput = document.getElementById('mainSearchInput');
const inputContainer = document.getElementById('inputContainer');
const body = document.getElementsByTagName('body')[0];
let isSearching = false;

// 현재 페이지의 프로토콜 가져오기
const currentProtocol = window.location.protocol;
// HTTP 또는 HTTPS 프로토콜에 따라 요청을 보낼 URL 설정
let requestBaseURL = '';
if (currentProtocol === 'http:') {
    requestBaseURL = 'http://localhost:8080';
} else if (currentProtocol === 'https:') {
    requestBaseURL = 'https://kustaurant.com';
}

//-------------
mainSearchInput.addEventListener('focus', function() {
    inputContainer.classList.add('border-highlight');
})
mainSearchInput.addEventListener('blur', function() {
    inputContainer.classList.remove('border-highlight');
})

document.addEventListener('keydown', function(event) {
    // '/' 키가 눌린 경우
    if (event.key === '/') {
        event.preventDefault();
        openSearchWindow();
    }
});

// 뒤에 어두운 배경 누른 경우
backdrop.addEventListener('click', function() {
    closeSearchWindow();
})

// 검색창 열기
function openSearchWindow() {
    body.classList.add('prevent-scroll'); // 스크롤 막기
    isSearching = true;
    searchArea.style.display = 'block';
    mainSearchInput.click();
}
mainSearchInput.onclick = function() {mainSearchInput.focus();}
mainSearchInput.onfocus = function() {isFeedbackWindowOpen = false;}

// 검색창 닫기
function closeSearchWindow() {
    body.classList.remove('prevent-scroll');
    isSearching = false;
    searchArea.style.display = 'none';
}

// 검색창 문자 지우기
document.getElementById('eraseButtonReal').addEventListener('click', function() {
    mainSearchInput.value = '';
    mainSearchInput.click();
})

// 검색하기
function search(searchInput) {
    window.location.href = '/search?kw=' + searchInput;
}
document.addEventListener('keydown', function(event) {
    if (isSearching && !isFeedbackWindowOpen && (event.key === 'Enter' || event.key === 'Return')) {
        search(mainSearchInput.value.trim());
    }
})

// 피드백 버튼 리스너
const feedbackButton = document.getElementById('feedbackButton');
const feedbackTextarea = document.getElementById('feedbackTextarea');
const modal = new bootstrap.Modal(document.getElementById('exampleModalFeedback'));
var isFeedbackWindowOpen = false;

function setIsFeedbackWindowOpenFalse() {
    isFeedbackWindowOpen = false;
}

// 피드백 버튼 누르면 이게 실행됨
function isLogin() {
    feedbackTextarea.value = '';
    fetch(requestBaseURL + '/user/api/is-login', {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
    })
        .then(response => {
            if (response.redirected) {
                // 리다이렉션이 발생하면 로그인 페이지로 이동
                window.location.href = response.url;
            } else if (response.ok) {
                // 로그인된 경우 모달 표시
                isFeedbackWindowOpen = true;
                modal.show();
            } else if (response.status === 400) {
                // 로그인되지 않은 경우 400 Bad Request 처리
                alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                window.location.href = '/user/login'; // 로그인 페이지로 리다이렉트
            } else {
                throw new Error('Network response was not ok');
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
            alert('로그인 확인 중 문제가 발생했습니다.');
        });
}



function submitFeedback() {
    // 피드백 입력 내용
    const feedbackBody = feedbackTextarea.value.trim();

    // CSRF 토큰과 헤더 이름을 메타 태그에서 가져옴
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 피드백 창 닫기
    setIsFeedbackWindowOpenFalse();

    // 피드백 제출 요청
    fetch(requestBaseURL + '/api/feedback', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken, // CSRF 헤더와 토큰을 요청 헤더에 추가
        },
        body: JSON.stringify({
            feedbackBody: feedbackBody
        })
    })
        .then(response => {
            if (response.redirected) {
                // 서버가 리다이렉션을 보내면 새로운 페이지로 이동
                window.location.href = response.url;
            } else if (response.status === 200) {
                // 성공적인 응답일 경우 메시지 표시
                alert('피드백이 성공적으로 제출되었습니다.');
            } else if (response.status === 404 || response.status === 400) {
                // 오류 발생 시 서버에서 전달한 오류 메시지를 표시
                return response.text().then(errorMessage => {
                    alert(errorMessage);
                });
            } else {
                // 기타 상태 코드 처리
                alert('알 수 없는 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            // 네트워크 오류 또는 기타 에러 처리
            console.error('Error submitting feedback:', error);
            alert('피드백 제출 중 문제가 발생했습니다.');
        });
}
