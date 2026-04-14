// 음식점 관리 관련 기능들
console.log("admin js loaded");

function getRestaurantStatusText(status) {
    const statusMap = {
        'active': '운영중',
        'inactive': '휴업',
        'closed': '폐업',
        'pending': '승인대기'
    };
    return statusMap[status] || status;
}

function getRestaurantStatusClass(status) {
    const classMap = {
        'active': 'active',
        'inactive': 'inactive',
        'closed': 'closed',
        'pending': 'pending'
    };
    return classMap[status] || 'unknown';
}

function renderRestaurantsTable(restaurants) {
    const tbody = document.getElementById('restaurants-tbody');
    tbody.innerHTML = '';

    if (restaurants.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8">음식점 정보가 없습니다.</td></tr>';
        return;
    }

    restaurants.forEach(restaurant => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${restaurant.restaurantId}</td>
            <td>${restaurant.restaurantName}</td>
            <td>${restaurant.restaurantAddress}</td>
            <td>${restaurant.restaurantPosition}</td>
            <td>${restaurant.restaurantType || '-'}</td>
            <td><span class="evaluation-count">${restaurant.restaurantEvaluationCount || 0}</span></td>
            <td><span class="restaurant-status ${getRestaurantStatusClass(restaurant.status)}">${getRestaurantStatusText(restaurant.status)}</span></td>
            <td>${formatDateTime(restaurant.createdAt)}</td>
        `;
        tbody.appendChild(row);
    });
}

function loadRestaurants(page = 0) {
    fetch(`/admin/api/restaurants?page=${page}&size=20`)
        .then(response => response.json())
        .then(data => {
            renderRestaurantsTable(data.restaurants);
            renderPagination(data, 'restaurants', loadRestaurants);
            document.getElementById('restaurants-total').textContent = `총 ${data.totalElements.toLocaleString()}개`;
        })
        .catch(error => {
            console.error('음식점 데이터 로드 실패:', error);
            document.getElementById('restaurants-tbody').innerHTML = '<tr><td colspan="8">데이터 로드 실패</td></tr>';
        });
}

function getPartnershipStatusText(status) {
    const map = {
        'MATCHED': '매칭',
        'UNMATCHED': '미매칭'
    };
    return map[status] || status;
}

function getPartnershipStatusClass(status) {
    const map = {
        'MATCHED': 'matched',
        'UNMATCHED': 'unmatched'
    };
    return map[status] || 'unknown';
}

function renderPartnershipTable(partnerships) {
    const tbody = document.getElementById('partnerships-tbody');
    tbody.innerHTML = '';

    if (!partnerships || partnerships.length === 0) {
        tbody.innerHTML = '<tr><td colspan="11">제휴 정보가 없습니다.</td></tr>';
        return;
    }

    const fragment = document.createDocumentFragment();

    partnerships.forEach(p => {
        const tr = document.createElement('tr');
        tr.classList.add('partnership-row');
        tr.dataset.id = p.id;

        tr.innerHTML = `
            <td>
                <button class="toggle-candidates-btn" data-id="${p.id}">후보보기</button>
            </td>
            <td>${p.id ?? '-'}</td>
            <td>${p.restaurantId ?? '-'}</td>
            <td>${p.restaurantName ?? '-'}</td>
            <td>${p.target ?? '-'}</td>
            <td>${truncateText(p.benefit ?? '-', 30)}</td>
            <td>
                <span class="partnership-status ${getPartnershipStatusClass(p.status)}">
                    ${getPartnershipStatusText(p.status)}
                </span>
            </td>
            <td>${p.url ?? '-'}</td>
            <td>${p.createdAt ? formatDateOnly(p.createdAt) : '-'}</td>
            <td>${p.updatedAt ? formatDateOnly(p.updatedAt) : '-'}</td>
            <td>
                <button class="edit-partnership-btn" data-id="${p.id}">수정</button>
            </td>
        `;

        const detailTr = document.createElement('tr');
        detailTr.classList.add('candidate-detail-row');
        detailTr.dataset.id = p.id;
        detailTr.style.display = 'none';
        detailTr.innerHTML = `
            <td colspan="11">
                <div class="candidate-detail-box">후보를 불러오는 중...</div>
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

    const box = detailRow.querySelector('.candidate-detail-box');

    if (detailRow.style.display !== 'none') {
        detailRow.style.display = 'none';
        buttonEl.textContent = '후보보기';
        return;
    }

    detailRow.style.display = '';
    buttonEl.textContent = '접기';

    if (detailRow.dataset.loaded === 'true') {
        return;
    }

    box.innerHTML = '후보를 불러오는 중...';

    try {
        const response = await fetch(`/admin/api/partnerships/${partnershipId}/candidates`);
        if (!response.ok) {
            throw new Error('candidate load failed');
        }

        const data = await response.json();
        renderCandidateDetail(box, data);
        detailRow.dataset.loaded = 'true';
    } catch (error) {
        console.error('후보 조회 실패:', error);
        box.innerHTML = '<div class="candidate-empty">후보 조회 실패</div>';
    }
}


function renderCandidateDetail(container, data) {
    const candidates = data.candidates || [];

    if (candidates.length === 0) {
        container.innerHTML = `
            <div class="candidate-wrapper">
                <div><strong>원본 업체명:</strong> ${data.rawRestaurantName ?? '-'}</div>
                <div><strong>원본 위치:</strong> ${data.rawLocationText ?? '-'}</div>
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
                    data-restaurant-name="${c.restaurantName ?? ''}"
                    data-benefit="${data.benefit ?? ''}"
                >
                    이 후보로 적용
                </button>
            </div>
            <div class="candidate-main">
                <div class="candidate-rank">${index + 1}순위</div>
                <div><strong>ID:</strong> ${c.restaurantId ?? '-'}</div>
                <div><strong>이름:</strong> ${c.restaurantName ?? '-'}</div>
                <div><strong>주소:</strong> ${c.address ?? '-'}</div>
                <div><strong>전화번호:</strong> ${c.phoneNumber ?? '-'}</div>
            </div>
        </div>
    `).join('');

    container.innerHTML = `
        <div class="candidate-wrapper">
            <div><strong>원본 업체명:</strong> ${data.rawRestaurantName ?? '-'}</div>
            <div><strong>원본 위치:</strong> ${data.rawLocationText ?? '-'}</div>
            <div class="candidate-list">
                ${itemsHtml}
            </div>
        </div>
    `;
}


function loadPartnerships(page = 0) {
    fetch(`/admin/api/partnerships?page=${page}&size=20`)
        .then(response => {
            if (!response.ok) {
                throw new Error('partnership load failed');
            }
            return response.json();
        })
        .then(data => {

            renderPartnershipTable(data.partnerships);
            renderPagination(data, 'partnerships', loadPartnerships);
            document.getElementById('partnerships-total').textContent =
                `총 ${data.totalElements.toLocaleString()}개`;
        })
        .catch(error => {
            console.error('파트너십 데이터 로드 실패:', error);
            document.getElementById('partnerships-tbody').innerHTML =
                '<tr><td colspan="10">데이터 로드 실패</td></tr>';
        });
}

function openCrawlModal() {
    const modal = document.getElementById('crawl-modal');
    if (!modal) return;
    modal.classList.remove('hidden');
}

function openNaverPlaceModal() {
    const modal = document.getElementById('naver-place-crawl-modal');
    if (!modal) return;
    modal.classList.remove('hidden');
}

function openNaverPlaceSyncModal() {
    const modal = document.getElementById('naver-place-sync-modal');
    if (!modal) return;
    modal.classList.remove('hidden');
}

function closeNaverPlaceModal() {
    const modal = document.getElementById('naver-place-crawl-modal');
    if (!modal) return;
    modal.classList.add('hidden');
}

function closeNaverPlaceSyncModal() {
    const modal = document.getElementById('naver-place-sync-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    stopNaverPlaceZoneSyncPolling();
}

function closeCrawlModal() {
    const modal = document.getElementById('crawl-modal');
    if (!modal) return;
    modal.classList.add('hidden');
}

// tmp
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        return parts.pop().split(';').shift();
    }
    return null;
}

