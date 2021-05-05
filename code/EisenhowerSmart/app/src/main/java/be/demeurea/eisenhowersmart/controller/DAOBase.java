package be.demeurea.eisenhowersmart.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.demeurea.eisenhowersmart.model.Task;
import be.demeurea.eisenhowersmart.utils.DatabaseHandler;

/**
 * @author Demeure Arnaud
 * @created on 27-02-21
 */
public class DAOBase {

    //Properties
    private final static int VERSION = 1;
    private final static String NAME = "tasks.sqlite";
    private DatabaseHandler mHandler;
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param pContext Context
     */
    public DAOBase(Context pContext) {
        assert pContext != null : "DAOBase.Constructor: context is null.";
        this.mHandler = new DatabaseHandler(pContext, NAME, null, VERSION);
    }

    /**
     * Add a task to the db.
     * @param nTask Task: new Task
     */
    public void add(Task nTask){
        assert nTask != null : "DAOBase.add: nTask is null.";

        db = mHandler.getWritableDatabase();

        //Create the request
        String addRequest = "INSERT INTO Task (name,description,deadline,emergency,importance) VALUES ";
        addRequest += "(\"" + nTask.getName() + "\",\"" + nTask.getDescription() + "\",";

        if(nTask.getDeadline() == null){
             addRequest += "NULL,";
         }else{
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
             addRequest += "\"" + s.format(nTask.getDeadline()) + "\",";
         }
         addRequest += nTask.getEmergency() + "," + nTask.getImportance() + ")";

         //execute the request
         db.execSQL(addRequest);
    }

    /**
     * Delete a Task from the database.
     * @param id Long: Task's id
     */
    public void delete(Long id){
        assert id != null: "DAOBAse.delete: id is null.";

        db = mHandler.getWritableDatabase();
        db.delete(DatabaseHandler.TASK_TABLE_NAME, DatabaseHandler.TASK_KEY + " = ?", new String[] {String.valueOf(id)});
    }

    /**
     * Get all the Tasks from the database.
     * @return allTasks ArrayList<Task>
     */
    public ArrayList<Task> getAll(){
        List<Task> allTasks = new ArrayList<Task>();

        //Get all the Tasks
        db = mHandler.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Task", new String[]{});

        //One task at a time
        while (c.moveToNext()) {
            //Get task's attributes
            Long id = c.getLong(0);
            String name = c.getString(1);
            String description = c.getString(2);
            //Parse the date
            Date deadline = null;
            if (c.getString(3) != null) {
                try {
                    deadline = new SimpleDateFormat("dd/MM/yyyy").parse(c.getString(3));
                } catch (ParseException e) {
                }
            }

            Double emergency = c.getDouble(4);
            Double importance = c.getDouble(5);

            Task t = new Task(id,name,description,deadline,emergency,importance);

            //Update the ArrayList
            allTasks.add(t);

        }
        c.close();

        return (ArrayList<Task>) allTasks;
    }
}

