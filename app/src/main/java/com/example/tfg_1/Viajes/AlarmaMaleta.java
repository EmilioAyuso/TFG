package com.example.tfg_1.Viajes;



import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tfg_1.MainActivity;


public class AlarmaMaleta extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        NotificationCompat.Builder builder;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Dress APP")
                    .setContentText("¡Tienes una nueva maleta que hacer!")
                    .setAutoCancel(true)
                    .setColor(-12551169)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
        }
        else{
            builder = new NotificationCompat.Builder(context, "foxandroid")
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Dress APP")
                    .setContentText("¡Tienes una nueva maleta que hacer!")
                    .setAutoCancel(true)
                    .setColor(-12551169)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // No es necesario verificar el permiso POST_NOTIFICATIONS aquí
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(123, builder.build());
    }
}
