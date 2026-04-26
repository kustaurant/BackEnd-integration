async function loadHtmlFragment(url) {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`HTML fragment load failed: ${url}`);

    return res.text();
}

async function appendModalIfNotExists(modalId, url) {
    if (document.getElementById(modalId)) return;
    const html = await loadHtmlFragment(url);
    document.body.insertAdjacentHTML('beforeend', html);
}

async function preloadAdminModals() {
    await appendModalIfNotExists(
        'naver-place-crawl-modal',
        '/admin/naver-place-crawl-modal.html'
    );

    await appendModalIfNotExists(
        'naver-place-sync-modal',
        '/admin/naver-place-sync-modal.html'
    );

    await appendModalIfNotExists(
        'crawl-modal',
        '/admin/crawl-modal.html'
    );

    await appendModalIfNotExists(
        'restaurant-sync-run-modal',
        '/admin/restaurant-sync-run-modal.html'
    );

    await appendModalIfNotExists(
        'partnership-edit-modal',
        '/admin/partnership-edit-modal.html'
    );

    await appendModalIfNotExists(
        'delete-partnership-modal',
        '/admin/partnership-delete-modal.html'
    );

}
