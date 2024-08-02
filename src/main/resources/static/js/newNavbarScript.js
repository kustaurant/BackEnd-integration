const myPageToggleButton = document.getElementById('myPageToggleButton');
if (myPageToggleButton) {
    myPageToggleButton.addEventListener('click', function() {
        toggleMyPageButton()
    });
}const myPageToggleBackdrop = document.getElementById('myPageToggleBackdrop');
if (myPageToggleBackdrop) {
    myPageToggleBackdrop.addEventListener('click', function() {
        toggleMyPageButton()
    });
}
function toggleMyPageButton() {
    var toggleDiv = document.getElementById('myPageToggleDiv');
    var toggleBackdrop = document.getElementById('myPageToggleBackdrop');
    if (toggleDiv.style.display === 'none') {
        toggleDiv.style.display = 'block';
        toggleBackdrop.style.display = 'block';
    } else {
        toggleDiv.style.display = 'none';
        toggleBackdrop.style.display = 'none';
    }
}