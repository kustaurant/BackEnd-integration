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
        tbody.innerHTML = '<tr><td colspan="10">제휴 정보가 없습니다.</td></tr>';
        return;
    }

    const fragment = document.createDocumentFragment();

    partnerships.forEach(p => {
        const tr = document.createElement('tr');

        tr.innerHTML = `
            <td>${p.id ?? '-'}</td>
            <td>${p.restaurantId ?? '-'}</td>
            <td>${p.restaurantName ?? '-'}</td>
            <td>${p.target ?? '-'}</td>
            <td>${truncateText(p.benefit ?? '-',30)}</td>
            <td>
            <span class="partnership-status ${getPartnershipStatusClass(p.status)}">
            ${getPartnershipStatusText(p.status)}
            </span>
            </td>
            <td>${p.url ?? '-'}</td>
            <td>${p.createdAt ? formatDateTime(p.createdAt) : '-'}</td>
            <td>${p.updatedAt ? formatDateTime(p.updatedAt) : '-'}</td>
            <td>
            <button class="edit-partnership-btn" data-id="${p.id}">수정</button>
            </td>
        `;

        fragment.appendChild(tr);
    });

    tbody.appendChild(fragment);
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
    const instaCrawlBtn = document.getElementById('insta-crawl');
    const crawlModalCloseBtn = document.getElementById('crawl-modal-close');
    const crawlCancelBtn = document.getElementById('crawl-cancel-btn');
    const crawlSubmitBtn = document.getElementById('crawl-submit-btn');
    const crawlModalOverlay = document.querySelector('#crawl-modal .admin-modal-overlay');

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
}

// 수정 버튼 클릭 이벤트
document.addEventListener("click", e=>{

    if(e.target.classList.contains("edit-partnership-btn")){

        const id=e.target.dataset.id

        fetch(`/admin/api/partnerships/${id}`)
            .then(r=>r.json())
            .then(openEditModal)

    }

})

// 제휴 수정 모달 열기
function openEditModal(p){

    document.getElementById("edit-partnership-id").value=p.id
    document.getElementById("edit-restaurant-id").value=p.restaurantId ?? ""
    document.getElementById("edit-restaurant-name").value=p.restaurantName ?? ""
    document.getElementById("edit-benefit").value=p.benefit ?? ""

    document
        .getElementById("partnership-edit-modal")
        .classList.remove("hidden")

}
// 제휴 수정 모달 닫기
function closeEditModal(){
    document
        .getElementById("partnership-edit-modal")
        .classList.add("hidden")

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

// 이벤트 연결
document.addEventListener("DOMContentLoaded",async()=>{
    await preloadAdminModals()
    await initCrawlModal();
    loadRestaurants()
    loadPartnerships()

    document.getElementById("edit-submit-btn")?.addEventListener("click",submitPartnershipEdit)
    document.getElementById("edit-cancel-btn")?.addEventListener("click",closeEditModal)
    document.getElementById("edit-modal-close")?.addEventListener("click",closeEditModal)
})