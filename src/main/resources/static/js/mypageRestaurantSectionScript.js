const dropdownSpan = document.querySelector('#dropdownMenuButton span');
const dropdownItems = document.querySelectorAll('.dropdown-item');
const restaurantEvaluationContainerOuter = document.querySelector('.restaurant-evaluation-container-outer');
const latestSortedArray = Array.from(document.querySelectorAll('.restaurant-evaluation-container'));

// 최신순
dropdownItems[0].addEventListener('click', function() {
    restaurantEvaluationContainerOuter.innerHTML = ''; // 내용 비우기
    dropdownSpan.innerText = this.innerText; // dropdown 문구 교체
    // 최신순 배열 삽입
    latestSortedArray.forEach(element => restaurantEvaluationContainerOuter.appendChild(element));
});
// 오래된순
dropdownItems[1].addEventListener('click', function() {
    restaurantEvaluationContainerOuter.innerHTML = ''; // 내용 비우기
    dropdownSpan.innerText = this.innerText; // dropdown 문구 교체
    // 오래된순 배열 삽입
    let oldestSortedArray = latestSortedArray.slice().reverse();
    oldestSortedArray.forEach(element => restaurantEvaluationContainerOuter.appendChild(element));
});
// 높게 평가한순
dropdownItems[2].addEventListener('click', function() {
    restaurantEvaluationContainerOuter.innerHTML = ''; // 내용 비우기
    dropdownSpan.innerText = this.innerText; // dropdown 문구 교체
    // 오래된순 배열 삽입
    let oldestSortedArray = latestSortedArray.slice().sort((a, b) => {
        let aScore = a.querySelector('.category-main-score').dataset.score;
        let bScore = b.querySelector('.category-main-score').dataset.score;
        return bScore - aScore;
    });
    oldestSortedArray.forEach(element => restaurantEvaluationContainerOuter.appendChild(element));
});
dropdownItems[3].addEventListener('click', function() {
    restaurantEvaluationContainerOuter.innerHTML = ''; // 내용 비우기
    dropdownSpan.innerText = this.innerText; // dropdown 문구 교체
    // 오래된순 배열 삽입
    let oldestSortedArray = latestSortedArray.slice().sort((a, b) => {
        let aScore = a.querySelector('.category-main-score').dataset.score;
        let bScore = b.querySelector('.category-main-score').dataset.score;
        return aScore-bScore;
    });
    oldestSortedArray.forEach(element => restaurantEvaluationContainerOuter.appendChild(element));
});