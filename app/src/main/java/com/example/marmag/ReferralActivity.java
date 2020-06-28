package com.example.marmag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReferralActivity extends AppCompatActivity {

    TextView email;
    Button submit,skip;
    String EMAIL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        email=(TextView)findViewById(R.id.email);
        submit=(Button)findViewById(R.id.submit);
        skip=(Button)findViewById(R.id.skip);

        getSupportActionBar().setTitle("Referral");
        Intent intent = getIntent();
        String Email=intent.getStringExtra("email");
        EMAIL=Email;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(ReferralActivity.this,LoginActivity.class);
                startActivity(intent1);
            }
        });
    }
    class Task extends AsyncTask<Void, Void, Void> {

        String  Name="", error="", Email="", Phone="",Password="";
        int marks;

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
                String a=email.getText().toString();

                while(resultSet.next()){
                    if(a.equals(resultSet.getString(3))){
                        int balance=resultSet.getInt(5);
                        balance+=100;
                        String sql="UPDATE user "+"SET Balance=? "+"WHERE Email=?";
                        PreparedStatement pstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                        pstmt.setInt(1,balance);
                        pstmt.setString(2,EMAIL);
                        pstmt.executeUpdate();
                        pstmt.setString(2,a);
                        pstmt.executeUpdate();
                        Intent intent1=new Intent(ReferralActivity.this,LoginActivity.class);
                        startActivity(intent1);
                    }
                }
                error="Referral Incorect";
            }
            catch (Exception e){
                error=e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(error != "") {
                Toast.makeText(ReferralActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }

}
