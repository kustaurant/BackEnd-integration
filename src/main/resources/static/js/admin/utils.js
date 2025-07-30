// 공통 유틸리티 함수들

// 날짜 시간 포맷팅 함수
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '-';
    
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('ko-KR') + ' ' + 
           date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
}

// 텍스트 줄이기 함수
function truncateText(text, maxLength) {
    if (!text) return '-';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}

// 통합 페이징 렌더링 함수
function renderPagination(data, containerId, loadFunction) {
    const paginationContainer = document.getElementById(containerId + '-pagination');
    paginationContainer.innerHTML = '';

    if (data.totalPages <= 1) return;

    // 이전 버튼
    const prevButton = document.createElement('button');
    prevButton.textContent = '←';
    prevButton.disabled = !data.hasPrevious;
    prevButton.onclick = () => loadFunction(data.currentPage - 1);
    paginationContainer.appendChild(prevButton);

    // 페이지 번호 계산 로직
    const maxVisiblePages = 10;
    const currentPage = data.currentPage;
    const totalPages = data.totalPages;
    
    // 현재 페이지가 속한 그룹 계산 (0부터 시작)
    const currentGroup = Math.floor(currentPage / maxVisiblePages);
    const startPage = currentGroup * maxVisiblePages;
    const endPage = Math.min(startPage + maxVisiblePages - 1, totalPages - 1);
    
    // 이전 그룹 버튼 (10페이지 단위로 이동)
    if (startPage > 0) {
        const prevGroupButton = document.createElement('button');
        prevGroupButton.textContent = '‹‹';
        prevGroupButton.onclick = () => loadFunction(startPage - 1);
        paginationContainer.appendChild(prevGroupButton);
    }
    
    // 페이지 번호 버튼들
    for (let i = startPage; i <= endPage; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i + 1;
        pageButton.className = i === currentPage ? 'active' : '';
        pageButton.onclick = () => loadFunction(i);
        paginationContainer.appendChild(pageButton);
    }
    
    // 다음 그룹 버튼 (10페이지 단위로 이동)
    if (endPage < totalPages - 1) {
        const nextGroupButton = document.createElement('button');
        nextGroupButton.textContent = '››';
        nextGroupButton.onclick = () => loadFunction(endPage + 1);
        paginationContainer.appendChild(nextGroupButton);
    }

    // 다음 버튼
    const nextButton = document.createElement('button');
    nextButton.textContent = '→';
    nextButton.disabled = !data.hasNext;
    nextButton.onclick = () => loadFunction(data.currentPage + 1);
    paginationContainer.appendChild(nextButton);
}

// 대시보드 통계 업데이트 함수
function updateDashboardStats() {
    // 실제 API 호출로 통계 데이터 가져오기
    fetch('/admin/api/stats')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            // 실제 데이터로 업데이트
            document.getElementById('total-restaurants').textContent = data.totalRestaurants;
            document.getElementById('total-reports').textContent = data.totalReports;
            document.getElementById('total-feedback').textContent = data.totalFeedback;
            document.getElementById('total-community-posts').textContent = data.totalCommunityPosts;
            document.getElementById('total-evaluations').textContent = data.totalEvaluations;
            document.getElementById('total-community-comments').textContent = data.totalCommunityComments;
            document.getElementById('total-evaluation-comments').textContent = data.totalEvaluationComments;
            
            // 유저 통계 업데이트
            document.getElementById('naver-count').textContent = data.totalNaverUsers;
            document.getElementById('apple-count').textContent = data.totalAppleUsers;
        })
        .catch(error => {
            console.error('통계 데이터 로드 실패:', error);
            // 에러 발생 시 기본값으로 fallback
            document.getElementById('total-restaurants').textContent = '0';
            document.getElementById('total-reports').textContent = '0';
            document.getElementById('total-feedback').textContent = '0';
            document.getElementById('total-community-posts').textContent = '0';
            document.getElementById('total-evaluations').textContent = '0';
            document.getElementById('total-community-comments').textContent = '0';
            document.getElementById('total-evaluation-comments').textContent = '0';
        });
    
    // 신규 유저 수 업데이트
    fetch('/admin/api/users/new/count')
        .then(response => response.json())
        .then(count => {
            document.getElementById('new-users-total').textContent = `총 ${count.toLocaleString()}명`;
        })
        .catch(error => {
            console.error('신규 유저 수 로드 실패:', error);
            document.getElementById('new-users-total').textContent = '총 0명';
        });
}