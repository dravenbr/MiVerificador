package net.ddns.deathaura.miverificador;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class Alta extends AppCompatActivity {
    IntentIntegrator barcode_scanner;
    EditText precio;
    EditText codigo;
    EditText nombre;
    ImageView barcode;
    Button boton_alta;
    String code;
    DBHelper database;
    Spinner sItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        codigo = findViewById(R.id.text_codigo);
        barcode = findViewById(R.id.imageView);
        boton_alta = findViewById(R.id.boton_alta);
        database = new DBHelper(this);
        nombre = findViewById(R.id.text_nombre);
        precio = findViewById(R.id.precio);
        //Ajustes para el scanner de código de barras
        barcode_scanner = new IntentIntegrator(this);
        barcode_scanner.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        barcode_scanner.setPrompt("Dirige el escaner al código de barras del producto.");
        barcode_scanner.setOrientationLocked(false);
        barcode_scanner.setBeepEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcode_scanner.initiateScan();
            }
        });
        barcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                barcodeGenerator(code);
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            code = extras.getString("code");
            codigo.setText(code);
            barcodeGenerator(code);
        }
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray = database.getAllTiendas();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = findViewById(R.id.combo_tiendas);
        sItems.setAdapter(adapter);
        boton_alta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    database.insertProducto(code, nombre.getText().toString(), sItems.getSelectedItem().toString(), Double.parseDouble(precio.getText().toString()));
                    Toast.makeText(getApplicationContext(), "Agregado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
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
                code = result.getContents();
                codigo.setText(code);
                barcodeGenerator(code);
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
}
