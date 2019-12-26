package com.shresthagaurav.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shresthagaurav.taskmanager.api.API;
import com.shresthagaurav.taskmanager.api.ApiClass;
import com.shresthagaurav.taskmanager.model.UserToken;
import com.shresthagaurav.taskmanager.model.User_model;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
EditText l_uname,l_password;
Button btnlogin;
String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l_password=findViewById(R.id.edl_password);
        l_uname=findViewById(R.id.edl_username);
        btnlogin=findViewById(R.id.btnlogin);
   btnlogin.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
          user=l_uname.getText().toString();
           String pass=l_password.getText().toString();
           User_model user_model =new User_model(user,pass);
           ApiClass apiClass = new ApiClass();
           Call<UserToken>userTokenCall=apiClass.calls().login(user_model);
           userTokenCall.enqueue(new Callback<UserToken>() {
               @Override
               public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                   if(!response.isSuccessful()){
                       Toast.makeText(MainActivity.this, "User not found" , Toast.LENGTH_SHORT).show();
                       Log.d("error", "error" + response.code());
                       return;
                   }
                   Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                   UserToken userToken=response.body();
                   Intent intent = new Intent(MainActivity.this,DashBoard.class);
                   intent.putExtra("token",userToken.getToken());
                   intent.putExtra("username",user);
                   startActivity(intent);


               }

               @Override
               public void onFailure(Call<UserToken> call, Throwable t) {
                   Toast.makeText(MainActivity.this, "error "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                   Log.d("error", "error" + t.getLocalizedMessage());

               }
           });
       }
   });
    }

}
