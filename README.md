# Helper Retrofit2

A library adopt Retrofit 2 and OkHttp as a REST client with proxy.

[Retrofit2](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java

[OKHttp](https://square.github.io/okhttp/) - OkHttp is an HTTP client

### How to use 

Build and Import this library jar into your project

```Groovy
// Build 
gradlew clean build

// Gradle import from your maven repository
repositories {
    mavenCentral()
    maven { url 'http://xxxxx.com/artifactory/libs' }
}

dependencies {
    compile(group: 'com.gmail.oraclebox', name: 'helper-retrofit2', version: '1.0.0')
}
```

### Create Remote API endpoints

```java
interface Api {

    @FormUrlEncoded
    @POST('oauth2/token')
    Call<OAuthResponse> oauth(@Field("client_id") String clientId,       @Field("client_secret") String clientSecret, @Field("grant_type") String grantType, @Field("resource") String resource);

    @GET('greeting')
    Call<Message> getTrailerOrders(@Header("Authorization") String accessToken);

    // Remote Return String only
    @GET('/')
    Call<String> testGoogle();
}

```

### Make connection to remote service with proxy

```groovy
Api api = RetrofitHelper.getRetofit(endpoint.uri, proxyHost, port, objectMapper).create(Api.class);
Response<Message> response = api.getTrailerOrders(accessToken).execute();
if (response.isSuccessful()) {
   // Success
}
```

### Make connection to remote service which return String body

```java
Api api = RetrofitHelper.getRetrofitStringConverter('https://www.google.com/', null, 0, new ObjectMapper()).create(Api.class);
Response<String> response = api.testGoogle().execute();
assertTrue(response.code() < 300);
assertNotNull(response.body());
```

### Make connection to remote service with Unsigned SSL cert

```java
Api api = RetrofitHelper.getUnsafeRetrofit('https://selfsign.com/', null, 0, new ObjectMapper(), new HttpLogger()).create(Api.class);
Response<Object> response = api.signInAccount(new SignInRequest(domainName: 'xxxxxxx', password: 'xxxxxx')).execute();
assertTrue(response.code() > 300);
```



### Remark

The libaray use Jackson for Json serialization and deserialization, you can pass Spring's ObjectMapper to method.