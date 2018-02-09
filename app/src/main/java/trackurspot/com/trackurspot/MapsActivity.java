package trackurspot.com.trackurspot;

/**
 * Created by Navaneeth on 1/21/2016.
 */


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends Fragment {

    private GoogleMap mMap;
    private SupportMapFragment fragment;
    Double longitude_single = 76.9616311d;
    Double latitude_single = 11.0195312d;
    Double longitude_multiple[] = new Double[100];
    Double latitude_multiple[] = new Double[100];
    String date[] = new String[100];

    Double stopping_longitude_multiple[] = new Double[100];
    Double stopping_latitude_multiple[] = new Double[100];
    String name[] = new String[100];
    String busid = "0";
    String busid_multiple[] = new String[100];
    int status = 0;
    int length = 0;
    HttpPost httppost;
    Runnable  synchTimer;
    final Handler synchHandler = new Handler();
    String ar[];
    public static boolean mMapIsTouched = false;

    Button cur_loc_click;
    TextView last_updated;
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_maps, container, false);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_frag);
        cur_loc_click = (Button) view.findViewById(R.id.current_location_click);
        last_updated = (TextView) view.findViewById(R.id.last_update_text);
        cur_loc_click.setVisibility(View.INVISIBLE);

        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.action_a);
        final com.getbase.floatingactionbutton.FloatingActionButton actionC = (com.getbase.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.action_c);
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    menuMultipleActions.collapse();
                    last_updated.setVisibility(View.VISIBLE);
                    status = 0;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.collapse();
                last_updated.setVisibility(View.INVISIBLE);
                status = 1;
                mMap.clear();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude_single, longitude_single))
                        .zoom(11)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                cur_loc_click.setVisibility(View.INVISIBLE);
            }
        });

        //   Log.w("Map", "map");
        cur_loc_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapIsTouched = false;

                cur_loc_click.setVisibility(View.INVISIBLE);
            }
        });
        getstopping();
      //  startService();
        synchfunction();

        return view;
    }

    private void getstopping() {
        FileInputStream fin = null;
        try {
            fin = getActivity().openFileInput("user_info.txt");
            int c;
            String temp = "";
            if ((c = fin.read()) != -1) {

                do {
                    temp = temp + Character.toString((char) c);
                } while ((c = fin.read()) != -1);
                temp = temp.substring(0, temp.length());
                ar = temp.split(",");

            }
            Log.w("wow5","stopping");
            new stopping_class().execute();

            //       Toast.makeText(getActivity(),"wow"+ar[0]+ar[4],Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            mMap = fragment.getMap();

            //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
            setUpMap();

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("inside","destroy");

        synchHandler.removeCallbacksAndMessages(null);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

            }
        }

    }


    private void setUpMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude_single, longitude_single))      // Sets the center of the map to Mountain View
                .zoom(15.0f)                 // Sets the tilt of the camera to 30 degrees
                .build();
        //   Log.w("123", mMap.getCameraPosition().zoom + "");// Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (status == 0) {
                    mMapIsTouched = true;
                    cur_loc_click.setVisibility(View.VISIBLE);
                }
            }
        });

        //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_single, longitude_single)));
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));


    }


  /*  public void startService() {

       Runnable synchTimer1 = new Runnable()  {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                if (status == 0) {
                    httppost = new HttpPost("http://www.grenmart.com/tracking/mapshow1.php");
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("stopname", ar[0]));
                    try {
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    httppost = new HttpPost("http://www.grenmart.com/tracking/mapshow1.php");
                    try {
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("allbus", "1"));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        // Execute HTTP Post Request
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                JSONObject json = new JSONObject();

                try {
                    // JSON data:
                    JSONArray postjson = new JSONArray();
                    postjson.put(json);
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    // for JSON:
                    if (response != null) {
                        //  String responseStr = EntityUtils.toString(response.getEntity());
                        //  Log.w("response1", responseStr);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        for (String line = null; (line = reader.readLine()) != null; ) {
                            builder.append(line).append("\n");
                        }
                        JSONTokener tokener = new JSONTokener(builder.toString());
                        JSONArray jsonArray = new JSONArray(tokener);
                        if (status == 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!jsonObject.getString("longi").equals("")) {
                                    longitude_single = Double.parseDouble(jsonObject.getString("longi"));
                                    latitude_single = Double.parseDouble(jsonObject.getString("lat"));
                                    busid = jsonObject.getString("busno");

                                    last_updated.setText("Last Updated:\n" + jsonObject.getString("date"));
                                }
                            }


                        } else {
                            Log.w("Enter","1");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (!jsonObject.getString("longi").equals("")) {
                                    longitude_multiple[i] = Double.parseDouble(jsonObject.getString("longi"));
                                    latitude_multiple[i] = Double.parseDouble(jsonObject.getString("lat"));
                                    busid_multiple[i] = jsonObject.getString("busno");
                                    date[i] = jsonObject.getString("date");

                                }
                            }

                        }

                    }

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                synchHandler1.postDelayed(this,5000);
            }
        };
        synchHandler1.postDelayed(synchTimer1,0);
    }

*/
    private class location_class extends AsyncTask<String, Integer, HttpResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpclient = new DefaultHttpClient();
            if (status == 0) {
                httppost = new HttpPost("http://www.grenmart.com/tracking/mapshow1.php");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("stopname", ar[0]));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                httppost = new HttpPost("http://www.grenmart.com/tracking/mapshow1.php");
                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("allbus", "1"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            JSONObject json = new JSONObject();

            try {
                // JSON data:
                JSONArray postjson = new JSONArray();
                postjson.put(json);
                // Execute HTTP Post Request
                 response = httpclient.execute(httppost);
            } catch (Exception e) {

            }
            return response;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            super.onPostExecute(response);
            try {
                if (response != null) {
                    //  String responseStr = EntityUtils.toString(response.getEntity());
                    //  Log.w("response1", responseStr);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null; ) {
                        builder.append(line).append("\n");
                    }
                    Log.w("wow3",builder.toString());
                    JSONTokener tokener = new JSONTokener(builder.toString());
                    JSONArray jsonArray = new JSONArray(tokener);
                    if (status == 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (!jsonObject.getString("longi").equals("")) {
                                longitude_single = Double.parseDouble(jsonObject.getString("longi"));
                                latitude_single = Double.parseDouble(jsonObject.getString("lat"));
                                busid = jsonObject.getString("busno");
                                Double q=Double.parseDouble(jsonObject.getString("date"));
                                String s="";
                                if(q<60)
                                    s="  seconds ago";
                                else if(q<120) {
                                    s = "  minute ago";
                                    q=q/60;
                                } else if(q<3600) {
                                    s = "  minutes ago";
                                    q=q/60;
                                } else if(q<7200) {
                                    s = "  hour ago";
                                    q=q/60;
                                }else if(q<86400){
                                    s="  hours ago";
                                    q=(q/60)/60;
                                }else if(q<172800){
                                    s="  day ago";
                                    q=(q/60)/60/24;
                                }else {
                                    s="  days ago";
                                    q=((q/60)/60)/24;
                                }
                                last_updated.setText("  Last Updated : "+q.intValue()+s );
                            }
                        }


                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (!jsonObject.getString("longi").equals("")) {
                                longitude_multiple[i] = Double.parseDouble(jsonObject.getString("longi"));
                                latitude_multiple[i] = Double.parseDouble(jsonObject.getString("lat"));
                                busid_multiple[i] = jsonObject.getString("busno");
                                date[i] = jsonObject.getString("date");

                            }
                        }

                    }

                }

            } catch (Exception e) {

            }

        }

    }

    private class stopping_class extends AsyncTask<String, Integer, HttpResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.w("wow5","instopping");
        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/tracking/getallbusstop.php");
            try {
                // Add your data
                //  Log.w("wow",roll_no+name+email+password+stopping+Ins_selected);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("stopname", ar[0]));
                nameValuePairs.add(new BasicNameValuePair("insid", ar[4]));
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
                    // String responseStr = EntityUtils.toString(response.getEntity());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null; ) {
                        builder.append(line).append("\n");
                    }
                    JSONTokener tokener = new JSONTokener(builder.toString());
                    JSONArray jsonArray = new JSONArray(tokener);
                    length = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (!jsonObject.getString("name").equals("")) {
                            name[i] = jsonObject.getString("name");
                            stopping_latitude_multiple[i] = Double.parseDouble(jsonObject.getString("lat"));
                            stopping_longitude_multiple[i] = Double.parseDouble(jsonObject.getString("longi"));
                            // Bitmap bitmap = iconGenerator.makeIcon(jsonObject.getString("name"));
                            //  mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(jsonObject.getString("lat")), Double.parseDouble(jsonObject.getString("longi")))).title("Marker").icon(BitmapDescriptorFactory.fromBitmap(bitmap))).setVisible(true);

                            Log.w("wow1", Double.parseDouble(jsonObject.getString("longi")) + "");
                            //latitude_multiple[i] = Double.parseDouble(jsonObject.getString("lat"));
                        }
                    }

                    //   Log.w("response", responseStr);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    public void synchfunction() {

        synchTimer = new Runnable() {
            @Override
            public void run() {
             //   Log.w("running","running");
               new location_class().execute();
                mMap.clear();
                if (status == 0) {
                    try {
                        IconGenerator iconGenerator = new IconGenerator(getContext());
                        for (int i = 0; i < length; i++) {
                      //      Bitmap bitmap = iconGenerator.makeIcon(name[i]);
                            mMap.addMarker(new MarkerOptions().position(new LatLng(stopping_latitude_multiple[i], stopping_longitude_multiple[i])).title(name[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedotedited1)).anchor((float) 0.5, (float) 0.5).rotation((float) 90.0));
                        }

                        if(Integer.parseInt(busid)!=0) {
                            Bitmap bitmap = iconGenerator.makeIcon(busid);
                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_single, longitude_single)).title("Marker").icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        }
                    } catch (Exception e) {

                    }
                    CameraPosition cameraPosition;
                    // mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_single, longitude_single)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.number_1)));

                    if (mMap.getCameraPosition().zoom < 11) {
                        cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(11.0195312d, 76.9616311d))
                                .zoom(11)
                                .build();
                        // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } else if (!mMapIsTouched) {
                        cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(latitude_single, longitude_single))
                                .zoom(mMap.getCameraPosition().zoom)
                                .build();
                        // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        // mMapIsTouched = true;
                    }


                    // Creates a CameraPosition from the builder
                    //    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    mMap.clear();
                    IconGenerator iconGenerator = new IconGenerator(getContext());
                    for (int i = 0; i < latitude_multiple.length; i++) {
                        if (latitude_multiple[i] != null) {
                            Bitmap bitmap = iconGenerator.makeIcon(busid_multiple[i]);
                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_multiple[i], longitude_multiple[i])).title(date[i]).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            //  mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_multiple[i], longitude_multiple[i])).title("Marker"));

                        }
                        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudecord, longitudecord), 12.0f));
                        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_single, longitude_single),12.0f));
                    }
                }
                synchHandler.postDelayed(this, 5000);
            }
        };
        synchHandler.postDelayed(synchTimer, 0);
    }

}