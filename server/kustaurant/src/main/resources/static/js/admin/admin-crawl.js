let naverPlaceZoneSyncPollTimer = null;

function stopNaverPlaceZoneSyncPolling() {
    if (naverPlaceZoneSyncPollTimer) {
        clearTimeout(naverPlaceZoneSyncPollTimer);
        naverPlaceZoneSyncPollTimer = null;
    }
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(";").shift();
    return null;
}

function openCrawlModal() {
    const modal = document.getElementById("crawl-modal");
    if (modal) modal.classList.remove("hidden");
}

function closeCrawlModal() {
    const modal = document.getElementById("crawl-modal");
    if (modal) modal.classList.add("hidden");
}

function openNaverPlaceModal() {
    const modal = document.getElementById("naver-place-crawl-modal");
    if (modal) modal.classList.remove("hidden");
}

function closeNaverPlaceModal() {
    const modal = document.getElementById("naver-place-crawl-modal");
    if (modal) modal.classList.add("hidden");
}

function openNaverPlaceSyncModal() {
    const modal = document.getElementById("naver-place-sync-modal");
    if (modal) modal.classList.remove("hidden");
}

function closeNaverPlaceSyncModal() {
    const modal = document.getElementById("naver-place-sync-modal");
    if (modal) modal.classList.add("hidden");
    stopNaverPlaceZoneSyncPolling();
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
            <div><strong>주소:</strong> ${data.restaurantAddress || "-"}</div>
            <div><strong>전화번호:</strong> ${data.phoneNumber || "-"}</div>
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

function buildNaverPlaceUrlFromId(placeIdInput) {
    const placeId = (placeIdInput || "").trim();
    if (!/^\d+$/.test(placeId)) {
        return null;
    }
    return `https://map.naver.com/p/entry/place/${placeId}`;
}

function enforceNaverPlaceIdOnlyInput() {
    const input = document.getElementById("naver-place-url");
    if (!input) return;
    input.addEventListener("input", () => {
        input.value = input.value.replace(/\D+/g, "");
    });
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

async function executeNaverPlaceAction(options) {
    const urlInput = document.getElementById("naver-place-url");
    const resultBox = document.getElementById("naver-place-crawl-result");
    const submitBtn = document.getElementById(options.submitButtonId);
    const placeIdInput = urlInput?.value?.trim();
    if (!placeIdInput) {
        alert("place ID를 입력하세요.");
        return;
    }

    const placeUrl = buildNaverPlaceUrlFromId(placeIdInput);
    if (!placeUrl) {
        alert("숫자로만 된 place ID를 입력하세요.");
        return;
    }

    if (!placeUrl) {
        alert("크롤링할 네이버 식당 URL을 입력하세요.");
        return;
    }

    if (options.confirmOnExistingRaw) {
        try {
            const existence = await fetchNaverPlaceRawExistence(placeIdInput);
            if (existence?.exists) {
                const zoneLabel = existence.crawlScopeDescription || existence.crawlScope || "알 수 없는";
                if (!window.confirm(`이미 ${zoneLabel} 구역에 존재합니다. 그래도 진행하시겠습니까?`)) {
                    return;
                }
            }
        } catch (error) {
            console.warn("기존 placeId 존재 여부 확인 실패. 확인 없이 진행합니다.", error);
        }
    }

    if (resultBox) {
        resultBox.classList.add("hidden");
        resultBox.innerHTML = "";
    }

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
            body: JSON.stringify({ placeUrl })
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
        runningText: "추가 중..",
        idleText: "URL로 음식점 추가",
        errorLogText: "네이버 식당 URL 추가 실패:",
        errorAlertText: "네이버 식당 URL 추가 실패",
        reloadRestaurants: true,
        confirmOnExistingRaw: true
    });
}

function runNaverPlaceAnalyze() {
    executeNaverPlaceAction({
        endpoint: "/admin/api/crawl/naver-place/analyze",
        submitButtonId: "naver-place-analyze-submit-btn",
        runningText: "분석 중..",
        idleText: "URL 분석하기",
        errorLogText: "네이버 식당 URL 분석 실패:",
        errorAlertText: "네이버 식당 URL 분석 실패",
        reloadRestaurants: false,
        confirmOnExistingRaw: false
    });
}

function isZoneSyncJobFinished(status) {
    return status === "SUCCESS" || status === "FAILED";
}

