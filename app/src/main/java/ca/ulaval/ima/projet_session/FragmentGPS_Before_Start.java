package ca.ulaval.ima.projet_session;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class FragmentGPS_Before_Start extends Fragment
{
    Button btnStart, btnStop, btnBind, btnUnbind, btnUpby1, btnUpby10;
    TextView textStatus, textIntValue, textStrValue;
    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ServiceGPS.MSG_SET_INT_VALUE:
                    textIntValue.setText("Int Message: " + msg.arg1);
                    break;
                case ServiceGPS.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
                    textStrValue.setText("Str Message: " + str1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            mService = new Messenger(service);
            textStatus.setText("Attached.");
            try
            {
                Message msg = Message.obtain(null, ServiceGPS.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e)
            {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className)
        {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            textStatus.setText("Disconnected.");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gps_before_start, container, false);
        btnStart = (Button)view.findViewById(R.id.btnStart);
        btnStop = (Button)view.findViewById(R.id.btnStop);
        textStatus = (TextView)view.findViewById(R.id.textStatus);
        textIntValue = (TextView)view.findViewById(R.id.textIntValue);
        textStrValue = (TextView)view.findViewById(R.id.textStrValue);
        btnUpby1 = (Button)view.findViewById(R.id.btnUpby1);
        btnUpby10 = (Button)view.findViewById(R.id.btnUpby10);

        btnStart.setOnClickListener(btnStartListener);
        btnStop.setOnClickListener(btnStopListener);
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

    private OnClickListener btnStartListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            FragmentGPS_Before_Start.this.getActivity().startService(new Intent(FragmentGPS_Before_Start.this.getActivity(), ServiceGPS.class));
            doBindService();
        }
    };

    private OnClickListener btnStopListener = new OnClickListener()
    {
        public void onClick(View v){
            doUnbindService();
            FragmentGPS_Before_Start.this.getActivity().stopService(new Intent(FragmentGPS_Before_Start.this.getActivity(), ServiceGPS.class));
        }
    };

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
        if (mIsBound)
        {
            if (mService != null)
            {
                try
                {
                    Message msg = Message.obtain(null, ServiceGPS.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {}
            }
        }
    }

    void doBindService()
    {
        FragmentGPS_Before_Start.this.getActivity().bindService(new Intent(FragmentGPS_Before_Start.this.getActivity(), ServiceGPS.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        textStatus.setText("Binding.");
    }

    void doUnbindService()
    {
        if (mIsBound)
        {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null)
            {
                try {
                    Message msg = Message.obtain(null, ServiceGPS.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e)
                {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            FragmentGPS_Before_Start.this.getActivity().unbindService(mConnection);
            mIsBound = false;
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
