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

    loadTabData(tabName) {
        // 각 탭이 활성화될 때 필요한 데이터 로드나 초기화 작업
        switch(tabName) {
            case 'dashboard':
                // 신규 유저 및 전체 유저 실제 데이터 로드
                loadNewUsers(0);
                loadAllUsers(0);
                break;
            case 'restaurants':
                // 실제 음식점 데이터 로드
                loadRestaurants(0);
                break;
            case 'feedback':
                // 실제 피드백 데이터 로드
                loadFeedbacks(0);
                break;
            case 'modal':
                // 현재 모달 정보 로드
                loadCurrentModal();
                break;
            case 'reports':
                // 신고 관리는 아직 구현되지 않음
                console.log('신고 관리 기능은 추후 구현 예정입니다.');
                break;
        }
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 탭 관리자 초기화
    const tabManager = new TabManager();

    // 대시보드 초기 데이터 설정
    updateDashboardStats();

    // 크롤링 버튼 클릭 이벤트
    document.getElementById('insta-crawl').onclick = function() {
        alert('인스타그램 제휴 음식점 정보 채우기 기능은 추후 구현됩니다.');
    };
    document.getElementById('sync-crawl').onclick = function() {
        alert('월간 음식점 정보 싱크 기능은 추후 구현됩니다.');
    };
    
    // 모달 설정 이벤트 초기화
    initializeModalEvents();
});