package com.example.marmag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClassroomActivity extends AppCompatActivity {

    int userId=0;
    TextView pass,newpass;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        Intent intent=getIntent();
        int userid=intent.getIntExtra("userId",0);
        userId=userid;

        getSupportActionBar().setTitle("Change Password");
        pass=(TextView)findViewById(R.id.password);
        newpass=(TextView)findViewById(R.id.cpassword);
        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Task().execute();
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

                ResultSet resultSet = statement.executeQuery("SELECT * FROM user");

                while(resultSet.next()){
                    if(resultSet.getInt(1)==userId){
                        if(resultSet.getString(4).equals(pass.getText().toString())){

                            String sql="Update user "+"SET password=? "+"WHERE Id=?";
                            PreparedStatement pstmt=connection.prepareStatement(sql);
                            pstmt.setString(1,newpass.getText().toString());
                            pstmt.setInt(2,userId);
                            pstmt.executeUpdate();
                            error="Password Changed Successfully";
                            Intent intent= new Intent(ClassroomActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        error="Password did not match";
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
                Toast.makeText(ClassroomActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }

    }
}
