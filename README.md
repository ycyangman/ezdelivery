## ezdelivery

![image](https://user-images.githubusercontent.com/487999/79708354-29074a80-82fa-11ea-80df-0db3962fb453.png)

본 프로젝트는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하고 배달서비스 쉽게 따라하기 입니다.

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [예제 - 음식배달](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

배달의 민족 커버하기 - https://1sung.tistory.com/106

기능적 요구사항
1. 고객이 메뉴를 선택하여 주문한다
1. 고객이 결제한다
1. 주문이 되면 주문 내역이 입점상점주인에게 전달된다
1. 상점주인이 확인하여 요리해서 배달 출발한다
1. 고객이 주문을 취소할 수 있다
1. 주문이 취소되면 배달이 취소된다
1. 고객이 주문상태를 중간중간 조회한다
1. 주문상태가 바뀔 때 마다 카톡으로 알림을 보낸다

비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 주문건은 아예 거래가 성립되지 않아야 한다  Sync 호출 
1. 장애격리
    1. 상점관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 자주 상점관리에서 확인할 수 있는 배달상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다  CQRS
    1. 배달상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven


# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/487999/79684144-2a893200-826a-11ea-9a01-79927d3a0107.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/487999/79684159-3543c700-826a-11ea-8d5f-a3fc0c4cad87.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://www.msaez.io/#/storming/ENCK1RBctmd90lE6Nh3Ch5cDhsI2/mine/95c737a498c29571a1e6686720027fea


### 이벤트 도출
 우선 시간의 흐름에 따라 비지니스의 상태 변경(생성,변경,삭제 등)을 의미하는 도메인 이벤트를 도출한다
![이벤트도출](https://user-images.githubusercontent.com/84304227/122169306-3a28f880-ceb8-11eb-9cea-173e118f755f.PNG)

### 부적격 이벤트 탈락
![부적격이벤트탈락](https://user-images.githubusercontent.com/84304227/122169352-4614ba80-ceb8-11eb-86be-cbaafdf2402b.PNG)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
        - 주문시>메뉴가 선택됨, 주문확인됨, 결제버튼이 클릭됨, 상점에 주문정보전달됨, 주문정보조회됨 :  UI 의 이벤트, 업무적인 의미의 이벤트가 아니라서 제외

### 액터, 커맨드 부착하여 읽기 좋게
- 엑터는 사람이나 조직이 될 수 있는데 역할 관점으로 도출한다. 엑터는 추상적으로 식별하지 말고 비지니스를 수행하는 구체적인 역할로 고려하여 도출한다. 
- 이벤트를 트리거하는(발생시키는) 커맨드를 도출한다. 커맨드는 동사 형태가 된다.

![액터 컴맨드읽기좋게](https://user-images.githubusercontent.com/84304227/122169788-c3402f80-ceb8-11eb-9515-38d8570d54af.PNG)

### 어그리게잇으로 묶기
어그리게잇은 커맨드와 이벤트가 영향을 주는 데이터 요소이다.
명사형이고 노란색 포스트잇에 작성하여 커맨드와 이벤트 사이의 상단에 겹쳐서 붙인다.

![어그릿게잇으로묶기](https://user-images.githubusercontent.com/84304227/122169817-ce935b00-ceb8-11eb-9838-d380eced6dd6.PNG)

- app의 Order, store 의 주문처리, 결제의 결제이력은 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨텍스트로 묶기

![바운디드켄텍스트로묶기](https://user-images.githubusercontent.com/84304227/122169883-e79c0c00-ceb8-11eb-92a0-c9a0cc3c318b.PNG)

    - 도메인 서열 분리 
        - Core Domain:  app(front), store : 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 app 의 경우 1주일 1회 미만, store 의 경우 1개월 1회 미만
        - Supporting Domain:   marketing, customer : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain:   pay : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 (핑크색으로 이후 전환할 예정)

### 폴리시 부착 (괄호는 수행주체, 폴리시 부착을 둘째단계에서 해놔도 상관 없음. 전체 연계가 초기에 드러남)
정책은 이벤트 뒤에 따라오는 반응 적인 비지니스 로직이며 어디인가에 존재하는 커맨드를 트리거 한다.

![폴리시부착](https://user-images.githubusercontent.com/84304227/122169969-013d5380-ceb9-11eb-891f-fe44c4663788.PNG)

### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)

비동기 호출은 커맨드가 수행주체의 폴리시로 이동

![폴리시의 이동과 컨텍스트 매핑](https://user-images.githubusercontent.com/84304227/122169980-04d0da80-ceb9-11eb-8372-977ba017b83a.PNG)

### 완성된 1차 모형

![완성된1차모형](https://user-images.githubusercontent.com/84304227/122170046-16b27d80-ceb9-11eb-9886-3a4446cb7877.PNG)

    - View Model 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![완성된1차모형_기능검증1](https://user-images.githubusercontent.com/84304227/122170091-25009980-ceb9-11eb-8661-67a5dc64cf8c.png)

    - 고객이 메뉴를 선택하여 주문한다 (ok)
    - 고객이 결제한다 (ok)
    - 주문이 되면 주문 내역이 입점상점주인에게 전달된다 (ok)
    - 상점주인이 확인하여 요리해서 배달 출발한다 (ok)

![완성된1차모형수정](https://user-images.githubusercontent.com/84304227/122170105-28942080-ceb9-11eb-9ee7-b27927cb0cbf.PNG)
    - 고객이 주문을 취소할 수 있다 (ok)
    - 주문이 취소되면 배달이 취소된다 (ok)
    - 고객이 주문상태를 중간중간 조회한다 (View-green sticker 의 추가로 ok) 
    - 주문상태가 바뀔 때 마다 카톡으로 알림을 보낸다 (?)


### 모델 수정

![완성된1차모형수정_기능검증2](https://user-images.githubusercontent.com/84304227/122170121-2b8f1100-ceb9-11eb-9495-68bd020af57d.png)

    
    - 수정된 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증

![비기능요구사항에대한검증](https://user-images.githubusercontent.com/84304227/122170321-698c3500-ceb9-11eb-86c7-6cedb91f039a.png)

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - 고객 주문시 결제처리:  결제가 완료되지 않은 주문은 절대 받지 않는다는 경영자의 오랜 신념(?) 에 따라, ACID 트랜잭션 적용. 주문와료시 결제처리에 대해서는 Request-Response 방식 처리
        - 결제 완료시 점주연결 및 배송처리:  App(front) 에서 Store 마이크로서비스로 주문요청이 전달되는 과정에 있어서 Store 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
        - 나머지 모든 inter-microservice 트랜잭션: 주문상태, 배달상태 등 모든 이벤트에 대해 카톡을 처리하는 등, 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.

### 최종 모델링

![최종소스기반모델링](https://user-images.githubusercontent.com/84304227/122336179-635c8e00-cf77-11eb-9b4b-98c6b672c650.PNG)

## 헥사고날 아키텍처 다이어그램 도출
    
![헥사고날아키텍처_new](https://user-images.githubusercontent.com/84304227/122347692-4464f880-cf85-11eb-8567-c58a622aa929.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐

## 이벤트스토밍 구현 기술연동

![이벤트스토밍구현기술연동](https://user-images.githubusercontent.com/84304227/122505402-3ec7eb00-d037-11eb-9d12-f03875dd68ad.PNG)

# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트와 파이선으로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd app
mvn spring-boot:run

cd pay
mvn spring-boot:run 

cd store
mvn spring-boot:run  

cd customer
python policy-handler.py 
```

## CQRS
CQRS는 Command and Query Responsibility Segregation(명령과 조회의 책임 분리)을 나타냅니다.

리뷰 및 주문/결재/배달 등  Status 에 대하여 점주 및 고객이 조회 할 수 있도록 CQRS 로 구현하였다.

```
@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    //-----------------------------------------------------
    // 주문되었을 때
    //-----------------------------------------------------
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1 (@Payload Ordered ordered) ;

    //-----------------------------------------------------
    // 주문취소되었을 때
    //-----------------------------------------------------
    @StreamListener(KafkaProcessor.INPUT)
        public void whenOrderCanceled_then_UPDATE_1(@Payload OrderCanceled orderCanceled) {
        try {

            if (!orderCanceled.validate()) return;

            System.out.println("\n\n##### listener whenOrderCanceled_then_UPDATE_1 : " + orderCanceled.toJson() + "\n\n");

            List<Mypage> mypageList = mypageRepository.findByOrderId(orderCanceled.getId());
            for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함

                if(StringUtils.isEmpty(orderCanceled.getStatus())) {
                    orderCanceled.setStatus("주문취소");
                }
                 mypage.setStatus(orderCanceled.getStatus());

                mypageRepository.save(mypage);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------
    // 배달시작되었을 때 --구현내용 상위 참조
    //-----------------------------------------------------
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_CREATE_3 (@Payload DeliveryStarted deliveryStarted) ;


    //-----------------------------------------------------
    // 결재취소되었을 때 --구현내용 상위 참조
    //-----------------------------------------------------
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPayCanceled_then_UPDATE_2(@Payload PayCanceled payCanceled);

}
```
## API 게이트웨이
1. gateway 스프링부트 App을 추가 후 application.yaml내에 각 마이크로 서비스의 routes 를 추가하고 gateway 서버의 포트를 8080 으로 설정함
- application.yaml 예시

```
spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: store
          uri: http://store:8080
          predicates:
            - Path=/stores/**
        - id: html
          uri: http://html:8080
          predicates:
            - Path=/**
        - id: order
          uri: http://order:8080
          predicates:
            - Path=/orders/** 
        - id: payment
          uri: http://payment:8080
          predicates:
            - Path=/payments/** 
        - id: alarm
          uri: http://alarm:8080
          predicates:
            - Path=/msgs/** 
        - id: mypage
          uri: http://mypage:8080
          predicates:
            - Path=/reviews/** /mypages/**
        - id: delivery
          uri: http://delivery:8080
          predicates:
            - Path=/deliveries/** 
#html 경로는 root path로 맨 나중에 위치함
        - id: html
          uri: http://html:8080
          predicates:
            - Path=/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
```

## Correlation
프로젝트에서는 PolicyHandler에서 처리 시 어떤 건에 대한 처리인지를 구별하기 위한 Correlation-key 구현을 이벤트 클래스 안의 변수로 전달받아 서비스간 연관된 처리를 정확하게 구현하고 있습니다.

각각의 MSA 서비스는 자신이 보유한 서비스내 Local 트랜잭션을 관리하며, 트랜잭션이 종료되면 완료 Event를 발행합니다. 
만약 그 다음에 수행되어야할 트랜잭션이 있다면,  해당 트랜잭션을 수행해야하는 App에서 완료 Event를 수신받고 다음 작업을 처리합니다. 
이때 Event는 Kafka와 같은 메시지 큐를 이용해서 비동기 방식으로 전달한다.

주문을 하면 동시에 연관된 주문(Order), 결제(Payment) 등의 서비스의 상태가 적당하게 변경이 되고,
주문을 취소하면 다시 연관된 Strore, 주문, 결제(Payment) 등의 서비스의 상태값 등의 데이터가 변경되는 것을 확인할 수 있습니다.



## DDD(Domain-Driven Design) 의 적용
도메인 모델은 특정 비지니스 맥락에서 통용되는 개념들의 관계를 잘 정의한 모형이다.
도메인 모델을 보면 각 도메인 모델과 다른 도메인 모델간의 경계가 보인다. 
여기서 사용하는 언어와 저곳에서 상용하는 언어와 개념이 상이하는 이 경계가 도메인의 경계, 컨텍스트 경계(Bounded Context)이다.
Bounded Context내의 도메인 주요개념을 표현하기 위해 도메인내에 공통으로 사용하는 언어가 유비쿼터스언어이다.
같은 컨텍스트내의 이해관계자가 사용하는 언어를 개발소스에도 사용해야 하나 아래의 이유로 영문구성을 하였다.

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 payment 마이크로 서비스). 
- 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 
- 하지만, 일부 구현에 있어서 영문이 아닌 경우는 실행이 불가능한 경우가 있기 때문에 계속 사용할 방법은 아닌것 같다. (Maven pom.xml, Kafka의 topic id, FeignClient 의 서비스 id 등은 한글로 식별자를 사용하는 경우 오류가 발생하는 것을 확인하였다)

```
package ezdelivery;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long storeId;  //상점ID
    private String storeName; //상점명
    private String host; //점주
    private String menuName; //메뉴명
    private Double payAmt; //결제금액
    private String payDate; //결제일자
    private String status; //상태
    private Long orderId; //주문ID
    private Long orderNumber; //주문건수
    private String guestName; //고객명

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Double getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Double payAmt) {
        this.payAmt = payAmt;
    }
    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 
  데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package ezdelivery;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="payments", path="payments")
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long>{
    
    List<Payment> findByStoreId(Long storeId);
    List<Payment> findByOrderId(Long orderId);

}
```
- 적용 후 REST API 의 테스트
```
# app 서비스의 주문처리
http POST localhost:8080/orders storeId=1 storeName="동네치킨" host="요기요" menuName="치킨두마리" price=22000 guestName="홍길동" status="주문됨" guestAddress="광화문1번지" orderNumber=10 orderDateTime="20210615"

# store 서비스의 배달처리
http POST localhost:8080/deliverys orderId=2 status="배달중"

# 주문 상태 확인
http localhost:8080/orders/1

```


## 폴리글랏 퍼시스턴스

앱프런트 (app) 는 서비스 특성상 많은 사용자의 유입과 상품 정보의 다양한 콘텐츠를 저장해야 하는 특징으로 인해 RDB 보다는 Document DB / NoSQL 계열의 데이터베이스인 Mongo DB 를 사용하기로 하였다. 이를 위해 order 의 선언에는 @Entity 가 아닌 @Document 로 마킹되었으며, 별다른 작업없이 기존의 Entity Pattern 과 Repository Pattern 적용과 데이터베이스 제품의 설정 (application.yml) 만으로 MongoDB 에 부착시켰다

```
# Order.java

package fooddelivery;

@Document
public class Order {

    private String id; // mongo db 적용시엔 id 는 고정값으로 key가 자동 발급되는 필드기 때문에 @Id 나 @GeneratedValue 를 주지 않아도 된다.
    private String item;
    private Integer 수량;

}


# 주문Repository.java
package fooddelivery;

public interface 주문Repository extends JpaRepository<Order, UUID>{
}

# application.yml

  data:
    mongodb:
      host: mongodb.default.svc.cluster.local
    database: mongo-example

```

## 폴리글랏 프로그래밍

고객관리 서비스(customer)의 시나리오인 주문상태, 배달상태 변경에 따라 고객에게 카톡메시지 보내는 기능의 구현 파트는 해당 팀이 python 을 이용하여 구현하기로 하였다. 해당 파이썬 구현체는 각 이벤트를 수신하여 처리하는 Kafka consumer 로 구현되었고 코드는 다음과 같다:
```
from flask import Flask
from redis import Redis, RedisError
from kafka import KafkaConsumer
import os
import socket


# To consume latest messages and auto-commit offsets
consumer = KafkaConsumer('fooddelivery',
                         group_id='',
                         bootstrap_servers=['localhost:9092'])
for message in consumer:
    print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))

    # 카톡호출 API
```

파이선 애플리케이션을 컴파일하고 실행하기 위한 도커파일은 아래와 같다 (운영단계에서 할일인가? 아니다 여기 까지가 개발자가 할일이다. Immutable Image):
```
FROM python:2.7-slim
WORKDIR /app
ADD . /app
RUN pip install --trusted-host pypi.python.org -r requirements.txt
ENV NAME World
EXPOSE 8090
CMD ["python", "policy-handler.py"]
```


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 주문(app)->결제(pay) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
#  PaymentService.java

package ezdelivery.external;

//api.url.payment ==> http://localhost:8083
@FeignClient(name="pay", url="http://localhost:8083")
public interface PaymentService {

    @RequestMapping(method= RequestMethod.GET, path="/payments")
    public void makePay(@RequestBody Payment payment);

}
```

- 주문을 받은 직후(@PostPersist) 결제를 요청하도록 처리
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        ezdelivery.external.Payment payment = new ezdelivery.external.Payment();
        BeanUtils.copyProperties(this, payment);
        payment.setOrderId(getId());

        payment.setPayAmt(getPrice() * getOrderNumber());
        payment.setPayDate(new SimpleDateFormat("YYYYMMdd").format(new Date()));
        payment.setStatus("결재승인");

        try {
            OrderApplication.applicationContext.getBean(ezdelivery.external.PaymentService.class).makePay(payment);
        
        }catch(Exception e) {
            throw new RuntimeException("결제서비스 호출 실패입니다."+e.getLocalizedMessage());
            //e.printStackTrace();
        }

        // 결제까지 완료되면 최종적으로 예약 완료 이벤트 발생
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.setStatus("결재승인");
        ordered.publishAfterCommit();

    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인:


```
# 결제 (pay) 서비스를 잠시 내려놓음 (ctrl+c)

#주문처리

```
- 주문실패
![payment동기식호출실패](https://user-images.githubusercontent.com/84304227/122171998-4b273900-cebb-11eb-880c-79cf316934fa.PNG)

```
#결제서비스 재기동
cd payment
mvn spring-boot:run

#주문처리
```
![payment동기식호출성공](https://user-images.githubusercontent.com/84304227/122173656-eff64600-cebc-11eb-8f04-6c7d9fdcb82d.PNG)

- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트


결제가 이루어진 후에 상점시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 상점 시스템의 처리를 위하여 결제주문이 블로킹 되지 않아도록 처리한다.
 
- 이를 위하여 결제이력에 기록을 남긴 후에 곧바로 결제승인이 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
package ezdelivery;

@Entity
@Table(name="Payment_table")
public class Payment {

 ...
    @PrePersist
    public void onPrePersist(){
        PayApproved payApproved = new PayApproved();
        BeanUtils.copyProperties(this, payApproved);
        payApproved.setStatus("결재승인");
        payApproved.publishAfterCommit();
        
    }

}
```
- 상점 서비스에서는 결제승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
package ezdelivery;

...

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void whenever결제승인됨_주문정보받음(@Payload 결제승인됨 결제승인됨){

        if(결제승인됨.isMe()){
            System.out.println("##### listener 주문정보받음 : " + 결제승인됨.toJson());
            // 주문 정보를 받았으니, 요리를 슬슬 시작해야지..
            
        }
    }

}

```
실제 구현을 하자면, 카톡 등으로 점주는 노티를 받고, 요리를 마친후, 주문 상태를 UI에 입력할테니, 우선 주문정보를 DB에 받아놓은 후, 이후 처리는 해당 Aggregate 내에서 하면 되겠다.:
  
```
  @Autowired 주문관리Repository 주문관리Repository;
  
  @StreamListener(KafkaProcessor.INPUT)
  public void whenever결제승인됨_주문정보받음(@Payload 결제승인됨 결제승인됨){

      if(결제승인됨.isMe()){
          카톡전송(" 주문이 왔어요! : " + 결제승인됨.toString(), 주문.getStoreId());

          주문관리 주문 = new 주문관리();
          주문.setId(결제승인됨.getOrderId());
          주문관리Repository.save(주문);
      }
  }

```

상점 시스템은 주문/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 상점시스템이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다:
```
# 상점 서비스 (store) 를 잠시 내려놓음 (ctrl+c)

#주문처리
http localhost:8081/orders item=통닭 storeId=1   #Success
http localhost:8081/orders item=피자 storeId=2   #Success

#주문상태 확인
http localhost:8080/orders     # 주문상태 안바뀜 확인

#상점 서비스 기동
cd 상점
mvn spring-boot:run

#주문상태 확인
http localhost:8080/orders     # 모든 주문의 상태가 "배송됨"으로 확인
```


# 운영

# 환경구성

* EKS Cluster create
```
$ eksctl create cluster --name skccuer10-Cluster --version 1.15 --nodegroup-name standard-workers --node-type t3.medium --nodes 3 --nodes-min 1 --nodes-max 4
```

* EKS Cluster settings
```
$ aws eks --region ap-northeast-2 update-kubeconfig --name skccuer10-Cluster
$ kubectl config current-context
$ kubectl get all
```

* ECR 인증
```
$ aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com
```

* Metric Server 설치
```
$ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.3.6/components.yaml
$ kubectl get deployment metrics-server -n kube-system
```

* Kafka install (kubernetes/helm)
```
$ curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash
$ kubectl --namespace kube-system create sa tiller      
$ kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
$ helm init --service-account tiller
$ kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'
$ helm repo add incubator http://storage.googleapis.com/kubernetes-charts-incubator
$ helm repo update
$ helm install --name my-kafka --namespace kafka incubator/kafka
$ kubectl get all -n kafka
```

* Istio 설치
```
$ curl -L https://git.io/getLatestIstio | ISTIO_VERSION=1.4.5 sh -
$ cd istio-1.4.5
$ export PATH=$PWD/bin:$PATH
$ for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done
$ kubectl apply -f install/kubernetes/istio-demo.yaml
$ kubectl get pod -n istio-system
```

* Kiali 설정
```
$ kubectl edit service/kiali -n istio-system

- type 변경 : ClusterIP -> LoadBalancer
- (접속주소) http://http://ac5885beaca174095bad6d5f5779a443-1156063200.ap-northeast-2.elb.amazonaws.com:20001/kiali
```

* Namespace 생성
```
$ kubectl create namespace ezdelivery
```

* Namespace istio enabled
```
$ kubectl label namespace ezdelivery istio-injection=enabled 

- (설정해제 : kubectl label namespace ezdelivery istio-injection=disabled --overwrite)
```

* siege deploy
```
cd ezdelivery/yaml
kubectl apply -f siege.yaml 
kubectl exec -it siege -n ezdelivery -- /bin/bash
apt-get update
apt-get install httpie
```

```
apiVersion: v1
kind: Pod
metadata:
  name: siege
  namespace: ezdelivery
spec:
  containers:
    - name: siege
      image: apexacme/siege-nginx
```
# Build & Deploy

* ECR image repository
```
$ aws ecr create-repository --repository-name user08-ezdelivery-gateway --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-store --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-order --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-payment --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-mypage --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-alarm --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-delivery --region ap-northeast-2

```

* image build & push
```
$ cd gateway
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-gateway:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-gateway:latest

$ cd ../store
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-store:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-store:latest

$ cd ../order
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-order:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-order:latest

$ cd ../payment
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-payment:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-payment:latest

$ cd ../mypage
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-mypage:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-mypage:latest

$ cd ../alarm
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-alarm:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-alarm:latest

$ cd ../delivery
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-delivery:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-delivery:latest

```

* Deploy
```
$ kubectl apply -f siege.yaml
$ kubectl apply -f configmap.yaml
$ kubectl apply -f gateway.yaml
$ kubectl apply -f store.yaml
$ kubectl apply -f order.yaml
$ kubectl apply -f payment.yaml
$ kubectl apply -f mypage.yaml
$ kubectl apply -f delivery.yaml
$ kubectl apply -f alarm.yaml

```
## CI/CD 설정

각 구현체들은 github의 각각의 source repository 에 구성
Image repository는 ECR 사용

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 GCP를 사용하였으며, 
pipeline build script 는 각 프로젝트 폴더 이하에 cloudbuild.yml 에 포함되었다.


## 동기식 호출 / 서킷 브레이킹 / 장애격리

방식1) 서킷 브레이킹 프레임워크의 선택: istio-injection + DestinationRule

```
kubectl get ns -L istio-injection
kubectl label namespace ezdelivery istio-injection=enabled
````
- 예약, 결제 서비스 모두 아무런 변경 없음
- 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명, 60초 동안 실시

```
kubectl run siege --image=apexacme/siege-nginx -n ezdelivery
kubectl exec -it siege -c siege -n ezdelivery -- /bin/bash
siege -c100 -t60S -r10 --content-type "application/json" 'http://order:8080/orders POST {"storeName": "yogiyo"}'
```
서킷 브레이킹을 위한 DestinationRule 적용

```
cd ezdelivery/yaml
kubectl apply -f dr-pay.yaml
```
DestinationRule 적용되어 서킷 브레이킹 동작 확인 (kiali 화면)


다시 부하 발생하여 DestinationRule 적용 제거하여 정상 처리 확인
```
cd ezdelivery/yaml
kubectl delete -f dr-pay.yaml
```


방식2) 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 주문(order)-->결제(pay) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# order_application.yml

feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
      
kubectl apply -f order_cb.yaml
```


```

cd ezdelivery/yaml
kubectl apply -f dr-pay.yaml

istio-injection 활성화 및 pod container 확인
kubectl get ns -L istio-injection
kubectl label namespace ezdelivery istio-injection=enabled 


```

- 피호출 서비스(결제:pay) 의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
```
# (pay) 결제이력.java (Entity)

    @PrePersist
    public void onPrePersist(){  //결제이력을 저장한 후 적당한 시간 끌기

        ...
        
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

istio-injection 적용 (기 적용완료)
```
kubectl label namespace mybnb istio-injection=enabled
```
* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
$ siege -c100 -t60S -r10 --content-type "application/json" 'http://order:8080/orders POST {"storeName": "yogiyo"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.73 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.75 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.77 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.97 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.81 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.87 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.12 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.16 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.17 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.26 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.25 secs:     207 bytes ==> POST http://localhost:8081/orders

* 요청이 과도하여 CB를 동작함 요청을 차단

HTTP/1.1 500     1.29 secs:     248 bytes ==> POST http://localhost:8081/orders   
HTTP/1.1 500     1.24 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.23 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.42 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     2.08 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.29 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.24 secs:     248 bytes ==> POST http://localhost:8081/orders

* 요청을 어느정도 돌려보내고나니, 기존에 밀린 일들이 처리되었고, 회로를 닫아 요청을 다시 받기 시작

HTTP/1.1 201     1.46 secs:     207 bytes ==> POST http://localhost:8081/orders  
HTTP/1.1 201     1.33 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.36 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.63 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.65 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.71 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.71 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.74 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.76 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.79 secs:     207 bytes ==> POST http://localhost:8081/orders

* 다시 요청이 쌓이기 시작하여 건당 처리시간이 610 밀리를 살짝 넘기기 시작 => 회로 열기 => 요청 실패처리

HTTP/1.1 500     1.93 secs:     248 bytes ==> POST http://localhost:8081/orders    
HTTP/1.1 500     1.92 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.93 secs:     248 bytes ==> POST http://localhost:8081/orders

* 생각보다 빨리 상태 호전됨 - (건당 (쓰레드당) 처리시간이 610 밀리 미만으로 회복) => 요청 수락

HTTP/1.1 201     2.24 secs:     207 bytes ==> POST http://localhost:8081/orders  
HTTP/1.1 201     2.32 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.16 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.21 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.29 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.30 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.38 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.59 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.61 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.62 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.64 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.01 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.27 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.33 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.45 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.52 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.57 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders

* 이후 이러한 패턴이 계속 반복되면서 시스템은 도미노 현상이나 자원 소모의 폭주 없이 잘 운영됨


HTTP/1.1 500     4.76 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.23 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.76 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.74 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.82 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.82 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.84 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.66 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     5.03 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.22 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.19 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.18 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.65 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     5.13 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.84 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.25 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.25 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.80 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.87 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.33 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.86 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.96 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.34 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.04 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.50 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.95 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.54 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.65 secs:     207 bytes ==> POST http://localhost:8081/orders


:
:

Transactions:		        1025 hits
Availability:		       63.55 %
Elapsed time:		       59.78 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
Successful transactions:        1025
Failed transactions:	         588
Longest transaction:	        9.20
Shortest transaction:	        0.00

```
- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 63.55% 가 성공하였고, 46%가 실패했다는 것은 고객 사용성에 있어 좋지 않기 때문에 Retry 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

- Retry 의 설정 (istio)
- Availability 가 높아진 것을 확인 (siege)

### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- (istio injection 적용한 경우) istio injection 적용 해제
```
kubectl label namespace ezdelivery istio-injection=disabled --overwrite

kubectl apply -f order.yaml
kubectl apply -f payment.yaml
```

- 결제서비스 배포시 resource 설정 적용되어 있음
```
    spec:
      containers:
          ...
          resources:
            limits:
              cpu: 500m
            requests:
              cpu: 200m
```

- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy payment -n ezdelivery --min=1 --max=10 --cpu-percent=15
#kubectl autoscale deploy order --min=1 --max=10 --cpu-percent=15
$ kubectl get deploy auth -n ezdelivery -w 
```
- CB 에서 했던 방식대로 워크로드를 2분 동안 걸어준다.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://order:8080/orders POST {"storeName": "yogiyo"}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy order -w -n ezdelivery 
kubectl get deploy order -w
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
```
NAME    DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
pay     1         1         1            1           17s
pay     1         2         1            1           45s
pay     1         4         1            1           1m
:
```
- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
```
Transactions:		        5078 hits
Availability:		       92.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
```


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함(위의 시나리오에서 제거되었음)

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://order:8080/orders POST {"storeName": "yogiyo"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
:

```

- # 컨테이너 이미지 Update (readness, liveness 미설정 상태)
```
- kubectl apply -f order_na.yaml 실행
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
```
Transactions:		        3078 hits
Availability:		       70.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```
배포기간중 Availability 가 평소 100%에서 70% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:

```

# deployment.yaml 의 readiness probe 의 설정:
- kubectl apply -f order.yaml 실행
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Transactions:		        3078 hits
Availability:		       100 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.


# 신규 개발 조직의 추가

  ![image](https://user-images.githubusercontent.com/487999/79684133-1d6c4300-826a-11ea-94a2-602e61814ebf.png)


## 마케팅팀의 추가
    - KPI: 신규 고객의 유입률 증대와 기존 고객의 충성도 향상
    - 구현계획 마이크로 서비스: 기존 customer 마이크로 서비스를 인수하며, 고객에 음식 및 맛집 추천 서비스 등을 제공할 예정

## 이벤트 스토밍 
    ![image](https://user-images.githubusercontent.com/487999/79685356-2b729180-8273-11ea-9361-a434065f2249.png)


## 헥사고날 아키텍처 변화 

![image](https://user-images.githubusercontent.com/487999/79685243-1d704100-8272-11ea-8ef6-f4869c509996.png)

## 구현  

기존의 마이크로 서비스에 수정을 발생시키지 않도록 Inbund 요청을 REST 가 아닌 Event 를 Subscribe 하는 방식으로 구현. 기존 마이크로 서비스에 대하여 아키텍처나 기존 마이크로 서비스들의 데이터베이스 구조와 관계없이 추가됨. 

## 운영과 Retirement

Request/Response 방식으로 구현하지 않았기 때문에 서비스가 더이상 불필요해져도 Deployment 에서 제거되면 기존 마이크로 서비스에 어떤 영향도 주지 않음.

* [비교] 결제 (pay) 마이크로서비스의 경우 API 변화나 Retire 시에 app(주문) 마이크로 서비스의 변경을 초래함:

예) API 변화시
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        fooddelivery.external.결제이력 pay = new fooddelivery.external.결제이력();
        pay.setOrderId(getOrderId());
        
        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제(pay);

                --> 

        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제2(pay);

    }
```

예) Retire 시
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        /**
        fooddelivery.external.결제이력 pay = new fooddelivery.external.결제이력();
        pay.setOrderId(getOrderId());
        
        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제(pay);

        **/
    }
```
