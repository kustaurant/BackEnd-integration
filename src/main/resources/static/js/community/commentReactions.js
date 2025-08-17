/**
 * 댓글 반응(좋아요, 싫어요) 관리
 */
class CommentReactions {
    constructor() {
        this.init();
    }

    init() {
        this.bindCommentLikeButtons();
        this.bindCommentDislikeButtons();
    }

    bindCommentLikeButtons() {
        document.querySelectorAll('.comment-up').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const commentId = e.currentTarget.getAttribute('data-id');
                this.toggleCommentLike(commentId, e.currentTarget);
            });
        });
    }

    bindCommentDislikeButtons() {
        document.querySelectorAll('.comment-down').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const commentId = e.currentTarget.getAttribute('data-id');
                this.toggleCommentDislike(commentId, e.currentTarget);
            });
        });
    }

    async toggleCommentLike(commentId, buttonElement) {
        try {
            const response = await fetch(`/api/comments/${commentId}/like`, {
                method: 'POST',
                headers: Utils.getCsrfHeaders()
            });

            if (Utils.handleLoginRedirect(response)) return;

            const data = await response.json();
            this.updateCommentReactionUI(buttonElement, data);
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async toggleCommentDislike(commentId, buttonElement) {
        try {
            const response = await fetch(`/api/comments/${commentId}/dislike`, {
                method: 'POST',
                headers: Utils.getCsrfHeaders()
            });

            if (Utils.handleLoginRedirect(response)) return;

            const data = await response.json();
            this.updateCommentReactionUI(buttonElement, data);
        } catch (error) {
            console.error('Error:', error);
        }
    }

    updateCommentReactionUI(buttonElement, data) {
        const netLikes = data.likeCount - data.dislikeCount;
        const parentNode = buttonElement.parentNode;
        const totalLikeCountElement = parentNode.querySelector(".totalLikeCount");
        
        if (totalLikeCountElement) {
            totalLikeCountElement.textContent = netLikes;
        }

        const likeButtonImage = parentNode.querySelector('.comment-up img');
        const dislikeButtonImage = parentNode.querySelector('.comment-down img');

        if (likeButtonImage && dislikeButtonImage) {
            this.updateReactionImages(data.reactionType, likeButtonImage, dislikeButtonImage);
        }
    }

    updateReactionImages(reactionType, likeImage, dislikeImage) {
        if (reactionType === 'LIKE') {
            likeImage.src = '/img/community/up-green.png';
            dislikeImage.src = '/img/community/down.png';
        } else if (reactionType === 'DISLIKE') {
            likeImage.src = '/img/community/up.png';
            dislikeImage.src = '/img/community/down-red.png';
        } else {
            // reactionType이 null인 경우 (반응 취소됨)
            likeImage.src = '/img/community/up.png';
            dislikeImage.src = '/img/community/down.png';
        }
    }
}