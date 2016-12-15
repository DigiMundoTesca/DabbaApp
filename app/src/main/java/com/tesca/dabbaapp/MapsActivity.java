package com.tesca.dabbaapp;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.tesca.dabbaapp.Estructuras.Orden;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private String TAG = "Maps_Activity";
    private LatLng destination = new LatLng(19.525170, -99.226120);
    private TextView textView, user, status_tv, costo_tv, id_tv;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private Location mLastLocation;
    String address;
    AddressResultReceiver mResultReceiver;
    int fetchType = Constants.USE_ADDRESS_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Mapa");

        this.overridePendingTransition(R.anim.slide_in,
                R.anim.slide_out);
        mAuth = FirebaseAuth.getInstance();
        // Establecer punto de entrada para la API de ubicación
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textView = (TextView) findViewById(R.id.textView4);
        Orden extras = (Orden) getIntent().getSerializableExtra("Orden");

        String id = extras.getId();
        String customer = extras.getCustom_user().getUser().getUsername();
        String latitude = extras.getLatitude();
        String longitude = extras.getLongitude();
        String status = extras.getStatus();
        String created_time = extras.getCreated_date();
        String delivery_time = extras.getDelivery_date();
        String price = "100";

        Log.v("Orden", longitude + "  " + latitude);

        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        destination = latlng;

        Date date = null;

        //19 digitos

        try {
            String delivery_Date_use = delivery_time.substring(0, 19);
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(delivery_Date_use);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar b = toCalendar(date);
        long delivery_long = b.getTimeInMillis();

        Calendar c = Calendar.getInstance();
        long current_long = c.getTimeInMillis();

        countDown(delivery_long, current_long);

        fetchType = Constants.USE_ADDRESS_LOCATION;
        GetAddress(latitude, longitude, customer);

        user = (TextView) findViewById(R.id.user_name);
        user.setText("Cliente:\n\t\t"+customer+"\nDirección:\n\t\t"); //Address

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.logout){
            mAuth.signOut();
            startActivity(new Intent(MapsActivity.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void GetAddress(String latitude, String longitude, String customer) {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        if(fetchType == Constants.USE_ADDRESS_LOCATION) {
            if(latitude.equals(null) || longitude.equals(null)) {
                Toast.makeText(this,
                        "Verifique la dirección",
                        Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,
                    Double.parseDouble(latitude));
            intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,
                    Double.parseDouble(longitude));
        }
        Log.e(TAG, "Starting Service");
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                address=resultData.getString(Constants.RESULT_DATA_KEY);
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*progressBar.setVisibility(View.GONE);
                        infoText.setVisibility(View.VISIBLE);
                        infoText.setText("Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" +*/
                        user.setText("Address: " + resultData.getString(Constants.RESULT_ADDRESS));
                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*progressBar.setVisibility(View.GONE);
                        infoText.setVisibility(View.VISIBLE);*/
                        user.setText(resultData.getString(Constants.RESULT_ADDRESS));
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, "Ubicación encontrada", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            LatLng changed_position = new LatLng(loc.getLatitude(), loc.getLongitude());
            String url = getDirectionsUrl(changed_position, destination);
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(changed_position,mMap.getCameraPosition().zoom)); //Animate the camera to follow position

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            CameraPosition builder = new CameraPosition.Builder()  //--->Previous code for camera
                    .target(changed_position)
                    .zoom(20)
                    .tilt(50)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.BLUE);
                lineOptions.clickable(true);
            }

            // Drawing polyline in the Google Map for the i-th route

            try {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(destination));
                mMap.addPolyline(lineOptions);
            } catch (NullPointerException e) {

            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
    }

    private void countDown(long b, long a){

        long horaentrega = b-a;

        new CountDownTimer(horaentrega, 1000) {

            public void onTick(long millisUntilFinished) {

                String a = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                if(a.equals("00:20:00")){
                    textView.setBackgroundColor(Color.YELLOW);
                }
                if(a.equals("00:10:00")){
                    textView.setBackgroundColor(Color.RED);
                    dialog();
                }
                textView.setText(a);
            }

            public void onFinish() {
                textView.setText("Fin del tiempo");
            }
        }.start();

    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


    private void dialog() //Alert dialog
    {

        final AlertFragment dialog = new AlertFragment();
        dialog.show(getSupportFragmentManager(), "dialog");
        final MediaPlayer mp = MediaPlayer.create(MapsActivity.this, R.raw.alert);
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss(); // Close alert dialog
                t.cancel(); // Stop timer to avoid crash report
            }
        }, 5000); // Starts activity after 5 seconds
    }

    private void notif()  //Local notification
    {

        NotificationCompat.Builder  notif = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.moto2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.moto2))
                .setContentTitle("Nueva entrega")
                .setContentText("Revisa la seccion de entregas");
        notif.setAutoCancel(true);
        notif.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat notifman = NotificationManagerCompat.from (this);
        notifman.notify(1, notif.build());

    }

}
