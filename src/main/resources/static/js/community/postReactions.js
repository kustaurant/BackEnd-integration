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

        likeButton.addEventListener('click', (e) => {
            const postId = e.currentTarget.dataset.postId;
            this.togglePostLike(postId);
        });
    }

    bindDislikeButton() {
        const dislikeButton = document.getElementById("dislikeButton");
        if (!dislikeButton) return;

        dislikeButton.addEventListener('click', (e) => {
            e.preventDefault();
            const postId = e.currentTarget.dataset.postId;
            this.togglePostDislike(postId);
        });
    }

    bindScrapButton() {
        const scrapButton = document.getElementById("scrap");
        if (!scrapButton) return;

        scrapButton.addEventListener('click', (e) => {
            e.preventDefault();
            const postId = e.currentTarget.dataset.postId;
            this.togglePostScrap(postId);
        });
    }

    async togglePostLike(postId) {
        try {
            const response = await fetch(`/api/posts/${postId}/like`, {
                method: 'POST',
                headers: Utils.getCsrfHeaders()
            });

            if (Utils.handleLoginRedirect(response)) return;

            const data = await response.json();
            this.updateLikeDislikeUI(data);
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async togglePostDislike(postId) {
        try {
            const response = await fetch(`/api/posts/${postId}/dislike`, {
                method: 'POST',
                headers: Utils.getCsrfHeaders()
            });

            if (Utils.handleLoginRedirect(response)) return;

            const data = await response.json();
            this.updateLikeDislikeUI(data);
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async togglePostScrap(postId) {
        try {
            const response = await fetch(`/api/posts/${postId}/scrap`, {
                method: 'POST',
                headers: Utils.getCsrfHeaders()
            });

            if (Utils.handleLoginRedirect(response)) return;

            const data = await response.json();
            this.updateScrapUI(data);
        } catch (error) {
            console.error('Error:', error);
        }
    }

    updateLikeDislikeUI(data) {
        // 숫자 업데이트
        const likeSpan = document.querySelector("#likeButton > span");
        const dislikeSpan = document.querySelector("#dislikeButton > span");
        const recommendSpan = document.querySelector("#postRecommendCount");

        if (likeSpan) likeSpan.textContent = data.likeCount;
        if (dislikeSpan) dislikeSpan.textContent = data.dislikeCount === 0 ? 0 : "-" + data.dislikeCount;
        if (recommendSpan) recommendSpan.textContent = "추천 " + (data.likeCount - data.dislikeCount);

        // reactionType 기반 이미지 처리
        const likeButtonImage = document.querySelector('#likeButton img');
        const dislikeButtonImage = document.querySelector('#dislikeButton img');
        
        if (likeButtonImage && dislikeButtonImage) {
            if (data.reactionType === 'LIKE') {
                // 사용자가 좋아요를 누른 상태
                likeButtonImage.src = '/img/community/up-green.png';
                dislikeButtonImage.src = '/img/community/down.png';
            } else if (data.reactionType === 'DISLIKE') {
                // 사용자가 싫어요를 누른 상태
                likeButtonImage.src = '/img/community/up.png';
                dislikeButtonImage.src = '/img/community/down-red.png';
            } else {
                // 반응 없음 (null)
                likeButtonImage.src = '/img/community/up.png';
                dislikeButtonImage.src = '/img/community/down.png';
            }
        }
    }

    updateScrapUI(data) {
        const scrapImage = document.querySelector('#scrap img');
        const scrapCountSpan = document.querySelector('#scrap span');
        const postScrapCountSpan = document.querySelector('#postScrapCount');
        
        if (scrapImage) {
            scrapImage.src = data.status === 'SCRAPPED' 
                ? '/img/community/scrap-green.png' 
                : '/img/community/scrap.png';
        }
        
        if (scrapCountSpan) scrapCountSpan.textContent = data.scrapCount;
        if (postScrapCountSpan) postScrapCountSpan.textContent = '스크랩 ' + data.scrapCount;
    }
}