function renderNaverPlaceCrawlResult(data) {
    const resultBox = document.getElementById('naver-place-crawl-result');
    if (!resultBox) return;

    const menus = data.menus || [];
    const menuHtml = menus.length === 0
        ? '<li>메뉴 정보 없음</li>'
        : menus.map(menu => `
            <li>
                <strong>${menu.menuName || '-'}</strong>
                <span>${menu.menuPrice || '-'}</span>
            </li>
        `).join('');

    resultBox.innerHTML = `
        <div class="naver-place-result-summary">
            <div><strong>Raw ID:</strong> ${data.rawId}</div>
            <div><strong>식당명:</strong> ${data.placeName || '-'}</div>
            <div><strong>카테고리:</strong> ${data.category || '-'}</div>
            <div><strong>도로명 주소:</strong> ${data.restaurantAddress || '-'}</div>
            <div><strong>전화번호:</strong> ${data.phoneNumber || '-'}</div>
            <div><strong>좌표:</strong> ${data.latitude || '-'}, ${data.longitude || '-'}</div>
            <div><strong>메뉴 수:</strong> ${data.menuCount || 0}</div>
        </div>
        <div class="naver-place-result-menus">
            <strong>메뉴 미리보기</strong>
            <ul>${menuHtml}</ul>
        </div>
    `;
    resultBox.classList.remove('hidden');
}

