package de.r3chn3n.zahlenwaage2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    PaintView paintView;
    FloatingActionButton bDeleteCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = findViewById(R.id.paint_view);
        bDeleteCircle = findViewById(R.id.btnDeleteCircle);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        bDeleteCircle.setOnClickListener(view -> paintView.deleteCircles());
    }
}