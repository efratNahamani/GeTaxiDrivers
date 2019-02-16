package com.example.shosh.get_taxi_driver.model.datasource;
import android.app.Service;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.controller.ApplicationContextProvider;
import com.example.shosh.get_taxi_driver.controller.DriverActivity;
import com.example.shosh.get_taxi_driver.controller.MyService;
import com.example.shosh.get_taxi_driver.model.entities.Driver;
import com.example.shosh.get_taxi_driver.model.entities.RIDESTATUS;
import com.example.shosh.get_taxi_driver.model.entities.Ride;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.shosh.get_taxi_driver.model.backend.Backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//database that uses in fire base
public class DatabaseFB implements Backend {


    public interface NotifyDataChange<T>//this interface defines the argument of register to event of change in fire base
    {   void onDataChanged(T obj);
        void onFailure(Exception exception);
    }
    FirebaseDatabase database = FirebaseDatabase.getInstance();//database Firebase
    DatabaseReference driverRef=database.getReference("Driver");//the Drivers root at the fire base
    DatabaseReference ridesRef= database.getReference("Ride");//the Rides root at the fire base
    static List<Ride> rideList=new ArrayList<>();//the list of the rides that in the fire base, it will be changed in real time with new rides

    static List<Driver> driverList=new ArrayList<>();//the list of the drivers that in the fire base, it will be changed in real time with new drivers
    private static ChildEventListener rideRefChildEventListener;//listener of the Rides list in the fire base
    private static ChildEventListener driverRefChildEventListener;//listener of the Drivers list in the fire base
    private static ChildEventListener serviceListener;//listener of the rides list in the fire base for the service-broadcast


