package com.lee.edu.mydemo;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by dmrf on 18-1-25.
 */

public class SaveFile extends AsyncTask<String, Void, Void> {

    private String filename;
    private String filepath;
    private ArrayList<Double>content[];
    private int length;

    public SaveFile(String filename, String filepath, ArrayList<Double> content[]) {
        this.filename = filename;
        this.filepath = filepath;
        this.content = content;
    }


    @Override
    protected Void doInBackground(String... strings) {
        File file = new File(filepath + filename);

        FileOutputStream outStream = null;
        try {
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            outStream = new FileOutputStream(file,true);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, "gb2312");
            for (int i=0;i<8;i++){
                for (int j=0;j<content[i].size();j++){
                    writer.write(String.valueOf(content[i].get(j)));//这边改成浮点型不知道会不会出错
                    writer.write("\n");
                }
            }
            writer.close();
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
