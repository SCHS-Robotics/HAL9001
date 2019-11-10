package com.SCHSRobotics.HAL9001.util.control;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.video.KalmanFilter;

@Deprecated
//TODO currently broken, WIP
public class LKF {
    public Point lastResult;
    private KalmanFilter kf;

    public boolean wasUpdated;

    public boolean isFirstUpdate;

    public long lastUpdateTimer;
    public long creationTime;

    public double trustworthyness;
    private int correctTimes;
    public int totalUpdates;

    private int params = 6;

    public LKF(Point init) {

        //dynamParams: number of things in the state vector that change
        //measureParams: number of parameters in the state vector that are measured (x and y position only so 2)
        //control params you shouldn't worry about too much
        kf = new KalmanFilter(params, 2, 0, CvType.CV_32F);

        //transitionMatrix
        Mat transitionMatrix = new Mat(params, params, CvType.CV_32F, new Scalar(0));

         /* For taking into account only velocity
        float[] tM = {
                1, 0, 1, 0,
                0, 1, 0, 1,
                0, 0, 1, 0,
                0, 0, 0, 1 };
         */

        //For taking into account acceleration
        float[] tM = {
                1, 0, 1, 0, 0.5f, 0,
                0, 1, 0, 1, 0, 0.5f,
                0, 0, 1, 0, 1, 0,
                0, 0, 0, 1, 0, 1,
                0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 1 };


        transitionMatrix.put(0,0,tM);

        kf.set_transitionMatrix(transitionMatrix);

        lastResult = new Point(init.x,init.y);
        Mat statePre = new Mat(((int) Math.round(Math.sqrt(tM.length))), 1, CvType.CV_32F, new Scalar(0));
        statePre.put(0, 0, init.x);
        statePre.put(1, 0, init.y);
        kf.set_statePre(statePre);

        kf.set_measurementMatrix(Mat.eye(2,params, CvType.CV_32F));

        Mat processNoiseCov = Mat.eye(params, params, CvType.CV_32F);
        processNoiseCov = processNoiseCov.mul(processNoiseCov, 1e-4);
        kf.set_processNoiseCov(processNoiseCov);

        Mat id1 = Mat.eye(2,2, CvType.CV_32F);
        id1 = id1.mul(id1,1e-3);
        kf.set_measurementNoiseCov(id1);

        Mat id2 = Mat.eye(params,params, CvType.CV_32F);
        id2 = id2.mul(id2,1e-2);
        kf.set_errorCovPost(id2);

        creationTime = System.currentTimeMillis();

        lastUpdateTimer = 0;
        trustworthyness = 0;
        totalUpdates = 0;
        correctTimes = 0;

        isFirstUpdate = true;
    }


    //dataCorrect = "is the data correct or not?" updates it with last known result if data is incorrect, otherwise updates it with current result
    public Point update(Point p, boolean dataCorrect) {

        this.wasUpdated = true;

        totalUpdates++;

        Mat measurement = new Mat(2, 1, CvType.CV_32F, new Scalar(0)) ;

        if (!dataCorrect) {
            measurement.put(0, 0, lastResult.x);
            measurement.put(1, 0, lastResult.y);
            trustworthyness = 1.0*correctTimes/totalUpdates;

        } else {
            measurement.put(0, 0, p.x);
            measurement.put(1, 0, p.y);
            creationTime = System.currentTimeMillis();

            correctTimes++;

            trustworthyness = 1.0*correctTimes/totalUpdates;
        }

        lastUpdateTimer = System.currentTimeMillis() - creationTime;

        // Correction
        Mat estimated = kf.correct(measurement); //updates predicted state from the measurement
        lastResult.x = estimated.get(0, 0)[0];
        lastResult.y = estimated.get(1, 0)[0];

        if(totalUpdates%2 == 0 && isFirstUpdate) {
            isFirstUpdate = false;
        }

        return lastResult;
    }

    public Point getPrediction() {
        this.wasUpdated = false;
        Mat prediction = kf.predict();
        lastResult = new Point(prediction.get(0, 0)[0], prediction.get(1, 0)[0]);
        return lastResult;
    }
    public Point correction(Point p){
        Mat measurement = new Mat(2, 1, CvType.CV_32F, new Scalar(0));
        measurement.put(0, 0, p.x);
        measurement.put(1, 0, p.y);

        Mat estimated = kf.correct(measurement);
        lastResult.x = estimated.get(0, 0)[0];
        lastResult.y = estimated.get(1, 0)[0];
        return lastResult;
    }
}
