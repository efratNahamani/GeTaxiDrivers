package com.example.shosh.get_taxi_driver.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.Backend;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;
import com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB;
import com.example.shosh.get_taxi_driver.model.entities.Driver;
import com.example.shosh.get_taxi_driver.model.entities.RIDESTATUS;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyRidesListFragment extends Fragment {

    ArrayAdapter<Ride> adapter;//between myList and listView
    ArrayList<Ride> myList;//the current list of rides
    ListView listView;//view

    /**
     * this function calls when the list that in listView of this fragment need to change
     * @param list new list that will be in the listView
     */
    public void updateListView(ArrayList<Ride> list)
    {
        try {
            myList = list;//put the new list
            adapter=new ArrayAdapter<Ride>(getActivity(),R.layout.my_ride_item,myList){//adapter that connect between
                /**
                 * this function calls on every Ride in myList.it design the Ride looking in listView
                 * @param position the Ride in position place in myList
                 * @param convertView the current view
                 * @param parent the parent of view
                 */
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(getActivity(), R.layout.my_ride_item, null);//init convertView
                    }
                    //take view widgets from view that define in my_ride_item xml file
                    TextView nameTextView = (TextView) convertView.findViewById(R.id.name_my_ride_item);
                    TextView dateTextView = (TextView) convertView.findViewById(R.id.date_my_ride_item);
                    TextView sourceTextView = (TextView) convertView.findViewById(R.id.source_my_ride_item);
                    TextView destTextView = (TextView) convertView.findViewById(R.id.dest_my_ride_item);
                    Button addContact = (Button) convertView.findViewById(R.id.button_my_add_contact);
                    addContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {//implement the onClick of button that add client of ride to contacr in phone
                            View parent = (View) view.getParent().getParent().getParent().getParent().getParent();
                            ListView ls = (ListView) parent.getParent();//the listView
                            int position = ls.getPositionForView(parent);//get the position of the ride that the user click on it's button-TakeRide
                            Ride ride = myList.get(position);//get ride
                            //insert to contact
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                            intent.putExtra(ContactsContract.Intents.Insert.NAME, ride.getClientName());
                            intent.putExtra(ContactsContract.Intents.Insert.PHONE, ride.getClientPhone());
                            startActivity(intent);
                        }
                    });
                    Ride ride=(Ride)myList.get(position);//get the ride
                    //show the ride details in row of listView like this:
                    nameTextView.setText(ride.getClientName());
                    dateTextView.setText(ride.getEndTime().toString());
                    sourceTextView.setText(ride.getSourceLocation());
                    destTextView.setText(ride.getDestinationLocation());
                    return convertView;
                }
            };

            listView.setAdapter(adapter);//set the view
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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
        View view = inflater.inflate(R.layout.fragment_my_rides_list, container, false);
        try {
            listView = view.findViewById(R.id.my_rides_list);//the list view
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long i) {//implement onClick on listView item
                    Ride ride = myList.get(position);//get the selected ride
                    myRideDetailsFragment fragDetails = (myRideDetailsFragment) getActivity().getFragmentManager().findFragmentByTag("myRideDetailsFragmentTag");//get the fragment of details
                    fragDetails.updateRide(ride);//put the ride details in this fragment
                }
            });
            updateListView(BackendFactory.getInstance(getActivity()).getAllDriverRides(((DriverActivity) getActivity()).getCurrentDriver()));//put all the current driver rides in listView
            return view;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return view;
        }

    }

}
