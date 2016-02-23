package com.chronix.databaseparsing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
    //private SharedPreferences.Editor editor;

    private final String LINK_MAIN = "http://192.168.1.4/";
    private final String SERVICE_REGISTER = "register.php?";
    private final String SERVICE_EMAIL_CHECK = "checkemail.php?";
    private final String SERVICE_LOGIN = "login.php";

    private final String link = "http://192.168.1.4/";
    //private final String idDB = "id=";
    private final String REGISTER_NAME = "name=";
    private final String REGISTER_EMAIL = "email=";
    private final String REGISTER_PASSWORD = "password=";

    private Database database;
    ResponseClass responseClass;

    private UserRegisterTask mUserRegisterTask = null;

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
        boolean network = database.isNetworkAvailable();

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
                }
            }
        });
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
            mUserRegisterTask = new UserRegisterTask(email, password, name);
            mUserRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void showProgressCircle(final boolean show) {

            /*
                add a authentication system.
                the app sends the user id for system prefs to the server and receives an authentication.
                if the server does not have the id in its database then the app resets current account info
                and asks the user for new login
            */

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
        boolean status = settings.getBoolean("isLoggedIn", false);

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

    class UserRegisterTask extends AsyncTask<Void, Void, Response> {
        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final Gson gson;
        private final OkHttpClient client;


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
            JSONObject jsonObject = new JSONObject(emailCheckRequest.toString());
            if (jsonObject.getInt("success") == 1){
                return null;
            }
            Request request = new Request.Builder()
                    .url(LINK_MAIN + SERVICE_REGISTER + REGISTER_NAME + mName + "&" + REGISTER_EMAIL + mEmail + "&" + REGISTER_PASSWORD + mPassword)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Toast.makeText(LoginActivity.this, LINK_MAIN + SERVICE_REGISTER + REGISTER_NAME + mName + "&" + REGISTER_EMAIL + mEmail + "&" + REGISTER_PASSWORD + mPassword, Toast.LENGTH_LONG).show();
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
                responseClass = gson.fromJson(response.toString(), ResponseClass.class);
                if (responseClass.getSuccess() == 1) {      // read message, return id.
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