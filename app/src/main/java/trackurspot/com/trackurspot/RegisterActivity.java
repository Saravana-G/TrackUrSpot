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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Navaneeth on 12/28/2015.
 */
public class RegisterActivity extends AppCompatActivity {

    //Key string to get selected BusNumber
    String keySelectedBusNumber = "BusNumber";
    String keySelectedInsNumber = "InsNumber";

    String Ins_selected = "";
    static final int REQUEST_ENABLE = 1;


    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mRollnoView;
    private AutoCompleteTextView mNameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mMyStoppingText;


    //Coordinator Layout for snackbar
    CoordinatorLayout coordinatorLayout;
    private String email, password;
    private String name, confirm_password, roll_no;
    private String stopping;
    private String institution;
    ArrayList<String> bus_stop;
    ListPopupWindow popup;
    private EditText institute_textview;
    private String institute;
    ArrayAdapter bus_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_register);
        mNameView = (AutoCompleteTextView) findViewById(R.id.name_register);
        mRollnoView = (AutoCompleteTextView) findViewById(R.id.roll_no_register);
        mPasswordView = (EditText) findViewById(R.id.password_register);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirm_password_register);
        mMyStoppingText = (EditText) findViewById(R.id.myStoppingText);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_register);

        Button mEmailSignInButton = (Button) findViewById(R.id.button_register);
        Button mEmailLoginInButton = (Button) findViewById(R.id.button_register_login);

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();

            }
        });
        mEmailLoginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        institute_textview = (EditText) findViewById(R.id.institute);

     /*   bus_stop = new ArrayList<>();
        bus_stop.add("Srec");
        bus_stop.add("Srit");
        institute_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


            }
        });

        popup = new ListPopupWindow(this);
        bus_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bus_stop);

        popup.setAdapter(bus_adapter);
        popup.setAnchorView(institute_textview);
        popup.setBackgroundDrawable(getResources().getDrawable(R.color.bg_login));
        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RegisterActivity.this, "Clicked item " + bus_stop.get(position), Toast.LENGTH_SHORT).show();
                institute = bus_stop.get(position);
                institute_textview.setText(institute);
                popup.dismiss();
            }
        });
*/
        institute_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // popup.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if (!isOnline()) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG);
                    // Changing message text color
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                } else {
                    Intent i = new Intent(getApplicationContext(), MyInstituteActivity.class);
                    startActivityForResult(i, 2);
                }

            }
        });

        mMyStoppingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (institute_textview.getText().toString().matches(""))
                    Toast.makeText(RegisterActivity.this, "Plz Select Institute", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(RegisterActivity.this, institute_textview.getText(), Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    if (!isOnline()) {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG);
                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    } else {
                        Intent i = new Intent(getApplicationContext(), MyBusActivity.class);
                        i.putExtra("ins_id", Ins_selected);
                        startActivityForResult(i, REQUEST_ENABLE);
                    }
                }
            }
        });






    }

    private void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRollnoView.setError(null);
        mConfirmPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        name = mNameView.getText().toString();
        confirm_password = mConfirmPasswordView.getText().toString();
        roll_no = mRollnoView.getText().toString();
        stopping = mMyStoppingText.getText().toString();
        institution = institute_textview.getText().toString();
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");

        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");

        } else if (!isEmailValid(email)) {
            mEmailView.setError("invalid_email");

        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("This password is too short");
        } else {
            if (isOnline()) {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                new register_class().execute();
            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptRegister();
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

    private class register_class extends AsyncTask<String, Integer, HttpResponse> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Hang in there...");
            progressDialog.show();

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/loginapi/index.php");
            try {
                // Add your data
           //     Log.w("wow", roll_no + name + email + password + stopping + Ins_selected);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                nameValuePairs.add(new BasicNameValuePair("tag", "register"));
                nameValuePairs.add(new BasicNameValuePair("rollno", roll_no));
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("stopping", stopping));
                nameValuePairs.add(new BasicNameValuePair("institution", Ins_selected));
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
                    //  Log.w("response", responseStr);
                    JSONObject root = new JSONObject(responseStr);

                   Log.w("response", responseStr);
                    if (root.getString("tag").equals("register")) {
                        progressDialog.cancel();
                        if (root.getString("success").equals("1")) {

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            /**
                             * Close Register Screen
                             **/

                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, root.getString("error_msg"), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            if (requestCode == REQUEST_ENABLE) {

                String message = data.getStringExtra(keySelectedBusNumber);
                mMyStoppingText.setText(message);
            } else if (requestCode == 2) {
                String message = data.getStringExtra("InsNumber1");
                Ins_selected = data.getStringExtra(keySelectedInsNumber);
                institute_textview.setText(message);
                mMyStoppingText.setClickable(true);
            }
        } catch (Exception e) {

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 3;
    }
}
