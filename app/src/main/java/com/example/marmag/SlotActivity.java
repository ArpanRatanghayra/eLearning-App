package com.example.marmag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SlotActivity extends AppCompatActivity {

    TextView slot,link,mark,quiz;
    int userId,courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);

        Intent intent = getIntent();
        slot=(TextView)findViewById(R.id.slot);
        link=(TextView)findViewById(R.id.link);
        mark=(TextView)findViewById(R.id.mark);
        quiz=(TextView)findViewById(R.id.quiz);
        int userid=intent.getIntExtra("userId",0);
        final int courseid=intent.getIntExtra("courseId",0);
        userId=userid;
        courseId=courseid;

        SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("userId",userId);
        getSupportActionBar().setTitle("Course Details");
        new Task().execute();
        slot.setMovementMethod(new ScrollingMovementMethod());
        link.setMovementMethod(new ScrollingMovementMethod());
        mark.setMovementMethod(new ScrollingMovementMethod());
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quiz.getText().toString().equals("")){
                    Toast.makeText(SlotActivity.this,"No Quiz Available",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent1 = new Intent(SlotActivity.this, QuizActivity.class);
                    intent1.putExtra("userId", userId);
                    intent1.putExtra("courseId", courseId);
                    intent1.putExtra("quiz", quiz.getText().toString());
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }
    class Task extends AsyncTask<Void, Void, Void> {

        String  records="", error="", aa="", links="",quizz="";
        int marks;

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM course");

                while(resultSet.next()){
                    if(resultSet.getInt(1)==courseId){
                        records+=(resultSet.getString(6))+" - "+(resultSet.getString(7));
                    }
                }

                resultSet=statement.executeQuery("Select * FROM slot");
                int count=0;
                while (resultSet.next()){
                    if(resultSet.getInt(2)==courseId) {
                        count++;
                        records += "\n" + "Class " + count + ": " + resultSet.getDate(3);
                        if (resultSet.getString(4) != null) {
                            links = resultSet.getString(4);
                        }
                        if (resultSet.getString(5) != null) {
                            quizz = resultSet.getString(5);
                        }
                    }
                }

                resultSet=statement.executeQuery("Select * FROM enrollment");
                while (resultSet.next()){
                    if(resultSet.getInt(2)==userId && resultSet.getInt(3)==courseId){
                        marks=resultSet.getInt(7);
                    }
                }
            }
            catch (Exception e){
                error=e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            link.setText(links);
            slot.setText(records);
            aa=Integer.toString(marks);
            mark.setText(aa);
            quiz.setText(quizz);
            if(error != "") {
                Toast.makeText(SlotActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
