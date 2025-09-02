document.addEventListener("DOMContentLoaded", function () {
    const btn_search = document.getElementById("postSearch");
    if (!btn_search) return;

    btn_search.addEventListener('click', function () {
        const kwEl = document.getElementById('kw');
        const inputEl = document.getElementById('search_kw');
        const pageEl = document.getElementById('page');
        const formEl = document.getElementById('searchForm');

        if (!kwEl || !inputEl || !pageEl || !formEl) return;

        kwEl.value = inputEl.value;
        pageEl.value = 0;
        formEl.submit();
    });
});