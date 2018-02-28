package com.lee.edu.mydemo;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Double> L_I[];
    private ArrayList<Double> L_Q[];
    private ArrayList<Double> R_I[];
    private ArrayList<Double> R_Q[];

    private double[] Freqarrary = {17500, 17850, 18200, 18550, 18900, 19250, 19600, 19950, 20300, 20650};        //设置播放频率
    private int numfre = 8;
    private Button btnPlayRecord;        //开始按钮
    private Button btnStopRecord;        //结束按钮
    private Spinner userSpinner;
    private Spinner gestureSpinner;
    private TextView tvDist;            //显示距离
    private TextView tvDist2;            //显示距离
    private boolean flag = true;        //播放标志
    private AudioRecord audioRecord;    //录音对象
    private int recBufSize = 4400 * 2;            //定义录音片长度
    private int count = 0;
    FrequencyPlayer FPlay;
    /**
     * 采样率（默认44100，每秒44100个点）
     */
    private int sampleRateInHz = 44100;
    /**
     * 编码率（默认ENCODING_PCM_16BIT）
     */
    private int encodingBitrate = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 声道（默认单声道）
     */
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;        //立体道  MONO单声道，STEREO立体声
    /**
     * 1s内17500hz的波值
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        //设置圆环角度
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            RequestPermission();
        }

        InitData();
        InitView();

        InitListener();

    }

    private void InitData() {

        L_I = new ArrayList[8];
        L_Q = new  ArrayList[8];
        R_I = new  ArrayList[8];
        R_Q = new  ArrayList[8];
        for (int i=0;i<8;i++){
            ArrayList<Double> list1=new ArrayList<Double>();
            ArrayList<Double> list2=new ArrayList<Double>();
            ArrayList<Double> list3=new ArrayList<Double>();
            ArrayList<Double> list4=new ArrayList<Double>();
            L_I[i]=list1;
            L_Q[i]=list2;
            R_I[i]=list3;
            R_Q[i]=list4;
        }

    }


    private void InitView() {
        btnPlayRecord = (Button) findViewById(R.id.btnplayrecord);
        btnStopRecord = (Button) findViewById(R.id.btnstoprecord);
        userSpinner = (Spinner) findViewById(R.id.spinner_user);
        gestureSpinner = (Spinner) findViewById(R.id.spinner_gesture);

        ArrayList<String> user_list = new ArrayList<String>();
        user_list.add("XUE");
        user_list.add("WANG");
        user_list.add("MLI");
        user_list.add("CAI");
        user_list.add("WLI");
        user_list.add("ZHANG");

        ArrayAdapter userAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, user_list);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
        ArrayList<String> gesture_list = new ArrayList<String>();
        for (int i = 1; i <= 15; i++) {
            gesture_list.add(String.valueOf((char) (i + 64)));
        }
        ArrayAdapter gestureAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gesture_list);
        gestureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gestureSpinner.setAdapter(gestureAdapter);

        tvDist = (TextView) findViewById(R.id.textView1);
        tvDist.setText(String.valueOf(0));
        tvDist2 = (TextView) findViewById(R.id.textView2);
        tvDist2.setText(String.valueOf(0));
        btnStopRecord.setEnabled(false);    //

        int minBufSize = recBufSize;      //0.1s

        /*
        public AudioRecord (int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes)
        参数解释：
        audioSource  音频源：指的是从哪里采集音频。这里我们当然是从麦克风采集音频，所以此参数的值为MIC
        sampleRateInHz  采样率：音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。给出的实例是
                                                      44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、
                                                      8000等低采样率。

          channelConfig   声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
           audioFormat  编码制式和采样大小：采集来的数据当然使用PCM编码(脉冲代码调制编码，即PCM编码。
                                        PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码。)
                                        android支持的采样大小16bit 或者8bit。当然采样大小越大，那么信息量越多，音质也越高，
                                        现在主流的采样大小都是16bit，在低质量的语音传输的时候8bit足够了。
           bufferSizeInBytes  采集数据需要的缓冲区的大小，如果不知道最小需要的大小可以在getMinBufferSize()查看。


           采集到的数据保存在一个byteBuffer中，可以使用流将其读出。亦可保存成为文件的形式
         */
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//从麦克风采集音频
                sampleRateInHz,//采样率，这里的值是sampleRateInHz = 44100即每秒钟采样44100次
                channelConfig,//声道设置，MONO单声道，STEREO立体声，这里用的是立体声
                encodingBitrate,//编码率（默认ENCODING_PCM_16BIT）
                minBufSize);//录音片段的长度，给的是minBufSize=recBufSize = 4400 * 2;
    }


    private void InitListener() {
        //17500Hz的波形
        /**
         * 这部分取最大波形恐怕有问题，录取声音不一定为小于1的正弦波
         *
         */
        //播放按钮
        btnPlayRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlayRecord.setEnabled(false);
                btnStopRecord.setEnabled(true);


                if (L_I[0]!=null){
                    for (int i=0;i<8;i++){
                        L_I[i].clear();
                        L_Q[i].clear();
                        R_I[i].clear();
                        R_Q[i].clear();
                    }
                }




                count++;
                flag = true;
                new ThreadInstantPlay().start();        //播放(发射超声波)


                try {
                    Thread.sleep(10);    //等待开始播放再录音
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                new ThreadInstantRecord().start();        //录音
                //录音播放线程
            }
        });

        //停止按钮
        btnStopRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                btnPlayRecord.setEnabled(true);
                btnStopRecord.setEnabled(false);
                FPlay.colseWaveZ();
                flag = false;
            }
        });
    }

    /**
     * 即时播放线程
     *
     * @author lisi
     */
    class ThreadInstantPlay extends Thread {
        @Override
        public void run() {
            FPlay = new FrequencyPlayer(numfre, Freqarrary);
            FPlay.palyWaveZ();
            while (flag) {
            }
            FPlay.colseWaveZ();
        }
    }

    /**
     * 即时录音线程
     */
    class ThreadInstantRecord extends Thread {
        @Override
        public void run() {
            short[] bsRecord = new short[recBufSize];//recBufSize=4400*2

            short[] bsRecordL = new short[recBufSize / 2];
            short[] bsRecordR = new short[recBufSize / 2];

            short[] BIGDATA = new short[44100 * 30];
            short[] BIGDATA2 = new short[44100 * 30];

            double[] BIGDATAIIL = new double[44100 * 30];//这边4个从1开始取 注意+1
            double[] BIGDATAQQL = new double[44100 * 30];

            double[] BIGDATAIIR = new double[44100 * 30];
            double[] BIGDATAQQR = new double[44100 * 30];
            //八个频率调整好的数据
            double[] needIL = new double[880 + 2];
            double[] needQL = new double[880 + 2];

            double[] needIR = new double[880 + 2];
            double[] needQR = new double[880 + 2];

            int n = 0;
            double totPhase = 0;
            double lastDist = 0;
            double lastDistR = 0;
            double NowPhase = 0;
            //--------------jni------------------------
            DemoNew();

            while (flag == false) {
            }
            try {
                audioRecord.startRecording();

            } catch (IllegalStateException e) {
                // 录音开始失败
                Toast.makeText(MainActivity.this, "录音开始失败！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            //Log.w("tip","start");
            int Len;
            long start = System.currentTimeMillis();

            Calendar c = Calendar.getInstance();
            int mDay = c.get(Calendar.DAY_OF_MONTH);//日期
            int mHour = c.get(Calendar.HOUR_OF_DAY);//时
            int mMinute = c.get(Calendar.MINUTE);//分
            int mSecond = c.get(Calendar.SECOND);//秒
            String user = (String) userSpinner.getSelectedItem();
            String gesture = (String) gestureSpinner.getSelectedItem();


            while (flag)//大循环
            {

                long end = System.currentTimeMillis();
                if (end - start >= 3000) {
                    flag = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FPlay.colseWaveZ();
                            btnPlayRecord.setEnabled(true);
                            btnStopRecord.setEnabled(false);

                        }
                    });

                }


                //读取录音
                // short[] bsRecord = new short[recBufSize];//recBufSize=4400*2

                Len = audioRecord.read(bsRecord, 0, recBufSize);

                /*
                public int read (short[] audioData, int offsetInShorts, int sizeInShorts)     从音频硬件录制缓冲区读取数据。

           　　参数:

                            audioData        写入的音频录制数据。

                            offsetInShorts           目标数组 audioData 的起始偏移量。

                            sizeInShorts              请求读取的数据大小。

　　　　　　返回值:
　　                  返回short型数据，表示读取到的数据，如果对象属性没有初始化，则返回ERROR_INVALID_OPERATION，
                            如果参数不能解析成有效的数据或索引，则返回ERROR_BAD_VALUE。 返回数值不会超过sizeInShorts。
                 */

                /*
                BIGDATA记录的是整个手势的过程中产生的数据
                bsRecord记录的是单个循环中产生的数据
                 */
//

                /*
                降噪（高通滤波）
                 */


                System.out.println(Len);


                //将读到数据的L和R分开
                for (int i = 0; i < Len; i++) {
                    BIGDATA[n] = bsRecord[i];
                    bsRecordL[i / 2] = bsRecord[i++];

                    BIGDATA2[n++] = bsRecord[i];
                    bsRecordR[i / 2] = bsRecord[i];
                }


                double[] di = new double[110];
                //-----------------------你们需要的数据就是这个tempII 和tempQQ------------------------------------
                //-------------------------下面有保存方法saveToSDCard ，你们可以自己试着按照你们的需要保存----------------------------------
                double[] tempIIL = new double[880];
                double[] tempQQL = new double[880];

                DemoL(bsRecordL, di, tempIIL, tempQQL);
                AddDataToList(L_I,tempIIL);
                AddDataToList(L_Q,tempQQL);

                lastDist = di[110 - 1];

                double[] tempIIR = new double[880];
                double[] tempQQR = new double[880];
                DemoR(bsRecordR, di, tempIIR, tempQQR);
                AddDataToList(R_I,tempIIR);
                AddDataToList(R_Q,tempQQR);



                /*
                做的存储数据的操作
                 */


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
            }//while end


            String channel = "Left";
            String cnt = String.valueOf(count);
            String filePath = Environment.getExternalStorageDirectory() + "/ASSET" + "/" + user + "/" + gesture + "/";
            String temp = user + "_" + gesture + "_";
            //还没与学长原本的波形比较
            SaveFile saveFile1 = new SaveFile(temp + "I_" + mDay + mHour + mMinute + mSecond + ".txt", filePath + channel + "/", L_I);
            SaveFile saveFile2 = new SaveFile(temp + "Q_" + mDay + mHour + mMinute + mSecond + ".txt", filePath + channel + "/", L_Q);
            channel = "Right";
            SaveFile saveFile3 = new SaveFile(temp + "I_" + mDay + mHour + mMinute + mSecond + ".txt", filePath + channel + "/", R_I);
            SaveFile saveFile4 = new SaveFile(temp + "Q_" + mDay + mHour + mMinute + mSecond + ".txt", filePath + channel + "/", R_Q);

            saveFile1.execute("");
            saveFile2.execute("");
            saveFile3.execute("");
            saveFile4.execute("");


            audioRecord.stop();


        }

    }

    private void AddDataToList(ArrayList<Double>[] list, double[] data) {
        if (data.length!=880){
            Toast.makeText(MainActivity.this,"出现了未知bug，请联系小五改bug！",Toast.LENGTH_SHORT).show();
            btnPlayRecord.setEnabled(true);
            btnStopRecord.setEnabled(false);
            FPlay.colseWaveZ();
            flag = false;
            return;
        }

        boolean all_zero_flag=true;
        for (int i=0;i<880;i++){
            if (data[i]!=0){
                all_zero_flag=false;
            }
        }

        if (all_zero_flag){
            return;
        }
        int count=-1;
        for (int i=0;i<880;i++){
            if (i%110==0){
                count++;
            }
            list[count].add(data[i]);
       }


    }


    private void RequestPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionsUtil.hasPermission(MainActivity.this, permissions)) {
            //已经获取相关权限
        } else {
            PermissionsUtil.requestPermission(MainActivity.this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    //用户授予了权限
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    //用户拒绝了权限
                    Toast.makeText(MainActivity.this, "相关权限被拒绝，本应用将无法正常运行", Toast.LENGTH_SHORT).show();
                }
            }, permissions);
        }
    }

    //本地方法，由java调用
    public native String stringFromJNI(int[] I);

    public native void mycicFromJNI(int[] I, double[] II);

    public native void myADistFromJNI(double[] inC, double[] inS, double[] RE);

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