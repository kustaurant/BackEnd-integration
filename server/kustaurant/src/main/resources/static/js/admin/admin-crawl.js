let naverPlaceZoneCrawlPollTimer = null;
let activeNaverPlaceZoneCrawlJobId = null;

function stopNaverPlaceZoneCrawlPolling() {
    if (!naverPlaceZoneCrawlPollTimer) return;
    clearTimeout(naverPlaceZoneCrawlPollTimer);
    naverPlaceZoneCrawlPollTimer = null;
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(";").shift();
    return null;
}

function parseJsonResponse(response) {
    return response.text().then(text => {
        let payload = null;
        try {
            payload = text ? JSON.parse(text) : null;
        } catch (ignored) {
        }
        if (!response.ok) {
            const reason = payload?.errors?.[0]?.reason;
            const message = payload?.message || reason || text || `HTTP ${response.status}`;
            throw new Error(message);
        }
        return payload || {};
    });
}

function openCrawlModal() {
    document.getElementById("crawl-modal")?.classList.remove("hidden");
}

function closeCrawlModal() {
    document.getElementById("crawl-modal")?.classList.add("hidden");
}

function openNaverPlaceModal() {
    document.getElementById("naver-place-crawl-modal")?.classList.remove("hidden");
}

function closeNaverPlaceModal() {
    document.getElementById("naver-place-crawl-modal")?.classList.add("hidden");
}

function openNaverPlaceSyncModal() {
    document.getElementById("naver-place-sync-modal")?.classList.remove("hidden");
    resumeNaverPlaceZoneCrawlPollingIfNeeded();
}

function closeNaverPlaceSyncModal() {
    document.getElementById("naver-place-sync-modal")?.classList.add("hidden");
    stopNaverPlaceZoneCrawlPolling();
}

function getSelectedSyncScope() {
    const scopeValue = document.getElementById("naver-place-sync-scope")?.value;
    if (!scopeValue || scopeValue === "ALL") return null;
    return scopeValue;
}

function enforceNaverPlaceIdOnlyInput() {
    const input = document.getElementById("naver-place-url");
    if (!input) return;
    input.addEventListener("input", () => {
        input.value = input.value.replace(/\D+/g, "");
    });
}

function buildNaverPlaceId(input) {
    const placeId = (input || "").trim();
    if (!/^\d+$/.test(placeId)) return null;
    return placeId;
}

