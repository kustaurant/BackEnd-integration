// 데이터 세팅
var tieredRestaurants = mapData.tieredRestaurants;
var nonTieredRestaurants  = mapData.nonTieredRestaurants;
var favoriteRestaurants = mapData.favoriteRestaurants;
var favoriteIds = favoriteRestaurants.map(restaurant => restaurant.restaurantId)
var visibleBounds = mapData.visibleBounds;

// 네이버 지도
var map = new naver.maps.Map('map', {
    minZoom: 13,
});

// 초기 보이는 영역 조정
const southWest = new naver.maps.LatLng(visibleBounds[2], visibleBounds[0]); // 남서쪽(좌하단)
const northEast = new naver.maps.LatLng(visibleBounds[3], visibleBounds[1]); // 북동쪽(우상단)
const bounds = new naver.maps.LatLngBounds(southWest, northEast);

// [[ 지도 열기 버튼 클릭 시 ]]
const mapArea = document.getElementById('mapArea');
let isMapOpen = false;
document.getElementById('mapOpenButton').addEventListener('click', function() {
    isMapOpen = true;
    history.pushState({}, '');
    body.classList.add('prevent-scroll');
    mapArea.style.display = 'flex';

    resizeMap();
    map.fitBounds(bounds);
    resetPolygons();
    updateMarkers(map);
});
// [[ 지도 닫기 버튼 클릭 시 ]]
document.getElementById('mapCloseButton').addEventListener('click', function() {
    history.back();
})
// 지도가 열려 있을 때 뒤로가기 = 지도 닫기
window.onpopstate = function() {
    if (isMapOpen) {
        closeMap();
    }
}
function closeMap() {
    isMapOpen = false;
    body.classList.remove('prevent-scroll');
    mapArea.style.display = 'none';
}
// [[ 창 크기 변경 ]]
function resizeMap(){
    let screenWidth = window.innerWidth;
    let screenHeight = window.innerHeight;
    var Size = new naver.maps.Size(screenWidth - 10, screenHeight - 10);
    map.setSize(Size);
}
window.addEventListener('resize', resizeMap);

// 마커와 정보창 전역 변수
var tieredMarkers = [];
var tieredInfoWindows = [];
var tierRestaurantCount = 0; // 즐찾을 제외한 티어가 있는 식당 마커 개수
var nonTieredMarkersMap = new Map();
var nonTieredInfoWindowsMap = new Map();

// [[ 마커 및 정보창 생성 ]]
// 1. 즐찾 마커/정보창 생성
var favoriteRestaurantMarkers = [];
var favoriteRestaurantInfoWindows = [];
for (const restaurant of favoriteRestaurants) {
    let marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(restaurant.latitude, restaurant.longitude),
        icon: {
            url: '/img/tier/mapStar.png',
            size: new naver.maps.Size(28, 28),
            scaledSize: new naver.maps.Size(28, 28),
            origin: new naver.maps.Point(0, 0),
            anchor: new naver.maps.Point(14, 28)
        },
        zIndex: 9950
    });
    favoriteRestaurantMarkers.push(marker);
    // info window
    var restaurantImgUrl = restaurant.restaurantImgUrl;
    // 티어 있는 경우
    if (restaurant.mainTier !== -1) {
        var infoWindow = new naver.maps.InfoWindow({
            content:
                `<a class="map-info-window" href="/restaurants/${restaurant.restaurantId}">` +
                    '<div class="info-window-info">' +
                        `<img src="${restaurantImgUrl}">` +
                        '<div>' +
                            `<span class="info-window-name">${restaurant.restaurantName}</span>` +
                            `<span class="info-window-type">${restaurant.restaurantType}</span>` +
                        '</div>' +
                    '</div>' +
                    '<div class="info-window-main-tier">' +
                        '<span>메인 티어</span>' +
                        `<img src="/img/tier/${restaurant.mainTier}tier.png">` +
                    '</div>' +
                '</a>',
            zIndex: 9999
        });
    } else { // 티어가 없는 경우
        var infoWindow = new naver.maps.InfoWindow({
            content:
                `<a class="map-info-window" href="/restaurants/${restaurant.restaurantId}">` +
                    '<div class="info-window-info">' +
                        `<img src="${restaurantImgUrl}">` +
                        '<div>' +
                            `<span class="info-window-name">${restaurant.restaurantName}</span>` +
                            `<span class="info-window-type">${restaurant.restaurantType}</span>` +
                        '</div>' +
                    '</div>' +
                '</a>',
            zIndex: 9999
        });
    }
    favoriteRestaurantInfoWindows.push(infoWindow);
}

