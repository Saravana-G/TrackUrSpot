package trackurspot.com.trackurspot;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    AsyncTask<Void, Void, Void> mRegisterTask;


    static String email, password, gcm_name, gcm_email;
    public String file = "user_info.txt";
    private String separator = ",";

    FileOutputStream fOut = null;

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });
    }


    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This fiels is required");

        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This fiels is required");

        } else if (!isEmailValid(email)) {
            mEmailView.setError("invalid_email");

        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("This password is too short");
        } else {
            if (isOnline()) {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                new login_class().execute();
            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });

                //F:\Androidkeystore\Trackurspotkey.jks keytool -list -keystore
// D9:13:7B:7A:25:C0:54:34:EB:8B:EE:95:9F:84:85:D6:52:72:B7:B7
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
            }
        }


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private class login_class extends AsyncTask<String, Integer, HttpResponse> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Hang in there...");
            progressDialog.show();
        }

        @Override
        protected HttpResponse doInBackground(String... strings) {
            try {
                fOut = openFileOutput(file, 0x0000);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.trackurspot.com/loginapi/index.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("tag", "login"));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                return httpclient.execute(httppost);


            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            super.onPostExecute(response);
            Log.w("wow10","in");
            try {
                if (response != null) {

                    String responseStr = EntityUtils.toString(response.getEntity());
                    JSONObject root = new JSONObject(responseStr);
                    Log.w("login", responseStr);
                    if (root.getString("tag").equals("login"))
                        if (root.getString("success").equals("1")) {
                            gcm_name = root.getString("name");
                            gcm_email = root.getString("email");
                            fOut.write(root.getString("stopping").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(gcm_name.getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(gcm_email.getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("rollno").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("institution").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("institution_name").getBytes());


                       //     Log.w("login", root.getString("institution_name") + "");

                            //   Toast.makeText(getApplicationContext(), responseStr + "", Toast.LENGTH_LONG).show();
/*                           GCMRegistrar.checkDevice(LoginActivity.this);
                            GCMRegistrar.checkManifest(LoginActivity.this);

                            registerReceiver(mHandleMessageReceiver, new IntentFilter(
                                    DISPLAY_MESSAGE_ACTION));

                            // Get GCM registration id
                            final String regId = GCMRegistrar.getRegistrationId(LoginActivity.this);
                            Log.w("gcmtest", "hi");
                            Log.w("gcmtest", regId);
                            // Check if regid already presents
                            if (regId.equals("")) {
                                // Registration is not present, register now with GCM
                                Log.w("gcmtest1", "hello");
                                GCMRegistrar.register(LoginActivity.this, SENDER_ID);
                            } else {
                                // Device is already registered on GCM
                                if (GCMRegistrar.isRegisteredOnServer(LoginActivity.this)) {
                                    // Skips registration.
                                    Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
                                } else {
                                    // Try to register again, but not in the UI thread.
                                    // It's also necessary to cancel the thread onDestroy(),
                                    // hence the use of AsyncTask instead of a raw thread.
                                    final Context context = LoginActivity.this;
                                    mRegisterTask = new AsyncTask<Void, Void, Void>() {

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            // Register on our server
                                            // On server creates a new user
                                            ServerUtilities.register(context, gcm_name, gcm_email, regId);
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void result) {
                                            mRegisterTask = null;
                                        }

                                    };
                                    mRegisterTask.execute(null, null, null);
                                }
                            }
                            Intent upanel = new Intent(getApplicationContext(), MainActivity.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          //  unregisterReceiver(mHandleMessageReceiver);
                         //   GCMRegistrar.unregister(LoginActivity.this);
                          //  startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            /**
                             * Close Login Screen

                            startActivity(upanel);*/
                            progressDialog.cancel();
                            fOut.close();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(LoginActivity.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
                        }
                }
            } catch (Exception e) {
                progressDialog.cancel();
                e.printStackTrace();
            }
            progressDialog.cancel();
        }

    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 3;
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public void new_user_click(View v) {
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
        finish();
    }

    public void change_pass_click(View v) {
        Intent i = new Intent(getApplicationContext(), PasswordReset.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }

        super.onDestroy();
    }
}


