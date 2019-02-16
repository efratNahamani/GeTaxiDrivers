package com.example.shosh.get_taxi_driver.model.entities;

import java.util.Date;

public class Ride {
    protected long id;
    protected RIDESTATUS rideStatus;
    protected String sourceLocation;
    protected String destinationLocation;
    protected Date beginningTime;
    protected Date endTime;
    protected String clientName;
    protected String clientPhone;
    protected String clientEmail;
    protected long idDriver;

    //constructors
    public Ride() {
        rideStatus=null;
        sourceLocation="";
        destinationLocation="";
        beginningTime=null;
        endTime=null;
        clientName="";
        clientPhone="";
        clientEmail="";
    }
    public Ride(Ride r) {
        id=r.getId();
        idDriver=r.getIdDriver();
        rideStatus= r.getRideStatus();
        sourceLocation=new String(r.getSourceLocation());
        destinationLocation=new String(r.getDestinationLocation());
        beginningTime=new Date(r.getBeginningTime().getTime());
        endTime=new Date(r.getEndTime().getTime());
        clientName=new String(r.getClientName());;
        clientPhone=new String(r.getClientPhone());;
        clientEmail=new String(r.getClientEmail());;
    }

    public Ride(long id, RIDESTATUS rideStatus, String sourceLocation, String destinationLocation, Date beginningTime, Date endTime, String clientName, String clientPhone, String clientEmail, long idDriver) {
        this.id = id;
        this.rideStatus = rideStatus;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.beginningTime = beginningTime;
        this.endTime = endTime;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.idDriver = idDriver;
    }

    public Ride(String sourceLocation, String destinationLocation, String clientName, String clientPhone, String clientEmail) {
        this.rideStatus = RIDESTATUS.AVAILABLE;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        //this.beginningTime = new Time(0);

        //this.endTime = new Time(0);
        this.beginningTime = new Date();
        this.endTime = new Date();
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
    }

    public Ride(long id,String sourceLocation, String destinationLocation,String clientName, String clientPhone, String clientEmail) {
        this.id=id;
        this.rideStatus = RIDESTATUS.AVAILABLE;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        /*this.beginningTime = new Time(0);
        this.endTime = new Time(0);*/
        this.beginningTime = new Date();
        this.endTime = new Date();
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
    }

    //Getters and Setters
    public RIDESTATUS getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(RIDESTATUS rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Date getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(Date beginningTime) {
        this.beginningTime = beginningTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
   /* public Time getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(Time beginningTime) {
        this.beginningTime = beginningTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }*/

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(long idDriver) {
        this.idDriver = idDriver;
    }

    //toString
    @Override
    public String toString() {
        return "Ride{" +
                "id="+id+
                "rideStatus=" + rideStatus +
                ", sourceLocation='" + sourceLocation + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", beginningTime=" + beginningTime +
                ", endTime=" + endTime +
                ", clientName='" + clientName + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ",idDriver='"+idDriver+'\''+
                '}';
    }
    //equals


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return id == ride.id &&
                rideStatus == ride.rideStatus &&
                sourceLocation.equals( ride.sourceLocation) &&
                destinationLocation.equals( ride.destinationLocation) &&
                beginningTime.equals(ride.beginningTime) &&
                endTime.equals(ride.endTime) &&
                clientName.equals(ride.clientName) &&
                clientPhone.equals(ride.clientPhone) &&
                clientEmail.equals(ride.clientEmail)&&
                idDriver==ride.idDriver;
    }
}
