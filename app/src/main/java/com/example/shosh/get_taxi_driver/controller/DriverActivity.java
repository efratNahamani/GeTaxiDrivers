package com.example.shosh.get_taxi_driver.controller;

import android.Manifest;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.Backend;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;
import com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB;
import com.example.shosh.get_taxi_driver.model.entities.Driver;
import com.example.shosh.get_taxi_driver.model.entities.RIDESTATUS;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//the main activity, there is a menu
public class DriverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean flag = false;//true when the fragments are changed in the first time
    Driver currentDriver;//will contains the driver that signed in to the application (in the LoginActivity)
    Ride currentRide;//if the driver has a busy ride, this field will contain this ride, else it will be null
    Address currentLocation;//will contain the current location of this phone
    LocationManager mLocationManager;//
    MenuItem itemStart;//the item that if the driver has busy ride (currentRide!=null), the driver can click it to save the start time of this ride
    //if the ride already started, it will show the started time
    MenuItem itemEnd;//the item that if the driver has busy ride (currentRide!=null), the driver can click it to save the end time of this ride
    SubMenu itemDetails;//the item (subMenu) that if the driver has busy ride (currentRide!=null), the driver can click it to show the details of this ride
    //only source and destination

    MenuItem from;//item in the itemDetails that will show the source address of the current ride
    MenuItem to;//item in the itemDetails that will show the destination address of the current ride
    Menu menuTakeRide;//the menu that will be only if the currentDriver!=null
    static ComponentName service = null;//the service that listener to change of rides in the database, it will send broadcast.



   private LocationListener mLocationListener = new LocationListener() {
       /**
        * this function gets the current location of the user and it sets in "currentLocation" field of the DriverActivity
        * it removes the listener after the first time that it takes the location
        * @param location the current location of the phone, Location type= contains latitude and longitude
        */
        @Override
        public void onLocationChanged(final Location location) {

            try {
                Geocoder geocoder = new Geocoder(DriverActivity.this, Locale.getDefault());
                List<Address> addresses  = null;
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);//convert the location to address
                if(addresses.size()==0)//error
                {
                    currentLocation=new Address(Locale.getDefault());
                }
                else{
                    currentLocation=addresses.get(0);
                    mLocationManager.removeUpdates(mLocationListener);//removes this listener after the first time
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    /**
     * this function set the visible of the menuTakeRide with false.
     */
  public void invisibleCurrentRideItems(){
      menuTakeRide.setGroupVisible(R.id.group,false);
   }
    /**
     * this function puts the current ride details- source and location in the submenu
     */
   public void putCurrentRideDetails(){
       menuTakeRide.setGroupVisible(R.id.group,true);
       if(currentDriver.getIsStarted()==true)//the ride already started
       { SimpleDateFormat formatter_to = new SimpleDateFormat("HH:mm");
           itemStart.setTitle("start: " + formatter_to.format(currentRide.getBeginningTime()));
           itemStart.setEnabled(false);}
           from.setTitle("from: "+currentRide.getSourceLocation());
           to.setTitle("to: "+currentRide.getDestinationLocation());
           openOptionsMenu();

   }

    /**
     * @return the busy ride of the driver
     */
    public Ride getCurrentRide() {
       return currentRide;
   }

   /**
     * @param ride  the busy ride of the driver
     */
   public void setCurrentRide(Ride ride){currentRide=ride;}

    /**
     * @return the currentLocation of this phone
     */
    public Address getCurrentAddress() {
        return currentLocation;
    }

    /**
     * @return the current driver- the driver that signed in the LoginActivity
     */
    public Driver getCurrentDriver() {
        return currentDriver;
    }

    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            currentLocation = new Address(Locale.getDefault());//default address (before the listener will change it)
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //checks permissions to take location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            }

            //registers to changing in the location, when it will be changed- the "mLocationListener" will be called, every 1 ms, and 100 meters
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 100, mLocationListener);
            Backend b = BackendFactory.getInstance(this);
            //set the currentDriver= the driver that signed in, this information is taken
            //from the LoginActivity (we sends it in the extra info of the intent)
            currentDriver = (Driver) getIntent().getSerializableExtra("driver");
            //set the currentRide=the busy ride of the currentDriver, if there is no besy ride it will be null
            currentRide = ((DatabaseFB) BackendFactory.getInstance(this)).getBusyRideByDriver(currentDriver);

            //set the service field= the service that listener to change of rides in the database, it will send broadcast.
            if (service == null) {
                Intent intent = new Intent(getBaseContext(), MyService.class);
                service = startService(intent);
            }
            setContentView(R.layout.activity_driver);//loads the view from the activity_driver xml file
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //the left menu
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //loads the fragments to welcome mode (home)
            navigationView.setCheckedItem(R.id.nav_home);// the home icon will be "clicked"
            onNavigationItemSelected(navigationView.getMenu().getItem(0));//loads the fragments...
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * this function calls when the user click on backPressed
     */
    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);//the left menu
            if (drawer.isDrawerOpen(GravityCompat.START)) {//if the left menu is open, closes it.
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * this function calls when the optionMenu (the right menu= the menu off the current ride) is created
     * @param menu the right menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.driver, menu);
            menuTakeRide = menu;
            itemStart = menu.findItem(R.id.action_start);
            itemEnd = menu.findItem(R.id.action_end);
            itemDetails = menu.findItem(R.id.action_details).getSubMenu();
            from = itemDetails.findItem(R.id.action_source);
            to = itemDetails.findItem(R.id.action_dest);
            if (currentRide == null)//if the driver does not have a busy ride
                invisibleCurrentRideItems();
            else
                putCurrentRideDetails();
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }
    /**
     * this function is called when the item is selected in the right menu (of the current ride)
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        try {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_start)//if the item selected is the start time item, the current time will be store on the database in this ride details,
            //in the startTime field
            {
                Calendar now = Calendar.getInstance();
                Date currentTime = now.getTime();
                currentRide.setBeginningTime(currentTime);
                ((DatabaseFB) BackendFactory.getInstance(this)).setRide(currentRide);
                currentDriver.setIsStarted(true);
                ((DatabaseFB) BackendFactory.getInstance(this)).setDriver(currentDriver);
                SimpleDateFormat formatter_to = new SimpleDateFormat("hh:mm");
                itemStart.setTitle("start: " + formatter_to.format(currentTime));//shows the start time on this item
                itemStart.setEnabled(false);
                return true;

            }
            if (id == R.id.action_end)//if the item selected is the end time, the current time will be store on the database in this ride details in the endTime
            //field
            {
                //takes current time and set it in the database
                Date currentTime = Calendar.getInstance().getTime();
                currentRide.setEndTime(currentTime);
                currentRide.setRideStatus(RIDESTATUS.DONE);
                ((DatabaseFB) BackendFactory.getInstance(this)).setRide(currentRide);
                currentDriver.setIsStarted(false);
                ((DatabaseFB) BackendFactory.getInstance(this)).setDriver(currentDriver);
                invisibleCurrentRideItems();//
                itemStart.setEnabled(true);
                itemStart.setTitle("click to start ride");
                double costRide = ((DatabaseFB) BackendFactory.getInstance(this)).cost(currentRide.getSourceLocation(), currentRide.getDestinationLocation());

                //changes the fragments to the welcome mode= home
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                //loads the fragments to welcome mode (home)
                navigationView.setCheckedItem(R.id.nav_home);// the home icon will be "clicked"
                onNavigationItemSelected(navigationView.getMenu().getItem(0));//loads the fragments...
                //shows the cost of the ride to the driver
                Toast.makeText(this, "Thank you, the ride's cost " + Double.toString(costRide) + "$", Toast.LENGTH_LONG).show();
                currentRide=null;
                return true;
            }
        }
        catch (Exception e)
            {
                System.out.println(e.getMessage());
                return super.onOptionsItemSelected(item);
            }


        return super.onOptionsItemSelected(item);
    }



    /**
     * this function calls when user clicks on item in navigationMenu (the left menu)
     * @param item the item that the user clicks on it
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        try {
            int id = item.getItemId();

            if (id == R.id.nav_exit) {//exit from the application
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            } else if (id == R.id.nav_myRides) {//changes the fragments to "my ride" mode= shows driver's rides with filter options...
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                filterMyRidesFragment fragment = new filterMyRidesFragment();
                MyRidesListFragment fragment1 = new MyRidesListFragment();
                myRideDetailsFragment fragment2 = new myRideDetailsFragment();
                fragmentTransaction.replace(R.id.container1, fragment);
                fragmentTransaction.replace(R.id.container2, fragment1, "myRidesListFragmentTag");
                fragmentTransaction.replace(R.id.container3, fragment2, "myRideDetailsFragmentTag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //changes the fragments to "search ride" mode= shows available rides with filter options...
            } else if (id == R.id.nav_search) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                filterSearchRidesFragment fragment = new filterSearchRidesFragment();
                SearchRidesFragment fragment1 = new SearchRidesFragment();
                RideDetailsFragment fragment2 = new RideDetailsFragment();
                fragmentTransaction.replace(R.id.container1, fragment);
                fragmentTransaction.replace(R.id.container2, fragment1, "searchRidesListFragmentTag");
                fragmentTransaction.replace(R.id.container3, fragment2, "RideDetailsFragmentTag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //signed out from this user= back to LoginActivity
            } else if (id == R.id.nav_sign_out) {
                Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear the backStack
                startActivity(intent);

                //changes the fragments to "home" mode= shows welcome fragment with the user name
            } else if (id == R.id.nav_home) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                WelcomeFragment fragmentW = new WelcomeFragment();
                fragmentW.setDriverName(currentDriver.getFirstName(), currentDriver.getLastName());
                BlankFragment fragment1 = new BlankFragment();
                BlankFragment fragment2 = new BlankFragment();
                if (!flag)//
                {
                    fragmentTransaction.add(R.id.container1, fragment1);
                    fragmentTransaction.add(R.id.container2, fragmentW);
                    fragmentTransaction.add(R.id.container3, fragment2);
                    fragmentTransaction.commit();
                    flag = true;

                } else {
                    fragmentTransaction.replace(R.id.container1, fragment1);
                    fragmentTransaction.replace(R.id.container2, fragmentW);
                    fragmentTransaction.replace(R.id.container3, fragment2);
                    fragmentTransaction.addToBackStack(null);//stack for back normally
                    fragmentTransaction.commit();
                }

            } else {
            }//error
            //closes the drawer= the left menu
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

}

