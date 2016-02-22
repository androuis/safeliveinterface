package evolveconference.safelive.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.R;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.PrefUtil;

public class LoginActivity extends Activity {

    /**
     * View binding
     */
    @Bind(R.id.email) AutoCompleteTextView mEmailView;
    @Bind(R.id.password) EditText mPasswordView;
    @Bind(R.id.login_progress) View mProgressView;
    @Bind(R.id.login_form) View mLoginFormView;
    @Bind(R.id.login_button) Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(PrefUtil.getString(getApplicationContext(), AppConstants.SESSION_TOKEN))) {
            startMainActivity();
        }

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private UserLoginTask mAuthTask = null;

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends BaseAsyncRequest {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void doSetup() throws ApiException, JSONException {
            callerName = "loginActivity";
            serviceName = "user";
            endPoint = "session";

            verb = "POST";

            // post email and password to get back session token
            // need session token to make every call other than login and challenge
            requestBody = new JSONObject();
            requestBody.put("email", mEmail);
            requestBody.put("password", mPassword);

            // include API key
            applicationApiKey = AppConstants.API_KEY;
        }

        @Override
        protected void processResponse(String response) throws ApiException, JSONException {
            // store the session_token to be used later on
            JSONObject jsonObject = new JSONObject(response);
            String session_token = jsonObject.getString("session_token");
            if(session_token.length() == 0){
                throw new ApiException(0, "did not get a valid session token in the response");
            }
            PrefUtil.putString(getApplicationContext(), AppConstants.SESSION_TOKEN, session_token);
        }

        @Override
        protected void onCompletion(boolean success) {
            mAuthTask = null;

            showProgress(false);
            if(success){
                startMainActivity();
            }
            else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void startMainActivity() {
        Intent i = new Intent();
        i.setClass(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
