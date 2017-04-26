package ca.ulaval.ima.projet_session;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentListeDepenses extends Fragment {

    private ListView myList;
    private ArrayList<String> date;
    private ArrayList<String> prix;
    private ArrayList<String> categorie;
    private ArrayList<String> description;
    private ArrayList<String> resume;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_liste_depenses, container, false);

        date = new ArrayList<>();
        prix = new ArrayList<>();
        categorie = new ArrayList<>();
        description = new ArrayList<>();
        resume = new ArrayList<>();

        populateListView();

        myList = (ListView) mView.findViewById(R.id.liste_depenses);
        myList.setAdapter(new Adapter_Fragment_ListeDepense(getActivity(), resume));

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String dateString = date.get(position);
                String categorieString = categorie.get(position);
                String prixString = prix.get(position);
                String descriptionString = description.get(position);

                Bundle args = new Bundle();
                args.putString("description", descriptionString);
                args.putString("prix", prixString);
                args.putString("categorie", categorieString);
                args.putString("date", dateString);

                FragmentListeDepenses_Item fragmentListeDepenses_Item = new FragmentListeDepenses_Item();
                fragmentListeDepenses_Item.setArguments(args);

                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.root_liste_depenses, fragmentListeDepenses_Item);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        return mView;
    }

    private void populateListView(){
        DatabaseHelper db = ((MainActivity)getActivity()).mDatabaseHelper;
        Cursor data = db.getData();
        Calendar calendar = Calendar.getInstance();
        while(data.moveToNext()){
            String dateString = data.getString(0);
            date.add(dateString);

            String prixString = data.getString(1);
            prix.add(prixString);

            String categorieString = data.getString(2);
            categorie.add(categorieString);

            description.add(data.getString(3));

            calendar.setTimeInMillis(Long.parseLong(dateString));
            String mMonth = getMonthFromInt(calendar.get(Calendar.MONTH));
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mYear = calendar.get(Calendar.YEAR);
            resume.add(mDay + " " + mMonth + " (" + mYear + ") " + " : " + prixString + " $ - ( " + categorieString + " )");
        }
    }

    private String getMonthFromInt(int month){
        String result = "";
        switch(month){
            case 0:
                result = "Janvier";
                break;
            case 1:
                result = "Février";
                break;
            case 2:
                result = "Mars";
                break;
            case 3:
                result = "Avril";
                break;
            case 4:
                result = "Mai";
                break;
            case 5:
                result = "Juin";
                break;
            case 6:
                result = "Juillet";
                break;
            case 7:
                result = "Aout";
                break;
            case 8:
                result = "Septembre";
                break;
            case 9:
                result = "Octobre";
                break;
            case 10:
                result = "Novembre";
                break;
            case 11:
                result = "Décembre";
                break;
        }
        return result;
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getFloatingActionButton().show();
    }
}