function executeNaverPlaceAction(options) {
    const urlInput = document.getElementById('naver-place-url');
    const resultBox = document.getElementById('naver-place-crawl-result');
    const submitBtn = document.getElementById(options.submitButtonId);
    const placeUrl = urlInput?.value?.trim();

    if (!placeUrl) {
        alert('크롤링할 네이버 식당 URL을 입력하세요.');
        return;
    }

    if (resultBox) {
        resultBox.classList.add('hidden');
        resultBox.innerHTML = '';
    }

    const csrfToken = getCookie('XSRF-TOKEN');
    submitBtn.disabled = true;
    submitBtn.textContent = options.runningText;

    fetch(options.endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            'X-XSRF-TOKEN': csrfToken
        },
        body: JSON.stringify({ placeUrl })
    })
        .then(async response => {
            const text = await response.text();
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
        })
        .then(data => {
            renderNaverPlaceCrawlResult(data);
            if (options.reloadRestaurants) {
                loadRestaurants(0);
            }
        })
        .catch(error => {
            console.error(options.errorLogText, error);
            alert(`${options.errorAlertText}: ${error.message}`);
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = options.idleText;
        });
}

function runNaverPlaceCrawl() {
    executeNaverPlaceAction({
        endpoint: '/admin/api/crawl/naver-place/raw',
        submitButtonId: 'naver-place-crawl-submit-btn',
        runningText: '추가 중..',
        idleText: 'URL로 음식점 추가',
        errorLogText: '네이버 식당 URL 추가 실패:',
        errorAlertText: '네이버 식당 URL 추가 실패',
        reloadRestaurants: true
    });
}

function runNaverPlaceAnalyze() {
    executeNaverPlaceAction({
        endpoint: '/admin/api/crawl/naver-place/analyze',
        submitButtonId: 'naver-place-analyze-submit-btn',
        runningText: '분석 중..',
        idleText: 'URL 분석하기',
        errorLogText: '네이버 식당 URL 분석 실패:',
        errorAlertText: '네이버 식당 URL 분석 실패',
        reloadRestaurants: false
    });
}

