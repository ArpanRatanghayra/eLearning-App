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

public class SignUpActivity extends AppCompatActivity {

    TextView name,password,email,login;
    Button signUp;
    int temp=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("SignUp");
        name=(TextView)findViewById(R.id.fullName);
        email=(TextView)findViewById(R.id.email);
        password=(TextView)findViewById(R.id.password);
        login=(TextView)findViewById(R.id.textView9);
        signUp=(Button)findViewById(R.id.signUp);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
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
                    if(email.getText().toString().equals(resultSet.getString(3))){
                        Toast.makeText(getApplicationContext(),"Email Exists",Toast.LENGTH_LONG).show();
                        temp=1;
                    }
                }
                if(temp==0) {
                    String sql = "INSERT INTO user(Name,Email,Password,Balance,Active)" + "values(?,?,?,?,?)";
                    PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    if(name.getText().toString().equals("") || password.getText().toString().equals("") || email.getText().toString().equals("")) {
                       error="Empty Input";
                    }
                    else {
                        String Email=email.getText().toString();
                        pstmt.setString(1, name.getText().toString());
                        pstmt.setString(2, email.getText().toString());
                        pstmt.setString(3, password.getText().toString());
                        pstmt.setInt(4, 0);
                        pstmt.setInt(5, 0);
                        pstmt.executeUpdate();
                        Intent intent=new Intent(SignUpActivity.this,ReferralActivity.class);
                        intent.putExtra("email",Email);
                        startActivity(intent);
                        finish();
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
                Log.i("error",error);
                Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);

        }
    }
}
