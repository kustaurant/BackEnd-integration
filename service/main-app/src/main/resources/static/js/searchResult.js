setMouseHover();
function setMouseHover() {
    // 표 위에 마우스 올렸을 때 색상 변경
    document.querySelectorAll("#tierTableBody tr").forEach(function (tr) {
        // 마우스를 올렸을 때
        tr.addEventListener("mouseenter", function () {
            this.style.backgroundColor = "#eee";
            this.querySelectorAll("*").forEach(function (child) {
                child.style.backgroundColor = "#eee";
            });
        });
        // 마우스를 빼앗았을 때
        tr.addEventListener("mouseleave", function () {
            this.style.backgroundColor = "";
            this.querySelectorAll("*").forEach(function (child) {
                child.style.backgroundColor = "";
            });
        });
    });
}