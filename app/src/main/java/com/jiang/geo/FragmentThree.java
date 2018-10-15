package com.jiang.geo;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * fragment3
 */
public class FragmentThree extends Fragment implements View.OnClickListener {

    private View mView;
    private static final int START_RECORD = 0;

    private Button mBtnStart,/*mBtnStop,*/
            mback;

    private File mFileRec;
    private Thread mThread;
    private MediaRecorder mMediaRecorder;
    private MyHandle mHandle;
    private TextView max, avg, tip, seconds;
    private ImageView tips;


    public boolean isRecording = false;
    private boolean isListener = false;
    private boolean isThreading = true;
    final int request = 1000;
    private String lat;
    private String lon;
    private float volume;
    boolean start = false;
    boolean running = false;
    FirebaseDatabase mdatabase;
    DatabaseReference mreport;
    private Button msummary;
    View recordview;

    private int mils, maxValue, avgValue, total, count;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//
        recordview = inflater.inflate(R.layout.layout_fragment_three, container, false);

        mdatabase = FirebaseDatabase.getInstance();
        mreport = mdatabase.getInstance().getReference().child("Report");
        max = recordview.findViewById(R.id.max);
        avg = recordview.findViewById(R.id.avg);
        tip = recordview.findViewById(R.id.tip);
        tips = recordview.findViewById(R.id.tips);
        seconds = recordview.findViewById(R.id.seconds);
        max.setText("MAX\n00");
        avg.setText("AVG\n00");
        seconds.setText("SECONDS\n00");

        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RecordVoiceTipAct.class));
            }
        });

        if (!checkPremission())
            requestpermission();
        mHandle = new MyHandle();
        mBtnStart = recordview.findViewById(R.id.btn_start);
        // mBtnStop = recordview.findViewById(R.id.btn_stop);
        mBtnStart.setOnClickListener(this);
        // mBtnStop.setOnClickListener(this);
        return recordview;


    }

    private void reset() {
        maxValue = 0;
        avgValue = 0;
        total = 0;
        count = 0;
        mils = 0;
        tip.setBackground(null);
        tip.setText("Measure your current noise");
        setMaxAndAvg();
    }

    private void setMaxAndAvg() {
        if (maxValue == 0) {
            max.setText("MAX\n00");
        } else {
            max.setText("MAX\n" + maxValue);
        }
        if (avgValue == 0) {
            avg.setText("AVG\n00");
        } else {
            avg.setText("AVG\n" + avgValue);
        }
        seconds.setText("SECONDS\n" + (mils / 1000));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:

                String s = mBtnStart.getText().toString();
                if (s.equalsIgnoreCase("START")) {
                    mBtnStart.setText("STOP");
                    isListener = true;
                    isRecording = true;
                    isThreading = true;
                    Message msg = new Message();
                    msg.what = START_RECORD;
                    mHandle.sendMessage(msg);
                    start = true;
                    running = true;
                    reset();
                } else {
                    if (mils < 1000L * 10) {
                        Toast.makeText(getContext(), "Please record at least 10 seconds", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mBtnStart.setText("START");
                    isListener = false;
                    isThreading = false;
                    isRecording = false;
                    mMediaRecorder.reset();

                    mFileRec.delete(); //delete the file
                    // float db = Calculator.dbstart;

                    mBtnStart.setEnabled(true);
                    Toast.makeText(getContext(), "The maximum value is " + maxValue + " and the average value is " + avgValue, Toast.LENGTH_SHORT).show();
                    start = false;
                    running = false;
                    if (avgValue == 0) {
                        avg.setText("AVG\n00");
                        tip.setText("Quiet, Safe for hearing");
                        tip.setBackgroundResource(R.drawable.common_shadow_toss2);
                    } else {
                        avg.setText("AVG\n" + avgValue);
                        if (avgValue >= 80) {
                            tip.setText("Loud,long exposure can cause hearing loss");
                            tip.setBackgroundResource(R.drawable.common_shadow_toss);
                        } else if (avgValue <= 70) {
                            tip.setText("Quiet,Safe for hearing");
                            tip.setBackgroundResource(R.drawable.common_shadow_toss3);
                        } else {
                            tip.setText("Moderate,Likely safe for hearing");
                            tip.setBackgroundResource(R.drawable.common_shadow_toss2);
                        }
                    }
                }

                //check value for the last
                Calculator.dbstart = 0;
                break;

            case R.id.btn_stop:
                if (start) {
                    if (mils < 1000L * 10) {
                        Toast.makeText(getContext(), "Please record at least 10 seconds", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isListener = false;
                    isThreading = false;
                    isRecording = false;
                    mMediaRecorder.reset();

                    mFileRec.delete(); //delete the file
                    // float db = Calculator.dbstart;

                    mBtnStart.setEnabled(true);
                    Toast.makeText(getContext(), "The maximum value is " + maxValue + " and the average value is " + avgValue, Toast.LENGTH_SHORT).show();
                    start = false;
                    running = false;
                    if (avgValue == 0) {
                        avg.setText("AVG\n00");
                        tip.setText("Quiet, Safe for hearing");
                        tip.setBackgroundResource(R.drawable.common_shadow_toss2);
                    } else {
                        avg.setText("AVG\n" + avgValue);
                        if (avgValue >= 80) {
                            tip.setText("Loud,long exposure can cause hearing loss");
                            tip.setBackgroundResource(R.drawable.common_shadow_toss);
                        } else if (avgValue <= 70) {
                            tip.setText("Moderate,Likely safe for hearing");
                            tip.setBackgroundResource(R.drawable.common_shadow_toss3);
                        } else {
                            tip.setText("Quiet,Safe for hearing");
                            tip.setBackgroundResource(R.drawable.common_shadow_toss2);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Please Click Start button", Toast.LENGTH_SHORT).show();

                }

                //check value for the last
                Calculator.dbstart = 0;
                break;


//            case R.id.backforradio:
//                if(running){
//                    Toast.makeText(getContext(), "Please Click Stop button", Toast.LENGTH_SHORT).show();
//                }
//                else{
////                    onBackPressed();
//                }
////                start = false;
//                break;

//            case R.id.summary:
//                Intent intent = new Intent(getActivity(),SummaryActivity.class);
//                startActivity(intent);


        }
    }

    private void beginstart() {
        isListener = true;
        mFileRec = Util.createFile("test.amr");
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    private void startRecord(File mFileRec) {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(mFileRec.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreading) {
                    try {
                        if (isListener) {
                            volume = mMediaRecorder.getMaxAmplitude();
                            if (volume > 0 && volume < 1000000) {
                                //calculate the db
                                Calculator.setDbCount(20 * (float) (Math.log10(volume)));

                            }
                        }
                        Log.v("activity", "db = " + Calculator.dbstart);//check for log
                        Thread.sleep(100);
                        mils += 100;
                        count += 1;
                        total += Calculator.dblast;
                        if (Calculator.dbstart > maxValue) {
                            maxValue = (int) Calculator.dbstart;
                        }
                        if (mils > 0) {
                            avgValue = total / count;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setMaxAndAvg();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isListener = false;
                    }
                }

            }
        });
        mThread.start();


    }

//    private void dialog(final float noise) {
//
//
//
//        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(getContext())
//                .setimageResource(R.drawable.icons8_listening_480)
//                .setTextTitle("The Noise Level Is " + nosielevel(noise))
//                .setBody("The noise near your is " + convertTo2(noise) + " DB" )
//                .setNegativeColor(R.color.app_blue)
//                .setNegativeButtonText("Got It")
//                .setBackgroundColor(R.color.white)
//                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
//                    @Override
//                    public void OnClick(View view, Dialog dialog) {
//                        dialog.dismiss();
//
//                    }
//                })
//                .setPositiveButtonText("Tell Us")
//                .setPositiveColor(R.color.red_primary)
//                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
//                    @Override
//                    public void OnClick(View view, Dialog dialog) {
//                        double latittude = Double.valueOf(lat);
//                        double longtitude = Double.valueOf(lon);
//                        uploadToDatabase(latittude,longtitude, noise);
//                        Toast.makeText(getContext(), "Thank you very much", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setBodyGravity(FancyAlertDialog.TextGravity.LEFT)
//                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                .setSubtitleGravity(FancyAlertDialog.TextGravity.RIGHT)
//                .setCancelable(false)
//                .build();
//
//        alert.show();
//
//    }


    class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case START_RECORD:
                    beginstart();
                    if (mFileRec != null) {

                        startRecord(mFileRec);
                    } else {
                        Toast.makeText(getContext(), "Create file failed", Toast.LENGTH_LONG).show();
                    }

                    break;
            }

            super.handleMessage(msg);
        }

    }


    private void requestpermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO}, request);

    }

    private boolean checkPremission() {
        int write_result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int radio_result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        return write_result == PackageManager.PERMISSION_GRANTED && radio_result == PackageManager.PERMISSION_GRANTED;
    }


    private void uploadToDatabase(Double lat, Double lon, float noise) {


        DatabaseReference report = mreport.push();
        Map result = new HashMap();
        result.put("latitude", lat);
        result.put("longitude", lon);
        result.put("noiseValue", noise);
        result.put("time", getCurrentTime());
        result.put("ID", report.getKey());
        result.put("noiseLevel", nosielevel(noise));
        report.setValue(result);

    }

    private String convertTo2(Float distance) {


        String noise = String.format("%.2f", distance);
        return noise;

    }

    private String nosielevel(Float noise) {
        if (noise <= 20 && noise > 0) {
            return "Faint";
        }
        if (20 < noise && noise < 50) {
            return "Soft";

        }
        if (50 <= noise && noise < 70) {
            return "Moderate";

        }
        if (70 <= noise && noise < 90) {
            return "Loud";
        }
        if (90 <= noise && noise < 120) {
            return "Very Loud";
        }
        if (120 <= noise && noise < 190) {
            return "Dangerous";
        }
        return null;
    }


    public int getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        int time = myDate.get(Calendar.HOUR_OF_DAY);


        return time;
    }
}