async function fetchNaverPlaceRawExistence(placeId) {
    const response = await fetch(`/admin/api/crawl/naver-place/raw/existence/${placeId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    });
    return parseJsonResponse(response);
}

function renderNaverPlaceCrawlResult(data) {
    const resultBox = document.getElementById("naver-place-crawl-result");
    if (!resultBox) return;

    const menus = data.menus || [];
    const menuHtml = menus.length === 0
        ? "<li>메뉴 정보 없음</li>"
        : menus.map(menu => `<li><strong>${menu.menuName || "-"}</strong> <span>${menu.menuPrice || "-"}</span></li>`).join("");

    resultBox.innerHTML = `
        <div class="naver-place-result-summary">
            <div><strong>Raw ID:</strong> ${data.rawId}</div>
            <div><strong>식당명:</strong> ${data.placeName || "-"}</div>
            <div><strong>카테고리:</strong> ${data.category || "-"}</div>
            <div><strong>구역:</strong> ${data.crawlScopeDescription || data.crawlScope || "-"}</div>
            <div><strong>주소:</strong> ${data.restaurantAddress || "-"}</div>
            <div><strong>전화:</strong> ${data.phoneNumber || "-"}</div>
            <div><strong>좌표:</strong> ${data.latitude || "-"}, ${data.longitude || "-"}</div>
            <div><strong>메뉴 수:</strong> ${data.menuCount || 0}</div>
        </div>
        <div class="naver-place-result-menus">
            <strong>메뉴 미리보기</strong>
            <ul>${menuHtml}</ul>
        </div>
    `;
    resultBox.classList.remove("hidden");
}

async function executeNaverPlaceAction(options) {
    const urlInput = document.getElementById("naver-place-url");
    const resultBox = document.getElementById("naver-place-crawl-result");
    const submitBtn = document.getElementById(options.submitButtonId);
    const placeId = buildNaverPlaceId(urlInput?.value);

    if (!placeId) {
        alert("숫자 place ID를 입력해 주세요.");
        return;
    }

    if (options.confirmOnExistingRaw) {
        try {
            const existence = await fetchNaverPlaceRawExistence(placeId);
            if (existence?.exists) {
                const zoneLabel = existence.crawlScopeDescription || existence.crawlScope || "미확인";
                if (!window.confirm(`이미 raw에 존재합니다. (구역: ${zoneLabel}) 계속 진행할까요?`)) return;
            }
        } catch (error) {
            console.warn("existing raw check failed:", error);
        }
    }

    resultBox?.classList.add("hidden");
    if (resultBox) resultBox.innerHTML = "";

    submitBtn.disabled = true;
    submitBtn.textContent = options.runningText;

    try {
        const response = await fetch(options.endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "X-Requested-With": "XMLHttpRequest",
                "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
            },
            body: JSON.stringify({placeId})
        });
        const data = await parseJsonResponse(response);
        renderNaverPlaceCrawlResult(data);
        if (options.reloadRestaurants) loadRestaurants(0);
    } catch (error) {
        console.error(options.errorLogText, error);
        alert(`${options.errorAlertText}: ${error.message}`);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = options.idleText;
    }
}

function runNaverPlaceCrawl() {
    executeNaverPlaceAction({
        endpoint: "/admin/api/crawl/naver-place/raw",
        submitButtonId: "naver-place-crawl-submit-btn",
        runningText: "추가 중...",
        idleText: "URL로 음식점 추가",
        errorLogText: "naver place crawl failed:",
        errorAlertText: "추가 실패",
        reloadRestaurants: true,
        confirmOnExistingRaw: true
    });
}

function runNaverPlaceAnalyze() {
    executeNaverPlaceAction({
        endpoint: "/admin/api/crawl/naver-place/analyze",
        submitButtonId: "naver-place-analyze-submit-btn",
        runningText: "분석 중...",
        idleText: "URL 분석하기",
        errorLogText: "naver place analyze failed:",
        errorAlertText: "분석 실패",
        reloadRestaurants: false,
        confirmOnExistingRaw: false
    });
}

function isZoneCrawlJobFinished(status) {
    return status === "SUCCESS" || status === "FAILED";
}

function renderNaverPlaceZoneCrawlResult(data) {
    const resultBox = document.getElementById("naver-place-sync-result");
    if (!resultBox) return;
    const finalFailedIds = (data.finalFailedPlaceIds || []).join(", ");
    resultBox.innerHTML = `
        <div><strong>상태:</strong> ${data.status || "-"}</div>
        <div><strong>구역:</strong> ${data.crawlScope || "-"}</div>
        <div><strong>현재 단계:</strong> ${data.currentPhase || "-"}</div>
        <div><strong>그리드 진행:</strong> ${(data.processedGridCount || 0)} / ${(data.totalGridCount || 0)}</div>
        <div><strong>발견 식당 수:</strong> ${data.discoveredPlaceCount || 0}</div>
        <div><strong>크롤 시도/성공:</strong> ${(data.attemptedPlaceCount || 0)} / ${(data.crawledSuccessCount || 0)}</div>
        <div><strong>raw 저장 성공/실패:</strong> ${(data.savedRawCount || 0)} / ${(data.saveFailedCount || 0)}</div>
        <div><strong>현재 grid:</strong> ${data.currentGrid || "-"}</div>
        <div><strong>현재 placeId:</strong> ${data.currentPlaceId || "-"}</div>
        ${data.errorMessage ? `<div><strong>에러:</strong> ${data.errorMessage}</div>` : ""}
        <div><strong>최종 실패 수:</strong> ${data.finalFailedCount || 0}</div>
        <div><strong>최종 실패 placeId:</strong> ${finalFailedIds || "-"}</div>
    `;
    resultBox.classList.remove("hidden");
}

function pollNaverPlaceZoneCrawlStatus(jobId, submitBtn) {
    activeNaverPlaceZoneCrawlJobId = jobId;
    fetch(`/admin/api/crawl/naver-place/crawl-zone/jobs/${jobId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    })
        .then(parseJsonResponse)
        .then(data => {
            renderNaverPlaceZoneCrawlResult(data);
            if (isZoneCrawlJobFinished(data.status)) {
                activeNaverPlaceZoneCrawlJobId = null;
                submitBtn.disabled = false;
                submitBtn.textContent = "구역 크롤 시작";
                stopNaverPlaceZoneCrawlPolling();
                return;
            }
            naverPlaceZoneCrawlPollTimer = setTimeout(() => pollNaverPlaceZoneCrawlStatus(jobId, submitBtn), 5000);
        })
        .catch(error => {
            console.error("zone crawl status failed:", error);
            submitBtn.disabled = false;
            submitBtn.textContent = "구역 크롤 시작";
            stopNaverPlaceZoneCrawlPolling();
            alert(`구역 크롤 상태 조회 실패: ${error.message}`);
        });
}

