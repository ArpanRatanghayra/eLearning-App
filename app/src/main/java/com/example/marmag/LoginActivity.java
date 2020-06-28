package com.example.marmag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import static java.lang.Class.forName;

public class LoginActivity extends AppCompatActivity {

    TextView email,password,signup;
    Button login;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(TextView)findViewById(R.id.email);
        password=(TextView)findViewById(R.id.password);
        signup=(TextView)findViewById(R.id.textView9);
        login=(Button)findViewById(R.id.login);
        getSupportActionBar().setTitle("Login");
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });


        SharedPreferences sp=getSharedPreferences("shp",MODE_PRIVATE);
        int user=sp.getInt("userId", 0);
        if(user>0){
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            intent.putExtra("userId",user);
            startActivity(intent);
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute();
            }
        });
    }
    class Task extends AsyncTask<Void, Void, Void>{

        String  error="";

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
                String a=email.getText().toString();
                String b=password.getText().toString();
                while(resultSet.next()) {
                    if(a.equals(resultSet.getString(2))) {
                        if(b.equals(resultSet.getString(4))) {
                            userId=resultSet.getInt(1);
                            SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putInt("userId",userId);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("userId",resultSet.getInt(1));
                            String sql="UPDATE user " + "SET Active=? " + "WHERE id=?";
                            PreparedStatement pstmt=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                            pstmt.setInt(1,1);
                            pstmt.setInt(2,userId);//resultSet.getInt(1));
                            pstmt.executeUpdate();
                            startActivity(intent);
                            finish();
                        }
                        else{
                            error="Password Incorrect";
                            break;
                        }
                    }
                    error="User Incorrect";
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
                Toast.makeText(LoginActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
