package com.example.pdfmaker;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class camera extends AppCompatActivity {

    private LinearLayout thumbnailContainer;
    private TextView finishTick;
    private final ArrayList<Bitmap> capturedImages = new ArrayList<>();
    private ActivityResultLauncher<Void> cameraLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        thumbnailContainer = findViewById(R.id.thumbnailContainer);

        finishTick = findViewById(R.id.btnFinish);

        finishTick.setOnClickListener(v -> showSaveOptions());

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        capturedImages.add(bitmap);
                        addThumbnail(bitmap);

                        finishTick.setVisibility(View.VISIBLE);
                        finishTick.setText("✓ " + capturedImages.size());

                        finishTick.setScaleX(0.5f);
                        finishTick.setScaleY(0.5f);

                        finishTick.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(250)
                                .setInterpolator(new OvershootInterpolator())
                                .start();
                    }
                }
        );

        Button proceedButton = findViewById(R.id.btnproceed);
        proceedButton.setOnClickListener(v -> cameraLauncher.launch(null));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
    }
    private void addThumbnail(Bitmap bitmap) {
        if (thumbnailContainer == null || bitmap == null) return;
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(80),
                dpToPx(100)
        );
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        thumbnailContainer.addView(imageView);
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void showSaveOptions() {

        if (capturedImages.isEmpty()) {
            Toast.makeText(this,
                    "Capture at least one page first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String[] options = {
                "Create New PDF",
                "Add to Existing PDF"
        };

        new AlertDialog.Builder(this)
                .setTitle("Finish scanning")
                .setMessage("You have scanned "
                        + capturedImages.size() + " pages.")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        Toast.makeText(this,
                                "Create New PDF selected",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Add to Existing PDF selected",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }













}
