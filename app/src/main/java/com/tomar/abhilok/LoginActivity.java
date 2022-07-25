package com.tomar.abhilok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tomar.abhilok.Model.Users;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity
{
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView adminLink, NotAdminLink;
    private String parentDbName ="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        adminLink =(TextView) findViewById( R.id.admin_panel_link);
        NotAdminLink =(TextView) findViewById( R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog( this);

        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    LoginUser();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
              LoginButton.setText("Login Admin");
              adminLink.setVisibility(View.INVISIBLE);
              NotAdminLink.setVisibility(View.VISIBLE);
              parentDbName ="Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName ="Users";

            }
        });

    }

    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

         if (TextUtils.isEmpty(phone))
         {
        Toast.makeText(this, "Please Enter your phone number...", Toast.LENGTH_SHORT).show();
         }
         else if (TextUtils.isEmpty(password))
         {
        Toast.makeText(this, "Please Enter your password...", Toast.LENGTH_SHORT).show();
         }
         else
         {
             loadingBar.setTitle("Fetching Account");
             loadingBar.setMessage("Please wait, while we are checking in Abhilok server ");
             loadingBar.setCanceledOnTouchOutside(false);
             loadingBar.show();


             AllowAccessToAccount(phone, password);
         }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists())
                {
                    Users UsersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (UsersData.getPhone().equals(phone))
                    {
                        if (UsersData.getPassword().equals(password))
                        {
                           if (parentDbName.equals("Admins"))
                           {
                               Toast.makeText(LoginActivity.this, "Welcome Alok", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this, AdminProductActivity.class);
                               startActivity(intent);
                           }
                           else if (parentDbName.equals("Users"))
                           {
                               Toast.makeText(LoginActivity.this, "logged in successfully...", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                               startActivity(intent);
                           }
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Password is incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this " +phone+ "number do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
