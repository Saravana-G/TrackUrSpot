package trackurspot.com.trackurspot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saravana on 3/15/2016.
 */
public class MyInstituteActivity extends Activity{
    //Key string to get selected BusNumber
    String keySelectedInsNumber = "InsNumber";
    String keySelectedInsNumber1 = "InsNumber1";
    int ins_id[]= new int[100];
    // List view
    private ListView lv;

    // Listview Adapter
    ArrayAdapter<String> adapter;

    // Search EditText
    EditText inputSearch;

    //to hold stopings
    private ArrayList<String> bus_stop = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_institute);
        lv = (ListView) findViewById(R.id.List1);
        inputSearch = (EditText) findViewById(R.id.inputSearch1);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bus_stop);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(keySelectedInsNumber1, bus_stop.get(position));
                intent.putExtra(keySelectedInsNumber, ins_id[position]+"");
                setResult(2, intent);
                finish();//finishing activity
            }
        });

        //dont finish the acivity when user touches outside the activity
        this.setFinishOnTouchOutside(false);

        //    adapter.notifyDataSetChanged();



        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MyInstituteActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        new bus_stopping().execute();
    }
/*
    // on back button pressed instead of going back to the called activity, give the toast
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Select Your Institution", Toast.LENGTH_SHORT).show();
    }
*/
    private class bus_stopping extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog nDialog;
        HttpResponse response;

        protected void onPreExecute() {
            nDialog = new ProgressDialog(MyInstituteActivity.this);
            super.onPreExecute();
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Fetching Institution");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            //     HttpPost httppost = new HttpPost("http://www.grenmart.com/tracking/mapshow1.php?busstops=1");
            HttpPost httppost = new HttpPost("http://www.trackurspot.com/tracking/getins.php");

            //     JSONObject json = new JSONObject();

            try {
                // JSON data:
                //     JSONArray postjson = new JSONArray();
                //     postjson.put(json);
                // Execute HTTP Post Request
                response = httpclient.execute(httppost);
                // for JSON:
                if (response != null) {
                    /*
                    InputStream is = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String jsonStr = sb.toString();
                    String str[] = jsonStr.split("\"");
                    bus_stop.clear();
                    for (int i = 3, j = 0; i < str.length; i = i + 4, j++) {
                        bus_stop.add(str[i]);
                    }
*/

                    //   String responseStr = EntityUtils.toString(response.getEntity());
                    //   JSONObject root = new JSONObject(responseStr);
                    // some response object
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null;) {
                        builder.append(line).append("\n");
                    }
                    JSONTokener tokener = new JSONTokener(builder.toString());
                    JSONArray jsonArray = new JSONArray(tokener);
                    // JSONArray jsonArray=root.getJSONArray("institutions");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ins_id[i]=jsonObject.getInt("ins_id");
                        bus_stop.add(jsonObject.getString("ins_name"));
                    }

                    nDialog.dismiss();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (response != null)
                adapter.notifyDataSetChanged();
            else {
                nDialog.setMessage("Error");
            }
        }
    }
}
