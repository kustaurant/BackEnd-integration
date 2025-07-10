// csrf 토큰 읽어오기
// CSRF 토큰 가져오기
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

const btn1 = document.getElementById('button1');
const btn2 = document.getElementById('button2');
let activeButton = btn1;

// 초기 로드
document.addEventListener('DOMContentLoaded', () => {
  loadComments('POPULARITY');
});

// 버튼 클릭 처리
btn1.addEventListener('click', () => {
  if (activeButton !== btn1) {
    switchActive(btn1);
    loadComments('POPULARITY');
  }
});
btn2.addEventListener('click', () => {
  if (activeButton !== btn2) {
    switchActive(btn2);
    loadComments('LATEST');
  }
});

function switchActive(newBtn) {
  activeButton.classList.remove('active');
  newBtn.classList.add('active');
  activeButton = newBtn;
}

async function loadComments(sort) {
  const restaurantId = window.location.pathname.split('/')[2];
  const res = await fetch(`/web/api/restaurants/${restaurantId}/comments?sort=${sort}`, {
    headers: { 'Accept': 'application/json', [csrfHeader]: csrfToken }
  });
  if (!res.ok) throw new Error(res.status);
  const reviews = await res.json();
  renderComments(reviews);
}

function renderComments(reviews) {
  const commentsUl = document.getElementById('commentList');
  commentsUl.innerHTML = '';

  reviews.forEach((rev, idx) => {
    const li = document.createElement('li');
    // 마지막 댓글이면 라운드 처리
    if (idx === reviews.length - 1) {
      li.style.borderRadius = '0 0 7px 7px';
    }

    li.innerHTML = `
      <div>
        <!-- 댓글 좋아요/싫어요 영역 -->
        <div class="like-div">
          <button class="comment-up" type="button" onclick="commentLike(this)"
                  data-id="${rev.evalId}" data-parent-id="${rev.evalId}">
            <img src="${rev.reactionType==='LIKE'
        ? '/img/community/up-green.png'
        : '/img/community/up.png'}">
          </button>
          <span>${rev.evalLikeCount - rev.evalDislikeCount}</span>
          <button class="comment-down" type="button" onclick="commentDislike(this)"
                  data-id="${rev.evalId}" data-parent-id="${rev.evalId}">
            <img src="${rev.reactionType==='DISLIKE'
        ? '/img/community/down-red.png'
        : '/img/community/down.png'}">
          </button>
        </div>
        <!-- 댓글 본문 영역 -->
        <div class="body-div">
          <div class="evaluation-star-div">
            <img src="${getStarImgUrl(rev.evalScore)}"/>
            <span>${rev.evalScore}</span>
          </div>
          <div class="nick-date-div">
            <img src="${rev.writerIconImgUrl}" />
            <span class="nick-span">${rev.writerNickname}</span>
            <span class="date-span">${rev.timeAgo}</span>
          </div>
          ${rev.evalBody || rev.evalImgUrl ?
            `<div class="real-comment-container">
              ${rev.evalImgUrl
            ? `<img src="${rev.evalImgUrl}"/>`
            : ''}
              <span>${rev.evalBody}</span>
            </div>`
          : ''}
        </div>
      </div>
      <!-- 대댓글 부분 -->
      <ul id="subCommentList">
        ${rev.evalCommentList.map((reply, j) => `
          <li style="${j === rev.evalCommentList.length - 1
        ? 'border-radius: 0 0 7px 7px;'
        : ''}">
            <div class="reply-img-container">
              <img src="/img/restaurant/reply.svg"/>
            </div>
            <div class="like-div">
              <button class="comment-up" type="button" onclick="commentLike(this)"
                      data-id="${reply.commentId}"
                      data-parent-id="${rev.evalId}">
                <img src="${reply.reactionType==='LIKE'
                  ? '/img/community/up-green.png'
                  : '/img/community/up.png'}">
              </button>
              <span>${reply.commentLikeCount - reply.commentDislikeCount}</span>
              <button class="comment-down" type="button" onclick="commentDislike(this)"
                      data-id="${reply.commentId}"
                      data-parent-id="${rev.evalId}">
                <img src="${reply.reactionType==='DISLIKE'
                  ? '/img/community/down-red.png'
                  : '/img/community/down.png'}">
              </button>
            </div>
            <div class="body-div">
              <div class="nick-date-div">
                <img src="${reply.writerIconImgUrl || ''}">
                <span class="nick-span">${reply.writerNickname}</span>
                <span class="date-span">${reply.timeAgo}</span>
                ${reply.isCommentMine
        ? `<button type="button" data-id="${reply.commentId}"
                             class="delete-button btn btn-primary"
                             onclick="deleteComment(this)"
                             data-bs-toggle="modal"
                             data-bs-target="#exampleModal">
                       삭제
                     </button>`
        : ''}
              </div>
              <div class="real-comment-container">
                <span>${reply.commentBody}</span>
              </div>
            </div>
          </li>
        `).join('')}
      </ul>
    `;

    commentsUl.appendChild(li);
  });
}

// 별 이미지 가져오기
function getStarImgUrl(score) {
  const code = String(Math.round(score * 10)).padStart(2, '0');
  return `https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star${code}.svg`;
}

// 좋아요/싫어요 핸들러
async function commentLike(btn) {
  await handleReaction(btn, 'LIKE');
}

async function commentDislike(btn) {
  await handleReaction(btn, 'DISLIKE');
}

async function handleReaction(btn, reaction) {
  const id       = btn.dataset.id;
  const parentId = btn.dataset.parentId;
  // 평가인지 대댓글인지 구분
  const isEval = id === parentId;
  // URL 결정
  const url = isEval
      ? `/web/api/restaurants/evaluations/${id}/${reaction}`
      : `/web/api/restaurants/comments/${id}/${reaction}`;

  try {
    const res = await fetch(url, { method: 'POST', headers: {[csrfHeader]: csrfToken} });
    if (res.redirected) {
      // 로그인 화면으로 넘어간 상황
      window.location.href = res.url;  // 직접 페이지 이동
      return;
    }
    if (!res.ok) throw new Error(res.status);
    const json = await res.json();
    // 서버가 돌려주는 응답 레코드
    // { evaluationId or evalCommentId, reaction, likeCount, dislikeCount }
    const likeCount    = json.likeCount;
    const dislikeCount = json.dislikeCount;
    const newReact     = json.reaction; // "LIKE", "DISLIKE" 또는 null

    // 같은 .like-div 내부의 버튼·카운트 요소 가져오기
    const likeBtn    = btn.parentElement.querySelector('.comment-up');
    const dislikeBtn = btn.parentElement.querySelector('.comment-down');
    const countSpan  = btn.parentElement.querySelector('span');

    // 1) 카운트 갱신
    countSpan.textContent = likeCount - dislikeCount;

    // 2) 아이콘 갱신
    const upImg   = likeBtn.querySelector('img');
    const downImg = dislikeBtn.querySelector('img');

    upImg.src   = newReact === 'LIKE'
        ? '/img/community/up-green.png'
        : '/img/community/up.png';

    downImg.src = newReact === 'DISLIKE'
        ? '/img/community/down-red.png'
        : '/img/community/down.png';

  } catch (e) {
    console.error('Reaction error:', e);
  }
}
