package com.example.stockmonitor3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    Button button;
    ListView listView;
    EditText editText;
    EditText editText2;
    String stockID = "";
    String stockName = "";
    Toast AddID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btnHit);
        listView = findViewById(R.id.user_list);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        AddID.makeText(MainActivity.this, "ADD VALUES", Toast.LENGTH_SHORT).show();

        StockPriceFetcher task = new StockPriceFetcher();
        task.execute();
    }

    public class StockPriceFetcher extends AsyncTask<String, String, String> {

        @Override
        public String doInBackground(String... strings) {
            final String data = loadFromWeb("https://financialmodelingprep.com/api/company/price/AAPL,INTC,IBM,GOOGL,FB,NOK,RHT,MSFT,AMZN,BRK-B,BABA,JNJ,JPM,XOM,BAC,WMT,WFC,RDS-B,V,PG,BUD,T,TWX,CVX,UNH,PFE,CHL,HD,TSM,VZ,ORCL,C,NVS?datatype=json");
            if (data != null) {
                final ArrayList<String> stockDatas = parseStockData(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,stockDatas);
                        listView.setAdapter(arrayAdapter);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stockName = editText2.getText().toString();
                                stockID = editText.getText().toString();
                                double jari = parseStockDataUser(data);
                                    for (int i = 0; i < parseStockDataUser(data); i++) {
                                        if (!editText2.getText().toString().equals(stockName)) {
                                            Toast.makeText(MainActivity.this, "ADD STOCK NAME", Toast.LENGTH_SHORT).show();
                                            break;}
                                        else if (!editText.getText().toString().equals(stockID)) {
                                            Toast.makeText(MainActivity.this, "ADD VALID STOCK ID", Toast.LENGTH_SHORT).show();
                                            break;}
                                        else if (editText.getText().toString().equals(stockID)) {
                                            Toast.makeText(MainActivity.this, "STOCK " + stockName +  " ADDED SUCCESFULLY", Toast.LENGTH_SHORT).show();
                                            arrayAdapter.add(stockName + " " + jari + "");
                                            break;}
                                        else if (editText.getText().toString().equals("") && editText2.getText().toString().equals("")){
                                            AddID.makeText(MainActivity.this, "ADD VALUES", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                }
                        });

                    }
                });
            }

            return data;
        }
        private ArrayList<String> parseStockData(String data) {
            ArrayList<String> stockDatas = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                Iterator<String> it = jsonObject.keys();
                int i = 0;
                while (it.hasNext()) {
                    String key = it.next();
                    JSONObject stock = jsonObject.getJSONObject(key);
                    double stockPrice = stock.getDouble("price");
                    stockDatas.add(" " + key + " " + stockPrice);
                    i++;
                    if(i > 6){
                        break;
                    }

                }
            } catch (Exception e){e.printStackTrace();} return stockDatas;
        }
        private double parseStockDataUser(String data){
            double jari = 0;
            try{
                JSONObject jsonObject2 = new JSONObject(data);
                JSONObject added = jsonObject2.getJSONObject(stockID);
                 jari = added.getDouble("price");
            } catch (JSONException e) {
                e.printStackTrace();
            }return jari;
        }
        public String loadFromWeb(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                String htmlText = Utilities.fromStream(in);
                return htmlText;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