function renderNaverPlaceSyncResult(data) {
    const resultBox = document.getElementById('naver-place-sync-result');
    if (!resultBox) return;

    resultBox.innerHTML = `
        <div><strong>구역:</strong> ${data.crawlScope || '-'}</div>
        <div><strong>발견 식당 수:</strong> ${data.discoveredPlaceCount || 0}</div>
        <div><strong>크롤 성공 수:</strong> ${data.crawledSuccessCount || 0}</div>
        <div><strong>raw 저장 수:</strong> ${data.savedRawCount || 0}</div>
    `;
    resultBox.classList.remove('hidden');
}

function runNaverPlaceZoneSync() {
    const scopeSelect = document.getElementById('naver-place-sync-scope');
    const resultBox = document.getElementById('naver-place-sync-result');
    const submitBtn = document.getElementById('naver-place-sync-submit-btn');
    const crawlScope = scopeSelect?.value;

    if (!crawlScope) {
        alert('크롤링할 구역을 선택하세요.');
        return;
    }

    if (resultBox) {
        resultBox.classList.add('hidden');
        resultBox.innerHTML = '';
    }

    const csrfToken = getCookie('XSRF-TOKEN');
    submitBtn.disabled = true;
    submitBtn.textContent = '실행 중..';

    fetch('/admin/api/crawl/naver-place/sync-zone', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            'X-XSRF-TOKEN': csrfToken
        },
        body: JSON.stringify({ crawlScope })
    })
        .then(async response => {
            const text = await response.text();
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
        })
        .then(data => {
            renderNaverPlaceSyncResult(data);
        })
        .catch(error => {
            console.error('네이버 플레이스 구역 크롤 실패:', error);
            alert(`네이버 플레이스 구역 크롤 실패: ${error.message}`);
        })
        .finally(() => {
            submitBtn.disabled = false;
            submitBtn.textContent = '구역 크롤 시작';
        });
}

let naverPlaceZoneSyncPollTimer = null;

function stopNaverPlaceZoneSyncPolling() {
    if (naverPlaceZoneSyncPollTimer) {
        clearTimeout(naverPlaceZoneSyncPollTimer);
        naverPlaceZoneSyncPollTimer = null;
    }
}

function isZoneSyncJobFinished(status) {
    return status === 'SUCCESS' || status === 'FAILED';
}

function renderNaverPlaceSyncResult(data) {
    const resultBox = document.getElementById('naver-place-sync-result');
    if (!resultBox) return;

    resultBox.innerHTML = `
        <div><strong>상태:</strong> ${data.status || '-'}</div>
        <div><strong>구역:</strong> ${data.crawlScope || '-'}</div>
        <div><strong>현재 단계:</strong> ${data.currentPhase || '-'}</div>
        <div><strong>그리드 진행:</strong> ${(data.processedGridCount || 0)} / ${(data.totalGridCount || 0)}</div>
        <div><strong>발견 식당 수:</strong> ${data.discoveredPlaceCount || 0}</div>
        <div><strong>크롤 시도/성공:</strong> ${(data.attemptedPlaceCount || 0)} / ${(data.crawledSuccessCount || 0)}</div>
        <div><strong>raw 저장 성공/실패:</strong> ${(data.savedRawCount || 0)} / ${(data.saveFailedCount || 0)}</div>
        <div><strong>현재 grid:</strong> ${data.currentGrid || '-'}</div>
        <div><strong>현재 placeId:</strong> ${data.currentPlaceId || '-'}</div>
        ${data.errorMessage ? `<div><strong>오류:</strong> ${data.errorMessage}</div>` : ''}
    `;
    resultBox.classList.remove('hidden');
}

