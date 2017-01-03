package com.tesca.dabbaapp;

import android.app.FragmentManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.tesca.dabbaapp.Estructuras.Orden;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.InfoWindowAdapter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Serializable {

    private GoogleMap mMap;
    private String TAG = "Maps_Activity";
    private LatLng destination = new LatLng(19.525170, -99.226120);
    private TextView textView;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private Location mLastLocation;
    private LayoutInflater inflater;
    private static final int ALARM_REQUEST_CODE = 1;
    private static final String Excecute_Alarm = "com.tesca.dabbaapp.action.RUN_INTENT_SERVICE";


    String city, line, state, zip, country, address, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        destination = latlng;

        Date date = null;

        Double lat = Double.valueOf(latitude);
        Double lon = Double.valueOf(longitude);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(lat,lon,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        city = addresses.get(0).getLocality();
        line = addresses.get(0).getAddressLine(0);
        state = addresses.get(0).getAdminArea();
        zip = addresses.get(0).getPostalCode();
        country = addresses.get(0).getCountryName();
        address = line + ", " + state + ", " + zip + ", " + country + " ";
        info = "Cliente:\n\t\t"+customer+"\nDirección:\n\t\t"+ address +"\n";

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

        Long hora = delivery_long-current_long;

        countDown(delivery_long, current_long);

        //user = (TextView) findViewById(R.id.user_name);
        //user.setText("Cliente:\n\t\t"+customer+"\nDirección:\n\t\t"+ address +"\n"); //Address

        // Filtro de acciones
        IntentFilter filter =  new IntentFilter(Excecute_Alarm);
        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, filter);
        // Ejecutar el ServiceIntent
        Intent servicio = new Intent(this, AlarmService.class);
        servicio.putExtra("hora",hora);
        servicio.setAction(Excecute_Alarm);
        startService(servicio);


    }

    // Broadcast receiver que recibe las emisiones desde los servicios
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Excecute_Alarm:
                    Toast.makeText(getApplicationContext(), "Servicio destruido...", Toast.LENGTH_SHORT).show();
                    break;

                /*case Constants.ACTION_RUN_ISERVICE:
                    progressText.setText(intent.getIntExtra(Constants.EXTRA_PROGRESS, -1) + "");
                    break;*/

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
            Toast.makeText(this, "Ubicación encontrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
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

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(destination);
            builder.include(changed_position);

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20);

            mMap.animateCamera(cu);
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
        mMap.setInfoWindowAdapter(this);

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

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return prepareInfoView(marker);
        //return null;
    }

    private View prepareInfoView(Marker marker){
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);
        //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_map);
        infoImageView.setImageDrawable(drawable);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(MapsActivity.this);
        subInfoLat.setText(info);
        subInfoView.addView(subInfoLat);
        infoView.addView(subInfoView);

        return infoView;
    }

    private void countDown(long b, long a){

        long horaentrega = b-a;

        new CountDownTimer(horaentrega, 1000) {

            public void onTick(long millisUntilFinished) {

                String a = String.format("%02d:%02d:%02d",
                        MILLISECONDS.toHours(millisUntilFinished),
                        MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(millisUntilFinished)));
                String b = String.format("%02d%02d%02d",
                        MILLISECONDS.toHours(millisUntilFinished),
                        MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(millisUntilFinished)));
                Integer c = 002000;
                Integer d = 001000;

                if(Integer.valueOf(b) <= c){
                    textView.setBackgroundColor(Color.YELLOW);
                }
                if(Integer.valueOf(b) <= d){
                    textView.setBackgroundColor(Color.RED);
                }
                if (a.equals("00:10:00")){
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
        android.app.Fragment frag = getFragmentManager().findFragmentByTag("dialog");
        if (frag != null){
            getFragmentManager().beginTransaction().remove(frag).commit();
        }
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

    private void notif(){

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
