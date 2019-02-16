package com.example.shosh.get_taxi_driver.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shosh.get_taxi_driver.R;

//sending a broadcast
public class WelcomeFragment extends Fragment {
    String name;
    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * this function calls when the activity is opened, after the function "onCreate" is called
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The fragment
     * should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_welcome, container, false);
        try {
            TextView nameText = v.findViewById(R.id.welcome_driver_name);
            nameText.setText(name);//put the full name of driver
            return v;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return v;
        }
    }
    /**
     * this function put in the variable name the full name of the current driver so it can appear in WELCOME fragment
     * @param firstName the first name of current driver
     * @param lastName  the last name of current driver
     */
    public void setDriverName(String firstName,String lastName){
        name=firstName+" "+lastName;
    }


}
