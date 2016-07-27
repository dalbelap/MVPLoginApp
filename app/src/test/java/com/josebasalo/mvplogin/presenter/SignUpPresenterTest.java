package com.josebasalo.mvplogin.presenter;

import com.josebasalo.mvplogin.BuildConfig;
import com.josebasalo.mvplogin.model.SignUpModel;
import com.josebasalo.mvplogin.view.SignUpActivity;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.doReturn;

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
@RunWith(JUnit4.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class SignUpPresenterTest {

    private SignUpModel mModel;
    private SignUpPresenter mPresenter, spyPresenter;
    private SignUpActivity mView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {

        mView = Mockito.mock(SignUpActivity.class);
        mPresenter = new SignUpPresenter(mView);
        mModel = Mockito.mock(SignUpModel.class);

        mPresenter.testWithModel(mModel);

        spyPresenter = Mockito.spy(mPresenter);
        doReturn(mModel).when(spyPresenter).getModel();
    }
}
