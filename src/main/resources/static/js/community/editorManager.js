/**
 * TinyMCE 에디터 관리 공통 모듈
 */
class EditorManager {
    constructor() {
        this.defaultConfig = null;
    }

    init() {
        const textareaHeight = document.querySelector(".post-body-inner")?.clientHeight || 400;
        
        this.defaultConfig = {
            selector: "#tiny-editor",
            content_style: `
                p { margin: 0; }
                img { max-width: 100%; max-height: 400px; }
            `,
            min_height: textareaHeight,
            max_height: textareaHeight,
            statusbar: false,
            menubar: false,
            paste_as_text: true,
            fullpage_default_font_size: "14px",
            branding: false,
            plugins: "autolink code link autoresize paste contextmenu image preview",
            toolbar: "custom_image | undo redo | fontsizeselect | forecolor | bold italic strikethrough underline | alignleft aligncenter alignright alignjustify",
            fontsize_formats: '10px 12px 14px 16px 18px 20px 22px 24px 28px 32px 36px 48px',
            setup: (editor) => this.setupEditor(editor)
        };
    }

    setupEditor(editor) {
        // 사용자 정의 버튼 (이미지 업로드)
        editor.ui.registry.addButton('custom_image', {
            icon: 'image',
            tooltip: 'insert Image',
            onAction: () => {
                this.handleImageUpload();
            }
        });
    }

    handleImageUpload() {
        this.documentUpload({
            multiple: false,
            accept: '.jpg, .png',
            callback: (data, response) => {
                if (response && response.ok) {
                    const editor = tinymce.activeEditor;
                    if (editor) {
                        editor.insertContent(`<img src='${data.thumbnailPath}' data-mce-src='${data.thumbnailPath}' data-originalFileName='${data.orgFilename}'>`);
                    }
                } else {
                    Utils.showAlert('이미지 업로드에 실패했습니다.');
                }
            }
        });
    }

    documentUpload(options) {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', options.accept || 'image/jpeg, image/png, image/gif, image/bmp, image/tiff, image/webp');

        input.onchange = async function() {
            const files = this.files;
            const formData = new FormData();
            formData.append('image', files[0]);

            try {
                const response = await fetch('/api/images', {
                    method: 'POST',
                    headers: Utils.getCsrfHeaders(),
                    body: formData
                });

                if (typeof options.callback === 'function') {
                    if (response.ok) {
                        const data = await response.json();
                        options.callback(data, response);
                    } else {
                        options.callback(null, response);
                    }
                }
            } catch (error) {
                console.error('Error:', error);
                if (typeof options.callback === 'function') {
                    options.callback(null, null);
                }
            }
        };

        input.click();
    }

    initializeEditor(customConfig = {}) {
        this.init();
        const config = { ...this.defaultConfig, ...customConfig };
        
        if (typeof tinymce !== 'undefined') {
            return tinymce.init(config);
        } else {
            console.error('TinyMCE not loaded');
        }
    }
}