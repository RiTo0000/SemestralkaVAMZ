package com.example.covidpass20.ui.os_udaje;

import android.os.Bundle;
import android.util.DisplayMetrics;
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

public class OsUdajeFragment extends Fragment {

    private OsUdajeViewModel osUdajeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        osUdajeViewModel =
                new ViewModelProvider(this).get(OsUdajeViewModel.class);
        View root = inflater.inflate(R.layout.os_udaje_fragment, container, false);
        TextView priezviskoVstup = root.findViewById(R.id.priezviskoVstup);
        TextView menoVstup = root.findViewById(R.id.menoVstup);
        TextView rcVstup = root.findViewById(R.id.rodCisloVstup);
        TextView mestoVstup = root.findViewById(R.id.mestoVstup);
        TextView adresaVstup = root.findViewById(R.id.adresaVstup);

        EditText meno = root.findViewById(R.id.meno);
        EditText priezvisko = root.findViewById(R.id.priezvisko);
        EditText rc = root.findViewById(R.id.rodCislo);
        EditText mesto = root.findViewById(R.id.mesto);
        EditText adresa = root.findViewById(R.id.adresa);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int stageWidth = display.getWidth();

        priezviskoVstup.setWidth(stageWidth/2);
        menoVstup.setWidth(stageWidth/2);
        rcVstup.setWidth(stageWidth/2);
        mestoVstup.setWidth(stageWidth/2);
        adresaVstup.setWidth(stageWidth/2);

        meno.setWidth(stageWidth/2);
        priezvisko.setWidth(stageWidth/2);
        rc.setWidth(stageWidth/2);
        mesto.setWidth(stageWidth/2);
        adresa.setWidth(stageWidth/2);
        return root;
    }
}