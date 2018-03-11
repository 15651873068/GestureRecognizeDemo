package com.lee.edu.mydemo.Global;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lee.edu.mydemo.FrequencyPlayer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by dmrf on 18-3-11.
 */

public class Global_data {

    public int encodingBitrate = AudioFormat.ENCODING_PCM_16BIT;//编码率（默认ENCODING_PCM_16BIT）
    public int channelConfig = AudioFormat.CHANNEL_IN_STEREO;        //声道（默认单声道） 立体道  MONO单声道，STEREO立体声
    public double[] Freqarrary = {17500, 17850, 18200, 18550, 18900, 19250, 19600, 19950, 20300, 20650};        //设置播放频率
    public int recBufSize = 4400 * 2;            //定义录音片长度
    public int sampleRateInHz = 44100;//采样率（默认44100，每秒44100个点）
    public AudioRecord audioRecord;    //录音对象
    public FrequencyPlayer FPlay;
    public Socket socket = null;
    public PrintWriter out;

    public ArrayList<Double> L_I[];
    public ArrayList<Double> L_Q[];
    public ArrayList<Double> R_I[];
    public ArrayList<Double> R_Q[];

    public int numfre = 8;
    public boolean flag = true;        //播放标志


    public EditText port;
    public TextView textView1;
    public TextView textView2;
    public Button btnPlayRecord;        //开始按钮
    public Button btnStopRecord;        //结束按钮




    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    textView1.setText(msg.obj.toString());
                    break;
                case 2:
                    textView2.setText(msg.obj.toString());
                    break;
            }
        }
    };


    public Global_data() {

        L_I = new ArrayList[8];
        L_Q = new ArrayList[8];
        R_I = new ArrayList[8];
        R_Q = new ArrayList[8];

        for (int i = 0; i < 8; i++) {
            ArrayList<Double> list1 = new ArrayList<Double>();
            ArrayList<Double> list2 = new ArrayList<Double>();
            ArrayList<Double> list3 = new ArrayList<Double>();
            ArrayList<Double> list4 = new ArrayList<Double>();
            L_I[i] = list1;
            L_Q[i] = list2;
            R_I[i] = list3;
            R_Q[i] = list4;
        }

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//从麦克风采集音频
                sampleRateInHz,//采样率，这里的值是sampleRateInHz = 44100即每秒钟采样44100次
                channelConfig,//声道设置，MONO单声道，STEREO立体声，这里用的是立体声
                encodingBitrate,//编码率（默认ENCODING_PCM_16BIT）
                recBufSize);//录音片段的长度，给的是minBufSize=recBufSize = 4400 * 2;
    }


    public void InitListener() {

        //播放按钮
        btnPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPlayRecord.setEnabled(false);
                btnStopRecord.setEnabled(true);


                if (L_I[0] != null) {
                    for (int i = 0; i < 8; i++) {
                        L_I[i].clear();
                        L_Q[i].clear();
                        R_I[i].clear();
                        R_Q[i].clear();
                    }
                }


                flag = true;
                new ThreadInstantPlay(Global_data.this).start();        //播放(发射超声波)


                try {
                    Thread.sleep(10);    //等待开始播放再录音
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                new ThreadInstantRecord(Global_data.this, mHandler).start();        //录音
                //录音播放线程

            }
        });

        //停止按钮
        btnStopRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                btnPlayRecord.setEnabled(true);
                btnStopRecord.setEnabled(false);
                FPlay.colseWaveZ();
                flag = false;


                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


}
