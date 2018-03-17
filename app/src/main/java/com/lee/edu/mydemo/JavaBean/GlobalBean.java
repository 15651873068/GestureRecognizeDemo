package com.lee.edu.mydemo.JavaBean;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.edu.mydemo.Activity.MainActivity;
import com.lee.edu.mydemo.R;
import com.lee.edu.mydemo.Thread.InstantPlayThread;
import com.lee.edu.mydemo.Thread.InstantRecordThread;
import com.lee.edu.mydemo.Utils.FrequencyPlayerUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dmrf on 18-3-15.
 */

public class GlobalBean {




   /*
   set audio
    */

    public double[] Freqarrary = {17500, 17850, 18200, 18550, 18900, 19250, 19600, 19950, 20300, 20650};        //设置播放频率
    public int encodingBitrate = AudioFormat.ENCODING_PCM_16BIT;// 编码率（默认ENCODING_PCM_16BIT）
    public int channelConfig = AudioFormat.CHANNEL_IN_MONO;        //声道（默认单声道） 单道  MONO单声道，STEREO立体声
    public AudioRecord audioRecord;    //录音对象
    public FrequencyPlayerUtils FPlay;
    public int sampleRateInHz = 44100;//采样率（默认44100，每秒44100个点）
    public int recBufSize = 4400;            //定义录音片长度
    public int numfre = 8;


    /*
    views
     */
    public Button btnPlayRecord;        //开始按钮
    public Button btnStopRecord;        //结束按钮
    public TextView tvDist;            //显示距离
    public TextView tvDist2;            //显示距离
    public Button btnSetWhichandWho;
    public Button btnQuerycount;
    public int is_in_count = -1;

    /*
    variable
     */
    public boolean flag = true;        //播放标志
    public ArrayList<Double> L_I[];
    public ArrayList<Double> L_Q[];
    public String whoandwhich = "";

    private Context context;


    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        //设置圆环角度
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj.toString().equals("stop")) {
                        Stop();
                    } else if (msg.obj.toString().equals("playe")) {
                        Toast.makeText(context, "发生了异常，请联系最帅的人优化代码～", Toast.LENGTH_SHORT).show();
                    } else if (msg.obj.toString().equals("start")) {
                        btnPlayRecord.setEnabled(false);
                        btnPlayRecord.setTextColor(R.color.notcolor);
                        btnStopRecord.setEnabled(true);
                        btnStopRecord.setTextColor(R.color.okcolor);
                        tvDist.setVisibility(View.VISIBLE);
                        tvDist2.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    tvDist.setText(msg.obj.toString());
                    break;
                case 2:
                    tvDist2.setText(msg.obj.toString());
                    break;
            }
        }
    };


    public GlobalBean(Context context) {
        this.context = context;
    }

    public void Init() {
        L_I = new ArrayList[8];
        L_Q = new ArrayList[8];

        for (int i = 0; i < 8; i++) {
            ArrayList<Double> list1 = new ArrayList<Double>();
            ArrayList<Double> list2 = new ArrayList<Double>();
            L_I[i] = list1;
            L_Q[i] = list2;

        }


        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//从麦克风采集音频
                sampleRateInHz,//采样率，这里的值是sampleRateInHz = 44100即每秒钟采样44100次
                channelConfig,//声道设置，MONO单声道，STEREO立体声，这里用的是立体声
                encodingBitrate,//编码率（默认ENCODING_PCM_16BIT）
                recBufSize);//录音片段的长度，给的是minBufSize=recBufSize = 4400 * 2;


        btnPlayRecord.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                if (whoandwhich.equals("")) {
                    Toast.makeText(context, "不告诉我你是谁不让你录！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (L_I[0] != null) {
                    for (int i = 0; i < 8; i++) {
                        L_I[i].clear();
                        L_Q[i].clear();
                    }
                }

                flag = true;
                new InstantPlayThread(GlobalBean.this).start();        //播放(发射超声波)


                try {
                    Thread.sleep(10);    //等待开始播放再录音
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                new InstantRecordThread(GlobalBean.this, context).start();        //录音
                //录音播放线程
            }
        });

        //停止按钮
        btnStopRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                Stop();
            }
        });

        btnQuerycount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnQuerycount.setText("查询中.......");
                if (whoandwhich.equals("")) {
                    Toast.makeText(context, "不告诉我你是谁我怎么怎么帮你查！", Toast.LENGTH_SHORT).show();
                    btnQuerycount.setText("查询已录条数");
                    return;
                } else {

                    if (is_in_count != -1) {
                        Toast.makeText(context, "已录入" + is_in_count + "条数据", Toast.LENGTH_SHORT).show();
                    } else {
                        BmobQuery<DataBean> bmobQuery = new BmobQuery<DataBean>();
                        bmobQuery.addWhereEqualTo("whoandwhich", whoandwhich);
                        bmobQuery.findObjects(new FindListener<DataBean>() {
                            @Override
                            public void done(List<DataBean> list, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(context, "已录入" + list.size() + "条数据", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(context, "查询数据失败，请联系最帅的人查找bug～", Toast.LENGTH_SHORT).show();
                                }
                                btnQuerycount.setText("查询已录条数");
                            }
                        });
                    }
                }
            }
        });

        btnSetWhichandWho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whoandwhich = "";
                ShowChoiseWho();
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    public void Stop() {
        btnPlayRecord.setEnabled(true);
        btnPlayRecord.setTextColor(R.color.okcolor);
        btnStopRecord.setEnabled(false);
        btnStopRecord.setTextColor(R.color.notcolor);
        tvDist.setVisibility(View.GONE);
        tvDist2.setVisibility(View.GONE);
        FPlay.colseWaveZ();
        flag = false;
    }

    public void AddDataToList(ArrayList<Double>[] list, double[] data) {

        int count = -1;
        for (int i = 0; i < 880; i++) {
            if (i % 110 == 0) {
                count++;
            }
            list[count].add(data[i]);
        }

    }


    public void ShowChoiseWho() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("设置玩家姓名");
        //    指定下拉列表的显示数据
        final String[] names = {"王jun玙", "历傲然", "蔡益武", "李珍岩", "张玉麟", "薛方岗"};
        final String[] codes = {"wangjunyu", "liaoran", "caiyiwu", "lizhenyan", "zhangyulin", "xuefanggang"};
        SimpleDateFormat formatter = new SimpleDateFormat("MM_dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        final String day = formatter.format(curDate);

        //    设置一个下拉的列表选择项
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                whoandwhich = codes[which] + "_" + day;
                ShowChoiseWhich();
            }
        });
        AlertDialog alertDialog = builder.create();
        final Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.show();
    }


    private void ShowChoiseWhich() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("设置游戏英雄");

        //    指定下拉列表的显示数据
        final String[] names = {"A", "B", "C", "D", "E", "F"};
        //    设置一个下拉的列表选择项
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                whoandwhich = whoandwhich + "_" + names[which];
                Toast.makeText(context, "choose:" + whoandwhich, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        final Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.show();
    }

}
