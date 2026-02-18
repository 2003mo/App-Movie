package com.example.project.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;
import com.example.project.db.UserDB;
import com.google.firebase.auth.FirebaseAuth;

public class settings extends AppCompatActivity {

    TextView user_name , tv_email , bt_logout;
    ImageView profile_image , iv_home , iv_fav , iv_search , iv_setting;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user_name = findViewById(R.id.user_name);
        tv_email = findViewById(R.id.tv_email);
        profile_image = findViewById(R.id.profileImage);
        bt_logout = findViewById(R.id.bt_logout);

        iv_home = findViewById(R.id.iv_home);
        iv_fav = findViewById(R.id.iv_fav);
        iv_search = findViewById(R.id.iv_search);
        iv_setting = findViewById(R.id.iv_settings);

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_home = new Intent(settings.this , MainActivity.class);
                i_home.putExtra("dest" , "home");
                i_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i_home);
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_search = new Intent(settings.this , MainActivity.class);
                i_search.putExtra("dest" , "search");
                i_search.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i_search);            }
        });
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_fav = new Intent(settings.this , MainActivity.class);
                i_fav.putExtra("dest" , "favorites");
                i_fav.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i_fav);
            }
        });
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_settings = new Intent(settings.this , settings.class);
                startActivity(i_settings);
            }
        });

        user_name.setText("Welcome back,");
        profile_image.setImageResource(R.drawable.hackers);

        UserDB localDB = new UserDB(this);
        String email = localDB.getEmail();
        localDB.closeDB();
        if (email != null && !email.isEmpty()) {
            tv_email.setText(email);
        } else {
            tv_email.setText("No email");
        }

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                UserDB db = new UserDB(settings.this);
                db.clearSession();
                db.closeDB();
                Intent i_logout = new Intent(settings.this, login.class);
                startActivity(i_logout);
                finish();

            }
        });

    }
}