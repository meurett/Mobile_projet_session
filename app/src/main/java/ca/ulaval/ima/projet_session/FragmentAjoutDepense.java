package ca.ulaval.ima.projet_session;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class FragmentAjoutDepense extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    Button buttonAddImage;
    ImageView imageViewImage;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ajout_depense, container, false);

        final EditText editText_ajout_depense_prix = (EditText) view.findViewById(R.id.editText_ajout_depense_prix);
        final EditText editText_ajout_depense_description = (EditText) view.findViewById(R.id.editText_ajout_depense_description);

        final Spinner spinnerCategorie = (Spinner) view.findViewById(R.id.spinner_ajout_depense_categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorie.setAdapter(adapter);

        imageViewImage = (ImageView) view.findViewById(R.id.imageView_photo_depense);

        buttonAddImage = (Button) view.findViewById(R.id.button_ajout_depense_ajouter_photo);

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        EditText EtOne = (EditText) view.findViewById(R.id.editText_ajout_depense_description);
        EtOne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editText_ajout_depense_description) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        Button buttonAjoutDepense = (Button) view.findViewById(R.id.button_ajout_depense_valider_ajout);
        buttonAjoutDepense.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  String prix = editText_ajout_depense_prix.getText().toString();
                  String description = editText_ajout_depense_description.getText().toString();
                  String categorie = spinnerCategorie.getSelectedItem().toString();
                  Calendar c = Calendar.getInstance();
                  String date = "" + c.getTimeInMillis();
                  if (prix.equals("")){
                      Toast.makeText(getActivity(), "\"PRIX\" doit être non vide !", Toast.LENGTH_SHORT).show();
                  } else {
                      DatabaseHelper db = ((MainActivity)getActivity()).mDatabaseHelper;
                      boolean insertData;
                      if (!(bitmap == null)){
                          insertData = db.addData(date, categorie, prix, description, BitmapHelper.getBytes(bitmap));
                      } else {
                          insertData = db.addDataWithoutImage(date, categorie, prix, description);
                      }
                      if (insertData){
                          Toast.makeText(getActivity(), "Insertion avec succès !", Toast.LENGTH_SHORT).show();
                      } else {
                          Toast.makeText(getActivity(), "ERREUR ! :(", Toast.LENGTH_SHORT).show();
                      }
                  }
              }
          }
        );

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

}