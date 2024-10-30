# OAuth-Provider
Spring security / OAuth2 Client 를 사용하지 않고 OAuth 구현해보기 

> 📌 개요

- 해당 기간 과제가 주어졌는데 리팩토링에 관한 주제를 다룬 과제였다. 하지만 리팩토링할만한 부분이 잘 보이지 않고 요구사항대로 과제를 진행했더니 1시간 안에 다 끝났다…. ;; (뭐 더 할 수 있다면 리팩토링을 진행해볼 수 있겠지만 그러기엔 내가 주어진 시간을 활용해야하는 공부시간 대비 해당과제를 고민하는 시간이 아깝게 느껴졌기 때문에 적당히만 진행하고 트러블 슈팅이나 과제 관련한 TIL 작성도 하지 않았다… )
- 그러다 챌린지 세션에서 재미있는 과제가 주어졌다.
- `Spring Security` 와 `OAuth Client` 를 적용하지 않고 `OAuth` 로그인 구현하기라는 과제가 주어졌다. 요구사항은 간단했다.

- 요구사항
    - `Spring Security`, `OAuth Client` 라이브러리를 사용하지 않을 것
    - 로그인 완료시 해당 화면에 `Access Token` 정보를 보이게 할 것
    - 여러가지 서비스가 추가될 수 있기때문에 그부분을 생각하고 구현할 것

- 요구사항만 보게 되면 간단하지만 `OAuth` 로그인은 내부적으로 어떤 방식으로 통신이 이루어지는 알아야하고 단순히 하나의 소셜로그인 서비스만 제공하면 간단하겠지만 여러 서비스를 제공해야한다면 각각에 `Authorization Server` 에서 요구하는 필수 데이터를 전달해야 하기 때문에 구현조건이 까다롭다.
- 또한 `Authorization Server` 에서 요구하는 `API` 가 각자 다르기때문에 `Spring Security` 에 존재하는 `Registration`을 직접 구성해야했다.

- 프로젝트에 시작하기 앞서 동작원리나 짚고 넘어가야할 것을 정리해봐야겠다.

<br><br><br><br><br><br><br><br>


> ⚙️ OAuth 동작 원리 

![](https://velog.velcdn.com/images/dbrjsdn2051/post/c827bc3a-1e76-4fe0-b45d-c30ad39e3912/image.png)

- `GET : /oauth/authorize` - 인가 코드 받기
    - User가 소셜 로그인을 클릭하게되면 해당 서비스에 로그인 창으로 이동하게된다. 이때 요청하는 URI 가 /oauth/authorize URI 가 되는 것이다.
    - 유저가 로그인을 성공하게되면 Authorization Server 로 부터 Code 를 부여받게된다.
    - 이 코드를 이용하여 Access Token 정보를 받아올 수 있다.
- `POST : /oauth/token` - 토큰 정보 받기
    - 유저가 로그인 성공을 하고 받은 `Code`를 통해 `Authorization Server` 에 `Code` 정보와 추가 데이터를 보내게 된다.
    - `Authorization Server` 에서 유효성을 검증하였다면 토큰을 발급하게 된다.

<br><br>

- `OAuth` 로그인에 전체적인 흐름은 이렇게 진행된다. 그렇다면 어떤 데이터를 주고받아야 하는지도 정리해보겠다.

<br><br><br><br><br><br><br><br>


> 📍 Authorization Server 에서 요구하는 데이터 정리 

<br>

## NAVER

- 인가 코드 요청 API - GET
    - `Client-Id`
    - `Redirect-Uri`
    - `Response-Type`
<br>    

- 토큰 정보 요청 API - POST
    - `Clinet-Id`
    - `Clinet-Secret`
    - `Redirect-Uri`
    - `Grant-Type`
    - `Code`
<br>

## KAKAO

- 인가 코드 요청 API - GET
    - `Client-Id`
    - `Redirect-Uri`
    - `Response-Type`
    - `Code`
<br>        

- 토큰 정보 요청 API - POST
    - `Client-id`
    - `Redirect-Uri`
    - `Grant-Type`
    - `Code`

<br>

## GOOGLE

- 인가 코드 요청 API - GET
    - `Client-Id`
    - `Redirect-Uri`
    - `Response-Type`
    - `Scope`
 
 <br>    
    
- 토큰 정보 요청 API - POST
    - `Client-Id`
    - `Client-Secret`
    - `Redirect-Uri`
    - `Grant_type`
    - `Code`
    
<br><br><br><br><br>

## 요청 데이터 정보

- `Client-Id`
    - 해당 서비스의 API를 요청할 수 있는 ID 정보
- `Client-Secret`
    - 해당 서비스의 API를 요청할 수 있는 인코딩된 비밀번호 정보
- `Redirect-Uri`
    - 해당 서비스 요청후 응답받을 URI 정보
- `Grant-Type`
    - 권한 부여 타입 정보
- `Response-Type`
    - `Authorization Server` 로 요청을 보내고 어떤 타입으로 응답받을지에 대한 정보
    - ex) `code`, `token`, `id_token`
