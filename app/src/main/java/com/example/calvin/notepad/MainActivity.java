package com.example.calvin.notepad;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private EditText content;
    private TextView time;
    private Note note;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = findViewById(R.id.time);
        content = findViewById(R.id.note);

        content.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("NOTE", content.getText().toString());
        outState.putString("TIME",time.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        content.setText(savedInstanceState.getString("NOTE"));
        time.setText(savedInstanceState.getString("TIME"));
    }

    //Save time and note to JSON

    @Override
    protected void onPause() {
        note.setNote(content.getText().toString());
        note.setTime(time.getText().toString());
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        setTime();
        saveJSON();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        setTime();
        saveJSON();
        super.onStop();
    }

    public void setTime(){
        DateFormat df = new SimpleDateFormat("EEE, MMM d, h:mm a");
        String date = "Last Update: " + df.format(Calendar.getInstance().getTime());
        time.setText(date);
        note.setTime(date);
    }

    public void saveJSON(){
        try{
            FileOutputStream fos = getApplicationContext().openFileOutput("Note.json", Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.setIndent(" ");
            writer.beginObject();
            writer.name("time").value(note.getTime());
            writer.name("note").value(note.getNote());
            writer.endObject();
            writer.close();
        } catch (Exception e){
            e.getStackTrace();
        }
    }
    //Restore from JSON

    @Override
    protected void onResume() {
        note = loadJSON();
        if(note != null){
            time.setText(note.getTime());
            content.setText((note.getNote()));
        }
        super.onResume();
    }

    public Note loadJSON(){
        note = new Note();
        try{
            InputStream is = getApplicationContext().openFileInput("Note.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

            reader.beginObject();
            while(reader.hasNext()){
                String name = reader.nextName();
                if(name.equals("time")){
                    note.setTime(reader.nextString());
                }else if (name.equals("note")){
                    note.setNote(reader.nextString());
                }else{
                    reader.skipValue();
                }
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
        }

        return note;
    }
}
