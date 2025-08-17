/**
 * 커뮤니티 게시글 페이지 메인 스크립트
 * 
 * 모듈화된 기능들을 초기화하고 관리합니다.
 */
document.addEventListener('DOMContentLoaded', function () {
    // 모듈 초기화
    const postReactions = new PostReactions();
    const commentManager = new CommentManager();
    const commentReactions = new CommentReactions();
    const modalManager = new ModalManager(commentManager);

    console.log('Community post page initialized with modular architecture');
});