// 2. 티어 있는 식당 마커/정보창 생성
for (const restaurant of tieredRestaurants) {
    var marker;
    let mainTier = restaurant.mainTier;
    // 즐겨찾기에서 이미 마커를 생성한 경우는 티어 마커를 생성 안함.
    if (favoriteIds?.includes(restaurant.restaurantId)) {
        continue;
    }

    marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(restaurant.latitude, restaurant.longitude),
        icon: {
            url: `/img/tier/${mainTier}tier.png`, // 티어 이미지 url
            size: new naver.maps.Size(24, 24),
            scaledSize: new naver.maps.Size(24, 24),
            origin: new naver.maps.Point(0, 0),
            anchor: new naver.maps.Point(12, 24)
        },
        zIndex: 9950 - mainTier // 티어가 높을 수록 앞으로 오도록 함
    });
    tierRestaurantCount++;
    // info window
    var restaurantImgUrl = restaurant.restaurantImgUrl;
    var infoWindow = new naver.maps.InfoWindow({
        content:
            `<a class="map-info-window" href="/restaurants/${restaurant.restaurantId}">` +
                '<div class="info-window-info">' +
                    `<img src="${restaurantImgUrl}">`+
                    '<div>' +
                        `<span class="info-window-name">${restaurant.restaurantName}</span>` +
                        `<span class="info-window-type">${restaurant.restaurantType}</span>` +
                    '</div>' +
                '</div>' +
                '<div class="info-window-main-tier">' +
                    '<span>메인 티어</span>' +
                    `<img src="/img/tier/${restaurant.mainTier}tier.png">` +
                '</div>' +
            '</a>',
        zIndex: 9999 - mainTier
    });
    tieredInfoWindows.push(infoWindow);
    tieredMarkers.push(marker);
}
// 3. 티어 없는 식당 마커/정보창 생성
for (const restaurantAndZoom of nonTieredRestaurants) {
    const zoom = restaurantAndZoom.zoom;
    var markers = [];
    var infoWindows = [];
    for (const restaurant of restaurantAndZoom.restaurants) {
        var marker;
        // 즐겨찾기에서 이미 마커를 생성한 경우는 티어 마커를 생성 안함.
        if (favoriteIds?.includes(restaurant.restaurantId)) {
            continue;
        }

        marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(restaurant.latitude, restaurant.longitude),
            icon: {
                url: '/img/tier/pin.png', // 지도 핀 이미지 url
                size: new naver.maps.Size(20, 20),
                scaledSize: new naver.maps.Size(20, 20),
                origin: new naver.maps.Point(0, 0),
                anchor: new naver.maps.Point(10, 20)
            },
            zIndex: 9950 - 6
        });
        // info window
        var restaurantImgUrl = restaurant.restaurantImgUrl;
        var infoWindow = new naver.maps.InfoWindow({
            content:
                `<a class="map-info-window" href="/restaurants/${restaurant.restaurantId}">` +
                    '<div class="info-window-info">' +
                        `<img src="${restaurantImgUrl}">`+
                        '<div>' +
                            `<span class="info-window-name">${restaurant.restaurantName}</span>` +
                            `<span class="info-window-type">${restaurant.restaurantType}</span>` +
                        '</div>' +
                    '</div>' +
                '</a>',
            zIndex: 9999 - 6
        });
        markers.push(marker);
        infoWindows.push(infoWindow);
    }
    nonTieredMarkersMap.set(zoom, markers);
    nonTieredInfoWindowsMap.set(zoom, infoWindows);
}

