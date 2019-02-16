package com.example.shosh.get_taxi_driver.model.backend;

import android.location.Address;

import com.example.shosh.get_taxi_driver.model.entities.Driver;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

import java.util.ArrayList;
import java.util.Date;

public interface Backend {//interface that will help us use a DataBase(some diffrenent)
       // public ArrayList<Driver> getAllDrivers() throws Exception;
        //public ArrayList<String> getAllDriversNames() throws Exception;
        public void addDriver(Driver driver) throws Exception;
        public ArrayList<Ride> getAllRides() throws Exception;
        public ArrayList<Ride> getAllAvailableRides() throws Exception;
        public ArrayList<Ride> getAllDoneRides() throws Exception;
        public ArrayList<Ride> getAllDoneDriverRides(Driver driver) throws Exception;
        public ArrayList<Ride> getAllAvailableRidesInCity(String city) throws Exception ;
        //public ArrayList<Ride> getAllAvailableRides(Driver driver,String driverLocation) throws Exception ;
        public ArrayList<Ride> getAllRidesByDate(Date date, Driver driver) throws Exception;
        public ArrayList<Ride> getallRidesByCost(long cost,Driver driver) throws Exception;
        public ArrayList<Ride> getAllDriverRides(Driver driver)throws Exception;
        public ArrayList<Ride> getAllRidesInCity(String city,Driver driver) throws Exception;
        public ArrayList<Ride> getAllAvailableRidesByDistance(Address currentLocation,float radius) throws Exception;
}
