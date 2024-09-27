package com.example.fitlog;

import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("Profile Activity");
        textView.setTextSize(24);
        textView.setPadding(16, 16, 16, 16);
        
        setContentView(textView);
    }

    @Override
    protected int getLayoutResourceId() {
        // Return 0 as we're setting the content view programmatically
        return 0;
    }
}