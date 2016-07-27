package com.josebasalo.mvplogin.model;

import android.util.Log;

import com.josebasalo.mvplogin.CWApplication;
import com.josebasalo.mvplogin.R;
import com.josebasalo.mvplogin.api.ApiClient;
import com.josebasalo.mvplogin.auth.JWTTokenService;
import com.josebasalo.mvplogin.interfaces.SignInMVP;
import com.josebasalo.mvplogin.model.domain.LoginRequest;
import com.josebasalo.mvplogin.model.domain.LoginResult;
import com.josebasalo.mvplogin.model.domain.SLoginGoogleRequest;
import com.josebasalo.mvplogin.presenter.SignInPresenter;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
public class SignInModel implements SignInMVP.ModelOps {

    // Presenter reference
    private SignInMVP.RequiredPresenterOps mPresenter;

    public SignInModel(SignInMVP.RequiredPresenterOps mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void validateUsername(String username) {
        if (username.isEmpty())
            mPresenter.onErrorUsername(CWApplication.getContext().getString(R.string.msg_error_username));

        else if (username.length() > 50)
            mPresenter.onErrorUsername(CWApplication.getContext().getString(R.string.msg_error_username_max_length));
        else
            mPresenter.onValidUsername();

    }

    @Override
    public void validatePassword(String password) {
        if (password.isEmpty()) {
            mPresenter.onErrorPassword(CWApplication.getContext().getString(R.string.msg_error_password_empty));
        } else if(password.length() < 4 || password.length() >100)
            mPresenter.onErrorPassword(CWApplication.getContext().getString(R.string.msg_error_password_min_max_length));
        else
            mPresenter.onValidPassword();
    }

    @Override
    public void loginWithMyServer(LoginRequest login) {
        if (!CWApplication.getApplication().isDeviceOnline()) {
            mPresenter.onError(CWApplication.getContext().getString(R.string.no_internet_connection), false);
            return;
        }

        Call<LoginResult> call = ApiClient.getsMyServerApiService().login(login);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    JWTTokenService.getInstance().setToken(response.body().getIdToken());
                    mPresenter.onLoginWithMyServer();
                } else {
                    int statusCode = response.code();
                    switch (statusCode) {
                        case 401:
                            mPresenter.onErrorLoginWithMyServer(CWApplication.getContext().getString(R.string.error_signing_in_credentials));
                            break;
                        default:
                            mPresenter.onError(CWApplication.getContext().getString(R.string.error_signing_in_common), false);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.d(getClass().getSimpleName(), t.toString());
                mPresenter.onError(CWApplication.getContext().getString(R.string.error_signing_up_common), false);
            }
        });

    }

    @Override
    public void loginWithGoogle(SLoginGoogleRequest login) {
        if (!CWApplication.getApplication().isDeviceOnline()) {
            mPresenter.onError(CWApplication.getContext().getString(R.string.no_internet_connection), false);
            return;
        }

        Call<LoginResult> call = ApiClient.getsMyServerApiService().socialSiginGoogle(login);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    JWTTokenService.getInstance().setToken(response.body().getIdToken());
                    mPresenter.onLoginGoogle();
                } else {
                    int statusCode = response.code();
                    switch (statusCode) {
                        default:
                            mPresenter.onError(CWApplication.getContext().getString(R.string.error_signing_in_common), false);
                            break;
                    }

                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.d(getClass().getSimpleName(), t.toString());
                mPresenter.onError(CWApplication.getContext().getString(R.string.error_signing_up_common), false);
            }
        });
    }

    @Override
    public void loginWithTwitter(Map<String, String> headers) {

    }

    @Override
    public void loginWithFacebook(String token) {

    }

    @Override
    public void restorePassword(String email) {

    }

    /**
     * Sent from {@link SignInPresenter#onDestroy(boolean)}
     * Should stop/kill operations that could be running
     * and aren't needed anymore
     */
    @Override
    public void onDestroy() {
        // destroying actions
    }


}
