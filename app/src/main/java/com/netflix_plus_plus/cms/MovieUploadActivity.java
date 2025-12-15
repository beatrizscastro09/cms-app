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
import java.io.FileInputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieUploadActivity extends AppCompatActivity {

    // UI Elements
    private Button btnSelectVideo, btnSelectCoverImage, btnSelectSliderImage;
    private Button btnUpload;
    private EditText etTitle, etDescription, etDirector, etReleaseYear;
    private EditText etDuration, etRating;
    private Spinner spinnerClassification;
    private TextView tvSelectedFile, tvSelectedCoverImage, tvSelectedSliderImage;
    private TextView tvUploadStatus;
    private ProgressBar progressBar;

    // Selected files
    private Uri selectedVideoUri;
    private File selectedVideoFile;
    private Uri selectedCoverImageUri;
    private File selectedCoverImageFile;
    private Uri selectedSliderImageUri;
    private File selectedSliderImageFile;

    // Track which button was clicked
    private enum FileType { VIDEO, COVER_IMAGE, SLIDER_IMAGE }
    private FileType currentFileType;

    // Activity Result Launchers
    private ActivityResultLauncher<String> filePickerLauncher;
    private ActivityResultLauncher<String> imagePickerLauncher;
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
        btnSelectCoverImage = findViewById(R.id.btnSelectCoverImage);
        btnSelectSliderImage = findViewById(R.id.btnSelectSliderImage);
        btnUpload = findViewById(R.id.btnUpload);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDirector = findViewById(R.id.etDirector);
        etReleaseYear = findViewById(R.id.etReleaseYear);
        etDuration = findViewById(R.id.etDuration);
        etRating = findViewById(R.id.etRating);

        spinnerClassification = findViewById(R.id.spinnerClassification);

        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        tvSelectedCoverImage = findViewById(R.id.tvSelectedCoverImage);
        tvSelectedSliderImage = findViewById(R.id.tvSelectedSliderImage);
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
        // Video file picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleSelectedFile(uri);
                    }
                });

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleSelectedImage(uri);
                    }
                });

        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (currentFileType == FileType.VIDEO) {
                            openFilePicker();
                        } else {
                            openImagePicker();
                        }
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setClickListeners() {
        btnSelectVideo.setOnClickListener(v -> {
            currentFileType = FileType.VIDEO;
            checkPermissionAndPickFile();
        });

        btnSelectCoverImage.setOnClickListener(v -> {
            currentFileType = FileType.COVER_IMAGE;
            checkPermissionAndPickFile();
        });

        btnSelectSliderImage.setOnClickListener(v -> {
            currentFileType = FileType.SLIDER_IMAGE;
            checkPermissionAndPickFile();
        });

        btnUpload.setOnClickListener(v -> uploadMovie());
    }

    private void checkPermissionAndPickFile() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? (currentFileType == FileType.VIDEO ?
                Manifest.permission.READ_MEDIA_VIDEO :
                Manifest.permission.READ_MEDIA_IMAGES)
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            if (currentFileType == FileType.VIDEO) {
                openFilePicker();
            } else {
                openImagePicker();
            }
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch("video/*");
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void handleSelectedFile(Uri uri) {
        selectedVideoUri = uri;

        if (!FileUtils.isVideoFile(this, uri)) {
            Toast.makeText(this, "Please select a video file", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = FileUtils.getFileName(this, uri);
        tvSelectedFile.setText("Selected: " + fileName);
        tvSelectedFile.setTextColor(getResources().getColor(android.R.color.white));

        selectedVideoFile = FileUtils.getFileFromUri(this, uri);
    }

    private void handleSelectedImage(Uri uri) {
        String fileName = FileUtils.getFileName(this, uri);

        if (currentFileType == FileType.COVER_IMAGE) {
            selectedCoverImageUri = uri;
            selectedCoverImageFile = FileUtils.getFileFromUri(this, uri);
            tvSelectedCoverImage.setText("✓ " + fileName);
            tvSelectedCoverImage.setTextColor(getResources().getColor(android.R.color.white));
        } else if (currentFileType == FileType.SLIDER_IMAGE) {
            selectedSliderImageUri = uri;
            selectedSliderImageFile = FileUtils.getFileFromUri(this, uri);
            tvSelectedSliderImage.setText("✓ " + fileName);
            tvSelectedSliderImage.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void uploadMovie() {
        if (!validateInputs()) {
            return;
        }

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

        try {
            // Create video file part
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
            RequestBody languageIdPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    "eec9c9b5-d78a-11f0-ae4d-b6b8a7b9a959"  // English
            );

            // Create image parts (optional)
            MultipartBody.Part coverImagePart = null;
            if (selectedCoverImageFile != null) {
                RequestBody coverRequestBody = RequestBody.create(
                        MediaType.parse("image/*"),
                        selectedCoverImageFile
                );
                coverImagePart = MultipartBody.Part.createFormData(
                        "coverImage",
                        selectedCoverImageFile.getName(),
                        coverRequestBody
                );
            }

            MultipartBody.Part sliderImagePart = null;
            if (selectedSliderImageFile != null) {
                RequestBody sliderRequestBody = RequestBody.create(
                        MediaType.parse("image/*"),
                        selectedSliderImageFile
                );
                sliderImagePart = MultipartBody.Part.createFormData(
                        "sliderImage",
                        selectedSliderImageFile.getName(),
                        sliderRequestBody
                );
            }

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
                    languageIdPart,
                    coverImagePart,
                    sliderImagePart
            );

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    showProgress(false);

                    if (response.isSuccessful()) {
                        Toast.makeText(MovieUploadActivity.this,
                                "Movie uploaded successfully! 360p version will be created automatically.",
                                Toast.LENGTH_LONG).show();
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

        } catch (Exception e) {
            showProgress(false);
            Toast.makeText(this, "Error preparing upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs() {
        if (selectedVideoFile == null) {
            Toast.makeText(this, "Please select a video file", Toast.LENGTH_SHORT).show();
            return false;
        }

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
        btnSelectCoverImage.setEnabled(!show);
        btnSelectSliderImage.setEnabled(!show);
    }
}