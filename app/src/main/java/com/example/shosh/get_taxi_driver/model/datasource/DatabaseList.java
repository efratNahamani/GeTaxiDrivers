package com.example.shosh.get_taxi_driver.model.datasource;

import com.example.shosh.get_taxi_driver.model.backend.Backend;
import com.example.shosh.get_taxi_driver.model.entities.Driver;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseList {//implements Backend {//this class implement Backend. save the data in Lists.
    ArrayList<Driver> drivers=new ArrayList<Driver>();//the list
    static public long driverCounter=0;
   /* public ArrayList<Driver> getAllDrivers() throws Exception
    {

    }
    public ArrayList<String> getAllDriversNames() throws Exception
    {

    }*/
    public void addDriver(Driver driver) throws Exception
    {
        driver.setId(driverCounter++);
        for(Driver item:drivers)//check if the ride already exist
            if(item.equals(drivers))
                throw new Exception("this drive already exist");
        drivers.add(driver);
    }

    public ArrayList<Ride> getAllRides() throws Exception
    {
        return new ArrayList<Ride>();
    }
    /*
    public ArrayList<Ride> getAllAvailableRides() throws Exception
    {
        return new ArrayList
    }
    public ArrayList<Ride> getAllDoneRides() throws Exception
    {

    }
    public ArrayList<Ride> getAllDoneDriverRides(Driver driver) throws Exception
    {

    }
    public ArrayList<Ride> getAllAvailableRidesInCity(String city) throws Exception
    {

    }
    public ArrayList<Ride> getAllAvailableRides(Driver driver,String driverLocation) throws Exception
    {

    }
    public ArrayList<Ride> getAllRidesByDate(Date date) throws Exception
    {

    }
    public ArrayList<Ride> getallRidesByCost(long cost) throws Exception
    {

    }*/
}
