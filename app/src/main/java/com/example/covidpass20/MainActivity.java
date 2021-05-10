package com.example.covidpass20;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    DBHelper db = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_os_udaje, R.id.nav_test, R.id.nav_ockovanie, R.id.nav_kontrola, R.id.nav_karantena)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //notifikacie


        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.HOUR_OF_DAY, 8);
        calendar.set(calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);


        Intent intent = new Intent(this, Primac_notifikacii.class);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        /*alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, pendingIntent);*/
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        createNotificationChannel();
    }


    public void onClickBtnSentInfo(View v)
    {
        EditText menoPole, peizviskoPole, rcPole, mestoPole, adresaPole;
        menoPole = findViewById(R.id.meno);
        peizviskoPole = findViewById(R.id.priezvisko);
        rcPole = findViewById(R.id.rodCislo);
        mestoPole = findViewById(R.id.mesto);
        adresaPole = findViewById(R.id.adresa);
        String meno = menoPole.getText().toString();
        meno = meno.trim();
        String priezvisko = peizviskoPole.getText().toString();
        priezvisko = priezvisko.trim();
        String rodcislo = rcPole.getText().toString();
        rodcislo = rodcislo.trim();
        String mesto = mestoPole.getText().toString();
        mesto = mesto.trim();
        String adresa = adresaPole.getText().toString();
        adresa = adresa.trim();
        if (!rodcislo.equals("") && !meno.equals("") && !priezvisko.equals("") && !mesto.equals("") && !adresa.equals("")) //kontrola ci uzivatel zadal vsetky parametre
        {
            if (db.jeVtabulke(rodcislo, "UserInfo",2)) //kontrola ci taky uzivatel uz neexistuje ak ano robim update ak nie robim insert
            {
                boolean checkUpdate = db.updateUser(meno, priezvisko, rodcislo, mesto, adresa);
                if (checkUpdate)
                {
                    Toast.makeText(MainActivity.this, "Upravil si už existujúceho používateľa", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Neupravil si už existujúceho používateľa", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                boolean checkInsert = db.insertUser(meno, priezvisko, rodcislo, mesto, adresa);
                if (checkInsert)
                {
                    Toast.makeText(MainActivity.this, "Vložil si nového používateľa", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(MainActivity.this, "Nevložil si nového používateľa", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
            Toast.makeText(this, "Všetky údaje musia byť vyplnené", Toast.LENGTH_SHORT).show();
    }


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Moja notifikacia";
            String description = "tu napisem info ";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyMe", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void onClickBtnVypisOsoby(View v)
    {
        Cursor res = db.getData("UserInfo");
        if (res.getCount() == 0)
        {
            Toast.makeText(this, "V databáze nie je žiaden užívateľ", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) { //naplnanie buffera
            buffer.append("Meno : " + res.getString(0) + "\n");
            buffer.append("Priezvisko : " + res.getString(1) + "\n");
            buffer.append("Rod. číslo : " + res.getString(2) + "\n");
            buffer.append("Mesto : " + res.getString(3) + "\n");
            buffer.append("Adresa : " + res.getString(4) + "\n\n");
        }
        //vypis buffera
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Užívatelia");
        builder.setMessage(buffer.toString());
        builder.show();
    }



    public void onClickBtnZapTest(View v)
    {
        EditText datumPole, rcPole;
        Spinner typPole = findViewById(R.id.typ);
        datumPole = findViewById(R.id.datum);
        rcPole = findViewById(R.id.rc);
        String typ = typPole.getSelectedItem().toString();
        String datum = datumPole.getText().toString();
        datum = datum.trim();
        String rodcislo = rcPole.getText().toString();
        rodcislo = rodcislo.trim();
        CheckBox vysledokPole = findViewById(R.id.vysledok);
        String vysledok;
        if (vysledokPole.isChecked())
            vysledok = "Positive";
        else
            vysledok = "Negative";
        if (!rodcislo.equals("")) { //kontrola ci uzivatel zadal rod. cislo
            if (db.jeVtabulke(rodcislo, "UserInfo",2)) { //kontrola ci uzivatel s danym rodnym cislom existuje
                Cursor res = db.getData("Testy");
                if (datum.equals("") || datum.equals("DD/MM/YYYY")) //kontrola spravnosti zadaneho datumu
                {
                    Toast.makeText(this, "Musíš zadať dátum", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum), new Date(), 0)) { //kontrola ci zadany datum nie je neskor ako ten dnesny
                        String poslednyDatum = "01/01/0001";
                        while (res.moveToNext()) {
                            String rcOut = res.getString(4);

                            if (rodcislo.equals(rcOut))
                            {
                                poslednyDatum = res.getString(1); //zapis datumu posledneho testu pre dane rod. cislo
                            }
                        }
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(poslednyDatum), db.help.dateFromString(datum), 0)) //kontrola ci zadany datum nie je mensi ako posledny
                        {
                            boolean checkInsert = db.insertTest(res.getCount() + 1, datum, vysledok, typ, rodcislo);
                            if (checkInsert)
                                Toast.makeText(MainActivity.this, "Test bol uložený", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, "Test nebol uložený", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(this, "Musíš zadať neskorší dátum ako je dátum posledného testu", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this, "Zadaný dátum nesmie byť neskorší ako je ten dnešný", Toast.LENGTH_SHORT).show();

                }
            }
            else {
                Toast.makeText(MainActivity.this, "Užívateľ s daným rod. číslom neexistuje", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnVypTesty(View v)
    {
        EditText rcPole;
        rcPole = findViewById(R.id.rcVypis);
        String rc = rcPole.getText().toString();
        rc = rc.trim();
        if (!rc.equals("")) { //kontrola ci bolo zadane rod. cislo
            Cursor res = db.getData("Testy");
            if (res.getCount() == 0) //kontrola ci tabulka testov nie je prazdna
            {
                Toast.makeText(this, "V tabuľke testy nie je žiaden záznam", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                String rcvypis = res.getString(4);
                if (rc.equals(rcvypis)) { //naplnanie buffera

                    buffer.append("Id_testu : " + res.getString(0) + "\n");
                    buffer.append("Dátum : " + res.getString(1) + "\n");
                    buffer.append("Výsledok : " + res.getString(2) + "\n");
                    buffer.append("Typ testu : " + res.getString(3) + "\n");
                    buffer.append("Rod. číslo : " + rcvypis + "\n\n");
                }
            }
            if (buffer.length() == 0)
                buffer.append("Rod. číslo : " + rc + " nemá zapésané žiadne testy"+ "\n\n");
            // vypis buffera
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Testy");
            builder.setMessage(buffer.toString());
            builder.show();
        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();
    }

    public String getDatum(String rodCislo, int cisDatum) { //metoda na vratenie datumu bud prveho alebo druheho podla vstupnych parametrov
        Cursor res = db.getData("Ockovanie");
        if (res.getCount() == 0)
        {
            return "";
        }
        String rc;
        String datum;
        while (res.moveToNext()) {
            datum = res.getString(cisDatum - 1);
            rc = res.getString(4);
            if (rc.equals(rodCislo))
                return datum;
        }
        return "";
    }

    public void onClickBtnZapOckovanie(View v) {
        EditText datum1Pole, datum2Pole, rcPole, kodOCPole;
        datum1Pole = findViewById(R.id.datum1);
        datum2Pole = findViewById(R.id.datum2);
        Spinner typVakcinyPole = findViewById(R.id.typVakciny);
        rcPole = findViewById(R.id.rcOckovanie);
        kodOCPole = findViewById(R.id.kodOC);
        String datum1 = datum1Pole.getText().toString();
        datum1 = datum1.trim();
        String datum2 = datum2Pole.getText().toString();
        datum2 = datum2.trim();
        String typVakciny = typVakcinyPole.getSelectedItem().toString();
        String rodCislo = rcPole.getText().toString();
        rodCislo = rodCislo.trim();
        String kodOC = kodOCPole.getText().toString();
        kodOC = kodOC.trim();
        CheckBox druhaDavkaPole = findViewById(R.id.druhaDavka);
        if (!rodCislo.equals("")) { //kontrola ci uzivatel zadal rod. cislo
            if (kodOC.equals("")) { //kontrola ci je kod ockovacieho centra vyplneny
                Toast.makeText(MainActivity.this, "Kód očkovacieho centra musí byť vyplnený", Toast.LENGTH_SHORT).show();
                return;
            }
            int pocetDavok;
            if (druhaDavkaPole.isChecked())
            {
                pocetDavok = 2;
            }
            else
            {
                pocetDavok = 1;
                datum2 = datum1;
            }
            if (db.jeVtabulke(rodCislo, "UserInfo",2)) //kontrola ci zadane rod. cislo je ako uzivatel v tabulke userInfo
            {
                if (db.jeVtabulke(rodCislo, "Ockovanie",4)) //kontrola ci uzivatel uz nahodou nebol ockovany
                {
                    if (getDatum(rodCislo,2).equals("")) //kontrola ci osoba s danym rod. cislom uz nahodou nieje zaockovana
                    {
                        if (datum2.equals("") || datum2.equals("DD/MM/YYYY")) //kontrola ci uzivatel vyplnil druhy datum
                            Toast.makeText(MainActivity.this, "Druhý dátum musí byť vyplnený", Toast.LENGTH_SHORT).show();
                        else
                        {
                            if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum2), new Date(), 0)) { //kontrola ci druhy datum nieje neskorsi ako ten dnesny
                                if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(getDatum(rodCislo,1)), db.help.dateFromString(datum2), 0)) //kontrola ci druhy datum je neskor ako ten prvy
                                {
                                    boolean checkUpdate = db.updateOckovanie(datum2, rodCislo);
                                    if (checkUpdate)
                                        Toast.makeText(MainActivity.this, "Doplnil si druhý dátum", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this, "Nedoplnil si druhý dátum", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(this, "Druhý dátum musí byť neskôr ako ten prvý", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(this, "Druhý dátum nesmie byť neskorší ako je ten dnešný", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(MainActivity.this, "Osoba s daným rodným číslom je už zaočkovaná", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (datum1.equals("") || datum1.equals("DD/MM/YYYY")) //kontrola ci uzivatel vyplnil prvy datum
                        Toast.makeText(MainActivity.this, "Prvý dátum musí byť vyplnený", Toast.LENGTH_SHORT).show();
                    else
                    {
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum1), new Date(), 0)) { //kontrola datum1 ci neni vacsi ako dnesny
                            if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum2), new Date(), 0)) { //kontrola datum2 ci neni vacsi ako dnesny
                                if (pocetDavok == 2)
                                {
                                    if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum1), db.help.dateFromString(datum2), 0)) { //kontrola datum1 musi byt mensi ako datum2

                                        boolean checkInsert = db.insertOckovanie(datum1, datum2, typVakciny, pocetDavok, rodCislo, kodOC);
                                        if (checkInsert)
                                            Toast.makeText(MainActivity.this, "Očkovanie bolo zapísané", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(MainActivity.this, "Očkovanie nebolo zapísané", Toast.LENGTH_SHORT).show();
                                    }else
                                        Toast.makeText(this, "Druhý dátum musí byť neskorší ako prvý", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    boolean checkInsert = db.insertOckovanie(datum1, datum2, typVakciny, pocetDavok, rodCislo, kodOC);
                                    if (checkInsert)
                                        Toast.makeText(MainActivity.this, "Očkovanie bolo zapísané", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this, "Očkovanie nebolo zapísané", Toast.LENGTH_SHORT).show();
                                }

                            } else
                                Toast.makeText(this, "Druhý dátum nesmie byť neskorší ako je ten dnešný", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "Prvý dátum nesmie byť neskorší ako je ten dnešný", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(MainActivity.this, "Užívateľ s daným rodným číslom neexistuje", Toast.LENGTH_SHORT).show();
            }

        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();

    }

    public void onClickBtnVypOckovanie(View v)
    {
        EditText rcPole;
        rcPole = findViewById(R.id.rcOckovanieVypis);
        String rc = rcPole.getText().toString();
        rc = rc.trim();
        if (!rc.equals("")) { //kontrola rod. cisla
            Cursor res = db.getData("Ockovanie");
            if (res.getCount() == 0) //kontrola ci v danej tabulke je nejaky zaznam
            {
                Toast.makeText(this, "V tabuľke očkovanie nie je žiaden záznam", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                String rcvypis = res.getString(4);
                if (rc.equals(rcvypis)) { //naplnanie buffera
                    buffer.append("Dátum 1. dávky : " + res.getString(0) + "\n");
                    buffer.append("Dátum 2. dávky : " + res.getString(1) + "\n");
                    buffer.append("Typ očkovacej látky : " + res.getString(2) + "\n");
                    buffer.append("Počet potrebných dávok : " + res.getString(3) + "\n");
                    buffer.append("Rod. číslo : " + rcvypis + "\n");
                    buffer.append("Kód očkovacieho centra : " + res.getString(5) + "\n\n");
                }
            }
            if (buffer.length() == 0)
                buffer.append("Užívateľ s rod. číslom : " + rc + " nie je zaočkovaná"+ "\n\n");
            //vypis buffera
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Očkovanie");
            builder.setMessage(buffer.toString());
            builder.show();

        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();
    }


    public void onClickBtnZacKarantenu(View v) {
        EditText datumZaciatkuPole, dobaTrvaniaPole, rcPole;
        datumZaciatkuPole = findViewById(R.id.datumZaciatku);
        dobaTrvaniaPole = findViewById(R.id.dobaTrvania);
        rcPole = findViewById(R.id.rcKarantena);
        String datumZaciatku = datumZaciatkuPole.getText().toString();
        datumZaciatku = datumZaciatku.trim();
        String dobaTrvania = dobaTrvaniaPole.getText().toString();
        dobaTrvania = dobaTrvania.trim();
        String rodCislo = rcPole.getText().toString();
        rodCislo = rodCislo.trim();
        if (!rodCislo.equals("")) { //kontrola zadaneho rod. cisla
            Cursor res = db.getData("Karantena");
            if (db.jeVtabulke(rodCislo, "UserInfo", 2)) { //kontrola ci uzivatel s danym rod. cislom exituje
                if (db.jeVtabulke(rodCislo, "Karantena", 3)) { //kontrola ci uzivatel s danym rodnym cislom je v tabulke karantena
                    res = db.getData("Karantena");
                    boolean jeVkarantene = false;
                    String poslednyDatum = "";
                    while (res.moveToNext()) { //prechadzanie databazou a zistovanie posledneho datumu karanteny a ci je karantena este platna
                        String rcOut = res.getString(3);

                        if (rodCislo.equals(rcOut)) {
                            jeVkarantene = db.help.nemozeVon(Integer.parseInt(res.getString(0)), "Karantena", Integer.parseInt(res.getString(2)), db);
                            poslednyDatum = res.getString(1);
                        }
                    }
                    if (jeVkarantene)
                        Toast.makeText(MainActivity.this, "Užívateľ s daným rodným číslom už je v karanténe", Toast.LENGTH_SHORT).show();
                    else if (datumZaciatku.equals("") || datumZaciatku.equals("DD/MM/YYYY") || dobaTrvania.equals("")) //kontrola ci uzivatel zadal datum zaciatku aj dobu trvania
                        Toast.makeText(MainActivity.this, "Musíš zadať dátum začiatku aj dobu trvania", Toast.LENGTH_SHORT).show();
                    else {
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datumZaciatku), new Date(), 0)) { //kontrola ci datum zaciatku neni vacsi ako dnesny
                            if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(poslednyDatum), db.help.dateFromString(datumZaciatku), 0)) { //kontrola ci zadany datum nieje mensi ako posledny
                                boolean checkInsert = db.insertKarantenu(res.getCount() + 1, datumZaciatku, dobaTrvania, rodCislo);
                                if (checkInsert)
                                    Toast.makeText(MainActivity.this, "Začal si karanténu", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MainActivity.this, "Nezačal si karanténu", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(this, "Musíš zadať neskorší dátum ako dátum poslednej karantény", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "Musíš zadať dátum ktorý je menší ako dnešný", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (datumZaciatku.equals("") || datumZaciatku.equals("DD/MM/YYYY") || dobaTrvania.equals("")) //kontrola ci uzivatel zadal datum zaciatku aj dobu trvania
                        Toast.makeText(MainActivity.this, "Musíš zadať dátum začiatku aj dobu trvania", Toast.LENGTH_SHORT).show();
                    else {
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datumZaciatku), new Date(), 0)) { //kontrola ci datum zaciatku neni vacsi ako dnesny
                            boolean checkInsert = db.insertKarantenu(res.getCount() + 1, datumZaciatku, dobaTrvania, rodCislo);
                            if (checkInsert)
                                Toast.makeText(MainActivity.this, "Začal si karanténu", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, "Nezačal si karanténu", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "Musíš zadať dátum ktorý je menší ako dnešný", Toast.LENGTH_SHORT).show();

                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "Užívateľ s daným rod. číslom neexistuje", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnKontrolaKaranteny(View v) {
        EditText rcPole;
        rcPole = findViewById(R.id.rcKarantenaKontrola);
        String rc = rcPole.getText().toString();
        rc = rc.trim();
        if (!rc.equals("")) {

            if (db.jeVtabulke(rc, "UserInfo", 2)) {
                Cursor res = db.getData("Karantena");
                if (res.getCount() == 0) {
                    Toast.makeText(this, "V tabuľke karanténa niesu žiadne záznamy", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    String rcvypis = res.getString(3);
                    if (rc.equals(rcvypis)) {
                        buffer.append("id_karanteny : " + res.getString(0) + "\n");
                        buffer.append("Dátum začiatku karantény : " + res.getString(1) + "\n");
                        buffer.append("Doba trvania karantény : " + res.getString(2) + "\n");
                        if (db.help.nemozeVon(Integer.parseInt(res.getString(0)), "Karantena", Integer.parseInt(res.getString(2)), db)) {
                            buffer.append("Stav : !!!stále v karanténe!!!" + "\n");
                        } else {
                            buffer.append("Stav : si voľný ako vták ohnivák" + "\n");
                        }

                        buffer.append("Rod. číslo : " + rcvypis + "\n\n");
                    }
                }
                if (buffer.length() == 0)
                    buffer.append("Rod. číslo : " + rc + " nemá zapísanú žiadnu karanténu" + "\n\n");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Karanténa");
                builder.setMessage(buffer.toString());
                builder.show();
            } else
                Toast.makeText(this, "Užívateľ s daným rodným číslom neexistuje", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();
    }




    public void onClickBtnKontrola(View v) {
        EditText rodCisloVstup, datumVystup, vysledokVystup, stavVystup, menoVystup, priezviskoVystup, ockovanyVystup, typVakcinyVystup;
        rodCisloVstup = findViewById(R.id.rcKontrola);
        datumVystup = findViewById(R.id.datumTestuKontrola);
        vysledokVystup = findViewById(R.id.vysledokTestuKontrola);
        stavVystup = findViewById(R.id.stavKontrola);
        menoVystup = findViewById(R.id.menoKontrola);
        priezviskoVystup = findViewById(R.id.priezviskoKontrola);
        ockovanyVystup = findViewById(R.id.ockovanyKontrola);
        typVakcinyVystup = findViewById(R.id.typVakcinyKontrola);
        String rcKontrola = rodCisloVstup.getText().toString();
        rcKontrola = rcKontrola.trim();
        if (!rcKontrola.equals("")) {
            if (db.jeVtabulke(rcKontrola, "UserInfo", 2)) {
                String datum = "";
                String vysledok = "";
                String id = "";
                Cursor res = db.getData("Testy");
                while (res.moveToNext()) {
                    String rcvypis = res.getString(4);
                    if (rcKontrola.equals(rcvypis)) {
                        id = res.getString(0);
                        datum = res.getString(1);
                        vysledok = res.getString(2);
                    }
                }
                if (vysledok.equals("Positive") && db.help.nemozeVon(Integer.parseInt(id), "Testy", 7, db)) {
                    stavVystup.setText("Chorý");
                } else {
                    if (db.jeVtabulke(rcKontrola, "Karantena", 3)) {
                        res = db.getData("Karantena");
                        boolean jeVkarantene = false;
                        while (res.moveToNext()) {
                            String rcOut = res.getString(3);

                            if (rcKontrola.equals(rcOut))
                                jeVkarantene = db.help.nemozeVon(Integer.parseInt(res.getString(0)), "Karantena", Integer.parseInt(res.getString(2)), db);
                        }
                        if (jeVkarantene)
                            stavVystup.setText("V karanténe");
                        else
                            stavVystup.setText("Zdravý");
                    } else
                        stavVystup.setText("Zdravý");
                }
                datumVystup.setText(datum);
                vysledokVystup.setText(vysledok);

                String ockovany = "";
                String ockovaciaDavka = "";
                if (db.jeVtabulke(rcKontrola, "Ockovanie", 4)) {
                    res = db.getData("Ockovanie");
                    while (res.moveToNext()) {
                        String rcvypis = res.getString(4);
                        if (rcKontrola.equals(rcvypis)) {
                            if (!getDatum(rcKontrola, 2).equals("")) {
                                ockovany = "Áno";
                                ockovaciaDavka = res.getString(2);
                            }
                        }
                    }
                }
                ockovanyVystup.setText(ockovany);
                typVakcinyVystup.setText(ockovaciaDavka);

                String meno = "";
                String priezvisko = "";
                res = db.getData("UserInfo");
                while (res.moveToNext()) {
                    String rcvypis = res.getString(2);
                    if (rcKontrola.equals(rcvypis)) {
                        meno = res.getString(0);
                        priezvisko = res.getString(1);
                    }
                }
                menoVystup.setText(meno);
                priezviskoVystup.setText(priezvisko);


            } else {
                datumVystup.setText("");
                vysledokVystup.setText("");
                stavVystup.setText("");
                menoVystup.setText("");
                priezviskoVystup.setText("");
                ockovanyVystup.setText("");
                typVakcinyVystup.setText("");
                Toast.makeText(this, "Užívateľ s daným rodným číslom neexistuje", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Rod. číslo musí byť vyplnené", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}