package com.example.shosh.get_taxi_driver.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //RideDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class RideDetailsFragment extends Fragment {
    TextView clientName;
    TextView clientPhone;
    TextView clientEmail;
    TextView rideNum;
    TextView status;
    TextView source;
    TextView dest;


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
        View view=inflater.inflate(R.layout.fragment_ride_details, container, false);
        try {//get all the textView in view
            clientName=view.findViewById(R.id.details_search_ride_name);
            clientPhone=view.findViewById(R.id.details_search_ride_phone);
            clientEmail=view.findViewById(R.id.details_search_ride_email);
            rideNum=view.findViewById(R.id.details_search_ride_id);
            status=view.findViewById(R.id.details_search_ride_status);
            source=view.findViewById(R.id.details_search_ride_source);
            dest=view.findViewById(R.id.details_search_ride_dest);
            return view;
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            return view;
        }
    }
    /**
     * this function get a ride and put his details in the last fragment.calls when the user click on listView element.
     * @param ride is the ride that user see it's details
     * pay attention that the ride details that will show dependent in the ride status.
     */
    public void updateRide(Ride ride)
    {
        try {
            //put a left drawable in all the textViews in this details fragment
            addLeftDrawable(getResources().getDrawable(R.drawable.ic_action_little_taxi), rideNum);
            addLeftDrawable(getResources().getDrawable(R.drawable.ic_action_little_phone), clientPhone);
            addLeftDrawable(getResources().getDrawable(R.drawable.ic_action_little_email), clientEmail);
            addLeftDrawable(getResources().getDrawable(R.drawable.ic_action_little_location), source);
            addLeftDrawable(getResources().getDrawable(R.drawable.ic_action_little_location), dest);

            //put in all the textViews the values of the ride
            clientName.setText("Name: " + ride.getClientName());
            clientPhone.setText("Phone Number: " + ride.getClientPhone());
            clientEmail.setText("Email: " + ride.getClientEmail());
            rideNum.setText("Ride Number: " + Long.toString(ride.getId()));
            status.setText("Status: " + ride.getRideStatus().toString());
            source.setText("From: " + ride.getSourceLocation());
            dest.setText("To: " + ride.getDestinationLocation());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
    /**
     * this function put a left image from drawable in textView.
     * @param d is wanted drawable image.
     * @param e is the textView that we want to put image in.
     */
    public void addLeftDrawable(Drawable d, TextView e)
    {
        try {
            d.setBounds(0, 0, 60, 60);
            e.setCompoundDrawables(d, null, null, null);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    /**
     * this function put in all the textViews "" and remove the left drawable.calls when we want thr details fragment empty.
     */
    public void refresh()
    {
        try {
            //make all the left drawable of the textViews empty
            rideNum.setCompoundDrawables(null,null, null, null);
            clientPhone.setCompoundDrawables(null,null, null, null);
            clientEmail.setCompoundDrawables(null,null, null, null);
            source.setCompoundDrawables(null,null, null, null);
            dest.setCompoundDrawables(null,null, null, null);

            //make all the text of the textViews ""
            clientName.setText("");
            clientPhone.setText("");
            clientEmail.setText("");
            rideNum.setText("");
            status.setText("");
            source.setText("");
            dest.setText("");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
