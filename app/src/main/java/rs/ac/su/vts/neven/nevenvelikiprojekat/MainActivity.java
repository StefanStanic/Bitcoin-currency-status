package rs.ac.su.vts.neven.nevenvelikiprojekat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class    MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    NumberFormat formatter = NumberFormat.getNumberInstance();

    // URL to get contacts JSON
    private static String url = "https://api.coindesk.com/v1/bpi/currentprice.json";

    ArrayList<HashMap<String, String>> bitcoinList;

    HttpHandler sh = new HttpHandler();

    // Making a request to url and getting response
    String jsonStr = sh.makeServiceCall(url);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //chart handles

        bitcoinList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetCurrency().execute();

    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int imageResource = android.R.drawable.ic_dialog_alert;
        Drawable image = getResources().getDrawable(R.drawable.icon_bc);

        builder.setTitle("Exit").setMessage("Are you sure you want to exit?").setIcon(image).setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();

    }


    public void onClickGoInformation(View c)
    {
        setContentView(R.layout.information);
    }
    public void onClickGoBack(View d)
    {
        setContentView(R.layout.list_item);
        startActivity(new Intent(this, MainActivity.class));
    }



    /**
     * Async task class to get json by making HTTP call
     */
    private class GetCurrency extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject bpiObject = new JSONObject(jsonStr);
                    JSONObject timeObject = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject bitcoin = bpiObject.getJSONObject("bpi");
                    JSONObject bitUSD=bitcoin.getJSONObject("USD");
                    JSONObject bitGBP=bitcoin.getJSONObject("GBP");
                    JSONObject bitEUR=bitcoin.getJSONObject("EUR");



                    Double BITUSD = bitUSD.getDouble("rate_float");
                    Double BITGBP = bitGBP.getDouble("rate_float");
                    Double BITEUR = bitEUR.getDouble("rate_float");

                    String BITUSDReal=BITUSD.toString();
                    String BITGBPReal=BITGBP.toString();
                    String BITEURReal=BITEUR.toString();


                    // tmp hash map za jedan currency
                    HashMap<String, String> currencySingle = new HashMap<>();

                    // adding each child node to HashMap key => value
                    currencySingle.put("BITUSD", BITUSDReal);
                    currencySingle.put("BITGBP", BITGBPReal);
                    currencySingle.put("BITEUR", BITEURReal);

                    // adding contact to contact list
                    bitcoinList.add(currencySingle);

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

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, bitcoinList,
                    R.layout.list_item, new String[]{"BITUSD", "BITGBP",
                    "BITEUR"}, new int[]{R.id.BITUSD,
                    R.id.BITGBP, R.id.BITEUR});


            lv.setAdapter(adapter);
        }

    }
}
