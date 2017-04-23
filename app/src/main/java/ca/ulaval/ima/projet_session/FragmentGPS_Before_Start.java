package ca.ulaval.ima.projet_session;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

public class FragmentGPS_Before_Start extends Fragment
{
    Button btnUpby1, btnUpby10;
    TextView textStatus, textIntValue, textStrValue, textDistanceValue;
    Messenger toServiceMessenger = null;
    boolean isBound;
    final Messenger toFragmentMessenger = new Messenger(new FragmentHandler());

    private class FragmentHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case ServiceGPS.MSG_SET_INT_VALUE:
                    textIntValue.setText("Int Message: " + message.arg1);
                    break;
                case ServiceGPS.MSG_SET_STRING_VALUE:
                    String str1 = message.getData().getString("str1");
                    textStrValue.setText("Str Message: " + str1);
                    break;
                case ServiceGPS.MSG_SET_DISTANCE_VALUE:
                    float distance = message.getData().getFloat("distance");
                    textDistanceValue.setText(Float.toString(distance));
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder serviceIBinder)
        {
            toServiceMessenger = new Messenger(serviceIBinder);
            textStatus.setText("Attached.");
            try
            {
                Message message = Message.obtain(null, ServiceGPS.MSG_REGISTER_CLIENT);
                message.replyTo = toFragmentMessenger;
                toServiceMessenger.send(message);
            }
            catch (RemoteException e) {}
        }

        //Unexpected disconnection
        public void onServiceDisconnected(ComponentName className)
        {
            toServiceMessenger = null;
            textStatus.setText("Disconnected.");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gps_before_start, container, false);
        ((Button)view.findViewById(R.id.btnStart)).setOnClickListener(new onClickBtnStart());
        ((Button)view.findViewById(R.id.btnStop)).setOnClickListener(new onClickBtnStop());
        textStatus = (TextView)view.findViewById(R.id.textStatus);
        textIntValue = (TextView)view.findViewById(R.id.textIntValue);
        textStrValue = (TextView)view.findViewById(R.id.textStrValue);
        textDistanceValue = (TextView)view.findViewById(R.id.textDistance);
        btnUpby1 = (Button)view.findViewById(R.id.btnUpby1);
        btnUpby10 = (Button)view.findViewById(R.id.btnUpby10);

        btnUpby1.setOnClickListener(btnUpby1Listener);
        btnUpby10.setOnClickListener(btnUpby10Listener);

        restoreMe(savedInstanceState);

        if (ServiceGPS.isRunning()) { doBindService(); }

        return view;
    }

    private void restoreMe(Bundle state)
    {
        if (state!=null)
        {
            textStatus.setText(state.getString("textStatus"));
            textIntValue.setText(state.getString("textIntValue"));
            textStrValue.setText(state.getString("textStrValue"));
        }
    }

    private class onClickBtnStart implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Activity activity = FragmentGPS_Before_Start.this.getActivity();
            activity.startService(new Intent(activity, ServiceGPS.class));
            doBindService();
        }
    }

    private class onClickBtnStop implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            doUnbindService();
            Activity activity = FragmentGPS_Before_Start.this.getActivity();
            activity.stopService(new Intent(activity, ServiceGPS.class));
        }
    }

    private OnClickListener btnUpby1Listener = new OnClickListener()
    {
        public void onClick(View v)
        {
            sendMessageToService(1);
        }
    };

    private OnClickListener btnUpby10Listener = new OnClickListener()
    {
        public void onClick(View v){
            sendMessageToService(10);
        }
    };

    private void sendMessageToService(int intvaluetosend)
    {
        if (isBound)
        {
            if (toServiceMessenger != null)
            {
                try
                {
                    Message msg = Message.obtain(null, ServiceGPS.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    msg.replyTo = toFragmentMessenger;
                    toServiceMessenger.send(msg);
                }
                catch (RemoteException e) {}
            }
        }
    }

    void doBindService()
    {
        Activity activity = FragmentGPS_Before_Start.this.getActivity();
        activity.bindService(new Intent(activity, ServiceGPS.class), serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
        textStatus.setText("Binding.");
    }

    void doUnbindService()
    {
        if (isBound)
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
            isBound = false;
            textStatus.setText("Unbinding.");
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            doUnbindService();
        }
        catch (Throwable t)
        {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }
}