// 4. 마커에 이벤트 핸들러 붙이기
// 티어 있는 식당 마커
attachClickHandlers(tieredMarkers, tieredInfoWindows);
// 즐찾 식당 마커
attachClickHandlers(favoriteRestaurantMarkers, favoriteRestaurantInfoWindows);
// 티어 없는 식당 마커
for (const [zoom, markers] of nonTieredMarkersMap) {
    const infoWindows = nonTieredInfoWindowsMap.get(zoom) ?? [];
    attachClickHandlers(markers, infoWindows);
}

function attachClickHandlers(markers, infoWindows) {
    for (let i = 0; i < markers.length; i++) {
        const marker = markers[i];
        const infoWindow = infoWindows[i];
        if (!marker || !infoWindow) continue;

        naver.maps.Event.addListener(marker, 'click', () => {
            if (infoWindow.getMap()) infoWindow.close();
            else infoWindow.open(map, marker);
        });
    }
}

// [[ 마커와 지도 연결 ]]
// 이벤트 등록
naver.maps.Event.addListener(map, 'init', () => updateMarkers(map));
naver.maps.Event.addListener(map, 'zoom_changed', () => updateMarkers(map));
naver.maps.Event.addListener(map, 'dragend', () => updateMarkers(map));
naver.maps.Event.addListener(map, 'idle', () => updateMarkers(map));

function updateMarkers(map) {
    var mapBounds = map.getBounds();
    var curZoom = map.getZoom();
    // 즐찾 식당
    for (const marker of favoriteRestaurantMarkers) {
        if (mapBounds.hasLatLng(marker.getPosition())) {
            showMarker(map, marker);
        } else {
            hideMarker(map, marker);
        }
    }
    // 티어 있는 식당
    for (const marker of tieredMarkers) {
        if (mapBounds.hasLatLng(marker.getPosition())) {
            showMarker(map, marker);
        } else {
            hideMarker(map, marker);
        }
    }
    // 티어 없는 식당
    for (const [zoom, markers] of nonTieredMarkersMap) {
        if (zoom > curZoom) {
            for (const marker of markers) {
                hideMarker(map, marker);
            }
        } else {
            for (const marker of markers) {
                if (mapBounds.hasLatLng(marker.getPosition())) {
                    showMarker(map, marker);
                } else {
                    hideMarker(map, marker);
                }
            }
        }
    }
}
function showMarker(map, marker) {
    if (marker.getMap()) return;
    marker.setMap(map);
}
function hideMarker(map, marker) {
    if (!marker.getMap()) return;
    marker.setMap(null);
}

// [[ 폴리곤 생성 ]]
const solidPolygonCoordsList = mapData.solidPolygonCoordsList;
const dashedPolygonCoordsList = mapData.dashedPolygonCoordsList;
let polygons = [];

function resetPolygons() {
    removePolygons()

    for (const coords of solidPolygonCoordsList) {
        const path = coords.map(c => new naver.maps.LatLng(c.x, c.y));

         polygons.push(new naver.maps.Polygon({
            map,
            paths: [path],
            fillColor: '#008000',
            fillOpacity: 0.2,
            strokeColor: '#008000',
            strokeOpacity: 0.7,
            strokeWeight: 3
        }));
    }
    for (const coords of dashedPolygonCoordsList) {
        const path = coords.map(c => new naver.maps.LatLng(c.x, c.y));

        polygons.push(new naver.maps.Polygon({
            map,
            paths: [path],
            fillColor: '#008000',
            fillOpacity: 0.08,
            strokeColor: '#008000',
            strokeOpacity: 0.3,
            strokeWeight: 2,
            strokeStyle: 'longdash'
        }));
    }
}

function removePolygons() {
    for (const polygon of polygons) {
        polygon.setMap(null);
    }
}
