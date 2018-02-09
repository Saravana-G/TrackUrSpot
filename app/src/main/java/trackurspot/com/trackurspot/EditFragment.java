package trackurspot.com.trackurspot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class EditFragment extends Fragment {

    //Key string to get selected BusNumber
    String keySelectedBusNumber = "BusNumber";
    String keySelectedInsNumber = "InsNumber";
    public String file = "user_info.txt";
    private String separator = ",";
    FileOutputStream fOut = null;
    String Ins_selected = "";
    static final int REQUEST_ENABLE = 1;
    private EditText mMyStoppingText;
    private EditText institute_textview;
    String stopping = "", institution = "";
    LinearLayout editlayout;
    private String email = "",old="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_fragment,
                container, false);

        institute_textview = (EditText) view.findViewById(R.id.institute1);
        mMyStoppingText = (EditText) view.findViewById(R.id.myStoppingText1);

        Button mEditButton = (Button) view.findViewById(R.id.button_register1);
        Button changepass = (Button) view.findViewById(R.id.change_pass_act);
        editlayout = (LinearLayout) view.findViewById(R.id.editlayout);
        edit_method();
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();

            }
        });
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ChangePassword.class);
                i.putExtra("email",old);
                startActivityForResult(i,12);
            }
        });


        institute_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // popup.show();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if (!isOnline()) {
                    Snackbar snackbar = Snackbar
                            .make(editlayout, "No internet connection!", Snackbar.LENGTH_LONG);
                    // Changing message text color
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                } else {
                    mMyStoppingText.setText("");
                    Intent i = new Intent(view.getContext(), MyInstituteActivity.class);
                    startActivityForResult(i, 2);
                }

            }
        });

        mMyStoppingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (institute_textview.getText().toString().matches(""))
                    Toast.makeText(view.getContext(), "Plz Select Institute", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(view.getContext(), institute_textview.getText(), Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    if (!isOnline()) {
                        Snackbar snackbar = Snackbar
                                .make(editlayout, "No internet connection!", Snackbar.LENGTH_LONG);
                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    } else {
                        Intent i = new Intent(view.getContext(), MyBusActivity.class);
                        i.putExtra("ins_id", Ins_selected);
                        startActivityForResult(i, REQUEST_ENABLE);
                    }
                }
            }
        });

        return view;


    }

    private void edit_method() {
        FileInputStream fin = null;
        try {
            fin = getActivity().openFileInput("user_info.txt");

            int c;
            String temp = "";
            if ((c = fin.read()) != -1) {
                Log.v("setfile", "notnull");
                do {
                    temp = temp + Character.toString((char) c);
                } while ((c = fin.read()) != -1);
                temp = temp.substring(0, temp.length());
                String ar[] = temp.split(",");
                old=ar[2];
                email=ar[2];
                mMyStoppingText.setText(ar[0]);
                Ins_selected=ar[4];
                institute_textview.setText(ar[5]);
                fin.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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

    private void attemptRegister() {
        // Reset errors.


        // Store values at the time of the login attempt.
        stopping = mMyStoppingText.getText().toString();
        institution = institute_textview.getText().toString();
        // Check for a valid email address.

      if(TextUtils.isEmpty(stopping)){
                mMyStoppingText.setError("Field is Empty");}
      else if(TextUtils.isEmpty(institution)){
          institute_textview.setError("Field is Empty");
      }
        else{
            if (isOnline()) {
                Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                new register_class().execute();
            } else {
                Snackbar snackbar = Snackbar
                        .make(editlayout, "No internet connection!", Snackbar.LENGTH_LONG)
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
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Updating");
            progressDialog.setMessage("Hang in there...");
            progressDialog.show();

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/loginapi/index.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("tag", "edit"));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("stopping", stopping));
                nameValuePairs.add(new BasicNameValuePair("institution", Ins_selected));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                Log.w("asdfg",email+stopping+Ins_selected);
                return httpclient.execute(httppost);


            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            super.onPostExecute(response);
            try {
                fOut = getActivity().openFileOutput(file,0x0000 );

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (response != null) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    JSONObject root = new JSONObject(responseStr);
                    Log.w("response", responseStr);

                    if (root.getString("tag").equals("edit"))
                        if (root.getString("success").equals("1")) {
                            fOut.flush();
                            fOut.write(root.getString("stopping").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("name").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("email").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("rollno").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("institution").getBytes());
                            fOut.write(separator.getBytes());
                            fOut.write(root.getString("institution_name").getBytes());

                         //   startActivity(new Intent(getActivity(),MainActivity.class));
                          //  getActivity().finish();
                        } else {
                          //  Toast.makeText(getActivity(), root.getString("error_msg"), Toast.LENGTH_LONG).show();
                        }
                progressDialog.cancel();
                }
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


}
