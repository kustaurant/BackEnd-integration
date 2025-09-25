// 모달 설정 관련 기능들

// 현재 모달 정보 로드
function loadCurrentModal() {
    fetch('/admin/api/modal')
        .then(response => response.json())
        .then(data => {
            if (data) {
                document.getElementById('modal-title').value = data.title || '';
                document.getElementById('modal-content').value = data.body || '';
                document.getElementById('modal-end-date').value = data.expiredAt ? data.expiredAt.substring(0, 16) : '';
                
                // 현재 모달 미리보기 업데이트
                updateCurrentModalPreview(data);
            } else {
                // 데이터가 없을 때 폼 초기화
                document.getElementById('modal-title').value = '';
                document.getElementById('modal-content').value = '';
                document.getElementById('modal-end-date').value = '';
                
                // 미리보기도 초기화
                updateCurrentModalPreview(null);
            }
        })
        .catch(error => {
            console.error('모달 정보 로드 실패:', error);
        });
}

// 모달 설정 관련 함수들
function saveModalSettings() {
    const title = document.getElementById('modal-title').value;
    const content = document.getElementById('modal-content').value;
    const endDate = document.getElementById('modal-end-date').value;
    
    if (!title.trim() || !content.trim()) {
        alert('제목과 내용을 입력해주세요.');
        return;
    }
    
    if (!endDate) {
        alert('종료일을 설정해주세요.');
        return;
    }
    
    if (new Date(endDate) <= new Date()) {
        alert('종료일은 현재 시간보다 늦어야 합니다.');
        return;
    }
    
    const requestData = {
        title: title,
        body: content,
        expiredAt: endDate
    };
    
    fetch('/admin/api/modal', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        updateCurrentModalPreview(data);
        alert('모달 설정이 저장되었습니다.');
    })
    .catch(error => {
        console.error('모달 저장 실패:', error);
        alert('모달 저장 중 오류가 발생했습니다.');
    });
}

function previewModal() {
    const title = document.getElementById('modal-title').value;
    const content = document.getElementById('modal-content').value;
    const endDate = document.getElementById('modal-end-date').value;
    
    if (!title.trim() || !content.trim()) {
        alert('제목과 내용을 입력해주세요.');
        return;
    }
    
    // 서버에서 fragment를 렌더링해서 실제 홈페이지와 완전히 동일한 모달 미리보기 제공
    const requestData = {
        title: title,
        body: content,
        expiredAt: endDate
    };
    
    fetch('/admin/api/modal/preview', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.text())
    .then(html => {
        const previewWindow = window.open('', 'preview', 'width=600,height=500');
        previewWindow.document.write(html);
        previewWindow.document.close();
    })
    .catch(error => {
        console.error('미리보기 생성 실패:', error);
        alert('미리보기 생성 중 오류가 발생했습니다.');
    });
}

function deleteModal() {
    fetch('/admin/api/modal', {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            // 폼 초기화
            document.getElementById('modal-title').value = '';
            document.getElementById('modal-content').value = '';
            document.getElementById('modal-end-date').value = '';
            
            // 현재 모달 미리보기 숨기기
            const previewContainer = document.getElementById('current-modal-preview');
            if (previewContainer) {
                previewContainer.innerHTML = '<p class="text-muted">설정된 모달이 없습니다.</p>';
            }
            
            alert('모달이 삭제되었습니다.');
        } else {
            throw new Error('모달 삭제 실패');
        }
    })
    .catch(error => {
        console.error('모달 삭제 실패:', error);
        alert('모달 삭제 중 오류가 발생했습니다.');
    });
}

// 현재 모달 미리보기 업데이트
function updateCurrentModalPreview(modalData) {
    const previewContainer = document.getElementById('current-modal-preview');
    if (!previewContainer) return;
    
    if (!modalData) {
        previewContainer.innerHTML = '<p class="text-muted">설정된 모달이 없습니다.</p>';
        return;
    }
    
    const isActive = modalData.isActive;
    const statusClass = isActive ? 'text-success' : 'text-danger';
    const statusText = isActive ? '활성' : '비활성';
    
    previewContainer.innerHTML = `
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h6 class="mb-0">현재 설정된 모달</h6>
                <span class="badge ${isActive ? 'bg-success' : 'bg-secondary'}">${statusText}</span>
            </div>
            <div class="card-body">
                <h5 class="card-title text-primary">${modalData.title}</h5>
                <p class="card-text">${modalData.body.replace(/\n/g, '<br>')}</p>
                <small class="text-muted">만료일: ${new Date(modalData.expiredAt).toLocaleString('ko-KR')}</small>
            </div>
        </div>
    `;
}

function updateCurrentModal(title, content, startDate, endDate, isActive) {
    document.getElementById('current-modal-title').textContent = title;
    document.getElementById('current-modal-content').textContent = content;
    
    const startDateObj = new Date(startDate);
    const endDateObj = new Date(endDate);
    const formatDate = (date) => date.toLocaleDateString('ko-KR');
    
    document.getElementById('current-modal-period').textContent = 
        `${formatDate(startDateObj)} ~ ${formatDate(endDateObj)}`;
    
    const statusElement = document.getElementById('current-modal-status');
    if (isActive) {
        statusElement.textContent = '활성';
        statusElement.className = 'status-active';
    } else {
        statusElement.textContent = '비활성';
        statusElement.className = 'status-inactive';
    }
}

// 모달 설정 이벤트 리스너 초기화
function initializeModalEvents() {
    document.getElementById('save-modal').onclick = function() {
        saveModalSettings();
    };
    document.getElementById('preview-modal').onclick = function() {
        previewModal();
    };
    document.getElementById('delete-modal').onclick = function() {
        if (confirm('모달을 삭제하시겠습니까?')) {
            deleteModal();
        }
    };
}