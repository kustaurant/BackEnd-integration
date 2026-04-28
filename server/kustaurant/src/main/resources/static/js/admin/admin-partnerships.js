function getPartnershipStatusText(status) {
    const map = {
        MATCHED: "매칭",
        UNMATCHED: "미매칭"
    };
    return map[status] || status;
}

function getPartnershipStatusClass(status) {
    const map = {
        MATCHED: "matched",
        UNMATCHED: "unmatched"
    };
    return map[status] || "unknown";
}

function renderPartnershipTable(partnerships) {
    const tbody = document.getElementById("partnerships-tbody");
    if (!tbody) return;
    tbody.innerHTML = "";

    if (!partnerships || partnerships.length === 0) {
        tbody.innerHTML = "<tr><td colspan=\"11\">제휴 정보가 없습니다.</td></tr>";
        return;
    }

    const fragment = document.createDocumentFragment();
    partnerships.forEach(p => {
        const tr = document.createElement("tr");
        tr.classList.add("partnership-row");
        tr.dataset.id = p.id;
        tr.innerHTML = `
            <td><button class="toggle-candidates-btn" data-id="${p.id}">후보보기</button></td>
            <td>${p.id ?? "-"}</td>
            <td>${p.restaurantId ?? "-"}</td>
            <td>${p.restaurantName ?? "-"}</td>
            <td>${p.target ?? "-"}</td>
            <td>${truncateText(p.benefit ?? "-", 30)}</td>
            <td><span class="partnership-status ${getPartnershipStatusClass(p.status)}">${getPartnershipStatusText(p.status)}</span></td>
            <td>${p.url ?? "-"}</td>
            <td>${p.createdAt ? formatDateOnly(p.createdAt) : "-"}</td>
            <td>${p.updatedAt ? formatDateOnly(p.updatedAt) : "-"}</td>
            <td><button class="edit-partnership-btn" data-id="${p.id}">수정</button></td>
        `;

        const detailTr = document.createElement("tr");
        detailTr.classList.add("candidate-detail-row");
        detailTr.dataset.id = p.id;
        detailTr.style.display = "none";
        detailTr.innerHTML = `
            <td colspan="11">
                <div class="candidate-detail-box">후보를 불러오는 중..</div>
            </td>
        `;
        fragment.appendChild(tr);
        fragment.appendChild(detailTr);
    });

    tbody.appendChild(fragment);
}

async function togglePartnershipCandidates(partnershipId, buttonEl) {
    const detailRow = document.querySelector(`.candidate-detail-row[data-id="${partnershipId}"]`);
    if (!detailRow) return;
    const box = detailRow.querySelector(".candidate-detail-box");

    if (detailRow.style.display !== "none") {
        detailRow.style.display = "none";
        buttonEl.textContent = "후보보기";
        return;
    }

    detailRow.style.display = "";
    buttonEl.textContent = "닫기";
    if (detailRow.dataset.loaded === "true") return;

    box.innerHTML = "후보를 불러오는 중..";
    try {
        const response = await fetch(`/admin/api/partnerships/${partnershipId}/candidates`);
        if (!response.ok) throw new Error("candidate load failed");

        const data = await response.json();
        renderCandidateDetail(box, data);
        detailRow.dataset.loaded = "true";
    } catch (error) {
        console.error("후보 조회 실패:", error);
        box.innerHTML = "<div class=\"candidate-empty\">후보 조회 실패</div>";
    }
}

function renderCandidateDetail(container, data) {
    const candidates = data.candidates || [];
    if (candidates.length === 0) {
        container.innerHTML = `
            <div class="candidate-wrapper">
                <div><strong>원본 업체명:</strong> ${data.rawRestaurantName ?? "-"}</div>
                <div><strong>원본 위치:</strong> ${data.rawLocationText ?? "-"}</div>
                <div class="candidate-empty">후보가 없습니다.</div>
            </div>
        `;
        return;
    }

    const itemsHtml = candidates.map((c, index) => `
        <div class="candidate-item">
            <div class="candidate-action">
                <button
                    class="apply-candidate-btn"
                    data-partnership-id="${data.partnershipId}"
                    data-restaurant-id="${c.restaurantId}"
                    data-restaurant-name="${c.restaurantName ?? ""}"
                    data-benefit="${data.benefit ?? ""}">
                    이 후보로 적용
                </button>
            </div>
            <div class="candidate-main">
                <div class="candidate-rank">${index + 1}순위</div>
                <div><strong>ID:</strong> ${c.restaurantId ?? "-"}</div>
                <div><strong>이름:</strong> ${c.restaurantName ?? "-"}</div>
                <div><strong>주소:</strong> ${c.address ?? "-"}</div>
                <div><strong>전화번호:</strong> ${c.phoneNumber ?? "-"}</div>
            </div>
        </div>
    `).join("");

    container.innerHTML = `
        <div class="candidate-wrapper">
            <div><strong>원본 업체명:</strong> ${data.rawRestaurantName ?? "-"}</div>
            <div><strong>원본 위치:</strong> ${data.rawLocationText ?? "-"}</div>
            <div class="candidate-list">${itemsHtml}</div>
        </div>
    `;
}

