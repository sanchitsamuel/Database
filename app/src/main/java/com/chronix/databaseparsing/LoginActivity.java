package com.chronix.databaseparsing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private String authenticationLinkDB = "http://192.168.1.4/connection.php?id=";
    private String registerUserLinkDB = "http://192.168.1.4/register.php?";
    private String idDB = "id=";
    private String nameDB = "name=";
    private String emailDB = "email=";
    private String passwordDB = "password=";

    SQLiteDatabase userDatabase;

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

        userDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        userDatabase.execSQL("CREATE TABLE IF NOT EXISTS local_user_table(name VARCHAR, email VARCHAR, password VARCHAR, id VARCHAR)");

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!register) {
                    try {
                        attemptRegisterUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(v.getRootView(), "Unable to register your account, internal app error. " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                } else {

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
        boolean status = settings.getBoolean("isLoggedIn", false);
        showProgressCircle(status);
    }

    private void attemptRegisterUser() throws Exception {           // to be called when the registration is done
        mNameEditText.setError(null);
        mEmailEditText.setError(null);
        mPasswordText.setError(null);

        boolean cancel = false;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordText.getText().toString();
        String name = mNameEditText.getText().toString();
        byte[] temp;
        String id;

        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            cancel = true;
        } else if (!isValidPassword(password) || TextUtils.isEmpty(password)) {
            mPasswordText.setError(getString(R.string.error_invalid_password));
            cancel = true;
        } else if (TextUtils.isEmpty(name)) {
            mNameEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        } else {
            cancel = false;
        }

        if (cancel) {
            return;
        } else {
            // send user data over to the server a copy in local db.
            temp = encrypt(email);
            id = temp.toString();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("name", name);
//            contentValues.put("email", email);
//            contentValues.put("password", password);
//            contentValues.put("id", id);
//            userDatabase.insert("local_user_table", null, contentValues);

            idDB = idDB.concat(id);
            nameDB = nameDB.concat(name);
            emailDB = emailDB.concat(email);
            passwordDB = passwordDB.concat(password);

            registerUserLinkDB = registerUserLinkDB.concat(idDB);
            registerUserLinkDB = registerUserLinkDB.concat("&");
            registerUserLinkDB = registerUserLinkDB.concat(nameDB);
            registerUserLinkDB = registerUserLinkDB.concat("&");
            registerUserLinkDB = registerUserLinkDB.concat(emailDB);
            registerUserLinkDB = registerUserLinkDB.concat("&");
            registerUserLinkDB = registerUserLinkDB.concat(passwordDB);

//            URL url = new URL(registerUserLinkDB)-;
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet();
//            request.setURI(new URI(registerUserLinkDB));
//            HttpResponse response = client.execute(request);

            settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
            settings.edit().putBoolean("isLoggedIn", true).apply();
            settings.edit().putString("ID", id).apply();
            showProgressCircle(true);
        }
    }

    private boolean isEmailValid(String email) {        // check on server
        return email.contains("@");
    }

    private boolean isValidPassword(String password) {
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
        } else {        // for registered users, increase the slash time.
            mEmailLayout.setVisibility(View.GONE);
            mPasswordLayout.setVisibility(View.GONE);
            mNameLayout.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            linearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            final Animation imageViewAnim = AnimationUtils.loadAnimation(this, R.anim.image_view_anim);
            mImageView.startAnimation(imageViewAnim);
            imageViewAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                    settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
                    String id = settings.getString("ID", null);
                    if (id == null)
                        showProgressCircle(false);
                    else {

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.putExtra("link", registerUserLinkDB);
                        LoginActivity.this.startActivity(mainIntent);
                        finish();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    void authenicateUser(String id) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                return null;
            }
        }.execute();
    }

    public static byte[] encrypt(String x) throws Exception {
        java.security.MessageDigest d = null;
        d = java.security.MessageDigest.getInstance("SHA-1");
        d.reset();
        d.update(x.getBytes());
        return d.digest();
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
}