package com.ryanwedoff.senor.naoservercontroller;
/**
 * RemoteFragment is the Parent Fragment used in ControllerActivity.
 * It holds the wrapper remote control for each robot in the view pager.
 * RemoteFragment also contains WalkFragment and JoyStickFrag
 *
 * @see com.ryanwedoff.senor.naoservercontroller.WalkFragment
 * @see com.ryanwedoff.senor.naoservercontroller.JoyStickFrag
 * @see com.ryanwedoff.senor.naoservercontroller.ControllerActivity
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


public class RemoteFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_ROBOT_NAME = "robot_name";
    /**
     * The fragment argument representing the robot name for this
     * fragment.
     */
    private OnSendMessageListener mListener;
    private EditText editText;
    private TextView textView;
    private TextView sentTextView;
    public RemoteFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given robot name
     */
    public static RemoteFragment newInstance(String robotName) {
        RemoteFragment fragment = new RemoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROBOT_NAME, robotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnSendMessageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSendMessageListener");
        }
    }

    /**
     * Many event listeners for the fragment are coded here.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Fragment thisFrag = this;
        FragmentManager controlFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = controlFragMan.beginTransaction();
        Bundle bundle = thisFrag.getArguments();
        JoyStickFrag joyStickFrag = JoyStickFrag.newInstance(bundle.getString(ARG_ROBOT_NAME, "Robot Name Error"));
        childFragTrans.add(R.id.remote_fragment_container, joyStickFrag);
        childFragTrans.addToBackStack(null);
        childFragTrans.commit();

        final LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_remote, container, false);

        //Sets onClick listeners for each fragment, not done in XML to get robot name
        final Button standButton = (Button) rootLayout.findViewById(R.id.stand_button);
        standButton.setOnClickListener(this);
        Button crouchButton = (Button) rootLayout.findViewById(R.id.crouch_button);
        crouchButton.setOnClickListener(this);
        Button waveButton = (Button) rootLayout.findViewById(R.id.wave_button);
        waveButton.setOnClickListener(this);
        Button sendTextButton = (Button) rootLayout.findViewById(R.id.send_text_button);
        sendTextButton.setOnClickListener(this);
        Button sitButton = (Button) rootLayout.findViewById(R.id.sit_button);
        sitButton.setOnClickListener(this);
        editText = (EditText) rootLayout.findViewById(R.id.say_text_edit);
        textView = (TextView) rootLayout.findViewById(R.id.prev_sent_text_view);
        sentTextView = (TextView) rootLayout.findViewById(R.id.sendMessageText);
        Switch switchButton = (Switch) rootLayout.findViewById(R.id.head_walk_toggle);
        final TextView headWalkTextview = (TextView) rootLayout.findViewById(R.id.head_walk_text_view);
        headWalkTextview.setText(R.string.head_control);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    headWalkTextview.setText(R.string.walk_control);
                    FragmentManager controlFragMan = getChildFragmentManager();
                    FragmentTransaction childFragTrans = controlFragMan.beginTransaction();
                    Bundle bundle = thisFrag.getArguments();
                    WalkFragment walkFragment = WalkFragment.newInstance(bundle.getString(ARG_ROBOT_NAME, "Robot Name Error"));
                    childFragTrans.replace(R.id.remote_fragment_container, walkFragment);
                    childFragTrans.addToBackStack(null);
                    childFragTrans.commit();

                } else {
                    headWalkTextview.setText(R.string.head_control);
                    FragmentManager controlFragMan = getChildFragmentManager();
                    FragmentTransaction childFragTrans = controlFragMan.beginTransaction();
                    Bundle bundle = thisFrag.getArguments();
                    JoyStickFrag joyStickFrag = JoyStickFrag.newInstance(bundle.getString(ARG_ROBOT_NAME, "Robot Name Error"));
                    childFragTrans.replace(R.id.remote_fragment_container, joyStickFrag);
                    childFragTrans.addToBackStack(null);
                    childFragTrans.commit();
                }
            }
        });

        return rootLayout;
    }

    /**
     * General on click listener for all buttons and interactions.
     */
    @Override
    public void onClick(View v) {
        Bundle bundle = this.getArguments();
        String robotName = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
        switch (v.getId()){
            case R.id.stand_button:
                //Toast.makeText(getActivity(), robotName, Toast.LENGTH_LONG).show();
                // Send message to the host activity(ConrollerActivity)
                mListener.onSendMessage(setSentText(robotName + "StandUp;"));
                break;
            case R.id.crouch_button:
                mListener.onSendMessage(setSentText(robotName + "Crouch;"));
                break;
            case R.id.wave_button:
                mListener.onSendMessage(setSentText(robotName + "Wave;"));
                break;
            case R.id.send_text_button:
                if(editText!= null){
                    String text = editText.getText().toString();
                    String textMinusSemis = text.replace(';',':');
                    mListener.onSendMessage(setSentText(robotName + "Speech;" + textMinusSemis + ";"));
                    editText.setText("");
                    String prevMessage = robotName.substring(0,robotName.length()-1) + " said: " + textMinusSemis;
                    textView.setText(prevMessage);
                }
                break;
            case R.id.sit_button:
                mListener.onSendMessage(setSentText(robotName + "SitDown;"));
                break;
        }
    }

    private String setSentText(String message) {
        sentTextView.setText(message);
        return message;
    }

    public interface OnSendMessageListener {
        void onSendMessage(String message);
    }
}
