package com.ryanwedoff.senor.naoservercontroller;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;



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
    private boolean headWalkToggle; //True = walk-mode, false = head-mode
    private int oldAngle = 0;

    public RemoteFragment() {
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
        Switch switchButton = (Switch) rootLayout.findViewById(R.id.head_walk_toggle);
        final TextView headWalkTextview = (TextView) rootLayout.findViewById(R.id.head_walk_text_view);
        final TextView downTextView = (TextView) rootLayout.findViewById(R.id.down_info_text_view);
        final TextView upTextView = (TextView) rootLayout.findViewById(R.id.up_info_text_view);
        headWalkTextview.setText(R.string.head_control);
        final Button stopButton = (Button) rootLayout.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    headWalkTextview.setText(R.string.walk_control);
                    upTextView.setText(R.string.Forward);
                    downTextView.setText(R.string.Backward);
                    headWalkToggle = true;
                    stopButton.setText(R.string.Stop);
                } else {
                    headWalkTextview.setText(R.string.head_control);
                    upTextView.setText(R.string.Up);
                    downTextView.setText(R.string.Down);
                    headWalkToggle = false;
                    stopButton.setText(R.string.Center2);
                }
            }
        });


        //JOYSTICK CODE TEST
//        angleTextView = (TextView) findViewById(R.id.angleTextView);
//        powerTextView = (TextView) findViewById(R.id.powerTextView);
//        directionTextView = (TextView) findViewById(R.id.directionTextView);
        //Referencing also other views
        EditedJoyStickView joystick = (EditedJoyStickView) rootLayout.findViewById(R.id.joystickView);
        final Fragment thisFrag = this;
        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new EditedJoyStickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int newAngle, int newPower, int direction) {
                Bundle bundle = thisFrag.getArguments();
                String robotName = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                //angleTextView.setText(" " + String.valueOf(angle) + "°");
                //powerTextView.setText(" " + String.valueOf(power) + "%");
                Log.i("Angle (Degrees): ",String.valueOf(newAngle));
                Log.i("Power (%): ",String.valueOf(newPower));


               if(Math.abs(newAngle - oldAngle) >= 0.05){ //Int values are used, probably never > 0.05

                    //NOTES:  RIGHT means the right stick, which is the head, the units are in radians, I need to figure out the unit circle and
                    //how it relates to my joystick circle
                    if(newPower != 0){
                        double radians = Math.toRadians(newAngle);
                        if(headWalkToggle){
                            //Angles not exact to have a margin of error with walking
                            if((newAngle > 45 && newAngle < 135) || newAngle == 90){
                                Log.i("Right", Double.toString(Math.cos(radians)));
                                mListener.onSendMessage(robotName + "Theta=-1;");
                            }
                            if((newAngle > -135 && newAngle < -45) || newAngle == -90) {
                                Log.i("Left", Double.toString(radians));
                                mListener.onSendMessage(robotName + "Theta=1;");
                            }
                            if((newAngle > -44 && newAngle < 44) || newAngle == 0){
                                Log.i("Up", Double.toString(radians));
                                mListener.onSendMessage(robotName + "LeftY=" + Math.cos(radians) + ";");
                            }
                            if((newAngle > 135 || newAngle < -135) || newAngle == 180) {
                                Log.i("Down", Double.toString(radians));
                                mListener.onSendMessage(robotName + "LeftY=" + Math.cos(radians) + ";");
                            }

                        }else{
                                double normalizedPowerX = (newPower * 2.5) /100;
                                mListener.onSendMessage(robotName + "RightX=" + Math.sin(-radians)*normalizedPowerX + ";");
                                mListener.onSendMessage(robotName + "RightY=" + -Math.cos(radians) + ";");
                        }

                    } else{
                        if(headWalkToggle){
                            Log.i("Stop","Stop");
                            mListener.onSendMessage(robotName + "Theta=0;");
                            mListener.onSendMessage(robotName + "LeftY=0;");
                        }
                    }
                    oldAngle = newAngle;
                }
            }
        }, EditedJoyStickView.DEFAULT_LOOP_INTERVAL);

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
            case R.id.send_text_button:
               View rootView = v.getRootView();
                EditText editText = (EditText) rootView.findViewById(R.id.say_text_edit);
                if(editText!= null){
                    String text = editText.getText().toString();
                    String textMinusSemis = text.replace(';',':');
                    mListener.onSendMessage(robotName + "Speech;" + textMinusSemis + ";");
                    editText.setText("");
                    TextView textView = (TextView) rootView.findViewById(R.id.prev_sent_text_view);
                    String prevMessage = robotName.substring(0,robotName.length()-1) + " said: " + textMinusSemis;
                    textView.setText(prevMessage);
                }
                break;
            case R.id.stop_button:
                if(headWalkToggle){
                    mListener.onSendMessage(robotName + "Theta=0;");
                    mListener.onSendMessage(robotName + "LeftY=0;");
                } else{
                    mListener.onSendMessage(robotName + "RightX=0;");
                    mListener.onSendMessage(robotName + "RightY=0;");
                }
                break;
        }
    }

    public interface OnSendMessageListener {
        void onSendMessage(String message);
    }
}
