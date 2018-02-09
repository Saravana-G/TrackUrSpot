package trackurspot.com.trackurspot;


import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.io.FileInputStream;


public class StartUp extends Activity implements Animation.AnimationListener {
    ImageView imageView;
    Animation animation2;
    Boolean exis = false;
    FloatingActionButton fab_new, fab_exis;


    TextView new_text, existing_text;

    Animation zoom_anim, fade_anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up);








        FileInputStream fin = null;
        try {
            fin = openFileInput("user_info.txt");

            if ((fin.read()) != -1) {
                exis = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        String mess = prefs.getString("notificationMessage", "");
        editor.putString("notificationMessage", "");
        editor.commit();
        // Application app = new Application();
        // app.onCreate();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/segoepr.ttf");
        imageView = (ImageView) findViewById(R.id.gifImage);
       /* WebSettings settings = gif.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        gif.loadUrl("file:///android_asset/gg.gif");*/
        String fontPath = "fonts/segoepr.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        fade_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        zoom_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
/*
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(fab_new.getLayoutParams());
        //  marginParams.setMargins(left_margin, top_margin, right_margin, bottom_margin);
        Toast.makeText(StartUp.this,height * 0.75 +"", Toast.LENGTH_SHORT).show();
        marginParams.setMargins((int) (width * 0.1), (int) (height * 0.75), 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        fab_new.setLayoutParams(layoutParams);
        */

        new_text = (TextView) findViewById(R.id.new_text);
        existing_text = (TextView) findViewById(R.id.existing_text);
        new_text.setTypeface(tf);
        existing_text.setTypeface(tf);
        fab_exis = (FloatingActionButton) findViewById(R.id.fab_existing);

        ViewGroup.MarginLayoutParams marginParams_1 = new ViewGroup.MarginLayoutParams(fab_exis.getLayoutParams());
        //  marginParams.setMargins(left_margin, top_margin, right_margin, bottom_margin);
        marginParams_1.setMargins(0, (int) (height * 0.75), (int) (width * 0.1), 0);
        RelativeLayout.LayoutParams layoutParams_1 = new RelativeLayout.LayoutParams(marginParams_1);
        layoutParams_1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_END);
        fab_exis.setLayoutParams(layoutParams_1);

        fab_exis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);

        ViewGroup.MarginLayoutParams marginParams_2 = new ViewGroup.MarginLayoutParams(fab_new.getLayoutParams());
        //  marginParams.setMargins(left_margin, top_margin, right_margin, bottom_margin);
        marginParams_2.setMargins((int) (width * 0.1), (int) (height * 0.75), 0, 0);
        RelativeLayout.LayoutParams layoutParams_2 = new RelativeLayout.LayoutParams(marginParams_2);
        fab_new.setLayoutParams(layoutParams_2);

        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });


        //SETTING A ANIMATION FOR LOAD PAGE
        animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.quake);
        imageView.startAnimation(animation2);
        animation2.setAnimationListener(this);


    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animation2) {
            if (!exis) {


                new_text.setVisibility(View.VISIBLE);
                existing_text.setVisibility(View.VISIBLE);
                fab_exis.setVisibility(View.VISIBLE);
                fab_new.setVisibility(View.VISIBLE);

                new_text.startAnimation(fade_anim);
                existing_text.startAnimation(fade_anim);
                fab_exis.startAnimation(zoom_anim);
                fab_new.startAnimation(zoom_anim);
            } else {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }




}