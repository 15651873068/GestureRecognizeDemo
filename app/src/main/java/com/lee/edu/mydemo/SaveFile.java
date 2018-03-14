package com.lee.edu.mydemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dmrf on 18-1-25.
 */

public class SaveFile extends AsyncTask<String, Void, Void> {

    public SaveFile(ArrayList<Double>[] l_i, ArrayList<Double>[] l_q, ArrayList<Double>[] r_i, ArrayList<Double>[] r_q, Button start) {
        this.l_i = l_i;
        this.l_q = l_q;
        this.r_i = r_i;
        this.r_q = r_q;
        this.start = start;

    }

    private ArrayList<Double> l_i[];
    private ArrayList<Double> l_q[];
    private ArrayList<Double> r_i[];
    private ArrayList<Double> r_q[];
    private Button start;
    private Context context;


    @Override
    protected Void doInBackground(String... strings) {
        Calendar c = Calendar.getInstance();
        int mDay = c.get(Calendar.DAY_OF_MONTH);//日期
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int mSecond = c.get(Calendar.SECOND);//秒
        String filePath_L = Environment.getExternalStorageDirectory() + "/ASSET/Data/L/";
        String filePath_R = Environment.getExternalStorageDirectory() + "/ASSET/Data/R/";
        String filename_I = "I_" + mDay + mHour + mMinute + mSecond + ".txt";
        String filename_Q = "Q_" + mDay + mHour + mMinute + mSecond + ".txt";


        File file_l_i = new File(filePath_L + filename_I);
        File file_l_q = new File(filePath_L + filename_Q);
        File file_r_i = new File(filePath_R + filename_I);
        File file_r_q = new File(filePath_R + filename_Q);


        FileOutputStream outStream_l_i = null;
        FileOutputStream outStream_l_q = null;
        FileOutputStream outStream_r_i = null;
        FileOutputStream outStream_r_q = null;


        try {
            if (!file_l_i.exists()) {
                File dir = new File(file_l_i.getParent());
                dir.mkdirs();
                file_l_i.createNewFile();
            }

            if (!file_l_q.exists()) {
                File dir1 = new File(file_l_q.getParent());
                dir1.mkdirs();
                file_l_q.createNewFile();
            }

//            if (!file_r_i.exists()) {
//                File dir2 = new File(file_r_i.getParent());
//                dir2.mkdirs();
//                file_r_i.createNewFile();
//            }
//
//            if (!file_r_q.exists()) {
//                File dir3 = new File(file_r_q.getParent());
//                dir3.mkdirs();
//                file_r_q.createNewFile();
//            }


            outStream_l_i = new FileOutputStream(file_l_i, true);
            outStream_l_q = new FileOutputStream(file_l_q, true);
//            outStream_r_i = new FileOutputStream(file_r_i, true);
//            outStream_r_q = new FileOutputStream(file_r_q, true);

            OutputStreamWriter writer_l_i = new OutputStreamWriter(outStream_l_i, "gb2312");
            OutputStreamWriter writer_l_q = new OutputStreamWriter(outStream_l_q, "gb2312");
//            OutputStreamWriter writer_r_i = new OutputStreamWriter(outStream_r_i, "gb2312");
//            OutputStreamWriter writer_r_q = new OutputStreamWriter(outStream_r_q, "gb2312");

            int len = l_i[0].size();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < len; j++) {
                    writer_l_i.write(String.valueOf(l_i[i].get(j)));//这边改成浮点型不知道会不会出错
                    writer_l_i.write("\n");
                    writer_l_q.write(String.valueOf(l_q[i].get(j)));//这边改成浮点型不知道会不会出错
                    writer_l_q.write("\n");
//                    writer_r_i.write(String.valueOf(r_i[i].get(j)));//这边改成浮点型不知道会不会出错
//                    writer_r_i.write("\n");
//                    writer_r_q.write(String.valueOf(r_q[i].get(j)));//这边改成浮点型不知道会不会出错
//                    writer_r_q.write("\n");

                }
            }

            writer_l_i.close();
            outStream_l_i.close();
            writer_l_q.close();
            outStream_l_q.close();
//            writer_r_i.close();
//            outStream_r_i.close();
//            writer_r_q.close();
//            outStream_r_q.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        start.setEnabled(true);
        //Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();

    }
}
