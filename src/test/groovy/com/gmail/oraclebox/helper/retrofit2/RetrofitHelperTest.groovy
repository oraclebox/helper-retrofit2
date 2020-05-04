package com.gmail.oraclebox.helper.retrofit2

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

class RetrofitHelperTest extends Assert {

    @Test
    void testGoogleCom() {
        Api api = RetrofitHelper.getRetrofitStringConverter('https://www.google.com/', null, 0, new ObjectMapper()).create(Api.class);
        Response<String> response = api.testGoogle().execute();
        assertTrue(response.code() < 300);
        assertNotNull(response.body());

    }

    interface Api {

        @GET('/')
        Call<String> testGoogle();

    }

    class SignInRequest {
        String domainName;
        String password;
    }

}