function renderNaverPlaceSyncResult(data) {
    const resultBox = document.getElementById("naver-place-sync-result");
    if (!resultBox) return;
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
        ${data.errorMessage ? `<div><strong>오류:</strong> ${data.errorMessage}</div>` : ""}
    `;
    resultBox.classList.remove("hidden");
}

function pollNaverPlaceZoneSyncStatus(jobId, submitBtn) {
    fetch(`/admin/api/crawl/naver-place/sync-zone/jobs/${jobId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    })
        .then(parseJsonResponse)
        .then(data => {
            renderNaverPlaceSyncResult(data);
            if (isZoneSyncJobFinished(data.status)) {
                submitBtn.disabled = false;
                submitBtn.textContent = "구역 크롤 시작";
                stopNaverPlaceZoneSyncPolling();
                return;
            }
            naverPlaceZoneSyncPollTimer = setTimeout(() => pollNaverPlaceZoneSyncStatus(jobId, submitBtn), 2000);
        })
        .catch(error => {
            console.error("네이버 플레이스 구역 동기화 상태 조회 실패:", error);
            submitBtn.disabled = false;
            submitBtn.textContent = "구역 크롤 시작";
            stopNaverPlaceZoneSyncPolling();
            alert(`구역 동기화 상태 조회 실패: ${error.message}`);
        });
}

function runNaverPlaceZoneSync() {
    const scopeSelect = document.getElementById("naver-place-sync-scope");
    const resultBox = document.getElementById("naver-place-sync-result");
    const submitBtn = document.getElementById("naver-place-sync-submit-btn");
    const crawlScope = scopeSelect?.value;

    if (!crawlScope) {
        alert("크롤링할 구역을 선택하세요.");
        return;
    }
    if (resultBox) {
        resultBox.classList.add("hidden");
        resultBox.innerHTML = "";
    }

    submitBtn.disabled = true;
    submitBtn.textContent = "작업 시작 중..";
    stopNaverPlaceZoneSyncPolling();

    fetch("/admin/api/crawl/naver-place/sync-zone/jobs", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        body: JSON.stringify({ crawlScope })
    })
        .then(parseJsonResponse)
        .then(data => {
            if (!data.jobId) throw new Error("zone sync job id is missing");
            submitBtn.textContent = "진행 중..";
            pollNaverPlaceZoneSyncStatus(data.jobId, submitBtn);
        })
        .catch(error => {
            console.error("네이버 플레이스 구역 동기화 시작 실패:", error);
            alert(`네이버 플레이스 구역 동기화 시작 실패: ${error.message}`);
            submitBtn.disabled = false;
            submitBtn.textContent = "구역 크롤 시작";
            stopNaverPlaceZoneSyncPolling();
        });
}

function runNaverPlaceZoneSyncTest() {
    const scopeSelect = document.getElementById("naver-place-sync-scope");
    const resultBox = document.getElementById("naver-place-sync-result");
    const submitBtn = document.getElementById("naver-place-sync-test-submit-btn");
    const crawlScope = scopeSelect?.value;

    if (!crawlScope) {
        alert("크롤링할 구역을 선택하세요.");
        return;
    }
    if (resultBox) {
        resultBox.classList.add("hidden");
        resultBox.innerHTML = "";
    }

    submitBtn.disabled = true;
    submitBtn.textContent = "테스트 실행 중...";

    fetch("/admin/api/crawl/naver-place/sync-zone/test", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        body: JSON.stringify({ crawlScope })
    })
        .then(parseJsonResponse)
        .then(data => {
            renderNaverPlaceSyncResult(data);
        })
        .catch(error => {
            console.warn("네이버 플레이스 테스트 구역 크롤 경고(알림 미표시):", error);
            if (resultBox) {
                resultBox.classList.remove("hidden");
                resultBox.innerHTML = `
                    <div><strong>상태:</strong> 경고</div>
                    <div><strong>메시지:</strong> 테스트 크롤 중 일부 항목 실패가 발생했습니다. 알림은 표시하지 않습니다.</div>
                    <div><strong>상세:</strong> ${error.message || "-"}</div>
                `;
            }
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = "테스트 동기 크롤";
        });
}

function runInstagramCrawl() {
    const account = document.getElementById("crawl-account").value.trim();
    const target = document.getElementById("crawl-target").value;
    if (!account) {
        alert("인스타 계정을 입력하세요.");
        return;
    }
    if (!target) {
        alert("타겟을 선택하세요.");
        return;
    }

    const submitBtn = document.getElementById("crawl-submit-btn");
    submitBtn.disabled = true;
    submitBtn.textContent = "실행 중..";

    fetch("/admin/api/crawl/ig/run", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        },
        body: JSON.stringify({ accountName: account, target })
    })
        .then(parseJsonResponse)
        .then(data => {
            alert(
                `크롤링 완료\n` +
                `크롤링 게시글 수: ${data.crawledPages}\n` +
                `raw 저장 수: ${data.rawSavedCount}\n` +
                `매칭 성공 수: ${data.matchedRestaurantCount}\n` +
                `미매칭 수: ${data.unmatchedRestaurantCount}`
            );
            closeCrawlModal();
            loadPartnerships(0);
        })
        .catch(error => {
            console.error("인스타 크롤 실행 실패:", error);
            alert(`인스타 크롤 실행 실패: ${error.message}`);
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
    naverPlaceSyncSubmitBtn?.addEventListener("click", runNaverPlaceZoneSync);
    naverPlaceSyncTestSubmitBtn?.addEventListener("click", runNaverPlaceZoneSyncTest);
}
