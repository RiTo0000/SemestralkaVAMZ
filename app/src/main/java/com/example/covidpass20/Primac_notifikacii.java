package com.example.covidpass20;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

public class Primac_notifikacii extends BroadcastReceiver {

    DBHelper db;
    String notificationText = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DBHelper(context);

        nastavTextNotifikacieStavu();
        notifikaciaStavu(context);
        notifikaciaKaranteny(context);
        notifikaciaTestov(context);

    }

    /**
     * metoda na vypisanie notifikacie o stave vsetkych uzivatelov
     * @param context parameter potrebny pre tvorbu notifikacie
     */
    public void notifikaciaStavu(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyMe")
                .setSmallIcon(R.drawable.ic_nakazeny)
                .setContentTitle("Notifikácia")
                .setContentText("Výpis stavu všetkých užívateľov...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(100, builder.build());
    }

    /**
     * metoda na tvorbu a vypisanie notifikacie o konciacej karantene pre uzivatelov
     * @param context parameter potrebny pre tvorbu notifikacie
     */
    public void notifikaciaKaranteny(Context context) {
        Cursor user = db.getData("UserInfo");
        Cursor karantena = db.getData("Karantena");
        String rodCislo = "";
        String notificationText = "";
        if (user.getCount() != 0) { //kontrola ci aplikacia ma nejakých užívatelov
            if (karantena.getCount() != 0) { //kontrola ci tabulka karantena nie je prazdna
                while (user.moveToNext())
                {
                    rodCislo = user.getString(2);
                    if (db.jeVtabulke(rodCislo, "Karantena", 3)) { //kontrola ci dany uzivatel je v tabulke
                        karantena.moveToLast();
                        do
                        {
                            if (karantena.getString(3).equals(rodCislo)) {
                                if (db.help.porovnajCiSaRovnajuDvaDatumyPoPridaniXdni(db.help.dateFromString(karantena.getString(1)), new Date(), Integer.parseInt(karantena.getString(2)) - 1)) { //kontrola ci danemu pouzivatelovi zajtra konci karantena
                                    notificationText = notificationText + "Pouzivatelovi " + user.getString(0) + " " + user.getString(1) + " zajtra konci karantena \n"; //nastavovanie textu notifikacie
                                }
                                break;
                            }
                        } while (karantena.moveToPrevious());
                    }
                }
            }
        }

        if (!notificationText.equals("")) //podmienka aby sa prazdna notifikacia nevypisovala
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyMe")
                    .setSmallIcon(R.drawable.ic_karantena)
                    .setContentTitle("Karantena")
                    .setContentText("Vypis ludom co zajtra konci karantena...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(notificationText))
                    .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        }


    }

    /**
     * metoda na tvorbu a vypisanie notifikacie o konciacom teste pre uzivatelov
     * @param context parameter potrebny pre tvorbu notifikacie
     */
    public void notifikaciaTestov(Context context) {
        Cursor user = db.getData("UserInfo");
        Cursor testy = db.getData("Testy");
        String rodCislo = "";
        String notificationText = "";
        if (user.getCount() != 0) { //kontrola ci v aplikacii je zaregistrovany nejaky uzivatel
            if (testy.getCount() != 0) {// kontrola ci tabulka testy nie je prazdna
                while (user.moveToNext())
                {
                    rodCislo = user.getString(2);
                    if (db.jeVtabulke(rodCislo, "Testy", 4)) { //kontrola ci dany uzivatel je v tabulke testy
                        testy.moveToLast();
                        do
                        {
                            if (testy.getString(4).equals(rodCislo)) {
                                if (db.help.porovnajCiSaRovnajuDvaDatumyPoPridaniXdni(db.help.dateFromString(testy.getString(1)), new Date(), 6)) { //kontrola ci danemu uzivatelovi zajtra konci test
                                    notificationText = notificationText + "Pouzivatelovi " + user.getString(0) + " " + user.getString(1) + " zajtra konci test \n"; //nastavovanie textu notifikacie
                                }
                                break;
                            }
                        } while (testy.moveToPrevious());
                    }
                }
            }
        }

        if (!notificationText.equals("")) //podmienka aby sa prazdna notifikacia nevypisovala
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyMe")
                    .setSmallIcon(R.drawable.ic_test)
                    .setContentTitle("Testy")
                    .setContentText("Vypis ludom co zajtra konci test...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(notificationText))
                    .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(300, builder.build());
        }


    }

    /**
     * metoda na nastavenie textu notifikacie stavu
     */
    public void nastavTextNotifikacieStavu() {
        Cursor res = db.getData("UserInfo");
        String zapisOsoby = "";
        notificationText = "";
        while (res.moveToNext()) {
            zapisOsoby = res.getString(0) + " " + res.getString(1)
                    + " si zdravy mozes ist kamkolvek"+ "\n";
            if (db.jeVtabulke(res.getString(2),"Karantena", 3))
            {
                Cursor r = db.getData("Karantena");
                r.moveToLast();
                if (res.getString(2).equals(r.getString(3)))
                {
                    if (db.help.nemozeVon(Integer.parseInt(r.getString(0)), "Karantena", 7, db))
                    {
                        zapisOsoby =  res.getString(0) + " " + res.getString(1)
                                + " je v karantene preto musi ostat doma"+ "\n";
                    }
                }
                else
                {
                    while (r.moveToPrevious())
                    {
                        if (res.getString(2).equals(r.getString(3)))
                        {
                            if (db.help.nemozeVon(Integer.parseInt(r.getString(0)), "Karantena", 7, db))
                            {
                                zapisOsoby =  res.getString(0) + " " + res.getString(1)
                                        + " je v karantene preto musi ostat doma"+ "\n";
                            }
                            break;
                        }
                    }
                }
            }
            if (db.jeVtabulke(res.getString(2),"Testy", 4))
            {
                Cursor r = db.getData("Testy");
                r.moveToLast();
                if (res.getString(2).equals(r.getString(4)))
                {
                    if (db.help.nemozeVon(Integer.parseInt(r.getString(0)), "Testy", 7, db))
                    {
                        zapisOsoby = res.getString(0) + " " + res.getString(1)
                                + " je pozitivny preto musi ostat doma"+ "\n";
                    }
                }
                else
                {
                    while (r.moveToPrevious())
                    {
                        if (res.getString(2).equals(r.getString(4)))
                        {
                            if (db.help.nemozeVon(Integer.parseInt(r.getString(0)), "Testy", 7, db))
                            {
                                zapisOsoby = res.getString(0) + " " + res.getString(1)
                                        + " je pozitivny preto musi ostat doma"+ "\n";
                            }
                            break;
                        }
                    }
                }

            }
            notificationText = notificationText + zapisOsoby;
        }
    }


}
