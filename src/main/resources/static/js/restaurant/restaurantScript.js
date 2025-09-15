// csrf 토큰 읽어오기
// CSRF 토큰 가져오기
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
// 창이 로드될 때와 창 크기가 바뀔 때 적용할 함수 넣어주기
var map;
var marker;
window.onload = function() {
    mainImgResize();
    // html 에서 식당 정보 가져오기
    var restaurantInfo = document.getElementById('restaurantInfo');
    var name = restaurantInfo.getAttribute('data-name');
    var latitude = parseFloat(restaurantInfo.getAttribute('data-latitude'));
    var longitude = parseFloat(restaurantInfo.getAttribute('data-longitude'));
    // 네이버 지도
    map = new naver.maps.Map('map', {
        center: new naver.maps.LatLng(latitude, longitude),//위도, 경도
        zoom: 16,
        minZoom: 10,
    });
    marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(latitude, longitude),//위도, 경도
        map: map
    });
};
window.onresize = function() {
    mainImgResize();
}

// 평가하기 버튼
const submitBtn = document.getElementById('evaluationButton');

// 메인 이미지 정사각형으로 되게
function mainImgResize() {
    const mainImg = document.getElementById('mainImg');
    mainImg.alt = 'main img';
    let mainImgWidth = parseFloat(getComputedStyle(mainImg.parentElement).width) * 0.3;
    mainImg.style.width = mainImgWidth + 'px';
    mainImg.style.height = mainImgWidth + 'px';
}

// 네이버 지도 펼쳤다 접기
document.getElementById('mapUnfoldButton').addEventListener('click', function() {
    const thisText = this.textContent;
    const mapDiv = document.getElementById('map');
    const mapContainer = document.getElementById('mapContainer');
    const width = parseFloat(getComputedStyle(this).width);

    if (thisText === '펼치기') {
        this.textContent = '접기';
        let newHeight = width * 0.6;
        if (newHeight < 400) {
            newHeight = 400;
        }
        resize(width, newHeight);
        // 지도가 가장 위로 오도록 화면 스크롤
        document.getElementById('mapTopDiv').scrollIntoView({ behavior: 'smooth', block: 'start' });
        //window.scrollBy(0, -110);
    } else {
        this.textContent = '펼치기';
        resize(width, 150);
    }
});
function resize(width, height){
    var Size = new naver.maps.Size(width, height);
    map.setSize(Size);
}

// 댓글 입력 창 글자 제한
const commentTextArea = document.getElementById('commentInput');
const maxLength = 1000;
commentTextArea.addEventListener("input", function() {
    var remainingCharacters = maxLength - commentTextArea.value.length;
    //document.getElementById("remainingCharacters").textContent = remainingCharacters;

    // 최대 길이 초과 시 잘라냄
    if (commentTextArea.value.length > maxLength) {
        commentTextArea.value = commentTextArea.value.substring(0, maxLength);
    }
});