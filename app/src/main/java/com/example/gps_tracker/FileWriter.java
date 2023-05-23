package com.example.gps_tracker;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

class FileWriter {
    Boolean m_bIsPause = false;
    FileOutputStream mFile = null;
    Context mContext;

    FileWriter(Context context) {
        mContext = context;
    }

    void log(String text) {
        Log.d("FileWriter", text);
    }

    void startRecording() {
        // if there's paused state just end this state
        if(m_bIsPause == true) {
            m_bIsPause = false;
            return;
        }
        try {
            mFile = new FileOutputStream(createNewFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        write("<?xml version=\"1.0\"?>");
        write("<gpx version=\"1.1\" creator=\"Android Smartphone\">");
        write("<name>Trackname1</name>");
        write("<desc>Trackbeschreibung</desc>");
        write("<trk>");
        write("<trkseg>");
        log("startRecording successfull");

    }

    void pauseRecording() {
        // enter paused state
        m_bIsPause = true;
    }

    void stopRecording() {
        // end paused state
        m_bIsPause = false;
        try {
            if(mFile != null) {
                write("</trkseg>");
                write("</trk>");
                write("</gpx>");
                mFile.close();
                mFile = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("stopRecording succesfull");
    }

    void write(String text) {
        text += "\r\n";
        try {
            mFile.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writePosition(double longitude, double latitude, double altitude) {
        // only write to file if there's no paused state
        if(m_bIsPause == true)
            return;
        // also return if there's no mFile-Objekt
        if(mFile == null)
            return;

        String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date());
        StringBuilder time = new StringBuilder(time1);
        time.setCharAt(10,'T');
        time.setCharAt(19,'Z');
        write(String.format("<trkpt lat=\"%.6f\" lon=\"%.6f\">", latitude, longitude));
        write(String.format("<ele>%.0f</ele>", altitude));
        write(String.format("<time>%s</time>", time));
        write("</trkpt>");
        //log("wrote position");

    }

    boolean isRecording() {
        return mFile != null;
    }

    void startRecordingCSV() {
        if(m_bIsPause == true) {
            m_bIsPause = false;
            return;
        }
        try {
            mFile = new FileOutputStream(createNewFile(".csv"));
            write("Time,Latitude,Longitude,Altitude,Speed\n"); // CSV headers
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log("startRecordingCSV successfull");
    }

    void writePositionCSV(double longitude, double latitude, double altitude, float speed) {

        if(m_bIsPause == true)
            return;

        if(mFile == null)
            return;

        String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        write(String.format("%s,%.6f,%.6f,%.0f,%f\n", time1, latitude, longitude, altitude, speed));
        //log("wrote position");
    }


    private File createNewFile() {
        File fileStorageDir = null;
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        //    fileStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        //}

        fileStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(fileStorageDir.getPath() + File.separator + timeStamp+ ".gpx");
        return file;
    }

    private File createNewFile(String extension) {
        File fileStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(fileStorageDir.getPath() + File.separator + timeStamp + extension);
        Log.d("FileWriter", "File path: " + file.getAbsolutePath());
        return file;
    }




}
