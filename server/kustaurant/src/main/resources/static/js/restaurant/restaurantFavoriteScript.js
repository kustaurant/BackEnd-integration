const beforeImgUrl = 'https://s-lol-web.op.gg/images/icon/icon-bookmark.svg?v=1702977255104';
const afterImgUrl = 'https://s-lol-web.op.gg/images/icon/icon-bookmark-on-w.svg?v=1702977255104';
const favoriteImg = document.getElementById('favoriteImg');
// favorite 버튼 이벤트리스너 등록
favoriteImg.addEventListener("click", () => {
  const isFavorite = favoriteImg.classList.contains("after-favorite");
  if (isFavorite) {
    removeFavoriteRequest();
  } else {
    addFavoriteRequest();
  }
});

// 식당 Favorite 추가 요청
function addFavoriteRequest() {
  fetch(window.location.origin + "/web/api" + window.location.pathname + "/favorite", {
    method: 'PUT',
    headers: {
      "Content-Type": "application/json",
      [csrfHeader]: csrfToken
    }
  })
  .then(handleFavoriteResponse)
  .catch(error => console.error('Error:', error));
}

// 식당 Favorite 제거 요청
function removeFavoriteRequest() {
  fetch(window.location.origin + "/web/api" + window.location.pathname + "/favorite", {
    method: 'DELETE',
    headers: {
      "Content-Type": "application/json",
      [csrfHeader]: csrfToken
    }
  })
  .then(handleFavoriteResponse)
  .catch(error => console.error('Error:', error));
}

// 공통 응답 처리
function handleFavoriteResponse(response) {
  if (response.redirected) {
    window.location.href = "/user/login";
    return;
  }
  if (!response.ok) {
    throw new Error(`HTTP error! Status: ${response.status}`);
  }
  return response.json().then(data => {
    toggleFavoriteHTML(data.isFavorite);
    changeFavoriteCount(data.count);
  });
}

// 즐겨찾기 버튼 상태 변경
function toggleFavoriteHTML(afterToggle) {
  if (!afterToggle) {
    favoriteImg.src = beforeImgUrl;
    favoriteImg.classList.remove('after-favorite');
    favoriteImg.classList.add('before-favorite');
  } else {
    favoriteImg.src = afterImgUrl;
    favoriteImg.classList.remove('before-favorite');
    favoriteImg.classList.add('after-favorite');
  }
}

function changeFavoriteCount(count) {
  document.getElementById('favoriteCount').innerText = `${count}개`;
}