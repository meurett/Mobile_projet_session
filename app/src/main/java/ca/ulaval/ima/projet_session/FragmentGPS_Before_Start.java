package ca.ulaval.ima.projet_session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentGPS_Before_Start extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gps_before_start, container, false);
        Button button_start_GPS = (Button) view.findViewById(R.id.button_start_GPS);
        button_start_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityGPS.class);
                (getActivity()).startActivity(intent);
            }
        });
        return view;
    }

}
