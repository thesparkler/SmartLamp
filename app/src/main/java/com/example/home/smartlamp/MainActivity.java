package com.example.home.smartlamp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    ImageView imgSwitchOff;
    TextView tv_percent;
    ArcSeekBar arcSeekBar;
    ImageView imgLampOff, imgLight;
    MediaPlayer mediaPlayer;
    private boolean isOn = false;
    TextView textView;
    ImageView imgMic;
    ImageView imgInfo;


    private TextToSpeech myTTS;
    private SpeechRecognizer mySpeechRecognizer;

    // Dialogs
    Dialog QuestionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeTextToSpeech();
        initializeSpeechRecognizer();

        // Initialize dialog
        QuestionDialog = new Dialog(this);


        imgSwitchOff = (ImageView)findViewById(R.id.img_switch_off);
        tv_percent = (TextView)findViewById(R.id.light_percentage);
        imgMic = (ImageView)findViewById(R.id.mic);
        imgInfo = (ImageView)findViewById(R.id.info);

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionsDialog();
            }
        });

        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mySpeechRecognizer.startListening(intent);
            }
        });


        arcSeekBar = (ArcSeekBar)findViewById(R.id.seekBar);


        imgLampOff = (ImageView)findViewById(R.id.lamp_off);
        imgLight = (ImageView)findViewById(R.id.img_light);
        textView = (TextView)findViewById(R.id.text);

        ProgressListener progressListener = progress ->{
                                                     tv_percent.setText(progress +"");

                                                        imgLight.setAlpha((float) progress / 100);
                                                     };

        progressListener.invoke(0);

        arcSeekBar.setMaxProgress(100);

        arcSeekBar.setOnProgressChangedListener(progressListener);


        imgSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isOn)
                {
                    imgSwitchOff.setImageResource(R.drawable.ic_switch_on);
                    playSound();
                    imgLampOff.setImageResource(R.drawable.lamp_on);
                    imgLight.setVisibility(View.VISIBLE);

                    isOn = true;
                }
                else {
                    playSound();
                    imgSwitchOff.setImageResource(R.drawable.ic_switch_off);
                    imgLampOff.setImageResource(R.drawable.lamp_off);
                    imgLight.setVisibility(View.INVISIBLE);

                    isOn = false;
                }
            }
        });
    }


    private void playSound()
    {
        if(isOn)
        {
            mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.light_switch_off);
        }
        else
        {
            mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.light_switch_on);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }


    private void initializeTextToSpeech()
    {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size() == 0){
                    Toast.makeText(MainActivity.this, "There is no TTS engine on your device", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    myTTS.setLanguage(Locale.ENGLISH);
                    speak("Hello! I am ready.");
                }

            }
        });
    }

    private void speak(String message)
    {
        if(Build.VERSION.SDK_INT >= 21)
        {
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else{
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTTS.shutdown();
    }



    private void initializeSpeechRecognizer()
    {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> res = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(res.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void processResult(String command) {
        command = command.toLowerCase();

        // Question Command
        if(command.contains("what's your name")){
            speak("My name is smart lamp.");
        }

        if(command.contains("what's time")){
            Date now = new Date();
            String time = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_TIME);
            speak("The time now is " + time);
        }

        if(command.contains("name of your developer")){
            speak("his name is amit");
        }

        if(command.contains("make brightness")){
            speak("ok");
            tv_percent.setText("50");
            arcSeekBar.setProgress(50);
            ProgressListener progressListener = progress -> { imgLight.setAlpha((float) progress / 50);
            };
        }

        if(command.contains("what's your life story")){
            speak("I'm still on the very first chapter.");
        }

        if(command.contains("who is your hero")){
            speak("I'm a fan of refrigerators, they are very cool.");
        }

        if(command.contains("do you sleep")){
            speak("I take power naps when we aren't talking.");
        }

        /*
         logic for on the lamp
          */
        if(command.contains("switch on the lamp")) {
            if (!isOn) {
                speak("okay");
                imgSwitchOff.setImageResource(R.drawable.ic_switch_on);
                playSound();
                imgLampOff.setImageResource(R.drawable.lamp_on);
                imgLight.setVisibility(View.VISIBLE);

                isOn = true;
            } else {
                speak("hey, lamp is already on");

                return;
            }
        }

        /*
        logic for off the lamp
         */
        if(command.contains("switch off the lamp"))
        {
            if(isOn){
                speak("okay");
                playSound();
                imgSwitchOff.setImageResource(R.drawable.ic_switch_off);
                imgLampOff.setImageResource(R.drawable.lamp_off);
                imgLight.setVisibility(View.INVISIBLE);

                isOn = false;
            }
            else{
                 speak("hey, lamp is already off");

                return;
            }

        }

    }

    private void QuestionsDialog()
    {
        QuestionDialog.setContentView(R.layout.question_dialog);
        QuestionDialog.show();
        QuestionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        QuestionDialog.setCanceledOnTouchOutside(true);
        QuestionDialog.setCancelable(true);
    }

}



