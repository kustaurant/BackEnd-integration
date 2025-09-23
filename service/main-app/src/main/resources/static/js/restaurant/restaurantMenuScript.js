// 메뉴
function fillMenuInfo(data, num) { //num은 처음 표시할 메뉴 개수임. -1일 경우 모든 메뉴 표시
  const menuInfoContainer = document.getElementById('menuInfoContainer');
  menuInfoContainer.innerHTML = '';
  const menuUl = document.createElement('ul');
  menuUl.id = 'menuUL'
  menuUl.classList.add('menu-ul');
  menuInfoContainer.appendChild(menuUl);

  for (var i = 0; i < data.length; i++) {
    const item = data[i];
    const menuLi = document.createElement('li');
    const textDiv = document.createElement('div');
    textDiv.classList.add('menu-text-container');
    const menuNameDiv = document.createElement('div');
    menuNameDiv.classList.add('menu-name');
    menuNameDiv.textContent = item.menuName;
    const menuPriceContainer = document.createElement('div');
    menuPriceContainer.classList.add('menu-price');
    const menuPriceEm = document.createElement('em');
    if (item.menuPrice != undefined) {
      menuPriceEm.textContent = item.menuPrice;
    }
    menuPriceContainer.appendChild(menuPriceEm);
    textDiv.appendChild(menuNameDiv);
    textDiv.appendChild(menuPriceContainer);

    if (item.naverType === 'type1' || item.naverType === 'type3') {
      const imgDiv = document.createElement('div');
      imgDiv.classList.add('menu-img-container');
      const img = document.createElement('img');
      img.alt = 'menu img';
      const menuImgUrl = item.menuImgUrl
      if (menuImgUrl === 'icon') {
        img.setAttribute('src', '/img/favicon.png');
      } else {
        img.setAttribute('src', menuImgUrl);
      }
      menuLi.appendChild(img);
      menuLi.appendChild(textDiv);
    } else if (item.naverType === 'type2' || item.naverType === 'type4') {
      menuLi.appendChild(textDiv);
    } else {
      const nullDiv = document.createElement('div');
      nullDiv.classList.add('menu-name');
      nullDiv.textContent = '메뉴 없음';
      menuLi.appendChild(nullDiv);
    }

    menuUl.appendChild(menuLi);

    if (i + 1 >= num && num !== -1) {
      break;
    }
  }
}

// 메뉴 펼쳤다 접기
const unfoldButton = document.getElementById('menuUnfoldButton');
const menuUL = document.getElementById('menuUL');
if (unfoldButton) {
  let initialMenusHeight;
  if (menuUL) {
    initialMenusHeight = parseFloat(getComputedStyle(menuUL).height);
  }
  unfoldButton.addEventListener('click', function() {
    const windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
    const thisText = this.textContent;
    const menuContainer = document.getElementById('menuContainer');
    if (thisText === '펼치기') {
      fillMenuInfo(restaurantMenus, -1); // 모든 메뉴 표시
      const menuUL = document.getElementById('menuUL');
      this.textContent = '접기';
      let maxHeight
          = windowHeight * 0.55 > initialMenusHeight + 70 ? windowHeight * 0.55 : initialMenusHeight + 70;
      menuUL.style.maxHeight = maxHeight + 'px';
      menuUL.style.overflowY = 'scroll';
    } else {
      fillMenuInfo(restaurantMenus, initialDisplayMenuCount);
      const menuUL = document.getElementById('menuUL');
      this.textContent = '펼치기';
      menuUL.style.maxHeight = 'none';
      menuUL.style.overflowY = 'visible';
    }
    document.getElementById('menuTopDiv').scrollIntoView({ behavior: 'smooth', block: 'start' });
  })
}