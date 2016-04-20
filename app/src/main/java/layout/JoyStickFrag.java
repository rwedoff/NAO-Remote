package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ryanwedoff.senor.naoservercontroller.EditedJoyStickView;
import com.ryanwedoff.senor.naoservercontroller.R;
import com.ryanwedoff.senor.naoservercontroller.RemoteFragment;


/**
 * JoyStickFrag is the Fragment that holds EditedJoyStickView and is used for Robot Head Control.
 * It is contained in RemoteFragment and ControllerActivity
 * @see RemoteFragment
 * @see com.ryanwedoff.senor.naoservercontroller.ControllerActivity
 */
public class JoyStickFrag extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ROBOT_NAME = "robot_name";
    private int oldAngle = 0;
    private String robotName;

    private RemoteFragment.OnSendMessageListener mListener2;

    public JoyStickFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param robotName Parameter 1.
     * @return A new instance of fragment JoyStickFrag.
     */
    public static JoyStickFrag newInstance(String robotName) {
        JoyStickFrag fragment = new JoyStickFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ROBOT_NAME, robotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            robotName = getArguments().getString(ARG_ROBOT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootLayout =  inflater.inflate(R.layout.fragment_joy_stick, container, false);

        Button centerButton =(Button)rootLayout.findViewById(R.id.stop_button);
        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Center", "Stop");
                mListener2.onSendMessage(robotName +";"+ "RightX=0;");
                mListener2.onSendMessage(robotName +";"+ "RightY=0;");
            }
        });
        EditedJoyStickView joystick = (EditedJoyStickView) rootLayout.findViewById(R.id.joystickView);
        Log.e("Temp", Integer.toString(joystick.getId()));
        final Fragment thisFrag = this;

        joystick.setOnJoystickMoveListener(new EditedJoyStickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int newAngle, int newPower, int direction) {
                Log.e("HERE","HERE");
                Bundle bundle = thisFrag.getArguments();
                String robotName = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";

                //angleTextView.setText(" " + String.valueOf(angle) + "°");
                //powerTextView.setText(" " + String.valueOf(power) + "%");
                Log.i("Angle (Degrees): ", String.valueOf(newAngle));
                Log.i("Power (%): ",String.valueOf(newPower));


               if(Math.abs(newAngle - oldAngle) >= 0.05){ //Int values are used, probably never > 0.05

                    //NOTES:  RIGHT means the right stick, which is the head, the units are in radians, I need to figure out the unit circle and
                    //how it relates to my joystick circle
                    if(newPower != 0){
                        double radians = Math.toRadians(newAngle);
                        double normalizedPowerX = (newPower * 2.5) /100;
                        mListener2.onSendMessage(robotName + "RightX=" + Math.sin(-radians)*normalizedPowerX + ";");
                        mListener2.onSendMessage(robotName + "RightY=" + -Math.cos(radians) + ";");

                    }

                    oldAngle = newAngle;
                }

            }
        }, EditedJoyStickView.DEFAULT_LOOP_INTERVAL);

        // Inflate the layout for this fragment
        return rootLayout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener2 = (RemoteFragment.OnSendMessageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSendMessageListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener2 = null;
    }

}