function runNaverPlaceZoneSync() {
    const scopeSelect = document.getElementById('naver-place-sync-scope');
    const resultBox = document.getElementById('naver-place-sync-result');
    const submitBtn = document.getElementById('naver-place-sync-submit-btn');
    const crawlScope = scopeSelect?.value;

    if (!crawlScope) {
        alert('크롤링할 구역을 선택하세요.');
        return;
    }

    if (resultBox) {
        resultBox.classList.add('hidden');
        resultBox.innerHTML = '';
    }

    const csrfToken = getCookie('XSRF-TOKEN');
    submitBtn.disabled = true;
    submitBtn.textContent = '작업 시작 중..';
    stopNaverPlaceZoneSyncPolling();

    fetch('/admin/api/crawl/naver-place/sync-zone/jobs', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            'X-XSRF-TOKEN': csrfToken
        },
        body: JSON.stringify({ crawlScope })
    })
        .then(async response => {
            const text = await response.text();
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
        })
        .then(data => {
            if (!data.jobId) {
                throw new Error('zone sync job id is missing');
            }
            submitBtn.textContent = '진행 중..';
            pollNaverPlaceZoneSyncStatus(data.jobId, submitBtn);
        })
        .catch(error => {
            console.error('네이버 플레이스 구역 동기화 시작 실패:', error);
            alert(`네이버 플레이스 구역 동기화 시작 실패: ${error.message}`);
            submitBtn.disabled = false;
            submitBtn.textContent = '구역 크롤 시작';
            stopNaverPlaceZoneSyncPolling();
        });
}

function pollNaverPlaceZoneSyncStatus(jobId, submitBtn) {
    const csrfToken = getCookie('XSRF-TOKEN');
    fetch(`/admin/api/crawl/naver-place/sync-zone/jobs/${jobId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            'X-XSRF-TOKEN': csrfToken
        }
    })
        .then(async response => {
            const text = await response.text();
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
        })
        .then(data => {
            renderNaverPlaceSyncResult(data);
            if (isZoneSyncJobFinished(data.status)) {
                submitBtn.disabled = false;
                submitBtn.textContent = '구역 크롤 시작';
                stopNaverPlaceZoneSyncPolling();
                return;
            }
            naverPlaceZoneSyncPollTimer = setTimeout(() => pollNaverPlaceZoneSyncStatus(jobId, submitBtn), 2000);
        })
        .catch(error => {
            console.error('네이버 플레이스 구역 동기화 상태 조회 실패:', error);
            submitBtn.disabled = false;
            submitBtn.textContent = '구역 크롤 시작';
            stopNaverPlaceZoneSyncPolling();
            alert(`구역 동기화 상태 조회 실패: ${error.message}`);
        });
}

function runInstagramCrawl() {
    console.log('[1] runInstagramCrawl 시작');

    const account = document.getElementById('crawl-account').value.trim();
    const target = document.getElementById('crawl-target').value;

    console.log('[2] account =', account);
    console.log('[3] target =', target);

    if (!account) {
        alert('인스타 계정을 입력하세요.');
        return;
    }

    if (!target) {
        alert('타겟을 선택하세요.');
        return;
    }

    const csrfToken = getCookie('XSRF-TOKEN');
    console.log('[4] csrfToken =', csrfToken);

    const submitBtn = document.getElementById('crawl-submit-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = '실행 중...';

    console.log('[5] fetch 직전');

    fetch('/admin/api/crawl/ig/run', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            'X-XSRF-TOKEN': csrfToken
        },
        body: JSON.stringify({
            accountName: account,
            target: target
        })
    })
        .then(async response => {
            console.log('[6] response 도착, status =', response.status);
            const text = await response.text();
            console.log('[7] response body =', text);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status} - ${text}`);
            }

            return text ? JSON.parse(text) : {};
        })
        .then(data => {
            console.log('[8] 성공 data =', data);
            alert(
                `크롤링 및 매칭 완료

                크롤링 게시글 수: ${data.crawledPages}
                raw 저장 수: ${data.rawSavedCount}
                매칭 성공 수: ${data.matchedRestaurantCount}
                미매칭 수: ${data.unmatchedRestaurantCount}`
            );
            closeCrawlModal();
            loadPartnerships(0);
        })
        .catch(error => {
            console.error('[9] 크롤링 실행 실패:', error);
            alert(`크롤링 실행 실패: ${error.message}`);
        })
        .finally(() => {
            console.log('[10] finally 진입');
            submitBtn.disabled = false;
            submitBtn.textContent = '크롤링 실행';
        });
}

////////////////////////////

