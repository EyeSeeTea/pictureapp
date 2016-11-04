package org.eyeseetea.malariacare.layout.customization;

import android.view.ViewGroup;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;

public class LoginActivityCustomization{

    public void customize(LoginActivity loginActivity) {

        ViewGroup content = getSDKViewGroup(loginActivity);

        loginActivity.getLayoutInflater().inflate(R.layout.demo_login_button, content, true);
    }

    private ViewGroup getSDKViewGroup(LoginActivity loginActivity) {
        return (ViewGroup) loginActivity.findViewById(R.id.login_views_container);
    }
}
