package com.example.anomalbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText edPassword, edEmail;
    private FirebaseAuth mAuth;

    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseName;
    private String DATA_KEY = "USERS";
    private int edEmailToHash;

    private void init() {
//        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY);
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();


        //DELETE!!!!!!!!!!!!!!!!!!!!!!!!
        Button buttonAdmin = (Button) findViewById(R.id.buttonAdmin);
        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                edName.setText("Anomal");
                edEmail.setText("clashroyaleakks3@gmail.com");
                edPassword.setText("Qwerty@123");
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {
            if(cUser.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
//                Toast.makeText(this, "User not null", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "User null", Toast.LENGTH_SHORT).show();
            }
        }
        else Toast.makeText(getApplicationContext(), "Check your email!", Toast.LENGTH_SHORT).show();
    }





    public void onClickSignUp(View view)
    {
        if (!TextUtils.isEmpty(edEmail.getText().toString()) && !TextUtils.isEmpty(edPassword.getText().toString()))
        {

//            FirebaseUser user = mAuth.getCurrentUser();
//            if (user != null) user.delete();

            mAuth.createUserWithEmailAndPassword(edEmail.getText().toString(), edPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        edEmailToHash = edEmail.getText().toString().hashCode();
                        mDataBaseName = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + edEmailToHash + "/" + "name");
                        mDataBaseName.setValue("Пользователь");
                        mDataBase.child(edEmailToHash + "/" + "email").setValue(edEmail.getText().toString());
                        mDataBase.child(String.valueOf(edEmailToHash)).child("logoPhoto").setValue("https://firebasestorage.googleapis.com/v0/b/anomalbrowseronline.appspot.com/o/browserlogo.png?alt=media&token=c919c0ab-b489-4bd0-bafc-b072d1583885");
                        Toast.makeText(getApplicationContext(), "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                        sendEmailVer();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Пароль должен иметь хотя бы одну цифру и одну заглавную букву!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Пожалуйста, введите пароль и почту.", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickSignIn(View view)
    {
        if (!TextUtils.isEmpty(edEmail.getText().toString()) && !TextUtils.isEmpty(edPassword.getText().toString())) {
            mAuth.signInWithEmailAndPassword(edEmail.getText().toString(), edPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Успешный вход!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else Toast.makeText(getApplicationContext(), "На вашу почту пришло письмо, пожалуйста преейдите по ссылке в нем, чтобы закончить регистрацию!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Не удалось войти!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendEmailVer()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        assert user!=null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Проверьте вашу почту!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка. Проверьте правильность введенной почты!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
