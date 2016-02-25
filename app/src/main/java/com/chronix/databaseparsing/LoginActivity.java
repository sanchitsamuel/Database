package com.chronix.databaseparsing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private View mNameLayout;
    private EditText mEmailEditText;
    private View mEmailLayout;
    private EditText mPasswordText;
    private View mPasswordLayout;
    private Button mLoginButton;
    private TextView mTextView;
    private ProgressBar mProgressView;
    private TextView mLoginTextView;
    private ImageView mImageView;

    private LinearLayout linearLayout;

    private boolean register = true;

    private static final String SETTINGS_NAME = "MySettingsFile";
    private static final String DATABASE_NAME = "userDB";
    private SharedPreferences settings;

    private final String LINK_MAIN = "http://192.168.1.4/";
    private final String SERVICE_REGISTER = "register.php?";
    private final String SERVICE_EMAIL_CHECK = "email.php?";
    private final String SERVICE_AUTHENTICATION = "id.php?";
    private final String SERVICE_LOGIN = "login.php?";

    private final String REGISTER_ID = "id=";
    private final String REGISTER_NAME = "name=";
    private final String REGISTER_EMAIL = "email=";
    private final String REGISTER_PASSWORD = "password=";
    private boolean network;

    private Database database;
    ResponseClass responseClass;

    private UserRegisterTask mUserRegisterTask = null;
    private Authentication mAuthentication = null;
    private Login mLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mNameEditText = (EditText) findViewById(R.id.name);
        mNameLayout = findViewById(R.id.name_layout);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mEmailLayout = findViewById(R.id.email_layout);
        mPasswordText = (EditText) findViewById(R.id.password);
        mPasswordLayout = findViewById(R.id.password_layout);
        mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
        mTextView = (TextView) findViewById(R.id.action_register);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        mLoginTextView = (TextView) findViewById(R.id.login_text);
        mImageView = (ImageView) findViewById(R.id.image);

        linearLayout = (LinearLayout) findViewById(R.id.email_login_form);
        database = new Database(this);
        network = database.isNetworkAvailable();

        animate(!true);
        //showProgressCircle(!true);

        mEmailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    if (!register) {
                        // attemptRegister();
                    } else {
                        // attemptLogin();
                    }
                    return true;
                }
                return false;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!register) {
                    // attemptRegister();
                    attemptRegister();
                } else {
                    // attemptLogin();
                    attemptLogin();
                }
            }
        });
    }

    private void attemptLogin() {
        if (mLogin != null) {
            return;
        }

        // reset errors
        mEmailEditText.setError(null);
        mPasswordText.setError(null);

        // save values
        String email = mEmailEditText.getText().toString();
        String password = mPasswordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordText;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgressCircle(true);
            if (network) {
                mLogin = new Login(email, password);
                mLogin.execute((Void) null);
            } else {
                Snackbar.make(linearLayout, "No internet connection.", Snackbar.LENGTH_LONG).show();
                showProgressCircle(false);
            }
        }
    }

    private void attemptRegister() {
        if (mUserRegisterTask != null) {
            return;
        }

        // reset errors
        mEmailEditText.setError(null);
        mNameEditText.setError(null);
        mPasswordText.setError(null);

        // save values
        String email = mEmailEditText.getText().toString();
        String name = mNameEditText.getText().toString();
        String password = mPasswordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // check for valid password.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordText;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgressCircle(true);
            if (network) {
                mUserRegisterTask = new UserRegisterTask(email, password, name);
                mUserRegisterTask.execute((Void) null);
            } else {
                Snackbar.make(linearLayout, "No internet connection.", Snackbar.LENGTH_LONG).show();
                showProgressCircle(false);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void showProgressCircle(final boolean show) {

        if (!show) {        // for login and registrations
            mLoginTextView.setVisibility(show ? View.VISIBLE : View.GONE);
            linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        } else {        // for registered users, increase the slash time.
            mLoginTextView.setVisibility(show ? View.VISIBLE : View.GONE);
            linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
        //boolean status = settings.getBoolean("isLoggedIn", false);
        String id = settings.getString("ID", null);
        if (network) {
            mAuthentication = new Authentication(id);
            mAuthentication.execute((Void) null);
        } else {
            Snackbar.make(linearLayout, "No internet connection.", Snackbar.LENGTH_INDEFINITE).show();
            showProgressCircle(false);
        }
    }

    public void onRegister(View view) {
        if (!register) {  // name hiding
            //mLoginButton.animate().translationY(-mNameEditText.getHeight()/3);
            mNameLayout.setVisibility(View.GONE);
            mLoginButton.setText(R.string.action_sign_in);
            mTextView.setText(R.string.action_or_register);
            register = true;
        } else {    // name coming visible
            //mLoginButton.animate().translationYBy(mEmailEditText.getHeight()/3).setDuration(1000);
            mLoginButton.setText(R.string.action_register);
            //mTextView.animate().translationYBy(mEmailEditText.getHeight()/3).setDuration(1000);
            mTextView.setText(R.string.action_or_sign_in);
            mNameLayout.setVisibility(View.VISIBLE);
            register = false;
        }
    }

    void animate(boolean show) {
        linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final Animation imageViewAnim = AnimationUtils.loadAnimation(this, R.anim.image_view_anim);
        mImageView.startAnimation(imageViewAnim);
        imageViewAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.animate().translationY(-16).setDuration(500).setStartDelay(4000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mEmailLayout.setVisibility(View.VISIBLE);
                        mPasswordLayout.setVisibility(View.VISIBLE);
                        mLoginButton.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.VISIBLE);
                        mEmailLayout.setAnimation(fadeIn);
                        mPasswordLayout.setAnimation(fadeIn);
                        mLoginButton.setAnimation(fadeIn);
                        mTextView.setAnimation(fadeIn);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    class Login extends AsyncTask<Void, Void, String> {
        private final String mEmail;
        private final String mPassword;
        private OkHttpClient client;

        Login (String email, String password) {
            mEmail = email;
            mPassword = password;
            client = new OkHttpClient();
        }

        String run() throws Exception {
            Request emailCheckRequest = new Request.Builder()
                    .url(LINK_MAIN+SERVICE_LOGIN+REGISTER_EMAIL+mEmail+"&"+REGISTER_PASSWORD+mPassword)
                    .build();
            Response emailCheckResponse = client.newCall(emailCheckRequest).execute();
            if (!emailCheckResponse.isSuccessful()) throw new IOException("Unexpected code " + emailCheckResponse);
            JSONObject jsonObject = new JSONObject(emailCheckResponse.body().string());
            if (jsonObject.getInt("success") == 1)
                return jsonObject.getString("message");
            return null;
        }

        @Override
        protected String doInBackground(Void... params) {
            String id = null;
            try {
                id = run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return id;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null) {
                settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
                settings.edit().putString("ID", s).apply();
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(mainIntent);

                finish();
            } else {
                Snackbar.make(linearLayout, "Email or Password incorrect.", Snackbar.LENGTH_SHORT).show();
                mEmailEditText.requestFocus();
                showProgressCircle(false);
            }
        }
    }

    class Authentication extends AsyncTask<Void, Void, Boolean> {
        private final String mID;
        private OkHttpClient client;

        Authentication(String savedID) {
            mID = savedID;
            client = new OkHttpClient();
        }

        boolean run() throws Exception {
            Request emailCheckRequest = new Request.Builder()
                    .url(LINK_MAIN+SERVICE_AUTHENTICATION+REGISTER_ID+mID)
                    .build();
            Response emailCheckResponse = client.newCall(emailCheckRequest).execute();
            if (!emailCheckResponse.isSuccessful()) throw new IOException("Unexpected code " + emailCheckResponse);
            JSONObject jsonObject = new JSONObject(emailCheckResponse.body().string());
            if (jsonObject.getInt("success") == 1) {
                return true;
            }
            return false;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status = false;
            try {
                status = run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(mainIntent);

                finish();
            } else {
                if(mID != null) {
                    Snackbar.make(linearLayout, "Account details not found.", Snackbar.LENGTH_LONG).show();
                    settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
                    settings.edit().remove("ID").apply();
                }
                showProgressCircle(false);
            }
        }
    }

    class UserRegisterTask extends AsyncTask<Void, Void, Response> {
        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private Gson gson;
        private OkHttpClient client;


        UserRegisterTask(String email, String password, String name) {
            mEmail = email;
            mPassword = password;
            mName = name;
            gson = new Gson();
            client = new OkHttpClient();
        }

        Response run() throws Exception {
            Request emailCheckRequest = new Request.Builder()
                    .url(LINK_MAIN+SERVICE_EMAIL_CHECK+REGISTER_EMAIL+mEmail)
                    .build();
            Response emailCheckResponse = client.newCall(emailCheckRequest).execute();
            if (!emailCheckResponse.isSuccessful()) throw new IOException("Unexpected code " + emailCheckResponse);
            JSONObject jsonObject = new JSONObject(emailCheckResponse.body().string());
            if (jsonObject.getInt("success") == 1) {
                return null;
            }
            Request request = new Request.Builder()
                    .url(LINK_MAIN+SERVICE_REGISTER+REGISTER_NAME+mName+"&"+REGISTER_EMAIL+mEmail+"&"+REGISTER_PASSWORD+mPassword)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response;
        }

        @Override
        protected Response doInBackground(Void... params) {
            Response response = null;
            try {
                response = run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response == null)
                return null;
            else
                return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            mUserRegisterTask = null; // set global var
            showProgressCircle(false);
            if (response == null) {
                mUserRegisterTask = null; // set global var
                showProgressCircle(false);
                mEmailEditText.setError(getString(R.string.error_registered_email));
                mEmailEditText.requestFocus();
            } else {
                try {
                    responseClass = gson.fromJson(response.body().string(), ResponseClass.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (responseClass.getSuccess() == 1) {      // read message, return id.
                    Log.i("info", "success");
                    settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
                    settings.edit().putString("ID", responseClass.getMessage()).apply();
                    settings.edit().putBoolean("isLoggedIn", true).apply();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(mainIntent);

                    finish();
                } else {
                    mUserRegisterTask = null; // set global var
                    showProgressCircle(false);
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mUserRegisterTask = null; // set global var
            showProgressCircle(false);
        }
    }
}