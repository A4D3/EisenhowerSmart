package be.demeurea.eisenhowersmart.model;

import com.jjoe64.graphview.series.DataPointInterface;

/**
 * Customed DataPoint.There is a task associated with it.
 * @author Demeure Arnaud
 * @created on 10-02-21
 */
public class TaskPoint implements DataPointInterface {

    private double x;
    private double y;
    private Task task;

    /**
     * Constructor
     * @param x double: x-value
     * @param y double: y-value
     * @param t Task: task to associate with the point.
     * @precondition -10 <= x <= 10 and -10 <= y <= 10
     */
    public TaskPoint(double x, double y, Task t) {
        assert t != null : "TaskPoint.Constructor: task is null.";
        assert x <= 10 : "TaskPoint.Constructor: x is greater than 10.";
        assert x >= -10 : "TaskPoint.Constructor: x is smaller than -10.";
        assert y <= 10 : "TaskPoint.Constructor: y is greater than 10.";
        assert y >= -10 : "TaskPoint.Constructor: y is smaller than -10.";

        this.x=x;
        this.y=y;
        this.task=t;
    }


    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    /**
     * Get the task associated with the DataPoint.
     * @return task Task
     */
    public Task getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "TaskPoint{" +
                "x=" + x +
                ", y=" + y +
                ", task=" + task.getName() +
                '}';
    }
}
