package com.example.phiiphiroberts.uearn;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

public class Login extends AppCompatActivity {
    RelativeLayout rellay1, rellay2;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView userEmail, userPassword;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 1000);//time out for the splash screen

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabase  = FirebaseDatabase.getInstance().getReference().child("users");

    }

    public void loginButtonClicked(View v) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network

            final String user_email = userEmail.getText().toString().trim();
            final String user_pass = userPassword.getText().toString().trim();

            //Checking for Empty Fields and setting errors to fields
            if (TextUtils.isEmpty(user_email) && TextUtils.isEmpty(user_pass)) {
                userEmail.setError("This field is required");
                userPassword.setError("This field is required");
                return;
            } else if (TextUtils.isEmpty(user_email) && !TextUtils.isEmpty(user_pass)) {
                userEmail.setError("This field is required");
                return;
            } else if (!TextUtils.isEmpty(user_pass) && TextUtils.isEmpty(user_pass)) {
                userPassword.setError("This field is required");
                return;
            } else {

            }

            //setting a progress dialogue
            final ProgressDialog mDialog = new ProgressDialog(Login.this);
            mDialog.setMessage("Please Waiting...");
            mDialog.show();

            if (!TextUtils.isEmpty(user_email) && !TextUtils.isEmpty(user_pass)) {
                {
                    mAuth.createUserWithEmailAndPassword(user_email, user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user = mDatabase.child((user_id));

                                //giving feedback after user click sign in button
                                FancyToast.makeText(Login.this, "Login Successful", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                finish();
                            } else {
                                FancyToast.makeText(Login.this, "Wrong Password", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            } else{
                                mDialog.dismiss();
                                FancyToast.makeText(Login.this, "User not found in database", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                            }
                        }
                    });
                }

            }

            connected = true;
        }
        else{
            FancyToast.makeText(Login.this, "Please Check your Internet Connection", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
            connected = false;}

    }
}
