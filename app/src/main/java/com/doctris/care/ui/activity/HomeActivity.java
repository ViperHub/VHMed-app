package com.doctris.care.ui.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.doctris.care.R;
import com.doctris.care.ui.fragment.AccountFragment;
import com.doctris.care.ui.fragment.HomeFragment;
import com.doctris.care.ui.fragment.BlogFragment;
import com.doctris.care.ui.fragment.UpComingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private final HomeFragment homeFragment = new HomeFragment();
    private final AccountFragment accountFragment = new AccountFragment();
    private final BlogFragment blogFragment = new BlogFragment();
    private final UpComingFragment upComingFragment = new UpComingFragment();
    private FloatingActionButton fabCall;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindingView();
        bindingAction();
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                    break;
                case R.id.upcoming:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, upComingFragment).commit();
                    break;
                case R.id.search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, blogFragment).commit();
                    break;
                case R.id.account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
                    break;
                default:
                    break;
            }
            return true;
        });
        openFabNewIcon();
        notificationRequest();
        firebaseToken();
    }

    private void notificationRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                EasyPermissions.requestPermissions(this, "Ch??ng t??i c???n quy???n th??ng b??o ????? g???i ?????n b???n nh???ng tin t???c m???i", 1, android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void firebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            String token = task.getResult();
            String msg = getString(R.string.msg_token_fmt, token);
            Log.d(TAG, msg);
        });
    }

    private void bindingView() {
        toolbar = findViewById(R.id.action_bar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        fabCall = findViewById(R.id.call_support);
    }

    private void openFabNewIcon() {
        fab.setOnClickListener(this:: fabOnClick);
    }

    private void fabOnClick(View view) {
        Intent intent = new Intent(this, CategoryBookingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up,  R.anim.no_animation);
    }

    private void bindingAction(){
        fabCall.setOnClickListener(this::fabCallOnClick);
    }

    private void fabCallOnClick(View view) {
        String permission = android.Manifest.permission.CALL_PHONE;
        if (EasyPermissions.hasPermissions(this, permission)) {
            makeCall();
        } else {
            EasyPermissions.requestPermissions(this, "Ch??ng t??i c???n quy???n g???i ??i???n tho???i ????? li??n h??? v???i t?? v???n vi??n", 1, permission);
        }
    }

    private void makeCall(){
        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                .setTitleText("B???n c?? mu???n g???i t?? v???n vi??n?")
                .setContentText("Ch??ng t??i s??? g???i cho b???n ngay khi b???n nh???n ?????ng ??")
                .setConfirmClickListener("?????ng ??", sDialog -> {
                    sDialog.dismissWithAnimation();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:0888195313")); // change the number
                    intent.setPackage("com.android.server.telecom");
                    startActivity(intent);
                })
                .setCancelClickListener("H???y", KAlertDialog::dismissWithAnimation)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 100 && perms.size() == 2) {
            makeCall();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            EasyPermissions.requestPermissions(this, "H??y c???p quy???n g???i ??i???n", 100, perms.toArray(new String[0]));
        }
    }
}
