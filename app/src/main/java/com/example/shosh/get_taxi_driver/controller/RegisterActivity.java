package com.example.shosh.get_taxi_driver.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.Backend;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;
import com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB;
import com.example.shosh.get_taxi_driver.model.entities.Driver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    Backend instance;

    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            Button buttonAdd = (Button) findViewById(R.id.add_button);
            buttonAdd.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        instance = BackendFactory.getInstance(getApplicationContext());//get the instance of Backend
                        //get all widgets from view
                        final EditText firstName = (EditText) findViewById(R.id.firstNameText);
                        final EditText lastName = (EditText) findViewById(R.id.lastNameText);
                        final EditText id = (EditText) findViewById(R.id.idText);
                        final EditText email = (EditText) findViewById(R.id.emailText);
                        final EditText phone = (EditText) findViewById(R.id.phoneText);
                        final EditText creditCard = (EditText) findViewById(R.id.creditCardNumberText);
                        final EditText password = (EditText) findViewById(R.id.passwordText);
                        if (validate(firstName, lastName, id, email, phone, creditCard, password)) {//check if the new driver details valid
                            final Driver driver = new Driver(lastName.getText().toString(), firstName.getText().toString(), Long.parseLong(id.getText().toString()), phone.getText().toString(), email.getText().toString()
                                    , Long.parseLong(creditCard.getText().toString()), password.getText().toString());//the new driver
                            new AsyncTask<Context, Void, Void>() {//open "thread" to add data to database
                                /**
                                 * this function start the thread action
                                 *
                                 * @param contexts the context of the activity
                                 * @return void
                                 */
                                @Override
                                protected Void doInBackground(Context... contexts) {//add the new driver to firebase bt AsyncTask
                                    try {
                                        instance.addDriver(driver);//add to database
                                        return null;
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                        return null;
                                    }
                                }

                            }.execute();//run thread

                            //clear the fields to the next details entering
                            firstName.setText("");
                            lastName.setText("");
                            id.setText("");
                            email.setText("");
                            phone.setText("");
                            creditCard.setText("");
                            password.setText("");
                            Intent intent = new Intent(RegisterActivity.this, DriverActivity.class);//go to the driver space
                            intent.putExtra("driver", driver);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    // }
                }
            });

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * this function return True if the email address in the email EditText is valid
     * @param email EditText that contain the email address for checking
     * @return boolean if the email address is valid
     */
    private boolean isValidEmail(EditText email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";//the email valid format

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email.getText().toString());
        check = m.matches();

        if (!check) {
            email.setError("Not Valid Email");
        }
        return check;
    }

    /**
     * this function return True if the phone number in the phone EditText is valid
     * @param phone EditText that contain the phone number for checking
     * @return boolean if the phone number is valid
     */
    private boolean isValidMobile(EditText phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone.getText().toString())) {//there is only numbers
            if (phone.getText().toString().length() < 6 || phone.getText().toString().length() > 13) {//check if the number of digits valid
                check = false;
                phone.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    /**
     * this function return True if the credit card number in the creditCard EditText is valid
     * @param creditCard EditText that contain the credit card number for checking
     * @return boolean if the credit card number is valid
     */
    private boolean isValidCreditCard(EditText creditCard) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", creditCard.getText().toString())) {//there is only numbers
            if (creditCard.getText().toString().length() < 16) {//check if the number of digits valid
                check = false;
                creditCard.setError("Not Valid Card Number");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }
    /**
     * this function return True if the new driver identify details(id and email) are not alreadt exist in firebase
     * @param id of new driver
     * @param email of new driver
     * @return boolean if the credit card number is valid
     */
    public boolean identification(long id, String email) {
        try {
            for (Driver d : ((DatabaseFB) instance).getAllDrivers()) {
                if (d.getId() == id && d.getMailAddress().equals(email)) {
                    Toast.makeText(RegisterActivity.this, "User already exist.Check user Id and Email",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (d.getId() == id) {
                    Toast.makeText(RegisterActivity.this, "User already exist.Check user Id",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                if (d.getMailAddress().equals(email)) {
                    Toast.makeText(RegisterActivity.this, "User already exist.Check user Email",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * this function return True if the input from the user is valid (all the fields)
     */
    private boolean validate(EditText firstName, EditText lastName, EditText id, EditText email, EditText phone, EditText creditCard, EditText password) {
       try {
           if (firstName.getText().toString().length() == 0 || lastName.getText().toString().length() == 0 || id.getText().toString().length() == 0 ||
                   email.getText().toString().length() == 0 || phone.getText().toString().length() == 0 || creditCard.getText().toString().length() == 0 || password.getText().toString().length() == 0)//if there is empty field
           {
               Toast.makeText(RegisterActivity.this, "You have to fill all the fields",
                       Toast.LENGTH_LONG).show();
               return false;
           }
           boolean flag = true;
           if (!isValidCreditCard(creditCard)) flag = false;
           if (!isValidEmail(email)) flag = false;
           if (!isValidMobile(phone)) flag = false;
           if (!identification(Long.parseLong(id.getText().toString()), email.getText().toString()))
               flag = false;
           return flag;
       }
       catch (Exception e)
       {
           System.out.println(e.getMessage());
           return false;
       }
    }
}

