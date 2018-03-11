package com.lee.edu.mydemo.Global;

import com.lee.edu.mydemo.FrequencyPlayer;

/**
 * Created by dmrf on 18-3-11.
 */

class ThreadInstantPlay extends Thread {
    private Global_data global;

    public ThreadInstantPlay(Global_data global_data) {
        this.global = global_data;
    }

    @Override
    public void run() {

        global.FPlay = new FrequencyPlayer(global.numfre, global.Freqarrary);
        global.FPlay.palyWaveZ();
        while (global.flag) {
        }
        global.FPlay.colseWaveZ();
    }
}
