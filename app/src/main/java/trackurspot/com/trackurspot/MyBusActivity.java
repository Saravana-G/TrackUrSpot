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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MyBusActivity extends Activity {

    //Key string to get selected BusNumber
    String keySelectedBusNumber = "BusNumber";
    static final int REQUEST_ENABLE = 1;
    String ins_id="";
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
        setContentView(R.layout.activity_select_stoppping);



        lv = (ListView) findViewById(R.id.List);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bus_stop);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(keySelectedBusNumber, adapter.getItem(position));
                setResult(REQUEST_ENABLE, intent);
                finish();//finishing activity
            }
        });

        //dont finish the acivity when user touches outside the activity
        this.setFinishOnTouchOutside(false);

       //     adapter.notifyDataSetChanged();
       ins_id=getIntent().getExtras().getString("ins_id");


        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MyBusActivity.this.adapter.getFilter().filter(cs);
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

    // on back button pressed instead of going back to the called activity, give the toast
/*    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Select Your Bus Number", Toast.LENGTH_SHORT).show();
    }
*/
    private class bus_stopping extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog nDialog;
        HttpResponse response;

        protected void onPreExecute() {
            nDialog = new ProgressDialog(MyBusActivity.this);
            super.onPreExecute();
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Fetching Stoppings");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
       //     HttpPost httppost = new HttpPost("http://www.grenmart.com/tracking/mapshow1.php?busstops=1");
            HttpPost httppost = new HttpPost("http://www.trackurspot.com/tracking/mapshow1.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("ins_id",ins_id ));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request



            } catch (Exception e) {

            }
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
                        bus_stop.add(jsonObject.getString("name"));
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