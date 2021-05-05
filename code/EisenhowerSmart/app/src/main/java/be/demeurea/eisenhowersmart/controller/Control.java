package be.demeurea.eisenhowersmart.controller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import be.demeurea.eisenhowersmart.model.Task;

/**
 * The controller
 * Singleton
 * @author Demeure Arnaud
 * @created on 25-01-21
 */
public final class Control extends Observable{

    //Properties
    private static final String TAG = "Control";

    private static Control instance = null;
    private Task task = null;

    //Observable properties
    private final Set<Observer> observers = new HashSet<Observer>();

    //Database
    private static DAOBase localAccess;

    /**
     * Private constructor
     */
    private Control(){
        super();
    }

    /**
     * Public Constructor => always only one instance
     * @param context Context
     * @return Control instance: the only instance of the controller
     */
    public static Control getInstance(Context context){
        assert context != null : "Control.getInstance : context is null.";

        if (Control.instance == null){
            Control.instance = new Control();
            localAccess = new DAOBase(context);
        }
        return Control.instance;
    }

    /**
     * Get the task.
     * @return task Task
     */
    public Task getTask(){
        return task;
    }

    /**
     * Get all the tasks.
     * @return allTasks ArrayList<Task>
     */
    public ArrayList<Task> getAllTasks() {return (ArrayList<Task>) localAccess.getAll();}

    /**
     * Set task.
     * @param newTask Task: new task
     */
    public void setTask(Task newTask){
        assert newTask != null : "Control.setTask : newTask is null.";

        this.task = newTask;
    }

    /**
     * Set singleton's task.
     * @param name String: name of the task
     * @param description String: its description
     * @param deadline Date: its deadline, type Date
     * @param emergency Integer: subjective number that represents the emergency
     * @param importance Integer: subjective number that represents the importance
     * @precondition -10 <= emergency <= 10 and -10 <= importance <= 10
     */
    public void addTask(String name, String description, Date deadline, Double emergency, Double importance){
        assert name != null : "Control.addTask : name is null";
        assert description != null : "Control.addTask : description is null";
        //deadline is optional
        assert emergency != null : "Control.addTask : emergency is null";
        assert importance != null : "Control.addTask : importance is null";

        assert emergency <= 10 : "Control.addTask : emergency is greater than 10.";
        assert emergency >= -10 : "Control.addTask : emergency is smaller than -10.";
        assert importance <= 10 : "Control.addTask : importance is greater than 10.";
        assert importance >= -10 : "Control.addTask : importance is smaller than -10.";

        Task newTask = new Task(name, description, deadline, emergency, importance);

        //Add to database
        localAccess.add(newTask);

        Log.d(TAG, "Task added.");

        //Notify the observers that there are some changes
        notifyObservers();
    }

    /**
     * Delete a task from the list.
     * @param taskToDelete Task: the task to delete
     */
    public void deleteTask(Task taskToDelete) {
        assert taskToDelete != null : "Control.deleteTask : taskToDelete is null.";

        //Delete from database
        localAccess.delete(taskToDelete.getId());

        Log.d(TAG, "Task deleted : " + taskToDelete.getName());

        //Notify the observers that there are some changes
        notifyObservers();
    }

    /**
     * Update the task that has been modified.
     * @param newTask Task: new Task after modification.
     */
    public void updateTask(Task newTask) {
        assert newTask != null : "Control.updateTask : newTask is null.";

        localAccess.delete(task.getId());
        localAccess.add(newTask);

        Log.d(TAG, "Task updated : " + newTask.getName());

        //Notify the observers that there are some changes
        notifyObservers();
    }

    /**
     * Add an observer to this class
     * @param o Observer
     */
    public void addObserver( Observer o){
        assert o != null : "Control.addObserver : new observer is null.";
        observers.add(o);
        Log.d(TAG, "Observer has been added.");
    }

    /**
     * Delete an observer from this class
     * @param o Observer
     */
    public void deleteObserver( Observer o){
        assert o != null : "Control.deleteObserver : observer to delete is null.";
        observers.remove(o);
        Log.d(TAG, "Observer has been deleted.");
    }

    /**
     * Notify all the observers that there might be updates in the class
     */
    public void notifyObservers(){
        Log.d(TAG, "Observers have been notified.");
        for(Observer o: observers){
            o.update(this, null);
        }
    }
}
