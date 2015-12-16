package co.cabsolutely.android.phonenumbertextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import co.cabsolutely.android.widgets.PhoneNumberTextView;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private PhoneNumberTextView mPhoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mPhoneNumberTextView = (PhoneNumberTextView) findViewById(R.id.view);
        findViewById(R.id.iso).setOnClickListener(this);
        findViewById(R.id.code).setOnClickListener(this);
        findViewById(R.id.formatted).setOnClickListener(this);
        findViewById(R.id.raw).setOnClickListener(this);
        findViewById(R.id.error).setOnClickListener(this);
        findViewById(R.id.longer).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iso:
                makeToast(mPhoneNumberTextView.getCountryISO());
                break;
            case R.id.code:
                makeToast(mPhoneNumberTextView.getCountryCode());
                break;
            case R.id.formatted:
                makeToast(mPhoneNumberTextView.getFormattedPhoneNumber());
                break;
            case R.id.raw:
                makeToast(mPhoneNumberTextView.getRawPhoneNumber());
                break;
            case R.id.error:
                mPhoneNumberTextView.setError("Error!");
                break;
            case R.id.longer:
                mPhoneNumberTextView.setCanEnterLongerNumber(!mPhoneNumberTextView.canEnterLongerNumber());
                break;
        }
    }

    private void makeToast(String text) {
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
    }
}
