package trackurspot.com.trackurspot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.Fragment;
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
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FeedbackFragment extends Fragment{

    //editText for feedback
    EditText feedbackEditText;
    Button feed_button;
    MainActivity Main=new MainActivity();
    String s="",email="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback,
                container, false);
        feedbackEditText = (EditText) view.findViewById(R.id.feedback_text);
        feedbackEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        feed_button = (Button) view.findViewById(R.id.feed_send);

        feed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = feedbackEditText.getText().toString();
                if (s.equals(""))
                    feedbackEditText.setError("Field is Empty");
                else {
                    new feedback_class().execute();
                }
            }
        });
        edit_method();
        return view;
    }

    private class feedback_class extends AsyncTask<String, Integer, HttpResponse> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Sending Feedback");
            progressDialog.setMessage("Hang in there...");
            progressDialog.show();}

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/loginapi/index.php");
            try {
                // Add your data
                //  Log.w("wow",roll_no+name+email+password+stopping+Ins_selected);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("tag", "feedback"));
                nameValuePairs.add(new BasicNameValuePair("email",email ));
                nameValuePairs.add(new BasicNameValuePair("content", s));
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
                    if (root.getString("tag").equals("feedback")) {


                        if (root.getString("success").equals("1")) {

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            feedbackEditText.setText("");
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            //    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new MapsActivity()).commit();

                            //   Log.w("response", responseStr);
                        } else {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.cancel();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }




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
                email=ar[2];


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
