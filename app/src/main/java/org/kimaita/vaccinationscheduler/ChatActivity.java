package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.adapters.ChatMessageAdapter;
import org.kimaita.vaccinationscheduler.models.Hospital;
import org.kimaita.vaccinationscheduler.models.Message;
import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    TextInputEditText inputEditText;
    TextInputLayout inputLayout;
    MaterialToolbar materialToolbar;
    DBViewModel viewModel;
    ChatMessageAdapter chatAdapter;
    File file;
    User user = new User();
    private static Hospital mHospital = new Hospital();
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static Location mLastLocation;
    private static Location mCurrentLocation;

    private static FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static Boolean mRequestingLocationUpdates;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static LocationCallback mLocationCallback;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        file = new File(this.getFilesDir(), usrDetails);
        user = readUserFile(file);
        int hosID = getIntent().getIntExtra("hospital", 0);
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        materialToolbar = findViewById(R.id.topAppBar);
        inputEditText = findViewById(R.id.textinput_message);
        inputLayout = findViewById(R.id.textinput_layout);
        mRecyclerView = findViewById(R.id.recycler_chat_messages);
        chatAdapter = new ChatMessageAdapter(new ChatMessageAdapter.MessageDiff());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(chatAdapter);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        viewModel.getmHospital(hosID).observe(this, hospital -> {
            mHospital = hospital;
            materialToolbar.setSubtitle(mHospital.getEmail_address());
            viewModel.getChatMessages(user.getDbID(), mHospital.getHospital_id()).observe(this, messages -> chatAdapter.submitList(messages));
        });

        inputLayout.setEndIconOnClickListener(v -> {
            if (!inputEditText.getText().toString().isEmpty()) {
                ArrayList<Message> messages = new ArrayList<>();
                Message message = new Message();
                message.setContent(inputEditText.getText().toString());
                message.setParent(user.getDbID());
                message.setHospital(mHospital.getHospital_id());
                message.setTime(System.currentTimeMillis());
                message.setRead(false);
                message.setSender("P");
                messages.add(message);
                viewModel.sendMessage(message);
                viewModel.getChatMessages(user.getDbID(), mHospital.getHospital_id()).observe(this, chatMessages -> {
                    chatMessages.add(message);
                });
                inputEditText.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
            } else {
                inputLayout.setBoxStrokeColor(Color.RED);
                inputEditText.requestFocus();
            }
        });

        materialToolbar.setTitle(getIntent().getStringExtra("name"));

        materialToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.hospLocation:
                    BottomSheetDialog bottomSheet = new BottomSheetDialog();
                    bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
                    if (checkPermissions()) {
                        getLastLocation();
                    } else if (!checkPermissions()) {
                        requestPermissions();
                    }
                    return true;
                case R.id.deleteChat:
                    viewModel.deleteHospitalChat(user.getDbID(), mHospital.getHospital_id());
                    return true;
            }
            return false;
        });
        mSettingsClient = LocationServices.getSettingsClient(this);
        mRequestingLocationUpdates = false;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                        } else if (task.getResult() == null) {
                            startLocationUpdates();
                            mRequestingLocationUpdates = true;
                        } else {
                            Log.e(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    public void showSnackbar(final String text) {
        View container = findViewById(R.id.chat_activity_layout);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    view -> {
                        // Request permission
                        startLocationPermissionRequest();
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.
                showSnackbar(getString(R.string.permission_denied_explanation));

            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        fusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ChatActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }
                });
    }

    public static class BottomSheetDialog extends BottomSheetDialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable
                ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.bottom_sheet_chat,
                    container, false);

            MaterialButton navDirections = v.findViewById(R.id.bottom_sheet_directions);
            MaterialTextView textHospitalName = v.findViewById(R.id.bottom_sheet_hospitalName), textDistance = v.findViewById(R.id.bottom_sheet_hospitalDist);
            float distance;
            Location hospitalLocation = new Location("");
            hospitalLocation.setLatitude(mHospital.getLatitude());
            hospitalLocation.setLongitude(mHospital.getLongitude());
            if (hospitalLocation == null) {
                textDistance.setText("This hospital is not sharing its location");
            } else if (mLastLocation != null) {
                distance = mLastLocation.distanceTo(hospitalLocation);
                textDistance.setText(distance / 1000 + " KM");
            } else if (mCurrentLocation != null) {
                distance = mCurrentLocation.distanceTo(hospitalLocation);
                textDistance.setText(distance / 1000 + " KM");
            } else {
                textDistance.setText("Failed to compute distance");
                navDirections.setEnabled(false);
            }

            textHospitalName.setText(mHospital.getHospital_name());

            navDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Or map point based on latitude/longitude
                    Uri location = Uri.parse("geo:" + mHospital.getLatitude() + "," + mHospital.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                    // Try to invoke the intent.
                    try {
                        startActivity(mapIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), "No apps found", Toast.LENGTH_SHORT).show();
                    }

                    dismiss();
                    // Remove location updates to save battery.
                    if (!mRequestingLocationUpdates) {
                        Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
                        return;
                    }

                    fusedLocationClient.removeLocationUpdates(mLocationCallback)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mRequestingLocationUpdates = false;
                                }
                            });
                }
            });

            return v;
        }
    }

}