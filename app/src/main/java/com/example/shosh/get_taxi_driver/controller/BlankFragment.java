package com.example.shosh.get_taxi_driver.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shosh.get_taxi_driver.R;


//this fragment is empty fragment (we did it in order to fill the container with empty fragment)
public class BlankFragment extends Fragment {


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

        return inflater.inflate(R.layout.fragment_blank, container, false);// Inflate the layout for this fragment
    }

}
