package info.androidhive.introslider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCall;
    private final static int PERMISSION_CALL_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_play_again).setOnClickListener(this);
        btnCall = (Button) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_again:
                playAgain();
                break;
            case R.id.btn_call:
                call();
                break;
            default:
                break;
        }
    }

    private void playAgain() {
        // We normally won't show the welcome slider again in real app
        // but this is for testing
        PrefManager prefManager = new PrefManager(getApplicationContext());

        // make first time launch TRUE
        prefManager.setFirstTimeLaunch(true);

        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        finish();
    }

    private void call() {
        Log.i(TAG, "call: ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();

        } else {
            callPhone();
        }
    }


    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "10086");
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == PERMISSION_CALL_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhone();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void requestPermission() {
        //如果之前申请权限被拒绝，再次申请权限时说明为什么申请该权限。
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest
                .permission.CALL_PHONE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("说明")
                    .setMessage("需要使用电话权限，进行电话测试")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new
                                    String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_CALL_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .create()
                    .show();
        }else {
            //如果是第一次申请该权限，则直接弹出对话框让用户选取。
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_CALL_REQUEST_CODE);
        }
    }

}
