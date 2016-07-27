package com.josebasalo.mvplogin.presenter;

import com.josebasalo.mvplogin.CWApplication;
import com.josebasalo.mvplogin.R;
import com.josebasalo.mvplogin.interfaces.SignInMVP;
import com.josebasalo.mvplogin.model.SignInModel;
import com.josebasalo.mvplogin.model.domain.LoginRequest;
import com.josebasalo.mvplogin.model.domain.SLoginGoogleRequest;
import com.josebasalo.mvplogin.view.SignInActivity;

import java.lang.ref.WeakReference;
import java.util.Map;

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
public class SignInPresenter implements SignInMVP.RequiredPresenterOps, SignInMVP.PresenterOps {

    // Layer View reference
    private WeakReference<SignInMVP.RequiredViewOps> mView;
    // Layer Model reference
    private SignInMVP.ModelOps mModel;
    // Configuration change state
    private boolean mIsChangingConfig;

    public SignInPresenter(SignInMVP.RequiredViewOps mView) {
        this.mView = new WeakReference<>(mView);
        this.mModel = new SignInModel(this);
    }

    /** View -> Presenter **/

    /**
     * Sent from Activity after a configuration changes
     * @param view  View reference
     */
    @Override
    public void onConfigurationChanged(SignInMVP.RequiredViewOps view) {
        mView = new WeakReference<>(view);
    }

    /**
     * Receives {@link SignInActivity#onDestroy()} event
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
    public void loginUserWithMyServer(String username, String password) {
        mModel.loginWithMyServer(new LoginRequest(username, password));
    }

    @Override
    public void loginUserWithGoogle(String tokenId) {
        mView.get().showIndeterminateDialog(
                CWApplication.getContext().getString(R.string.loggin_in_user),
                CWApplication.getContext().getString(R.string.please_wait)
        );
        SLoginGoogleRequest loginRequest = new SLoginGoogleRequest();
        loginRequest.setTokenId(tokenId);

        mModel.loginWithGoogle(loginRequest);
    }

    @Override
    public void loginUserWithTwitter(Map<String, String> headers) {
        mView.get().showIndeterminateDialog(
                CWApplication.getContext().getString(R.string.loggin_in_user),
                CWApplication.getContext().getString(R.string.please_wait)
        );
        mModel.loginWithTwitter(headers);
    }

    @Override
    public void loginUserWithFacebook(String token) {
        mView.get().showIndeterminateDialog(
                CWApplication.getContext().getString(R.string.loggin_in_user),
                CWApplication.getContext().getString(R.string.please_wait)
        );
        mModel.loginWithFacebook(token);
    }


    @Override
    public void restorePassword(String email) {
        mView.get().showIndeterminateDialog(
                CWApplication.getContext().getString(R.string.restore_password_in_process),
                CWApplication.getContext().getString(R.string.please_wait));
        mModel.restorePassword(email);
    }



    /*********************************************************************************************/

    @Override
    public void onValidUsername() {
        mView.get().validUsername();
    }

    /** Model -> Presenter **/

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
    public void onPasswordRestored() {
        mView.get().dismissIndeterminateDialog();
        mView.get().showAlert(CWApplication.getContext().getString(R.string.restore_successful));
    }

    @Override
    public void onLoginWithMyServer() {
        mView.get().dismissIndeterminateDialog();
        mView.get().loggedWithMyServer();
    }

    @Override
    public void onErrorLoginWithMyServer(String msg) {
        mView.get().errorLoginWithMyServer(msg);
    }

    @Override
    public void onLoginGoogle() {
        mView.get().dismissIndeterminateDialog();
        mView.get().loggedWithGoogle();
    }

    @Override
    public void onLoginTwitter() {
        mView.get().dismissIndeterminateDialog();
        mView.get().loggedWithTwitter();
    }

    @Override
    public void onLoginFacebook() {
        mView.get().dismissIndeterminateDialog();
        mView.get().loggedWithFacebook();
    }

    /**
     * receive errors
     */
    @Override
    public void onError(String errorMsg, boolean alert) {
        mView.get().dismissIndeterminateDialog();
        if (alert)
            mView.get().showAlert(errorMsg);
        else
            mView.get().showSnack(errorMsg);
    }
}
