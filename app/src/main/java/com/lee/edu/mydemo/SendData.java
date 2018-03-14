package com.lee.edu.mydemo;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dmrf on 18-3-14.
 */

public class SendData extends AsyncTask<String, Void, Void> {

    private DataBean dataBean;
    private Context context;

    public SendData(DataBean dataBean, Context context) {
        this.dataBean = dataBean;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (dataBean!=null){
            dataBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Toast.makeText(context,"添加数据成功，返回objectId为："+s,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"数据创建失败"+s,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return null;
    }
}
