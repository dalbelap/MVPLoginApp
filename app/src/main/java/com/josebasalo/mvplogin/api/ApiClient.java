package com.josebasalo.mvplogin.api;

import android.util.Log;

import com.josebasalo.mvplogin.BuildConfig;
import com.josebasalo.mvplogin.auth.JWTTokenService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The MIT License (MIT)
 *
 *Copyright (c) 2016 Jose Basalo
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 *associated documentation files (the "Software"), to deal in the Software without restriction,
 *including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 *furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or substantial
 *portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 *NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();
    private static final String SERVER_URL = "";

    private static MyServerApiService sMyServerApiService;
    private static JWTTokenService sJWTTokenService;
    private static Retrofit retrofit;

    public static MyServerApiService getsMyServerApiService() {
        if (sMyServerApiService == null) {
            sJWTTokenService = JWTTokenService.getInstance();

            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthenticatingInterceptor());

            if (BuildConfig.DEBUG) {
                client.addInterceptor(new LoggingInterceptor());
            }

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            sMyServerApiService = retrofit.create(MyServerApiService.class);
        }

        return sMyServerApiService;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            getsMyServerApiService();
            return retrofit;
        }
        return retrofit;
    }

    private static class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.d(TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.d(TAG, String.format("Received response for %s in %.1fms%n%s",
                   response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

    private static class AuthenticatingInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {

            Request request = chain.request();
            Request modifiedRequest = request;
            String jwt = sJWTTokenService.getToken();
            if(jwt != null) {
                modifiedRequest = request.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + jwt)
                        .build();
            }

            return chain.proceed(modifiedRequest);
        }
    }

}
