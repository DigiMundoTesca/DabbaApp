package com.tesca.dabbaapp;


import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.tesca.dabbaapp.R.id.lista;

public class MainActivity extends AppCompatActivity {

    private String TAG = "TAG";
    private RecyclerView lv;
    private SwipeRefreshLayout refreshLayout;
    private OrdenAdapter orden;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.overridePendingTransition(R.anim.slide_out,
                R.anim.slide_in);

        new GetContacts().execute();

        lv = (RecyclerView) findViewById(lista);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new GetContacts().execute();
                        refreshLayout.setRefreshing(false);
                    }
                }
        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new GetContacts().execute();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class GetContacts extends AsyncTask<Object, Object, List<Orden>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Orden> doInBackground(Object... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "http://dabbanet.dabbawala.com.mx/api/v1/customer-orders/?format=json";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            List<Orden> lista_de_ordenes = null;

            if (jsonStr != null) {
                try {

                    //Lee el resultado, convierte a arreglo y luego a Lista de ordenes

                    JSONArray array_pedidos = new JSONArray(jsonStr);
                    lista_de_ordenes = new ArrayList<>();

                    // Iterando a todos los resultados de pedidos

                    for (int i = 0; i < array_pedidos.length(); i++) {

                        //Crea nueva Orden

                        Orden orden = new Orden();
                        List<Cartucho> lista_de_cartuchos = new ArrayList<>();
                        List<Paquete> lista_de_paquetes = new ArrayList<>();

                        JSONObject pedido = array_pedidos.getJSONObject(i);

                        //Lee orden en general
                        orden.setId(pedido.getString("id"));
                        orden.setCreated_date(pedido.getString("created_at"));
                        orden.setDelivery_date(pedido.getString("delivery_date"));

                        //Lee usuario

                        JSONObject customer_user = pedido.getJSONObject("customer_user");
                        JSONObject user = customer_user.getJSONObject("user");
                        String phone_number = customer_user.getString("phone_number");

                        Custom_User custom_user = new Custom_User(
                                new User_Dabba(
                                        user.getString("id"),
                                        user.getString("username"),
                                        user.getString("first_name"),
                                        user.getString("last_name"),
                                        user.getString("email")
                                ),phone_number);

                        orden.setCustom_user(custom_user);

                        //Lee lista de combos y platillos
                        JSONArray custom_order_details = pedido.getJSONArray("customer_order_details");

                        for (int cs = 0; cs < custom_order_details.length(); cs++) {

                            Custom_Order custom_order = new Custom_Order();

                            JSONObject element = custom_order_details.getJSONObject(cs);

                            String id_orden = element.getString("id");
                            boolean car = element.isNull("cartridge");
                            boolean paq = element.isNull("package_cartridge");
                            String quantity_element = element.getString("quantity");

                            custom_order.setId(id_orden);
                            custom_order.setQuantity(quantity_element);

                            if (!car) {

                                JSONObject cartridge = element.getJSONObject("cartridge");

                                Cartucho cartucho = new Cartucho();

                                cartucho.setId(cartridge.getString("id"));
                                cartucho.setName(cartridge.getString("name"));
                                cartucho.setCategory(cartridge.getString("category"));
                                cartucho.setPrice(cartridge.getString("price"));
                                cartucho.setImage(cartridge.getString("image"));

                                custom_order.setCartucho(cartucho);
                                lista_de_cartuchos.add(cartucho);
                            }

                            if (!paq) {

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

                                for (int z = 0; z < paquete_recipe.length(); z++) {

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

                        orden.setStatus(pedido.getString("status"));
                        orden.setPrice(pedido.getString("price"));
                        orden.setLatitude(pedido.getString("latitude"));
                        orden.setLongitude(pedido.getString("longitude"));
                        orden.setLista_de_cartuchos(lista_de_cartuchos);
                        orden.setLista_de_paquetes(lista_de_paquetes);

                        String delivery_string = orden.getDelivery_date();

                        //Verificar si el pedido es futuro y agregarlo a la lista
                        Date datex = null;
                        try {
                            datex = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(delivery_string);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar delivery_cal = toCalendar(datex);
                        Calendar current_date = Calendar.getInstance();
                        if (delivery_cal.getTimeInMillis() > current_date.getTimeInMillis()) {
                            lista_de_ordenes.add(orden);
                        }
                    }
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

                return lista_de_ordenes;
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
                return null;
            }


        }

        @Override
        protected void onPostExecute(final List<Orden> result) {
            super.onPostExecute(result);

            lv.setHasFixedSize(true);
            RecyclerView.LayoutManager lManager = new LinearLayoutManager(MainActivity.this);
            lv.setLayoutManager(lManager);
            orden = new OrdenAdapter(result);
            lv.setAdapter(orden);
        }

    }

    public class OrdenAdapter extends RecyclerView.Adapter<OrdenAdapter.OrdenViewHolder> {

        private List<Orden> items;

        class OrdenViewHolder extends RecyclerView.ViewHolder {
            // Campos respectivos de un item

            TextView customer;
            TextView price;
            TextView status;
            TextView hora_entrega;
            TextView hora_creacion;
            CardView card_view;


            public OrdenViewHolder(View v) {
                super(v);
                customer = (TextView) v.findViewById(R.id.customer);
                price = (TextView) v.findViewById(R.id.price);
                status = (TextView) v.findViewById(R.id.status);
                hora_entrega = (TextView) v.findViewById(R.id.hour_tv);
                hora_creacion = (TextView) v.findViewById(R.id.created_time);
                card_view = (CardView) v.findViewById(R.id.card_layout);
            }
        }

        OrdenAdapter(List<Orden> items) {
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public OrdenViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_view, viewGroup, false);
            return new OrdenViewHolder(v);
        }

        @Override
        public void onBindViewHolder(OrdenViewHolder viewHolder, final int i) {

            User_Dabba us = items.get(i).getCustom_user().getUser();

            viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Tabbed_Requests.class);
                    intent.putExtra("object", (Serializable) items);
                    intent.putExtra("position", i);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                }
            });
            viewHolder.customer.setText(us.getFirst_name() + " " + us.getLast_name());
            viewHolder.status.setText(items.get(i).getStatus());
            viewHolder.price.setText(items.get(i).getPrice());

            String originalString = items.get(i).getDelivery_date().substring(0, 19);
            String show_time = "00:00";

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(originalString);
                SimpleDateFormat output = new SimpleDateFormat("HH:mm");
                show_time = output.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String original_created = items.get(i).getCreated_date().substring(0, 19);
            String created_date = "00:00";

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(original_created);
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                created_date = output.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            viewHolder.hora_creacion.setText(created_date);
            viewHolder.hora_entrega.setText(show_time);



        }
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void notification() {

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
