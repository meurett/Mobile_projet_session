package ca.ulaval.ima.projet_session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentStatistiques extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_statistiques, container, false);

        final ArrayList<String> depenses = new ArrayList<>();
        depenses.add("{" +
                "\"date\":\"12/02\"," +
                "\"categorie\":\"Essence\"," +
                "\"prix\":\"100\"" +
                "}");
        depenses.add("{" +
                "\"date\":\"14/02\"," +
                "\"categorie\":\"Restaurant\"," +
                "\"prix\":\"70\"" +
                "}");
        depenses.add("{" +
                "\"date\":\"20/07\"," +
                "\"categorie\":\"Essence\"," +
                "\"prix\":\"50\"" +
                "}");

        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> categorie = new ArrayList<>();
        ArrayList<String> prix = new ArrayList<>();


        for (int i = 0; i < depenses.size(); i++) {
            try {
                date.add((new JSONObject(depenses.get(i))).getString("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                categorie.add((new JSONObject(depenses.get(i))).getString("categorie"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                prix.add((new JSONObject(depenses.get(i))).getString("prix"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Integer montantTotalInt = 0;
        Integer montantEssenceInt = 0;
        Integer montantRestaurantInt = 0;

        for (int i = 0; i < depenses.size(); i++) {
            String cat = categorie.get(i);
            if (cat.equals("Restaurant")){
                montantRestaurantInt += Integer.parseInt(prix.get(i));
            }
            if (cat.equals("Essence")){
                montantEssenceInt += Integer.parseInt(prix.get(i));
            }
            montantTotalInt += Integer.parseInt(prix.get(i));
        }

        String montantTotal = montantTotalInt.toString();
        String montantEssence = montantEssenceInt.toString();
        String montantRestaurant = montantRestaurantInt.toString();

        TextView textView_statistiques_montant_total = (TextView)view.findViewById(R.id.textView_statistiques_montant_total);
        textView_statistiques_montant_total.setText(montantTotal + " $");

        TextView textView_statistiques_montant_essence = (TextView)view.findViewById(R.id.textView_statistiques_montant_essence);
        textView_statistiques_montant_essence.setText(montantEssence + " $");

        TextView textView_statistiques_montant_restaurant = (TextView)view.findViewById(R.id.textView_statistiques_montant_restaurant);
        textView_statistiques_montant_restaurant.setText(montantRestaurant + " $");

        return view;
    }

}
