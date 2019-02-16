package com.example.shosh.get_taxi_driver.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;
import com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB;
import com.example.shosh.get_taxi_driver.model.entities.RIDESTATUS;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

import java.util.ArrayList;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class SearchRidesFragment extends Fragment {

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
            adapter=new ArrayAdapter<Ride>(getActivity(),R.layout.search_ride_item,myList){//adapter that connect between
                /**
                 * this function calls on every Ride in myList.it design the Ride looking in listView
                 * @param position the Ride in position place in myList
                 * @param convertView the current view
                 * @param parent the parent of view
                 */
                @Override
                public View getView(int position,View convertView,ViewGroup parent)
                {
                    if(convertView==null)
                    {
                        convertView=View.inflate(getActivity(),R.layout.search_ride_item,null);//init convertView
                    }
                    //take view widgets from view that define in search_ride_item xml file
                    TextView nameTextView=(TextView)convertView.findViewById(R.id.name_search_ride_item);
                    TextView dateTextView=(TextView)convertView.findViewById(R.id.date_search_ride_item);
                    TextView sourceTextView=(TextView)convertView.findViewById(R.id.source_search_ride_item);
                    TextView destTextView=(TextView)convertView.findViewById(R.id.dest_search_ride_item);
                    Button buttonTakeRide=(Button)convertView.findViewById(R.id.button_take_ride);
                    buttonTakeRide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {//implement the onClick of button that take the ride for driver
                            View parent= (View) view.getParent().getParent().getParent().getParent().getParent();
                            ListView ls= (ListView) parent.getParent();//the listView
                            int position=ls.getPositionForView(parent);//get the position of the ride that the user click on it's button-TakeRide
                            if(((DriverActivity)getActivity()).getCurrentRide()!=null)//if there is already a ride that the current driver take
                            {
                                Toast.makeText(getActivity(),"You have already busy ride",Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    Ride ride = myList.get(position);//get ride
                                    //change ride details
                                    ride.setRideStatus(RIDESTATUS.BUSY);
                                    ride.setIdDriver(((DriverActivity) getActivity()).getCurrentDriver().getId());
                                    ((DatabaseFB) BackendFactory.getInstance(getActivity())).setRide(ride);//save in the firebase database
                                    ((DriverActivity) getActivity()).setCurrentRide(ride);//this ride become the current ride
                                    ((DriverActivity) getActivity()).putCurrentRideDetails();//open the menu to start the ride
                                    Toast.makeText(getActivity(),"This ride is yours! the passenger waits",Toast.LENGTH_LONG).show();

                                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
                                    {
                                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},1);
                                    }
                                    String message="Hi "+ride.getClientName()+",\n"+"the driver "+((DriverActivity) getActivity()).getCurrentDriver().getFirstName()+" is in his way to you!";
                                    SmsManager smsManager=SmsManager.getDefault();
                                    smsManager.sendTextMessage(ride.getClientPhone(),null,message,null,null);
                                }
                                catch (Exception e){}
                            }
                        }});
                    Button buttonAddContact=(Button)convertView.findViewById(R.id.button_add_contact);//a button to add the ride client to the contact in phone
                    buttonAddContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {//implement the onClick of button that add client of ride to contacr in phone
                            View parent= (View) view.getParent().getParent().getParent().getParent().getParent();
                            ListView ls= (ListView) parent.getParent();//the listView
                            int position=ls.getPositionForView(parent);//get the position of the ride that the user click on it's button-TakeRide
                            Ride ride=myList.get(position);//get ride
                            //insert to contact
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                            intent.putExtra(ContactsContract.Intents.Insert.NAME, ride.getClientName());
                            intent.putExtra(ContactsContract.Intents.Insert.PHONE, ride.getClientPhone());
                            startActivity(intent);
                        }});

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
        View view=inflater.inflate(R.layout.fragment_search_rides, container, false);

        listView=view.findViewById(R.id.search_rides_list);//the list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long i)
            {//implement onClick on listView item
                Ride ride=myList.get(position);//get the selected ride
                RideDetailsFragment fragDetails= (RideDetailsFragment)getActivity().getFragmentManager().findFragmentByTag("RideDetailsFragmentTag");//get the fragment of details
                fragDetails.updateRide(ride);//put the ride details in this fragment
            }
        });
        try {
            updateListView( BackendFactory.getInstance(getActivity()).getAllAvailableRides());//put all the available rides in listView by default
        }
        catch (Exception e){}
        return view;

    }
}