async function initCrawlModal() {
    const naverPlaceAddBtn = document.getElementById('naver-place-add-btn');
    const naverPlaceSyncBtn = document.getElementById('sync-crawl');
    const instaCrawlBtn = document.getElementById('insta-crawl');
    const crawlModalCloseBtn = document.getElementById('crawl-modal-close');
    const crawlCancelBtn = document.getElementById('crawl-cancel-btn');
    const crawlSubmitBtn = document.getElementById('crawl-submit-btn');
    const crawlModalOverlay = document.querySelector('#crawl-modal .admin-modal-overlay');
    const naverPlaceModalCloseBtn = document.getElementById('naver-place-crawl-modal-close');
    const naverPlaceCancelBtn = document.getElementById('naver-place-crawl-cancel-btn');
    const naverPlaceAnalyzeSubmitBtn = document.getElementById('naver-place-analyze-submit-btn');
    const naverPlaceSubmitBtn = document.getElementById('naver-place-crawl-submit-btn');
    const naverPlaceModalOverlay = document.querySelector('#naver-place-crawl-modal .admin-modal-overlay');
    const naverPlaceSyncModalCloseBtn = document.getElementById('naver-place-sync-modal-close');
    const naverPlaceSyncCancelBtn = document.getElementById('naver-place-sync-cancel-btn');
    const naverPlaceSyncSubmitBtn = document.getElementById('naver-place-sync-submit-btn');
    const naverPlaceSyncModalOverlay = document.querySelector('#naver-place-sync-modal .admin-modal-overlay');

    if (naverPlaceAddBtn) {
        naverPlaceAddBtn.addEventListener('click', openNaverPlaceModal);
    }

    if (naverPlaceSyncBtn) {
        naverPlaceSyncBtn.addEventListener('click', openNaverPlaceSyncModal);
    }

    if (instaCrawlBtn) {
        instaCrawlBtn.addEventListener('click', openCrawlModal);
    }

    if (crawlModalCloseBtn) {
        crawlModalCloseBtn.addEventListener('click', closeCrawlModal);
    }

    if (crawlCancelBtn) {
        crawlCancelBtn.addEventListener('click', closeCrawlModal);
    }

    if (crawlModalOverlay) {
        crawlModalOverlay.addEventListener('click', closeCrawlModal);
    }

    if (crawlSubmitBtn) {
        crawlSubmitBtn.addEventListener('click', runInstagramCrawl);
    }

    if (naverPlaceModalCloseBtn) {
        naverPlaceModalCloseBtn.addEventListener('click', closeNaverPlaceModal);
    }

    if (naverPlaceCancelBtn) {
        naverPlaceCancelBtn.addEventListener('click', closeNaverPlaceModal);
    }

    if (naverPlaceModalOverlay) {
        naverPlaceModalOverlay.addEventListener('click', closeNaverPlaceModal);
    }

    if (naverPlaceAnalyzeSubmitBtn) {
        naverPlaceAnalyzeSubmitBtn.addEventListener('click', runNaverPlaceAnalyze);
    }

    if (naverPlaceSubmitBtn) {
        naverPlaceSubmitBtn.addEventListener('click', runNaverPlaceCrawl);
    }

    if (naverPlaceSyncModalCloseBtn) {
        naverPlaceSyncModalCloseBtn.addEventListener('click', closeNaverPlaceSyncModal);
    }

    if (naverPlaceSyncCancelBtn) {
        naverPlaceSyncCancelBtn.addEventListener('click', closeNaverPlaceSyncModal);
    }

    if (naverPlaceSyncModalOverlay) {
        naverPlaceSyncModalOverlay.addEventListener('click', closeNaverPlaceSyncModal);
    }

    if (naverPlaceSyncSubmitBtn) {
        naverPlaceSyncSubmitBtn.addEventListener('click', runNaverPlaceZoneSync);
    }
}

// 수정 버튼 클릭 이벤트
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
        return;
    }
});


