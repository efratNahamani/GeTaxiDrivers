package com.example.shosh.get_taxi_driver.controller;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;

import java.util.Calendar;
import java.util.Date;


public class filterMyRidesFragment extends Fragment {

    DatePickerDialog dateDialog;//the date picker filter
    DatePickerDialog.OnDateSetListener setDateListener;//event when the user choose date
    AlertDialog.Builder builderDialog;//builder of city dialog
    AlertDialog cityDialog;//the dialog that open when the user choose the city filter option
    View view;//view of this fragment
    Date filterDate;//the variable that take the selected date
    EditText cityEditTextDialog;//the editText that the user enter selected city in
    String inputFilter;//the variable that take the selected city
    AlertDialog.OnClickListener positiveListener;//event to the OK button in cityDialog
    AlertDialog.OnClickListener negativeListener;//event to the CANCEL button in cityDialog


    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //the city dialog
            builderDialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.dialog_text, null);//put the view from dialog_text XML file
            cityEditTextDialog = (EditText) view.findViewById(R.id.edittext_dialog);//take the city input editText
            positiveListener = new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//onClick OK button implement
                    try {
                        inputFilter = cityEditTextDialog.getText().toString();//get the city that enter in cityEditTextDialog
                        if (inputFilter.length() == 0) {//if there is ""
                        } else {//if there is a city
                            cityDialog.dismiss();//close the dialog
                            cityEditTextDialog.setText("");//clean the editText cityEditTextDialog
                            MyRidesListFragment myRidesListFragment = (MyRidesListFragment) getActivity().getFragmentManager().findFragmentByTag("myRidesListFragmentTag");//get the fragment of my ride that visible right now
                            myRidesListFragment.updateListView(BackendFactory.getInstance(getActivity()).getAllRidesInCity(inputFilter, ((DriverActivity) getActivity()).getCurrentDriver()));//update the list of my ride list fragment to a list that include only rides that their sourceLocation is the inputFilter city.

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            };
            negativeListener = new AlertDialog.OnClickListener() {//onClick CANCEL button implement
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    inputFilter = "";//clear the input String
                    cityDialog.dismiss();//close dialog
                    cityEditTextDialog.setText("");//create the dialog
                }
            };
            builderDialog.setView(view);
            builderDialog.setPositiveButton("OK", positiveListener);
            builderDialog.setNegativeButton("CANCEL", negativeListener);
            cityDialog = builderDialog.create();

            //the date dialog
            Calendar cal = Calendar.getInstance();
            //get current date
            final int todayYear = cal.get(Calendar.YEAR);
            final int todayMonth = cal.get(Calendar.MONTH);
            final int today = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            Calendar calS = Calendar.getInstance();
            //default date
            calS.set(2000, 1, 1, hour, minute);
            Date d = new Date();
            d.setTime(calS.getTimeInMillis());//the the date d is the date of now
            setDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {//action that will be done when the user select date
                    filterDate = new Date(year - 1900, month, dayOfMonth, 12, 12);// datepicker add 1900 years (we dont know why), so we sub 1900 from the years
                    filterDate = new Date(year - 1900, month, dayOfMonth, 12, 12);// the selected date.datepicker add 1900 years (we dont know why), so we sub 1900 from the years
                    try {
                        MyRidesListFragment myRidesListFragment1 = (MyRidesListFragment) getActivity().getFragmentManager().findFragmentByTag("myRidesListFragmentTag");//get the fragment of my ride that visible right now
                        myRidesListFragment1.updateListView(BackendFactory.getInstance(getActivity()).getAllRidesByDate(filterDate, ((DriverActivity) getActivity()).getCurrentDriver()));//update the list of my ride list fragment to a list that include only rides that in beginningTime is the same as filterDate(only the date-without hours).
                        dateDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                setDateListener, todayYear, todayMonth, today);//put in date picker the date of now to the next time it open
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                    }
                }
            };
            dateDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setDateListener, todayYear, todayMonth, today);//put in date picker the date of now.
            // set the range of date that can be selected
            dateDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dateDialog.getDatePicker().setMinDate(d.getTime());

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
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
        View viewFrag = inflater.inflate(R.layout.fragment_filter_my_rides, container, false);//the filter fragment view

        try {
            final Spinner filter_search = (Spinner) viewFrag.findViewById(R.id.filter_myRides);//filter spinner
            String[] optionList = {"Filter By","Date", "City","No Filter"};//spinner options

            ArrayAdapter<String> adap = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, optionList) {//the adapter of the spinner options
                @Override
                public boolean isEnabled(int position) {//the first option of-"Filter by"
//will be unclickable
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }
            };

            filter_search.setAdapter(adap);//put list in spinner

            filter_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {//action that will be done when the user click on element in spinner listView
                    int item = filter_search.getSelectedItemPosition();//the selected item place in list
                    switch (item) {
                        case 0://Filter by option filter
                            break;
                        case 1://Date option filter
                            dateDialog.show();//open dialog with date picker and "set" button, when the user click set, the filter date
                            //field of this class will contain the input
                            ((myRideDetailsFragment)getActivity().getFragmentManager().findFragmentByTag("myRideDetailsFragmentTag")).refresh(); //refresh the details with empty strings
                            break;
                        case 2://City option filter
                            cityEditTextDialog.setHint("City       ");
                            cityEditTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);// changes the type of the edit text to only number
                            cityDialog.show();
                            cityEditTextDialog.setText("");
                            ((myRideDetailsFragment)getActivity().getFragmentManager().findFragmentByTag("myRideDetailsFragmentTag")).refresh(); //refresh the details with empty strings
                            break;
                        case 3://No filter option filter
                            try{
                                MyRidesListFragment myRidesListFragment2=(MyRidesListFragment)getActivity().getFragmentManager().findFragmentByTag("myRidesListFragmentTag");//get the fragment of my ride that visible right now
                                myRidesListFragment2.updateListView(BackendFactory.getInstance(getActivity()).getAllDriverRides(((DriverActivity)getActivity()).getCurrentDriver()));}//update the list of my ride list fragment to a list that include all the rides of the current driver.
                            catch (Exception e){}
                            ((myRideDetailsFragment)getActivity().getFragmentManager().findFragmentByTag("myRideDetailsFragmentTag")).refresh(); //refresh the details with empty strings
                            break;
                    }

                }

                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            return viewFrag;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return viewFrag;
        }
    }
}