- `Scope`
    - `Authorization Server` 에서 가져올 데이터 정보
    - 제한된 데이터 접근을 위해 사용
    - ex) `email`, `profile`
- `State`
    - `CSRF` 공격 방지를 위해 사용하는 정보
    - 임의의 문자열을 생성하고 사용자가 앱을 승인한 후 `Authorization Server` 로 부터 동일한 값인지 확인

<br><br><br><br><br><br><br><br>



> 💡 짚고 넘어가야할 것

- 저번 과제에서 외부 `API` 통신을 위해 `Feign Client` 를 사용했었다. `MSA` 프로젝트에서 사용해보고 너무 편하다는 것을 알았기 때문이다. 하지만 스프링 공식문서에는 이제 `Open Feign` 에 대해 지원을 중단한다고 밝혔다. 이미 완벽한 기능이기 때문이라는 이유이다. `Feign Client` 도 좋고 편해서 해당 과제해서 적용해도되지만 챌린지 세션에서 `RestClient` 라이브러리가 새롭게 생겨났다고 소개받아서 해당과제에서 새로 배우고 써보기로 했다.
- 해당 과제는 지속적으로 서비스가 추가된다는 시나리오를 가지고 구조를 설계해야한다. 언제든 새롭게 추가되는 것에 변경이 전파되지 않게 고려해야한다. 계산기 과제에서 사용해본 팩토리 패턴을 이용해 객채 생성책임을 위임할 생각이다.
- 구글, 카카오, 네이버 소셜로그인 서비스들은 인증서버로 정보가져오고 토큰정보를 받아오는 동일한 작업을 수행하게 된다. 이부분을 인터페이스를 상속받아 사용할 수 있도록 한다.
- 팩토리 메서드 패턴을 `Enum` 클래스로 사용하거나 의존성 주입을 이용하지 않을 경우 `@Value` 어노테이션을 해당 구현체가 쓰고 있을 경우 빈으로 관리되지 않기 때문에 `@Value`  어노테이션을 적용한 파라미터나, 필드 값이 바인딩되지 않는다. (해결방법으로 `Environment` 를 사용하는 방법이 있지만 코드량 증가/가독성 하락…)

<br><br><br><br><br>


> 🧑‍💻 코드로 구현하기

<br>

## Controller

```java
@GetMapping("/login/{provider}")
public void loginPage(@PathVariable String provider, HttpServletResponse response) throws IOException {
		String redirectUrl = oAuthLoginService.retrieveUrlFromProvider(provider);
		response.sendRedirect(redirectUrl);
}

@GetMapping("/login/oauth2/code/{provider}")
public String accessTokenInfo(@RequestParam("code") String code, @PathVariable String provider) {
		log.info("code = {}", code);
		return oAuthLoginService.exchangeCodeForToken(code, provider);
}
```

- `loginPage`
    - `Client` 가 어떤 서비스로 로그인을 수행할지 모르기때문에 `provider`를 소셜로그인 정보를 받는다.
    - 해당 소셜로그인 정보를 서비스로직으로 넘겨주고 `API URL` 주소를 받아온후 해당 경로로 데이터를 요청한다.
