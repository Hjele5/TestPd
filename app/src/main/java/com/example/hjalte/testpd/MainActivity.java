package com.example.hjalte.testpd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    boolean weather = false;

    private PdUiDispatcher dispatcher;

    public void buttonOnClick(View v){
        Button button=(Button) v;
        ((Button)v).setText("clicked");

    }


    private void initPD() throws IOException{
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate,0,2,8,true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    private void initGUI1() {
        Switch weatherSwitch = (Switch) findViewById(R.id.weatherSwitch);
        weatherSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                float val = (b) ? 1.0f : 0.0f;
                PdBase.sendFloat("modOnOff",val);

            }
        });
    }

    private void initGUI(){
        Switch onOffSwitch = (Switch) findViewById(R.id.onOffSwitch);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            initPD();
            loadPDPatch();
        }catch (IOException e){
            finish();
        }
        initGUI();
        initGUI1();
    }

    @Override
    protected void onResume(){
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        PdAudio.stopAudio();
    }
}
