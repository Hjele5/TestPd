package com.example.hjalte.testpd;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    boolean weather = false;
    private PdService pdService = null;

    private PdUiDispatcher dispatcher;



    private void initPD() throws IOException{
        int sampleRate = AudioParameters.suggestSampleRate();
        pdService.initAudio(sampleRate,0,2,8);
        pdService.startAudio();

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private void initGUI3(){
        ToggleButton btn = (ToggleButton) findViewById(R.id.toggleButton);
        btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f;
                PdBase.sendFloat("pausePlay",val);
                }
        });
    };


    private void initGUI1() {
        ToggleButton weatherSwitch = (ToggleButton) findViewById(R.id.weatherSwitch);
        weatherSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                float val = (b) ? 1.0f : 0.0f;
                PdBase.sendFloat("modOnOff",val);

            }
        });
    }

    private void initGUI(){
        ToggleButton onOffSwitch = (ToggleButton) findViewById(R.id.onOffSwitch);
        onOffSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    float vul = (b) ? 1.0f : 0.0f;
                    PdBase.sendFloat("songOn", vul);
                    PdBase.sendFloat("onOff", vul);
                    PdBase.sendFloat("musicPlay",vul);
                    PdBase.sendFloat("volumeAdjustment", vul);
                    PdBase.sendFloat("modOff", vul);
                }});
    }

    private void loadPDPatch() throws IOException {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.highpass),dir,true);
        File pdPatch = new File(dir,"highpass.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());

    }

    private final ServiceConnection pdConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pdService = ((PdService.PdBinder)service).getService();
            try{
                initPD();
                loadPDPatch();
            } catch (IOException e) {
                e.printStackTrace();
                finish();

            }}

            @Override
            public void onServiceDisconnected(ComponentName name){

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGUI();
        initGUI1();
        initGUI3();
        bindService(new Intent(this,PdService.class),pdConnection,BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(pdConnection);
    }
}
