// 유저 관리 관련 기능들

// 상태 텍스트 변환 함수
function getStatusText(status) {
    const statusMap = {
        'active': '활성',
        'inactive': '비활성',
        'suspended': '정지'
    };
    return statusMap[status] || status;
}

// 신규 유저 테이블 렌더링
function renderNewUsersTable(users) {
    const tbody = document.getElementById('new-users-tbody');
    tbody.innerHTML = '';
    
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">신규 가입 유저가 없습니다.</td></tr>';
        return;
    }
    
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.userId}</td>
            <td>${user.nickname}</td>
            <td>${formatDateTime(user.createdAt)}</td>
            <td><span class="login-type ${user.loginApi}">${user.loginApi}</span></td>
            <td><span class="user-status ${user.status.toLowerCase()}">${getStatusText(user.status)}</span></td>
        `;
        tbody.appendChild(row);
    });
}

// 전체 유저 테이블 렌더링
function renderAllUsersTable(users) {
    const tbody = document.getElementById('all-users-tbody');
    tbody.innerHTML = '';
    
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">유저가 없습니다.</td></tr>';
        return;
    }
    
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.userId}</td>
            <td>${user.nickname}</td>
            <td>${formatDateTime(user.createdAt)}</td>
            <td><span class="login-type ${user.loginApi}">${user.loginApi}</span></td>
            <td><span class="user-status ${user.status.toLowerCase()}">${getStatusText(user.status)}</span></td>
            <td>${user.lastLoginAt ? formatDateTime(user.lastLoginAt) : '-'}</td>
        `;
        tbody.appendChild(row);
    });
}

// 신규 유저 데이터 로드 함수
function loadNewUsers(page = 0) {
    fetch(`/admin/api/users/new?page=${page}&size=20`)
        .then(response => response.json())
        .then(data => {
            renderNewUsersTable(data.users);
            renderPagination(data, 'new-users', loadNewUsers);
            
            // 신규 유저 수 업데이트
            document.getElementById('new-users-total').textContent = `총 ${data.totalElements.toLocaleString()}명`;
        })
        .catch(error => {
            console.error('신규 유저 데이터 로드 실패:', error);
            document.getElementById('new-users-tbody').innerHTML = '<tr><td colspan="5">데이터 로드 실패</td></tr>';
        });
}

// 전체 유저 데이터 로드 함수
function loadAllUsers(page = 0) {
    fetch(`/admin/api/users/all?page=${page}&size=50`)
        .then(response => response.json())
        .then(data => {
            renderAllUsersTable(data.users);
            renderPagination(data, 'all-users', loadAllUsers);
            
            // 전체 유저 수 업데이트
            document.getElementById('all-users-total').textContent = `총 ${data.totalElements.toLocaleString()}명`;
        })
        .catch(error => {
            console.error('전체 유저 데이터 로드 실패:', error);
            document.getElementById('all-users-tbody').innerHTML = '<tr><td colspan="6">데이터 로드 실패</td></tr>';
        });
}