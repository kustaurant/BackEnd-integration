function fillMenuInfo(data, num) {
  const menuInfoContainer = document.getElementById("menuInfoContainer");
  if (!menuInfoContainer) return;

  menuInfoContainer.innerHTML = "";
  const menuUl = document.createElement("ul");
  menuUl.id = "menuUL";
  menuUl.classList.add("menu-ul");
  menuInfoContainer.appendChild(menuUl);

  const menus = Array.isArray(data) ? data : [];
  for (let i = 0; i < menus.length; i++) {
    const item = menus[i] || {};
    const menuLi = document.createElement("li");

    const textDiv = document.createElement("div");
    textDiv.classList.add("menu-text-container");

    const menuNameDiv = document.createElement("div");
    menuNameDiv.classList.add("menu-name");
    menuNameDiv.textContent = item.menuName || "";

    const menuPriceContainer = document.createElement("div");
    menuPriceContainer.classList.add("menu-price");
    const menuPriceEm = document.createElement("em");
    menuPriceEm.textContent = item.menuPrice || "";
    menuPriceContainer.appendChild(menuPriceEm);

    textDiv.appendChild(menuNameDiv);
    textDiv.appendChild(menuPriceContainer);

    const menuImgUrl = (item.menuImgUrl || "").trim();
    if (menuImgUrl) {
      const imgDiv = document.createElement("div");
      imgDiv.classList.add("menu-img-container");
      const img = document.createElement("img");
      img.alt = "menu img";
      img.src = menuImgUrl === "icon" ? "/img/favicon.png" : menuImgUrl;
      imgDiv.appendChild(img);
      menuLi.appendChild(imgDiv);
    }

    menuLi.appendChild(textDiv);
    menuUl.appendChild(menuLi);

    if (i + 1 >= num && num !== -1) {
      break;
    }
  }
}

const unfoldButton = document.getElementById("menuUnfoldButton");
const initialMenuUl = document.getElementById("menuUL");
if (unfoldButton) {
  let initialMenusHeight = 0;
  if (initialMenuUl) {
    initialMenusHeight = parseFloat(getComputedStyle(initialMenuUl).height);
  }

  unfoldButton.dataset.expanded = "false";
  unfoldButton.textContent = "펼치기";

  unfoldButton.addEventListener("click", function () {
    const expanded = this.dataset.expanded === "true";
    if (!expanded) {
      fillMenuInfo(restaurantMenus, -1);
      const menuUl = document.getElementById("menuUL");
      this.dataset.expanded = "true";
      this.textContent = "접기";
      const windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
      const maxHeight = windowHeight * 0.55 > initialMenusHeight + 70 ? windowHeight * 0.55 : initialMenusHeight + 70;
      if (menuUl) {
        menuUl.style.maxHeight = maxHeight + "px";
        menuUl.style.overflowY = "scroll";
      }
    } else {
      fillMenuInfo(restaurantMenus, initialDisplayMenuCount);
      const menuUl = document.getElementById("menuUL");
      this.dataset.expanded = "false";
      this.textContent = "펼치기";
      if (menuUl) {
        menuUl.style.maxHeight = "none";
        menuUl.style.overflowY = "visible";
      }
    }
    document.getElementById("menuTopDiv")?.scrollIntoView({ behavior: "smooth", block: "start" });
  });
}
