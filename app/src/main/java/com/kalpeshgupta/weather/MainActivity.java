package com.kalpeshgupta.weather;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private TextView tv1,tv2;
    private String temperature;
    private EditText et1,et2;
    private SwipeRefreshLayout slayout;

    /*@Override
    public boolean OnCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        return true;

    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.kelvin);
        tv2 = findViewById(R.id.celcius);
        slayout = findViewById(R.id.swipe1) ;
        slayout.setColorSchemeResources(R.color.colorAccent);
       slayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetTemperature().execute(et1.getText().toString(), et2.getText().toString());
                slayout.setRefreshing(false);
            }
        });
        /*MenuItem pop = new Menu();
        pop.getItem(R.menu.menu1);
        onOptionsItemSelected(pop.getItem(R.id.it1));*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.it1 : SearchDialog(); break;
        }
        return true;
    }

    public void SearchDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog,null);
        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();

        et1 = dialogView.findViewById(R.id.city);
        et2 = dialogView.findViewById(R.id.coun);
        Button b1 = dialogView.findViewById(R.id.but1);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                new GetTemperature().execute(et1.getText().toString(), et2.getText().toString());
            }
        });
        dialog.show();
    }


    private class GetTemperature extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is Downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... voids) {
            HttpHandler sh = new HttpHandler();
            Log.i("City" , voids[0]+","+voids[1]);
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + voids[0] + "," + voids[1] + "&appid=fd04edf8988d8b3c25febc0874545232";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG,"Response from URL : " + jsonStr);
            if(jsonStr != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject temp = jsonObj.getJSONObject("main");
                    temperature = temp.getString("temp");
                }catch (final JSONException e)
                {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"JSON parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else {
                Log.e(TAG,"Couldn't get Json from the server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get JSON from server. Check LogCat for possible errors! ",Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            Double degCelcius = Double.parseDouble(temperature);
            degCelcius = degCelcius - 273.15 ;
            Formatter fmt = new Formatter();
            tv2.setText(fmt.format("%.3f",degCelcius).toString() + " deg. Celcius");
            tv1.setText(temperature + " Kelvin");
        }
    }
}

