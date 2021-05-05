package be.demeurea.eisenhowersmart.view;

import android.app.DatePickerDialog;
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
import be.demeurea.eisenhowersmart.model.Task;
import be.demeurea.eisenhowersmart.utils.CustomPopup;

/**
 * Activity that shows the details of a task
 * @author Demeure Arnaud
 * @created on 07-02-21
 */
public class DetailTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_task);

        init();
    }

    // properties
    private static final String TAG = "DetailTask";

    private EditText txtName;
    private EditText txtDescr;
    private TextView chooseDate;
    private DatePickerDialog.OnDateSetListener datePicker;
    private ColorSeekBar skbEmergency;
    private ColorSeekBar skbImportance;

    private Button btnSave;
    private Button btnCancel;
    private Button btnDelete;

    private Control control;

    int skbMax = 20;
    int skbDiff = skbMax / 2;
    double skbStep = 0.2;

    /**
     * Initialization of links with graphics widgets.
     */
    private void init(){
        //Find the useful widgets
        txtName = (EditText) findViewById(R.id.txtName);
        txtDescr = (EditText) findViewById(R.id.txtDescr);
        chooseDate = (TextView) findViewById(R.id.choose_date);
        skbEmergency = (ColorSeekBar) findViewById(R.id.skbEmergency);
        skbImportance = (ColorSeekBar) findViewById(R.id.skbImportance);

        btnSave = (Button) findViewById(R.id.btnSaveUpdates);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        //Init control Instance
        this.control = Control.getInstance(DetailTask.this);


        initWidgetsValue();

        listenSaveUpdates();
        listenCancel();
        listenDelete();
        listenDate();

        //Make sure that back button = cancel button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                btnCancel.performClick();
            }
        };
        DetailTask.this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Initialize the widgets with the task's attributes
     */
    private void initWidgetsValue() {
        Task task = (Task) control.getTask();

        //Init the widgets
        txtName.setText(task.getName());
        txtDescr.setText(task.getDescription());

        if(task.getDeadline() != null){
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
            chooseDate.setText(s.format(task.getDeadline()));
        }

        //Place the ColorSeekBar's cursor on the right position
        int position = (int) ((control.getTask().getEmergency() + skbDiff) / skbStep);
        skbEmergency.setPosition(position,50);
        position = (int) ((control.getTask().getImportance() + skbDiff) / skbStep);
        skbImportance.setPosition(position,50);
    }

    /**
     * Listen the event of the button "Save updates".
     * @exception  ParseException
     * @exception Exception
     */
    private void listenSaveUpdates() {
        btnSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert v != null : "DetailTask.listenSaveUpdates : view is null.";

                Log.d(TAG, "Save updates button clicked.");

                String name = "";
                String descr = "";
                String deadline = "";
                Double emergency = null;
                Double importance = null;

                //Get the inputs
                name = txtName.getText().toString();
                descr = txtDescr.getText().toString();
                deadline = chooseDate.getText().toString();
                emergency = Math.round((skbEmergency.getColorBarPosition() * skbStep - skbDiff) * 10.0) / 10.0;
                importance = Math.round((skbImportance.getColorBarPosition() * skbStep - skbDiff) * 10.0) / 10.0;

                //Control the user's inputs
                if(name.equals("") || descr.equals("")){
                    Toast.makeText(DetailTask.this, "Name and description cannot be empty.", Toast.LENGTH_LONG).show();
                }else {

                    Date date = null;

                    //Parse the date
                    try {
                        date = new SimpleDateFormat("dd/MM/yyyy").parse(deadline);
                    }catch (ParseException e){Log.d(TAG,"Failed parseDate or no date");}

                    Task t = new Task(name, descr, date, emergency, importance);
                    control.updateTask(t);

                    Toast.makeText(DetailTask.this, "Task has been updated.", Toast.LENGTH_LONG).show();

                    //Back to Matrix activity
                    finish();
                }
            }
        });
    }

    /**
     * Listen to clicks on the button "Cancel".
     */
    private void listenCancel(){
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert v != null : "DetailTask.listenCancel : view is null.";

                Log.d(TAG, "Cancel button clicked.");

                //Build a pop up
                final CustomPopup cancelPopup = new CustomPopup(DetailTask.this);
                cancelPopup.setTitle("Cancel");
                cancelPopup.setMessage("Are you sure you want to leave this page ?");
                cancelPopup.setWarning("Modifications will not be saved.");
                cancelPopup.getYesBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Cancelled.");

                        cancelPopup.dismiss();

                        //Back to  Matrix activity
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
     * Listen to clicks on the button "Delete".
     */
    private void listenDelete() {
        btnDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert v != null : "DetailTask.listenDelete : view is null.";

                Log.d(TAG, "Delete button clicked.");

                //Build a pop up
                final CustomPopup deletePopup = new CustomPopup(DetailTask.this);
                deletePopup.setTitle("Delete");
                deletePopup.setMessage("Are you sure you want to delete this task ?");
                deletePopup.setWarning("This process cannot be undone.");
                deletePopup.getYesBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Deleted.");

                        deletePopup.dismiss();

                        control.deleteTask(control.getTask());

                        Toast.makeText(DetailTask.this, "Task deleted.", Toast.LENGTH_LONG).show();

                        //Back to Matrix activity
                        finish();
                    }
                });
                deletePopup.getNoBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Delete cancelled.");
                        deletePopup.dismiss();
                    }
                });
                deletePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deletePopup.build();
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
                assert v != null : "DetailTask.listenDate: chooseDate: view is null.";

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
                DatePickerDialog dateDialog = new DatePickerDialog(DetailTask.this, R.style.ThemeOverlay_AppCompat_Dialog, datePicker, year, month, day);
                dateDialog.show();
            }
        });

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                assert view != null : "DetailTask.listenDate : DatePicker is null.";

                assert year == 0 : "DetailTask.listenDate : year is ND.";
                assert month >= 0 : "DetailTask.listenDate : month is < 0.";
                assert month <= 11 : "DetailTask.listenDate : month is > 11.";
                assert dayOfMonth >= 0 : "DetailTask.listenDate : dayOfMonth is < 0.";
                assert dayOfMonth <= 31 : "DetailTask.listenDate : dayOfMonth is > 31.";

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
                assert v != null : "DetailTask.listenDate: resetDate: view is null.";

                chooseDate.setText("");
                Log.d(TAG, "Date reset.");
            }
        });
    }
}