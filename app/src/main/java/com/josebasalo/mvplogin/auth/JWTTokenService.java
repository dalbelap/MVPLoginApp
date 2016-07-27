package com.josebasalo.mvplogin.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.josebasalo.mvplogin.CWApplication;

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
public class JWTTokenService {
    private static final String JWT_KEY = "jwt";
    private static JWTTokenService mInstance = null;
    private String token;

    private JWTTokenService() {
        Context context = CWApplication.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.token = settings.getString(JWT_KEY, null);
    }

    public static JWTTokenService getInstance() {
        if (mInstance == null) {
            mInstance = new JWTTokenService();
        }
        return mInstance;
    }

    public boolean hasToken() {
        return token != null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;

        Context context = CWApplication.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JWT_KEY, token);
        editor.commit();
    }

    public void clearToken() {
        this.token = null;

        Context context = CWApplication.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(JWT_KEY);
        editor.apply();
    }
}
