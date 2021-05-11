package com.example.covidpass20;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {
    public Helper() {

    }

    /**
     * metoda na zistenie ci dany uzivatel moze opustit svoje bydlisko
     * @param id id riadku v ktorom sa nachadza datum na porovnanie
     * @param tabulka nazov tabulky v databaze
     * @param dobaZakazu pocet dni kolko po datume plati zakaz
     * @param db databaza
     * @return vrati true ak nemoze ist von a false ak moze
     */
    public boolean nemozeVon(int id, String tabulka, int dobaZakazu, DBHelper db) {
        Cursor res = db.getData(tabulka);
        Date datum = new Date();
        while (res.moveToNext()) { //vyhlada potrebny datum na porovnanie
            int idvypis = Integer.parseInt(res.getString(0));
            if (idvypis == id) {
                String datumS = res.getString(1);
                datum = dateFromString(datumS);
                return !porovnajDatumyPoPridaniXdni(datum, new Date(),dobaZakazu); //vrati true ak nemoze ist von lebo je v karantene alebo ma pozitivny test
            }
        }
        return false;
    }

    /**
     * metoda na porovnanie dvoch datumov po pripocitani x dni ku tomu prvemu
     * @param mensiDatum mensi datum ku nemu sa dni pripocitavaju
     * @param vacsiDatum vacsi datum
     * @param x pocet dni na pripocitanie
     * @return vrati true ak vacsi datum je po mensom datume po pripocitani x dni
     */
    public boolean porovnajDatumyPoPridaniXdni(Date mensiDatum, Date vacsiDatum, int x)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(mensiDatum);

        // manipulate date
        c.add(Calendar.DATE, x);

        // convert calendar to date
        Date navysenyDen = c.getTime();

        if (vacsiDatum.after(navysenyDen)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * metoda na porovnanie ci sa rovnaju dva datumy po pripocitani x dni ku tomu prvemu
     * @param datum1 prvy datum, ku nemu sa pripocitavaju dni
     * @param datum2 druhy datum
     * @param x pocet dni na pripocitanie
     * @return vrati true ak sa dane datumy rovnaju po pridani x dni k datum1
     */
    public boolean porovnajCiSaRovnajuDvaDatumyPoPridaniXdni(Date datum1, Date datum2, int x)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(datum1);

        // manipulate date
        cal1.add(Calendar.DATE, x);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(datum2);
        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && //porovnava den vroku a ataktiez aj rok a podla toho vrati vystup
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

        return sameDay;
    }

    /**
     * metoda ktora mi premeni datum v stringu na format Date
     * @param pDatum datum na premenenie do formatu Date
     * @return vrati datum vo formate Date
     */
    public Date dateFromString(String pDatum) {
        Date datum = new Date();
        try {
            datum = new SimpleDateFormat("dd/MM/yyyy").parse(pDatum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datum;
    }
}
