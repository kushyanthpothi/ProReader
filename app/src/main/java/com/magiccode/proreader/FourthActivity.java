package com.magiccode.proreader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class FourthActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mTextBox;
    private Button mSpeechToTextButton;
    private Button mTextToSpeechButton;
    private TextToSpeech mTextToSpeech;
    private Button copy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);
        mSpeechToTextButton=findViewById(R.id.button);
        mTextBox=findViewById(R.id.txtview);
        mTextToSpeechButton=findViewById(R.id.texttospeech);
        copy=findViewById(R.id.copybtn);
        getSupportActionBar().hide();

        mSpeechToTextButton.setOnClickListener(this);
        mTextToSpeechButton.setOnClickListener(this);

        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTextToSpeech.setLanguage(Locale.US);
                }

            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String output = mTextBox.getText().toString();
                if (output.isEmpty())
                    Toast.makeText(FourthActivity.this,"Your text box is empty!!",Toast.LENGTH_LONG).show();
                else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("MyData",output);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(FourthActivity.this, "Your Text is Copied!!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Intent speechToTextIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechToTextIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechToTextIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechToTextIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!");
                startActivityForResult(speechToTextIntent, 1);
                break;
            case R.id.texttospeech:
                String output = mTextBox.getText().toString();
                if (output.isEmpty())
                    Toast.makeText(FourthActivity.this,"Your text box is empty!!",Toast.LENGTH_LONG).show();
                String text = mTextBox.getText().toString();
                mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            mTextBox.setText(spokenText);
        }
    }
}