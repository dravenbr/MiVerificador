package net.ddns.deathaura.miverificador;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class Verificador extends AppCompatActivity {
    IntentIntegrator barcode_scanner;
    TextView codigo;
    TextView nombre;
    ImageView barcode;
    String code;
    DBHelper database;
    AdaptadorRegistros adaptador;
    ListView lv1;
    ArrayList<Registros> registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        registros = new ArrayList<Registros>();
        barcode = findViewById(R.id.imageView2);
        codigo = findViewById(R.id.edit_codigo);
        nombre = findViewById(R.id.label_nombre);
        database = new DBHelper(this);
        adaptador = new AdaptadorRegistros(this);
        lv1 = findViewById(R.id.list1);
        lv1.setAdapter(adaptador);
//Ajustes para el scanner de código de barras
        barcode_scanner = new IntentIntegrator(this);
        barcode_scanner.setDesiredBarcodeFormats(IntentIntegrator.EAN_13, IntentIntegrator.UPC_A, IntentIntegrator.UPC_E, IntentIntegrator.EAN_8);
        barcode_scanner.setPrompt("Dirige el escaner al código de barras del producto.");
        barcode_scanner.setOrientationLocked(false);
        barcode_scanner.setBeepEnabled(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                barcode_scanner.initiateScan();
            }
        });
    }

    ///En este método se obtiene el numero del código de barras del producto.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                //con el código haces la consulta a la db para obtener la info del producto

                try {
                    code = result.getContents();
                    barcodeGenerator(code);
                    codigo.setText(code);
                    nombre.setText(database.getProductoNombre(code));
                    adaptador.clear();
                    for (Registros r : database.getRegistros(code)) {
                        adaptador.add(r);
                    }
                    //registros=database.getRegistros(code);

                    adaptador.notifyDataSetChanged();
                    //Toast.makeText(getApplicationContext(), "Agregado correctamente", Toast.LENGTH_SHORT).show();

                } catch (Exception ex) {
                    // Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Producto no registrado", Toast.LENGTH_LONG).show();
                    Intent alta = new Intent(Verificador.this, Alta.class);
                    alta.putExtra("code", code); //Optional parameters
                    Verificador.this.startActivity(alta);
                }


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void barcodeGenerator(String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.EAN_13, 280, 115);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
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
        switch (id) {
            case R.id.action_alta:
                Intent alta = new Intent(Verificador.this, Alta.class);
                Verificador.this.startActivity(alta);
                break;
            case R.id.action_tienda:
                Intent tienda = new Intent(Verificador.this, Alta_Tienda.class);
                Verificador.this.startActivity(tienda);
                break;
            case R.id.action_compra:
                Intent carrito = new Intent(Verificador.this, Carrito.class);
                Verificador.this.startActivity(carrito);
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

    class AdaptadorRegistros extends android.widget.ArrayAdapter<Registros> {

        AppCompatActivity appCompatActivity;

        AdaptadorRegistros(AppCompatActivity context) {
            super(context, R.layout.registro, registros);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.registro, null);

            TextView tienda = item.findViewById(R.id.lay_tienda);
            tienda.setText(registros.get(position).getTienda());

            TextView precio = item.findViewById(R.id.lay_precio);
            precio.setText("$" + (Double.toString(registros.get(position).getPrecio())));

            TextView fecha = item.findViewById(R.id.lay_fecha);
            fecha.setText(registros.get(position).getFecha());

            return (item);
        }
    }
}