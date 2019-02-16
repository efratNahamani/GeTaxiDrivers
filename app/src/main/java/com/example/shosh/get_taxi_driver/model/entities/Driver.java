package com.example.shosh.get_taxi_driver.model.entities;

import java.io.Serializable;

public class Driver implements Serializable {

    protected String lastName;
    protected String firstName;
    protected long id;
    protected String phoneNum;
    protected String mailAddress;
    protected long creditCard;
    protected String password;
    protected boolean isStarted;


    //constructors
    public Driver() {
        lastName="";
        firstName="";
        id=0;
        phoneNum="";
        mailAddress="";
        creditCard=0;
        isStarted=false;
    }

    public Driver(String lastName, String firstName, long id, String phoneNum, String mailAddress, long creditCard,String password) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;
        this.phoneNum = phoneNum;
        this.mailAddress = mailAddress;
        this.creditCard = creditCard;
        this.password=password;
        this.isStarted=false;
    }
    //Getters and Setters

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public boolean getIsStarted() {
        return isStarted;
    }

    public void setIsStarted(boolean started) {
        isStarted = started;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setEmailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public long getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(long creditCard) {
        this.creditCard = creditCard;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    //toString
    @Override
    public String toString() {
        return "Driver{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", id=" + id +
                ", phoneNum='" + phoneNum + '\'' +
                ", emailAddress='" + mailAddress + '\'' +
                ", creditCard=" + creditCard +
                ", password="+password+", isStarted="+isStarted+
                '}';
    }
    //equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id &&
                isStarted==driver.isStarted&&
                creditCard == driver.creditCard &&
                lastName.equals(driver.lastName) &&
                firstName.equals(driver.firstName) &&
                phoneNum.equals(driver.phoneNum) &&
                mailAddress.equals(driver.mailAddress)&&
                password.equals(driver.password);
    }
}

