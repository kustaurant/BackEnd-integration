
<p align="middle" >
  <img src="https://github.com/user-attachments/assets/1a4e00c9-f37c-46b9-8c1e-22018fa60768" alt="kustaurant-logo" width="320" height="80">
</p>
<p align="middle" >건국대학교 대표 맛집 확인서비스</p>
<p align="middle" ><strong>'티어'</strong> 로 맛집확인, <strong>'뽑기'</strong> 로 맛집추천, <strong>'제휴서비스'</strong> 로 혜택맛집을 한눈에 알아보자!</p>
<p align="center">
  <a href="https://kustaurant.com/" target="_blank">
    쿠스토랑 웹사이트 바로가기
  </a>
</p>
<div align="center">
    <a href="https://play.google.com/store/apps/details?id=com.kust.kustaurant">
        <img src="https://github.com/user-attachments/assets/9c5549f2-4a3b-4b32-8577-3399a3016c9c" width="100">
    </a>
    <a href="https://apps.apple.com/kr/app/쿠스토랑/id6621209330">
        <img src="https://github.com/user-attachments/assets/e0a85067-c2d0-498a-8eb7-4697fe88c0cf" width="100">
    </a>
</div>

<br>
  
## <img src="https://github.com/user-attachments/assets/1e1a6499-37cc-4a8f-bbb3-5acbca24a4e7" alt="kustaurant-logo" width="30" height="30"> 서비스 소개
아시다 싶이 건대주변이 핫플이라서 먹거리도 많고 음식점도 많은데 막상 맛집 정보를 찾는건 불편하더라구요.  
네이버 지도나 다른 맛집 서비스 같은것들은 ‘전국구 단위의 지도중심 서비스’ 이기 때문이고,  
에브리타임에 작성된 글들 또한 산발적이고 오직 '글' 로만 정보를 전달하기 때문입니다.  

그래서 만들었습니다.
그 어떤 서비스보다도 건국대 한정해서는 편리하고 직관적이게 맛집 관련 정보를 유저들에게 제공해보자.

소개 페이지 확인해보기 -> <a href="https://leeward-foam-2c2.notion.site/11e5483fde8a80a9889af04812a85f49?pvs=74" target="_blank"> 노션 페이지로 이동 </a>

투데이건국 인터뷰 -> <a href="https://blog.naver.com/PostView.naver?blogId=dreamkonkuk&logNo=223479201690&categoryNo=8&parentCategoryNo=8&from=thumbnailList" target="_blank"> 투데이건국 블로그 이동 </a>

<br>

## 📁 레포지토리
web서버 + api서버 통합 서버입니다.  
web은 기존 클래스 명으로, 모바일은 클래스마다 api를 붙여 작성하였습니다. 

첫번째 프로젝트는 web + web서버에서 시작하여 이후 모바일로 확장함에 따라 이전 레포에서 현제 레포로 이전후 추가 작업을 이어갔습니다.  
이전레포 -> https://github.com/JaehyeongIm/RestaurantTier

<br>

## 🌆  Backend Members
|Ding-woon|Kyung-bo|Jae-hyeong|
|:-:|:-:|:-:|
|<img src="https://github.com/user-attachments/assets/b9478ab7-b1b6-4313-bbc8-38e195364dde" alt="dingwoonee" width="100" height="100">|<img src="https://github.com/user-attachments/assets/b41b6c42-76fd-4b9a-99eb-7676b64ef9e3" alt="kyung-bo" width="100" height="100">|<img src="https://github.com/user-attachments/assets/ac8cfcf7-8fc5-4232-8c0a-8492399feb56" alt="jae-hyeong" width="100" height="100">|
|[DingWoonee](https://github.com/DingWoonee)|[Wcwdfu](https://github.com/Wcwdfu)|[JaehyeongIm](https://github.com/JaehyeongIm)|
<br>    

## 🏗️ System Architecture
<details>
  <summary>  Architecture</summary>
<img width="1593" height="959" alt="image" src="https://github.com/user-attachments/assets/aeaacbea-6f27-4787-bf79-3373d2bbcddc" />
</details>


## 🛠 Tech Stack
<details>
  <summary>  Backend</summary>

- Java: JDK 21 (LTS)
- Spring Boot: 3.5.6
- Spring Data JPA
- QueryDSL
- Spring Security
- Thymeleaf
</details>

<details>
  <summary>  Database</summary>

**Database**
- MySQL
- Flyway (Schema Migration)

**Cache & Messaging**
- Redis
</details>

<details>
  <summary>  Frontend</summary>

- Thymeleaf (Server-Side Rendering)
- Vanilla JavaScript (ES6+)
</details>

<details>
  <summary>  Crawling / Automation</summary>


- Playwright
</details>

<details>
  <summary>  Observability & Monitoring</summary>

- Prometheus
- Grafana
- Loki
- Promtail
</details>

<details>
  <summary>  Infrastructure & DevOps</summary>

- Docker
- Docker Compose
- AWS S3: Object Storage
- AWS Lightsail: Application Hosting
- GitHub Actions: CI/CD Pipeline
</details>
