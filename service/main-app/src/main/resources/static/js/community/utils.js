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

    static apiHeaders({ json = false } = {}) {
        if (!this.csrfToken || !this.csrfHeader) this.init();

        const headers = {
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
        };

        if (this.csrfHeader && this.csrfToken) {
            headers[this.csrfHeader] = this.csrfToken;
        }

        if (json) headers['Content-Type'] = 'application/json';
        return headers;
    }

    static async safeJson(res) {
        const ct = res.headers.get('content-type') || '';
        if (ct.includes('application/json')) { try { return await res.json(); } catch {} }
        try { return JSON.parse(await res.text()); } catch { return null; }
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

    static redirectToLogin() {
        const currentUrl = encodeURIComponent(window.location.href);
        window.location.href = `/user/login?redirect=${currentUrl}`;
    }
}

// 초기화
Utils.init();