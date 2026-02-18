package com.example.project.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;
import com.example.project.db.UserDB;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class signup extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_sign_up;
    TextView login_link;
    ImageView iv_icon_google;
    private static final String TAG = "signup";
    private static final int RC_GOOGLE = 1001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        iv_icon_google = findViewById(R.id.iv_icon_google);
        login_link = findViewById(R.id.login_link);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(signup.this , "Please enter your email and password!" , Toast.LENGTH_SHORT).show();
                    et_email.requestFocus();
                    et_password.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    et_email.setError("Invalid email format");
                    et_email.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    et_password.setError("Password must be at least 6 characters");
                    et_password.requestFocus();
                    return;
                }
                createAccount(email, password);
            }
        });

        googleClient = GoogleSignIn.getClient(this, gso);
        iv_icon_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleClient.signOut().addOnCompleteListener(task -> {
                    Intent i_google = googleClient.getSignInIntent();
                    startActivityForResult(i_google, RC_GOOGLE);
                });
            }
        });

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_login = new Intent(signup.this , login.class);
                startActivity(i_login);
            }
        });
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(signup.this, "Account created", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserDB db = new UserDB(signup.this);
//                                db.saveSessionEmail(email);
                                db.saveEmail(user.getEmail());
                                db.closeDB();
                            }
                            Intent i = new Intent(signup.this, MainActivity.class);
                            i.putExtra("dest", "home");
                            startActivity(i);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signup.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(signup.this, "Google sign-in successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && user.getEmail() != null) {
                        UserDB db = new UserDB(signup.this);
                        db.saveEmail(user.getEmail());
                        db.closeDB();
                    }
                    Intent i_google = new Intent(signup.this , MainActivity.class);
                    startActivity(i_google);
                    finish();
                } else {
                    Toast.makeText(signup.this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}