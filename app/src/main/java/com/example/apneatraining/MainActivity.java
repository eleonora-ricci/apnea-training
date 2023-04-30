package com.example.apneatraining;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText maxTimeEditText;
    private Button saveButton;
    private Button startButton;
    private TextView timerTextView;
    private TextView practiceTypeView;
    private TextView cycleMessage;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds;
    private boolean timerRunning;

    private int table;  // 0 means CO2, 1 means O2
    private int cycles = 0;
    private int numCycles = 8;
    long[] breatheRatiosCO2 = {833, 750, 667, 583, 500, 417, 333, 333};
    long[] breatheRatiosO2 = {667, 667, 667, 667, 667, 667, 667, 667};
    long[] holdRatiosCO2 = {500, 500, 500, 500, 500, 500, 500, 500};
    long[] holdRatiosO2 = {333, 417, 500, 583, 667, 750, 833, 833};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button co2Button = findViewById(R.id.co2_button);
        Button o2Button = findViewById(R.id.o2_button);

        maxTimeEditText = findViewById(R.id.input_edittext);
        maxTimeEditText.setVisibility(View.INVISIBLE);
        saveButton = findViewById(R.id.save_button);
        saveButton.setVisibility(View.INVISIBLE);

        co2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table = 0;
                Toast.makeText(MainActivity.this, "CO2 table selected", Toast.LENGTH_SHORT).show();
                saveButton.setVisibility(View.VISIBLE);
                maxTimeEditText.setVisibility(View.VISIBLE);
            }
        });

        o2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table = 1;
                Toast.makeText(MainActivity.this, "O2 table selected", Toast.LENGTH_SHORT).show();
                saveButton.setVisibility(View.VISIBLE);
                maxTimeEditText.setVisibility(View.VISIBLE);
            }
        });



        startButton = findViewById(R.id.start_button);
        timerTextView = findViewById(R.id.timer_layout);
        practiceTypeView = findViewById(R.id.practice_type);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxTimeString = maxTimeEditText.getText().toString();
                if (!maxTimeString.equals("")) {
                    long maxTimeSeconds = Long.parseLong(maxTimeString);
                    timeLeftInMilliseconds = maxTimeSeconds * 1000;
                    maxTimeEditText.setVisibility(View.INVISIBLE);
                    saveButton.setVisibility(View.INVISIBLE);
                    startButton.setVisibility(View.VISIBLE);

                    co2Button.setVisibility(View.INVISIBLE);
                    o2Button.setVisibility(View.INVISIBLE);
                    practiceTypeView.setVisibility(View.INVISIBLE);

                    updateTimer();
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    startButton.setText("pause");
                    startTimer();
                } else {
                    startButton.setText("start");
                    pauseTimer();
                }
            }
        });

        timerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    cycles = 0;
                    countDownTimer.cancel();
                    startButton.setText("start");
                    timerRunning = false;
                }
            }
        });
    }

    private void startTimer() {

        cycleMessage = findViewById(R.id.cycle_message);

        if (cycles == numCycles * 2) {
            cycles = 0;
        }
        if (cycles % 2 == 0) {
            //isBreathing = true;
            cycleMessage.setText("Breathe");
            if (table == 0) {
                timeLeftInMilliseconds = Long.parseLong(maxTimeEditText.getText().toString()) * breatheRatiosCO2[cycles];
            } else {
                timeLeftInMilliseconds = Long.parseLong(maxTimeEditText.getText().toString()) * breatheRatiosO2[cycles];
            }

        } else {
            //isBreathing = false;
            cycleMessage.setText("Hold");
            if (table == 0) {
                timeLeftInMilliseconds = Long.parseLong(maxTimeEditText.getText().toString()) * holdRatiosCO2[cycles];
            } else {
                timeLeftInMilliseconds = Long.parseLong(maxTimeEditText.getText().toString()) * holdRatiosO2[cycles];
            }
        }

        //timeLeftInMilliseconds = timeLeftInMilliseconds - timeLeftInMilliseconds % 500;

        cycleMessage.setVisibility(View.VISIBLE);

        updateTimer();
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                if (cycles < numCycles * 2 - 1) {
                    cycles++;
                    startTimer();
                } else {
                    cycles = 0;
                    timerRunning = false;
                    startButton.setText("Start");
                }
            }
        }.start();

        timerRunning = true;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMilliseconds / 60000;
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }
}
