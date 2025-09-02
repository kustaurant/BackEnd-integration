/**
 * 댓글 반응(좋아요, 싫어요) 관리 - 이벤트 위임 + 401 처리 + 안전 파싱
 */
class CommentReactions {
    constructor() {
        this.onClick = this.onClick.bind(this);
        this.init();
    }

    init() {
        // 댓글 리스트 컨테이너 기준(없으면 document에 바인딩)
        this.container = document.querySelector('.comment-ul') || document;
        this.container.addEventListener('click', this.onClick, false);
    }

    // 이벤트 위임: .comment-up / .comment-down 클릭 처리
    onClick(e) {
        const likeBtn = e.target.closest('.comment-up');
        const dislikeBtn = e.target.closest('.comment-down');

        if (!likeBtn && !dislikeBtn) return;

        e.preventDefault();

        const btn = likeBtn || dislikeBtn;
        const commentId = btn.getAttribute('data-id') || btn.dataset.id;
        if (!commentId) return;

        if (btn.dataset.loading === '1') return; // 중복 요청 방지
        btn.dataset.loading = '1';

        if (likeBtn) {
            this.toggleReaction(commentId, btn, 'like');
        } else {
            this.toggleReaction(commentId, btn, 'dislike');
        }
    }

    async toggleReaction(commentId, buttonElement, kind /* 'like' | 'dislike' */) {
        try {
            const res = await fetch(`/api/comments/${commentId}/${kind}`, {
                method: 'POST',
                headers: Utils.apiHeaders()
            });

            if (res.status === 401) {
                Utils.redirectToLogin();
                return;
            }

            if (!res.ok) {
                const err = await Utils.safeJson(res);
                console.error('Comment reaction failed:', res.status, err?.message);
                Utils.showAlert(err?.message || '처리에 실패했습니다.');
                return;
            }

            const data = await Utils.safeJson(res) || {};
            this.updateCommentReactionUI(buttonElement, data);
        } catch (err) {
            console.error('toggleReaction error:', err);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
        } finally {
            delete buttonElement.dataset.loading;
        }
    }

    updateCommentReactionUI(buttonElement, data) {
        // 반응 영역 컨테이너 추정 (구조에 맞게 가장 가까운 공통 부모)
        const box =
            buttonElement.closest('.comment-reaction') ||
            buttonElement.closest('.comment-actions') ||
            buttonElement.parentNode;

        // 합산 카운트 갱신
        const likeCount = Number(data.likeCount ?? 0);
        const dislikeCount = Number(data.dislikeCount ?? 0);
        const netLikes = likeCount - dislikeCount;

        const totalLikeCountEl = box?.querySelector('.totalLikeCount');
        if (totalLikeCountEl) totalLikeCountEl.textContent = String(netLikes);

        // 아이콘 스왑
        const likeImg = box?.querySelector('.comment-up img');
        const dislikeImg = box?.querySelector('.comment-down img');
        this.updateReactionImages(data.reactionType, likeImg, dislikeImg);
    }

    updateReactionImages(reactionType, likeImage, dislikeImage) {
        if (!likeImage || !dislikeImage) return;

        if (reactionType === 'LIKE') {
            likeImage.src = '/img/community/up-green.png';
            dislikeImage.src = '/img/community/down.png';
        } else if (reactionType === 'DISLIKE') {
            likeImage.src = '/img/community/up.png';
            dislikeImage.src = '/img/community/down-red.png';
        } else {
            // null: 반응 해제
            likeImage.src = '/img/community/up.png';
            dislikeImage.src = '/img/community/down.png';
        }
    }
}
