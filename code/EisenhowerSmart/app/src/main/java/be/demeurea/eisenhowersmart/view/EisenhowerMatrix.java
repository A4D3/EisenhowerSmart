package be.demeurea.eisenhowersmart.view;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.demeurea.eisenhowersmart.R;
import be.demeurea.eisenhowersmart.controller.Control;
import be.demeurea.eisenhowersmart.model.Task;
import be.demeurea.eisenhowersmart.model.TaskPoint;

/**
 * Activity where the matrix is.
 * @author Demeure Arnaud
 * @created on 25-01-21
 */
public class EisenhowerMatrix extends AppCompatActivity implements Observer{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eisenhower_matrix);

        Log.d(TAG, "Activity created.");

        init();
    }

    //Properties
    private static final String TAG = "EisenhowerMatrix";
    Control control;

    GraphView graphView;
    PointsGraphSeries<TaskPoint> seriesDo;
    PointsGraphSeries<TaskPoint> seriesDecide;
    PointsGraphSeries<TaskPoint> seriesDelegate;
    PointsGraphSeries<TaskPoint> seriesDelete;

    boolean doubleClick;
    Handler doubleHandler;

    /**
     * Initialize the matrix and the class' properties.
     */
    private void init() {
        //Get control instance
        this.control = Control.getInstance(EisenhowerMatrix.this);

        //Init other variables: used to handle a double tap
        doubleClick = false;
        doubleHandler = new Handler(Looper.getMainLooper());

        //Add the class to an observer of Control
        control.addObserver(this);

        //Init Graph
        graphView = (GraphView)findViewById(R.id.graph);

        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Emergency");
        gridLabel.setVerticalAxisTitle("Importance");

        updateSeries();

        //Set the graph's limits
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);

        graphView.getViewport().setMinX(-10);
        graphView.getViewport().setMaxX(11);
        graphView.getViewport().setMinY(-10);
        graphView.getViewport().setMaxY(10.5);

        //zoom and scroll
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        listenDoubleTap();
    }

    /**
     * Get all the tasks, put them in the right quadrant(series) and add them to the matrix.
     */
    private void updateSeries() {
        //Remove the series from the graph and redraw the correct one
        graphView.removeAllSeries();

        //Make the zoom system more optimal
        //Without that, we can't zoom wherever we want and we are not able to zoom out to the initial view
        PointsGraphSeries<DataPoint> graphLimit;
        graphLimit = new PointsGraphSeries<>(new DataPoint[]{
                new DataPoint(11, 10.5),
        });
        graphLimit.setColor(Color.WHITE);
        graphLimit.setSize(1);
        graphView.addSeries(graphLimit);

        PointsGraphSeries<DataPoint> graphLimit1;
        graphLimit1 = new PointsGraphSeries<>(new DataPoint[]{
                new DataPoint(-10, -10),
        });
        graphLimit1.setColor(Color.WHITE);
        graphLimit1.setSize(1);
        graphView.addSeries(graphLimit1);

        //Reset an eventual zoom made previously
        graphView.getViewport().setMinX(-10);
        graphView.getViewport().setMaxX(11);
        graphView.getViewport().setMinY(-10);
        graphView.getViewport().setMaxY(10.5);

        //Get all the Tasks
        List<Task> tasks = control.getAllTasks();
        Log.d(TAG, "Get all the tasks.");

        List<TaskPoint> listDo = new ArrayList<>();
        List<TaskPoint> listDecide = new ArrayList<>();
        List<TaskPoint> listDelegate = new ArrayList<>();
        List<TaskPoint> listDelete = new ArrayList<>();

        //To know if we have to add (again) the listenTap and the custom shapes on the series
        boolean serieWasNull = false;

        //Divide the tasks according to their importance and emergency
        for(Task t: tasks){
            TaskPoint tp = new TaskPoint(t.getEmergency(), t.getImportance(), t);

            if(t.getEmergency() >= 0){
                if(t.getImportance() >= 0){
                    listDo.add(tp);
                }else{
                    listDelegate.add(tp);
                }
            }else{
                if(t.getImportance() >= 0){
                    listDecide.add(tp);
                }else{
                    listDelete.add(tp);
                }
            }
        }

        //Important and urgent
        TaskPoint[] arrayDo = new TaskPoint[listDo.size()];
        arrayDo = listDo.toArray(arrayDo);
        if(seriesDo == null) {
            seriesDo = new PointsGraphSeries<>(arrayDo);
            serieWasNull = true;
        }else{
            seriesDo.resetData(arrayDo);
        }

        graphView.addSeries(seriesDo);

        //Important and not urgent
        TaskPoint[] arrayDecide = new TaskPoint[listDecide.size()];
        arrayDecide = listDecide.toArray(arrayDecide);
        if(seriesDecide == null) {
            seriesDecide = new PointsGraphSeries<>(arrayDecide);
            serieWasNull = true;
        }else{
            seriesDecide.resetData(arrayDecide);
        }

        graphView.addSeries(seriesDecide);

        //Not important and urgent
        TaskPoint[] arrayDelegate = new TaskPoint[listDelegate.size()];
        arrayDelegate = listDelegate.toArray(arrayDelegate);
        if(seriesDelegate == null) {
            seriesDelegate = new PointsGraphSeries<>(arrayDelegate);
            serieWasNull = true;
        }else{
            seriesDelegate.resetData(arrayDelegate);
        }

        graphView.addSeries(seriesDelegate);

        //Not important and not urgent
        TaskPoint[] arrayDelete = new TaskPoint[listDelete.size()];
        arrayDelete = listDelete.toArray(arrayDelete);
        if(seriesDelete == null) {
            seriesDelete = new PointsGraphSeries<>(arrayDelete);
            serieWasNull = true;
        }else{
            seriesDelete.resetData(arrayDelete);
        }

        graphView.addSeries(seriesDelete);

        Log.d(TAG, "Series added to the graph.");

        //If one serie was null before the call of this function we have to add these properties to the new series
        if(serieWasNull){
            setCustomShape();
            listenSeriesTap();
        }
    }

    /**
     * Set a customed shape on the series
     */
    private void setCustomShape() {
        //Custom shape to show a circle and the name of the task
        seriesDo.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPointInterface) {
                TaskPoint tp = (TaskPoint) dataPointInterface;

                paint.setColor(Color.BLACK);
                paint.setTextSize(38);
                String name = tp.getTask().getName().length() > 10 ? tp.getTask().getName().substring(0,10) : tp.getTask().getName();
                canvas.drawText(name, x - (( name.length() / 2) * (tp.getTask().getEmergency() > 9 ? 40 : 18)), y-32, paint);
                paint.setColor(Color.RED);
                canvas.drawCircle(x,y,20,paint);
            }
        });

        //Custom shape to show a circle and the name of the task
        seriesDecide.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPointInterface) {
                TaskPoint tp = (TaskPoint) dataPointInterface;

                paint.setColor(Color.BLACK);
                paint.setTextSize(38);
                String name = tp.getTask().getName().length() > 10 ? tp.getTask().getName().substring(0,10) : tp.getTask().getName();
                canvas.drawText(name, x - (( name.length() / 2) * 18), y-32, paint);
                paint.setColor(Color.GREEN);
                canvas.drawCircle(x,y,20,paint);
            }
        });

        //Custom shape to show a circle and the name of the task
        seriesDelegate.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPointInterface) {
                TaskPoint tp = (TaskPoint) dataPointInterface;

                paint.setColor(Color.BLACK);
                paint.setTextSize(38);
                String name = tp.getTask().getName().length() > 10 ? tp.getTask().getName().substring(0,10) : tp.getTask().getName();
                canvas.drawText(name, x - (( name.length() / 2) * (tp.getTask().getEmergency() > 9 ? 40 : 18)), y-32, paint);
                paint.setColor(Color.BLUE);
                canvas.drawCircle(x,y,20,paint);
            }
        });

        //Custom shape to show a circle and the name of the task
        seriesDelete.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPointInterface) {
                TaskPoint tp = (TaskPoint) dataPointInterface;

                paint.setColor(Color.BLACK);
                paint.setTextSize(38);
                String name = tp.getTask().getName().length() > 10 ? tp.getTask().getName().substring(0,10) : tp.getTask().getName();
                canvas.drawText(name, x - (( name.length() / 2) * 18), y-32, paint);
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(x,y,20,paint);
            }
        });
    }

    /**
     * Listen if the user tap on a TaskPoint
     */
    private void listenSeriesTap() {
        seriesDo.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                assert series != null : "SeriesDoTap: series is null.";
                assert dataPointInterface != null : "SeriesDoTap: dataPointInterface is null.";

                TaskPoint tp = (TaskPoint) dataPointInterface;

                //Write some messages
                double pointY = tp.getY();
                double pointX = tp.getX();
                Log.d(TAG,"Point clicked [" + pointX + "," + pointY + "] : " + tp.getTask().getName());
                Toast.makeText(EisenhowerMatrix.this, tp.getTask().getName(), Toast.LENGTH_LONG).show();

                //Update unique task in control
                control.setTask(tp.getTask());

                //Launch other activity: DetailTask
                Intent detail_task = new Intent(getApplicationContext(), DetailTask.class);
                startActivity(detail_task);
            }
        });

        seriesDecide.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                assert series != null : "SeriesDecideTap: series is null.";
                assert dataPointInterface != null : "SeriesDecideTap: dataPointInterface is null.";

                TaskPoint tp = (TaskPoint) dataPointInterface;

                //Write some messages
                double pointY = tp.getY();
                double pointX = tp.getX();
                Log.d(TAG,"Point clicked [" + pointX + "," + pointY + "] : " + tp.getTask().getName());
                Toast.makeText(EisenhowerMatrix.this, tp.getTask().getName(), Toast.LENGTH_SHORT).show();

                //Update unique task in control
                control.setTask(tp.getTask());

                //Launch other activity: DetailTask
                Intent detail_task = new Intent(getApplicationContext(), DetailTask.class);
                startActivity(detail_task);
            }
        });

        seriesDelegate.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                assert series != null : "SeriesDelegateTap: series is null.";
                assert dataPointInterface != null : "SeriesDelegateTap: dataPointInterface is null.";

                TaskPoint tp = (TaskPoint) dataPointInterface;

                //Write some messages
                double pointY = tp.getY();
                double pointX = tp.getX();
                Log.d(TAG,"Point clicked [" + pointX + "," + pointY + "] : " + tp.getTask().getName());
                Toast.makeText(EisenhowerMatrix.this, tp.getTask().getName(), Toast.LENGTH_LONG).show();

                //Update unique task in control
                control.setTask(tp.getTask());

                //Launch other activity: DetailTask
                Intent detail_task = new Intent(getApplicationContext(), DetailTask.class);
                startActivity(detail_task);
            }
        });

        seriesDelete.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                assert series != null : "SeriesDeleteTap: series is null.";
                assert dataPointInterface != null : "SeriesDeleteTap: dataPointInterface is null.";

                TaskPoint tp = (TaskPoint) dataPointInterface;

                //Write some messages
                double pointY = tp.getY();
                double pointX = tp.getX();
                Log.d(TAG,"Point clicked [" + pointX + "," + pointY + "] : " + tp.getTask().getName());
                Toast.makeText(EisenhowerMatrix.this, tp.getTask().getName(), Toast.LENGTH_LONG).show();

                //Update unique task in control
                control.setTask(tp.getTask());

                //Launch other activity: DetailTask
                Intent detail_task = new Intent(getApplicationContext(), DetailTask.class);
                startActivity(detail_task);
            }
        });
    }

    /**
     * Listen if the user double-tap the screen
     */
    private void listenDoubleTap() {
        graphView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            doubleClick = false;
                        }
                    };

                    //Already clicked once => Double click
                    if (doubleClick) {

                        doubleClick = false;

                        //Ajuste the different values to get an approximation of where the user clicked
                        float viewX = (float) (event.getX() - 0.1 * v.getWidth());
                        float ViewY = (float) (event.getY() - 0.05 * v.getHeight());
                        float width = (float) (0.87 * v.getWidth());
                        float height = (float) (0.88 * v.getHeight());
                        float pourcentViewX = (float) (viewX / width);
                        float pourcentViewY = (float) (ViewY / height);

                        double maxX = graphView.getViewport().getMaxX(false);
                        double minX = graphView.getViewport().getMinX(false);
                        double maxY = graphView.getViewport().getMaxY(false);
                        double minY = graphView.getViewport().getMinY(false);

                        double newX = Math.round((minX + (maxX - minX) * pourcentViewX) * 10) / 10;
                        double newY = Math.round((maxY - (maxY - minY) * pourcentViewY) * 10) / 10;

                        //Check that X and Y are not over the limits
                        newX = newX > 10 ? 10 : newX < -10 ? -10 : newX;
                        newY = newY > 10 ? 10 : newY < -10 ? -10 : newY;

                        Log.d(TAG,"Double Tap. X : "  + newX + " | Y: "+ newY);

                        //Launch other activity: AddTask
                        Intent add_task = new Intent(getApplicationContext(), AddTask.class);
                        //Pass the values i the intent
                        add_task.putExtra("xValue", newX);
                        add_task.putExtra("yValue", newY);
                        startActivity(add_task);

                        //First Click
                    }else {
                        Log.d(TAG, "Single Tap.");
                        doubleClick = true;
                        //After 500 Ms, we forget that he clicked once => No double click
                        doubleHandler.postDelayed(r, 500);
                    }
                    return false;
                }
                return false;
            }
        });

    }

    @Override
    public void update(Observable o, Object arg) {
        assert o.getClass().getName().toString() == "Control" : "EisenhowerMatrix.update : Wrong Observable.";
        updateSeries();
    }
}