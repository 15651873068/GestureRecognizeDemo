package com.lee.edu.mydemo.Global;

import android.os.Handler;
import android.os.Message;

import com.lee.edu.mydemo.MainActivity;
import com.lee.edu.mydemo.SaveFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by dmrf on 18-3-11.
 */

class ThreadInstantRecord extends Thread {

    private Global_data global;
    private Handler mHandler;


    private short[] bsRecord;//recBufSize=4400*2

    private short[] bsRecordL;
    private short[] bsRecordR;

    private short[] BIGDATA;
    private short[] BIGDATA2;

    private double[] BIGDATAIIL;//这边4个从1开始取 注意+1
    private double[] BIGDATAQQL;

    private double[] BIGDATAIIR;
    private double[] BIGDATAQQR;
    //八个频率调整好的数据
    private double[] needIL;
    private double[] needQL;

    private double[] needIR;
    private double[] needQR;

    private int n = 0;
    private double totPhase = 0;
    private double lastDist = 0;
    private double lastDistR = 0;
    private double NowPhase = 0;

    private Socket socket;
    private PrintWriter out;
    public int PORT = 9999;
    public String IP = "192.168.43.135";


    public ThreadInstantRecord(Global_data global_data, Handler handler) {
        mHandler = handler;
        global = global_data;

        bsRecord = new short[global.recBufSize];//recBufSize=4400*2

        bsRecordL = new short[global.recBufSize / 2];
        bsRecordR = new short[global.recBufSize / 2];

        BIGDATA = new short[44100 * 30];
        BIGDATA2 = new short[44100 * 30];

        BIGDATAIIL = new double[44100 * 30];//这边4个从1开始取 注意+1
        BIGDATAQQL = new double[44100 * 30];

        BIGDATAIIR = new double[44100 * 30];
        BIGDATAQQR = new double[44100 * 30];
        //八个频率调整好的数据
        needIL = new double[880 + 2];
        needQL = new double[880 + 2];

        needIR = new double[880 + 2];
        needQR = new double[880 + 2];

        n = 0;
        totPhase = 0;
        lastDist = 0;
        lastDistR = 0;
        NowPhase = 0;

    }

    @Override
    public void run() {


        //向服务器发送消息
        try {

            String po = String.valueOf(global.port.getText());
            if (!po.equals("")) {
                PORT = Integer.parseInt(po);
            }

            socket = new Socket(IP, PORT);//第一个参数是ip地址，第二个是端口号
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DemoNew();

        while (!global.flag) {
        }

        global.audioRecord.startRecording();


        long s = System.currentTimeMillis();
        long e ;
        while (global.flag)//大循环
        {


            e = System.currentTimeMillis();
            if (e - s >= 3000) {
                global.flag = false;
                global.audioRecord.stop();
                break;
            }
            int Len = global.audioRecord.read(bsRecord, 0, global.recBufSize);


            //将读到数据的L和R分开
            for (int i = 0; i < Len; i++) {
                BIGDATA[n] = bsRecord[i];
                bsRecordL[i / 2] = bsRecord[i++];

                BIGDATA2[n++] = bsRecord[i];
                bsRecordR[i / 2] = bsRecord[i];
            }

            double[] di = new double[110];

            double[] tempIIL = new double[880];
            double[] tempQQL = new double[880];


            DemoL(bsRecordL, di, tempIIL, tempQQL);
            AddDataToList(global.L_I, tempIIL, true);
            AddDataToList(global.L_Q, tempQQL, true);

            if(out!=null){
                for (int i = 0; i < global.L_I[0].size(); i++) {
                    out.println(global.L_I[0].get(i));
                    out.flush();
                }

            }


            lastDist = di[110 - 1];

            double[] tempIIR = new double[880];
            double[] tempQQR = new double[880];

            DemoR(bsRecordR, di, tempIIR, tempQQR);
            AddDataToList(global.R_I, tempIIR, false);
            AddDataToList(global.R_Q, tempQQR, false);


            lastDistR = di[110 - 1];
            NowPhase += totPhase / 2;

            while (NowPhase < 0) NowPhase += Math.PI * 2;
            while (NowPhase > Math.PI * 2) NowPhase -= Math.PI * 2;

            Message msg1 = new Message();
            msg1.what = 1;
            DecimalFormat df = new DecimalFormat("#.00");

            msg1.obj = (df.format(lastDist));
            mHandler.sendMessage(msg1);

            Message msg2 = new Message();
            msg2.what = 2;

            msg2.obj = (df.format(lastDistR));
            mHandler.sendMessage(msg2);


        }

        global.audioRecord.stop();

        SaveFile saveFile = new SaveFile(global.L_I, global.L_Q, global.R_I,
                global.R_Q, global.btnPlayRecord);
        saveFile.execute("");
    }


    private void AddDataToList(ArrayList<Double>[] list, double[] data, boolean left) {
        if (data.length != 880) {
            global.btnPlayRecord.setEnabled(true);
            global.btnStopRecord.setEnabled(false);
            global.FPlay.colseWaveZ();
            global.flag = false;
            return;
        }

        boolean all_zero_flag = true;
        for (int i = 0; i < 880; i++) {
            if (data[i] != 0) {
                all_zero_flag = false;
            }
        }

        if (all_zero_flag) {
            return;
        }
        int count = -1;
        for (int i = 0; i < 880; i++) {
            if (i % 110 == 0) {
                count++;
            }
            list[count].add(data[i]);

        }

    }


    public native void DemoNew();

    public native int DemoL(short[] Record, double[] DIST, double[] tempII, double[] tempQQ);

    public native int DemoR(short[] Record, double[] DIST, double[] tempII, double[] tempQQ);


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

}
