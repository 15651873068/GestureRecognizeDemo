package com.lee.edu.mydemo.Thread;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;


import com.lee.edu.mydemo.JavaBean.DataBean;
import com.lee.edu.mydemo.JavaBean.GlobalBean;
import com.lee.edu.mydemo.Utils.SendDataUtils;

import java.text.DecimalFormat;

/**
 * 即时录音线程
 * Created by dmrf on 18-3-15.
 */

public class InstantRecordThread extends Thread {
    private GlobalBean globalBean;
    private Context context;

    public InstantRecordThread(GlobalBean globalBean, Context context) {
        this.globalBean = globalBean;
        this.context = context;
    }

    @Override
    public void run() {
        short[] bsRecord = new short[globalBean.recBufSize];//recBufSize=4400


        int n = 0;
        double totPhase = 0;
        double lastDist = 0;
        double lastDistR = 0;
        double NowPhase = 0;
        //--------------jni------------------------
        DemoNew();

        while (globalBean.flag == false) {
        }
        try {
            globalBean.audioRecord.startRecording();

        } catch (IllegalStateException e) {
            // 录音开始失败
            Toast.makeText(context, "录音开始失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        int while_count = 0;
        while (globalBean.flag)//大循环
        {


            //读取录音
            // short[] bsRecord = new short[recBufSize];//recBufSize=4400*2

            int Len = globalBean.audioRecord.read(bsRecord, 0, globalBean.recBufSize);


            double[] di = new double[110];
            double[] tempIIL = new double[880];
            double[] tempQQL = new double[880];


            DemoL(bsRecord, di, tempIIL, tempQQL);

            boolean all_zero_flag = true;
            for (int i = 0; i < 880; i++) {
                if (tempIIL[i] != 0) {
                    all_zero_flag = false;
                    break;
                }
            }

            if (!all_zero_flag) {
                globalBean.AddDataToList(globalBean.L_I, tempIIL);
                globalBean.AddDataToList(globalBean.L_Q, tempQQL);
                while_count++;
                if (while_count == 25) {


                    Message msg3 = new Message();
                    msg3.what = 0;

                    msg3.obj = ("stop");
                    globalBean.mHandler.sendMessage(msg3);
                    SaveData();
                    break;
                }
            }

            lastDist = di[110 - 1];

            double[] tempIIR = new double[880];
            double[] tempQQR = new double[880];
            DemoR(bsRecord, di, tempIIR, tempQQR);


            lastDistR = di[110 - 1];
            NowPhase += totPhase / 2;
            while (NowPhase < 0) NowPhase += Math.PI * 2;
            while (NowPhase > Math.PI * 2) NowPhase -= Math.PI * 2;

            Message msg1 = new Message();
            msg1.what = 1;
            DecimalFormat df = new DecimalFormat("#.00");

            msg1.obj = (df.format(lastDist));
            globalBean.mHandler.sendMessage(msg1);

            Message msg2 = new Message();
            msg2.what = 2;

            msg2.obj = (df.format(lastDistR));
            globalBean.mHandler.sendMessage(msg2);
        }//while end
        try {
            globalBean.audioRecord.stop();
        } catch (IllegalStateException e) {
            Toast.makeText(context, "发生了异常，请联系最帅的人优化代码", Toast.LENGTH_SHORT).show();
        }


    }

    private void SaveData() {

        DataBean dataBean = new DataBean();
        dataBean.setI0(globalBean.L_I[0].toString());
        dataBean.setI1(globalBean.L_I[1].toString());
        dataBean.setI2(globalBean.L_I[2].toString());
        dataBean.setI3(globalBean.L_I[3].toString());
        dataBean.setI4(globalBean.L_I[4].toString());
        dataBean.setI5(globalBean.L_I[5].toString());
        dataBean.setI6(globalBean.L_I[6].toString());
        dataBean.setI7(globalBean.L_I[7].toString());


        dataBean.setQ0(globalBean.L_Q[0].toString());
        dataBean.setQ1(globalBean.L_Q[1].toString());
        dataBean.setQ2(globalBean.L_Q[2].toString());
        dataBean.setQ3(globalBean.L_Q[3].toString());
        dataBean.setQ4(globalBean.L_Q[4].toString());
        dataBean.setQ5(globalBean.L_Q[5].toString());
        dataBean.setQ6(globalBean.L_Q[6].toString());
        dataBean.setQ7(globalBean.L_Q[7].toString());
        dataBean.setWhoandwhich(globalBean.whoandwhich);

        long time = System.currentTimeMillis();
        dataBean.setFilename(String.valueOf(time));

        SendDataUtils sendDataUtils = new SendDataUtils(dataBean, context,globalBean);
        sendDataUtils.execute("");
    }


    public native void DemoNew();

    public native int DemoL(short[] Record, double[] DIST, double[] tempII, double[] tempQQ);

    public native int DemoR(short[] Record, double[] DIST, double[] tempII, double[] tempQQ);

    static {
        System.loadLibrary("native-lib");
    }
}
