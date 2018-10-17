package net.ddns.deathaura.miverificador;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    ///Instancia de zxing para el escaner del código de barras
    IntentIntegrator barcode_scanner;
    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase mydatabase = openOrCreateDatabase("miverificador.db", MODE_PRIVATE, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///Ajustes para el scanner de código de barras
        barcode_scanner = new IntentIntegrator(this);
        barcode_scanner.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        barcode_scanner.setPrompt("Dirige el escaner al código de barras del producto.");
        barcode_scanner.setOrientationLocked(false);
        barcode_scanner.setBeepEnabled(true);

        ///el fab lanza el scanner cuando es presionado
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcode_scanner.initiateScan();
            }
        });
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
        switch (id) {

            case R.id.action_alta:
                Intent alta = new Intent(MainActivity.this, Alta.class);
                MainActivity.this.startActivity(alta);
                break;
            case R.id.action_tienda:
                Intent tienda = new Intent(MainActivity.this, Alta_Tienda.class);
                MainActivity.this.startActivity(tienda);
                break;
            default:
                break;
        }

        //noinspection SimplifiableIfStatement
        /*if (id == ) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    ///En este método se obtiene el numero del código de barras del producto.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                //con el código haces la consulta a la db para obtener la info del producto
                String resultado = result.getContents();
                Toast.makeText(this, "Código: " + resultado, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
