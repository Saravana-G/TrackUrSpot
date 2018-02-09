package trackurspot.com.trackurspot;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;


public class AboutFragment extends Fragment {
    WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment,
                container, false);

        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        TextView textView = (TextView) view.findViewById(R.id.version);
        textView.setText("Version :"+ version);
        mWebView = (WebView) view.findViewById(R.id.webview);

        String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/segoepr.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>";
        String pas = "</body></html>";
        String myHtmlString = pish +   "<P>Track Ur Spot is developed by the students of Computer Science and Engineering ( 2013 - 2017 ),<b> Sri Ramakrishna Engineering College</b>, for realtime tracking of the college buses.</p> <P> We take this opportunity to thank our Principal <b>Dr. N.R.Alamelu</b>, Director <b>Dr. A.Ebenezer Jeyakumar</b>, Head of the Department <b>Prof. K.Manoharan</b>, Project guide <b>Mr. M.Ganesh, Mrs. G.Rathi</b> and all<b> the faculty members of CSE Department</b> without whom the project would not be possible.</P> <P>Further we extend our thanks to all <b>Our Classmates ( CSE-A 2013 - 2017 )</b> , <b>G.Saravana</b> and <b>S.Kishorekumar</b> for their support in developing the Android Application.</P><br> <b><P>Project Members:</p> <p> Navaneeth.R.S &nbsp;&nbsp;&nbsp;&nbsp; Hari prasath.P </p></b> "+ pas;
        mWebView.loadDataWithBaseURL(null,myHtmlString, "text/html", "UTF-8", null);
        return view;

    }
}
