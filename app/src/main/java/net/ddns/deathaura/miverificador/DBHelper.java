package net.ddns.deathaura.miverificador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "miverificador.db";

    public static final String PRODUCTOS_TABLE_NAME = "productos";
    public static final String PRODUCTOS_COL_CODIGO = "codigo";
    public static final String PRODUCTOS_COL_NOMBRE = "nombre";

    public static final String TIENDAS_TABLE_NAME = "tiendas";
    public static final String TIENDAS_COL_NOMBRE = "nombre";

    public static final String REGISTROS_TABLE_NAME = "registros";
    public static final String REGISTROS_COL_PRODUCTO = "producto";
    public static final String REGISTROS_COL_TIENDA = "tienda";
    public static final String REGISTROS_COL_PRECIO = "precio";
    public static final String REGISTROS_COL_FECHA = "fecha";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table productos (codigo text primary key, nombre text, UNIQUE(codigo))");
        db.execSQL("create table tiendas (nombre text primary key, UNIQUE(nombre))");
        db.execSQL("create table registros (producto text, tienda text, precio double, fecha text, foreign key (producto) references productos(codigo) on update cascade on delete cascade, foreign key (tienda) references tiendas(nombre) on update cascade on delete cascade)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS tiendas");
        db.execSQL("DROP TABLE IF EXISTS registros");
        onCreate(db);
    }

    public void insertProducto(String codigo_producto, String nombre_producto, String tienda, double precio) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO productos (codigo, nombre) VALUES('" + codigo_producto + "','" + nombre_producto + "')");
        ContentValues contentValues = new ContentValues();
        contentValues.put(REGISTROS_COL_PRODUCTO, codigo_producto);
        contentValues.put(REGISTROS_COL_TIENDA, tienda);
        contentValues.put(REGISTROS_COL_PRECIO, precio);
        contentValues.put(REGISTROS_COL_FECHA, new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()));
        db.insert(REGISTROS_TABLE_NAME, null, contentValues);
    }

    public void insertTienda(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO tiendas (nombre) VALUES('" + nombre + "')");
    }

    public boolean insertContact(String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public String getProductoNombre(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select nombre from productos where codigo=" + codigo + "", null);
        res.moveToFirst();
        return res.getString(res.getColumnIndex(PRODUCTOS_COL_NOMBRE));
    }

    public boolean isRegistered(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + PRODUCTOS_TABLE_NAME + " WHERE " + PRODUCTOS_COL_CODIGO + "='" + barcode + "'", null);

        return numRows != 0;

    }

    public boolean updateContact(Integer id, String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            //   array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllTiendas() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tiendas", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(TIENDAS_COL_NOMBRE)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Registros> getRegistros(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Registros> lista = new ArrayList<Registros>();
        Cursor res = db.rawQuery("select tienda, precio, fecha from registros where producto = '" + codigo + "' order by precio asc", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            lista.add(new Registros(codigo, res.getString(res.getColumnIndex(REGISTROS_COL_TIENDA)), Double.parseDouble(res.getString(res.getColumnIndex(REGISTROS_COL_PRECIO))), res.getString(res.getColumnIndex(REGISTROS_COL_FECHA))));
            res.moveToNext();
        }
        return lista;
    }
}