/**
 * 커뮤니티 게시글 작성 페이지
 */
document.addEventListener('DOMContentLoaded', function () {
    const editorManager = new EditorManager();
    editorManager.initializeEditor();

    document.getElementById('back-button')?.addEventListener('click', () => window.history.back());

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

        const title = form.querySelector('.title')?.value.trim();
        if (!title) return Utils.showAlert('제목을 입력해주세요.');

        const content = tinymce.get('tiny-editor')?.getContent();
        if (!content) return Utils.showAlert('내용을 입력해주세요.');

        submitPost({ title, category, content });
    }

    async function submitPost(postData) {
        try {
            const res = await fetch('/api/posts', {
                method: 'POST',
                headers: Utils.apiHeaders({ json: true }),
                body: JSON.stringify(postData)
            });

            if (res.status === 401) { Utils.redirectToLogin(); return; }

            if (!res.ok) {
                const err = await Utils.safeJson(res);
                Utils.showAlert(err?.message || `작성 실패 (${res.status})`);
                return;
            }

            // 서버가 생성된 글 ID를 JSON으로 준다면:
            // const data = await Utils.safeJson(res);
            // if (data?.postId) return Utils.redirectTo(`/community/${data.postId}`);

            Utils.redirectTo('/community');
        } catch (error) {
            console.error('submitPost error:', error);
            Utils.showAlert('네트워크 오류가 발생했습니다.');
            Utils.redirectTo('/community');
        }
    }
});
