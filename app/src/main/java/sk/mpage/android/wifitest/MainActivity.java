package sk.mpage.android.wifitest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView mainText;
    WifiManager mainWifi;
    MyWiFiReceiver receiverWifi;
    List<ScanResult> wifiList;
    String wifiText = "";

    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainText = (TextView) findViewById(R.id.mainText);

        // Initiate wifi service manager
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Check for wifi is disabled
        if (!mainWifi.isWifiEnabled())
        {
            Toast.makeText(getApplicationContext(), R.string.wifiEnabledMsg,
                    Toast.LENGTH_LONG).show();

            if (!mainWifi.setWifiEnabled(true)){
                mainText.setText(R.string.notEnabledWifi);
                return;
            }
        }



        // wifi scanned value broadcast receiver
        receiverWifi = new MyWiFiReceiver();

        // Register broadcast receiver
        // Broadcast receiver will automatically call when number of wifi connections changed
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //final IntentFilter filters = new IntentFilter();
        //filters.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //filters.addAction(WifiManager.WIFI_STATE_DISABLED);
        //registerReceiver(receiverWifi, filters);

        Button button = (Button) findViewById(R.id.refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPerm()){
                    return;
                }
                startScan();
            }
        });

        if (!checkPerm()){
            return;
        }

        startScan();
    }



    public boolean checkPerm(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION);

//            }

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startScan();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void startScan(){

        mainWifi.startScan();
        mainText.setText(R.string.startingScan);

    }


    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class MyWiFiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {

//            if (intent!=null){
//                switch (intent.getAction()) {
//                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
//                        //code to handle WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
//                        break;
//                }
//            }

            wifiText = "";
            wifiList = mainWifi.getScanResults();
            wifiText+="\n        Number Of Wifi connections :"+wifiList.size()+"\n\n";

            for(int i = 0; i < wifiList.size(); i++){

                wifiText+=(wifiList.get(i)).toString();
                wifiText+="\n\n";
            }

            mainText.setText(wifiText);
        }

    }
}


