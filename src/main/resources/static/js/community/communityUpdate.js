/**
 * 커뮤니티 게시글 수정 페이지
 */
document.addEventListener('DOMContentLoaded', function () {
    const editorManager = new EditorManager();
    editorManager.initializeEditor();

    const backButton = document.getElementById('back-button');
    backButton?.addEventListener('click', () => window.history.back());

    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', (event) => {
            event.preventDefault();
            handleFormSubmit(form);
        });
    }

    function handleFormSubmit(form) {
        const category = form.querySelector('select[name="postCategory"]')?.value;
        if (!category) return Utils.showAlert('카테고리를 선택해주세요.');

        const title = form.querySelector('input[name="title"]')?.value.trim();
        if (!title) return Utils.showAlert('제목을 입력해주세요.');

        const content = tinymce.get('tiny-editor')?.getContent();
        if (!content) return Utils.showAlert('내용을 입력해주세요.');

        const postId = form.dataset.id;
        if (!postId) return Utils.showAlert('게시글 ID를 찾을 수 없습니다.');

        const postData = { title, category, content };
        updatePost(postId, postData);
    }

    async function updatePost(postId, postData) {
        try {
            const res = await fetch(`/api/posts/${postId}`, {
                method: 'PUT',
                headers: Utils.apiHeaders({ json: true }),
                body: JSON.stringify(postData)
            });

            if (res.status === 401) { Utils.redirectToLogin(); return; }

            if (!res.ok) {
                const err = await Utils.safeJson(res);
                Utils.showAlert(err?.message || `수정 실패 (${res.status})`);
                return;
            }

            Utils.redirectTo(`/community/${postId}`);
        } catch (error) {
            console.error('updatePost error:', error);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
        }
    }
});
