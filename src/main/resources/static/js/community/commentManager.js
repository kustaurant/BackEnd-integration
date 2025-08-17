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
                const response = await fetch("/api/login/comment-write", { method: 'GET' });
                if (response.ok && response.redirected) {
                    Utils.redirectTo("/user/login");
                    return;
                }
                await response.json();
            } catch (error) {
                console.error('Error:', error);
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
            if (!e.target.matches('.comment-submit-btn')) return;
            
            e.preventDefault();
            this.submitReply(e.target);
        });
    }

    async submitComment() {
        const contentInput = document.querySelector('.post-footer .comment-content');
        const content = contentInput?.value.trim();

        if (!content) {
            Utils.showAlert('댓글 내용을 입력해주세요.');
            return;
        }

        const postId = Utils.getPostIdFromUrl();
        const requestData = {
            content: content,
            parentCommentId: null
        };

        try {
            const response = await fetch(`/api/posts/${postId}/comments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...Utils.getCsrfHeaders()
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                if (Utils.handleLoginRedirect(response)) return;
                Utils.reloadPage();
            }
        } catch (error) {
            Utils.showAlert(error.message);
            console.error('Error:', error);
        }
    }

    async submitReply(submitButton) {
        const commentWriteForm = submitButton.closest('.comment-write');
        const content = commentWriteForm?.querySelector('.comment-content')?.value.trim();
        
        if (!content) {
            Utils.showAlert('댓글 내용을 입력해주세요.');
            return;
        }

        const postId = Utils.getPostIdFromUrl();
        const parentCommentId = commentWriteForm?.getAttribute('data-comment-id');

        const requestData = {
            content: content,
            parentCommentId: parentCommentId ? parseInt(parentCommentId) : null
        };

        try {
            const response = await fetch(`/api/posts/${postId}/comments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...Utils.getCsrfHeaders()
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                if (Utils.handleLoginRedirect(response)) return;
                Utils.reloadPage();
            }
        } catch (error) {
            Utils.showAlert(error.message);
            console.error('Error:', error);
        }
    }

    toggleReplyForm(replyButton) {
        const allForms = document.querySelectorAll('.comment-ul .comment-write');
        const parentComment = replyButton.closest('.comment-li');
        
        // 기존 폼이 있는지 확인
        let existingForm = null;
        allForms.forEach(form => {
            if (parentComment.nextSibling === form) {
                existingForm = form;
            }
        });

        // 기존 폼이 있으면 제거
        if (existingForm) {
            existingForm.remove();
            return;
        }

        // 모든 기존 폼 제거
        allForms.forEach(form => form.remove());

        // 새 댓글 작성 창 생성
        const originalForm = document.querySelector('.comment-write');
        if (!originalForm) return;

        const commentWriteForm = originalForm.cloneNode(true);
        const parentCommentId = parentComment.getAttribute('data-id');
        
        commentWriteForm.setAttribute('data-comment-id', parentCommentId);
        
        // 버튼 타입 변경
        const submitButton = commentWriteForm.querySelector('.comment-submit-btn') || 
                           commentWriteForm.querySelector('button[type="submit"]');
        if (submitButton) {
            submitButton.type = 'button';
            submitButton.className = 'comment-submit-btn';
        }

        parentComment.parentNode.insertBefore(commentWriteForm, parentComment.nextSibling);
        commentWriteForm.querySelector('textarea')?.focus();
    }

    async deleteComment(commentId) {
        try {
            const response = await fetch(`/api/comments/${commentId}`, {
                method: 'DELETE',
                headers: Utils.getCsrfHeaders()
            });

            if (response.ok) {
                const data = await response.json();
                
                // removeIds에 포함된 댓글들을 DOM에서 제거
                if (data.removeIds && data.removeIds.length > 0) {
                    data.removeIds.forEach(id => {
                        const commentElement = document.querySelector(`[data-id="${id}"]`)?.closest('.comment-li');
                        if (commentElement) {
                            commentElement.remove();
                        }
                    });
                }
                
                // 서버에서 제공하는 총 댓글 수로 업데이트
                if (data.postTotalCommentCount !== undefined) {
                    this.setCommentCount(data.postTotalCommentCount);
                }
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    updateCommentCount(delta) {
        const commentCountSpan = document.querySelector("#commentCount");
        if (!commentCountSpan) return;

        const currentCount = parseInt(commentCountSpan.innerText.replace('댓글 ', ''));
        commentCountSpan.innerText = '댓글 ' + (currentCount + delta);
    }

    setCommentCount(totalCount) {
        const commentCountSpan = document.querySelector("#commentCount");
        if (!commentCountSpan) return;

        commentCountSpan.innerText = '댓글 ' + totalCount;
    }
}