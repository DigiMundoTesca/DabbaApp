package com.tesca.dabbaapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.tesca.dabbaapp.Estructuras.Cartucho;
import com.tesca.dabbaapp.Estructuras.Custom_Order;
import com.tesca.dabbaapp.Estructuras.Custom_User;
import com.tesca.dabbaapp.Estructuras.Orden;
import com.tesca.dabbaapp.Estructuras.Paquete;
import com.tesca.dabbaapp.Estructuras.Paquete_Recipe;
import com.tesca.dabbaapp.Estructuras.User_Dabba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private String TAG = "TAG";
    private static String url = "http://dabbawala.com.mx/api/v1/customer-orders/?format=json";
    ArrayList<HashMap<String, String>> contactList;
    private ListView lv;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        contactList = new ArrayList<>();
        lv = (ListView)findViewById(R.id.lista);

        if(savedInstanceState == null) {
            new GetContacts().execute();
        }

    }

    @Override  //Refresh activity
    protected void onRestart (){
        super.onRestart();
        Intent i = new Intent(MainActivity.this,MainActivity.class);
        finish();
        startActivity(i);
    }

    private class GetContacts extends AsyncTask<Object, Object, Json_Request> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            lv.setAdapter(null);

        }

        @Override
        protected Json_Request doInBackground(Object... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Json_Request json_request = new Json_Request();

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Leer atributos generales

                    int count = jsonObj.getInt("count");
                    String next = jsonObj.getString("next");
                    String previous = jsonObj.getString("previous");

                    List<Orden> lista_de_ordenes = new ArrayList<>();

                    json_request.setCount(count);
                    //Leer resultados

                    JSONArray results = jsonObj.getJSONArray("results");
                    // Iterando a todos los resultados de pedidos

                    for (int i = 0; i < results.length(); i++) {

                        Orden orden = new Orden();

                        List<Cartucho> lista_de_cartuchos = new ArrayList<>();
                        List<Paquete> lista_de_paquetes = new ArrayList<>();

                        JSONObject pedido = results.getJSONObject(i);

                        //Lee pedido en general
                        String id_pedido = pedido.getString("id");
                        String created_at = pedido.getString("created_at");
                        String delivery_date = pedido.getString("delivery_date");

                        orden.setId(id_pedido);
                        orden.setCreated_date(created_at);
                        orden.setDelivery_date(delivery_date);

                        //Lee usuario

                        JSONObject customer_user = pedido.getJSONObject("customer_user");
                        Custom_User custom_user = new Custom_User();

                        JSONObject user = customer_user.getJSONObject("user");
                        User_Dabba user1 = new User_Dabba();

                        user1.setId(user.getString("id"));
                        user1.setEmail(user.getString("email"));
                        user1.setFirst_name(user.getString("first_name"));
                        user1.setLast_name(user.getString("last_name"));
                        String username = user.getString("username");
                        user1.setUsername(username);

                        String phone_number = customer_user.getString("phone_number");
                        custom_user.setUser(user1);
                        custom_user.setPhone_number(phone_number);

                        orden.setCustom_user(custom_user);

                        //Lee lista de combos y platillos
                        JSONArray custom_order_details = pedido.getJSONArray("customer_order_details");

                        for(int cs=0;cs<custom_order_details.length();cs++){

                            Custom_Order custom_order = new Custom_Order();

                            JSONObject element = custom_order_details.getJSONObject(cs);

                            String id_orden = element.getString("id");
                            boolean car = element.isNull("cartridge");
                            boolean paq = element.isNull("package_cartridge");
                            String quantity_element = element.getString("quantity");

                            custom_order.setId(id_orden);
                            custom_order.setQuantity(quantity_element);

                            if(!car){

                                JSONObject cartridge = element.getJSONObject("cartridge");

                                Cartucho cartucho = new Cartucho();

                                String id_cartucho = cartridge.getString("id");
                                String name = cartridge.getString("name");
                                String price = cartridge.getString("price");
                                String category = cartridge.getString("category");
                                String image = cartridge.getString("image");

                                cartucho.setId(id_cartucho);
                                cartucho.setName(name);
                                cartucho.setCategory(category);
                                cartucho.setPrice(price);
                                cartucho.setImage(image);

                                custom_order.setCartucho(cartucho);

                                lista_de_cartuchos.add(cartucho);
                            }

                            if(!paq){

                                JSONObject paquete = element.getJSONObject("package_cartridge");

                                String id_paquete = paquete.getString("id");
                                String name_paquete = paquete.getString("name");
                                String price_paquete = paquete.getString("price");
                                String image_paquete = paquete.getString("image");

                                Paquete nuevo_paquete = new Paquete();

                                nuevo_paquete.setId(id_paquete);
                                nuevo_paquete.setName(name_paquete);
                                nuevo_paquete.setPrice(price_paquete);
                                nuevo_paquete.setImage(image_paquete);

                                List<Paquete_Recipe> paquete_recipes = new ArrayList<>();

                                JSONArray paquete_recipe = paquete.getJSONArray("package_cartridge_recipe");

                                for(int z = 0;z<paquete_recipe.length();z++){

                                    JSONObject combo = paquete_recipe.getJSONObject(z);

                                    Paquete_Recipe recipe = new Paquete_Recipe();

                                    String id_paquete_recipe = combo.getString("id");

                                    JSONObject cartucho_combo = combo.getJSONObject("cartridge");

                                    Cartucho nuevo_cart = new Cartucho();

                                    String id = cartucho_combo.getString("id");
                                    String name_combo = cartucho_combo.getString("name");
                                    String price_combo = cartucho_combo.getString("price");
                                    String name_cartucho_combo = cartucho_combo.getString("category");
                                    String image_combo = cartucho_combo.getString("image");

                                    nuevo_cart.setId(id);
                                    nuevo_cart.setImage(image_combo);
                                    nuevo_cart.setPrice(price_combo);
                                    nuevo_cart.setCategory(name_cartucho_combo);
                                    nuevo_cart.setName(name_combo);

                                    String quantitya = combo.getString("quantity");

                                    recipe.setId(id_paquete_recipe);
                                    recipe.setCartucho(nuevo_cart);
                                    recipe.setQuantity(quantitya);

                                    paquete_recipes.add(recipe);

                                }

                                nuevo_paquete.setPack_cartridges(paquete_recipes);
                                custom_order.setPaquete(nuevo_paquete);
                                lista_de_paquetes.add(nuevo_paquete);

                            }

                        }

                        String status = pedido.getString("status");
                        String price = pedido.getString("price");
                        String latitude = pedido.getString("latitude");
                        String longitude = pedido.getString("longitude");

                        orden.setStatus(status);
                        orden.setPrice(price);
                        orden.setLatitude(latitude);
                        orden.setLongitude(longitude);
                        orden.setLista_de_cartuchos(lista_de_cartuchos);
                        orden.setLista_de_paquetes(lista_de_paquetes);

                        // Converting dateFormat

                        String originalString = delivery_date.substring(0,19);
                        String show_time = "00:00";

                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(originalString);
                            SimpleDateFormat output = new SimpleDateFormat("HH:mm");
                            show_time = output.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String original_created = created_at.substring(0,19);
                        String created_date = "00:00";

                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(original_created);
                            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            created_date = output.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // Añadir cada campo al Hash Map ("nombre de atributo",atributo)

                        contact.put("latitude", latitude);
                        contact.put("longitude", longitude);
                        contact.put("price", price);
                        contact.put("customer",username);
                        contact.put("status",status);
                        contact.put("created_time",created_date);
                        contact.put("show_time",show_time);
                        contact.put("delivery_date",delivery_date);

                        // adding contact to contact list


                        String delivery_string = orden.getDelivery_date();

                        Date datex = null;

                        try {
                             datex = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(delivery_string);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Calendar delivery_cal = toCalendar(datex);
                        Calendar current_date = Calendar.getInstance();

                        if(delivery_cal.getTimeInMillis()>current_date.getTimeInMillis()){
                            lista_de_ordenes.add(orden);
                            contactList.add(contact);
                        }


                    }

                    json_request.setCount(count);
                    json_request.setNext(next);
                    json_request.setPreviuos(previous);
                    json_request.setResultados(lista_de_ordenes);


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return json_request;
        }

        @Override
        protected void onPostExecute(final Json_Request result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"customer","price","status","show_time","created_time"},
                                        new int[]{R.id.customer,R.id.price, R.id.status,R.id.hour_tv,R.id.created_time});

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(MainActivity.this, Tabbed_Requests.class);
                    intent.putExtra("object", result);
                    intent.putExtra("position",i);
                    startActivity(intent);

                }
            });
        }

    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void notification(){

        String ACTION_DISMISS = "Dissmiss";

        Intent dismissIntent = new Intent(this, MainActivity.class);

        dismissIntent.setAction(ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        android.support.v4.app.NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.moto2)
                        .setContentTitle("Nuevo pedido")
                        .setContentText("Ah llegado un nuevo pedido")
                        .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                        .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                        .addAction(R.drawable.done,
                                getString(R.string.gotoApp), piDismiss);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }
}
