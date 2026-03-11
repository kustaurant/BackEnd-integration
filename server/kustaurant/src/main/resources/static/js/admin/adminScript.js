// 메인 관리자 스크립트 - TabManager와 초기화 담당

// 탭 전환 기능
class TabManager {
    constructor() {
        this.currentTab = 'dashboard';
        this.init();
    }

    init() {
        // 탭 버튼 이벤트 리스너 등록
        const tabButtons = document.querySelectorAll('.tab-button');
        tabButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const tabName = e.target.getAttribute('data-tab');
                this.switchTab(tabName);
            });
        });
    }

    switchTab(tabName) {
        // 현재 활성 탭과 버튼 비활성화
        document.querySelector('.tab-button.active').classList.remove('active');
        document.querySelector('.tab-content.active').classList.remove('active');

        // 새로운 탭과 버튼 활성화
        document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
        document.getElementById(`${tabName}-tab`).classList.add('active');

        this.currentTab = tabName;

        // 탭별 데이터 로드 (필요시)
        this.loadTabData(tabName);
    }

    loadTabData(tabName) { // 각 탭이 활성화될 때 필요한 데이터 로드나 초기화 작업
        switch(tabName) {
            case 'dashboard':
                loadNewUsers(0);
                loadAllUsers(0);
                break;
            case 'restaurants':
                loadRestaurants(0);
                loadPartnerships(0);
                break;
            case 'feedback':
                loadFeedbacks(0);
                break;
            case 'modal':
                loadCurrentModal();
                break;
            case 'reports':
                console.log('신고 관리 기능은 추후 구현 예정입니다.');
                break;
        }
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    const tabManager = new TabManager();
    updateDashboardStats();
    initializeModalEvents();
});