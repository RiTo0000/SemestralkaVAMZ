package com.example.covidpass20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.covidpass20.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    String DB_FULL_PATH = "CovidPassDatabase.db";
    Helper help;

    public DBHelper(Context context) {

        super(context, "CovidPassDatabase.db", null, 1);
        help = new Helper();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!checkDataBase())
        {
            db.execSQL("create Table UserInfo(meno TEXT, priezvisko TEXT, rodcislo TEXT primary key, mesto TEXT, adresa TEXT)");
            db.execSQL("create Table Testy(id_test INTEGER primary key, datum TEXT, vysledok TEXT, typ TEXT, rodcislo TEXT )");
            db.execSQL("create Table Ockovanie(datum1 TEXT, datum2 TEXT, typ TEXT, pocetDavok INTEGER, rodcislo TEXT primary key, kodCentra TEXT )");
            db.execSQL("create Table Karantena(id_karantena INTEGER primary key, datum TEXT, doba TEXT, rodcislo TEXT )");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists UserInfo");
        db.execSQL("drop Table if exists Testy");
        db.execSQL("drop Table if exists Ockovanie");
        db.execSQL("drop Table if exists Karantena");
    }

    /**
     * metoda na pridanie noveho pouzivatela do databazy
     * @param meno meno pouzivatela
     * @param priezvisko priezvisko pouzivatela
     * @param rodcislo rodne cislo pouzivatela
     * @param mesto mesto bydliska pouzivatela
     * @param adresa adresa bydliska pouzivatela
     * @return vrati true ak bolo pridanie uspesne
     */
    public boolean insertUser(String meno, String priezvisko, String rodcislo, String mesto, String adresa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("meno", meno);
        contentValues.put("priezvisko", priezvisko);
        contentValues.put("rodcislo", rodcislo);
        contentValues.put("mesto", mesto);
        contentValues.put("adresa", adresa);
        long result=db.insert("UserInfo",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * metoda na upravenie uz existujuceho pouzivatela
     * @param meno nove meno pouzivatela
     * @param priezvisko nove priezvisko pouzivatela
     * @param rodcislo rodne cislo uzivatela ktoreho chceme upravit
     * @param mesto nove mesto bydliska uzivatela
     * @param adresa nova adresa bydliska uzivatela
     * @return vrati true ak bolo upravenie uspesne
     */
    public boolean updateUser(String meno, String priezvisko, String rodcislo, String mesto, String adresa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("meno", meno);
        contentValues.put("priezvisko", priezvisko);
        contentValues.put("mesto", mesto);
        contentValues.put("adresa", adresa);
        Cursor cursor = db.rawQuery("select * from UserInfo where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.update("UserInfo",contentValues, "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * metoda ktora vrati kurzor potrebny na pristupenie k datam z databazy
     * @param table nazov tabulky z ktorej chceme citat data
     * @return vrati kurzor na danu tabulku
     */
    public Cursor getData(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table, null);
        return cursor;
    }

    /**
     * metoda na vlozenie noveho testu
     * @param id id testu pre potreby primarneho kluca
     * @param datum datum testu
     * @param vysledok vysledok testu
     * @param typ typ testu
     * @param rodcislo rodne cislo pouzivatela ktoreho test zapisujeme
     * @return vrati true ak bolo vlozenie uspesne
     */
    public boolean insertTest(int id, String datum, String vysledok, String typ, String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_test", id);
        contentValues.put("datum", datum);
        contentValues.put("vysledok", vysledok);
        contentValues.put("typ", typ);
        contentValues.put("rodcislo", rodcislo);
        long result=db.insert("Testy",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * metoda na vlozenie zaznamu o ockovani danej osoby
     * @param datum1 datum prvej davky ockovania
     * @param datum2 datum druhej davky ockovania
     * @param typ typ ockovacej davky
     * @param pocetDavok pocet potrebnych davok
     * @param rodcislo rodne cislo ockovanej osoby
     * @param kodCentra kod ockovacieho centra
     * @return vrati true ak bolo vlozenie uspesne
     */
    public boolean insertOckovanie(String datum1, String datum2, String typ , int pocetDavok, String rodcislo, String kodCentra) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("datum1", datum1);
        contentValues.put("datum2", datum2);
        contentValues.put("typ", typ);
        contentValues.put("pocetDavok", pocetDavok);
        contentValues.put("rodcislo", rodcislo);
        contentValues.put("kodCentra", kodCentra);
        long result=db.insert("Ockovanie",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * metoda ktora sluzi na doplnenie druhej davky ockovania
     * @param datum2 datum druhej davky ockovania
     * @param rodcislo rodne cislo uzivatela ktoreho ockovanie mame doplnit
     * @return vrati true ak doplnenie bolo uspesne
     */
    public boolean updateOckovanie(String datum2, String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("datum2", datum2);
        Cursor cursor = db.rawQuery("select * from Ockovanie where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.update("Ockovanie",contentValues, "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * metoda ktora sluzi na pridanie novej karanteny do databazy
     * @param id id karanteny potrebne pre primarny kluc
     * @param datum datum zaciatku karanteny
     * @param doba doba trvania karanteny
     * @param rodcislo rodne cislo pouzivatela ktory ma byt v karantene
     * @return vrati true ak sa pridanie podarilo
     */
    public boolean insertKarantenu(int id, String datum, String doba, String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_karantena", id);
        contentValues.put("datum", datum);
        contentValues.put("doba", doba);
        contentValues.put("rodcislo", rodcislo);
        long result=db.insert("Karantena",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * metoda ktora sluzi na kontrolu ci dana tabulka uz existuje aby sa nam stale nevytvarala nova
     * @return vrati true ak databaza existuje
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READWRITE);
            checkDB.close();
            System.out.println("databaza existuje");
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            System.out.println("databaza neexistuje");
        }
        System.out.println("databaza neexistuje");
        return checkDB != null;
    }

    /**
     * metoda ktora kontroluje ci uzivatel so zadanym rodnym cislom je v danej tabulke
     * @param rodCislo rodne cislo pouzivatela
     * @param tabulka tabulka v ktorej to mame kontrolovat
     * @param indexRC index miesta na ktorom sa v danej tabulke nachadza rodne cislo
     * @return vrati true ak sa tam nachadza aspon jeden zaznam
     */
    public boolean jeVtabulke(String rodCislo, String tabulka, int indexRC) {
        Cursor res = getData(tabulka);
        if (res.getCount() == 0)
        {
            return false;
        }
        String rc;
        while (res.moveToNext()) {
            rc = res.getString(indexRC);
            if (rc.equals(rodCislo))
                return true;
        }
        return false;
    }


}
