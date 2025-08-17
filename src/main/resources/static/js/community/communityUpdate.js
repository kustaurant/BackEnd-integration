/**
 * 커뮤니티 게시글 수정 페이지
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
        console.log("submit 진입");

        // 입력 검증
        const category = form.querySelector('select[name="postCategory"]').value;
        if (!category) {
            Utils.showAlert('카테고리를 선택해주세요.');
            return;
        }

        const title = form.querySelector('input[name="title"]').value.trim();
        if (!title) {
            Utils.showAlert('제목을 입력해주세요.');
            return;
        }

        const content = tinymce.get('tiny-editor').getContent();
        if (!content) {
            Utils.showAlert('내용을 입력해주세요.');
            return;
        }

        const postId = form.dataset.id;
        if (!postId) {
            Utils.showAlert('게시글 ID를 찾을 수 없습니다.');
            return;
        }

        // 서버로 전송
        const postData = {
            title: title,
            category: category,
            content: content
        };

        updatePost(postId, postData);
    }

    async function updatePost(postId, postData) {
        try {
            const response = await fetch(`/api/posts/${postId}`, {
                method: 'PUT',
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
        }
    }

});