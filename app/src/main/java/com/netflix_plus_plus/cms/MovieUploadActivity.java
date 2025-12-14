package com.netflix_plus_plus.cms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.netflix_plus_plus.cms.api.RetrofitClient;
import com.netflix_plus_plus.cms.models.ApiResponse;
import com.netflix_plus_plus.cms.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieUploadActivity extends AppCompatActivity {

    // UI Elements
    private Button btnSelectVideo;
    private Button btnUpload;
    private EditText etTitle, etDescription, etDirector, etReleaseYear;
    private EditText etDuration, etRating;
    private Spinner spinnerClassification;  // Changed from EditText to Spinner
    private TextView tvSelectedFile, tvUploadStatus;
    private ProgressBar progressBar;

    // Selected file
    private Uri selectedVideoUri;
    private File selectedVideoFile;

    // Activity Result Launchers
    private ActivityResultLauncher<String> filePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_upload);

        // Initialize UI elements
        initializeViews();

        // Setup classification spinner
        setupClassificationSpinner();

        // Initialize activity result launchers
        initializeLaunchers();

        // Set click listeners
        setClickListeners();
    }

    private void initializeViews() {
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnUpload = findViewById(R.id.btnUpload);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDirector = findViewById(R.id.etDirector);
        etReleaseYear = findViewById(R.id.etReleaseYear);
        etDuration = findViewById(R.id.etDuration);
        etRating = findViewById(R.id.etRating);
        spinnerClassification = findViewById(R.id.spinnerClassification);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClassificationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.classification_options,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerClassification.setAdapter(adapter);
    }

    private void initializeLaunchers() {
        // File picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleSelectedFile(uri);
                    }
                });

        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openFilePicker();
                    } else {
                        Toast.makeText(this, "Permission denied. Cannot select video.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setClickListeners() {
        btnSelectVideo.setOnClickListener(v -> checkPermissionAndPickFile());
        btnUpload.setOnClickListener(v -> uploadMovie());
    }

    private void checkPermissionAndPickFile() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_VIDEO
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch("video/*");
    }

    private void handleSelectedFile(Uri uri) {
        selectedVideoUri = uri;

        // Check if it's a video file
        if (!FileUtils.isVideoFile(this, uri)) {
            Toast.makeText(this, "Please select a video file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display file name
        String fileName = FileUtils.getFileName(this, uri);
        tvSelectedFile.setText("Selected: " + fileName);
        tvSelectedFile.setTextColor(getResources().getColor(android.R.color.white));

        // Convert URI to File (for upload)
        selectedVideoFile = FileUtils.getFileFromUri(this, uri);
    }

    private void uploadMovie() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Show progress
        showProgress(true);
        tvUploadStatus.setText("Uploading movie...");

        // Get form data
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String director = etDirector.getText().toString().trim();
        int releaseYear = Integer.parseInt(etReleaseYear.getText().toString().trim());
        int duration = Integer.parseInt(etDuration.getText().toString().trim());
        double rating = etRating.getText().toString().isEmpty() ? 5.0 :
                Double.parseDouble(etRating.getText().toString().trim());

        String classification = spinnerClassification.getSelectedItem().toString();
        if (classification.contains("(")) {
            classification = classification.substring(0, classification.indexOf("(")).trim();
        }

        // Create multipart request body
        RequestBody fileRequestBody = RequestBody.create(
                MediaType.parse("video/*"),
                selectedVideoFile
        );

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                selectedVideoFile.getName(),
                fileRequestBody
        );

        // Create form data parts
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody directorPart = RequestBody.create(MediaType.parse("text/plain"), director);
        RequestBody releaseYearPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(releaseYear));
        RequestBody durationPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(duration));
        RequestBody ratingPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(rating));
        RequestBody classificationPart = RequestBody.create(MediaType.parse("text/plain"), classification);
        RequestBody languageIdPart = RequestBody.create(MediaType.parse("text/plain"), "1"); // Default: English

        // Make API call
        Call<ApiResponse> call = RetrofitClient.getApiService().uploadMovie(
                filePart,
                titlePart,
                descriptionPart,
                directorPart,
                releaseYearPart,
                durationPart,
                ratingPart,
                classificationPart,
                languageIdPart
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(MovieUploadActivity.this,
                            "Movie uploaded successfully! 360p version will be created automatically.",
                            Toast.LENGTH_LONG).show();

                    // Clear form and go back
                    finish();
                } else {
                    Toast.makeText(MovieUploadActivity.this,
                            "Upload failed: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MovieUploadActivity.this,
                        "Upload error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs() {
        // Check if file is selected
        if (selectedVideoFile == null) {
            Toast.makeText(this, "Please select a video file", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check required text fields
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return false;
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return false;
        }

        if (etDirector.getText().toString().trim().isEmpty()) {
            etDirector.setError("Director is required");
            etDirector.requestFocus();
            return false;
        }

        if (etReleaseYear.getText().toString().trim().isEmpty()) {
            etReleaseYear.setError("Release year is required");
            etReleaseYear.requestFocus();
            return false;
        }

        if (etDuration.getText().toString().trim().isEmpty()) {
            etDuration.setError("Duration is required");
            etDuration.requestFocus();
            return false;
        }

        // Check if classification is selected (position 0 is "Select Classification")
        if (spinnerClassification.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a classification", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvUploadStatus.setVisibility(show ? View.VISIBLE : View.GONE);
        btnUpload.setEnabled(!show);
        btnSelectVideo.setEnabled(!show);
    }
}