package com.josebasalo.mvplogin.view;

import android.os.Bundle;

import com.josebasalo.mvplogin.BuildConfig;
import com.josebasalo.mvplogin.StateMaintainer;
import com.josebasalo.mvplogin.interfaces.SignUpMVP;
import com.josebasalo.mvplogin.model.SignUpModel;
import com.josebasalo.mvplogin.presenter.SignUpPresenterTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class SignUpActivityTest {

    private SignUpActivity activity;
    private ActivityController<SignUpActivity> controller;
    private SignUpMVP.PresenterOps presenterOps;
    private StateMaintainer stateMaintainer;

    @Before
    public void before(){
        controller = Robolectric.buildActivity(SignUpActivity.class);
        presenterOps = Mockito.mock(SignUpMVP.PresenterOps.class);
        stateMaintainer = Mockito.mock(StateMaintainer.class);
    }

    @Test
    public void testOnCreate() {
        activity = controller.get();
        when(stateMaintainer.firstTimeIn()).thenReturn(true);

        activity.mStateMaintainer = stateMaintainer;

        controller.create();

        Mockito.verify(stateMaintainer, VerificationModeFactory.atLeast(2)).put(any((SignUpPresenterTest.class)));
        Mockito.verify(stateMaintainer, VerificationModeFactory.atLeast(2)).put(any(SignUpModel.class));

        assertNotNull(activity.mPresenter);
    }

    @Test
    public void testOnCreateReconfig(){
        controller = Robolectric.buildActivity(SignUpActivity.class);
        activity = controller.get();

        when(stateMaintainer.firstTimeIn()).thenReturn(false);
        when(stateMaintainer.get(SignUpPresenterTest.class.getName())).thenReturn(presenterOps);
        activity.mStateMaintainer = stateMaintainer;

        Bundle savedInstanceState = new Bundle();
        controller
                .create(savedInstanceState)
                .start()
                .restoreInstanceState(savedInstanceState)
                .postCreate(savedInstanceState)
                .resume()
                .visible();

        Mockito.verify(stateMaintainer).get(SignUpPresenterTest.class.getName());
        Mockito.verify(presenterOps).setView(any(SignUpMVP.RequiredViewOps.class));

    }

    @Test
    public void testOnDestroy(){
        controller.create();
        activity = controller.get();
        activity.mPresenter = presenterOps;

        controller.destroy();
        Mockito.verify(presenterOps).onDestroy(Mockito.anyBoolean());

    }

}
