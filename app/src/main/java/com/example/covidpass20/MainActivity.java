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
        System.out.println("zacinam");
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
        System.out.println("klikol som sentInfo.....................................");
        EditText menoPole, peizviskoPole, rcPole, mestoPole, adresaPole;
        menoPole = findViewById(R.id.meno);
        peizviskoPole = findViewById(R.id.priezvisko);
        rcPole = findViewById(R.id.rodCislo);
        mestoPole = findViewById(R.id.mesto);
        adresaPole = findViewById(R.id.adresa);
        String meno = menoPole.getText().toString();
        String priezvisko = peizviskoPole.getText().toString();
        String rodcislo = rcPole.getText().toString();
        rodcislo = rodcislo.trim();
        String mesto = mestoPole.getText().toString();
        String adresa = adresaPole.getText().toString();
        if (!rodcislo.equals(""))
        {
            if (db.jeVtabulke(rodcislo, "UserInfo",2))
            {
                boolean checkUpdate = db.updateUser(meno, priezvisko, rodcislo, mesto, adresa);
                if (checkUpdate)
                {
                    Toast.makeText(MainActivity.this, "upravil si uz existujuceho uzivatela", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "neupravil si uz existujuceho uzivatela", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                boolean checkInsert = db.insertUser(meno, priezvisko, rodcislo, mesto, adresa);
                if (checkInsert)
                {
                    Toast.makeText(MainActivity.this, "vlozil si noveho pouzivatela", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(MainActivity.this, "nevlozil si noveho pouzivatela", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
            Toast.makeText(this, "Rod. cislo nemoze byt prazdne", Toast.LENGTH_SHORT).show();
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
        System.out.println("klikol som vypisOsoby.....................................");
        Cursor res = db.getData("UserInfo");
        if (res.getCount() == 0)
        {
            Toast.makeText(this, "nic tam neni", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Meno : " + res.getString(0) + "\n");
            buffer.append("Priezvisko : " + res.getString(1) + "\n");
            buffer.append("Rod. cislo : " + res.getString(2) + "\n");
            buffer.append("Mesto : " + res.getString(3) + "\n");
            buffer.append("Adresa : " + res.getString(4) + "\n\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Uzivatelia");
        builder.setMessage(buffer.toString());
        builder.show();


    }



    public void onClickBtnZapTest(View v)
    {
        System.out.println("klikol som zapTest.....................................");
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
        if (!rodcislo.equals("")) {
            if (db.jeVtabulke(rodcislo, "UserInfo",2)) {
                Cursor res = db.getData("Testy");
                if (datum.equals(""))
                {
                    Toast.makeText(this, "musis zadat datum", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum), new Date(), 0)) {
                        String poslednyDatum = "01/01/0001";
                        while (res.moveToNext()) {
                            String rcOut = res.getString(4);

                            if (rodcislo.equals(rcOut))
                            {
                                poslednyDatum = res.getString(1);
                            }
                        }
                        //kontrola ci zadany datum nieje mensi ako posledny
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(poslednyDatum), db.help.dateFromString(datum), 0))
                        {
                            boolean checkInsert = db.insertTest(res.getCount() + 1, datum, vysledok, typ, rodcislo);
                            if (checkInsert)
                                Toast.makeText(MainActivity.this, "test bol zadany", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, "test nebol zadany", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(this, "Musis zadat neskorsi datum ako datum posledneho testu", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this, "datum nesmie byt neskorsi ako dnesny", Toast.LENGTH_SHORT).show();

                }
            }
            else {
                Toast.makeText(MainActivity.this, "osoba s danym rod cislom neexistuje", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnVypTesty(View v)
    {
        System.out.println("klikol som vypTesty.....................................");
        //tu robim vypis

        EditText rcPole;
        rcPole = findViewById(R.id.rcVypis);
        String rc = rcPole.getText().toString();
        rc = rc.trim();
        if (!rc.equals("")) {
            Cursor res = db.getData("Testy");
            if (res.getCount() == 0)
            {
                Toast.makeText(this, "nic tam neni", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                String rcvypis = res.getString(4);
                if (rc.equals(rcvypis)) {

                    buffer.append("Id_testu : " + res.getString(0) + "\n");
                    buffer.append("Datum : " + res.getString(1) + "\n");
                    buffer.append("Vysledok : " + res.getString(2) + "\n");
                    buffer.append("Typ testu : " + res.getString(3) + "\n");
                    buffer.append("Rod. cislo : " + rcvypis + "\n\n");
                }
            }
            if (buffer.length() == 0)
                buffer.append("Rod. cislo : " + rc + " nema zapisane ziadne testy"+ "\n\n");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Testy");
            builder.setMessage(buffer.toString());
            builder.show();
        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();
    }

    public String getDatum(String rodCislo, int cisDatum) {
        Cursor res = db.getData("Ockovanie");
        if (cisDatum == 1)
        {
            if (res.getCount() == 0)
            {
                return "";
            }
            String rc;
            String datum1;
            while (res.moveToNext()) {
                datum1 = res.getString(0);
                rc = res.getString(4);
                if (rc.equals(rodCislo))
                    return datum1;
            }
            return "";
        }
        else if (cisDatum == 2)
        {
            if (res.getCount() == 0)
            {
                return "";
            }
            String rc;
            String datum2;
            while (res.moveToNext()) {
                datum2 = res.getString(1);
                rc = res.getString(4);
                if (rc.equals(rodCislo))
                    return datum2;
            }
            return "";
        }
        return "";
    }

    public void onClickBtnZapOckovanie(View v) {
        System.out.println("klikol som zapOckovanie.....................................");
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
        if (!rodCislo.equals("")) {

            if (kodOC.equals(""))
            {
                Toast.makeText(MainActivity.this, "nevyplnili ste kod ockovacieho centra", Toast.LENGTH_SHORT).show();
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
            Cursor res = db.getData("Ockovanie");
            if (db.jeVtabulke(rodCislo, "UserInfo",2))
            {
                System.out.println("jeOsoba");
                if (db.jeVtabulke(rodCislo, "Ockovanie",4))
                {
                    System.out.println("jeOckovany");
                    if (getDatum(rodCislo,2).equals(""))
                    {

                        if (datum2.equals(""))
                            Toast.makeText(MainActivity.this, "nezadal si druhy datum", Toast.LENGTH_SHORT).show();
                        else
                        {
                            if (datum1.equals(""))
                                Toast.makeText(MainActivity.this, "nezadal si prvy datum", Toast.LENGTH_SHORT).show();
                            else
                            {
                                if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum2), new Date(), 0)) {
                                    if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(getDatum(rodCislo,1)), db.help.dateFromString(datum2), 0))
                                    {
                                        boolean checkUpdate = db.updateOckovanie(datum2, rodCislo);
                                        if (checkUpdate)
                                            Toast.makeText(MainActivity.this, "doplnil si druhy datum", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(MainActivity.this, "nedoplnil si druhy datum", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(this, "druhy datum musi byt neskor ako prvy", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(this, "druhy datum nesmie byt neskorsi ako je ten dnesny", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                        Toast.makeText(MainActivity.this, "osoba s danym rodnym cislom je uz zaockovana", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (datum1.equals(""))
                        Toast.makeText(MainActivity.this, "nezadal si prvy datum", Toast.LENGTH_SHORT).show();
                    else
                    {
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum1), new Date(), 0)) { //kontrola datum1 ci neni vacsi ako dnesny
                            if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum2), new Date(), 0)) { //kontrola datum2 ci neni vacsi ako dnesny
                                if (pocetDavok == 2)
                                {
                                    if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datum1), db.help.dateFromString(datum2), 0)) { //kontrola datum1 musi byt mensi ako datum2

                                        boolean checkInsert = db.insertOckovanie(datum1, datum2, typVakciny, pocetDavok, rodCislo, kodOC);
                                        if (checkInsert)
                                            Toast.makeText(MainActivity.this, "zapisal si ockovanie", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(MainActivity.this, "nezapisal si ockovanie", Toast.LENGTH_SHORT).show();
                                    }else
                                        Toast.makeText(this, "druhy datum musi byt neskor ako prvy", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    boolean checkInsert = db.insertOckovanie(datum1, datum2, typVakciny, pocetDavok, rodCislo, kodOC);
                                    if (checkInsert)
                                        Toast.makeText(MainActivity.this, "zapisal si ockovanie", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this, "nezapisal si ockovanie", Toast.LENGTH_SHORT).show();
                                }

                            } else
                                Toast.makeText(this, "druhy datum nesmie byt neskorsi ako je ten dnesny", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "prvy datum nesmie byt neskorsi ako dnesny", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(MainActivity.this, "osoba s danym rodnym cislom neexistuje", Toast.LENGTH_SHORT).show();
            }

        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();

    }

    public void onClickBtnVypOckovanie(View v)
    {
        System.out.println("klikol som vypOckovanie.....................................");
        //tu robim vypis

        EditText rcPole;
        rcPole = findViewById(R.id.rcOckovanieVypis);
        String rc = rcPole.getText().toString();
        rc = rc.trim();
        if (!rc.equals("")) {
            Cursor res = db.getData("Ockovanie");
            if (res.getCount() == 0)
            {
                Toast.makeText(this, "nic tam neni", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                String rcvypis = res.getString(4);
                if (rc.equals(rcvypis)) {

                    buffer.append("Datum 1. davky : " + res.getString(0) + "\n");
                    buffer.append("Datum 2. davky : " + res.getString(1) + "\n");
                    buffer.append("Typ ockovacej latky : " + res.getString(2) + "\n");
                    buffer.append("Pocet potrebnych davok : " + res.getString(3) + "\n");
                    buffer.append("Rod. cislo : " + rcvypis + "\n");
                    buffer.append("Kod ockovacieho centra : " + res.getString(5) + "\n\n");
                }
            }
            if (buffer.length() == 0)
                buffer.append("Rod. cislo : " + rc + " nie je zaockovana"+ "\n\n");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Ockovanie");
            builder.setMessage(buffer.toString());
            builder.show();

        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();
    }


    public void onClickBtnZacKarantenu(View v) {
        System.out.println("klikol som zacKarantenu.....................................");
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
        if (!rodCislo.equals("")) {
            Cursor res = db.getData("Karantena");
            if (db.jeVtabulke(rodCislo, "UserInfo", 2)) {
                if (db.jeVtabulke(rodCislo, "Karantena", 3)) {
                    res = db.getData("Karantena");
                    boolean jeVkarantene = false;
                    String poslednyDatum = "";
                    while (res.moveToNext()) {
                        String rcOut = res.getString(3);

                        if (rodCislo.equals(rcOut)) {
                            jeVkarantene = db.help.nemozeVon(Integer.parseInt(res.getString(0)), "Karantena", Integer.parseInt(res.getString(2)), db);
                            poslednyDatum = res.getString(1);
                        }
                    }
                    if (jeVkarantene)
                        Toast.makeText(MainActivity.this, "osoba s danym rod cislom uz je v karantene", Toast.LENGTH_SHORT).show();
                    else if (datumZaciatku.equals("") || dobaTrvania.equals(""))
                        Toast.makeText(MainActivity.this, "musis zadat datum zaciatku aj dobu trvania", Toast.LENGTH_SHORT).show();
                    else {
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datumZaciatku), new Date(), 0)) { //kontrola ci datum zaciatku neni vacsi ako dnesny
                            if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(poslednyDatum), db.help.dateFromString(datumZaciatku), 0)) { //kontrola ci zadany datum nieje mensi ako posledny
                                boolean checkInsert = db.insertKarantenu(res.getCount() + 1, datumZaciatku, dobaTrvania, rodCislo);
                                if (checkInsert)
                                    Toast.makeText(MainActivity.this, "zacal si karantenu", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MainActivity.this, "nezacal si karantenu", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(this, "Musis zadat neskorsi datum ako datum poslednej karanteny", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "musis zadat datum ktory je mensi ako dnesny", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (datumZaciatku.equals("") || dobaTrvania.equals(""))
                        Toast.makeText(MainActivity.this, "musis zadat datum zaciatku aj dobu trvania", Toast.LENGTH_SHORT).show();
                    else {
                        if (db.help.porovnajDatumyPoPridaniXdni(db.help.dateFromString(datumZaciatku), new Date(), 0)) { //kontrola ci datum zaciatku neni vacsi ako dnesny
                            boolean checkInsert = db.insertKarantenu(res.getCount() + 1, datumZaciatku, dobaTrvania, rodCislo);
                            if (checkInsert)
                                Toast.makeText(MainActivity.this, "zacal si karantenu", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, "nezacal si karantenu", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(this, "musis zadat datum ktory je mensi ako dnesny", Toast.LENGTH_SHORT).show();

                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "osoba s danym rod cislom neexistuje", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnKontrolaKaranteny(View v) {
        System.out.println("klikol som kontroluKaranteny.....................................");
        //tu robim vypis

        EditText rcPole;
        rcPole = findViewById(R.id.rcKarantenaKontrola);
        String rc = rcPole.getText().toString();
        rc = rc.trim();
        if (!rc.equals("")) {

            if (db.jeVtabulke(rc, "UserInfo", 2)) {
                Cursor res = db.getData("Karantena");
                if (res.getCount() == 0) {
                    Toast.makeText(this, "v tabulke karantena nie su ziadne udaje", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {

                    String rcvypis = res.getString(3);

                    if (rc.equals(rcvypis)) {
                        System.out.println("som vo while");
                        buffer.append("id_karanteny : " + res.getString(0) + "\n");
                        buffer.append("Datum zaciatku karanteny : " + res.getString(1) + "\n");
                        buffer.append("Doba trvania karanteny : " + res.getString(2) + "\n");
                        if (db.help.nemozeVon(Integer.parseInt(res.getString(0)), "Karantena", Integer.parseInt(res.getString(2)), db)) {
                            buffer.append("Stav : !!!stale v karantene!!!" + "\n");
                        } else {
                            buffer.append("Stav : si volny ako vtak ohnivak" + "\n");
                        }

                        buffer.append("Rod. cislo : " + rcvypis + "\n\n");
                    }
                }
                if (buffer.length() == 0)
                    buffer.append("Rod. cislo : " + rc + " nema zapisany ziadnu karantenu" + "\n\n");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Karantena");
                builder.setMessage(buffer.toString());
                builder.show();
            } else
                Toast.makeText(this, "osoba s danym rodnym cislom neexistuje", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();
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
                    stavVystup.setText("Chory");
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
                            stavVystup.setText("V karantene");
                        else
                            stavVystup.setText("Zdravy");
                    } else
                        stavVystup.setText("Zdravy");
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
                                ockovany = "Ano";
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
                Toast.makeText(this, "uzivatel s danym rodnym cislom neexistuje", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "musis zadat rod. cislo", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}