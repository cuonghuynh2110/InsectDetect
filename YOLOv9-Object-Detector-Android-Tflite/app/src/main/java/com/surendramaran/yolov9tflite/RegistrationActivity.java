package com.surendramaran.yolov9tflite;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password, name;
    private Button mRegister;
    private TextView existAccount;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Kiểm tra và thiết lập ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Create Account");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Ánh xạ UI
        email = findViewById(R.id.register_email);
        name = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        mRegister = findViewById(R.id.register_button);
        existAccount = findViewById(R.id.homepage);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        // Bắt sự kiện click vào nút đăng ký
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String userEmail = email.getText().toString().trim();
                String userName = name.getText().toString().trim();
                String userPass = password.getText().toString().trim();
//                addUserToDatabase();
//                 Kiểm tra dữ liệu đầu vào
                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                } else if (userPass.length() < 6) {
                    password.setError("Password must be at least 6 characters");
                    password.requestFocus();
                } else {
                    registerUser(userEmail, userPass, userName);
                }
            }
        });

        // Bắt sự kiện click vào "Have an account?"
        existAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }



    public void addUserToDatabase() {
        // Lấy reference đến node "Users"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");


        // Tạo userID ngẫu nhiên
        String userID = reference.push().getKey();

        // Dữ liệu của user
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", "Huynh Cuong");
        userData.put("password", "12345");

        // Lưu dữ liệu vào Firebase
        reference.child(userID).setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Hiển thị thông báo khi lưu thành công
                        Toast.makeText(getApplicationContext(), "Thêm dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hiển thị thông báo khi có lỗi xảy ra
                        Toast.makeText(getApplicationContext(), "Lỗi khi thêm dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser(String userEmail, final String userPass, final String userName) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserDataToDatabase(user, userName);
                            }
                        } else {
                            showToast("Registration failed. Try again.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showToast("Error: " + e.getMessage());
                    }
                });
    }

    private void saveUserDataToDatabase(FirebaseUser user, String userName) {
        String email = user.getEmail();
        String uid = user.getUid();

        // Tạo HashMap để lưu dữ liệu
        HashMap<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("uid", uid);
        userData.put("name", userName);
        userData.put("onlineStatus", "online");
        userData.put("typingTo", "noOne");
        userData.put("image", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast1("Dữ liệu đã được lưu thành công!");
                        finish();
                    } else {
                        showToast1("Lỗi khi lưu dữ liệu! Hãy thử lại.");
                    }
                })
                .addOnFailureListener(e -> showToast("Lỗi: " + e.getMessage()));
    }

    private void showToast1(String message) {
        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
    }


    private void showToast(String message) {
        if (!isFinishing()) {
            Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
