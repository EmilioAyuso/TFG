package com.example.tfg_1.Armario;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.tfg_1.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class PrendaAdapter extends BaseAdapter {
    private final Context context;
    private final List<byte[]> fotos;
    public PrendaAdapter(Context context, List<byte[]> fotos){
        this.context=context;
        this.fotos=fotos;
    }
    @Override
    public int getCount() {
        return fotos.size();
    }

    @Override
    public byte[] getItem(int i) {
        return fotos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        View view=convertview;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.lista_fotos, null);

            ViewHolder holder=new ViewHolder();
            holder.imageView=view.findViewById(R.id.imageViewFoto);
            holder.tick=view.findViewById(R.id.tick);
            holder.tick.setVisibility(View.INVISIBLE);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        byte[] bytes=getItem(i);
        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        holder.imageView.setImageBitmap(bitmap);
        return view;
    }

    // ViewHolder pattern to improve performance
    private static class ViewHolder {
        ImageView imageView;
        ImageView tick;
    }
}
