package com.example.shosh.get_taxi_driver.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;
import com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB;
import com.example.shosh.get_taxi_driver.model.entities.Driver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox checkBox;

    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            BackendFactory.getInstance(this);
            setContentView(R.layout.activity_login);
            //set underline to register button
            Button buttonRegister = (Button) findViewById(R.id.register_button);
            buttonRegister.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            buttonRegister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {//onClick of register button take the user to register activity
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            mPasswordView = (EditText) findViewById(R.id.password);
            checkBox = findViewById(R.id.checkBoxStayIn);
            Button SignInButton = (Button) findViewById(R.id.sign_in_button);
            SignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {//onClick on OK button
                    attemptLogin();//check the mail and password
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
            loadStaySignedIn();//share preference
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
    /**
     * Attempts to sign in  the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.)
     */
    private void attemptLogin() {
        try {

            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();


            if (TextUtils.isEmpty(password) && TextUtils.isEmpty(email)) {//if the fields empty
                mEmailView.setError(getString(R.string.error_field_required));
                mPasswordView.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {//if the email empty
                mEmailView.setError(getString(R.string.error_field_required));
            }
            if (!TextUtils.isEmpty(email)) {
                if (!isEmailValid(email))//email invalid
                    mEmailView.setError(getString(R.string.error_invalid_email));
                else {
                    if (TextUtils.isEmpty(password))//if the password empty
                        mPasswordView.setError(getString(R.string.error_field_required));
                    else {

                        if (checkBox.isChecked())//check if to save in share preference
                            staySignedIn(true);
                        else
                            staySignedIn(false);
                        Driver driver = ((DatabaseFB) BackendFactory.getInstance(this)).authenticate(email, password);
                        //get the current driver
                        if (driver != null) {//move the driver to his space
                            Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
                            intent.putExtra("driver", driver);
                            startActivity(intent);//there are not driver with this identify details
                        } else {
                            Toast.makeText(LoginActivity.this, "Try again",
                                    Toast.LENGTH_LONG).show();//message to user
                        }
                    }
                }

            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * this function calls when the driver click on sign in button and the fields of identify details are not empty
     * @param flagIsStaySignedIn contains true if the driver want to save his identify details in share preference.else-contains false.
     */
    private void staySignedIn(boolean flagIsStaySignedIn){
        try {
            SharedPreferences prefs = getSharedPreferences("GeTaxiTemp", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();//edit the file
            if (flagIsStaySignedIn) {
                editor.putString("emailStaySignedIn", mEmailView.getText().toString());//put the email in file
                editor.putString("passwordStaySignedIn", mPasswordView.getText().toString());//put the email in file
                editor.putBoolean("checkboxStaySignedIn", true);//put the checkbox in file
            } else {
                editor.putString("emailStaySignedIn", "");//put the email in file
                editor.putString("passwordStaySignedIn", "");//put the email in file
                editor.putBoolean("checkboxStaySignedIn", false);//put the checkbox in file
            }
            editor.apply();//save the edits
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * this function calls in onCreate. it load the identify details that store in the share preference
     */
    private void loadStaySignedIn(){
        try {
            SharedPreferences prefs = getSharedPreferences("GeTaxiTemp", MODE_PRIVATE);
            mEmailView.setText(prefs.getString("emailStaySignedIn", ""));//load name
            mPasswordView.setText(prefs.getString("passwordStaySignedIn", ""));//load phone
            if (prefs.getBoolean("checkboxStaySignedIn", false))
                checkBox.setChecked(true);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    /**
     * this function calls in onCreate. it load the identify details that store in the share preference
     */
    private boolean isEmailValid(String email) {
        boolean check;
        Pattern p;
        Matcher m;
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";//the email valid format

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();
        return check;
    }

}

