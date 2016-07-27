package com.josebasalo.mvplogin.model;

import android.util.Log;

import com.josebasalo.mvplogin.CWApplication;
import com.josebasalo.mvplogin.R;
import com.josebasalo.mvplogin.Utils;
import com.josebasalo.mvplogin.api.ApiClient;
import com.josebasalo.mvplogin.interfaces.SignUpMVP;
import com.josebasalo.mvplogin.model.domain.RegisterRequest;

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
public class SignUpModel implements SignUpMVP.ModelOps {

    // Presenter reference
    private SignUpMVP.RequiredPresenterOps mPresenter;

    public SignUpModel(SignUpMVP.RequiredPresenterOps mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void onDestroy() {

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
    public void validateEmail(String email) {
        if (email.isEmpty()) {
            mPresenter.onErrorEmail(CWApplication.getContext().getString(R.string.msg_error_email_empty));
        } else if (!Utils.isValidEmail(email)) {
            mPresenter.onErrorEmail(CWApplication.getContext().getString(R.string.msg_error_email_not_valid));
        } else
            mPresenter.onValidEmail();

    }

    @Override
    public void createAccount(final RegisterRequest account) {

        if (!CWApplication.getApplication().isDeviceOnline()) {
            mPresenter.onError(CWApplication.getContext().getString(R.string.no_internet_connection), false);
            return;
        }

        Call<Void> call = ApiClient.getsMyServerApiService().register(account);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mPresenter.onCreatedAccount(account.getLogin());
                } else {
                    int statusCode = response.code();
                    switch (statusCode) {
                        case 400:
                            mPresenter.onError(CWApplication.getContext().getString(R.string.username_or_email_already_in_use), false);
                            break;
                        default:
                            mPresenter.onError(CWApplication.getContext().getString(R.string.error_signing_up_common), false);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(getClass().getSimpleName(), t.toString());
                mPresenter.onError(CWApplication.getContext().getString(R.string.error_signing_up_common), false);
            }
        });
    }
}
