function getRestaurantStatusText(status) {
    const statusMap = {
        active: "운영중",
        inactive: "영업종료",
        closed: "폐업",
        pending: "확인대기"
    };
    return statusMap[status] || status;
}

function getRestaurantStatusClass(status) {
    const classMap = {
        active: "active",
        inactive: "inactive",
        closed: "closed",
        pending: "pending"
    };
    return classMap[status] || "unknown";
}

function renderRestaurantsTable(restaurants) {
    const tbody = document.getElementById("restaurants-tbody");
    if (!tbody) return;
    tbody.innerHTML = "";

    if (!restaurants || restaurants.length === 0) {
        tbody.innerHTML = "<tr><td colspan=\"8\">식당 정보가 없습니다.</td></tr>";
        return;
    }

    restaurants.forEach(restaurant => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${restaurant.restaurantId}</td>
            <td>${restaurant.restaurantName}</td>
            <td>${restaurant.restaurantAddress}</td>
            <td>${restaurant.restaurantPosition}</td>
            <td>${restaurant.restaurantType || "-"}</td>
            <td><span class="evaluation-count">${restaurant.restaurantEvaluationCount || 0}</span></td>
            <td><span class="restaurant-status ${getRestaurantStatusClass(restaurant.status)}">${getRestaurantStatusText(restaurant.status)}</span></td>
            <td>${formatDateTime(restaurant.createdAt)}</td>
        `;
        tbody.appendChild(row);
    });
}

function loadRestaurants(page = 0) {
    fetch(`/admin/api/restaurants?page=${page}&size=20`)
        .then(response => response.json())
        .then(data => {
            renderRestaurantsTable(data.restaurants);
            renderPagination(data, "restaurants", loadRestaurants);

            const total = document.getElementById("restaurants-total");
            if (total) {
                total.textContent = `총 ${data.totalElements.toLocaleString()}개`;
            }
        })
        .catch(error => {
            console.error("식당 데이터 로드 실패:", error);
            const tbody = document.getElementById("restaurants-tbody");
            if (tbody) {
                tbody.innerHTML = "<tr><td colspan=\"8\">데이터 로드 실패</td></tr>";
            }
        });
}
