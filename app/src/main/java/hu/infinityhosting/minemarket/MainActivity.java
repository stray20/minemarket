package hu.infinityhosting.minemarket;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private SMSDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasource = new SMSDataSource(this);
        datasource.open();
    }

    public void read(View view){
        try {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Kód beolvasás a MineMarketen");
            integrator.setResultDisplayDuration(0);
            integrator.setCameraId(0);
            integrator.initiateScan();
        }catch (Exception e){
            Log.e("Read error","Camera error");
        }
    }

    public void history(View view) throws ParseException {
        setContentView(R.layout.history);

        List<SMS> smses = datasource.getAllSMS();

        TableLayout table = (TableLayout)findViewById(R.id.tbl);
        TableRow tr_head = new TableRow(this);
        tr_head.setBackgroundColor(Color.GRAY);
        TextView lbl_date = new TextView(this);
        lbl_date.setText("Dátum");
        lbl_date.setTextColor(Color.WHITE);
        lbl_date.setPadding(5,5,10,5);
        tr_head.addView(lbl_date);

        TextView lbl_tel = new TextView(this);
        lbl_tel.setText("Telefonszám");
        lbl_tel.setTextColor(Color.WHITE);
        lbl_tel.setPadding(5,5,10,5);
        tr_head.addView(lbl_tel);

        TextView lbl_text = new TextView(this);
        lbl_text.setText("Szöveg");
        lbl_text.setTextColor(Color.WHITE);
        lbl_text.setPadding(5,5,10,5);
        tr_head.addView(lbl_text);

        TextView lbl_price = new TextView(this);
        lbl_price.setText("Ár");
        lbl_price.setTextColor(Color.WHITE);
        lbl_price.setPadding(5,5,5,5);
        tr_head.addView(lbl_price);

        table.addView(tr_head);

        for(SMS s : smses)
        {
            TableRow row= new TableRow(this);

            StringBuilder sb = new StringBuilder(s.getDate());

            Date d = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(s.getDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            TextView str = new TextView(this); str.setText(outputFormat.format(d));
            str.setPadding(5,5,15,5);
            row.addView(str);
            str.setText(s.getTel());
            row.addView(str);
            str.setText(s.getText());
            row.addView(str);
            str.setText(s.getPrice());
            row.addView(str);
            table.addView(row);

            Toast.makeText(getApplicationContext(), s.getTel(), Toast.LENGTH_SHORT).show();
        }
        table.requestLayout();     // Not sure if this is needed.
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setContentView(R.layout.activity_main);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume(){
        datasource.open();
        super.onResume();
    }

    @Override
    public void onPause(){
        datasource.close();
        super.onPause();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            try {
                final JSONObject jObj = new JSONObject(scanResult.getContents());
                Context context = getApplicationContext();
                final String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("SMS küldése");
                builder.setMessage("Biztos elküldöd az SMS-t?\n Üzenet: " + jObj.getString("uzenet") + " Ára: " + jObj.getString("ar"));

                builder.setPositiveButton("Küldés", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Toast toast = Toast.makeText(getApplicationContext(), "Az SMS küldése sikeres!", Toast.LENGTH_SHORT);
                        toast.show();

                        SmsManager sms = SmsManager.getDefault();
                        try {
                            //sms.sendTextMessage(jObj.getString("tel"),null,jObj.getString("uzenet"),null,null);
                            datasource.createSMS(date,jObj.getString("tel"),jObj.getString("uzenet"),jObj.getString("ar"),1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                            Toast toast = Toast.makeText(getApplicationContext(), "Az SMS küldés meghiúsult!", Toast.LENGTH_SHORT);
                            toast.show();
                        try {
                            datasource.createSMS(date,jObj.getString("tel"),jObj.getString("uzenet"),jObj.getString("ar"),2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