function resumeNaverPlaceZoneCrawlPollingIfNeeded() {
    if (!activeNaverPlaceZoneCrawlJobId) return;
    const submitBtn = document.getElementById("naver-place-sync-submit-btn");
    if (!submitBtn) return;

    submitBtn.disabled = true;
    submitBtn.textContent = "吏꾪뻾 以?..";
    stopNaverPlaceZoneCrawlPolling();
    pollNaverPlaceZoneCrawlStatus(activeNaverPlaceZoneCrawlJobId, submitBtn);
}

function runNaverPlaceZoneCrawl() {
    const crawlScope = getSelectedSyncScope();
    if (!crawlScope) {
        alert("크롤은 전체가 아니라 특정 구역만 선택할 수 있습니다.");
        return;
    }

    const resultBox = document.getElementById("naver-place-sync-result");
    const submitBtn = document.getElementById("naver-place-sync-submit-btn");

    resultBox?.classList.add("hidden");
    if (resultBox) resultBox.innerHTML = "";

    submitBtn.disabled = true;
    submitBtn.textContent = "작업 시작 중...";
    stopNaverPlaceZoneCrawlPolling();

    fetch("/admin/api/crawl/naver-place/crawl-zone/jobs", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        body: JSON.stringify({crawlScope})
    })
        .then(parseJsonResponse)
        .then(data => {
            if (!data.jobId) throw new Error("zone crawl job id missing");
            submitBtn.textContent = "진행 중...";
            pollNaverPlaceZoneCrawlStatus(data.jobId, submitBtn);
        })
        .catch(error => {
            console.error("zone crawl start failed:", error);
            alert(`구역 크롤 시작 실패: ${error.message}`);
            submitBtn.disabled = false;
            submitBtn.textContent = "구역 크롤 시작";
            stopNaverPlaceZoneCrawlPolling();
        });
}

function runNaverPlaceZoneCrawlTest() {
    const crawlScope = getSelectedSyncScope();
    if (!crawlScope) {
        alert("테스트도 특정 구역만 선택할 수 있습니다.");
        return;
    }

    const resultBox = document.getElementById("naver-place-sync-result");
    const submitBtn = document.getElementById("naver-place-sync-test-submit-btn");

    resultBox?.classList.add("hidden");
    if (resultBox) resultBox.innerHTML = "";

    submitBtn.disabled = true;
    submitBtn.textContent = "테스트 실행 중...";

    fetch("/admin/api/crawl/naver-place/crawl-zone/test", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        body: JSON.stringify({crawlScope})
    })
        .then(parseJsonResponse)
        .then(data => {
            renderNaverPlaceZoneCrawlResult(data);
        })
        .catch(error => {
            console.error("zone crawl test failed:", error);
            alert(`테스트 크롤 실패: ${error.message}`);
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = "테스트 크롤";
        });
}

function renderDbSyncResult(data) {
    const resultBox = document.getElementById("naver-place-db-sync-result");
    if (!resultBox) return;
    resultBox.innerHTML = `
        <div><strong>raw 수:</strong> ${data.rawCount || 0}</div>
        <div><strong>운영 수:</strong> ${data.existingCount || 0}</div>
        <div><strong>신규 후보:</strong> ${data.newCandidateCount || 0}</div>
        <div><strong>폐업 후보:</strong> ${data.closedCandidateCount || 0}</div>
        <div><strong>자동 갱신:</strong> ${data.updatedRestaurantCount || 0}</div>
    `;
    resultBox.classList.remove("hidden");
}

