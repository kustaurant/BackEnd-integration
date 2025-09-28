document.addEventListener('DOMContentLoaded', function () {
    // ===== 모달 안전 처리 =====
    var modal = document.getElementById("modal");
    var closeButton = document.querySelector('#modal button[data-bs-dismiss="modal"]');
    var hideCheckbox = document.getElementById("flexCheckChecked");

    if (modal) {
        if (closeButton) {
            closeButton.addEventListener("click", function() {
                var expiryDate = new Date();
                expiryDate.setDate(expiryDate.getDate() + 7);
                document.cookie = "hideModal=" + (hideCheckbox && hideCheckbox.checked ? "true" : "false") + "; expires=" + expiryDate.toUTCString();
                modal.style.display = "none";
            });
        }

        // 쿠키 체크 후 모달 표시
        var cookies = document.cookie.split(";").map(c => c.trim());
        var hideModalCookie = cookies.find(c => c.startsWith("hideModal="));
        if (!hideModalCookie || hideModalCookie.split("=")[1] !== "true") {
            modal.style.display = "block";
        }
    }

    // ===== Swiper 초기화 (모달 여부와 무관하게 항상 실행되도록 분리) =====
    if (typeof Swiper !== 'undefined') {
        // .swiper 컨테이너가 존재하는지 확인
        var swiperEl = document.querySelector('.swiper');
        if (swiperEl) {
            var swiper = new Swiper('.swiper', {
                slidesPerView: 1,
                spaceBetween: 60,
                loop: true,
                autoplay: {
                    delay: 2000,
                    disableOnInteraction: false,
                },
                keyboard: { enabled: true, onlyInViewport: true },
                breakpoints: {
                    576:  { slidesPerView: 2 },
                    768:  { slidesPerView: 3 },
                    992:  { slidesPerView: 3 },
                    1200: { slidesPerView: 5 },
                    1800: { slidesPerView: 7 },
                    2500: { slidesPerView: 9 },
                },
                effect: 'slide',
                // pagination: { el: '.slider-four-slide-pagination-1', clickable: true }, // 쓰려면 HTML 주석 해제
                // navigation: { nextEl: '.swiper-button-next', prevEl: '.swiper-button-prev' },
            });
        }
    } else {
        console.error('Swiper is not loaded. 확인: vendors.min.js 혹은 Swiper 번들이 로드되는지');
    }
});
