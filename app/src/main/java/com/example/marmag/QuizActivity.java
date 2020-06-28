package com.example.marmag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    ArrayList<String> question,option1,option2,option3,option4;
    ArrayList<Integer> answer;
    String quiz;
    int userId,courseId;
    TextView ques;
    Button submit;
    RadioGroup o;
    RadioButton o1,o2,o3,o4;
    int counter=0;
    int total=0,score=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        final Intent intent=getIntent();
        final int userid=intent.getIntExtra("userId",0);
        int courseid=intent.getIntExtra("courseId",0);
        String Quiz=intent.getStringExtra("quiz");
        quiz=Quiz;
        userId=userid;
        courseId=courseid;
        getSupportActionBar().setTitle("Quiz");
        question=new ArrayList<String>();
        option1=new ArrayList<String>();
        option2=new ArrayList<String>();
        option3=new ArrayList<String>();
        option4=new ArrayList<String>();
        answer=new ArrayList<Integer>();
        ques=(TextView)findViewById(R.id.question);
        ques.setMovementMethod(new ScrollingMovementMethod());
        o=(RadioGroup)findViewById(R.id.option);
        o1=(RadioButton)findViewById(R.id.option1);
        o2=(RadioButton)findViewById(R.id.option2);
        o3=(RadioButton)findViewById(R.id.option3);
        o4=(RadioButton)findViewById(R.id.option4);
        submit=(Button)findViewById(R.id.submit);

        new Task().execute();

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (o1.isChecked() || o2.isChecked() || o3.isChecked() || o4.isChecked()) {
                        RadioButton select = findViewById(o.getCheckedRadioButtonId());
                        int ans = o.indexOfChild(select)+1;
                        if (counter < answer.size()) {
                            if (ans == answer.get(counter)) {
                                score++;
                            }
                            counter++;
                            setQuestion();
                        } else {
                            new Task1().execute();
                            Intent intent1=new Intent(QuizActivity.this,HomeActivity.class);
                            intent1.putExtra("userId",userId);
                            startActivity(intent1);
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(QuizActivity.this, "Please Select Option", Toast.LENGTH_LONG).show();
                    }
                }
            });

    }

    public void setQuestion(){
        if(counter<question.size()){
            ques.setText(question.get(counter));
            o1.setText(option1.get(counter));
            o2.setText(option2.get(counter));
            o3.setText(option3.get(counter));
            o4.setText(option4.get(counter));

        }
    }

    class Task extends AsyncTask<Void, Void, Void> {

        String  error="";

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from "+quiz);
                while(resultSet.next()){
                   question.add(resultSet.getString(2));
                   option1.add(resultSet.getString(3));
                   option2.add(resultSet.getString(4));
                   option3.add(resultSet.getString(5));
                   option4.add(resultSet.getString(6));
                   answer.add(resultSet.getInt(7));
                }
            }
            catch (Exception e){
                error=e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            setQuestion();
            if(error != "") {
                Toast.makeText(QuizActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
    class Task1 extends AsyncTask<Void, Void, Void> {

        String  error="";

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.43.214/marmag", "root", "");

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from enrollment");
                error=String.valueOf(userId);
                while(resultSet.next()){
                    if(userId==resultSet.getInt(2) && courseId==resultSet.getInt(3)){
                        int n=resultSet.getInt(5);
                        int m=resultSet.getInt(7);
                        score=score/question.size();
                        score=score*100;
                        m=m*n+score;
                        n++;
                        m=m/n;
                        String sql="UPDATE enrollment "+"SET marks=?, "+"completion=? "+"WHERE user_id=?"+" and "+"course_id=?";
                        PreparedStatement pstmt =connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                        pstmt.setInt(1,m);
                        pstmt.setInt(2,n);
                        pstmt.setInt(3,userId);
                        pstmt.setInt(4,courseId);
                        pstmt.executeUpdate();
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
            setQuestion();
            if(error != "") {
                Toast.makeText(QuizActivity.this,error, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
