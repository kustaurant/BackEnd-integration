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

            extended_valid_elements: "img[class|src|alt|width|height|style|data-mce-src|data-originalFileName]",

            plugins: "autolink code link autoresize paste contextmenu image preview",
            toolbar: "custom_image | undo redo | fontsizeselect | forecolor | bold italic strikethrough underline | alignleft aligncenter alignright alignjustify",
            fontsize_formats: '10px 12px 14px 16px 18px 20px 22px 24px 28px 32px 36px 48px',
            setup: (editor) => this.setupEditor(editor),
            automatic_uploads: false
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
            accept: ".jpg, .png",
            callback: (data, response) => {
                if (response && response.ok) {
                    const editor = tinymce.activeEditor;
                    if (editor && data?.thumbnailPath) {
                        editor.insertContent(
                            `<img src='${data.thumbnailPath}' data-mce-src='${data.thumbnailPath}' data-originalFileName='${data.orgFilename || ""}'>`
                        );
                    }
                } else {
                    if (response && response.status === 401) {
                        Utils.redirectToLogin();
                        return;
                    }
                    Utils.showAlert("이미지 업로드에 실패했습니다.");
                }
            }
        });
    }

    documentUpload(options) {
        const input = document.createElement("input");
        input.type = "file";
        input.accept = options.accept || "image/jpeg, image/png, image/gif, image/bmp, image/tiff, image/webp";

        input.onchange = async () => {
            const file = input.files?.[0];
            if (!file) return;

            const formData = new FormData();
            formData.append("image", file);

            try {
                const response = await fetch("/api/images", {
                    method: "POST",
                    headers: Utils.apiHeaders(), // ← json: false (multipart)
                    body: formData
                });

                if (typeof options.callback === "function") {
                    if (response.status === 401) { options.callback(null, response); return; }
                    if (response.ok) {
                        const data = await Utils.safeJson(response);
                        options.callback(data, response);
                    } else {
                        options.callback(null, response);
                    }
                }
            } catch (error) {
                console.error("image upload error:", error);
                if (typeof options.callback === "function") {
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