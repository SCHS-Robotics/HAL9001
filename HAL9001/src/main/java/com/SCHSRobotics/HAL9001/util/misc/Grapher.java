/*
 * Filename: Grapher.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/3/19
 */

package com.SCHSRobotics.HAL9001.util.misc;

import android.os.Environment;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.plot.Plot2d;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A class for graphing functions on the phone in real time/
 */
public class Grapher {

    //The x and y data of the graph.
    private Mat xData, yData;
    //The time in milliseconds of the last time the graph was updated.
    private long lastUpdate;
    //The graph window's max x and y coordinate.
    private double maxX, maxY;
    //The time since the grapher was started.
    private double t;
    //The timestep in between graph requests.
    private double timeStep, lastTimeStep;
    //The current snapshot id. Used for taking snapshots of graph data.
    private int snapshotNumber;
    //A boolean specifying if the grapher is on its first loop.
    private boolean firstLoop;
    //A boolean specifying if the grapher should allow negative y values.
    private boolean negativeY;

    /**
     * Constructor for Grapher.
     *
     * @param maxX - The graph's maximium displayable x value. This is essentially the width of the graph window.
     * @param maxY - The graph's maximum displayable y value. This is essentially the height of the graph window.
     */
    public Grapher(double maxX, double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;

        t = 0;
        timeStep = 0;
        firstLoop = true;

        xData = new Mat();
        yData = new Mat();

        xData.convertTo(xData, CvType.CV_64F);
        yData.convertTo(xData, CvType.CV_64F);

        negativeY = true;

        snapshotNumber = 0;
    }

    /**
     * Constructor for Grapher.
     *
     * @param maxX - The graph's maximium displayable x value. This is essentially the width of the graph window.
     * @param maxY - The graph's maximum displayable y value. This is essentially the height of the graph window.
     * @param negativeY - Whether or not to allow negative y coordinates on the graph.
     */
    public Grapher(double maxX, double maxY, boolean negativeY) {
        this.maxX = maxX;
        this.maxY = maxY;

        t = 0;
        timeStep = 0;
        firstLoop = true;

        xData = new Mat();
        yData = new Mat();

        xData.convertTo(xData, CvType.CV_64F);
        yData.convertTo(xData, CvType.CV_64F);

        this.negativeY = negativeY;

        snapshotNumber = 0;
    }

    /**
     * Gets the next graph frame.
     *
     * @param nextY - The next y value on the graph.
     * @return - An image (Mat) of the graph.
     */
    public Mat getNextFrame(double nextY) {

        if(!firstLoop) {
            timeStep = (System.currentTimeMillis()-lastUpdate)/1000.0;
        }
        else {
            firstLoop = false;
        }

        t+=timeStep;

        if(t <= maxX) {

            Mat x = new Mat(1,1, CvType.CV_64F);
            Mat y = new Mat(1,1, CvType.CV_64F);

            x.put(0,0,t);
            y.put(0,0,nextY);

            xData.push_back(x);
            yData.push_back(y);

            x.release();
            y.release();
        }
        else {

            double lastX = xData.get(xData.rows()-1,0)[0];

            xData = xData.submat(1,xData.rows(),0,1);
            Mat newX = Mat.ones(1,1, CvType.CV_64F);

            Core.multiply(newX,new Scalar(lastX+lastTimeStep),newX);
            xData.push_back(newX);

            newX.release();

            Core.subtract(xData,new Scalar(lastTimeStep),xData);
            double xShift = xData.get(0,0)[0];
            Core.subtract(xData,new Scalar(xShift),xData);

            yData = yData.submat(1,yData.rows(),0,1);
            yData.push_back(Mat.zeros(1,1, CvType.CV_64F));
            yData.put(yData.rows()-1,0,nextY);
        }

        Plot2d plotter = Plot2d.create(xData,yData);
        plotter.setInvertOrientation(true);
        plotter.setMaxX(maxX);
        plotter.setMaxY(maxY);
        plotter.setMinX(0);
        plotter.setMinY(negativeY ? -maxY : 0);
        plotter.setShowText(false);

        Mat plot = new Mat();
        plotter.render(plot);

        Imgproc.resize(plot,plot,new Size(240,320));

        plotter.clear();

        lastTimeStep = timeStep;
        lastUpdate = System.currentTimeMillis();

        System.gc();

        return plot;
    }

    //TODO may need updating
    /**
     * Save a snapshot of all the data currently on the graph.
     */
    private void saveDataSnapshot() {

        snapshotNumber++;

        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/GraphDataSnapshots/dataSnapshot"+snapshotNumber+".txt");
            FileOutputStream fileoutput = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fileoutput);

            ps.println("Snapshot Timestamp: "+t+" Seconds"+"\r\n\r\n");

            for(int i = 0; i < xData.rows(); i++) {
                ps.println(xData.get(i, 0)[0] + ", " + yData.get(i, 0)[0]+"\r\n");
            }

            ps.close();
            fileoutput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