- `accessTokenInfo`
    - 파라미터로 `Code` 를 받아오고 소셜로그인 타입 정보를 URI 를 통해 받아오게된다.
    - 해당 `Code` 정보와 소셜로그인 타입 정보를 서비스 로직에게 넘겨준후 `Access Token` 정보를 받아온후 화면에 보여지게 된다.

<br><br><br>

## Service - Interface

```java
@Override
public String loginPage() {
		HashMap<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("redirect_uri", redirectUri);
		params.put("response_type", responseType);

		return queryParamBuilder.createUrl(authorizationUri, params);
}

@Override
public String getToken(String code) {
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("redirect_uri", redirectUri);
		body.add("grant_type", grantType);
		body.add("code", code);

		return restClientService.exchangeCodeForToken(tokenUri, body);
}
```

- `loginPage`
    - 소셜 로그인 페이지 진입전 필요한 정보를 담아서 `Url`를 만든 후 `Controller`로 반환하게 되는 로직이다.
- `getToken`
    - 토큰정보를 받아오기위해 필요한 데이터를 `body` 에 담아주고 `RestClientService` 를 호출하여 `Authorization Server` 와 통신하게 된다.

<br><br><br>

## Factory

```java
public LoginService getLoginService(String loginType) {
		return switch (loginType.toLowerCase()) {
				case "naver" -> naverLoginService;
				case "google" -> googleLoginService;
				case "kakao" -> kakaoLoginService;
				default -> throw new IllegalArgumentException("정확한 로그인 서비스를 입력해주세요.");
		};
}
```

- `Factory` 클래스는 오직 객체 생성만을 관심을 가지고 있기 때문에 객체를 생성하는데에만 집중합니다.
- 혹시 `provider` 가 대소문자가 섞여있을 수 있기 때문에 소문자로 전부 전환합니다.
- 해당 타입에 객체를 반환하게 됩니다.

<br><br><br>

## Service

```java
public String retrieveUrlFromProvider(String provider) {
		return oAuthLoginFactory.getLoginService(provider).loginPage();
}

public String exchangeCodeForToken(String code, String provider) {
		return oAuthLoginFactory.getLoginService(provider).getToken(code);
}
```

- 팩토리를 통해 객체를 생성한후 로직을 수행합니다.
- 인터페이스로 공통로직을 정의를 해놨기 때문에 구체적인 서비스클래스에 비해 특별한 로직을 수행하는 코드가 없습니다.

<br><br><br>

## RestClientService

```java
public String exchangeCodeForToken(String tokenUri, LinkedMultiValueMap<String, String> body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		URI uri = URI.create(tokenUri);

		Map response = RestClient.create()
								.post()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(body)
                .retrieve()
                .body(Map.class);

		log.info("Response = {}", response);
		return (String) response.get("access_token");
}
```

- `body` 에 담겨진 데이터를 통해서 외부 `Authorization Server` 와 통신하게 되는 코드입니다.
- `ContentType` 은 모든 소셜로그인이 동일하게 `Application Form Url Enoceded` 를 사용하기에 적용해줍니다.
- `tokenUri` 를 통해 `POST` 방식으로 `body`에 데이터가 담겨서 통신하게됩니다.
- 통신을 하고 난후 데이터는 `Map.class`  데이터를 바인딩해준후 `Access Token` 정보를 꺼낸후 반환하게 됩니다.

<br><br><br><br><br><br><br><br><br><br><br>


> 📖 톺아보기

- Spring Security 와 OAuth Client 를 사용하지 못해서 꽤나 많은 부분을 구현해야 할 줄 알았지만 생각보다 간단하게 구현하게됬습니다.
- 확실히 편한 라이브러리를 사용하지 않고 진행하다보니 OAuth 가 어떻게 동작되는지 제대로 학습하게된 계기가 된것같습니다.
- 해당과제에 요구사항은 간단했지만 많은 부분을 생각하고 학습하고 구현했기 때문에 매우 재미있는 과제였던거 같습니다.
- Spring Security 와 OAuth Client 에 내부구조로 한번 제대로 공부해야겠다는 생각이 들었습니다. (학습비용은 크겠지만…. )
