document.addEventListener('DOMContentLoaded', function () {

    // var modal = new bootstrap.Modal(document.getElementById("modal"));

    // modal.show();
    //
    // function setCookie( name, value, expiredays ) {  // 쿠키저장
    //     var todayDate = new Date();  //date객체 생성 후 변수에 저장
    //     todayDate.setDate( todayDate.getDate() + expiredays );
    //     // 시간지정(현재시간 + 지정시간)
    //     document.cookie = name + "=" + value + "; path=/; expires=" + todayDate.toUTCString() + ";"
    //     //위 정보를 쿠키에 굽는다
    // }

    // var modal = document.getElementById("modal");
    // var closeButton = document.querySelector('#modal button[data-bs-dismiss="modal"]');
    // var hideCheckbox = document.getElementById("flexCheckChecked");
    //
    // closeButton.addEventListener("click", function() {
    //     var expiryDate = new Date();
    //     expiryDate.setDate(expiryDate.getDate() + 7); // 일주일 후의 날짜 설정
    //     document.cookie = "hideModal=" + (hideCheckbox.checked ? "true" : "false") + "; expires=" + expiryDate.toUTCString(); // 쿠키 설정
    //     modal.style.display = "none"; // 모달 닫기
    // });
    //
    // // 페이지 로드 시 쿠키 확인
    // var cookies = document.cookie.split(";").map(cookie => cookie.trim());
    // var hideModalCookie = cookies.find(cookie => cookie.startsWith("hideModal="));
    //
    // // 쿠키에 hideModal이 없거나 그 값이 false일 경우에만 모달을 보여줌
    // if (!hideModalCookie || hideModalCookie.split("=")[1] !== "true") {
    //     modal.style.display = "block";
    // }


    var swiper = new Swiper('.swiper', {
        slidesPerView: 1,
        spaceBetween: 60,
        loop: true,
        autoplay: {
            delay: 2000, // 여기에서 속도를 조정하세요
            disableOnInteraction: false,
        },
        keyboard: {
            enabled: true,
            onlyInViewport: true,
        },
        breakpoints: {
            2500: {
                slidesPerView: 9,
            },
            1800: {
                slidesPerView: 7,
            },
            1200: {
                slidesPerView: 5,
            },
            992: {
                slidesPerView: 3,
            },
            768: {
                slidesPerView: 3,
            },
            576: {
                slidesPerView: 2,
            },
        },
        effect: 'slide',
    });
});
