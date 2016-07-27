package com.josebasalo.mvplogin.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.josebasalo.mvplogin.R;
import com.josebasalo.mvplogin.StateMaintainer;
import com.josebasalo.mvplogin.interfaces.SignUpMVP;
import com.josebasalo.mvplogin.presenter.SignUpPresenter;

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
public class SignUpActivity extends AppCompatActivity implements SignUpMVP.RequiredViewOps {

    protected final String TAG = getClass().getSimpleName();
    public static final String KEY_LOGIN = "login_username";

    @Bind(R.id.ll_sign_up) LinearLayout mParent;
    @Bind(R.id.et_input_login) EditText mLoginView;
    @Bind(R.id.et_input_email) EditText mEmailView;
    @Bind(R.id.et_input_pass) EditText mPassView;
    @Bind(R.id.til_sign_up_login) TextInputLayout mLoginILView;
    @Bind(R.id.til_sign_up_email) TextInputLayout mEmailILView;
    @Bind(R.id.til_sign_up_pass) TextInputLayout mPassILView;
    @Bind(R.id.sign_up_show_pass) ToggleButton mShowPassView;
    
    // Responsible to maintain the Objects state
    // during changing configuration
    public StateMaintainer mStateMaintainer =
            new StateMaintainer( this.getSupportFragmentManager(), TAG );

    // Presenter operations
    public SignUpMVP.PresenterOps mPresenter;

    MaterialDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMVPOps();
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_action_navigation_arrow_back);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(null);
        }

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

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.validateEmail(s.toString().trim());
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

                final Animation animFadeOut = AnimationUtils.loadAnimation(SignUpActivity.this, android.R.anim.fade_out);
                final Animation animFadeIn = AnimationUtils.loadAnimation(SignUpActivity.this, android.R.anim.fade_in);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        validUsername();
        validEmail();
        validPassword();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_sign_up)
    void clickSignUp() {
        mPresenter.validateUserName(mLoginView.getText().toString().trim());
        mPresenter.validatePassword(mPassView.getText().toString().trim());
        mPresenter.validateEmail(mEmailView.getText().toString().trim());

        if (!mLoginILView.isErrorEnabled() && !mPassILView.isErrorEnabled() && !mEmailILView.isErrorEnabled())
            mPresenter.createAccount(
                    mLoginView.getText().toString().trim(),
                    mPassView.getText().toString().trim(),
                    mEmailView.getText().toString().trim());
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
    public void errorEmail(String msg) {
        mEmailILView.setErrorEnabled(true);
        mEmailILView.setError(msg);
        if (mLoginILView.isErrorEnabled())
            requestFocus(mLoginView);
        else
            requestFocus(mEmailView);
    }

    @Override
    public void validEmail() {
        mEmailILView.setError(null);
        mEmailILView.setErrorEnabled(false);
    }

    @Override
    public void createdAccount(final String username) {
        new MaterialDialog.Builder(SignUpActivity.this)
                .content(R.string.successful_registration)
                .positiveText(R.string.accept)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_LOGIN, username);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void errorPassword(String msg) {
        mPassILView.setErrorEnabled(true);
        mPassILView.setError(msg);
        if (mLoginILView.isErrorEnabled())
            requestFocus(mLoginView);
        else if (mEmailILView.isErrorEnabled())
            requestFocus(mEmailView);
        else
            requestFocus(mPassView);
    }

    @Override
    public void validPassword() {
        mPassILView.setError(null);
        mPassILView.setErrorEnabled(false);
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
    private void initialize(SignUpMVP.RequiredViewOps view)
            throws InstantiationException, IllegalAccessException{
        mPresenter = new SignUpPresenter(view);
        mStateMaintainer.put(SignUpMVP.PresenterOps.class.getSimpleName(), mPresenter);
    }

    /**
     * Recovers Presenter and informs Presenter that occurred a config change.
     * If Presenter has been lost, recreates a instance
     */
    private void reinitialize(SignUpMVP.RequiredViewOps view)
            throws InstantiationException, IllegalAccessException {
        mPresenter = mStateMaintainer.get(SignUpMVP.PresenterOps.class.getSimpleName());

        if (mPresenter == null) {
            Log.w(TAG, "recreating Presenter");
            initialize(view);
        } else {
            mPresenter.onConfigurationChanged(view);
        }
    }
}
