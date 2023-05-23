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
import java.util.Locale;

class FileWriter {
    private Boolean m_bIsPause = false;
    private FileOutputStream mGpxFile = null;
    private FileOutputStream mCsvFile = null;
    private Context mContext;

    FileWriter(Context context) {
        mContext = context;
    }

    void log(String text) {
        Log.d("FileWriter", text);
    }

    void startRecording() {
        if(m_bIsPause == true) {
            m_bIsPause = false;
            return;
        }
        try {
            mGpxFile = new FileOutputStream(createNewFile(".gpx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        write("<?xml version=\"1.0\"?>", mGpxFile);
        write("<gpx version=\"1.1\" creator=\"Android Smartphone\">", mGpxFile);
        write("<name>Trackname1</name>", mGpxFile);
        write("<desc>Trackbeschreibung</desc>", mGpxFile);
        write("<trk>", mGpxFile);
        write("<trkseg>", mGpxFile);
        log("startRecording successfull");
    }

    void startRecordingCSV() {
        if(m_bIsPause == true) {
            m_bIsPause = false;
            return;
        }
        try {
            mCsvFile = new FileOutputStream(createNewFile(".csv"));
            write("Time,Latitude,Longitude,Altitude,Speed\n", mCsvFile); // CSV headers
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log("startRecordingCSV successfull");
    }

    void pauseRecording() {
        m_bIsPause = true;
    }

    void resumeRecording() {
        m_bIsPause = false;
    }

    void stopRecording() {
        m_bIsPause = false;
        try {
            if(mGpxFile != null) {
                write("</trkseg>", mGpxFile);
                write("</trk>", mGpxFile);
                write("</gpx>", mGpxFile);
                mGpxFile.close();
                mGpxFile = null;
            }
            if(mCsvFile != null) {
                mCsvFile.close();
                mCsvFile = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("stopRecording successful");
    }

    void write(String text, FileOutputStream file) {
        text += "\r\n";
        try {
            file.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writePosition(double longitude, double latitude, double altitude) {
        if(m_bIsPause) return;
        if(mGpxFile == null) return;

        String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date());
        StringBuilder time = new StringBuilder(time1);
        time.setCharAt(10,'T');
        time.setCharAt(19,'Z');
        write(String.format(Locale.US,"<trkpt lat=\"%.6f\" lon=\"%.6f\">", latitude, longitude), mGpxFile);
        write(String.format(Locale.US,"<ele>%.0f</ele>", altitude), mGpxFile);
        write(String.format(Locale.US,"<time>%s</time>", time), mGpxFile);
        write("</trkpt>", mGpxFile);
    }

    void writePositionCSV(double longitude, double latitude, double altitude, float speed) {
        if(m_bIsPause) return;
        if(mCsvFile == null) return;

        String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        write(String.format(Locale.US,"%s,%.6f,%.6f,%.2f,%.2f\n", time1, latitude, longitude, altitude, speed), mCsvFile);
    }

    boolean isRecording() {
        return mGpxFile != null || mCsvFile != null;
    }

    private File createNewFile(String extension) {
        File fileStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(fileStorageDir.getPath() + File.separator + timeStamp + extension);
        Log.d("FileWriter", "File path: " + file.getAbsolutePath());
        return file;
    }
}

