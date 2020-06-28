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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelectSlotActivity extends AppCompatActivity {

    TextView slot;
    Button enroll;
    int userId,courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_slot);

        slot=(TextView)findViewById(R.id.slot);
        enroll=(Button)findViewById(R.id.enrollSlot);
        getSupportActionBar().setTitle("Slot");
        Intent intent=getIntent();
        int userid=intent.getIntExtra("userId",0);
        int courseid=intent.getIntExtra("courseId",0);
        userId=userid;
        courseId=courseid;

        SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("userId",userId);
        new Task1().execute();
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute();
                Toast.makeText(getApplicationContext(),"Enroll Success", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(SelectSlotActivity.this,HomeActivity.class);
                intent1.putExtra("userId",userId);
                startActivity(intent1);
            }
        });
    }
    class Task extends AsyncTask<Void, Void, Void> {

        String  error="";
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();

                String sql="INSERT INTO enrollment(user_id,course_id,payment,completion,date,marks)"+"values(?,?,?,?,?,?)";
                PreparedStatement pstmt=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1,userId);
                pstmt.setInt(2,courseId);
                pstmt.setInt(3,0);
                pstmt.setInt(4,0);
                pstmt.setString(5,date);
                pstmt.setInt(6,0);
                pstmt.executeUpdate();

            }
            catch (Exception e){
                error=e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(error != "") {
                Toast.makeText(getApplicationContext(),error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
    class Task1 extends AsyncTask<Void, Void, Void> {

        String  records="", error="";

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

                resultSet=statement.executeQuery("Select * from slot");
                while (resultSet.next()){
                    if(resultSet.getInt(2)==courseId){
                        records+="\n"+resultSet.getDate(3);
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
            slot.setText(records);
            if(error != "") {
                Toast.makeText(SelectSlotActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }


}
