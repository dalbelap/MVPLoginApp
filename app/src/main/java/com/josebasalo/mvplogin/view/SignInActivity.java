package com.josebasalo.mvplogin.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.josebasalo.mvplogin.R;
import com.josebasalo.mvplogin.StateMaintainer;
import com.josebasalo.mvplogin.Utils;
import com.josebasalo.mvplogin.interfaces.SignInMVP;
import com.josebasalo.mvplogin.presenter.SignInPresenter;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.OAuthSigning;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
public class SignInActivity extends AppCompatActivity implements
        SignInMVP.RequiredViewOps,
        GoogleApiClient.OnConnectionFailedListener{

    protected final String TAG = getClass().getSimpleName();
    private static final int PICK_REGISTER = 0;

    // Responsible to maintain the Objects state
    // during changing configuration
    public final StateMaintainer mStateMaintainer =
            new StateMaintainer( this.getSupportFragmentManager(), TAG );
    // Presenter operations
    private SignInMVP.PresenterOps mPresenter;

    @Bind(R.id.ll_sign_in) RelativeLayout mParent;
    @Bind(R.id.et_input_username) EditText mLoginView;
    @Bind(R.id.et_input_pass) EditText mPassView;
    @Bind(R.id.til_sign_in_username) TextInputLayout mLoginILView;
    @Bind(R.id.til_sign_in_pass) TextInputLayout mPassILView;
    @Bind(R.id.sign_in_show_pass) ToggleButton mShowPassView;

    MaterialDialog pDialog;

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private TwitterAuthClient mTwitterAuthClient;
    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMVPOps();
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        mLoginView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.validateUserName(s.toString().trim());
            }
        });

        mPassView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                final Animation animFadeOut = AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_out);
                final Animation animFadeIn = AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_in);

                switch (s.toString().length()) {
                    case 0:
                        mShowPassView.setAnimation(animFadeOut);
                        mShowPassView.setVisibility(View.GONE);
                        break;

                    case 1:
                        if (mShowPassView.getVisibility() == View.GONE) {
                            mShowPassView.setChecked(true);
                            mShowPassView.setAnimation(animFadeIn);
                            mShowPassView.setVisibility(View.VISIBLE);
                            //TODO: fix bottom padding mShowPassView when show error
                        }
                        break;
                }

                mPresenter.validatePassword(s.toString().trim());

            }
        });

        mShowPassView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    mPassView.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    // hide password
                    mPassView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }

                mPassView.setSelection(mPassView.getText().length());
            }
        });

        FacebookSdk.sdkInitialize(this.getApplicationContext());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mTwitterAuthClient != null) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }

        if (mFacebookCallbackManager != null) {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == PICK_REGISTER) {
            if (resultCode == RESULT_OK) {

                String username = data.getStringExtra(SignUpActivity.KEY_LOGIN);

                mLoginView.setText(username);
                mPassView.setText(null);
                requestFocus(mPassView);
            }
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    @OnClick(R.id.bt_sign_in)
    void clickSignIn() {
        mPresenter.validateUserName(mLoginView.getText().toString().trim());
        mPresenter.validatePassword(mPassView.getText().toString().trim());

        if (!mLoginILView.isErrorEnabled() && !mPassILView.isErrorEnabled())
            mPresenter.loginUserWithMyServer(mLoginView.getText().toString().trim(), mPassView.getText().toString().trim());
    }

    @OnClick(R.id.tv_forgot_pass)
    void clickForgotPassword() {
        new MaterialDialog.Builder(this)
                .title(R.string.restore_password)
                .content(R.string.enter_your_email)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .positiveText(R.string.restore)
                .neutralText(R.string.cancel)
                .alwaysCallInputCallback()
                .theme(Theme.LIGHT)
                .input(R.string.hint_user_email, 0, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!Utils.isValidEmail(input.toString().trim())) {
                            dialog.getContentView().setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            dialog.setContent(R.string.valid_email);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.getContentView().setTextColor(getResources().getColor(R.color.text_secondary));
                            dialog.setContent(R.string.enter_your_email);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mPresenter.restorePassword(dialog.getInputEditText().getText().toString().trim());
                    }
                }).show();
    }

    @OnClick(R.id.tv_sign_up)
    void clickCreateAccount() {
        startActivityForResult(new Intent(SignInActivity.this, SignUpActivity.class), PICK_REGISTER);
    }

    @OnClick(R.id.bt_sign_in_google)
    void clickSignInGoogle() {
        if (mGoogleApiClient == null) {
            // Request only the user's ID token, which can be used to identify the
            // user securely to your backend. This will contain the user's basic
            // profile (name, profile picture URL, etc) so you should not need to
            // make an additional call to personalize your application.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_id_google))
                    .requestEmail()
                    .requestProfile()
                    .build();
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @OnClick(R.id.bt_sign_in_twitter)
    void clickSignInTwitter() {
        if (mTwitterAuthClient == null) {
            mTwitterAuthClient = new TwitterAuthClient();
        }

        mTwitterAuthClient.authorize(SignInActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = result.data;
                String username = session.getUserName();
                Log.d(TAG, "Twitter username: " + username);

                //OAuth Echo:
                //encapsulates the credentials to identify your Twitter application
                TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
                //represents the user credentials of a Twitter user
                TwitterAuthToken authToken = session.getAuthToken();

                OAuthSigning oauthSigning = new OAuthSigning(authConfig, authToken);
                Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();

                mPresenter.loginUserWithTwitter(authHeaders);

            }

            @Override
            public void failure(TwitterException e) {
                Log.d("TwitterKit", "Login with Twitter failure", e);
            }
        });
    }

    @OnClick(R.id.bt_sign_in_facebook)
    void clickSignInFacebook() {
        if (mFacebookCallbackManager == null) {
            mFacebookCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "Success Login facebook");
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "Cancel Login facebook");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "Error Login facebook " + error.toString());
                }
            });
        }

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

    }

    @Override
    public void showSnack(String msg) {
        Snackbar.make(mParent, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAlert(String msg) {
        pDialog = new MaterialDialog.Builder(this)
                .content(msg)
                .theme(Theme.LIGHT)
                .positiveText(R.string.accept)
                .show();
    }

    @Override
    public void showIndeterminateDialog(String title, String content) {
        pDialog = new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .theme(Theme.LIGHT)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    @Override
    public void dismissIndeterminateDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void errorUsername(String msg) {
        mLoginILView.setErrorEnabled(true);
        mLoginILView.setError(msg);
        requestFocus(mLoginView);
    }

    @Override
    public void validUsername() {
        mLoginILView.setError(null);
        mLoginILView.setErrorEnabled(false);
    }

    @Override
    public void errorPassword(String msg) {
        mPassILView.setErrorEnabled(true);
        mPassILView.setError(msg);
        if (mLoginILView.isErrorEnabled())
            requestFocus(mLoginView);
        else
            requestFocus(mPassView);
    }

    @Override
    public void validPassword() {
        mPassILView.setError(null);
        mPassILView.setErrorEnabled(false);
    }

    @Override
    public void loggedWithMyServer() {

    }

    @Override
    public void loggedWithGoogle() {

    }

    @Override
    public void loggedWithTwitter() {

    }

    @Override
    public void loggedWithFacebook() {

    }

    @Override
    public void errorLoginWithMyServer(String msg) {
        mLoginView.setText(null);
        mPassView.setText(null);
        requestFocus(mLoginView);
        showSnack(msg);
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Initialize and restart the Presenter.
     * This method should be called after {@link Activity#onCreate(Bundle)}
     */
    public void startMVPOps() {
        try {
            if (mStateMaintainer.firstTimeIn()) {
                Log.d(TAG, "onCreate() called for the first time");
                initialize(this);
            } else {
                Log.d(TAG, "onCreate() called more than once");
                reinitialize(this);
            }
        } catch (InstantiationException e) {
            Log.d(TAG, "onCreate() " + e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            Log.d(TAG, "onCreate() " + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize relevant MVP Objects.
     * Creates a Presenter instance, saves the presenter in {@link StateMaintainer}
     */
    private void initialize(SignInMVP.RequiredViewOps view)
            throws InstantiationException, IllegalAccessException{
        mPresenter = new SignInPresenter(view);
        mStateMaintainer.put(SignInMVP.PresenterOps.class.getSimpleName(), mPresenter);
    }

    /**
     * Recovers Presenter and informs Presenter that occurred a config change.
     * If Presenter has been lost, recreates a instance
     */
    private void reinitialize(SignInMVP.RequiredViewOps view)
            throws InstantiationException, IllegalAccessException {
        mPresenter = mStateMaintainer.get(SignInMVP.PresenterOps.class.getSimpleName());

        if (mPresenter == null) {
            Log.w(TAG, "recreating Presenter");
            initialize(view);
        } else {
            mPresenter.onConfigurationChanged(view);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            Log.d(TAG, "id_token: " + idToken);

            mLoginView.setText(idToken);

            mPresenter.loginUserWithGoogle(idToken);

        } else {
            Log.d(TAG, "id_token: null");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
