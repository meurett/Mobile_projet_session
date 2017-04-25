package ca.ulaval.ima.projet_session;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentStatistiques extends Fragment {

    private ArrayList<String> prix;
    private ArrayList<String> categorie;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_statistiques, container, false);

        prix = new ArrayList<>();
        categorie = new ArrayList<>();

        populateListView();

        float montantTotalInt = 0;
        float montantEssenceInt = 0;
        float montantRestaurantInt = 0;

        for (int i = 0; i < prix.size(); i++) {
            String cat = categorie.get(i);
            if (cat.equals("Restaurant")){
                montantRestaurantInt += Float.parseFloat(prix.get(i));
            }
            if (cat.equals("Essence")){
                montantEssenceInt += Float.parseFloat(prix.get(i));
            }
            montantTotalInt += Float.parseFloat(prix.get(i));
        }

        String montantTotal = Float.toString(montantTotalInt);
        String montantEssence = Float.toString(montantEssenceInt);
        String montantRestaurant = Float.toString(montantRestaurantInt);

        TextView textView_statistiques_montant_total = (TextView)view.findViewById(R.id.textView_statistiques_montant_total);
        textView_statistiques_montant_total.setText(montantTotal + " $");

        TextView textView_statistiques_montant_essence = (TextView)view.findViewById(R.id.textView_statistiques_montant_essence);
        textView_statistiques_montant_essence.setText(montantEssence + " $");

        TextView textView_statistiques_montant_restaurant = (TextView)view.findViewById(R.id.textView_statistiques_montant_restaurant);
        textView_statistiques_montant_restaurant.setText(montantRestaurant + " $");

        return view;
    }

    private void populateListView(){
        DatabaseHelper db = ((MainActivity)getActivity()).mDatabaseHelper;
        Cursor data = db.getData();
        while(data.moveToNext()){
            String prixString = data.getString(1);
            prix.add(prixString);

            String categorieString = data.getString(2);
            categorie.add(categorieString);
        }
    }

}
