package net.ddns.deathaura.miverificador;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Alta_Tienda extends AppCompatActivity {
    EditText nombre;
    Button guardar;
    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DBHelper(this);
        setContentView(R.layout.activity_alta__tienda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nombre = findViewById(R.id.nombre_tienda);
        guardar = findViewById(R.id.button_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    database.insertTienda(nombre.getText().toString().trim().toUpperCase());
                    Toast.makeText(getApplicationContext(), "Agregado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
