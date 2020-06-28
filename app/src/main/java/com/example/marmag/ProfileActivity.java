package com.example.marmag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProfileActivity extends AppCompatActivity {

    TextView name,email,phone,password;
    Button logout;
    int userId,courseId;
    private DrawerLayout dl;
    private ActionBarDrawerToggle to;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Intent intent = getIntent();
        final int userid=intent.getIntExtra("userId",0);
        int courseid=intent.getIntExtra("courseId",0);
        userId=userid;
        courseId=courseid;

        SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("userId",userId);
        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        password=(TextView)findViewById(R.id.password);
        logout=(Button)findViewById(R.id.logout);
        new Task().execute();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task1().execute();
                Intent intent1=new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(ProfileActivity.this,ClassroomActivity.class);
                intent1.putExtra("userId",userId);
                startActivity(intent1);
                finish();
            }
        });
        dl = (DrawerLayout)findViewById(R.id.activity_profile);
        to = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(to);
        to.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        Intent intent1 = new Intent(ProfileActivity.this, HomeActivity.class);
                        intent1.putExtra("userId", userId);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.classroom:
                        Intent intent2 = new Intent(ProfileActivity.this, CourseActivity.class);
                        intent2.putExtra("userId", userId);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.profile:
                        Intent intent3 = new Intent(ProfileActivity.this, ProfileActivity.class);
                        intent3.putExtra("userId", userId);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.wallet:
                        Intent intent4 = new Intent(ProfileActivity.this, WalletActivity.class);
                        intent4.putExtra("userId", userId);
                        startActivity(intent4);
                        finish();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(to.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
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

                while(resultSet.next()){
                    if(resultSet.getInt(1)==userId){
                        Name=resultSet.getString(2);
                        Email=resultSet.getString(3);
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
            name.setText(Name);
            email.setText(Email);
            if(error != "") {
                Toast.makeText(ProfileActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
    class Task1 extends AsyncTask<Void, Void, Void> {

        String  Name="", error="", Email="", Phone="",Password="";
        int marks;

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                String sql="UPDATE user "+"SET active=? "+"WHERE id=?";
                PreparedStatement pstmt=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1,0);
                pstmt.setInt(2,userId);
                pstmt.executeUpdate();
            }
            catch (Exception e){
                error=e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            name.setText(Name);
            email.setText(Email);
            if(error != "") {
                Toast.makeText(ProfileActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
