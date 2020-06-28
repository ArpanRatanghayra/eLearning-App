package com.example.marmag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SelectCourseActivity extends AppCompatActivity {

    TextView description;
    Button enroll;
    int userId,courseId,temp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        description=(TextView)findViewById(R.id.description);
        enroll=(Button)findViewById(R.id.enrollDescription);
        Intent intent=getIntent();
        int userid=intent.getIntExtra("userId",0);
        int courseid=intent.getIntExtra("courseId",0);
        userId=userid;
        courseId=courseid;

        SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("userId",userId);
        new Task().execute();
        getSupportActionBar().setTitle("Description");
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(temp==0) {
                    Intent intent1 = new Intent(SelectCourseActivity.this, SelectSlotActivity.class);
                    intent1.putExtra("userId", userId);
                    intent1.putExtra("courseId", courseId);
                    startActivity(intent1);
                }
            }
        });

    }
    class Task extends AsyncTask<Void, Void, Void> {

        String  error="";

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM course");

                while(resultSet.next()){
                    if(resultSet.getInt(1)==courseId){
                        description.setText(resultSet.getString(4));
                    }
                }
                resultSet=statement.executeQuery("Select * from enrollment");
                while(resultSet.next()){
                    if(resultSet.getInt(2)==userId && resultSet.getInt(3)==courseId){
                        temp=1;
                       error="Course Already Enrolled";
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
            if(error != "") {
                Toast.makeText(SelectCourseActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
