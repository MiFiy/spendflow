package com.example.spendflow;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class UpdateSpendActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button ton1, ton2;
    EditText text3, text4;
    TextView text1, dateTextView;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_spend);

        dbHelper = new DataHelper(this);
        text1 = (TextView) findViewById(R.id.editText1);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        text3 = (EditText) findViewById(R.id.editText3);
        text4 = (EditText) findViewById(R.id.editText4);

        // Initialize date to current date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        updateDateTextView(year, month, day);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Retrieve data from SQLite based on date passed from MainActivity
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM spend WHERE date = ?",
                new String[]{getIntent().getStringExtra("date")});

        if (cursor.moveToFirst()) {
            // If data found, populate EditText fields
            text1.setText(cursor.getString(0));
            dateTextView.setText(cursor.getString(1));
            text3.setText(cursor.getString(2));
            text4.setText(cursor.getString(3));
        }

        ton1 = (Button) findViewById(R.id.button1);
        ton2 = (Button) findViewById(R.id.button2);

        // Update button click listener
        ton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update data in SQLite database
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE spend SET date = ?, totalspend = ?, reference = ? " +
                                "WHERE number = ?",
                        new String[]{dateTextView.getText().toString(),
                                text3.getText().toString(),
                                text4.getText().toString(),
                                text1.getText().toString()});

                Toast.makeText(getApplicationContext(), "Data Successfully Updated", Toast.LENGTH_SHORT).show();

                // Notify MainActivity to refresh the list
                MainActivity.ma.RefreshList();
                finish(); // Close activity

                // Show notification
                showNotification();
            }
        });

        // Cancel button click listener
        ton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simply finish the activity
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        updateDateTextView(year, month, dayOfMonth);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateTextView(int year, int month, int day) {
        // Month is 0-indexed, so we add 1
        String date = day + "/" + (month + 1) + "/" + year;
        dateTextView.setText(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "spendflow_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel
                    = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "SpendFlow Notifications", NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel
            notificationChannel.setDescription("SpendFlow Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("SpendFlow Notification")
                .setContentText("Data Successfully Updated")
                .setContentInfo("Info");

        notificationManager.notify(1, notificationBuilder.build());
    }
}
