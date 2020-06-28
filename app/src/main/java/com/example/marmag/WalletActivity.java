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

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class WalletActivity extends AppCompatActivity {

    TextView balance;
    Button share;
    int userId,courseId,temp=0;
    private DrawerLayout dl;
    private ActionBarDrawerToggle to;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Intent intent=getIntent();
        int userid=intent.getIntExtra("userId",0);
        userId=userid;

        SharedPreferences sp=getSharedPreferences("shp",MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("userId",userId);
        balance=(TextView)findViewById(R.id.balance);
        share=(Button)findViewById(R.id.share);
        getSupportActionBar().setTitle("Wallet");
        new Task().execute();
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WalletActivity.this,"Share your Eamail",Toast.LENGTH_LONG).show();
            }
        });
        dl = (DrawerLayout)findViewById(R.id.activity_wallet);
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
                        Intent intent1 = new Intent(WalletActivity.this, HomeActivity.class);
                        intent1.putExtra("userId", userId);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.classroom:
                        Intent intent2 = new Intent(WalletActivity.this, CourseActivity.class);
                        intent2.putExtra("userId", userId);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.profile:
                        Intent intent3 = new Intent(WalletActivity.this, ProfileActivity.class);
                        intent3.putExtra("userId", userId);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.wallet:
                        Intent intent4 = new Intent(WalletActivity.this, WalletActivity.class);
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

        String Balance="Balance" ,error="";

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
                while(resultSet.next()){
                    if(resultSet.getInt(1)==userId){
                        Balance+="\n"+resultSet.getInt(5);
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
            balance.setText(Balance);
            if(error != "") {
                Toast.makeText(WalletActivity.this, error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);

        }
    }
}