function applyCandidateToEditModal(button) {
    const partnershipId = button.dataset.partnershipId;
    const restaurantId = button.dataset.restaurantId;
    const restaurantName = button.dataset.restaurantName;
    const benefit = button.dataset.benefit ?? "";

    document.getElementById("edit-partnership-id").value = partnershipId;
    document.getElementById("edit-restaurant-id").value = restaurantId ?? "";
    document.getElementById("edit-restaurant-name").value = restaurantName ?? "";
    document.getElementById("edit-benefit").value = benefit;

    document.getElementById("partnership-edit-modal").classList.remove("hidden");
}


// 제휴 수정 모달 열기
function openEditModal(p){

    document.getElementById("edit-partnership-id").value=p.id
    document.getElementById("edit-restaurant-id").value=p.restaurantId ?? ""
    document.getElementById("edit-restaurant-name").value=p.restaurantName ?? ""
    document.getElementById("edit-benefit").value=p.benefit ?? ""

    document.getElementById("partnership-edit-modal").classList.remove("hidden")
}
// 제휴 수정 모달 닫기
function closeEditModal(){
    document.getElementById("partnership-edit-modal").classList.add("hidden")
}

// 수정 API 호출
function submitPartnershipEdit(){
    const id=document.getElementById("edit-partnership-id").value
    const restaurantId = document.getElementById("edit-restaurant-id").value

    const payload={
        restaurantId: document.getElementById("edit-restaurant-id").value || null,
        restaurantName: document.getElementById("edit-restaurant-name").value,
        benefit: document.getElementById("edit-benefit").value,
        matchStatus: restaurantId === "" ? "UNMATCHED" : "MATCHED"
    }

    const csrfToken = getCookie("XSRF-TOKEN");

    fetch(`/admin/api/partnerships/${id}`,{
        method:"PATCH",
        headers:{
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest",
            "X-XSRF-TOKEN": csrfToken
        },
        body:JSON.stringify(payload)
    })
        .then(r=>{
            if(!r.ok) throw new Error()
        })
        .then(()=>{
            alert("수정 완료")
            closeEditModal()
            loadPartnerships()
        })
        .catch(()=>{
            alert("수정 실패")
        })

}

// 삭제 모달들 열고 닫기
function openDeletePartnershipModal() {
    document.getElementById("delete-partnership-modal")?.classList.remove("hidden");
}
function closeDeletePartnershipModal() {
    document.getElementById("delete-partnership-modal")?.classList.add("hidden");
}


function deletePartnerships() {
    const csrfToken = getCookie("XSRF-TOKEN");
    const target = document.getElementById("delete-partnership-target")?.value;

    console.log("csrfToken =", csrfToken);
    console.log("target =", target);

    if (!target) {
        alert("삭제 대상을 선택하세요.");
        return;
    }

    const label = target === "ALLDATA" ? "전체" :
        document.getElementById("delete-partnership-target")
        ?.selectedOptions?.[0]?.textContent ?? target;

    if (!confirm(`정말 [${label}] 제휴데이터를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
        return;
    }

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

// 이벤트 연결
document.addEventListener("DOMContentLoaded",async()=>{
    await preloadAdminModals()
    await initCrawlModal();
    loadRestaurants()
    loadPartnerships()

    document.getElementById("edit-submit-btn")?.addEventListener("click",submitPartnershipEdit)
    document.getElementById("edit-cancel-btn")?.addEventListener("click",closeEditModal)
    document.getElementById("edit-modal-close")?.addEventListener("click",closeEditModal)

    document.getElementById("delete-partnerships-btn")?.addEventListener("click", openDeletePartnershipModal);
    document.getElementById("delete-partnership-submit-btn")?.addEventListener("click", deletePartnerships);
    document.getElementById("delete-partnership-cancel-btn")?.addEventListener("click", closeDeletePartnershipModal);
    document.getElementById("delete-partnership-modal-close")?.addEventListener("click", closeDeletePartnershipModal);
    document.querySelector("#delete-partnership-modal .admin-modal-overlay")?.addEventListener("click", closeDeletePartnershipModal);
})
