/**
 * 댓글 반응(좋아요, 싫어요) 관리 - 이벤트 위임 + 401 처리 + 안전 파싱
 */
class CommentReactions {
    constructor() {
        this.onClick = this.onClick.bind(this);
        this.init();
    }

    init() {
        this.container = document.querySelector('.comment-ul') || document;
        this.container.addEventListener('click', this.onClick, false);
    }

    onClick(e) {
        const likeBtn = e.target.closest('.comment-up');
        const dislikeBtn = e.target.closest('.comment-down');
        if (!likeBtn && !dislikeBtn) return;

        e.preventDefault();
        const btn = likeBtn || dislikeBtn;

        const commentId = btn.getAttribute('data-id') || btn.dataset.id;
        if (!commentId) return;

        // 반응 영역 컨테이너(가장 가까운 공통 부모)
        const box =
            btn.closest('.comment-reaction') ||
            btn.closest('.comment-actions') ||
            btn.parentNode;

        // 중복 요청 방지
        if (btn.dataset.loading === '1') return;
        btn.dataset.loading = '1';

        // 현재 상태 읽고 목표 상태 계산
        const current = (box?.dataset.reaction || 'NONE').toUpperCase(); // 'LIKE' | 'DISLIKE' | 'NONE'
        const kind = likeBtn ? 'LIKE' : 'DISLIKE';
        const target = (current === kind) ? null : kind;

        this.setCommentReaction(commentId, box, btn, target);
    }

    async setCommentReaction(commentId, box, buttonElement, target /* 'LIKE'|'DISLIKE'|null */) {
        try {
            const qs = target ? `?reaction=${encodeURIComponent(target)}` : '';
            const res = await fetch(`/api/comments/${commentId}/reaction${qs}`, {
                method: 'PUT',
                headers: Utils.apiHeaders()
            });

            if (res.status === 401) { Utils.redirectToLogin(); return; }
            if (!res.ok) {
                const err = await Utils.safeJson(res);
                console.error('Comment reaction failed:', res.status, err?.message);
                Utils.showAlert(err?.message || '처리에 실패했습니다.');
                return;
            }

            const data = await Utils.safeJson(res) || {};
            this.updateCommentReactionUI(box, data);
        } catch (err) {
            console.error('setCommentReaction error:', err);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
        } finally {
            delete buttonElement.dataset.loading;
        }
    }

    updateCommentReactionUI(box, data) {
        // 카운트
        const likeCount = Number(data.likeCount ?? 0);
        const dislikeCount = Number(data.dislikeCount ?? 0);
        const netLikes = likeCount - dislikeCount;

        const totalLikeCountEl = box?.querySelector('.totalLikeCount');
        if (totalLikeCountEl) totalLikeCountEl.textContent = String(netLikes);

        // 현재 상태 기록(다음 클릭 때 기준)
        const state = data.reactionType || 'NONE'; // 'LIKE' | 'DISLIKE' | null
        if (box) box.dataset.reaction = state || 'NONE';

        // 아이콘 갱신
        const likeImg = box?.querySelector('.comment-up img');
        const dislikeImg = box?.querySelector('.comment-down img');
        this.updateReactionImages(state, likeImg, dislikeImg);
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
            likeImage.src = '/img/community/up.png';
            dislikeImage.src = '/img/community/down.png';
        }
    }
}