function candidateTypeText(type) {
    if (type === "NEW") return "신규 후보";
    if (type === "CLOSED") return "폐업 후보";
    return type;
}

function renderSyncCandidates(candidates) {
    const box = document.getElementById("naver-place-sync-candidates");
    if (!box) return;

    if (!candidates || candidates.length === 0) {
        box.innerHTML = `<div class="sync-candidate-empty">PENDING 후보가 없습니다.</div>`;
        return;
    }

    box.innerHTML = candidates.map(candidate => `
        <div class="sync-candidate-item">
            <div class="sync-candidate-meta">
                <div><strong>${candidateTypeText(candidate.candidateType)}</strong> · placeId: ${candidate.placeId}</div>
                <div>reason: ${candidate.reason || "-"}</div>
                <div>created: ${formatDateTime(candidate.createdAt)}</div>
            </div>
            <div class="sync-candidate-actions">
                <button type="button" data-action="approve" data-id="${candidate.id}">승인</button>
                <button type="button" data-action="reject" data-id="${candidate.id}">반려</button>
            </div>
        </div>
    `).join("");
}

async function fetchSyncCandidates() {
    const response = await fetch("/admin/api/sync/candidates?status=PENDING", {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    });
    const data = await parseJsonResponse(response);
    renderSyncCandidates(Array.isArray(data) ? data : []);
}

async function runDbSync() {
    const scope = getSelectedSyncScope();
    const button = document.getElementById("naver-place-db-sync-submit-btn");
    button.disabled = true;
    button.textContent = "싱크 실행 중...";

    try {
        const response = await fetch("/admin/api/sync/candidates", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "X-Requested-With": "XMLHttpRequest",
                "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
            },
            body: JSON.stringify(scope ? {crawlScope: scope} : {})
        });
        const data = await parseJsonResponse(response);
        renderDbSyncResult(data);
        await fetchSyncCandidates();
        loadRestaurants(0);
    } catch (error) {
        console.error("db sync failed:", error);
        alert(`싱크 실행 실패: ${error.message}`);
    } finally {
        button.disabled = false;
        button.textContent = "raw-운영 싱크 후보 생성";
    }
}

async function handleCandidateAction(action, candidateId) {
    const actionLabel = action === "approve" ? "승인" : "반려";
    if (!window.confirm(`후보를 ${actionLabel}하시겠습니까?`)) return;

    const response = await fetch(`/admin/api/sync/candidates/${candidateId}/${action}`, {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    });
    await parseJsonResponse(response);
    await fetchSyncCandidates();
    loadRestaurants(0);
}

function runInstagramCrawl() {
    const account = document.getElementById("crawl-account").value.trim();
    const target = document.getElementById("crawl-target").value;
    if (!account) {
        alert("인스타 계정을 입력해 주세요.");
        return;
    }
    if (!target) {
        alert("대상을 선택해 주세요.");
        return;
    }

    const submitBtn = document.getElementById("crawl-submit-btn");
    submitBtn.disabled = true;
    submitBtn.textContent = "실행 중...";

    fetch("/admin/api/crawl/ig/run", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        body: JSON.stringify({accountName: account, target})
    })
        .then(parseJsonResponse)
        .then(data => {
            alert(
                `크롤 완료\n` +
                `크롤 게시글 수: ${data.crawledPages}\n` +
                `raw 저장 수: ${data.rawSavedCount}\n` +
                `매칭 성공 수: ${data.matchedRestaurantCount}\n` +
                `미매칭 수: ${data.unmatchedRestaurantCount}`
            );
            closeCrawlModal();
            loadPartnerships(0);
        })
        .catch(error => {
            console.error("instagram crawl failed:", error);
            alert(`인스타 크롤 실패: ${error.message}`);
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = "크롤 실행";
        });
}

