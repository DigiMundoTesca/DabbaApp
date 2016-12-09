package com.tesca.dabbaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.tesca.dabbaapp.Estructuras.Cartucho;
import com.tesca.dabbaapp.Estructuras.Orden;
import com.tesca.dabbaapp.Estructuras.Paquete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class Tabbed_Requests extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private List<Orden> lista_resultados;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed__requests);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Contenido de la entrega");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        Json_Request object = (Json_Request) getIntent().getSerializableExtra("object");
        int position = getIntent().getExtras().getInt("position");
        lista_resultados = object.getResultados();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(position);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed__requests, menu);
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
            Intent intent = new Intent(Tabbed_Requests.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static List<Orden> lista;

        public PlaceholderFragment(List<Orden> lista) {
            PlaceholderFragment.lista =lista;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, List<Orden> lista) {
            PlaceholderFragment fragment = new PlaceholderFragment(lista);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed__requests, container, false);
            //Floating menu
            final FloatingActionMenu fam;
            final FloatingActionButton fab1,fab2,fab3;

            fam = (FloatingActionMenu) rootView.findViewById(R.id.material_design_android_floating_action_menu);
            fam.getMenuIconView().setImageResource(R.drawable.add);
            fab1 = (FloatingActionButton) rootView.findViewById(R.id.material_design_floating_action_menu_item1);
            fab2 = (FloatingActionButton) rootView.findViewById(R.id.material_design_floating_action_menu_item2);
            fab3 = (FloatingActionButton) rootView.findViewById(R.id.material_design_floating_action_menu_item3);

            fab1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    fam.setMenuButtonColorNormal(getResources().getColor(R.color.orange));
                    fam.getMenuIconView().setImageResource(R.drawable.pending);
                    fam.setClickable(false);
                }
            });
            fab2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    fam.setMenuButtonColorNormal(getResources().getColor(R.color.done));
                    fam.getMenuIconView().setImageResource(R.drawable.done);
                    fam.setClickable(false);
                }
            });
            fab3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    fam.setMenuButtonColorNormal(getResources().getColor(R.color.canceled));
                    fam.getMenuIconView().setImageResource(R.drawable.canceled);
                    fam.setClickable(false);
                }
            });


            TextView hora_tv = (TextView) rootView.findViewById(R.id.hour_tv);
            TextView created_time_tv = (TextView) rootView.findViewById(R.id.created_time);
            TextView customer_tv = (TextView) rootView.findViewById(R.id.customer);
            TextView price_tv = (TextView) rootView.findViewById(R.id.price);
            TextView status_tv = (TextView) rootView.findViewById(R.id.status);

            ListView list_view_cartuchos = (ListView) rootView.findViewById(R.id.lista);
            ListView list_view_paquetes = (ListView) rootView.findViewById(R.id.lista_paquetes);

            Button mapa = (Button) rootView.findViewById(R.id.button2);

            final int a = getArguments().getInt(ARG_SECTION_NUMBER);

            mapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(),MapsActivity.class);
                    i.putExtra("Orden",lista.get(a));
                    startActivity(i);


                }
            });




            ListAdapter_Cartuchos listAdapter_cartuchos = new ListAdapter_Cartuchos(getActivity(), R.layout.cartucho_element, lista.get(a).getLista_de_cartuchos());
            list_view_cartuchos.setAdapter(listAdapter_cartuchos);

            ListAdapter_Paquetes listAdapter_paquetes = new ListAdapter_Paquetes(getActivity(), R.layout.cartucho_element, lista.get(a).getLista_de_paquetes());
            list_view_paquetes.setAdapter(listAdapter_paquetes);

            String originalString = lista.get(a).getCreated_date().substring(0,19);
            String created_date = "00:00";

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(originalString);
                SimpleDateFormat output = new SimpleDateFormat("HH:mm");
                created_date = output.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            created_time_tv.setText(created_date);

            customer_tv.setText(lista.get(a).getCustom_user().getUser().getUsername());
            price_tv.setText(lista.get(a).getPrice());
            status_tv.setText(lista.get(a).getStatus());

            String delivery_String = lista.get(a).getDelivery_date().substring(0,19);
            String show_time = "00:00";

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(delivery_String);
                SimpleDateFormat output = new SimpleDateFormat("HH:mm");
                show_time = output.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            hora_tv.setText(show_time);




            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position ,lista_resultados);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return lista_resultados.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    public static class ListAdapter_Cartuchos extends ArrayAdapter<Cartucho>{

        public ListAdapter_Cartuchos(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter_Cartuchos(Context context, int resource, List<Cartucho> lista_cartuchos) {
            super(context, resource, lista_cartuchos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.cartucho_element, null);
            }

            Cartucho p = getItem(position);

            if (p != null) {
                ImageView tt1 = (ImageView) v.findViewById(R.id.imageView);
                TextView tt2 = (TextView) v.findViewById(R.id.textView);
                TextView tt3 = (TextView) v.findViewById(R.id.textView2);

                Picasso.with(getContext()).
                        load(p.getImage()).
                        into(tt1);

                tt2.setText(p.getName());
                tt3.setText(p.getPrice());
            }

            return v;
        }

    }

    public static class ListAdapter_Paquetes extends ArrayAdapter<Paquete>{

        public ListAdapter_Paquetes(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter_Paquetes(Context context, int resource, List<Paquete> lista_cartuchos) {
            super(context, resource, lista_cartuchos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.cartucho_element, null);
            }

            Paquete p = getItem(position);

            if (p != null) {

                ImageView tt1 = (ImageView) v.findViewById(R.id.imageView);
                TextView tt2 = (TextView) v.findViewById(R.id.textView);
                TextView tt3 = (TextView) v.findViewById(R.id.textView2);

                Picasso.with(getContext()).
                        load(p.getImage()).
                        into(tt1);

                tt2.setText(p.getName());
                tt3.setText(p.getPrice());
            }

            return v;
        }

    }
}
