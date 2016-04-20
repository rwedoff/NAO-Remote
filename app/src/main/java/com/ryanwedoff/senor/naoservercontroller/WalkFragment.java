package com.ryanwedoff.senor.naoservercontroller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * WalkFragment holds the Fragment that is used in Remote Fragment within ControllerActivity
 * @see RemoteFragment
 * @see ControllerActivity
 */
public class WalkFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ROBOT_NAME = "robot_name";

    private RemoteFragment.OnSendMessageListener socketListener;

    public WalkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param robotName Parameter 1
     * @return A new instance of fragment WalkFragment.
     */
    public static WalkFragment newInstance(String robotName) {
        WalkFragment fragment = new WalkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROBOT_NAME, robotName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootLayout =  inflater.inflate(R.layout.fragment_walk, container, false);
        final Fragment thisFrag = this;

        /**
         * Event listeners for FABs in the WalkFragment
         */
        FloatingActionButton forwardButton = (FloatingActionButton)rootLayout.findViewById(R.id.forwardWalkButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            boolean click1 = true;
            @Override
            public void onClick(View v) {
                Bundle bundle = thisFrag.getArguments();
                String robotNameMess = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                if(click1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftY=" + 1 + ";");
                    click1 = false;
                }

                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftY=0;");
                    click1 = true;
                }
            }
        });
        FloatingActionButton backButton = (FloatingActionButton)rootLayout.findViewById(R.id.backWalkButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            boolean click1 = true;
            @Override
            public void onClick(View v) {
                Bundle bundle = thisFrag.getArguments();
                String robotNameMess = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                if(click1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftY=" + -1 + ";");
                    click1 = false;
                }

                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftY=0;");
                    click1 = true;
                }
            }
        });
        FloatingActionButton rightButton = (FloatingActionButton)rootLayout.findViewById(R.id.rightWalkButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            boolean click1 = true;
            @Override
            public void onClick(View v) {
                Bundle bundle = thisFrag.getArguments();
                String robotNameMess = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                if(click1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftX=" + 1 + ";");
                    click1 = false;
                }

                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftX=0;");
                    click1 = true;
                }
            }
        });
        FloatingActionButton leftButton = (FloatingActionButton)rootLayout.findViewById(R.id.leftWalkButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            boolean click1 = true;
            @Override
            public void onClick(View v) {
                Bundle bundle = thisFrag.getArguments();
                String robotNameMess = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                if(click1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftX=" + -1 + ";");
                    click1 = false;
                }

                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));
                    }
                    socketListener.onSendMessage(robotNameMess + "LeftX=0;");
                    click1 = true;
                }
            }
        });
        FloatingActionButton rotleftButton = (FloatingActionButton)rootLayout.findViewById(R.id.walkRotLeft);
        rotleftButton.setOnClickListener(new View.OnClickListener() {
            boolean click1 = true;
            @Override
            public void onClick(View v) {
                Bundle bundle = thisFrag.getArguments();
                String robotNameMess = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                if(click1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    socketListener.onSendMessage(robotNameMess + "Theta=" + 1 + ";");
                    click1 = false;
                }

                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));
                    }
                    socketListener.onSendMessage(robotNameMess + "Theta=0;");
                    click1 = true;
                }


            }
        });
        FloatingActionButton rotrightButton = (FloatingActionButton)rootLayout.findViewById(R.id.walkRotRight);
        rotrightButton.setOnClickListener(new View.OnClickListener() {
            boolean click1 = true;
            @Override
            public void onClick(View v) {
                Bundle bundle = thisFrag.getArguments();
                String robotNameMess = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error") + ";";
                if(click1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    socketListener.onSendMessage(robotNameMess + "Theta=" + -1 + ";");
                    click1 = false;
                }

                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));
                    }
                    socketListener.onSendMessage(robotNameMess + "Theta=0;");
                    click1 = true;
                }

            }
        });

        return rootLayout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            socketListener = (RemoteFragment.OnSendMessageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSendMessageListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        socketListener = null;
    }


}
