package com.ryanwedoff.senor.naoservercontroller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class RemoteFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the robot name for this
     * fragment.
     */
    OnSendMessageListener mListener;
    private static final String ARG_ROBOT_NAME = "robot_name";
    public RemoteFragment() {
    }

    //TODO change to non deprecated code
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSendMessageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSendMessageListener");
        }
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RemoteFragment newInstance(String robotName) {
        RemoteFragment fragment = new RemoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROBOT_NAME, robotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_remote, container, false);
        //Sets onClick listeners for each fragment, not done in XML to get robot name
        Button standButton = (Button) rootLayout.findViewById(R.id.stand_button);
        standButton.setOnClickListener(this);
        Button crouchButton = (Button) rootLayout.findViewById(R.id.crouch_button);
        crouchButton.setOnClickListener(this);
        Button waveButton = (Button) rootLayout.findViewById(R.id.wave_button);
        waveButton.setOnClickListener(this);
        Button sendTextButton = (Button) rootLayout.findViewById(R.id.sendFragButton);
        sendTextButton.setOnClickListener(this);
        return rootLayout;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = this.getArguments();
        String robotName = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
        switch (v.getId()){
            case R.id.stand_button:
                //Toast.makeText(getActivity(), robotName, Toast.LENGTH_LONG).show();
                // Send message to the host activity(ConrollerActivity)
                mListener.onSendMessage(robotName + "ButtonA;");
                break;
            case R.id.crouch_button:
                mListener.onSendMessage(robotName + "ButtonB;");
                break;
            case R.id.wave_button:
                mListener.onSendMessage(robotName + "ButtonX;");
                break;
            case R.id.sendFragButton:
               /* View rootView = v.getRootView();
                EditText editText = (EditText) rootView.findViewById(R.id.say_text_edit);
                if(editText!= null){
                    String text = editText.getText().toString();
                    String textMinusSemis = text.replace(';',':');
                    mListener.onSendMessage(robotName + textMinusSemis);
                }*/
                //TODO change server parse to handle custom messages, in the mean time, just say 'hello'
                mListener.onSendMessage(robotName + "ButtonY;");
                break;
        }

    }


    public interface OnSendMessageListener {
        void onSendMessage(String message);
    }
}
