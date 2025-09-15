/**
 * 게시글 반응(좋아요, 싫어요, 스크랩) 관리
 */
class PostReactions {
    constructor() {
        this.init();
    }

    init() {
        this.bindLikeButton();
        this.bindDislikeButton();
        this.bindScrapButton();
    }

    bindLikeButton() {
        const likeButton = document.getElementById("likeButton");
        if (!likeButton) return;

        likeButton.addEventListener('click', async(e) => {
            const postId = e.currentTarget.dataset.postId;
            const current = likeButton.dataset.reaction || 'NONE';
            const target = (current === 'LIKE') ? null : 'LIKE';
            await this.setReaction(postId, target);
        });
    }

    bindDislikeButton() {
        const dislikeButton = document.getElementById("dislikeButton");
        if (!dislikeButton) return;

        dislikeButton.addEventListener('click', async (e) => {
            e.preventDefault();
            const postId = e.currentTarget.dataset.postId;
            const current = dislikeButton.dataset.reaction || 'NONE';
            const target = (current === 'DISLIKE') ? null : 'DISLIKE';
            await this.setReaction(postId, target);
        });
    }

    bindScrapButton() {
        const scrapButton = document.getElementById("scrap");
        if (!scrapButton) return;
        scrapButton.addEventListener('click', async (e) => {
            e.preventDefault();
            const postId = e.currentTarget.dataset.postId;
            const now = (scrapButton.dataset.scrapped === 'true');
            await this.setScrap(postId, !now);
        });
    }

    async setReaction(postId, target /* 'LIKE' | 'DISLIKE' | null */) {
        try {
            const qs = (target ? `?reaction=${encodeURIComponent(target)}` : '');
            const res = await fetch(`/api/posts/${postId}/reaction${qs}`, {
                method: 'PUT',
                headers: Utils.apiHeaders()
            });
            if (res.status === 401) { Utils.redirectToLogin(); return; }
            if (!res.ok) {
                let msg=''; try { msg=(await res.json()).message; } catch {}
                Utils.showAlert(msg || '반응 설정에 실패했습니다.'); return;
            }
            const data = await res.json();
            this.updateLikeDislikeUI(data);
        } catch (e) {
            console.error(e); Utils.showAlert('네트워크 오류가 발생했습니다.');
        }
    }

    async setScrap(postId, scrapped /* boolean */) {
        try {
            const res = await fetch(`/api/posts/${postId}/scrap?scrapped=${scrapped}`, {
                method: 'PUT',
                headers: Utils.apiHeaders()
            });
            if (res.status === 401) { Utils.redirectToLogin(); return; }
            if (!res.ok) {
                let msg=''; try { msg=(await res.json()).message; } catch {}
                Utils.showAlert(msg || '스크랩 설정에 실패했습니다.'); return;
            }
            const data = await res.json();
            this.updateScrapUI(data);
        } catch (e) {
            console.error(e); Utils.showAlert('네트워크 오류가 발생했습니다.');
        }
    }

    updateLikeDislikeUI(data) {
        // 숫자 업데이트
        const likeSpan = document.querySelector("#likeButton > span");
        const dislikeSpan = document.querySelector("#dislikeButton > span");
        const recommendSpan = document.querySelector("#postRecommendCount");
        if (likeSpan) likeSpan.textContent = data.likeCount;
        if (dislikeSpan) {
            const d = Number(data.dislikeCount ?? 0);
            dislikeSpan.textContent = d > 0 ? `-${d}` : '0';
        }
        if (recommendSpan) recommendSpan.textContent = "추천 " + (data.likeCount - data.dislikeCount);

        // 버튼 상태 dataset에 반영 (현재 반응 기록)
        const likeButton = document.getElementById("likeButton");
        const dislikeButton = document.getElementById("dislikeButton");
        const state = data.reactionType || 'NONE'; // 'LIKE' | 'DISLIKE' | null → 'NONE'
        if (likeButton) likeButton.dataset.reaction = state;
        if (dislikeButton) dislikeButton.dataset.reaction = state;

        // 아이콘 업데이트
        const likeButtonImage = document.querySelector('#likeButton img');
        const dislikeButtonImage = document.querySelector('#dislikeButton img');
        if (likeButtonImage && dislikeButtonImage) {
            if (state === 'LIKE') {
                likeButtonImage.src = '/img/community/up-green.png';
                dislikeButtonImage.src = '/img/community/down.png';
            } else if (state === 'DISLIKE') {
                likeButtonImage.src = '/img/community/up.png';
                dislikeButtonImage.src = '/img/community/down-red.png';
            } else {
                likeButtonImage.src = '/img/community/up.png';
                dislikeButtonImage.src = '/img/community/down.png';
            }
        }
    }

    updateScrapUI(data) {
        const scrapImage = document.querySelector('#scrap img');
        const scrapCountSpan = document.querySelector('#scrap span');
        const scrapButton = document.getElementById('scrap');

        if (scrapButton) scrapButton.dataset.scrapped = String(!!data.isScrapped);

        if (scrapImage) {
            scrapImage.src = data.isScrapped
                ? '/img/community/scrap-green.png'
                : '/img/community/scrap.png';
        }
        if (scrapCountSpan) {
            scrapCountSpan.textContent = data.postScrapCount;
        }
    }
}