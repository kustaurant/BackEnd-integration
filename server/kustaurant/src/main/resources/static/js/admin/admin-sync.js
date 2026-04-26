function openRestaurantSyncRunModal() {
    document.getElementById("restaurant-sync-run-modal")?.classList.remove("hidden");
}

function closeRestaurantSyncRunModal() {
    document.getElementById("restaurant-sync-run-modal")?.classList.add("hidden");
}

function buildSyncScopePayload() {
    const scope = document.getElementById("restaurant-sync-scope")?.value;
    if (!scope || scope === "ALL") return {};
    return { crawlScope: scope };
}

function renderUpdatedPlaces(updatedRestaurants) {
    const list = document.getElementById("sync-updated-list");
    if (!list) return;

    if (!updatedRestaurants || updatedRestaurants.length === 0) {
        list.innerHTML = `<div class="sync-updated-empty">이번 실행에서 업데이트된 식당이 없습니다.</div>`;
        return;
    }

    list.innerHTML = updatedRestaurants
        .map(item => `<a class="sync-updated-chip" href="${item.restaurantLink}" target="_blank" rel="noopener noreferrer">${item.restaurantName || item.placeId} (${item.placeId})</a>`)
        .join("");
}

function renderCandidateRows(targetBodyId, candidates) {
    const tbody = document.getElementById(targetBodyId);
    if (!tbody) return;
    const isClosedCandidateTable = targetBodyId === "sync-closed-tbody";

    if (!candidates || candidates.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5">대기 후보가 없습니다.</td></tr>`;
        return;
    }

    tbody.innerHTML = candidates.map(candidate => `
        <tr>
            <td>${candidate.id}</td>
            <td>${candidate.placeId}</td>
            <td><a href="${candidate.restaurantLink}" target="_blank" rel="noopener noreferrer">${candidate.restaurantName || candidate.placeId}</a></td>
            <td>${formatDateTime(candidate.createdAt)}</td>
            <td>
                <button type="button" data-sync-action="approve" data-id="${candidate.id}">${isClosedCandidateTable ? "폐점" : "승인"}</button>
                <button type="button" data-sync-action="reject" data-id="${candidate.id}">${isClosedCandidateTable ? "유지" : "반려"}</button>
            </td>
        </tr>
    `).join("");
}

function renderSyncSummary(runResponse, pendingCandidates) {
    const summaryEl = document.getElementById("restaurant-sync-summary");
    if (!summaryEl) return;

    const pendingNew = pendingCandidates.filter(candidate => candidate.candidateType === "NEW").length;
    const pendingClosed = pendingCandidates.filter(candidate => candidate.candidateType === "CLOSED").length;
    const rawCount = runResponse?.rawCount ?? "-";
    const existingCount = runResponse?.existingCount ?? "-";
    const updatedCount = runResponse?.updatedRestaurantCount ?? 0;
    summaryEl.textContent = `raw ${rawCount} / 운영 ${existingCount} / 업데이트 ${updatedCount} / 대기 신규 ${pendingNew} / 대기 폐점 ${pendingClosed}`;
}

async function loadPendingSyncCandidates(runResponse = null) {
    const response = await fetch("/admin/api/sync/candidates?status=PENDING", {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    });
    const candidates = await parseJsonResponse(response);
    const newCandidates = candidates.filter(candidate => candidate.candidateType === "NEW");
    const closedCandidates = candidates.filter(candidate => candidate.candidateType === "CLOSED");

    renderCandidateRows("sync-new-tbody", newCandidates);
    renderCandidateRows("sync-closed-tbody", closedCandidates);
    renderSyncSummary(runResponse, candidates);
}

async function runRestaurantSync() {
    const submitBtn = document.getElementById("restaurant-sync-run-submit-btn");
    if (!submitBtn) return;

    submitBtn.disabled = true;
    submitBtn.textContent = "실행 중..";

    try {
        const response = await fetch("/admin/api/sync/candidates", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "X-Requested-With": "XMLHttpRequest",
                "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
            },
            body: JSON.stringify(buildSyncScopePayload())
        });
        const runResponse = await parseJsonResponse(response);
        renderUpdatedPlaces(runResponse.updatedRestaurants || []);
        await loadPendingSyncCandidates(runResponse);
        closeRestaurantSyncRunModal();
        loadRestaurants(0);
    } catch (error) {
        console.error("restaurant sync failed:", error);
        alert(`싱크 실행 실패: ${error.message}`);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "싱크 실행";
    }
}

async function handleSyncCandidateAction(action, candidateId) {
    const actionLabel = action === "approve" ? "승인" : "반려";
    if (!window.confirm(`${actionLabel} 처리할까요?`)) return;

    const response = await fetch(`/admin/api/sync/candidates/${candidateId}/${action}`, {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
        }
    });
    await parseJsonResponse(response);
    await loadPendingSyncCandidates();
    loadRestaurants(0);
}

function bindSyncCandidateTableActions() {
    const handler = event => {
        const target = event.target;
        if (!(target instanceof HTMLElement)) return;
        const action = target.dataset.syncAction;
        const candidateId = target.dataset.id;
        if (!action || !candidateId) return;

        handleSyncCandidateAction(action, candidateId).catch(error => {
            console.error("sync candidate action failed:", error);
            alert(`후보 처리 실패: ${error.message}`);
        });
    };

    document.getElementById("sync-new-tbody")?.addEventListener("click", handler);
    document.getElementById("sync-closed-tbody")?.addEventListener("click", handler);
}

function initializeRestaurantSyncSection() {
    document.getElementById("open-restaurant-sync-modal-btn")?.addEventListener("click", openRestaurantSyncRunModal);
    document.getElementById("refresh-restaurant-sync-btn")?.addEventListener("click", () => {
        loadPendingSyncCandidates().catch(error => {
            console.error("sync candidates refresh failed:", error);
            alert(`후보 조회 실패: ${error.message}`);
        });
    });
    document.getElementById("restaurant-sync-run-submit-btn")?.addEventListener("click", runRestaurantSync);
    document.getElementById("restaurant-sync-run-cancel-btn")?.addEventListener("click", closeRestaurantSyncRunModal);
    document.getElementById("restaurant-sync-run-modal-close")?.addEventListener("click", closeRestaurantSyncRunModal);
    document.querySelector("#restaurant-sync-run-modal .admin-modal-overlay")
        ?.addEventListener("click", closeRestaurantSyncRunModal);

    bindSyncCandidateTableActions();
    renderUpdatedPlaces([]);
    loadPendingSyncCandidates().catch(error => {
        console.error("initial sync candidates load failed:", error);
    });
}
