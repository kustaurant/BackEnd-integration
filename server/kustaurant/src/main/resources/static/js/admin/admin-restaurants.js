// 음식점 관리 관련 기능들

// 음식점 상태 텍스트 변환 함수
function getRestaurantStatusText(status) {
    const statusMap = {
        'active': '운영중',
        'inactive': '휴업',
        'closed': '폐업',
        'pending': '승인대기'
    };
    return statusMap[status] || status;
}

// 음식점 상태 CSS 클래스 변환 함수
function getRestaurantStatusClass(status) {
    const classMap = {
        'active': 'active',
        'inactive': 'inactive',
        'closed': 'closed',
        'pending': 'pending'
    };
    return classMap[status] || 'unknown';
}

// 실제 음식점 데이터 렌더링 함수
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

// 음식점 데이터 로드 함수
function loadRestaurants(page = 0) {
    fetch(`/admin/api/restaurants?page=${page}&size=20`)
        .then(response => response.json())
        .then(data => {
            renderRestaurantsTable(data.restaurants);
            renderPagination(data, 'restaurants', loadRestaurants);
            
            // 음식점 총 수 업데이트
            document.getElementById('restaurants-total').textContent = `총 ${data.totalElements.toLocaleString()}개`;
        })
        .catch(error => {
            console.error('음식점 데이터 로드 실패:', error);
            document.getElementById('restaurants-tbody').innerHTML = '<tr><td colspan="8">데이터 로드 실패</td></tr>';
        });
}