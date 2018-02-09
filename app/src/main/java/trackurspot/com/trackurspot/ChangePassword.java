package trackurspot.com.trackurspot;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ChangePassword extends Activity {

    EditText  new_password, change_password;
    CoordinatorLayout layout;
    String email = "", old_p = "", new_p = "", confirm_p = "";
    Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        new_password = (EditText) findViewById(R.id.password_register_c);
        change_password = (EditText) findViewById(R.id.confirm_password_register_c);
        change = (Button) findViewById(R.id.button_change);
        layout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_register_c);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               attemptLogin();
            }
        });
        email = getIntent().getStringExtra("email");
        Log.w("email", email);

    }

    private void attemptLogin() {
        // Reset errors.
        new_password.setError(null);
        change_password.setError(null);

        // Store values at the time of the login attempt.
        new_p = new_password.getText().toString();
        confirm_p = change_password.getText().toString();

        // Check for a valid email address.
       if (TextUtils.isEmpty(new_p)) {
            new_password.setError("This field is required");

        } else if (TextUtils.isEmpty(confirm_p)) {
            change_password.setError("This field is required");

        } else if (!isPasswordValid(new_p)) {
            new_password.setError("This password is too short");
        }else if(!new_p.equals(confirm_p)){
            change_password.setError("Password Mismatch");
        }
        else {
            if (isOnline()) {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                new changeclass().execute();
            } else {
                Snackbar snackbar = Snackbar
                        .make(layout, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });

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

    private class changeclass extends AsyncTask<String, Integer, HttpResponse> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChangePassword.this);
            progressDialog.setTitle("Updating Password");
            progressDialog.setMessage("Hang in there...");
            progressDialog.show();

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.trackurspot.com/loginapi/index.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("tag", "chgpass"));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("newpas", new_p));
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
            try {
                if (response != null) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    JSONObject root = new JSONObject(responseStr);
                    Log.w("response", responseStr);
                    progressDialog.cancel();
                        if (root.getString("success").equals("1")) {
                            Intent intent = new Intent();
                            intent.putExtra("success",1);
                            setResult(12, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                        }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 3;
    }

}






















