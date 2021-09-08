package com.example.pisid2021.APP;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.opengl.EGLExt;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.pisid2021.APP.Connection.ConnectionHandler;
import com.example.pisid2021.APP.Database.DatabaseHandler;
import com.example.pisid2021.APP.Database.DatabaseReader;
import com.example.pisid2021.APP.Helper.UserLogin;
import com.example.pisid2021.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MedicoesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();

    String getMedicoes = "---";
    DatabaseHandler db = new DatabaseHandler(this);

    Handler h = new Handler();
    int delay = 1000; //1 second=1000 milisecond
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicoes);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Spinner spinner = findViewById(R.id.chooser);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tabelas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        h.postDelayed( runnable = new Runnable() {
            public void run() {
                updateMedicoes();
                drawGraphs();

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void alertas(View v){
        Intent i = new Intent(this, AlertasActivity.class);
        startActivity(i);
    }

    private void updateMedicoes(){
        db.clearMedicoes();
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        ConnectionHandler jParser = new ConnectionHandler();
        if(!getMedicoes.equals("---") || getMedicoes != null) {
            JSONArray medicoes = jParser.getJSONFromUrl(getMedicoes, params);
            try {
                if (medicoes != null) {
                    for (int i = 0; i < medicoes.length(); i++) {
                        JSONObject c = medicoes.getJSONObject(i);
                        String hora = c.getString("Hora");
                        double leitura;
                        boolean outlier = false;
                        try {
                            leitura = c.getDouble("Leitura");
                            outlier = c.getBoolean("Outlier");
                        } catch (Exception e) {
                            leitura = -1000.0;
                        }
                        db.insertMedicao(hora, leitura, outlier);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseMedicoes(String text) {
        if(text.equals("---") || text == null) {
            getMedicoes = "---";
        } else {
            getMedicoes = "http://" + IP + ":" + PORT + "/scripts/getMedicoes" + text + ".php";
        }
    }

    private void drawGraphs(){
        GraphView graph = findViewById(R.id.graph);
        graph.removeAllSeries();
        int helper=0;
        DatabaseReader dbReader = new DatabaseReader(db);
        Cursor cursorTemperatura = dbReader.readMedicoes();
        Date currentTimestamp = new Date();
        long currentLong = currentTimestamp.getTime();

        DataPoint[] datapointsTemperatura = new DataPoint[cursorTemperatura.getCount()];

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursorTemperatura.moveToNext()){
            String hora =  cursorTemperatura.getString(cursorTemperatura.getColumnIndex("Hora"));
            Integer valorMedicao = cursorTemperatura.getInt(cursorTemperatura.getColumnIndex("Leitura"));
            try {
                Date date = format.parse(hora);
                long pointLong = date.getTime();
                long difference = currentLong - pointLong;
                double seconds = 300 - TimeUnit.MILLISECONDS.toSeconds(difference);
                datapointsTemperatura[helper]=new DataPoint(seconds,valorMedicao);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            helper++;
        }
        cursorTemperatura.close();

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(300);
        LineGraphSeries<DataPoint> seriesTemperatura = new LineGraphSeries<>(datapointsTemperatura);
        seriesTemperatura.setColor(Color.RED);
        seriesTemperatura.setTitle("Temperatura");
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"300"," 250", "200", "150", "100", "50", "0"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setBackgroundColor(Color.alpha(0));
        graph.addSeries(seriesTemperatura);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String text = parent.getItemAtPosition(i).toString();
        if(text.equals("---") || text == null || text.equals("")) {
            Toast.makeText(parent.getContext(), "Escolha uma medição", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
            chooseMedicoes(text);

            updateMedicoes();
            drawGraphs();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
