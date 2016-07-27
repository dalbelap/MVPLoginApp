package com.josebasalo.mvplogin.api;

import com.josebasalo.mvplogin.model.domain.LoginRequest;
import com.josebasalo.mvplogin.model.domain.LoginResult;
import com.josebasalo.mvplogin.model.domain.RegisterRequest;
import com.josebasalo.mvplogin.model.domain.SLoginGoogleRequest;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
public interface MyServerApiService {

    @POST("/api/authenticate")
    Call<LoginResult> login(@Body LoginRequest body);

    @POST("/api/register")
    Call<Void> register(@Body RegisterRequest body);

    @POST("/api/account/reset_password/init")
    Call<Void> resetPassword(@Body RequestBody body);

    @GET("/social/signin-app")
    Call<LoginResult> socialSiginGoogle(@Body SLoginGoogleRequest body);
}
