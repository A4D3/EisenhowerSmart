package be.demeurea.eisenhowersmart.model;

import java.util.Date;

/**
 * Class that represents an object Task.
 * @author Demeure Arnaud
 * @created on 25-01-21
 */
public class Task {

    //Properties
    private Long id;
    private String name;
    private String description;
    private Date deadline;
    private Double emergency;
    private Double importance;

    /**
     * Constructor of a task.
     * @param name String: name of the task
     * @param description String: its description
     * @param deadline Date: its deadline, type Date
     * @param emergency Double: subjective number that represents the emergency
     * @param importance Double: subjective number that represents the importance
     * @precondition -10 <= emergency <= 10 and -10 <= importance <= 10
     */
    public Task(String name, String description, Date deadline, Double emergency, Double importance) {
        assert name != null : "Task.Constructor: name is null";
        assert description != null : "Task.Constructor: description is null";
        //deadline is optional
        assert emergency != null : "Task.Constructor: emergency is null";
        assert importance != null : "Task.Constructor: importance is null";

        assert emergency <= 10 : "Task.Constructor: emergency is greater than 10.";
        assert emergency >= -10 : "Task.Constructor: emergency is smaller than -10.";
        assert importance <= 10 : "Task.Constructor: importance is greater than 10.";
        assert importance >= -10 : "Task.Constructor: importance is smaller than -10.";

        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.emergency = emergency;
        this.importance = importance;
    }

    /**
     * Second constructor of a task.
     * @param id Long: id of the task in the db
     * @param name String: name of the task
     * @param description String: its description
     * @param deadline Date: its deadline, type Date
     * @param emergency Double: subjective number that represents the emergency
     * @param importance Double: subjective number that represents the importance
     * @precondition -10 <= emergency <= 10 and -10 <= importance <= 10
     */
    public Task(Long id, String name, String description, Date deadline, Double emergency, Double importance) {
        assert id != null : "Task.constructor: id is null.";
        assert name != null : "Task.Constructor: name is null";
        assert description != null : "Task.Constructor: description is null";
        //deadline is optional
        assert emergency != null : "Task.Constructor: emergency is null";
        assert importance != null : "Task.Constructor: importance is null";

        assert emergency <= 10 : "Task.Constructor: emergency is greater than 10.";
        assert emergency >= -10 : "Task.Constructor: emergency is smaller than -10.";
        assert importance <= 10 : "Task.Constructor: importance is greater than 10.";
        assert importance >= -10 : "Task.Constructor: importance is smaller than -10.";

        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.emergency = emergency;
        this.importance = importance;
    }

    //Getters

    /**
     * Get task's id.
     * @return id Long
     */
    public Long getId(){ return id; }

    /**
     * Get task's name.
     * @return name String
     */
     public String getName() {
        return name;
    }

    /**
     * Get task's description.
     * @return description String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get task's deadline.
     * @return deadline Date
     */
    public Date getDeadline() {
        return deadline;
    }

    /**
     * Get task's emergency.
     * @return emergency Double
     */
    public Double getEmergency() {
        return emergency;
    }

    /**
     * Get task's importance.
     * @return importance Double
     */
    public Double getImportance() {
        return importance;
    }

    //Setters

    /**
     * Set the task's id.
     * @param id Long: new id
     */
    public void setId(Long id){
        assert id != null : "setId : id is null.";
        this.id = id;
    }

    /**
     * Set the task's name.
     * @param name String: new name
     */
    public void setName(String name) {
        assert name != null : "setName: name is null";
        this.name = name;
    }

    /**
     * Set the task's description.
     * @param description String: new description
     */
    public void setDescription(String description) {
        assert description != null : "setDescription: description is null";
        this.description = description;
    }

    /**
     * Set the task's deadline.
     * @param deadline Date: new deadline
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * Set the task's emergency.
     * @param emergency Double: new emergency
     * @precondition -10 <= emergency <= 10
     */
    public void setEmergency(Double emergency) {
        assert emergency != null : "Task.setEmergency: emergency is null";
        assert emergency <= 10 : "Task.setEmergency: emergency is greater than 10.";
        assert emergency >= -10 : "Task.setEmergency: emergency is smaller than -10.";
        this.emergency = emergency;
    }

    /**
     * Set the task's importance.
     * @param importance Double: new importance
     * @precondition -10 <= importance <= 10
     */
    public void setImportance(Double importance) {
        assert importance != null : "Task.setImportance: importance is null";
        assert importance <= 10 : "Task.setImportance: importance is greater than 10.";
        assert importance >= -10 : "Task.setImportance: importance is smaller than -10.";
        this.importance = importance;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) &&
                description.equals(task.description) &&
                deadline.equals(task.deadline) &&
                emergency.equals(task.emergency) &&
                importance.equals(task.importance);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + name.hashCode();
        hash = hash * 31 + description.hashCode();
        hash = (int) Math.round(hash * 13 + emergency);
        hash = (int) Math.round(hash * 23 + importance);
        return hash;
    }
}
