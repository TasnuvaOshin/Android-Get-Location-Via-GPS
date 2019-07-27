package com.khudrosoft.simlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class MainActivity extends AppCompatActivity {
    int myLatitude, myLongitude;
    OpenCellID openCellID;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textGsmCellLocation = (TextView) findViewById(R.id.gsmcelllocation);
        TextView textMCC = (TextView) findViewById(R.id.mcc);
        TextView textMNC = (TextView) findViewById(R.id.mnc);
        TextView textCID = (TextView) findViewById(R.id.cid);
        TextView textLAC = (TextView) findViewById(R.id.lac);
        TextView textGeo = (TextView) findViewById(R.id.geo);
        TextView textRemark = (TextView) findViewById(R.id.remark);

//retrieve a reference to an instance of TelephonyManager


        //location Permission Needed for Our Work

        if (checkLocationPermission()) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

            String networkOperator = telephonyManager.getNetworkOperator();
            String mcc = networkOperator.substring(0, 3);
            String mnc = networkOperator.substring(3);
            textMCC.setText("mcc: " + mcc);
            textMNC.setText("mnc: " + mnc);

            int cid = cellLocation.getCid();
            int lac = cellLocation.getLac();
            textGsmCellLocation.setText(cellLocation.toString());
            textCID.setText("gsm cell id: " + String.valueOf(cid));
            textLAC.setText("gsm location area code: " + String.valueOf(lac));

            openCellID = new OpenCellID();

            openCellID.setMcc(mcc);
            openCellID.setMnc(mnc);
            openCellID.setCallID(cid);
            openCellID.setCallLac(lac);
            try {
                openCellID.GetOpenCellID();

                if (!openCellID.isError()) {
                    textGeo.setText(openCellID.getLocation());
                    textRemark.setText("nn"
                            + "URL sent: n" + openCellID.getstrURLSent() + "nn"
                            + "response: n" + openCellID.GetOpenCellID_fullresult);
                } else {
                    textGeo.setText("Error");
                }
            } catch (Exception e) {
// TODO Auto-generated catch block
                e.printStackTrace();
                textGeo.setText("Exception: " + e.toString());
            }


        }

    }


    public class OpenCellID {
        String mcc; //Mobile Country Code
        String mnc; //mobile network code
        String cellid; //Cell ID
        String lac; //Location Area Code

        Boolean error;
        String strURLSent;
        String GetOpenCellID_fullresult;

        String latitude;
        String longitude;

        public Boolean isError() {
            return error;
        }

        public void setMcc(String value) {
            mcc = value;
        }

        public void setMnc(String value) {
            mnc = value;
        }

        public void setCallID(int value) {
            cellid = String.valueOf(value);
        }

        public void setCallLac(int value) {
            lac = String.valueOf(value);
        }

        public String getLocation() {
            return (latitude + " : " + longitude);
        }

        public void groupURLSent() {
//            strURLSent =
//                    "http://www.opencellid.org/cell/get?mcc=" + mcc
//                            +"&mnc=" + mnc
//                            +"&cellid=" + cellid
//                            +"&lac=" + lac
//                            +"&fmt=txt";

            Log.d("lac", lac); //this is the main value via this we can get the location .
            Log.d("mcc", mcc);
            Log.d("cellid", cellid);
            Log.d("mnc", mnc);
            //https://cellphonetrackers.org/gsm/gsm-tracker.php go there and past the value and get your location


        }

        public String getstrURLSent() {
            return strURLSent;
        }

        public String getGetOpenCellID_fullresult() {
            return GetOpenCellID_fullresult;
        }

        public void GetOpenCellID() throws Exception {
            groupURLSent();
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(strURLSent);
            HttpResponse response = client.execute(request);
            GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
            spliteResult();
        }

        private void spliteResult() {
            if (GetOpenCellID_fullresult.equalsIgnoreCase("err")) {
                error = true;
            } else {
                error = false;
                String[] tResult = GetOpenCellID_fullresult.split(",");
                latitude = tResult[0];
                longitude = tResult[1];
            }
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("Please Share/on Your Location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("Please Share/on Your Location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


}
