package trackurspot.com.trackurspot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saravana, TRACK UR SPOT on 2/29/2016.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView view;
    TextView name_text;
    TextView email_text;
    private int flag = 0;
    Snackbar snackbar;
    int i = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting a reference to the view
        setContentView(R.layout.activity_main);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //  Toast.makeText(MainActivity.this,"GCM Registered",Toast.LENGTH_LONG).show();
                } else {
                    // Toast.makeText(MainActivity.this,"GCM NOT Registered",Toast.LENGTH_LONG).show();
                }
            }

        };

        registerReceiver();
        synchfunction();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        snackbar = Snackbar
                .make(drawerLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE);

        view = (NavigationView) findViewById(R.id.navigation);
        //  MapsActivity one_fragmen = new MapsActivity();
        //  getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, one_fragmen).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appp_bar);
        //setting the custom actionbar
        setSupportActionBar(toolbar);
        //setting the home button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (i != flag) {
                    i = flag;
                    drawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {

                            Rect r = new Rect();
                            view.getWindowVisibleDisplayFrame(r);
                            int screenHeight = view.getRootView().getHeight();

// r.bottom is the position above soft keypad or device button.
// if keypad is shown, the r.bottom is smaller than that before.
                            int keypadHeight = screenHeight - r.bottom;


                            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
// keyboard is opened
                                if (i != 3) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                }
                            } else {

                            }
                        }
                    });
                    switch (flag) {
                        case 1:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).replace(R.id.mainContent, new MapsActivity()).commit();
                            break;
                        case 2:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).replace(R.id.mainContent, new EditFragment()).commit();
                            break;
                        case 3:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out).replace(R.id.mainContent, new FeedbackFragment()).commit();
                            break;
                        case 4:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out).replace(R.id.mainContent, new AboutFragment()).commit();
                            break;

                    }
                }


            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);


            }


        };


        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        //listener for the list activity
        // view.setNavigationItemSelectedListener(this);


        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                //selecting the corresponding fragment & setting the actionbar title

                if (menuItem.getItemId() == R.id.map_view) {
                    flag = 1;
                    //  getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new MapsActivity()).commit();

                }

                if (menuItem.getItemId() == R.id.edit_profile) {
                    flag = 2;
                    //  getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new EditFragment()).commit();

                }
                if (menuItem.getItemId() == R.id.feedback) {
                    flag = 3;
                    //  getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new FeedbackFragment()).commit();


                }
                if (menuItem.getItemId() == R.id.about_us) {
                    flag = 4;
                    //  getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new AboutFragment()).commit();

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new MapsActivity()).commit();

        name_text = (TextView) findViewById(R.id.name_header);
        email_text = (TextView) findViewById(R.id.email_header);
        drawer_head();


    }


    private void drawer_head() {
        FileInputStream fin = null;
        try {
            fin = openFileInput("user_info.txt");

            int c;
            String temp = "";
            if ((c = fin.read()) != -1) {
                //   Log.v("setfile", "notnull");
                do {
                    temp = temp + Character.toString((char) c);
                } while ((c = fin.read()) != -1);
                temp = temp.substring(0, temp.length());
                String ar[] = temp.split(",");

                name_text.setText(ar[1].toString());
                email_text.setText(ar[2].toString());
                fin.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //called when the back key is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //to call the alert dialog box
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            if (i != 1 && i != 0) {
                view.getMenu().getItem(0).setChecked(true);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).replace(R.id.mainContent, new MapsActivity()).commit();
                i = 1;
            } else
                closeApp();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //method for creating the alert dialog box
    public void closeApp() {
        new AlertDialog.Builder(this).setTitle("QUIT").setMessage("Are you sure you want to close this app?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("No", null).show();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //     getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new MapsActivity()).commit();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    final Handler synchHandler = new Handler();

    boolean online = false;

    public void synchfunction() {

        Runnable synchTimer = new Runnable() {
            @Override
            public void run() {

                if (!online) {
                    if (!isOnline()) {

                        snackbar.show();
                        online = true;

                    }
                } else {

                    if (isOnline()) {

                        startActivity(new Intent(MainActivity.this, MainActivity.class));

                        snackbar.dismiss();
                        online = false;

                    }
                }
                synchHandler.postDelayed(this, 5000);
            }
        };
        synchHandler.postDelayed(synchTimer, 0);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            if (requestCode == 12) {
                String message = data.getStringExtra("success");
                if (message.equals("1")) finish();
            }
        } catch (Exception e) {

        }
    }


    private class feedback_class extends AsyncTask<String, Integer, HttpResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/loginapi/index.php");
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;

                // Add your data
                //  Log.w("wow",roll_no+name+email+password+stopping+Ins_selected);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("tag", "version"));
                nameValuePairs.add(new BasicNameValuePair("version", version));

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
                    if (root.getString("tag").equals("version")) {


                        if (root.getString("success").equals("1")) {


                        } else {
                            Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.dialog_box_version);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


}
