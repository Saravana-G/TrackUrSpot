package trackurspot.com.trackurspot;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.maps.android.ui.IconGenerator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PasswordReset extends AppCompatActivity {

    String email = "";
    EditText email_reset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwordreset);
        email_reset = (EditText) findViewById(R.id.forpas);
    }

    public void reset_pass(View v) {
        email = email_reset.getText().toString();
        new change_class().execute();
    }

    public void back_to_login(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private class change_class extends AsyncTask<String, Integer, HttpResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/loginapi/index.php");
            try {
                // Add your data
                //  Log.w("wow",roll_no+name+email+password+stopping+Ins_selected);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("tag", "forpass"));
                nameValuePairs.add(new BasicNameValuePair("forgotpassword", email));
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
                    if (root.getString("tag").equals("forpass"))
                        if (root.getString("success").equals("1")) {
                            startActivity(new Intent(getApplication(), LoginActivity.class));
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
}





