// 피드백 관리 관련 기능들

// 실제 피드백 데이터 렌더링 함수
function renderFeedbackTable(feedbacks) {
    const tbody = document.getElementById('feedback-tbody');
    tbody.innerHTML = '';
    
    if (feedbacks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">피드백이 없습니다.</td></tr>';
        return;
    }
    
    feedbacks.forEach(feedback => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${feedback.feedbackId}</td>
            <td class="feedback-content-cell">${truncateText(feedback.comment, 100)}</td>
            <td>${feedback.userId || '-'}</td>
            <td>${feedback.userNickname || '-'}</td>
            <td>${formatDateTime(feedback.createdAt)}</td>
        `;
        tbody.appendChild(row);
    });
}

// 피드백 데이터 로드 함수
function loadFeedbacks(page = 0) {
    fetch(`/admin/api/feedbacks?page=${page}&size=20`)
        .then(response => response.json())
        .then(data => {
            renderFeedbackTable(data.feedbacks);
            renderPagination(data, 'feedback', loadFeedbacks);
            
            // 피드백 총 수 업데이트
            document.getElementById('feedback-total').textContent = `총 ${data.totalElements.toLocaleString()}개`;
        })
        .catch(error => {
            console.error('피드백 데이터 로드 실패:', error);
            document.getElementById('feedback-tbody').innerHTML = '<tr><td colspan="5">데이터 로드 실패</td></tr>';
        });
}