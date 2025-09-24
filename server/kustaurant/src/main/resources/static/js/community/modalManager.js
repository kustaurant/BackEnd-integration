/**
 * 모달 관리 (게시글, 댓글 삭제 확인)
 */
class ModalManager {
    constructor(commentManager) {
        this.commentManager = commentManager;
        this.init();
    }

    init() {
        this.bindModalTriggers();
    }

    bindModalTriggers() {
        document.querySelectorAll('[data-bs-toggle="modal"]').forEach(button => {
            button.addEventListener('click', (e) => {
                const itemId = e.currentTarget.getAttribute('data-id');
                const itemType = e.currentTarget.getAttribute('data-type');
                this.setupModalActions(itemId, itemType);
            });
        });
    }

    setupModalActions(itemId, itemType) {
        if (itemType === 'post') {
            this.setupPostDeleteModal(itemId);
        } else if (itemType === 'comment') {
            this.setupCommentDeleteModal(itemId);
        }
    }

    setupPostDeleteModal(postId) {
        const postDeleteAgreeButton = document.getElementById('postDeleteAgreeButton');
        if (!postDeleteAgreeButton) return;

        postDeleteAgreeButton.setAttribute('data-id', postId);
        postDeleteAgreeButton.onclick = () => this.deletePost(postId);
    }

    setupCommentDeleteModal(commentId) {
        const commentDeleteAgreeButton = document.getElementById('commentDeleteAgreeButton');
        if (!commentDeleteAgreeButton) return;

        commentDeleteAgreeButton.setAttribute('data-id', commentId);
        commentDeleteAgreeButton.onclick = () => this.deleteComment(commentId);
    }

    async deletePost(postId) {
        try {
            const response = await fetch(`/api/posts/${postId}`, {
                method: 'DELETE',
                headers: Utils.apiHeaders()
            });

            if (response.ok) {
                Utils.redirectTo("/community");
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async deleteComment(commentId) {
        if (this.commentManager) {
            await this.commentManager.deleteComment(commentId);
        }
    }
}