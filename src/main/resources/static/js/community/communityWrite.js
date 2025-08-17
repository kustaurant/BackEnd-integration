/**
 * 커뮤니티 게시글 작성 페이지
 */
document.addEventListener('DOMContentLoaded', function () {
    // 에디터 초기화
    const editorManager = new EditorManager();
    editorManager.initializeEditor();

    // 뒤로 가기 버튼
    const backButton = document.getElementById('back-button');
    if (backButton) {
        backButton.addEventListener('click', () => {
            window.history.back();
        });
    }

    // 폼 제출 처리
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', (event) => {
            event.preventDefault();
            handleFormSubmit(form);
        });
    }

    function handleFormSubmit(form) {
        // 입력 검증
        const category = form.querySelector('select[name="postCategory"]').value;
        if (!category) {
            Utils.showAlert('카테고리를 선택해주세요.');
            return;
        }

        const title = form.querySelector(".title").value.trim();
        if (!title) {
            Utils.showAlert('제목을 입력해주세요.');
            return;
        }

        const content = tinymce.get('tiny-editor').getContent();
        if (!content) {
            Utils.showAlert('내용을 입력해주세요.');
            return;
        }

        // 서버로 전송
        const postData = {
            title: title,
            category: category,
            content: content
        };

        submitPost(postData);
    }

    async function submitPost(postData) {
        try {
            const response = await fetch('/api/posts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...Utils.getCsrfHeaders()
                },
                body: JSON.stringify(postData)
            });

            if (Utils.handleLoginRedirect(response)) return;

            Utils.redirectTo('/community');
        } catch (error) {
            Utils.showAlert(error.message);
            console.error('Error:', error);
            Utils.redirectTo('/community');
        }
    }

});