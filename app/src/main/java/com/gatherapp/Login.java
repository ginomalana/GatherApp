package com.gatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class Login extends AppCompatActivity {

    private TextView tvInfo;
    private LoginButton btnLogin;
    private CallbackManager callbackManager;
    private static ArrayList<String> userInfo;

    private DatabaseHandler db;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        tvInfo = (TextView)findViewById(R.id.info);
        btnLogin = (LoginButton)findViewById(R.id.btnLogin);

        btnLogin.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));

        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                userInfo = new ArrayList<>();
                userInfo.add(loginResult.getAccessToken().getUserId());

                db = new DatabaseHandler(getApplicationContext());

                boolean userExists = false;
                Log.wtf("TEST",userExists + ".");
                if (db.isExists(loginResult.getAccessToken().getUserId())) {
                    userExists = true;
                    finish();
                }
                final boolean asd = userExists;

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                Profile profile = Profile.getCurrentProfile();
                                userInfo.add(profile.getFirstName());
                                userInfo.add(profile.getLastName());
                                try {
                                    userInfo.add(object.getString("gender"));
                                    userInfo.add(object.getString("email"));
                                    userInfo.add(object.getString("birthday")); // 01/31/1980 format
                                    Log.wtf("USER INFO", userInfo.toString());
                                    if (!asd) {
                                        startActivityForResult(new Intent(Login.this, Register.class),1);
                                    }
                                } catch (Exception exc) {
                                    Log.wtf("USER INFO Exception", exc.toString());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                if (isLoggedIn()) {
                    startActivity(new Intent(Login.this, Home.class));
                    finish();
                }
            }

            @Override
            public void onCancel() {
                tvInfo.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                tvInfo.setText("Login attempt failed.");
            }
        });


        if (isLoggedIn()) {
            Log.wtf("USERID", Profile.getCurrentProfile().getId());
            editor.putString("UserId", Profile.getCurrentProfile().getId());
            editor.commit();
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void onLogoutClicked() {
        LoginManager.getInstance().logOut();
    }

    public ArrayList getUserInfo() {
        return userInfo;
    }

}
