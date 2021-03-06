package ca.ulaval.ima.projet_session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentGDrive extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gdrive, container, false);
        Button restore = (Button) view.findViewById(R.id.restore_data_from_api);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityGConnectRestore.class);
                (getActivity()).startActivity(intent);
            }
        });
        return view;
    }

}