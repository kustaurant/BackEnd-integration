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

function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
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
            <span class="nick-span">${escapeHtml(rev.writerNickname)}</span>
            <span class="date-span">${rev.timeAgo}</span>
            <button type="button" data-id="${rev.evalId}"
                         class="delete-button btn btn-primary"
                         onclick="addEvalComment(this)">
                   댓글 달기
                 </button>
          </div>
          ${rev.evalBody || rev.evalImgUrl ?
            `<div class="real-comment-container">
              ${rev.evalImgUrl
            ? `<img src="${rev.evalImgUrl}"/>`
            : ''}
              <span>${escapeHtml(rev.evalBody)}</span>
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
                <span class="nick-span">${escapeHtml(reply.writerNickname)}</span>
                <span class="date-span">${reply.timeAgo}</span>
                ${reply.isCommentMine
                  ? `<button type="button" data-id="${reply.commentId}"
                       class="delete-button btn btn-primary"
                       data-bs-toggle="modal"
                       data-bs-target="#exampleModal">
                       삭제
                     </button>`
                  : ''}
              </div>
              <div class="real-comment-container">
                <span>${escapeHtml(reply.commentBody)}</span>
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

// 평가 댓글 달기
// 현재 열려 있는 폼을 닫기
function removeActiveForm() {
  const existing = document.querySelector('.eval-comment-form');
  if (existing) existing.remove();
}

async function checkLoginStatus() {
  try {
    const statusRes = await fetch('/web/api/auth/status', {
      method: 'GET',
      credentials: 'include',
      redirect: 'manual'
    });
    // 리다이렉트 응답이 왔을 땐 로그인 화면으로
    if (statusRes.redirected) {
      window.location.href = statusRes.headers.get('Location') || '/user/login';
      return false;
    }
    return true;
  } catch (e) {
    console.error('로그인 체크 실패:', e);
    window.location.href = '/user/login';
    return false;
  }
}

// 1) “댓글 달기” 클릭 시: 인라인 폼 토글
async function addEvalComment(btn) {
  // 1) 로그인 상태 체크
  const loggedIn = await checkLoginStatus();
  if (!loggedIn) return;
  // 2) 로그인 상태 체크 후 로직 수행
  // 이미 열려 있으면 닫고 종료
  const sameContainer = btn.closest('li').querySelector('.eval-comment-form');
  if (sameContainer) {
    sameContainer.remove();
    return;
  }
  // 다른 폼이 열려 있으면 닫기
  removeActiveForm();

  const li = btn.closest('li');
  const restaurantId  = window.location.pathname.split('/')[2];
  const evalCommentId  = btn.dataset.id;

  // 폼 HTML
  const form = document.createElement('div');
  form.className = 'eval-comment-form';
  form.innerHTML = `
    <textarea class="eval-comment-textarea" rows="3"
      placeholder="댓글을 입력하세요 (1000자 이하)"></textarea>
    <div class="eval-comment-actions" style="margin-top:8px;">
      <button class="eval-comment-submit">등록</button>
      <button class="eval-comment-cancel">취소</button>
    </div>
  `;

  // 버튼에 context 저장
  form.dataset.restaurantId = restaurantId;
  form.dataset.evalCommentId = evalCommentId;

  // 이벤트 연결
  form.querySelector('.eval-comment-submit')
  .addEventListener('click', () => submitInlineComment(form));
  form.querySelector('.eval-comment-cancel')
  .addEventListener('click', () => form.remove());

  // li 끝에 삽입
  li.appendChild(form);

  const textarea = form.querySelector('.eval-comment-textarea');
  textarea.focus();
}

// 2) 폼 내 “등록” 클릭 시
async function submitInlineComment(form) {
  const body = form.querySelector('.eval-comment-textarea').value.trim();

  if (!body) {
    alert('댓글 내용을 입력해주세요.');
    return;
  }
  if (body.length > 1000) {
    alert('댓글은 1000자 이하로 입력해주세요.');
    return;
  }

  const { restaurantId, evalCommentId } = form.dataset;
  const url = `/web/api/restaurants/${restaurantId}/comments/${evalCommentId}`;

  try {
    const res = await fetch(url, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify({ body })
    });
    if (res.redirected) {
      // 로그인 화면으로 넘어간 상황
      window.location.href = res.url;  // 직접 페이지 이동
      return;
    }
    if (res.status === 400) {
      const errors = await res.json();
      const msg = errors.message;
      alert(msg);
      return;
    }
    if (res.status === 401 || res.status === 403) {
      window.location.href = '/user/login';
      return;
    }
    if (!res.ok) throw new Error(res.status);
    // 성공 시 목록 새로고침
    removeActiveForm();
    const sort = document.querySelector('#button1').classList.contains('active')
        ? 'POPULARITY' : 'LATEST';
    loadComments(sort);

  } catch (e) {
    console.error(e);
    alert('댓글 등록 중 오류가 발생했습니다.');
  }
}

// ----------- 평가 댓글 삭제 ---------------
// 모달에 평가 댓글 id 넘기기
document.addEventListener('click', e => {
  const btn = e.target.closest('.delete-button');
  if (!btn) return;

  const modal = document.getElementById('exampleModal');
  modal.dataset.evalCommentId = btn.dataset.id;
});
// 모달에서 삭제 버튼 클릭 시 삭제
document.getElementById('deleteAgreeButton')
.addEventListener('click', async () => {
  const modal = document.getElementById('exampleModal');
  const evalCommentId = modal.dataset.evalCommentId;
  const restaurantId  = window.location.pathname.split('/')[2];
  const url = `/web/api/restaurants/${restaurantId}/comments/${evalCommentId}`;

  try {
    const res = await fetch(url, {
      method: 'DELETE',
      credentials: 'include',
      headers: {
        [csrfHeader]: csrfToken
      }
    });

    if (res.status === 204) {
      // 삭제 성공: 목록 리프레시
      const sort = document.querySelector('#button1').classList.contains('active')
          ? 'POPULARITY' : 'LATEST';
      loadComments(sort);
    } else if (res.redirected) {
      // 로그인 화면으로 넘어간 상황
      window.location.href = res.url;  // 직접 페이지 이동
      return;
    } else if (res.status === 401 || res.status === 403) {
      // 권한 문제 시 로그인으로
      window.location.href = '/user/login';
    } else {
      console.error('삭제 실패', res.status);
      alert('댓글 삭제 중 오류가 발생했습니다.');
    }
  } catch (err) {
    console.error(err);
    alert('댓글 삭제 중 오류가 발생했습니다.');
  }
});