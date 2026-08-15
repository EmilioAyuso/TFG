package com.example.tfg_1;

import android.util.Pair;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseDatosOutfits extends SQLiteOpenHelper {

    public BaseDatosOutfits(@Nullable Context context) {
        super(context, "bdOutfits", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table outfits(id_outfit INTEGER primary key AUTOINCREMENT, nombre Text, id_up INT,id_down INT, id_entero INT, id_calzado INT,id_extra1 INT, id_extra2 INT, id_extra3 INT)");
        db.execSQL("create table colores(id_outfit INT , color TEXT, primary key(id_outfit,color))");
        db.execSQL("create table usoprenda(id_outfit INT, uso_prenda TEXT, primary key(id_outfit,uso_prenda))");

        db.execSQL("create table viajes(id_viaje INTEGER primary key AUTOINCREMENT, nombre_viaje TEXT, localizacion TEXT,fecha_inicio DATE,fecha_fin DATE, fecha_maleta DATE)");
        db.execSQL("create table oufits_viaje(id_viaje INT, id_outfit INTEGER, primary key(id_viaje,id_outfit))");


        db.execSQL("create table calendario_dias(fecha DATE primary key, id_outfit INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
    public int introducirOutfit(String nombre, int idUp, int idDown, int idEntero, int idCalzado, ArrayList<Integer> ids_extra,ArrayList<String> colores, ArrayList<String> usos) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(ids_extra.isEmpty()){
            ids_extra.add(0);
            ids_extra.add(0);
            ids_extra.add(0);
        }
        //añade a outfits
        values.put("nombre",nombre);
        values.put("id_up",idUp);
        values.put("id_down",idDown);
        values.put("id_entero",idEntero);
        values.put("id_calzado",idCalzado);
        values.put("id_extra1",ids_extra.get(0));
        values.put("id_extra2",ids_extra.get(1));
        values.put("id_extra3",ids_extra.get(2));
        long result = db.insert("outfits",null,values);
        if(result==-1) return -1;
        values.clear();
        int id=(int)result;

        //añade a colores en bucles
        for(String col:colores){
            values.put("id_outfit",id);
            values.put("color",col);
            result = db.insert("colores",null,values);
            if(result==-1) return -1;
            values.clear();
        }
        //añade a usoprenda
        for(String uso:usos){
            values.put("id_outfit",id);
            values.put("uso_prenda",uso);
            result = db.insert("usoprenda",null,values);
            if(result==-1) return -1;
            values.clear();
        }
        return id;
    }
    public List<Integer> pedirTodosOutfits(){
        List<Integer> lista= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_outfit from outfits", new String[] {});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_outfit");
                int id = cursor.getInt(posId);
                lista.add(id);
            } while (cursor.moveToNext());
        }
        return lista;
    }
    public List<Integer> pedirTodosOutfitsColor(String color){
        List<Integer> lista= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_outfit from colores where color = ?", new String[] {color});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_outfit");
                int id = cursor.getInt(posId);
                lista.add(id);
            } while (cursor.moveToNext());
        }
        return lista;
    }
    public List<Integer> pedirTodosOutfitsUso(String uso){
        List<Integer> lista= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_outfit from usoprenda where uso_prenda = ? ", new String[] {uso});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_outfit");
                int id = cursor.getInt(posId);
                lista.add(id);
            } while (cursor.moveToNext());
        }
        return lista;
    }
    public ArrayList<String> pedirColoresOutfit(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> res = new ArrayList<>();
        Cursor cursor= db.rawQuery("select color from colores where id_outfit = ?", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("color");
                res.add(cursor.getString(posId));
            } while (cursor.moveToNext());
        }
        return res;
    }
    public ArrayList<String> pedirUsoOutfit(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> res = new ArrayList<>();
        Cursor cursor= db.rawQuery("select uso_prenda from usoprenda where id_outfit = ? ", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("uso_prenda");
                res.add(cursor.getString(posId));
            } while (cursor.moveToNext());
        }
        return res;
    }
    public String pedirNamePrenda(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String res = null;
        Cursor cursor= db.rawQuery("select nombre from outfits where id_outfit = ?", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("nombre");
                res = cursor.getString(posId);
            } while (cursor.moveToNext());
        }
        return res;
    }
    //devuelve una lista con todos las combinaciones que tengan ese id_prenda
    public ArrayList<Integer> combinacionesConID(int id_prenda){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> res = new ArrayList<>();
        Cursor cursor= db.rawQuery("select id_outfit from outfits where id_up = ? or id_down = ? or id_entero = ? or id_calzado = ? or id_extra1 = ? or id_extra2 = ? or id_extra3 = ?", new String[] {String.valueOf(id_prenda),String.valueOf(id_prenda),String.valueOf(id_prenda),String.valueOf(id_prenda),String.valueOf(id_prenda),String.valueOf(id_prenda),String.valueOf(id_prenda)});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_outfit");
                res.add(cursor.getInt(posId));
            } while (cursor.moveToNext());
        }
        return res;
    }
    public ArrayList<Integer> unicoUso(String item) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list=new ArrayList<>();
        Cursor cursor= db.rawQuery("select id_outfit from usoprenda where uso_prenda = ? ", new String[] {item});
        if(cursor.getCount()<=0) return list;

        if(cursor.moveToFirst()) {
            do {
                int posId=cursor.getColumnIndex("id_outfit");
                int id=cursor.getInt(posId);
                Cursor cursor2=db.rawQuery("select * from usoprenda where uso_prenda != ? AND id_outfit = ?", new String[] {item,String.valueOf(id)});
                if(cursor2.getCount()<=0)
                    list.add(id);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    //hace cambios en la disposicion de elementos extra en el outfit
    public void editarOutfit(int id, ArrayList<Integer> ids_extra,ArrayList<String> colores, ArrayList<String> usos){
        SQLiteDatabase db = this.getWritableDatabase();
        if(ids_extra.isEmpty()){
            ids_extra.add(0);
            ids_extra.add(0);
            ids_extra.add(0);
        }
        //update en outfits
        ContentValues values= new ContentValues();
        values.put("id_extra1",ids_extra.get(0));
        values.put("id_extra2",ids_extra.get(1));
        values.put("id_extra3",ids_extra.get(2));
        db.update("outfits", values, "id_outfit = ?", new String[]{String.valueOf(id)});
        values.clear();

        editarCaracteristicasOutfit(id,null,colores,usos);
    }
    //hace cambios en el manejo de etiquetas de color y usos
    public void editarCaracteristicasOutfit(int id, String name, ArrayList<String> colores, ArrayList<String> usos){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();

        if(name!=null){
            values.put("nombre",name);
            db.update("outfits", values, "id_outfit = ?", new String[]{String.valueOf(id)});
            values.clear();
        }

        //update en colores
        db.delete("colores", "id_outfit = ?", new String[]{String.valueOf(id)});
        for(String col:colores){
            values.put("id_outfit",id);
            values.put("color",col);
            db.insert("colores",null,values);
            values.clear();
        }
        //update en uso_prenda
        db.delete("usoprenda", "id_outfit = ? ", new String[]{String.valueOf(id)});
        for(String uso:usos){
            values.put("id_outfit",id);
            values.put("uso_prenda",uso);
            db.insert("usoprenda",null,values);
            values.clear();
        }
    }
    public void eliminarOutfit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Eliminar de la tabla outfits
        db.delete("outfits", "id_outfit = ? ", new String[]{String.valueOf(id)});

        // Eliminar de la tabla colores
        db.delete("colores", "id_outfit = ?", new String[]{String.valueOf(id)});

        // Eliminar de la tabla usoprenda
        db.delete("usoprenda", "id_outfit = ?", new String[]{String.valueOf(id)});

        // Eliminar de la tabla outfits_viaje
        db.delete("oufits_viaje", "id_outfit = ?", new String[]{String.valueOf(id)});

        // Eliminar de la tabla calendario_dias
        db.delete("calendario_dias", "id_outfit = ?", new String[]{String.valueOf(id)});
    }
    public void vaciar(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists outfits");
        db.execSQL("drop table if exists colores");
        db.execSQL("drop table if exists usoprenda");
        db.execSQL("drop table if exists viajes");
        db.execSQL("drop table if exists oufits_viaje");
        db.execSQL("drop table if exists calendario_dias");

        onCreate(db);
    }
    //Devuelve el outfit relacionado con el id, o null si no existe
    public Outfit getOutfit(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Outfit res=null;
        Cursor cursor= db.rawQuery("select * from outfits where id_outfit = ?", new String[] {String.valueOf(id)});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                String name=cursor.getString(1);
                int idUp=cursor.getInt(2);
                int idDown=cursor.getInt(3);
                int idEntero=cursor.getInt(4);
                int idCalzado=cursor.getInt(5);
                ArrayList<Integer> ids_extra=new ArrayList<>();
                ids_extra.add(cursor.getInt(6));
                ids_extra.add(cursor.getInt(7));
                ids_extra.add(cursor.getInt(8));
                ArrayList<String> colores=pedirColoresOutfit(id);
                ArrayList<String> usos=pedirUsoOutfit(id);

                res=new Outfit(id,name,idUp,idDown,idEntero,idCalzado,ids_extra,colores,usos);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    //Devuelve una lista con todas los Outfits de la bbdd
    public ArrayList<Outfit> todasOutfits(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Outfit> lista=new ArrayList<>();
        Cursor cursor= db.rawQuery("select * from outfits", new String[] {});
        if(cursor.getCount()<=0) return lista;
        if(cursor.moveToFirst()) {
            do {
                int id=cursor.getInt(0);
                String name=cursor.getString(1);
                int idUp=cursor.getInt(2);
                int idDown=cursor.getInt(3);
                int idEntero=cursor.getInt(4);
                int idCalzado=cursor.getInt(5);
                ArrayList<Integer> ids_extra=new ArrayList<>();
                ids_extra.add(cursor.getInt(6));
                ids_extra.add(cursor.getInt(7));
                ids_extra.add(cursor.getInt(8));
                ArrayList<String> colores=pedirColoresOutfit(id);
                ArrayList<String> usos=pedirUsoOutfit(id);

                lista.add(new Outfit(id,name,idUp,idDown,idEntero,idCalzado,ids_extra,colores,usos));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
    public int setOutfits(ArrayList<Outfit> outfits){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Outfit outfit: outfits) {
            ArrayList<Integer> aux=outfit.getIdsExtra();
            //añade a outfits
            values.put("nombre",outfit.getName());
            values.put("id_up", outfit.getIdUp());
            values.put("id_down",outfit.getIdDown());
            values.put("id_entero",outfit.getIdEntero());
            values.put("id_calzado",outfit.getIdCalzado());
            values.put("id_extra1",outfit.getIdsExtra().get(0).intValue());
            values.put("id_extra2",outfit.getIdsExtra().get(1).intValue());
            values.put("id_extra3",outfit.getIdsExtra().get(2).intValue());
            long result = db.insert("outfits",null,values);
            if(result==-1) return -1;
            values.clear();
            int id=outfit.getId();

            //añade a colores en bucles
            for(String col:outfit.getColores()){
                values.put("id_outfit",id);
                values.put("color",col);
                result = db.insert("colores",null,values);
                if(result==-1) return -1;
                values.clear();
            }
            //añade a usoprenda
            for(String uso:outfit.getUsos()){
                values.put("id_outfit",id);
                values.put("uso_prenda",uso);
                result = db.insert("usoprenda",null,values);
                if(result==-1) return -1;
                values.clear();
            }
        }
        return 1;
    }

    ///////////////////////////////////PARTE DE VIAJES/////////////////////////////////////////////
    public int introducirViaje(String nombre_viaje, String localizacion, String fecha_inicio, String fecha_fin, String fecha_maleta, ArrayList<Integer> ids_outfit) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //añade a viajes
        values.put("nombre_viaje",nombre_viaje);
        values.put("localizacion",localizacion);
        values.put("fecha_inicio", fecha_inicio);
        values.put("fecha_fin",fecha_fin);
        values.put("fecha_maleta",fecha_maleta);
        //"2023-01-01"

        long result = db.insert("viajes",null,values);
        if(result==-1) return -1;
        values.clear();
        int id=(int)result;

        if(ids_outfit!=null)
            return editarOutfitsViajes(id,ids_outfit);
        return id;
    }
    public int editarOutfitsViajes(int id,ArrayList<Integer> ids_outfit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long result;
        //añade a oufits_viaje en bucle
        for(int id_outfit:ids_outfit){
            values.put("id_viaje",id);
            values.put("id_outfit",id_outfit);
            result = db.insert("oufits_viaje",null,values);
            if(result==-1) return -1;
            values.clear();
        }
        return id;
    }
    public int eliminarOutfitViaje(int id_viaje,int id_outfit){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("oufits_viaje", "id_viaje = ? AND id_outfit= ?", new String[]{String.valueOf(id_viaje),String.valueOf(id_outfit)});
    }
    public void eliminarViaje(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Eliminar de la tabla viajes
        db.delete("viajes", "id_viaje = ? ", new String[]{String.valueOf(id)});

        // Eliminar de la tabla oufits_viaje
        db.delete("oufits_viaje", "id_viaje = ?", new String[]{String.valueOf(id)});
    }
    public Viaje getViaje(int id){
        Viaje viaje=null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1= db.rawQuery("select * from viajes where id_viaje = ? ", new String[] {String.valueOf(id)});
        if(cursor1.getCount()<=0) return null;
        if(cursor1.moveToFirst()) {
            do {
                String nombre_viaje=cursor1.getString(1);
                String localizacion=cursor1.getString(2);
                String fecha_inicio=cursor1.getString(3);
                String fecha_fin=cursor1.getString(4);
                String fecha_maleta=cursor1.getString(5);
                ArrayList<Integer> ids_outfit=new ArrayList<>();

                Cursor cursor2= db.rawQuery("select id_outfit from oufits_viaje where id_viaje = ? ", new String[] {String.valueOf(id)});
                if(cursor2.getCount()>0 &&cursor2.moveToFirst()) {
                    do {
                        ids_outfit.add(cursor2.getInt(0));
                    } while (cursor2.moveToNext());
                }

                viaje=new Viaje(id,nombre_viaje,localizacion,fecha_inicio,fecha_fin,fecha_maleta,ids_outfit);

            } while (cursor1.moveToNext());
        }
        return viaje;
    }
    public ArrayList<Viaje> todosViajes(){
        ArrayList<Viaje> res=new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_viaje from viajes ORDER BY fecha_inicio ASC ", new String[] {});
        if(cursor.getCount()<=0) return null;
        if(cursor.moveToFirst()) {
            do {
                int id=cursor.getInt(0);
                //comprobamos que este tambien en outfits (que no se ha quedado a medias el proceso de introduccion)
                Cursor cursor2= db.rawQuery("select id_outfit from oufits_viaje where id_viaje = ? ", new String[] {String.valueOf(id)});
                if(cursor2.getCount()<=0)
                    db.delete("viajes","id_viaje = ?",new String[]{String.valueOf(id)});
                else
                    res.add(getViaje(id));
            } while (cursor.moveToNext());
        }
        return res;
    }
    public boolean viajesSolopan(String ini, String fin){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from viajes where (fecha_inicio <= ? AND fecha_fin >= ?) or (fecha_inicio <= ? AND fecha_fin >= ?)", new String[] {ini,ini,fin,fin});
        if(cursor.getCount()<=0) return false;
        cursor.close();
        return true;
    }
    public boolean viajeSolapaDia(String ini, String fin){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from calendario_dias where fecha >= ? AND fecha <= ?", new String[] {ini,fin});
        if(cursor.getCount()<=0) return false;
        cursor.close();
        return true;
    }
    public ArrayList<Integer> viajesFinalizados(String hoy){
        ArrayList<Integer> res=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select id_viaje from viajes where fecha_fin < ?", new String[] {hoy});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                int id=cursor.getInt(0);
                res.add(id);
            } while (cursor.moveToNext());
        }
        return res;
    }
    public int setViajes(ArrayList<HashMap> viajes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (HashMap viaje: viajes) {
            //añade a viajes
            int id= Math.toIntExact((Long) viaje.get("id_viaje"));
            values.put("id_viaje",id);
            values.put("nombre_viaje", (String) viaje.get("nombre_viaje"));
            values.put("localizacion",(String) viaje.get("localizacion"));
            values.put("fecha_inicio",(String) viaje.get("fecha_inicio"));
            values.put("fecha_fin",(String) viaje.get("fecha_fin"));
            values.put("fecha_maleta",(String) viaje.get("fecha_maleta"));
            long result = db.insert("viajes",null,values);
            if(result==-1) return -1;
            values.clear();
            //añadimos los outfits relacionados
            ArrayList<Long> outfits= (ArrayList<Long>) viaje.get("ids_outfit");
            ArrayList<Integer> ids_outfit=new ArrayList<>();
            for(Long o:outfits)
                ids_outfit.add(Math.toIntExact(o));
            editarOutfitsViajes(id,ids_outfit);
        }
        return 1;
    }
    ///////////////////////////////////PARTE DE CALENDARIO/////////////////////////////////////////////

    public void addDayOutfit(String fecha, int id_outfit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //añade a viajes
        values.put("fecha",fecha);
        values.put("id_outfit",id_outfit);
        db.insert("calendario_dias",null,values);
    }
    public void removeDayOutfit(String fecha){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("calendario_dias","fecha = ?",new String[] {fecha});
    }
    public int idOutfit(String fecha){
        SQLiteDatabase db = this.getReadableDatabase();
        int id=0;
        Cursor cursor= db.rawQuery("select id_outfit from calendario_dias where fecha = ? ", new String[] {fecha});
        if(cursor.getCount()<=0) return id;
        if(cursor.moveToFirst()) {
            do {
                return cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }
    public int idViaje(String fecha){
        SQLiteDatabase db = this.getReadableDatabase();
        int id=0;
        Cursor cursor= db.rawQuery("select id_viaje from viajes where fecha_fin >= ? AND fecha_inicio <= ? ", new String[] {fecha,fecha});
        if(cursor.getCount()<=0) return id;
        if(cursor.moveToFirst()) {
            do {
                return cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }
    public ArrayList<Integer> outfitsMes(String anio_mes){
        ArrayList<Integer> lista=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String fecha_lim_i=anio_mes+"01";
        String fecha_lim_f=anio_mes+"31";
        Cursor cursor= db.rawQuery("select fecha from calendario_dias where fecha >= ? AND fecha <= ?", new String[] {fecha_lim_i,fecha_lim_f});
        if(cursor.getCount()<=0) return lista;
        if(cursor.moveToFirst()) {
            do {
                LocalDate ld=LocalDate.parse(cursor.getString(0));
                lista.add(ld.getDayOfMonth());
            } while (cursor.moveToNext());
        }
        return lista;
    }

    /**
     * @param anio_mes
     * @return Parejas de intervalos del mes, en las cuales hay un viaje
     */
    public ArrayList<Pair<Integer, Integer>> viajesMes(String anio_mes){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Pair<Integer, Integer>> listDuplas= new ArrayList<>();
        String fecha_lim_i=anio_mes+"01";
        LocalDate inicio_mes = LocalDate.parse(fecha_lim_i);
        String fecha_lim_f=String.valueOf(inicio_mes.withDayOfMonth(inicio_mes.lengthOfMonth()));

        LocalDate fin_mes = LocalDate.parse(fecha_lim_f);

        //si el viaje tiene al menos una fecha de este mes
        Cursor cursor= db.rawQuery("select fecha_inicio,fecha_fin from viajes where (fecha_inicio <= ? AND fecha_fin >= ?)", new String[] {fecha_lim_f,fecha_lim_i});
        if(cursor.getCount()<=0) return listDuplas;
        if(cursor.moveToFirst()) {
            do {
                //Solo añadimos las fechas de ese mes
                LocalDate ld_i = LocalDate.parse(cursor.getString(0));
                LocalDate ld_f = LocalDate.parse(cursor.getString(1));
                LocalDate aux_i;
                LocalDate aux_f;

                /*
                //Cogemos el primer dia de ese mes y paramos en el
                //2 bucles, 1 busca el primer dia y el segundo el ultimo
                boolean encontrado=false;
                //busca primera fecha del mes
                while (!encontrado){
                    //si la posicion del primer puntero pertenece al mes, paramos
                    if(ld_i.isAfter(inicio_mes) || ld_i.isEqual(inicio_mes)){
                        encontrado=true;
                    }
                    else
                        ld_i=ld_i.plusDays(1);
                }

                 */
                //la primera fecha sera ld_i si entra en ese mes o 1 del mes si es menor
                if(ld_i.isAfter(inicio_mes))
                    aux_i=ld_i;
                else
                    aux_i=inicio_mes;
                //la ultima fecha sera ld_f si entra en ese mes o el ultimo dia del mes si es mayor
                if(ld_f.isBefore(fin_mes))
                    aux_f=ld_f;
                else
                    aux_f=aux_i.withDayOfMonth(aux_i.lengthOfMonth());


                listDuplas.add(new Pair<>(aux_i.getDayOfMonth(),aux_f.getDayOfMonth()));
            } while (cursor.moveToNext());
        }
        return listDuplas;
    }
    public ArrayList<DiaOutfit> todosDias(){
        ArrayList<DiaOutfit> res=new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from calendario_dias", new String[] {});
        if(cursor.getCount()<=0) return res;
        if(cursor.moveToFirst()) {
            do {
                String fecha=cursor.getString(0);
                int id=cursor.getInt(1);
                res.add(new DiaOutfit(id,fecha));
            } while (cursor.moveToNext());
        }
        return res;
    }
    public void setDias(ArrayList<HashMap> diasOutfit){
        for (HashMap dias: diasOutfit) {
            addDayOutfit((String) dias.get("fecha"),Math.toIntExact((Long) dias.get("id")));
        }
    }
}
