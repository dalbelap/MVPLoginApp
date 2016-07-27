package com.josebasalo.mvplogin.presenter;

import com.josebasalo.mvplogin.CWApplication;
import com.josebasalo.mvplogin.R;
import com.josebasalo.mvplogin.interfaces.SignUpMVP;
import com.josebasalo.mvplogin.model.SignUpModel;
import com.josebasalo.mvplogin.model.domain.RegisterRequest;
import com.josebasalo.mvplogin.view.SignUpActivity;

import java.lang.ref.WeakReference;

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
public class SignUpPresenter implements SignUpMVP.RequiredPresenterOps, SignUpMVP.PresenterOps {

    // Layer View reference
    private WeakReference<SignUpMVP.RequiredViewOps> mView;
    // Layer Model reference
    private SignUpMVP.ModelOps mModel;
    // Configuration change state
    private boolean mIsChangingConfig;

    public SignUpPresenter(SignUpMVP.RequiredViewOps mView) {
        this.mView = new WeakReference<>(mView);
        this.mModel = new SignUpModel(this);
    }

    public void setView(SignUpMVP.RequiredViewOps mView) {
        this.mView = new WeakReference<>(mView);
    }

    public void testWithModel(SignUpModel model) {
        mModel = model;
    }

    public SignUpMVP.ModelOps getModel() {
        return mModel;
    }

    /**
     * Sent from Activity after a configuration changes
     * @param view  View reference
     */
    @Override
    public void onConfigurationChanged(SignUpMVP.RequiredViewOps view) {
        mView = new WeakReference<>(view);
    }

    /**
     * Receives {@link SignUpActivity#onDestroy()} event
     * @param isChangingConfig  Config change state
     */
    @Override
    public void onDestroy(boolean isChangingConfig) {
        mView = null;
        mIsChangingConfig = isChangingConfig;
        if ( !isChangingConfig ) {
            mModel.onDestroy();
        }
    }

    @Override
    public void validateUserName(String userName) {
        mModel.validateUsername(userName);
    }

    @Override
    public void validatePassword(String password) {
        mModel.validatePassword(password);
    }

    @Override
    public void validateEmail(String email) {
        mModel.validateEmail(email);
    }

    @Override
    public void createAccount(String username, String password, String email) {
        mView.get().showIndeterminateDialog(
                CWApplication.getContext().getString(R.string.sign_up_in_process),
                CWApplication.getContext().getString(R.string.please_wait)
        );
        RegisterRequest account = new RegisterRequest();
        account.setEmail(email);
        account.setLogin(username);
        account.setLangKey("en");
        account.setPassword(password);
        mModel.createAccount(account);
    }

    /*********************************************************************************************/

    @Override
    public void onValidUsername() {
        mView.get().validUsername();
    }

    @Override
    public void onErrorUsername(String msg) {
        mView.get().errorUsername(msg);
    }

    @Override
    public void onErrorPassword(String msg) {
        mView.get().errorPassword(msg);
    }

    @Override
    public void onValidPassword() {
        mView.get().validPassword();
    }

    @Override
    public void onValidEmail() {
        mView.get().validEmail();
    }

    @Override
    public void onErrorEmail(String msg) {
        mView.get().errorEmail(msg);
    }

    @Override
    public void onCreatedAccount(String username) {
        mView.get().dismissIndeterminateDialog();
        mView.get().createdAccount(username);
    }

    @Override
    public void onError(String errorMsg, boolean alert) {
        mView.get().dismissIndeterminateDialog();
        if (alert)
            mView.get().showAlert(errorMsg);
        else
            mView.get().showSnack(errorMsg);
    }
}