async function initCrawlModal() {
    const naverPlaceAddBtn = document.getElementById("naver-place-add-btn");
    const naverPlaceSyncBtn = document.getElementById("sync-crawl");
    const instaCrawlBtn = document.getElementById("insta-crawl");

    const crawlModalCloseBtn = document.getElementById("crawl-modal-close");
    const crawlCancelBtn = document.getElementById("crawl-cancel-btn");
    const crawlSubmitBtn = document.getElementById("crawl-submit-btn");
    const crawlModalOverlay = document.querySelector("#crawl-modal .admin-modal-overlay");

    const naverPlaceModalCloseBtn = document.getElementById("naver-place-crawl-modal-close");
    const naverPlaceCancelBtn = document.getElementById("naver-place-crawl-cancel-btn");
    const naverPlaceAnalyzeSubmitBtn = document.getElementById("naver-place-analyze-submit-btn");
    const naverPlaceSubmitBtn = document.getElementById("naver-place-crawl-submit-btn");
    const naverPlaceModalOverlay = document.querySelector("#naver-place-crawl-modal .admin-modal-overlay");

    const naverPlaceSyncModalCloseBtn = document.getElementById("naver-place-sync-modal-close");
    const naverPlaceSyncCancelBtn = document.getElementById("naver-place-sync-cancel-btn");
    const naverPlaceSyncSubmitBtn = document.getElementById("naver-place-sync-submit-btn");
    const naverPlaceSyncTestSubmitBtn = document.getElementById("naver-place-sync-test-submit-btn");
    const naverPlaceDbSyncSubmitBtn = document.getElementById("naver-place-db-sync-submit-btn");
    const naverPlaceSyncCandidatesRefreshBtn = document.getElementById("naver-place-sync-candidates-refresh-btn");
    const naverPlaceSyncCandidates = document.getElementById("naver-place-sync-candidates");
    const naverPlaceSyncModalOverlay = document.querySelector("#naver-place-sync-modal .admin-modal-overlay");

    enforceNaverPlaceIdOnlyInput();

    naverPlaceAddBtn?.addEventListener("click", openNaverPlaceModal);
    naverPlaceSyncBtn?.addEventListener("click", openNaverPlaceSyncModal);
    instaCrawlBtn?.addEventListener("click", openCrawlModal);

    crawlModalCloseBtn?.addEventListener("click", closeCrawlModal);
    crawlCancelBtn?.addEventListener("click", closeCrawlModal);
    crawlModalOverlay?.addEventListener("click", closeCrawlModal);
    crawlSubmitBtn?.addEventListener("click", runInstagramCrawl);

    naverPlaceModalCloseBtn?.addEventListener("click", closeNaverPlaceModal);
    naverPlaceCancelBtn?.addEventListener("click", closeNaverPlaceModal);
    naverPlaceModalOverlay?.addEventListener("click", closeNaverPlaceModal);
    naverPlaceAnalyzeSubmitBtn?.addEventListener("click", runNaverPlaceAnalyze);
    naverPlaceSubmitBtn?.addEventListener("click", runNaverPlaceCrawl);

    naverPlaceSyncModalCloseBtn?.addEventListener("click", closeNaverPlaceSyncModal);
    naverPlaceSyncCancelBtn?.addEventListener("click", closeNaverPlaceSyncModal);
    naverPlaceSyncModalOverlay?.addEventListener("click", closeNaverPlaceSyncModal);
    naverPlaceSyncSubmitBtn?.addEventListener("click", runNaverPlaceZoneCrawl);
    naverPlaceSyncTestSubmitBtn?.addEventListener("click", runNaverPlaceZoneCrawlTest);
    naverPlaceDbSyncSubmitBtn?.addEventListener("click", runDbSync);
    naverPlaceSyncCandidatesRefreshBtn?.addEventListener("click", () => {
        fetchSyncCandidates().catch(error => {
            console.error("refresh sync candidates failed:", error);
            alert(`후보 조회 실패: ${error.message}`);
        });
    });

    naverPlaceSyncCandidates?.addEventListener("click", event => {
        const target = event.target;
        if (!(target instanceof HTMLElement)) return;
        const action = target.dataset.action;
        const id = target.dataset.id;
        if (!action || !id) return;

        handleCandidateAction(action, id).catch(error => {
            console.error("candidate action failed:", error);
            alert(`후보 ${action} 실패: ${error.message}`);
        });
    });
}
