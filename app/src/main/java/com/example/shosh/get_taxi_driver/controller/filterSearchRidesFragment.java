package com.example.shosh.get_taxi_driver.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;
import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //filterSearchRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link //filterSearchRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class filterSearchRidesFragment extends Fragment {
    AlertDialog.Builder builderDialog;//builder of city and distance dialog
    AlertDialog dialog;//city and distance dialog
    AlertDialog.OnClickListener positiveListener;//event to the OK button in cityDialog
    AlertDialog.OnClickListener negativeListener;//event to the CANCEL button in cityDialog
    String inputFilter;//the variable that take the selected city/distance in KM
    EditText editTextDialog;//the editText that the user enter selected city in/distance
    View view;//view of this fragment
    boolean cityFlag=true;//true-if the input is city.else, false
    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            builderDialog = new AlertDialog.Builder(getActivity());//dialog builder
            LayoutInflater inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.dialog_text, null);//view of dialog according to dialog_text xml file
            editTextDialog = (EditText) view.findViewById(R.id.edittext_dialog);//the input edit text of the dialog
            positiveListener = new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//implement onClick of OK button
                    inputFilter = editTextDialog.getText().toString();//get the input
                    if (inputFilter.length() == 0) {//if the input is ""
                    } else {
                        dialog.dismiss();//close dialog
                        editTextDialog.setText("");//clear editText
                        if (cityFlag) {//if the input is city
                            try {
                                SearchRidesFragment searchRidesFragment = (SearchRidesFragment) getActivity().getFragmentManager().findFragmentByTag("searchRidesListFragmentTag");//get the fragment that show the list(with listView)
                                searchRidesFragment.updateListView(BackendFactory.getInstance(getActivity()).getAllAvailableRidesInCity(inputFilter));//put in this fragment only rides that their source location in this city
                            } catch (Exception e) {
                                System.out.print(e.getMessage());
                            }
                        } else {//if the input is distance in KM
                            try {
                                SearchRidesFragment searchRidesFragment = (SearchRidesFragment) getActivity().getFragmentManager().findFragmentByTag("searchRidesListFragmentTag");//get the fragment that show the list(with listView)
                                searchRidesFragment.updateListView(BackendFactory.getInstance(getActivity()).getAllAvailableRidesByDistance(((DriverActivity) getActivity()).getCurrentAddress(), Float.parseFloat(inputFilter)));
                                //put in this fragment only rides that their source location is max distance from the current driver location it's the input
                            } catch (Exception e) {
                                System.out.print(e.getMessage());
                            }
                        }
                    }
                }
            };
            negativeListener = new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//implement onClick of CANCEL button
                    inputFilter = "";//clear
                    dialog.dismiss();//close
                    editTextDialog.setText("");//clear
                }
            };
            //create the dialog view
            builderDialog.setView(view);
            builderDialog.setPositiveButton("OK", positiveListener);
            builderDialog.setNegativeButton("CANCEL", negativeListener);
            dialog = builderDialog.create();
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
        final View viewFrag= inflater.inflate(R.layout.fragment_filter_search_rides, container, false);
        try {
            final Spinner filter_search = (Spinner) viewFrag.findViewById(R.id.filter_search);//filter spinner
            String[] optionList = {"Filter By", "City", "Distance(km)", "No Filter"};//filter options

            ArrayAdapter<String> adap = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, optionList) {//the adapterof spinner options
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
                    int item = filter_search.getSelectedItemPosition();
                    switch (item) {
                        case 0://Filter by option
                            break;
                        case 1://city option filter
                            editTextDialog.setHint("City         ");//put hint
                            editTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);// changes the type of the edit text to only text
                            cityFlag = true;//it's city input
                            dialog.show();//open dialog with edit text and "ok" button, ehwn the user click ok, the editTextDialog
                            editTextDialog.setText("");
                            ((RideDetailsFragment) getActivity().getFragmentManager().findFragmentByTag("RideDetailsFragmentTag")).refresh(); //refresh the details with empty strings
                            break;
                        case 2://Distance option filter
                            try {
                                editTextDialog.setHint("Distance (km)");//put hint
                                editTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);// changes the type of the edit text to only number
                                cityFlag = false;//it's distance input
                                dialog.setView(view);
                                dialog.show();//open dialog
                                editTextDialog.setText("");
                                ((RideDetailsFragment) getActivity().getFragmentManager().findFragmentByTag("RideDetailsFragmentTag")).refresh(); //refresh the details with empty strings
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 3://No filter option filter
                            try {
                                SearchRidesFragment searchRidesListFragment1 = (SearchRidesFragment) getActivity().getFragmentManager().findFragmentByTag("searchRidesListFragmentTag");//get the listView fragment
                                searchRidesListFragment1.updateListView(BackendFactory.getInstance(getActivity()).getAllAvailableRides());//put all the available rides in list view
                            } catch (Exception e) {
                            }
                            ((RideDetailsFragment) getActivity().getFragmentManager().findFragmentByTag("RideDetailsFragmentTag")).refresh(); //refresh the details with empty strings
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
