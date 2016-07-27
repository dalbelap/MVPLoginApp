package com.josebasalo.mvplogin.interfaces;

import com.josebasalo.mvplogin.model.domain.LoginRequest;
import com.josebasalo.mvplogin.model.domain.SLoginGoogleRequest;

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

/*
 * Aggregates all communication operations between MVP pattern layer:
 * Model, View and Presenter
 */
public interface SignInMVP {
    /**
     * View mandatory methods. Available to Presenter
     *      Presenter -> View
     */
    interface RequiredViewOps {
        void showSnack(String msg);
        void showAlert(String msg);
        void showIndeterminateDialog(String title, String content);
        void dismissIndeterminateDialog();
        void errorUsername(String msg);
        void validUsername();
        void errorPassword(String msg);
        void validPassword();
        void loggedWithMyServer();
        void loggedWithGoogle();
        void loggedWithTwitter();
        void loggedWithFacebook();
        void errorLoginWithMyServer(String msg);
    }

    /**
     * Operations offered from Presenter to View
     *      View -> Presenter
     */
    interface PresenterOps{
        void onConfigurationChanged(RequiredViewOps view);
        void onDestroy(boolean isChangingConfig);
        void validateUserName(String userName);
        void validatePassword(String password);
        void loginUserWithMyServer(String username, String password);
        void loginUserWithGoogle(String tokenId);
        void loginUserWithTwitter(Map<String, String> headers);
        void loginUserWithFacebook(String token);
        void restorePassword(String email);
    }

    /**
     * Operations offered from Presenter to Model
     *      Model -> Presenter
     */
    interface RequiredPresenterOps {
        void onValidUsername();
        void onErrorUsername(String msg);
        void onErrorPassword(String msg);
        void onValidPassword();
        void onPasswordRestored();
        void onLoginWithMyServer();
        void onErrorLoginWithMyServer(String msg);
        void onLoginGoogle();
        void onLoginTwitter();
        void onLoginFacebook();
        void onError(String errorMsg, boolean alert);
    }

    /**
     * Model operations offered to Presenter
     *      Presenter -> Model
     */
    interface ModelOps {
        void onDestroy();
        void validateUsername(String username);
        void validatePassword(String password);
        void loginWithMyServer(LoginRequest login);
        void loginWithGoogle(SLoginGoogleRequest login);
        void loginWithTwitter(Map<String, String> headers);
        void loginWithFacebook(String token);
        void restorePassword(String email);
    }
}
