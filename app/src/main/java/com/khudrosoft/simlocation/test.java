  TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

    int cid = cellLocation.getCid();
    int lac = cellLocation.getLac();
    textGsmCellLocation.setText(cellLocation.toString());
    textCID.setText("gsm cell id: " + String.valueOf(cid));
    textLAC.setText("gsm location area code: " + String.valueOf(lac));

    if(RqsLocation(cid, lac)){
     textGeo.setText(
          String.valueOf((float)myLatitude/1000000)
          + " : "
          + String.valueOf((float)myLongitude/1000000));
    latitude=String.valueOf((float)myLatitude/1000000);
     longitude=String.valueOf((float)myLongitude/1000000);
     lat_double=Double.parseDouble(latitude);
     lang_double=Double.parseDouble(longitude);
     geocoder = new Geocoder(AndroidTelephonyManager.this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(lat_double,  lang_double, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuilder str = new StringBuilder();
        //if (geocoder.isPresent()) {
            // Toast.makeText(getApplicationContext(),
            // "geocoder present",
            // Toast.LENGTH_SHORT).show();
            Address returnAddress = addresses.get(0);

            String area = returnAddress.getFeatureName();
            String thfare = returnAddress.getThoroughfare();
            String localityString = returnAddress.getLocality();
            // String region_code = returnAddress.getCountryCode();
            String zipcode = returnAddress.getPostalCode();
            String state = returnAddress.getAdminArea();
            String sublocal = returnAddress.getSubLocality();
            String city = returnAddress.getCountryName();
     Toast.makeText(getApplicationContext(),  latitude, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), longitude, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), thfare, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), area, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), localityString, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), zipcode, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), sublocal, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
     Toast.makeText(getApplicationContext(), city, Toast.LENGTH_LONG).show();
    }else{
     textGeo.setText("Can't find Location!");
    };
   // }
}


private Boolean RqsLocation(int cid, int lac){

       Boolean result = false;

       String urlmmap = "http://www.google.com/glm/mmap";

          try {
           URL url = new URL(urlmmap);
              URLConnection conn = url.openConnection();
              HttpURLConnection httpConn = (HttpURLConnection) conn;      
              httpConn.setRequestMethod("POST");
              httpConn.setDoOutput(true);
              httpConn.setDoInput(true);
      httpConn.connect();

      OutputStream outputStream = httpConn.getOutputStream();
            WriteData(outputStream, cid, lac);

            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {
             myLatitude = dataInputStream.readInt();
             myLongitude = dataInputStream.readInt();

                result = true;

            }
     } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     }

     return result;

      }

private void WriteData(OutputStream out, int cid, int lac)
          throws IOException          
{    
    DataOutputStream dataOutputStream = new DataOutputStream(out);
    dataOutputStream.writeShort(21);
    dataOutputStream.writeLong(0);
    dataOutputStream.writeUTF("en");
    dataOutputStream.writeUTF("Android");
    dataOutputStream.writeUTF("1.0");
    dataOutputStream.writeUTF("Web");
    dataOutputStream.writeByte(27);
    dataOutputStream.writeInt(0);
    dataOutputStream.writeInt(0);
    dataOutputStream.writeInt(3);
    dataOutputStream.writeUTF("");

    dataOutputStream.writeInt(cid);
    dataOutputStream.writeInt(lac);   

    dataOutputStream.writeInt(0);
    dataOutputStream.writeInt(0);
    dataOutputStream.writeInt(0);
    dataOutputStream.writeInt(0);
    dataOutputStream.flush();       
}
