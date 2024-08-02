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
                window.location.href = response.url;
            } else if (response.ok) {
                // 모달 표시하기
                isFeedbackWindowOpen = true;
                modal.show();
            } else {
                throw new Error('Network response was not ok');
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}
function submitFeedback() {
    const feedbackBody = feedbackTextarea.value.trim();
    setIsFeedbackWindowOpenFalse();
    fetch(requestBaseURL + '/api/feedback', {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            feedbackBody: feedbackBody
        })
    })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else if (response.status === 200 || response.status === 404 || response.status === 400) {
                return response.text().then(errorMessege => {
                    alert(errorMessege);
                })
            }
        })
}