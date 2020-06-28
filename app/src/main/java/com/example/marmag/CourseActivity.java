package com.example.marmag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    ListView enrolledCourse;
    ArrayList<String> enrolled;
    ArrayList<Integer> e_id;
    ArrayAdapter<String> e;
    int userId;
    private DrawerLayout dl;
    private ActionBarDrawerToggle to;
    private NavigationView nv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        enrolledCourse = (ListView)findViewById(R.id.enrolledCourse);
        enrolled = new ArrayList<String>();
        e_id = new ArrayList<Integer>();
        e = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, enrolled);
        getSupportActionBar().setTitle("Marmag Classroom");
        Intent intent = getIntent();
        int userid=intent.getIntExtra("userId",0);
        userId=userid;
        SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("userId",userId);
        new Task().execute();

        enrolledCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent1 = new Intent(CourseActivity.this,SlotActivity.class);
                intent1.putExtra("userId",userId);
                intent1.putExtra("courseId",e_id.get(i));
                startActivity(intent1);
                finish();
            }
        });
        dl = (DrawerLayout)findViewById(R.id.activity_course);
        to = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(to);
        to.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        Intent intent1 = new Intent(CourseActivity.this, HomeActivity.class);
                        intent1.putExtra("userId", userId);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.classroom:
                        Intent intent2 = new Intent(CourseActivity.this, CourseActivity.class);
                        intent2.putExtra("userId", userId);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.profile:
                        Intent intent3 = new Intent(CourseActivity.this, ProfileActivity.class);
                        intent3.putExtra("userId", userId);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.wallet:
                        Intent intent4 = new Intent(CourseActivity.this, WalletActivity.class);
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

        String records="",  error="";
        ArrayList<Integer> temp=new ArrayList<Integer>();

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from enrollment");
                while(resultSet.next()){
                    if(resultSet.getInt(2)==userId){
                        temp.add(resultSet.getInt(3));
                    }
                }
                resultSet=statement.executeQuery("Select * from course");
                while(resultSet.next()){
                    for(int i=0;i<temp.size();i++){
                        if(temp.get(i)==resultSet.getInt(1)){
                            enrolled.add(resultSet.getString(2));
                            e_id.add(resultSet.getInt(1));
                        }
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

            enrolledCourse.setAdapter(e);

            if(error != "") {
                Toast.makeText(CourseActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
