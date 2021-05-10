package com.example.covidpass20.ui.kontrola;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.covidpass20.R;

public class KontrolaFragment extends Fragment {

    private KontrolaViewModel kontrolaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        kontrolaViewModel =
                new ViewModelProvider(this).get(KontrolaViewModel.class);
        View root = inflater.inflate(R.layout.kontrola_fragment, container, false);

        TextView rcKontrolaVstup = root.findViewById(R.id.rcKontrolaVstup);
        TextView stavKontrolaVstup = root.findViewById(R.id.stavKontrolaVstup);
        TextView datumTestuVstup = root.findViewById(R.id.datumTestuKontrolaVstup);
        TextView vysledokVstup = root.findViewById(R.id.vysledokTestuKontrolaVstup);
        TextView ockovanyVstup = root.findViewById(R.id.ockovanyKontrolaVstup);
        TextView typVakcinyVstup = root.findViewById(R.id.typVakcinyKontrolaVstup);
        TextView menoVstup = root.findViewById(R.id.menoKontrolaVstup);
        TextView priesviskoVstup = root.findViewById(R.id.priezviskoKontrolaVstup);

        EditText rcKontrola = root.findViewById(R.id.rcKontrola);
        TextView stavKontrola = root.findViewById(R.id.stavKontrola);
        TextView datumTestu = root.findViewById(R.id.datumTestuKontrola);
        TextView vysledok = root.findViewById(R.id.vysledokTestuKontrola);
        TextView ockovany = root.findViewById(R.id.ockovanyKontrola);
        TextView typVakciny = root.findViewById(R.id.typVakcinyKontrola);
        TextView meno = root.findViewById(R.id.menoKontrola);
        TextView priezvisko = root.findViewById(R.id.priezviskoKontrola);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int stageWidth = display.getWidth();

        rcKontrolaVstup.setWidth(stageWidth/2);
        menoVstup.setWidth(stageWidth/2);
        stavKontrolaVstup.setWidth(stageWidth/2);
        datumTestuVstup.setWidth(stageWidth/2);
        vysledokVstup.setWidth(stageWidth/2);
        ockovanyVstup.setWidth(stageWidth/2);
        typVakcinyVstup.setWidth(stageWidth/2);
        priesviskoVstup.setWidth(stageWidth/2);

        meno.setWidth(stageWidth/2);
        priezvisko.setWidth(stageWidth/2);
        rcKontrola.setWidth(stageWidth/2);
        stavKontrola.setWidth(stageWidth/2);
        datumTestu.setWidth(stageWidth/2);
        vysledok.setWidth(stageWidth/2);
        ockovany.setWidth(stageWidth/2);
        typVakciny.setWidth(stageWidth/2);

        return root;
    }

}