package ca.ulaval.ima.projet_session;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class FragmentGPS_Before_Start extends Fragment
{
    private final Messenger toFragmentMessenger = new Messenger(new FragmentHandler());
    private final ServiceConnection serviceConnection = new ServiceConnection();
    private Button buttonToggleTracking, buttonSave, buttonReset;
    private TextView textDistanceValue;
    private EditText textEditKilometerCost;
    private Messenger toServiceMessenger = null;
    private float distanceTraveled = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gps_before_start, container, false);
        buttonToggleTracking = (Button)view.findViewById(R.id.buttonToggleTracking);
        buttonToggleTracking.setOnClickListener(new onClickButtonToggleTracking());
        buttonReset = (Button)view.findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new onClickButtonReset());
        buttonSave = (Button)view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new onClickButtonSave());
        textDistanceValue = (TextView)view.findViewById(R.id.textViewDistance);
        textEditKilometerCost = (EditText)view.findViewById(R.id.textEditKilometerCost);

        if (ServiceGPS.isRunning()) { bindGPSService(); }

        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            unbindGPSService();
        }
        catch (Throwable t)
        {}
    }

    private void bindGPSService()
    {
        Activity activity = FragmentGPS_Before_Start.this.getActivity();
        activity.startService(new Intent(activity, ServiceGPS.class));
        activity.bindService(new Intent(activity, ServiceGPS.class), serviceConnection, Context.BIND_AUTO_CREATE);
        buttonToggleTracking.setText("Arrêter");
        buttonReset.setEnabled(false);
        buttonSave.setEnabled(false);
    }

    private void unbindGPSService()
    {
        if (toServiceMessenger != null)
        {
            try
            {
                Message message = Message.obtain(null, ServiceGPS.MSG_UNREGISTER_CLIENT);
                message.replyTo = toFragmentMessenger;
                toServiceMessenger.send(message);
            }
            catch (RemoteException e) {}
        }
        FragmentGPS_Before_Start.this.getActivity().unbindService(serviceConnection);
        buttonToggleTracking.setText(distanceTraveled == 0 ? "Démarrer" : "Continuer");
        if (distanceTraveled != 0)
        {
            buttonReset.setEnabled(true);
            buttonSave.setEnabled(true);
        }
    }

    private class onClickButtonToggleTracking implements OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            if (ServiceGPS.isRunning())
            {
                unbindGPSService();
                Activity activity = FragmentGPS_Before_Start.this.getActivity();
                activity.stopService(new Intent(activity, ServiceGPS.class));

            }
            else
            {
                bindGPSService();
            }
        }
    }

    private class onClickButtonReset implements OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            distanceTraveled = 0;
            textDistanceValue.setText("Distance parcourue : " + String.format("%.3f", distanceTraveled/1000) + " km");
        }
    }

    private class onClickButtonSave implements OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.icon_no_image)).getBitmap();
            float kilometerCost = Float.valueOf(textEditKilometerCost.getText().toString());
            String prix = String.format("%.2f", distanceTraveled / 1000 * kilometerCost);
            String description = "";
            String categorie = "Essence";
            Calendar calendar = Calendar.getInstance();
            String date = "" + calendar.getTimeInMillis();
            DatabaseHelper databaseHelper = ((MainActivity)getActivity()).mDatabaseHelper;
            boolean insertData = databaseHelper.addData(date, categorie, prix, description, BitmapHelper.getBytes(bitmap));
            if (insertData)
            {
                Toast.makeText(getActivity(), "Insertion avec succès", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Erreur lors de l'insertion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FragmentHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case ServiceGPS.MSG_DISTANCE_VALUE:
                    distanceTraveled = message.getData().getFloat("distance");
                    textDistanceValue.setText("Distance parcourue : " + String.format("%.3f", distanceTraveled/1000) + " km");
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    private class ServiceConnection implements android.content.ServiceConnection
    {
        public void onServiceConnected(ComponentName className, IBinder serviceIBinder)
        {
            toServiceMessenger = new Messenger(serviceIBinder);
            try
            {
                Message message = Message.obtain(null, ServiceGPS.MSG_REGISTER_CLIENT);
                message.replyTo = toFragmentMessenger;
                toServiceMessenger.send(message);
            }
            catch (RemoteException e) {}
        }

        //Déconnexion inattendue
        public void onServiceDisconnected(ComponentName className)
        {
            toServiceMessenger = null;
        }
    }
}
