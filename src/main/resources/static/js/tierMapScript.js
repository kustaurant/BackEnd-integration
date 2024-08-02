//const body = document.getElementsByTagName('body')[0];
var latitude = parseFloat(mapInfo.getAttribute('data-latitude'));
var longitude = parseFloat(mapInfo.getAttribute('data-longitude'));
var mapZoom = parseInt(mapInfo.getAttribute('data-zoom'));
var restaurantList = JSON.parse(mapInfo.getAttribute('data-restaurantList'));
// 즐찾한 식당 id가 들어있는 리스트
var favoriteRestaurantList = JSON.parse(mapInfo.getAttribute('data-favoriteRestaurantIdList'));
var favoriteRestaurantIdList = favoriteRestaurantList?.map(element => element.restaurantId);
// 네이버 지도
var map = new naver.maps.Map('map', {
    center: new naver.maps.LatLng(latitude, longitude),
    zoom: mapZoom,
    minZoom: 13,
});

var markers = [];
var infoWindows = [];
var tierRestaurantCount = 0; // 즐찾을 제외한 티어가 있는 식당 마커 개수
var bounds = map.getBounds(), // 마커가 현재 화면에서만 표시되도록 하기 위함.
    southWest = bounds.getSW(),
    northEast = bounds.getNE(),
    lngSpan = northEast.lng() - southWest.lng(),
    latSpan = northEast.lat() - southWest.lat();

