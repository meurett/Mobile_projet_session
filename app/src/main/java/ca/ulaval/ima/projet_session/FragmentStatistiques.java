package ca.ulaval.ima.projet_session;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentStatistiques extends Fragment {

    private ListView myList;
    private Float montantTotal;
    private View mView;
    private ArrayList<String> resume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_statistiques, container, false);
        myList = (ListView) mView.findViewById(R.id.liste_depenses_par_categorie);
        return mView;
    }

    private void populateListView(){
        DatabaseHelper db = ((MainActivity)getActivity()).mDatabaseHelper;
        Cursor data = db.getSumsByCategorie();
        montantTotal = 0f;
        while(data.moveToNext()){
            resume.add(data.getString(0) + " : " + data.getString(1) + " $");
            montantTotal += Float.parseFloat(data.getString(1));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        resume = new ArrayList<>();
        populateListView();

        myList.setAdapter(new Adapter_Statistiques(getActivity(), resume));

        TextView textView_statistiques_montant_total = (TextView)getView().findViewById(R.id.textView_statistiques_montant_total);
        textView_statistiques_montant_total.setText(montantTotal + " $");
    }
}
