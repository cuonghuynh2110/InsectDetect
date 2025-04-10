package com.surendramaran.yolov9tflite;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.surendramaran.yolov9tflite.Models.Users;

import java.util.HashMap;

public class Loginn extends AppCompatActivity {
    private static final String FILE_NAME = "myFile";
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GoogleActivity";

    SignInButton mGoogleLoginBtn;
    private FirebaseAuth mAuth;
    EditText txtusername_login, txtpassword_login;
    Button btnLogin;
    TextView txtCreateAccount, mRecoverPassTv;
    CheckBox savePassword;
    String ten ="";

    //Bo sung
    GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //bo sung
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();


        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.hide();
        }

        Intent intent = new Intent();
        String ten = intent.getStringExtra("username");
        // Mapping views
        txtpassword_login = findViewById(R.id.edittext_password_login);
        txtusername_login = findViewById(R.id.edittext_username_login);
        savePassword = findViewById(R.id.checkSavePass);
        btnLogin = findViewById(R.id.btnLogin);
        txtCreateAccount = findViewById(R.id.txtcreateAccount);
        mRecoverPassTv = findViewById(R.id.recoverPassTv);

        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn);
        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.round_bottomnavigation);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{backgroundDrawable});
        int padding = (int) getResources().getDimension(R.dimen.size_5dp);
        layerDrawable.setLayerInset(0, padding, padding, padding, padding);
        mGoogleLoginBtn.setBackground(layerDrawable);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Firebase
        try{
            mAuth = FirebaseAuth.getInstance();
            Toast.makeText(Loginn.this, "Firebase initialized", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }




        // GoogleSignInOptions
        // Click đăng ký tài khoản listeners
        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Loginn.this, dangky.class));
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        txtusername_login.setText(username);
        txtpassword_login.setText(password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePassword.isChecked()) {
                    String username = txtusername_login.getText().toString();
                    String password = txtpassword_login.getText().toString();
                    storeDataUsingSharedPreferences(username, password);
                }

                if (isNetworkConnected()) {
                    String username = txtusername_login.getText().toString();
                    String password = txtpassword_login.getText().toString();
                    login(username, password);
                } else {
                    Toast.makeText(Loginn.this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code cux
                //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                //startActivityForResult(signInIntent,RC_SIGN_IN);

                //bo sung

                //signIn();
            }
        });
    }
    //bo sung
    void  signIn(){

    }
    public void login(String username, String password) {
        ProgressDialog loadingBar = new ProgressDialog(this); // Tạo popup loading
        loadingBar.setMessage("Đang đăng nhập...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show(); // Hiển thị popup

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                    String email = user.getEmail();
                    String uid = user.getUid();

                    // Lưu thông tin user vào Firebase
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", ten);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Users");
                    reference.child(uid).setValue(hashMap);
                }

                // Truy vấn vai trò của người dùng từ Firebase
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                userRef.orderByChild("email").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingBar.dismiss(); // Ẩn popup khi có kết quả

                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Users userData = dataSnapshot.getValue(Users.class);
                                if (userData != null && userData.getRole() != null) {
                                    if (userData.getRole().equals("admin")) {
                                        Toast.makeText(Loginn.this, "admin", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        Intent intent = new Intent(Loginn.this, Home.class);
                                        startActivity(intent);
                                        return;
                                    }
                                }
                                finish();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingBar.dismiss(); // Ẩn popup nếu truy vấn Firebase bị lỗi
                    }
                });

            } else {
                loadingBar.dismiss(); // Ẩn popup khi đăng nhập thất bại
                Toast.makeText(Loginn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            loadingBar.dismiss(); // Ẩn popup nếu có lỗi
            Toast.makeText(Loginn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        });
    }

  /* public void login(String username, String password) {
       mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
           if (task.isSuccessful()) {
               FirebaseUser user = mAuth.getCurrentUser();

               DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
               userRef.orderByChild("email").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists()) {
                           for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                               Users userData = dataSnapshot.getValue(Users.class);
                               if (userData.isLocked()) {
                                   // Nếu tài khoản đã bị khóa, hiển thị thông báo và không cho phép đăng nhập
                                   Intent lockedAccountIntent = new Intent(Login.this, LockedAccountActivity.class);
                                   startActivity(lockedAccountIntent);
                                   finish(); // Kết thúc activity hiện tại sau khi chuyển hướng
                                   // Dừng xử lý tiếp theo
                               }
                               // Kiểm tra quyền truy cập và chuyển hướng tương ứng

                           }
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Xử lý khi có lỗi xảy ra trong quá trình truy xuất cơ sở dữ liệu
                       Toast.makeText(Login.this, "Đã xảy ra lỗi khi truy xuất dữ liệu", Toast.LENGTH_SHORT).show();
                   }
               });
           } else {
               // Xử lý khi đăng nhập thất bại
               Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
           }
       }).addOnFailureListener(e -> Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show());
   }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {


        }

    }
    void navigateToMainActivity(){
        finish();

        //startActivity(new Intent(Login.this, HomeActivity.class));
    }
    private void firebaseAuthWithGoogle(String idToken) {

        Log.e("AccountFireBase", "firebaseAuthWithGoogle:" + idToken); // Thêm dòng log này để kiểm tra xem tài khoản Google đã được chọn thành công hay không
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                String email = user.getEmail();
                                String uid =user.getUid();
                                //HashMap
                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");
                                hashMap.put("typingTo","noOne");
                                hashMap.put("onlineStatus","Online");
                                hashMap.put("phone","");
                                hashMap.put("image","");
                                hashMap.put("hocvi","");
                                hashMap.put("role","");
                                hashMap.put("experience","");
                                hashMap.put("working","");
                                //Firebase database Instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //Sử dụng DatabaseReference để lưu thông tin user
                                DatabaseReference reference= database.getReference("Users");
                                reference.child(uid).setValue(hashMap);
                            }


                            finish();
                            // updateUI(user) Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();;
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Loginn.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //lấy lại PassWord....
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thiết Lập Lại Mật Khẩu");

        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Phục Hồi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Hủy Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginRecovery(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Loginn.this, "mật khẩu mới đã được gửi qua Email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Loginn.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(Loginn.this, "failure" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    //Lưu đăng nhập ko cần điền lại
    private void storeDataUsingSharedPreferences(String username, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