// 마커 생성
// 즐찾 마커 생성
var favoriteRestaurantMarkers = [];
var favoriteRestaurantInfoWindows = [];
if (favoriteRestaurantList) {
    for (var i = 0; i < favoriteRestaurantList.length; i++) {
        let restaurant = favoriteRestaurantList[i];
        let marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(restaurant.restaurantLatitude, restaurant.restaurantLongitude),
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
        var restaurantImgUrl = restaurant.restaurantImgUrl === 'no_img' ?
            '/img/tier/no_img.png' : restaurant.restaurantImgUrl;
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
} else { // 로그인 안돼서 즐찾 정보가 안 넘어온 경우 임시로 빈 리스트
    favoriteRestaurantList = [];
}
// 일반 마커 생성
for (var i = 0; i < restaurantList.length; i++) {
    let restaurant = restaurantList[i].restaurant;
    var marker;
    let mainTier = restaurant.mainTier;
    if (favoriteRestaurantIdList?.includes(restaurant.restaurantId)) { // 즐겨찾기에서 이미 마커를 생성한 경우는 티어 마커를 생성 안함.
        continue;
    }
    // 티어가 있는 경우
    if (mainTier !== -1) {
        marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(restaurant.restaurantLatitude, restaurant.restaurantLongitude),
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
        var restaurantImgUrl = restaurant.restaurantImgUrl === 'no_img' ?
            '/img/tier/no_img.png' : restaurant.restaurantImgUrl;
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
        infoWindows.push(infoWindow);
    } else { // 티어가 없는 경우
        marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(restaurant.restaurantLatitude, restaurant.restaurantLongitude),
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
        var restaurantImgUrl = restaurant.restaurantImgUrl === 'no_img' ?
            '/img/tier/no_img.png' : restaurant.restaurantImgUrl;
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
        infoWindows.push(infoWindow);
    }
    markers.push(marker);
}
// 티어 있는 식당 마커 지도와 연결 = 지도에 표시
for (var i = 0; i< tierRestaurantCount; i++) {
    showMarker(map, markers[i]);
}
// 즐찾 식당 마커 지도와 연결 = 지도에 표시
favoriteRestaurantMarkers.forEach(element => showMarker(map, element));
// info window 이벤트 핸들러
function getClickHandler(seq, markerList, infoWindowList) {
    return function(e) {
        var marker = markerList[seq],
            infoWindow = infoWindowList[seq];

        if (infoWindow.getMap()) {
            infoWindow.close();
        } else {
            infoWindow.open(map, marker);
        }
    }
}
for (var i=0, ii=markers.length; i<ii; i++) {
    naver.maps.Event.addListener(markers[i], 'click', getClickHandler(i, markers, infoWindows));
}
for (var i=0, ii=favoriteRestaurantMarkers.length; i<ii; i++) {
    naver.maps.Event.addListener(favoriteRestaurantMarkers[i], 'click', getClickHandler(i, favoriteRestaurantMarkers, favoriteRestaurantInfoWindows));
}

// 화면 이동이나 확대, 축소 후 마커 표시되는것 달라지게
naver.maps.Event.addListener(map, 'idle', function() {
    updateMarkers(map, markers);
});
function updateMarkers(map, markers) {

    var mapBounds = map.getBounds();

    for (var i = 0; i < markers.length; i++) {
        let zoom = map.getZoom();
        let marker = markers[i];
        let position = marker.getPosition();
        if (zoom < 16) { // zoom이 17보다 작을 경우 - 티어 있는 식당만 표시
            if (mapBounds.hasLatLng(position) && i < tierRestaurantCount) {
                showMarker(map, marker);
            } else {
                hideMarker(map, marker);
            }
        } else if (zoom === 16) { // zoom이 17일 경우 - 티어 없는 것 25% 표시
            if (mapBounds.hasLatLng(position) && (i < tierRestaurantCount || i % 10 === 0)) {
                showMarker(map, marker);
            } else {
                hideMarker(map, marker);
            }
        } else if (zoom === 17) { // zoom이 17일 경우 - 티어 없는 것 25% 표시
            if (mapBounds.hasLatLng(position) && (i < tierRestaurantCount || i % 7 === 0)) {
                showMarker(map, marker);
            } else {
                hideMarker(map, marker);
            }
        } else if (zoom === 18) { // zoom이 17일 경우 - 티어 없는 것 25% 표시
            if (mapBounds.hasLatLng(position) && (i < tierRestaurantCount || i % 4 === 0)) {
                showMarker(map, marker);
            } else {
                hideMarker(map, marker);
            }
        } else {
            if (mapBounds.hasLatLng(position)) { // zoom이 17보다 큰 경우 - 전부 표시
                showMarker(map, marker);
            } else {
                hideMarker(map, marker);
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


// 지도 열기 버튼
const mapArea = document.getElementById('mapArea');
let isMapOpen = false;
document.getElementById('mapOpenButton').addEventListener('click', function() {
    isMapOpen = true;
    history.pushState({}, '');
    body.classList.add('prevent-scroll');
    mapArea.style.display = 'flex';
    resizeMap();
    setMapPolygon();
});
// 지도가 열려있을 경우 뒤로가기 동작 지도가 닫히는 동작으로 대체
window.onpopstate = function() {
    if (isMapOpen) {
        closeMap();
    }
}
// 지도 닫기 버튼
document.getElementById('mapCloseButton').addEventListener('click', function() {
    history.back();
})
function closeMap() {
    isMapOpen = false;
    body.classList.remove('prevent-scroll');
    mapArea.style.display = 'none';
}
// 네이버 지도 리사이즈
function resizeMap(){
    let screenWidth = window.innerWidth;
    let screenHeight = window.innerHeight;
    var Size = new naver.maps.Size(screenWidth - 10, screenHeight - 10);
    map.setSize(Size);
}
window.addEventListener('resize', resizeMap);

// 지도 폴리곤 생성
// 인덱스 0번.전체 | 1번.건입~중문 | 2번.중문~어대 | 3번.후문 | 4번.정문 | 5번.구의역
var positionIndex = parseInt(mapInfo.getAttribute('data-positionIndex'));
var polygon1 = new naver.maps.Polygon({
    paths: [
        [
            new naver.maps.LatLng(37.5401732,127.062852),
            new naver.maps.LatLng(37.5378977,127.0696049),
            new naver.maps.LatLng(37.5421627,127.071636),
            new naver.maps.LatLng(37.5427753,127.0710213),
            new naver.maps.LatLng(37.5422156,127.0707644),
            new naver.maps.LatLng(37.5441201,127.0651452)
        ]
    ],
    fillColor: '#008000',
    fillOpacity: 0.15,
    strokeColor: '#008000',
    strokeOpacity: 0.5,
    strokeWeight: 3
});
var polygon2 = new naver.maps.Polygon({
    paths: [
        [
            new naver.maps.LatLng(37.5421627,127.071636),
            new naver.maps.LatLng(37.5427753,127.0710213),
            new naver.maps.LatLng(37.5422156,127.0707644),
            new naver.maps.LatLng(37.5441201,127.0651452),
            new naver.maps.LatLng(37.5482696,127.0674957),
            new naver.maps.LatLng(37.5478196,127.0716092),
            new naver.maps.LatLng(37.5472574,127.0740324),
            new naver.maps.LatLng(37.5459136,127.0733675)
        ]
    ],
    fillColor: '#008000',
    fillOpacity: 0.15,
    strokeColor: '#008000',
    strokeOpacity: 0.5,
    strokeWeight: 3
});
var polygon3 = new naver.maps.Polygon({
    paths: [
        [
            new naver.maps.LatLng(37.5445367,127.0728555),
            new naver.maps.LatLng(37.5444815,127.0731477),
            new naver.maps.LatLng(37.5447132,127.0739129),
            new naver.maps.LatLng(37.5445797,127.0747749),
            new naver.maps.LatLng(37.544736,127.0754595),
            new naver.maps.LatLng(37.5445765,127.0755668),
            new naver.maps.LatLng(37.5449818,127.0800863),
            new naver.maps.LatLng(37.545327,127.0799778),
            new naver.maps.LatLng(37.5453925,127.0793721),
            new naver.maps.LatLng(37.5458133,127.0773484),
            new naver.maps.LatLng(37.547219,127.0741961)
        ]
    ],
    fillColor: '#008000',
    fillOpacity: 0.15,
    strokeColor: '#008000',
    strokeOpacity: 0.5,
    strokeWeight: 3
});
var polygon4 = new naver.maps.Polygon({
    paths: [
        [
            new naver.maps.LatLng(37.5397225,127.0708216),
            new naver.maps.LatLng(37.5385701,127.0750329),
            new naver.maps.LatLng(37.5393212,127.0752326),
            new naver.maps.LatLng(37.5392034,127.0769366),
            new naver.maps.LatLng(37.5390193,127.0771502),
            new naver.maps.LatLng(37.5387767,127.0798368),
            new naver.maps.LatLng(37.540182,127.0827185),
            new naver.maps.LatLng(37.5400757,127.0831523),
            new naver.maps.LatLng(37.5369981,127.083432),
            new naver.maps.LatLng(37.5360015,127.0837294),
            new naver.maps.LatLng(37.5358341,127.0827587),
            new naver.maps.LatLng(37.5359128,127.0788435),
            new naver.maps.LatLng(37.5390731,127.0704382)
        ]
    ],
    fillColor: '#008000',
    fillOpacity: 0.15,
    strokeColor: '#008000',
    strokeOpacity: 0.5,
    strokeWeight: 3
});
var polygon5 = new naver.maps.Polygon({
    paths: [
        [
            new naver.maps.LatLng(37.536197,127.0837349),
            new naver.maps.LatLng(37.5370672,127.0876883),
            new naver.maps.LatLng(37.538271,127.0871438),
            new naver.maps.LatLng(37.5387071,127.0865054),
            new naver.maps.LatLng(37.5397109,127.0864035),
            new naver.maps.LatLng(37.5396575,127.0832533),
            new naver.maps.LatLng(37.5384175,127.0833418),
            new naver.maps.LatLng(37.5373818,127.0834571),
            new naver.maps.LatLng(37.5365395,127.0835778)
        ]
    ],
    fillColor: '#008000',
    fillOpacity: 0.15,
    strokeColor: '#008000',
    strokeOpacity: 0.5,
    strokeWeight: 3
});
function setMapPolygon() {
    if (positionIndex === 0) {
        polygon1.setMap(map);
        polygon2.setMap(map);
        polygon3.setMap(map);
        polygon4.setMap(map);
        polygon5.setMap(map);
    } else if (positionIndex === 1) {
        polygon1.setMap(map);
    } else if (positionIndex === 2) {
        polygon2.setMap(map);
    } else if (positionIndex === 3) {
        polygon3.setMap(map);
    } else if (positionIndex === 4) {
        polygon4.setMap(map);
    } else if (positionIndex === 5) {
        polygon5.setMap(map);
    }
}