function loadPartnerships(page = 0) {
    fetch(`/admin/api/partnerships?page=${page}&size=20`)
        .then(response => {
            if (!response.ok) throw new Error("partnership load failed");
            return response.json();
        })
        .then(data => {
            renderPartnershipTable(data.partnerships);
            renderPagination(data, "partnerships", loadPartnerships);

            const total = document.getElementById("partnerships-total");
            if (total) {
                total.textContent = `총 ${data.totalElements.toLocaleString()}개`;
            }
        })
        .catch(error => {
            console.error("제휴 데이터 로드 실패:", error);
            const tbody = document.getElementById("partnerships-tbody");
            if (tbody) {
                tbody.innerHTML = "<tr><td colspan=\"10\">데이터 로드 실패</td></tr>";
            }
        });
}

function applyCandidateToEditModal(button) {
    document.getElementById("edit-partnership-id").value = button.dataset.partnershipId;
    document.getElementById("edit-restaurant-id").value = button.dataset.restaurantId ?? "";
    document.getElementById("edit-restaurant-name").value = button.dataset.restaurantName ?? "";
    document.getElementById("edit-benefit").value = button.dataset.benefit ?? "";
    document.getElementById("partnership-edit-modal").classList.remove("hidden");
}

function openEditModal(p) {
    document.getElementById("edit-partnership-id").value = p.id;
    document.getElementById("edit-restaurant-id").value = p.restaurantId ?? "";
    document.getElementById("edit-restaurant-name").value = p.restaurantName ?? "";
    document.getElementById("edit-benefit").value = p.benefit ?? "";
    document.getElementById("partnership-edit-modal").classList.remove("hidden");
}

function closeEditModal() {
    document.getElementById("partnership-edit-modal").classList.add("hidden");
}

function submitPartnershipEdit() {
    const id = document.getElementById("edit-partnership-id").value;
    const restaurantId = document.getElementById("edit-restaurant-id").value;
    const payload = {
        restaurantId: restaurantId || null,
        restaurantName: document.getElementById("edit-restaurant-name").value,
        benefit: document.getElementById("edit-benefit").value,
        matchStatus: restaurantId === "" ? "UNMATCHED" : "MATCHED"
    };

    const csrfToken = getCookie("XSRF-TOKEN");
    fetch(`/admin/api/partnerships/${id}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": csrfToken
        },
        body: JSON.stringify(payload)
    })
        .then(r => {
            if (!r.ok) throw new Error("update failed");
        })
        .then(() => {
            alert("수정 완료");
            closeEditModal();
            loadPartnerships(0);
        })
        .catch(() => {
            alert("수정 실패");
        });
}

function openDeletePartnershipModal() {
    document.getElementById("delete-partnership-modal")?.classList.remove("hidden");
}

function closeDeletePartnershipModal() {
    document.getElementById("delete-partnership-modal")?.classList.add("hidden");
}

function deletePartnerships() {
    const csrfToken = getCookie("XSRF-TOKEN");
    const target = document.getElementById("delete-partnership-target")?.value;
    if (!target) {
        alert("삭제 대상을 선택하세요.");
        return;
    }

    const label = target === "ALLDATA"
        ? "전체"
        : (document.getElementById("delete-partnership-target")?.selectedOptions?.[0]?.textContent ?? target);

    if (!confirm(`정말 [${label}] 제휴데이터를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) return;

    fetch("/admin/api/partnerships", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": csrfToken
        },
        body: JSON.stringify({ target })
    })
        .then(r => {
            if (!r.ok) throw new Error("delete failed");
            return r.json();
        })
        .then(data => {
            alert(`삭제 완료: ${data.deletedCount}건`);
            closeDeletePartnershipModal();
            loadPartnerships(0);
        })
        .catch(error => {
            console.error("제휴데이터 삭제 실패:", error);
            alert("삭제 실패");
        });
}

document.addEventListener("click", async e => {
    if (e.target.classList.contains("edit-partnership-btn")) {
        const id = e.target.dataset.id;
        fetch(`/admin/api/partnerships/${id}`)
            .then(r => r.json())
            .then(openEditModal);
        return;
    }

    if (e.target.classList.contains("toggle-candidates-btn")) {
        const id = e.target.dataset.id;
        await togglePartnershipCandidates(id, e.target);
        return;
    }

    if (e.target.classList.contains("apply-candidate-btn")) {
        applyCandidateToEditModal(e.target);
    }
});
