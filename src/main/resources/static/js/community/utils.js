/**
 * 공통 유틸리티 함수들
 */
class Utils {
    static csrfToken = null;
    static csrfHeader = null;

    static init() {
        this.csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        this.csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    }

    static getCsrfHeaders() {
        if (!this.csrfToken || !this.csrfHeader) {
            this.init();
        }
        return {
            [this.csrfHeader]: this.csrfToken
        };
    }

    static handleLoginRedirect(response) {
        if (response.redirected) {
            window.location.href = "/user/login";
            return true;
        }
        return false;
    }

    static getPostIdFromUrl() {
        return window.location.pathname.split('/').pop();
    }

    static showAlert(message) {
        alert(message);
    }

    static reloadPage() {
        window.location.reload();
    }

    static redirectTo(url) {
        window.location.href = url;
    }
}

// 초기화
Utils.init();