package com.example.tfg_1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.loader.content.CursorLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BaseDatosPrendas extends SQLiteOpenHelper {
    public BaseDatosPrendas(Context context) {super(context, "bdPrendas", null, 1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tipoprenda(id_prenda INTEGER primary key AUTOINCREMENT, tipo_prenda TEXT, nombre_prenda TEXT, nombre_usuario TEXT, lavando INTEGER Default 0)");
        db.execSQL("create table colores(id_prenda INT , color TEXT, primary key(id_prenda,color))");
        db.execSQL("create table usoprenda(id_prenda INT, uso_prenda TEXT, primary key(id_prenda,uso_prenda))");
        db.execSQL("create table todosusos(id_prenda INTEGER primary key AUTOINCREMENT, uso_extra TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists tipoprenda");
        db.execSQL("drop table if exists colores");
        db.execSQL("drop table if exists usoprenda");
        db.execSQL("drop table if exists todosusos");
        onCreate(db);

    }
    public int introducirPrenda(String user, String tipo, String nombrePrenda, ArrayList<String> colores, ArrayList<String> usos) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //añade a tipoprenda
        values.put("tipo_prenda",tipo);
        values.put("nombre_prenda",nombrePrenda);
        values.put("nombre_usuario",user);
        long result = db.insert("tipoprenda",null,values);
        if(result==-1) return -1;
        values.clear();
        int id=(int)result;

        //añade a colores en bucles
        for(String col:colores){
            values.put("id_prenda",id);
            values.put("color",col);
            result = db.insert("colores",null,values);
            if(result==-1) return -1;
            values.clear();
        }
        //añade a usoprenda
        for(String uso:usos){
            values.put("id_prenda",id);
            values.put("uso_prenda",uso);
            result = db.insert("usoprenda",null,values);
            if(result==-1) return -1;
            values.clear();
        }
        return id;
    }


    //Devuelve una lista con todos los id de un tipo de prenda

    public List<Integer> pedirTodasFotosTipo(String tipoPrenda){
        List<Integer> lista= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_prenda from tipoprenda where tipo_prenda = ?", new String[] {tipoPrenda});
        if(cursor.getCount()<=0) return lista;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_prenda");
                int idPrenda = cursor.getInt(posId);
                lista.add(idPrenda);
            } while (cursor.moveToNext());
        }
        return lista;
    }
    public List<Integer> pedirTodasFotosColor(String color){
        List<Integer> lista= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_prenda from colores where color = ?", new String[] {color});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_prenda");
                int idPrenda = cursor.getInt(posId);
                lista.add(idPrenda);
            } while (cursor.moveToNext());
        }
        return lista;
    }
    public List<Integer> pedirTodasFotosUso(String uso){
        List<Integer> lista= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_prenda from usoprenda where uso_prenda = ? ", new String[] {uso});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_prenda");
                int idPrenda = cursor.getInt(posId);
                lista.add(idPrenda);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    /**
     * @return Lista de Prendas que sean de ese tipo y uso, disponibles
     */
    public List<Integer> pedirTodasFotosTipoUso(String tipo, String uso){
        List<Integer> listaTipo=pedirTodasFotosTipo(tipo);
        List<Integer> listaUso=pedirTodasFotosUso(uso);
        List<Integer> res=new ArrayList<>();
        if(listaUso==null || listaTipo==null)
            return res;

        for(int tp: listaTipo){
            if(listaUso.contains(tp) && !preguntaSeEstaLavando(tp)) res.add(tp);
        }
        return res;
    }
    /**
     * @return Lista de Prendas que sean de ese tipo,uso y color, disponibles
     */
    public List<Integer> pedirTodasFotosTipoUsoColores(String tipo, String uso, ArrayList<String> colores){
        List<Integer> listaTipo=pedirTodasFotosTipo(tipo);
        List<Integer> listaUso=pedirTodasFotosUso(uso);
        List<Integer> res=new ArrayList<>();
        boolean tieneColores=false;
        if(listaUso==null || listaTipo==null)
            return res;

        for(int tp: listaTipo){
            for (String color: pedirColoresPrenda(tp)) {
                if(colores.contains(color))tieneColores=true;
            }
            if(listaUso.contains(tp) && !preguntaSeEstaLavando(tp) && tieneColores) res.add(tp);
            tieneColores=false;
        }
        return res;
    }
    public String pedirNamePrenda(String username, int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String res = null;
        Cursor cursor= db.rawQuery("select nombre_prenda from tipoprenda where id_prenda = ? AND nombre_usuario = ?", new String[] {String.valueOf(id), username});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("nombre_prenda");
                res = cursor.getString(posId);
            } while (cursor.moveToNext());
        }
        return res;
    }
    public ArrayList<String> pedirColoresPrenda(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> res = new ArrayList<>();
        Cursor cursor= db.rawQuery("select color from colores where id_prenda = ?", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("color");
                res.add(cursor.getString(posId));
            } while (cursor.moveToNext());
        }
        return res;
    }
    public ArrayList<String> pedirUsoPrenda(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> res = new ArrayList<>();
        Cursor cursor= db.rawQuery("select uso_prenda from usoprenda where id_prenda = ? ", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("uso_prenda");
                res.add(cursor.getString(posId));
            } while (cursor.moveToNext());
        }
        return res;
    }
    //true if se esta lavando
    public boolean preguntaSeEstaLavando( int id){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean res=false;
        Cursor cursor= db.rawQuery("select lavando from tipoprenda where id_prenda = ?", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("lavando");
                int lavando=cursor.getInt(posId);
                res=(lavando!=0);
            } while (cursor.moveToNext());
        }
        return res;
    }
    //devuelve positivo si se pudo cambiar de estado
    //si estaba lavando, deja de estarlo y viceversa
    public boolean cambioLavado(int id){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();

        if(preguntaSeEstaLavando(id))
            values.put("lavando",0);
        else
            values.put("lavando",1);

        int numRowsUpdated = db.update("tipoprenda", values, "id_prenda = ?", new String[]{String.valueOf(id)});
        if (numRowsUpdated > 0) {
            return true;
        } else {
            return false;
        }
    }
    public void editar(int id, String username,String tipo,String nuevo_nombre, ArrayList<String> colores, ArrayList<String> usos){
        SQLiteDatabase db = this.getWritableDatabase();

        //update en tipoprenda
        ContentValues values= new ContentValues();
        values.put("tipo_prenda",tipo);
        values.put("nombre_prenda",nuevo_nombre);
        db.update("tipoprenda", values, "id_prenda = ? AND nombre_usuario = ?", new String[]{String.valueOf(id), username});
        values.clear();

        //update en colores
        db.delete("colores", "id_prenda = ?", new String[]{String.valueOf(id)});
        for(String col:colores){
            values.put("id_prenda",id);
            values.put("color",col);
            db.insert("colores",null,values);
            values.clear();
        }
        //update en uso_prenda
        db.delete("usoprenda", "id_prenda = ? ", new String[]{String.valueOf(id)});
        for(String uso:usos){
            values.put("id_prenda",id);
            values.put("uso_prenda",uso);
            db.insert("usoprenda",null,values);
            values.clear();
        }
    }
    public void eliminar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Eliminar de la tabla tipoprenda
        db.delete("tipoprenda", "id_prenda = ? ", new String[]{String.valueOf(id)});

        // Eliminar de la tabla colores
        db.delete("colores", "id_prenda = ?", new String[]{String.valueOf(id)});

        // Eliminar de la tabla usoprenda
        db.delete("usoprenda", "id_prenda = ?", new String[]{String.valueOf(id)});

    }
    public void vaciar(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists tipoprenda");
        db.execSQL("drop table if exists colores");
        db.execSQL("drop table if exists usoprenda");
        db.execSQL("drop table if exists todosusos");
        onCreate(db);
    }
    public int darId(String nombre_prenda, String username){
        SQLiteDatabase db = this.getReadableDatabase();
        int id=0;
        Cursor cursor= db.rawQuery("select id_prenda from tipoprenda where nombre_prenda = ? AND nombre_usuario = ?", new String[] {nombre_prenda, username});
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_prenda");
                id=cursor.getInt(posId);
            } while (cursor.moveToNext());
        }
        return id;
    }
    //añade un uso extra del que clasificar
    public boolean añadirUsoExtra(String uso_extra){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uso_extra",uso_extra);
        long result = db.insert("todosusos",null,values);
        return (result!=-1);
    }
    //devuelve todos los usos extra
    public ArrayList<String> getUsoExtra(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> lista=new ArrayList<>();
        Cursor cursor= db.rawQuery("select uso_extra from todosusos", new String[] {});
        if(cursor.getCount()<=0) return lista;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("uso_extra");
                lista.add(cursor.getString(posId));
            } while (cursor.moveToNext());
        }
        return lista;
    }
    //devuelve una lista con los ids que tengan ese unico uso (no tengan otros usos)
    public ArrayList<Integer> unicoUso(String item) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list=new ArrayList<>();
        Cursor cursor= db.rawQuery("select id_prenda from usoprenda where uso_prenda = ? ", new String[] {item});
        if(cursor.getCount()<=0) return list;

        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_prenda");
                int id=cursor.getInt(posId);
                Cursor cursor2=db.rawQuery("select * from usoprenda where uso_prenda != ? AND id_prenda = ?", new String[] {item,String.valueOf(id)});
                if(cursor2.getCount()<=0)
                    list.add(id);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public void eliminarUsoExtra(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        //eliminamos el item de la tabla de todos los usos
        db.delete("todosusos", "uso_extra = ? ", new String[]{item});

        //para los items que lo tengan como unico los borramos
        ArrayList<Integer> listaUnicos=unicoUso(item);
        for(int id:listaUnicos){
            eliminar(id);
        }
        //eliminamos todas sus apariciones en usoprenda
        db.delete("usoprenda", "uso_prenda = ? ", new String[]{item});

    }
    public int countItems(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
    public String existeOtroUsuario(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select nombre_usuario from tipoprenda where nombre_usuario != ?", new String[] {username});
        String persona="";
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("nombre_usuario");
                persona=cursor.getString(posId);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return persona;
    }
    //Devuelve una lista con todas las prendas de la bbdd
    public ArrayList<Prenda> todasPrendas(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Prenda> lista=new ArrayList<>();
        Cursor cursor= db.rawQuery("select * from tipoprenda", new String[] {});
        if(cursor.getCount()<=0) return lista;
        if(cursor.moveToFirst()) {
            do {
                int id=cursor.getInt(0);
                String tipo=cursor.getString(1);
                String nombre_prenda= cursor.getString(2);
                int lavando=cursor.getInt(4);
                ArrayList<String> colores=pedirColoresPrenda(id);
                ArrayList<String> usos=pedirUsoPrenda(id);

                lista.add(new Prenda(id,tipo,nombre_prenda,lavando,colores,usos));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
    public int setPrendas(ArrayList<Prenda> prendas,ArrayList<String> extrauso,String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Prenda prenda:prendas) {
            //añade a tipoprenda
            values.put("id_prenda",prenda.getId());
            values.put("tipo_prenda",prenda.getTipo());
            values.put("nombre_prenda",prenda.getNombre_prenda());
            values.put("nombre_usuario",username);
            values.put("lavando",prenda.getLavando());
            long result = db.insert("tipoprenda",null,values);
            if(result==-1) return -1;
            values.clear();
            int id=prenda.getId();

            //añade a colores en bucles
            for(String col:prenda.getColores()){
                values.put("id_prenda",id);
                values.put("color",col);
                result = db.insert("colores",null,values);
                if(result==-1) return -1;
                values.clear();
            }
            //añade a usoprenda
            for(String uso:prenda.getUsos()){
                values.put("id_prenda",id);
                values.put("uso_prenda",uso);
                result = db.insert("usoprenda",null,values);
                if(result==-1) return -1;
                values.clear();
            }
        }
        for(String uso:extrauso){
            añadirUsoExtra(uso);
        }
        return 1;
    }
}


