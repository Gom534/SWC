# 휴게소 포장 서비스 어플리케이션

### 프로젝트 소개

---

- 고속도로 휴게소 이용의 불편함
  
  - 자동차 보급 대수가 2023년 기준 약 2,600만대에 육박했으며, 2023년 6월 COVID-19 격리 의무가 해제되면서 고속도로 이용 급격하게 증가
  
  - 고속도로 휴게소 이용 시 휴게소 진입부터 정체, 음식 구매 대기 시간이 길어져 구매를 포기하거나 지나치는 상황 빈번하게 발생
  
  - 단체 여행객, 고속버스의 경우 제한적인 이용 시간으로 휴게소 내 서비스 이용에 대한 불편함 가중, 따라서 휴게소의 입장에서 매출 감소 발생

- 고속도로 휴게소 서비스 활용 어플리케이션 부재
  
  - 다양한 고속도로 이용 서비스 어플리케이션 존재
  
  - 휴게소 내 일반 정보(위치, 연락처 및 주유 정보)제공 외 서비스 부재
  
  - 시일이 지날수록 다양화(쇼핑몰, 애견운동장 입점 등)되는 휴게소 서비스 이용 정보 부재
  
  이러한 점들을 따져 보았을 때 구현하면 장점이 많아질 것 같아서 구현해보기로 하였습니다.

### 개발 기간

---

- 저희는 개발기간을 공모전에 맞추어 5/28일 부터 6/26일까지 1차 개발을 하였습니다.    2차 개발을  7월 28일에 시작하여서 8/23일까지 2차 개발을 하여 좀더 고도화를 해보는 기간을 가졌습니다. 

#### 사용자 Version 1 개발 간트차트

---

![버전1.png](.\이정우\버전1.png)



### 사용자 Version 2 개발 간트차트

---

![버전2.png](.\버전2.png)



#### 판매자 어플 간트차트

---

![판매자 간트차트.png](./판매자%20간트차트.png)

### 사용 기술

  ---



- Android Studio Kotlin  : 코틀린기반 휴게소 포장 서비스 개발

- Firebase RealTime DB : 두 앱의 실시간 소통을 위한 데이터베이스

- Firebase Storage : 이미지 저장을 위한 스토리지 사용 

- Firebase Cloud Message : 데이터 반영시 알림 전송기술 구현

- Kakao Pay API : 결제수단을 구현하기 위하여 API 사용

- Google Map API : 현재 기기의 GPS를 반영하여 지도 내에 표시 및 가까운 휴게소 위치 표시

- 한국 도로 공사 API : 제일 가까운 휴게소 주유소 가격을 띄어 주는 API 사용

- Text 기반 휴게소 검색 : 검색하고 싶은 휴게소를 텍스트 기반으로 검색기능 구현

### 





### 개발자 소개

---

- 팀장 이정우
  
  - 사용자용 앱을 개발하였습니다. 휴게소 포장 서비스의 사용자 부분을 개발하였으며 파이어 베이스를 한번 사용해보자는 마음으로 파이어베이스를 기반으로 대부분의 코드가 작성되었고 3가지의 API를 사용하였습니다. 그리고 한국도로공사 CSV를 활용하여 구글맵에 지도상의 거리가 제일가까운 휴게소등을 표현하는 서비스를 구현하였습니다.그리고 해당하는 휴게소의 유가를 API로 받아와서 표출하여주는 일을 하였습니다. 그리고 판매자가 올려주는 음식을 사용자앱에 올려주어 사용자들이 구매할수 있도록 하였습니다 결제시스템은 카카오페이로 연결해보았습니다.

- 팀원 오재경
  
  - 판매자앱을 맡아주셨던 팀원입니다. 판매자앱에서는 일반 구매자가 아닌 판매자들이 사용하기에 UI보다는 기능을 사용자를 사용자앱보다는 조금더 연령대가 있을거라 예상을 하고 만들었습니다. 그래서 전반적인 UI가 단순하고 편리하게 보이려고 노력하였습니다 . 그리고 판매자앱도 비슷하게 파이어베이스를 기반으로 작성하였고 Firebase Authentication , FCM , Firebase RealtimeDB 이러한 기능을 주로 사용하였습니다.

### Dependency

---

<img src="file:///./2024-08-27-14-53-34-image.png" title="" alt="" width="429">

### Trouble Shooting

---

-  해당 프로젝트는 데이터베이스에 많은 데이터를 저장하고 실시간으로 업데이트를 하는 특성상 비용 문제가 발생할 가능성이 높음
  
  - 이러한 문제를 해결하기 위해 자체 데이터베이스를 구축하는 것을 목표
  
  - 외부 서비스 사용으로 인한 비용을 절감하고, 데이터 관리의 효율성을 높이고자 함

- FCM의 사용방식 중 저희가 원하것은 Firebase의 Funtion기능을 사용하는 것을 원하였는데 Funtion기능이 유로기능이라고 하여 차질이 발생 
  
  - 알림기능을 자체적으로 구현하는 기능으로 바꿔서 해결하려 노력

-  휴게소를 자주 이용해봤지만, 상업적인 측면에서는 깊이 고민해본 경험이 부족하므로 사용자 관점에서의 표현이 강하게 반영될 가능성이 높다고 생각함
  
  - 저희만의 경험으로는 부족을 느껴 타 사용자에게 정보를 수집






















































