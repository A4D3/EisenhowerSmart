package be.demeurea.eisenhowersmart.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import be.demeurea.eisenhowersmart.R;
import be.demeurea.eisenhowersmart.controller.Control;
import be.demeurea.eisenhowersmart.utils.CustomPopup;

/**
 * Activity that allows to add a Task.
 * @author Demeure Arnaud
 * @created on 25-01-21
 */
public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        Log.d(TAG,"Activity created.");

        init();
    }

    // properties
    private static final String TAG = "AddTask";

    Intent intent;

    private EditText txtName;
    private EditText txtDescr;
    private TextView chooseDate;
    private DatePickerDialog.OnDateSetListener datePicker;
    private ColorSeekBar skbEmergency;
    private ColorSeekBar skbImportance;

    private Button btnConfirm;
    private Button btnCancel;

    private Control control;

    int skbMax = 20;
    int skbDiff = skbMax / 2;
    double skbStep = 0.2;

    /**
     * Initialization of links with graphics widgets.
     */
    private void init(){
        //Get intent from EisenhowerMatrix class
        intent = getIntent();

        //Find the useful widgets
        txtName = (EditText) findViewById(R.id.txtName);
        txtDescr = (EditText) findViewById(R.id.txtDescr);
        chooseDate = (TextView) findViewById(R.id.choose_date);
        skbEmergency = (ColorSeekBar) findViewById(R.id.skbEmergency);
        skbImportance = (ColorSeekBar) findViewById(R.id.skbImportance);

        skbEmergency.setPosition(50,50);
        skbImportance.setPosition(50,50);

        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        //Get control instance
        this.control = Control.getInstance(AddTask.this);

        listenConfirm();
        listenCancel();
        listenDate();
        initSeekBars();

        //Make sure that back button = cancel button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                btnCancel.performClick();
            }
        };
        AddTask.this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Listen the event of the button "Confirm"
     */
    private void listenConfirm(){
        btnConfirm.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                assert v != null : "AddTask.listenConfirm : view is null.";

                Log.d(TAG, "Confirm button clicked.");

                String name = "";
                String descr = "";
                String deadline = "";
                Double emergency = null;
                Double importance = null;

                //Get the inputs
                name = txtName.getText().toString();
                descr = txtDescr.getText().toString();
                deadline = chooseDate.getText().toString();
                Log.d(TAG, deadline);
                emergency = Math.round((skbEmergency.getColorBarPosition() * skbStep - skbDiff) * 10.0) / 10.0;
                importance = Math.round((skbImportance.getColorBarPosition() * skbStep - skbDiff) * 10.0) / 10.0;

                //Control the user's inputs
                if(name.equals("") || descr.equals("")){
                    Toast.makeText(AddTask.this, "Name and description cannot be empty.", Toast.LENGTH_LONG).show();
                }else{

                    //Create Task
                    create_task(name, descr, deadline, emergency, importance);

                    Toast.makeText(AddTask.this, "Task has been added.", Toast.LENGTH_LONG).show();

                    //Back to Matrix activity
                    finish();
                }

            }
        });

    }

    /**
     * Listen the event of the button "Cancel".
     */
    private void listenCancel() {
       btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert v != null : "AddTask.listenCancel : view is null.";

                Log.d(TAG, "Cancel button clicked.");

                //Build a pop up
                final CustomPopup cancelPopup = new CustomPopup(AddTask.this);
                cancelPopup.setTitle("Cancel");
                cancelPopup.setMessage("Are you sure you want to leave this page ?");
                cancelPopup.setWarning("Modifications will not be saved.");
                cancelPopup.getYesBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Cancelled.");

                        cancelPopup.dismiss();

                        //Back to Matrix activity
                        finish();
                    }
                });
                cancelPopup.getNoBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Cancel cancelled.");
                        cancelPopup.dismiss();
                    }
                });
                cancelPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cancelPopup.build();
            }
        });
    }

    /**
     * Listen if the user click on "Choose a date" to choose the deadline.
     */
    private void listenDate() {
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert v != null : "AddTask.listenDate: chooseDate: view is null.";

                int year, month, day;
                if(chooseDate.getText().length() == 0) {
                    //Get today's date
                    Calendar cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                }else{
                    //Set the date with the one that already exists
                    String date = chooseDate.getText().toString();
                    year = Integer.parseInt(date.substring(6));
                    month = Integer.parseInt(date.substring(3,5)) - 1;
                    day = Integer.parseInt(date.substring(0,2));
                }

                //Create and show the DatePickerDialog
                DatePickerDialog dateDialog = new DatePickerDialog(AddTask.this, R.style.ThemeOverlay_AppCompat_Dialog, datePicker, year, month, day);
                dateDialog.show();
            }
        });

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                assert view != null : "AddTask.listenDate : DatePicker is null.";

                assert year == 0 : "AddTask.listenDate : year is ND.";
                assert month >= 0 : "AddTask.listenDate : month is < 0.";
                assert month <= 11 : "AddTask.listenDate : month is > 11.";
                assert dayOfMonth >= 0 : "AddTask.listenDate : dayOfMonth is < 0.";
                assert dayOfMonth <= 31 : "AddTask.listenDate : dayOfMonth is > 31.";

                //Set the text with the chosen date
                month += 1;
                String date = (dayOfMonth < 10 ? "0" : "") + dayOfMonth + (month < 10 ? "/0" : "/") + month + "/" + year;
                chooseDate.setText(date);

                Log.d(TAG, "Chosen date: " + date);
            }
        };

        //Listen if the user wants to reset the date
        ((TextView) findViewById(R.id.resetDate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert v != null : "AddTask.listenDate: resetDate: view is null.";

                chooseDate.setText("");
                Log.d(TAG, "Date reset.");
            }
        });
    }

    /**
     * Get The data send by EisenhowerMatrix class and init seekbars with it.
     */
    private void initSeekBars() {

        double xValue = intent.getDoubleExtra("xValue", 0);
        double yValue = intent.getDoubleExtra("yValue", 0);

        Log.d(TAG, "Data received : x = " + xValue + " | y = " + yValue);

        //Place the ColorSeekBar's cursor on the right position
        int position = (int) ((xValue + skbDiff) / skbStep);
        skbEmergency.setPosition(position,50);
        position = (int) ((yValue + skbDiff) / skbStep);
        skbImportance.setPosition(position,50);
    }

    /**
     * Create a Task in the controller.
     * @param name String: name of the task
     * @param descr String: its description
     * @param deadline String: its deadline, format = "dd/MM/yyyy"
     * @param emergency Integer: subjective number that represents the urgency
     * @param importance Integer: subjective number that represents the importance
     * @exception ParseException
     * @precondition -10 <= emergency <= 10 and -10 <= importance <= 10
     */
    private void create_task(String name, String descr, String deadline, Double emergency, Double importance){
        assert name != null : "AddTask.create_task: name is null";
        assert descr != null : "AddTask.create_task: description is null";
        //deadline is optional
        assert emergency != null : "AddTask.create_task: emergency is null";
        assert importance != null : "AddTask.create_task: importance is null";

        assert emergency <= 10 : "AddTask.create_task: emergency is greater than 10.";
        assert emergency >= -10 : "AddTask.create_task: emergency is smaller than -10.";
        assert importance <= 10 : "AddTask.create_task: importance is greater than 10.";
        assert importance >= -10 : "AddTask.create_task: importance is smaller than -10.";

        Date date = null;

        //Parse the date
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(deadline);
        }catch (ParseException e){Log.d(TAG,"Failed parseDate or no date");}

        Log.d(TAG, "New Task added.");

        //Add the task to tasks
        control.addTask(name, descr, date, emergency, importance);

    }

}