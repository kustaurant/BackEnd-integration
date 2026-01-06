// [[ AI TIER 표시 ]]
const PARAM_AI = 'ai';

$(document).ready(function () {
    aiTier();

    // --------------- 클릭된 종류 버튼 효과 ----------------------------
    const PARAMS = ["cuisines", "situations", "locations", "ai"];

    function getList(sp, key) {
        const v = sp.get(key);
        if (!v) return [];
        return v.split(",").map(s => s.trim()).filter(Boolean);
    }

    function setList(sp, key, arr) {
        // page 제거
        sp.delete('page');
        // ALL(or 빈 배열) == 조건 제거
        if (!arr || arr.length === 0 || arr.includes("ALL")) {
            sp.delete(key);
        } else {
            sp.set(key, arr.join(","));
        }
    }

    function normalizeAi(sp) {
        if (sp.get("ai") !== "true") sp.delete("ai");
    }

    function toggle(list, value, { exclusiveJH = false } = {}) {
        // cuisines 전용: JH는 단독
        if (exclusiveJH && value === "JH") return ["JH"];
        // ALL 선택은 제거(=조건 없음)
        if (value === "ALL") return [];
        const has = list.includes(value);
        if (has) return list.filter(x => x !== value);
        // 기존에 ALL이나 JH가 있으면 교체
        if (exclusiveJH && list.includes("JH")) return [value];
        if (list.includes("ALL")) return [value];
        return [...list, value];
    }

    document.querySelectorAll(".category").forEach(btn => {
        btn.addEventListener("click", () => {
            const url = new URL(window.location.href);
            const sp = url.searchParams;

            // 현재 상태 읽기
            let cuisines = getList(sp, "cuisines");
            let situations = getList(sp, "situations");
            let locations = getList(sp, "locations");

            if (btn.dataset.cuisine) {
                const v = btn.dataset.cuisine;
                cuisines = toggle(cuisines, v, { exclusiveJH: true });
                setList(sp, "cuisines", cuisines);

            } else if (btn.dataset.situation) {
                const v = btn.dataset.situation;
                situations = toggle(situations, v);
                setList(sp, "situations", situations);

            } else if (btn.dataset.location) {
                const v = btn.dataset.location;
                locations = toggle(locations, v);
                setList(sp, "locations", locations);
            }

            // ai 정리: true만 유지
            normalizeAi(sp);

            // 최종 이동
            window.location.href = url.toString();
        });
    });

// pc에서도 카테고리의 가로 스크롤을 마우스 드래그로 할 수 있게 해주는 부분
    var scrollableElements = document.querySelectorAll('.scrollable');
    scrollableElements.forEach(function (scrollableElement) {
        var isMouseDown = false;
        var startX, scrollLeft;

        scrollableElement.addEventListener('mousedown', function (e) {
            isMouseDown = true;
            startX = e.pageX - scrollableElement.offsetLeft;
            scrollLeft = scrollableElement.scrollLeft;
        });

        scrollableElement.addEventListener('mouseleave', function () {
            isMouseDown = false;
        });

        scrollableElement.addEventListener('mouseup', function () {
            isMouseDown = false;
        });

        scrollableElement.addEventListener('mousemove', function (e) {
            if (!isMouseDown) return;
            e.preventDefault();
            var x = e.pageX - scrollableElement.offsetLeft;
            var walk = (x - startX) * 1.5; // 스크롤 속도 조절을 위한 계수
            scrollableElement.scrollLeft = scrollLeft - walk;
        });
    });

    setMouseHover();
    function setMouseHover() {
        // 표 위에 마우스 올렸을 때 색상 변경
        document.querySelectorAll("#tierTableBody tr.tr-main").forEach(function (tr) {
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

    function aiTier() {
        // [[ AI TIER 표시 ]]
        const checkbox = document.getElementById('aiTierCheckbox');
        // 체크박스 이벤트 리스너
        checkbox.addEventListener('change', function () {
            const next = new URL(window.location.href);
            const params = next.searchParams;

            // page 제거 -> 0 페이지로 이동하도록
            params.delete('page');

            // aiTier 토글
            if (this.checked) {
                params.set(PARAM_AI, 'true');
            } else {
                params.delete(PARAM_AI);
            }

            window.location.href = next.toString();
        });
    }
});
