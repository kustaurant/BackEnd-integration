/**
 * 댓글/대댓글 관리
 */
class CommentManager {
    constructor() {
        this.init();
    }

    init() {
        this.bindLoginCheck();
        this.bindCommentSubmit();
        this.bindReplyButtons();
        this.bindReplySubmit();
    }

    bindLoginCheck() {
        const commentInput = document.querySelector(".comment-content");
        if (!commentInput) return;

        commentInput.addEventListener("focus", async () => {
            try {
                const res = await fetch("/api/login/comment-write", {
                    method: 'GET',
                    headers: Utils.apiHeaders()
                });
                if (res.status === 401) Utils.redirectToLogin();
            } catch (err) {
                console.error('login-check error:', err);
            }
        });
    }

    bindCommentSubmit() {
        const submitBtn = document.querySelector('.post-footer .comment-submit-btn');
        if (!submitBtn) return;

        submitBtn.addEventListener('click', (e) => {
            e.preventDefault();
            this.submitComment();
        });
    }

    bindReplyButtons() {
        document.querySelectorAll('.reply').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                this.toggleReplyForm(e.currentTarget);
            });
        });
    }

    bindReplySubmit() {
        const commentUl = document.querySelector('.comment-ul');
        if (!commentUl) return;

        commentUl.addEventListener('click', (e) => {
            const btn = e.target.closest('.comment-submit-btn');
            if (!btn) return;
            e.preventDefault();
            this.submitReply(btn);
        });
    }

    async submitComment() {
        const submitBtn = document.querySelector('.post-footer .comment-submit-btn');
        const contentInput = document.querySelector('.post-footer .comment-content');
        const content = contentInput?.value.trim();

        if (!content) return Utils.showAlert('댓글 내용을 입력해주세요.');

        const postId = Utils.getPostIdFromUrl();
        const body = JSON.stringify({ content, parentCommentId: null });

        try {
            submitBtn?.setAttribute('disabled', 'true');

            const res = await fetch(`/api/posts/${postId}/comments`, {
                method: 'POST',
                headers: Utils.apiHeaders({ json: true }),
                body
            });

            if (res.status === 401) { Utils.redirectToLogin(); return; }

            if (!res.ok) {
                const err = await Utils.safeJson(res);
                Utils.showAlert(err?.message || '댓글 작성에 실패했습니다.');
                return;
            }

            Utils.reloadPage();
        } catch (error) {
            console.error('submitComment error:', error);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
        } finally {
            submitBtn?.removeAttribute('disabled');
        }
    }

    async submitReply(submitButton) {
        const form = submitButton.closest('.comment-write');
        const input = form?.querySelector('.comment-content');
        const content = input?.value.trim();

        if (!content) return Utils.showAlert('댓글 내용을 입력해주세요.');

        const postId = Utils.getPostIdFromUrl();
        const parentCommentId = parseInt(form?.getAttribute('data-comment-id') || '0', 10) || null;
        const body = JSON.stringify({ content, parentCommentId });

        try {
            submitButton.setAttribute('disabled', 'true');

            const res = await fetch(`/api/posts/${postId}/comments`, {
                method: 'POST',
                headers: Utils.apiHeaders({ json: true }),
                body
            });

            if (res.status === 401) { Utils.redirectToLogin(); return; }

            if (!res.ok) {
                const err = await Utils.safeJson(res);
                Utils.showAlert(err?.message || '댓글 작성에 실패했습니다.');
                return;
            }

            Utils.reloadPage();
        } catch (error) {
            console.error('submitReply error:', error);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
        } finally {
            submitButton.removeAttribute('disabled');
        }
    }

    toggleReplyForm(replyButton) {
        const parentComment = replyButton.closest('.comment-li');
        if (!parentComment) return;

        // 기존 임시 폼 모두 제거
        document.querySelectorAll('.comment-ul .comment-write[data-reply-form="true"]').forEach(f => f.remove());

        // 바로 뒤에 동일 폼이 있으면 접기
        const next = parentComment.nextElementSibling;
        if (next?.classList.contains('comment-write') && next?.dataset.replyForm === 'true') {
            next.remove();
            return;
        }

        // 원본 폼 복제
        const originalForm = document.querySelector('.comment-write');
        if (!originalForm) return;

        const form = originalForm.cloneNode(true);
        form.dataset.replyForm = 'true';
        form.setAttribute('data-comment-id', parentComment.getAttribute('data-id') || '');

        // 버튼 보장
        const submitButton = form.querySelector('.comment-submit-btn') ||
            form.querySelector('button[type="submit"]');
        if (submitButton) {
            submitButton.type = 'button';
            submitButton.classList.add('comment-submit-btn');
        }

        parentComment.parentNode.insertBefore(form, parentComment.nextSibling);
        form.querySelector('textarea')?.focus();
    }

    async deleteComment(commentId) {
        try {
            const res = await fetch(`/api/comments/${commentId}`, {
                method: 'DELETE',
                headers: Utils.apiHeaders()
            });

            if (res.status === 401) { Utils.redirectToLogin(); return; }

            if (res.status === 204) {
                // 바로 제거
                document.querySelector(`[data-id="${commentId}"]`)?.closest('.comment-li')?.remove();
                return;
            }

            if (!res.ok) {
                const err = await Utils.safeJson(res);
                Utils.showAlert(err?.message || '댓글 삭제에 실패했습니다.');
                return;
            }

            const data = await Utils.safeJson(res) || {};

            // 삭제 → PENDING 전환 UI
            if (data.status === 'PENDING' && data.id) {
                this.updateCommentToPending(data.id);
            }

            // removeIds에 포함된 댓글 제거
            (data.removeIds || []).forEach(id => {
                document.querySelector(`[data-id="${id}"]`)?.closest('.comment-li')?.remove();
            });

            // 총 댓글 수 반영
            if (typeof data.postTotalCommentCount === 'number') {
                this.setCommentCount(data.postTotalCommentCount);
            }
        } catch (error) {
            console.error('deleteComment error:', error);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
        }
    }

    updateCommentToPending(commentId) {
        const commentElement = document.querySelector(`[data-id="${commentId}"]`)?.closest('.comment-li');
        if (!commentElement) return;

        const commentDiv = commentElement.querySelector('.comment-div');
        if (!commentDiv) return;

        const pendingCommentHtml = `
            <div class="comment-content">
                <div class="comment-body">
                    <div>
                        <p>삭제된 댓글입니다.</p>
                    </div>
                </div>
            </div>
        `;
        commentDiv.innerHTML = pendingCommentHtml;
    }

    setCommentCount(totalCount) {
        const commentCountSpan = document.querySelector("#commentCount");
        if (!commentCountSpan) return;
        commentCountSpan.innerText = '댓글 ' + (Number(totalCount) || 0);
    }

}