    /**
     * this function call in constructor of DatabaseFB.it's start the event listeners for service and ridesRef
     * @param notifyDataChange - iterface implement that define reacting for a changes in firebase
     */
    public void notifyToRideList(final NotifyDataChange<List<Ride>> notifyDataChange) {
        try {
            if (notifyDataChange != null) {
                if (rideRefChildEventListener != null) {//if there is already a event
                    if (serviceListener != null) {//if there is already a event
                        notifyDataChange.onFailure(new Exception("first unNotify ClientRequest list"));
                        return;
                    } else {
                        serviceListener = new ChildEventListener() {//write servicListener event
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                notifyDataChange.onDataChanged(rideList);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        };
                        ridesRef.addChildEventListener(serviceListener);//add rideRef the serviceListener event
                        return;
                    }
                }
                rideList.clear();
                rideRefChildEventListener = new ChildEventListener() {//write rideRefListener event
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {//react when there is child added in firebase
                        try {
                            Ride ride = convertDataSnapshotToRide(dataSnapshot);//the func returns ride from the dataSnapshot
                            rideList.add(ride);
                            notifyDataChange.onDataChanged(rideList);
                        } catch (Exception E) {
                            System.out.println(E.getMessage());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {//react when there is child changed in firebase
                        try {
                            Ride ride = convertDataSnapshotToRide(dataSnapshot);//the func returns ride from the dataSnapshot

                            for (int i = 0; i < rideList.size(); i++) {
                                if (!rideList.get(i).equals(ride) && rideList.get(i).getId() == ride.getId()) {
                                    rideList.set(i, ride);
                                    break;
                                }
                            }
                            notifyDataChange.onDataChanged(rideList);
                        } catch (Exception E) {
                            System.out.println(E.getMessage());
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {//react when there is child removed from firebase
                        try {
                            Ride ride = convertDataSnapshotToRide(dataSnapshot);//the func returns ride from the dataSnapshot

                            for (int i = 0; i < rideList.size(); i++) {
                                if (rideList.get(i).equals(ride)) {
                                    rideList.remove(i);
                                    break;
                                }
                            }
                            notifyDataChange.onDataChanged(rideList);
                        } catch (Exception E) {
                            System.out.println(E.getMessage());
                        }

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        notifyDataChange.onFailure(databaseError.toException());

                    }
                };
                ridesRef.addChildEventListener(rideRefChildEventListener);//add rideRef the rideRefListener event

            }
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }

    /**
     * this function call when we want to stop listener to firebase
     */
    public void stopNotifyToRideList() {
        try {
            if (rideRefChildEventListener != null) {
                ridesRef.removeEventListener(rideRefChildEventListener);//remove listener
                rideRefChildEventListener = null;
            }
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }



    //////driver
    /**
     * this function call in constructor of DatabaseFB.it's start the event listeners for driverRef
     * @param driverNotifyDataChange - iterface implement that define reacting for a changes in firebase
     */
    public void notifyToDriverList(final NotifyDataChange<List<Driver>> driverNotifyDataChange)
    {try {
        if (driverNotifyDataChange != null) {
            if (driverRefChildEventListener != null) {//if there is already listener
                driverNotifyDataChange.onFailure(new Exception("unNotify drivers list"));
                return;
            }
            driverList.clear();
            driverRefChildEventListener = new ChildEventListener() {//write driverRefListener event
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {//react when there is child added to firebase
                    Driver driver = dataSnapshot.getValue(Driver.class); //it converts driver from the dataSnapshot
                    driverList.add(driver);
                    driverNotifyDataChange.onDataChanged(driverList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {//react when there is child changed in firebase
                    Driver driver = dataSnapshot.getValue(Driver.class); //it converts driver from the dataSnapshot

                    for (int i = 0; i < driverList.size(); i++) {
                        if (driverList.get(i).equals(driver)) {
                            driverList.set(i, driver);
                            break;
                        }
                    }
                    driverNotifyDataChange.onDataChanged(driverList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {//react when there is child removed from firebase
                    Driver driver = dataSnapshot.getValue(Driver.class); //it converts driver from the dataSnapshot
                    for (int i = 0; i < driverList.size(); i++) {
                        if (driverList.get(i).equals(driver)) {
                            driverList.remove(i);
                            break;
                        }
                    }
                    driverNotifyDataChange.onDataChanged(driverList);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    driverNotifyDataChange.onFailure(databaseError.toException());

                }
            };
            driverRef.addChildEventListener(driverRefChildEventListener);//add driverRef the driverRefListener event

        }
    }
    catch (Exception E)
    {
        System.out.println(E.getMessage());
    }
    }

    /**
     * this function call when we want to stop listener to firebase
     */
    public void stopNotifyToDriverList() {
        try {
            if (driverRefChildEventListener != null) {
                driverRef.removeEventListener(driverRefChildEventListener);//remove event from driverRef
                driverRefChildEventListener = null;
            }
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }

    /**
     * this constructor start the listeners to rideRef and driverRef
     */
    public DatabaseFB(){//constructor
        //register listener to rideRefChildEventListener, driverRefChildEventListener, serviceRefChildEventListener
        notifyToRideList(new NotifyDataChange<List<Ride>>() {//register listener to rideRefChildEventListener,serviceRefChildEventListener
        @Override
        public void onDataChanged(List<Ride> obj) { }
        @Override
        public void onFailure(Exception exception) { }
    });
        notifyToDriverList(new NotifyDataChange<List<Driver>>() {//register listener to driverRefChildEventListener
            @Override
            public void onDataChanged(List<Driver> obj) { }
            @Override
            public void onFailure(Exception exception) { }
        });

    }

    /**
     * updates ride in the fire base
     * @param ride - the ride with the same id in the fire base will be updated with this ride
     * @param
     */
    public void setRide(Ride ride){
        try {
            ridesRef.child(Long.toString(ride.getId())).setValue(ride);
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }

    /**
     * updates driver in the fire base
     * @param driver - the driver with the same id in the fire base will be updated with this driver
     * @param
     */
    public void setDriver(Driver driver){
        try {
            driverRef.child(Long.toString(driver.getId())).setValue(driver);
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }

    /**
     * authentication of email and password- search the appropriate driver and returns it
     * @param email - the email of the driver
     * @param password - the password of the driver
     * @return the driver in the fire base that his email and his password are equal to the parameters, if there is no
     * //driver who has this email and password- it returns null
     */
    public Driver authenticate(String email,String password) throws Exception//return null if the driver doesnt exist
    {
        try {
            for (Driver d : driverList) {
                if (email.equals(d.getMailAddress()) && password.equals(d.getPassword())) {
                    return d;
                }

            }
            return null;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return null;
        }
    }

    /**
     * @return all the drivers that in the fire base
     */
    public ArrayList<Driver> getAllDrivers() throws Exception
    {
        try {
            return (ArrayList<Driver>)driverList;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Driver>());
        }

    }

    /**
     * @return all the drivers names that in the fire base
     */
    public ArrayList<String> getAllDriversNames() throws Exception
    {
        try {
            ArrayList<String> driversNames = new ArrayList<String>();
            for (Driver d : driverList) {
                driversNames.add(d.getFirstName() + " " + d.getLastName());
            }
            return driversNames;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<String>());
        }
    }

    /**
     * adds driver to the firebase
     * @param driver= the driver that will be added to the firebase
     */
    public void addDriver(Driver driver) throws Exception
    {
        try {
            String key = Long.toString(driver.getId());
            driverRef.child(key).setValue(driver);
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }

    /**
     * @return all the rides that in the fire base
     */
    public ArrayList<Ride> getAllRides() throws Exception
    {
        try {
            return (ArrayList<Ride>) rideList;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }
    }

    /**
     * @return all the available rides (status=available) that in the fire base
     */
    public ArrayList<Ride> getAllAvailableRides() throws Exception
    {
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (r.getRideStatus().equals(RIDESTATUS.AVAILABLE))
                    list.add(r);
            return (ArrayList<Ride>) list;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }
    }

    /**
     * @return all the done rides (status=done) that in the fire base
     */
    public ArrayList<Ride> getAllDoneRides() throws Exception
    {
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (r.getRideStatus().equals(RIDESTATUS.DONE))
                    list.add(r);
            return (ArrayList<Ride>) list;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }

    }

    /**
     * @return ArrayList of all the done rides (status=done) of the driver parameter
     * @param driver - the driver that his done rides will be returned
     */
    public ArrayList<Ride> getAllDoneDriverRides(Driver driver) throws Exception
    {
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (r.getRideStatus().equals(RIDESTATUS.DONE) && r.getIdDriver() == driver.getId())
                    list.add(r);
            return (ArrayList<Ride>) list;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }
    }

    /**
     * @return ArrayList of all the available rides (status=available) that their sourceLocation in the city that is sent as parameter
     * @param city - the city that the available rides that their sourceLocation in this city will be returned
     */
    public ArrayList<Ride> getAllAvailableRidesInCity(String city) throws Exception {
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (r.getRideStatus().equals(RIDESTATUS.AVAILABLE)) {
                    Geocoder gc = new Geocoder(ApplicationContextProvider.getContext());
                    if (gc.isPresent()) {
                        List<Address> listAddress = gc.getFromLocationName(r.getSourceLocation(), 1);//convert string to address
                        if (listAddress.size() > 0) {
                            Address address = listAddress.get(0);
                            String rideCity = address.getLocality();
                            if (city.equals(rideCity)) {
                                list.add(new Ride(r));
                            }
                        }
                    }
                }
            return (ArrayList<Ride>) list;
        } catch (Exception e) {
            return new ArrayList<Ride>();
        }
    }


    /**
     * @return ArrayList of all the rides of the driver parameter that their sourceLocation in the same city as the city parameter
     * @param driver - returns driver's rides that their sourceLocation in the same city as the city parameter
     * @param city - the city that in the sourceLocation of the rides
     */
    public ArrayList<Ride> getAllRidesInCity(String city,Driver driver) throws Exception
    {
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (driver.getId() == r.getIdDriver()) {
                    Geocoder gc = new Geocoder(ApplicationContextProvider.getContext());
                    if (gc.isPresent()) {
                        List<Address> listAddress = gc.getFromLocationName(r.getSourceLocation(), 1);//convert string to address
                        if (listAddress.size() > 0) {
                            Address address = listAddress.get(0);
                            String rideCity = address.getLocality();
                            if (city.equals(rideCity)) {
                                list.add(new Ride(r));
                            }
                        }
                    }
                }
            return (ArrayList<Ride>) list;
        }
        catch (Exception e){
                return new ArrayList<Ride>();
            }
    }

    /**
     * @return ArrayList of all driver's ride that their date is the equal to the "date" parameter
     * @param driver - returns driver's rides that their date is the equal to the "date" parameter
     * @param date- the date of the rides (date in the beginningTime time field)
     */
    public ArrayList<Ride> getAllRidesByDate(Date date,Driver driver) throws Exception
    {
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList) {
                System.out.println(dateFormat.format(r.getBeginningTime()));
                System.out.println(dateFormat.format(date));
                if (r.getIdDriver() == driver.getId() && dateFormat.format(r.getBeginningTime()).equals(dateFormat.format(date))) {

                    list.add(new Ride(r));
                }
            }
            return (ArrayList<Ride>) list;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }
    }

    /**
     * @return ArrayList of all driver's ride that their cost is the equal to the cost" parameter
     * @param driver - returns driver's rides that their cost is the equal to the "cost" parameter
     * @param cost- the cost of the rides
     */
    public ArrayList<Ride> getallRidesByCost(long cost,Driver driver) throws Exception
    {
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (r.getIdDriver() == driver.getId() && cost(r.getSourceLocation(), r.getDestinationLocation()) == cost)
                    list.add(r);
            return (ArrayList<Ride>) list;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }

    }

    /**
     * @return ArrayList of all available rides that their distance from "currentLocation" parameter is equal or smaller than "radius" parameter
     * @param currentLocation - returns rides that their status is AVAILABLE and their distance from "currentLocation" parameter is equal or smaller than "radius" parameter
     * @param radius- the raduis distance from "currentLocation" parameter
     */
    public ArrayList<Ride> getAllAvailableRidesByDistance(Address currentLocation,float radius) throws Exception
    {
        try {
            List<Ride> list = new ArrayList<Ride>();
            Location DriverSourceLocation = new Location("name2");
            DriverSourceLocation.setLongitude(currentLocation.getLongitude());
            DriverSourceLocation.setLatitude(currentLocation.getLatitude());
            float dist;
            for (Ride r : rideList)
            {
                dist=distance(DriverSourceLocation, r.getSourceLocation());
                if (r.getRideStatus().equals(RIDESTATUS.AVAILABLE) && dist<= radius && dist!=-1)
                    list.add(r);}
            return (ArrayList<Ride>) list;
        }
        catch (Exception e){return new ArrayList<Ride>();}
    }

    /**
     * @return ArrayList of all driver's rides
     * @param driver - returns the rides of this driver
     */
    public ArrayList<Ride> getAllDriverRides(Driver driver)throws Exception{
        try {
            List<Ride> list = new ArrayList<Ride>();
            for (Ride r : rideList)
                if (r.getIdDriver() == driver.getId())
                    list.add(r);
            return (ArrayList<Ride>) list;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new ArrayList<Ride>());
        }
    }

    /**
     * @return the cost of ride between "SourceLocation" parameter to "DestinationLocation" parameter
     * @param SourceLocation - the source location of ride
     * @param DestinationLocation- the destination location of ride
     */
    public double cost(String SourceLocation,String DestinationLocation){
        try {
            return (0.25 * stringDistance(SourceLocation, DestinationLocation));
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return 0;
        }
    }
    /**
     * @return the distance of ride between "SourceLocation" parameter to "DestinationLocation" parameter.if there is error-return -1
     * @param SourceLocation - the source location of ride-String
     * @param DestinationLocation- the destination location of ride-String
     */
    public float stringDistance(String SourceLocation,String DestinationLocation)//if there error return -1
    {
        try {
            Location locationSource;
            Location locationDest;
            Geocoder gc = new Geocoder(ApplicationContextProvider.getContext());
            if (gc.isPresent()) {
                List<Address> listAddressSource = gc.getFromLocationName(SourceLocation, 1);//convert string to address
                List<Address> listAddressDest = gc.getFromLocationName(DestinationLocation, 1);//convert string to address
                if (listAddressSource.size() > 0&&listAddressDest.size() > 0) {
                    //source location
                    Address addressSource = listAddressSource.get(0);
                   locationSource=new Location("name");
                   locationSource.setLongitude(addressSource.getLongitude());
                   locationSource.setLatitude(addressSource.getLatitude());
                    //dest location
                    Address addressDest = listAddressDest.get(0);
                    locationDest=new Location("name1");
                    locationDest.setLongitude(addressDest.getLongitude());
                    locationDest.setLatitude(addressDest.getLatitude());
                    return locationSource.distanceTo(locationDest);//distance
                }

            }
            return -1;//if there error
        }
        catch(Exception e){
            return -1;//if there error
        }
    }
    /**
     * @return the distance of ride between "SourceLocation" parameter to "DestinationLocation" parameter.if there is error-return -1
     * @param SourceLocation - the source location of ride-Location variable
     * @param DestinationLocation- the destination location of ride-String
     */
   public float distance(Location SourceLocation,String DestinationLocation)//if there error return -1
   {
       try {
           Location locationDest;
           Geocoder gc = new Geocoder(ApplicationContextProvider.getContext());
           if (gc.isPresent()) {
               List<Address> listAddressDest = gc.getFromLocationName(DestinationLocation, 1);//convert string to address
               if (listAddressDest.size() > 0) {
                   //dest Location String convert to Location
                   Address addressDest = listAddressDest.get(0);
                   locationDest=new Location("name1");
                   locationDest.setLongitude(addressDest.getLongitude());
                   locationDest.setLatitude(addressDest.getLatitude());
                   return (SourceLocation.distanceTo(locationDest))/1000;
               }

           }
           return -1;//if there error
       }
       catch(Exception e){
           return -1;//if there error
       }
   }

    /**
     * @return the ride from "dataSnapshot" parameter
     * @param dataSnapshot - the current situation of firebase.by reading it we take a ride variable
     */
    public Ride convertDataSnapshotToRide(DataSnapshot dataSnapshot )
    {
        try {
            long id = Long.parseLong(dataSnapshot.getKey());
            RIDESTATUS rideStatus = RIDESTATUS.valueOf(dataSnapshot.child("rideStatus").getValue().toString());
            String sourceLocation = (String) dataSnapshot.child("sourceLocation").getValue();
            String destinationLocation = (String) dataSnapshot.child("destinationLocation").getValue();
            long beginningTimeYear = (long) dataSnapshot.child("beginningTime").child("year").getValue();
            long beginningTimeMonth = (long) dataSnapshot.child("beginningTime").child("month").getValue();
            long beginningTimeDate = (long) dataSnapshot.child("beginningTime").child("date").getValue();
            long beginningTimeHours = (long) dataSnapshot.child("beginningTime").child("hours").getValue();
            long beginningTimeMinutes = (long) dataSnapshot.child("beginningTime").child("minutes").getValue();
            long beginningTimeSeconds = (long) dataSnapshot.child("beginningTime").child("seconds").getValue();
            Date beginningTime = new Date((int) beginningTimeYear, (int) beginningTimeMonth, (int) beginningTimeDate, (int) beginningTimeHours, (int) beginningTimeMinutes, (int) beginningTimeSeconds);
            long endTimeYear = (long) dataSnapshot.child("endTime").child("year").getValue();
            long endTimeMonth = (long) dataSnapshot.child("endTime").child("month").getValue();
            long endTimeDate = (long) dataSnapshot.child("endTime").child("date").getValue();
            long endTimeHours = (long) dataSnapshot.child("endTime").child("hours").getValue();
            long endTimeMinutes = (long) dataSnapshot.child("endTime").child("minutes").getValue();
            long endTimeSeconds = (long) dataSnapshot.child("endTime").child("seconds").getValue();
            Date endTime = new Date((int) endTimeYear, (int) endTimeMonth, (int) endTimeDate, (int) endTimeHours, (int) endTimeMinutes, (int) endTimeSeconds);
            String clientName = (String) dataSnapshot.child("clientName").getValue();
            String clientPhone = (String) dataSnapshot.child("clientPhone").getValue();
            String clientEmail = (String) dataSnapshot.child("clientEmail").getValue();
            long idDriver = Long.parseLong(dataSnapshot.child("idDriver").getValue().toString());
            return new Ride(id, rideStatus, sourceLocation, destinationLocation, beginningTime, endTime, clientName, clientPhone, clientEmail, idDriver);
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return (new Ride());
        }
    }
    /**
     * @return the first BUSY ride of "driver" parameter(there is must be only one)
     * @param driver- the driver we search BUSY ride for
     */
    public Ride getBusyRideByDriver(Driver driver)
    {
        try {
            for (Ride r : rideList) {
                if (r.getRideStatus().equals(RIDESTATUS.BUSY) && r.getIdDriver() == driver.getId())
                    return r;
            }
            return null;
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
            return null;
        }
    }
}
