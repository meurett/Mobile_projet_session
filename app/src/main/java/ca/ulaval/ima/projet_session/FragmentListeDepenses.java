package ca.ulaval.ima.projet_session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentListeDepenses extends Fragment {

    private ListView myList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_liste_depenses, container, false);

        final ArrayList<String> depenses = new ArrayList<>();
        depenses.add("{" +
                "\"date\":\"12/02\"," +
                "\"categorie\":\"Essence\"," +
                "\"description\":\"Ceci est une description tres jolie\"," +
                "\"prix\":\"100\"" +
                "}");
        depenses.add("{" +
                "\"date\":\"14/02\"," +
                "\"categorie\":\"Restaurant\"," +
                "\"description\":\"Kepuik !\"," +
                "\"prix\":\"70\"" +
                "}");
        depenses.add("{" +
                "\"date\":\"20/07\"," +
                "\"categorie\":\"Essence\"," +
                "\"description\":\"Ceci est une description tres jolie\"," +
                "\"prix\":\"50\"" +
                "}");

        myList = (ListView) view.findViewById(R.id.liste_depenses);
        myList.setAdapter(new Adapter_Fragment_ListeDepense(getActivity(), depenses));

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String date = "";
                try {
                    date = (new JSONObject(depenses.get(position))).getString("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String categorie = "";
                try {
                    categorie = (new JSONObject(depenses.get(position))).getString("categorie");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String prix = "";
                try {
                    prix = (new JSONObject(depenses.get(position))).getString("prix");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String description = "";
                try {
                    description = (new JSONObject(depenses.get(position))).getString("description");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle args = new Bundle();
                args.putString("description", description);
                args.putString("prix", prix);
                args.putString("categorie", categorie);
                args.putString("date", date);

                FragmentListeDepenses_Item fragmentListeDepenses_Item = new FragmentListeDepenses_Item();
                fragmentListeDepenses_Item.setArguments(args);

                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.root_liste_depenses, fragmentListeDepenses_Item);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        return view;
    }

}