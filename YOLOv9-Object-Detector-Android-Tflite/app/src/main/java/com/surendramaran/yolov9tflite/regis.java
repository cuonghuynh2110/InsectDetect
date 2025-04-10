package com.surendramaran.yolov9tflite;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class regis extends AppCompatActivity {
    EditText txtusername, txtpassword, txtrepassword,txtemail;
    RelativeLayout btnSigup;
    String username, password, reenterPassword,email;
    private FirebaseAuth mAuth;
    TextView mHaveAccountTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { // Kiểm tra xem ActionBar có null không
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiện nút back
            actionBar.setTitle("Register"); // Đặt tiêu đề
            actionBar.show(); // Đảm bảo nó hiển thị
        }
        //ÁNH XẠ
        txtpassword = (EditText) findViewById(R.id.edittext_password);
        txtusername = (EditText) findViewById(R.id.edittext_username);
        txtrepassword = (EditText) findViewById(R.id.edittext_repassword);
        txtemail = (EditText) findViewById(R.id.edittext_email);
        btnSigup = (RelativeLayout) findViewById(R.id.btnSignup);
        mHaveAccountTv =(TextView) findViewById(R.id.have_accountTv);

        username  = password = reenterPassword = "";
        mAuth =FirebaseAuth.getInstance();

        /// chức năng show password
        txtpassword.setOnClickListener(new View.OnClickListener() {
            int  i =1;
            @Override
            public void onClick(View v) {

                if(i % 2!=0){
                    txtpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    txtpassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                    txtpassword.setSelection(txtpassword.getText().length());
                }else{
                    txtpassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    txtpassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                    txtpassword.setSelection(txtpassword.getText().length());
                }
                i++;
            }
        });
        txtrepassword.setOnClickListener(new View.OnClickListener() {
            int  i =1;
            @Override
            public void onClick(View v) {

                if(i % 2!=0){
                    txtrepassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    txtrepassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                    txtrepassword.setSelection(txtrepassword.getText().length());
                }else{
                    txtrepassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    txtrepassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                    txtrepassword.setSelection(txtrepassword.getText().length());
                }
                i++;
            }
        });
        // kết thúc chức năng show password

        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(regis.this,Login.class));
            }
        });
        btnSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(email,password);
            }
        });
    }
    public void save(String email,String password) {

        username = txtusername.getText().toString().trim();
        password = txtpassword.getText().toString().trim();
        email    = txtemail.getText().toString().trim();
        reenterPassword = txtrepassword.getText().toString().trim();
        if(!password.equals(reenterPassword)){
            Toast.makeText(this, "Reenterpassword không đúng!", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() <6  && reenterPassword.length()<6 ){
            Toast.makeText(regis.this,"Mật khẩu" + " "  + password + " " + "ngắn hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            txtpassword.setError("Mật khẩu ngắn hơn 6 ký tự");
            txtpassword.setFocusable(true);
        }
        else if (!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser == null) {
                                    Toast.makeText(regis.this, "User not created. Try again!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String email = currentUser.getEmail();
                                String uid = currentUser.getUid();

                                // Lưu role
//                                String role = hocvi.equals("admin") ? "admin" : "user";

                                // HashMap đúng kiểu dữ liệu
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("onlineStatus", "Online");
                                hashMap.put("name", username);
                                hashMap.put("phone", "");
                                hashMap.put("typingTo", "noOne");
                                hashMap.put("image", "");
                                hashMap.put("cover", "");
//                                hashMap.put("hocvi", hocvi);
//                                hashMap.put("role", role);
                                hashMap.put("experience", "");
                                hashMap.put("working", "");

                                // Firebase database Instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");

                                // Lưu vào Firebase Database và kiểm tra thành công
                                reference.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(regis.this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(regis.this, Login.class).putExtra("username", username));
                                            finish();
                                        } else {
                                            Toast.makeText(regis.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                Log.d("register", currentUser.toString());

                            } else {
                                Toast.makeText(regis.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(regis.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        else
        {
            Toast.makeText(regis.this, "not empty!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(regis.this,Login.class);
                startActivity(intent);
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}