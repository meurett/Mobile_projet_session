package ca.ulaval.ima.projet_session;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentListeDepenses_Item extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    Button buttonEditImage;
    ImageView imageViewImage;
    Bitmap bitmap;
    private FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_liste_depenses_item, container, false);

        savedInstanceState = getArguments();
        String description = savedInstanceState.getString("description");
        String prix = savedInstanceState.getString("prix");
        String categorie = savedInstanceState.getString("categorie");
        final String date = savedInstanceState.getString("date");

        // ==============================
        //      SET PRECEDENT VALUES
        // ==============================
        final TextView editText_liste_depense_item_description = (TextView) view.findViewById(R.id.editText_liste_depense_item_description);
        editText_liste_depense_item_description.setText(description);

        final TextView editText_liste_depense_item_prix = (TextView) view.findViewById(R.id.editText_liste_depense_item_prix);
        editText_liste_depense_item_prix.setText(prix);

        final Spinner spinnerCategorie = (Spinner) view.findViewById(R.id.spinner_liste_depense_item_categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorie.setAdapter(adapter);

        String[] list = getResources().getStringArray(R.array.categories_array);
        int index = 0;
        for(String item : list) {
            if(item.equals(categorie)){
                break;
            }
            index++;
        }
        spinnerCategorie.setSelection(index);

        // ==============================
        //      SET IMAGE HANDLING
        // ==============================
        imageViewImage = (ImageView) view.findViewById(R.id.imageView_photo_liste_depense_item);
        bitmap = (((MainActivity)getActivity()).mDatabaseHelper).getImage(date);
        if (!(bitmap == null)){
            imageViewImage.setImageBitmap(bitmap);
        }

        buttonEditImage = (Button) view.findViewById(R.id.button_liste_depense_item_ajouter_photo);
        buttonEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        // ==============================
        //        SET CHANGE BUTTON
        // ==============================

        Button buttonEditDepense = (Button) view.findViewById(R.id.button_liste_depense_item_valider_ajout);
        buttonEditDepense.setOnClickListener(
                new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

                          String prix = editText_liste_depense_item_prix.getText().toString();
                          String description = editText_liste_depense_item_description.getText().toString();
                          String categorie = spinnerCategorie.getSelectedItem().toString();

                          if (prix.equals("")){
                              Toast.makeText(getActivity(), "\"PRIX\" doit être non vide !", Toast.LENGTH_SHORT).show();
                          } else {
                              if (!(bitmap == null)){
                                  (((MainActivity)getActivity()).mDatabaseHelper).updateDepense(date, prix, description, categorie, BitmapHelper.getBytes(bitmap));

                              } else {
                                  (((MainActivity)getActivity()).mDatabaseHelper).updateDepenseWithoutImage(date, prix, description, categorie);

                              }
                              Toast.makeText(getActivity(), "Update succed !", Toast.LENGTH_SHORT).show();
                          }
                    }
                }
        );

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.delete_item_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (((MainActivity)getActivity()).mDatabaseHelper).deleteDepense(date);
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageViewImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
