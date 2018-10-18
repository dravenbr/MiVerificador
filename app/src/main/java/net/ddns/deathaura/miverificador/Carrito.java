package net.ddns.deathaura.miverificador;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class Carrito extends AppCompatActivity {
    IntentIntegrator barcode_scanner;
    Button button_guardar;
    TextView label_total;
    ListView list1;
    AdaptadorProductos adaptador;
    ArrayList<Producto> array_productos;
    Spinner combo_tiendas;
    DBHelper database;
    double precio;
    double total;
    int cantidad;
    IntentResult result;
    String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DBHelper(this);
        total = 0.0;
        setContentView(R.layout.activity_carrito);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        array_productos = new ArrayList<Producto>();
        adaptador = new AdaptadorProductos(this);
        list1 = findViewById(R.id.list1);
        list1.setAdapter(adaptador);
        registerForContextMenu(list1);
        button_guardar = findViewById(R.id.button_guardar);
        label_total = findViewById(R.id.label_total);
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray = database.getAllTiendas();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        combo_tiendas = findViewById(R.id.combo_tienda);
        combo_tiendas.setAdapter(adapter);
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
        button_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Producto p : array_productos) {
                    try {
                        database.insertRegistro(p.getCodigo(), combo_tiendas.getSelectedItem().toString(), p.getPrecio());
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                Toast.makeText(getApplicationContext(), "Datos de la compra guardados", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_listview, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                Producto p = (Producto) list1.getItemAtPosition(info.position);
                adaptador.remove(p);
                adaptador.notifyDataSetChanged();
                total -= p.getSubtotal();
                label_total.setText("$" + Double.toString(total));
                //((AdapterView.AdapterContextMenuInfo)info).position
                // remove stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    ///En este método se obtiene el numero del código de barras del producto.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                barcode = result.getContents();
                //con el código haces la consulta a la db para obtener la info del producto
                try {
                    if (database.isRegistered(barcode)) {
                        launchUserInput();
                    } else {
                        Toast.makeText(getApplicationContext(), "Producto no registrado", Toast.LENGTH_SHORT).show();
                        Intent alta = new Intent(Carrito.this, Alta.class);
                        alta.putExtra("code", barcode); //Optional parameters
                        Carrito.this.startActivity(alta);
                        launchUserInput();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void launchUserInput() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.input_precio, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialog_precio = mView.findViewById(R.id.userInputDialog_precio);
        final TextView userDialog_nombre = mView.findViewById(R.id.userDialog_nombre);
        final EditText userInputDialog_cantidad = mView.findViewById(R.id.userInputDialog_cantidad);
        userInputDialog_cantidad.setText("1");
        userDialog_nombre.setText(database.getProductoNombre(barcode));
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (userInputDialog_precio.getText().toString() == null || userInputDialog_precio.getText().toString().isEmpty()) {
                            precio = 0.0;
                        } else {
                            precio = Double.parseDouble(userInputDialog_precio.getText().toString());
                        }
                        cantidad = Integer.parseInt(userInputDialog_cantidad.getText().toString());
                        adaptador.add(new Producto(barcode, database.getProductoNombre(barcode), precio, cantidad, precio * cantidad));
                        adaptador.notifyDataSetChanged();
                        total += precio * cantidad;
                        label_total.setText("TOTAL: $" + Double.toString(total));
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    class AdaptadorProductos extends android.widget.ArrayAdapter<Producto> {

        AppCompatActivity appCompatActivity;

        AdaptadorProductos(AppCompatActivity context) {
            super(context, R.layout.item_compra, array_productos);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.item_compra, null);

            TextView producto = item.findViewById(R.id.sublabel_producto);
            producto.setText(array_productos.get(position).getNombre());

            TextView precio = item.findViewById(R.id.sublabel_precio);
            precio.setText("$" + (Double.toString(array_productos.get(position).getPrecio())));

            TextView cantidad = item.findViewById(R.id.sublabel_cantidad);
            cantidad.setText(Integer.toString(array_productos.get(position).getCantidad()));

            TextView subtotal = item.findViewById(R.id.sublabel_subtotal);
            subtotal.setText("$" + (Double.toString(array_productos.get(position).getSubtotal())));

            return (item);
        }
    }
}
