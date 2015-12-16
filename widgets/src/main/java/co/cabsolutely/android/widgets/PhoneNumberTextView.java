package co.cabsolutely.android.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.cabsolutely.android.widgets.adapters.CountryAdapter;
import co.cabsolutely.android.widgets.models.Country;


public class PhoneNumberTextView extends LinearLayout {

    private Spinner mSpinner;
    private EditText mPhoneNumber;
    private PhoneNumberUtil mPhoneUtil;
    private CountryAdapter mAdapter;
    private PhoneNumberPatternTextWatcher mFormatter;
    private String mCountry;
    private boolean mCanEnterLongerNumber;

    public PhoneNumberTextView(Context context) {
        super(context);
        init(context);
    }

    public PhoneNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhoneNumberTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public String getFormattedPhoneNumber() {
        Country country = mAdapter.getItem(mSpinner.getSelectedItemPosition());
        return "+" + country.getPhoneCode() + "" + mPhoneNumber.getText().toString();
    }

    public String getRawPhoneNumber() {
        return getRawNumber(getFormattedPhoneNumber());
    }

    public void setError(CharSequence error) {
        mPhoneNumber.setError(error);
        mPhoneNumber.requestFocus();
    }

    public String getCountryISO() {
        return mCountry;
    }

    public void setCanEnterLongerNumber(boolean mCanEnterLongerNumber) {
        this.mCanEnterLongerNumber = mCanEnterLongerNumber;
    }

    public boolean canEnterLongerNumber() {
        return mCanEnterLongerNumber;
    }

    public String getCountryCode() {
        Country country = mAdapter.getItem(mSpinner.getSelectedItemPosition());
        return country.getPhoneCode();
    }


    private void init(Context context) {
        final View v = inflate(context, R.layout.widget_phone_number, this);
        mPhoneUtil = PhoneNumberUtil.getInstance();
        mSpinner = (Spinner) v.findViewById(R.id.spinner);
        mPhoneNumber = (EditText) v.findViewById(R.id.phoneNumber);

        setUpAdapter();
    }

    private void setUpAdapter() {
        final TelephonyManager tm = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null) {
            mCountry = tm.getNetworkCountryIso();
        } else {
            mCountry = Locale.US.toString();
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        mAdapter = new CountryAdapter(getContext(), mSpinner, getCountries());
        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(R.layout.item_country);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public int mSelectCount;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectCount == 0) {
                    mSelectCount++;
                    mAdapter.selectCountry(mCountry);
                }
                mCountry = mAdapter.getItem(mSpinner.getSelectedItemPosition()).getIsoName();
                mPhoneNumber.setText("");
                setPhoneNumberHint(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAdapter.selectCountry(mCountry);
                mPhoneNumber.setText("");
            }
        });
        mSpinner.setAdapter(mAdapter);
    }

    private void setPhoneNumberHint(int position) {
        final String country = mAdapter.getItem(position).getIsoName();

        final Phonenumber.PhoneNumber number = mPhoneUtil
                .getExampleNumberForType(country, PhoneNumberUtil.PhoneNumberType.MOBILE);
        if (number != null) {
            final long sampleNumber = number.getNationalNumber();
            final String sample = String.valueOf(sampleNumber);
            final String formattedSample = formatNationalNumber(country, sample);
            final String pattern = formattedSample.replaceAll("\\d", "X");
            setPhoneNumberFormater(position, pattern);
            mPhoneNumber.setHint(formattedSample);
        }
    }

    private void setPhoneNumberFormater(int position, String pattern) {
        final String country = mAdapter.getItem(position).getIsoName();
        mPhoneNumber.removeTextChangedListener(mFormatter);
        mFormatter = new PhoneNumberPatternTextWatcher(country, pattern);
        mPhoneNumber.addTextChangedListener(mFormatter);
    }

    private String formatNationalNumber(String countryIso, String number) {
        try {
            Phonenumber.PhoneNumber swissNumberProto = mPhoneUtil.parse(number, countryIso);
            return mPhoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return  number;
        }
    }

    private String getRawNumber(String formattedNumber) {
        return formattedNumber
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "");
    }

    private List<Country> getCountries() {
        ArrayList<Country> result = new ArrayList<>();
        String[] codes = Constants.COUNTRIES;
        String[] phoneCodes = Constants.COUNTRY_CODES;
        for(int i = 0; i < codes.length; i++) {
            String code = codes[i];
            String phoneCode = phoneCodes[i];
            result.add(new Country("", code, phoneCode));
        }
        return result;
    }

    private class PhoneNumberPatternTextWatcher implements TextWatcher {

        private final String mPattern;
        private String mCountry;

        public PhoneNumberPatternTextWatcher(String country, String pattern) {
            mCountry = country;
            mPattern = pattern;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String rawNumber = getRawNumber(s.toString());
            if(rawNumber.length() > 0) {
                String rawPattern = getRawNumber(mPattern);
                if(!mCanEnterLongerNumber) {
                    if(rawNumber.length() > rawPattern.length()) {
                        rawNumber = rawNumber.substring(0, rawPattern.length());
                    }
                }
                int diff = rawPattern.length() - rawNumber.length();
                String paddedRawNumber = addPadding(rawNumber, diff);
                String paddedFormattedNumber = formatNationalNumber(mCountry, paddedRawNumber);
                setText(paddedFormattedNumber, diff);
            } else {
                setText("", 0);
            }

        }

        private void setText(String paddedFormattedNumber, int diff) {
            mPhoneNumber.removeTextChangedListener(this);
            mPhoneNumber.setText(removePaddingsFromFormattedNumber(paddedFormattedNumber, diff));
            mPhoneNumber.addTextChangedListener(this);
            mPhoneNumber.setSelection(getLastNonWhitespacePosition(mPhoneNumber.getText().toString()));
        }

        private String addPadding(String rawNumber, int diff) {
            String result = rawNumber + "";
            for(int i = 0; i < diff; i++) {
                result += "0";
            }
            return result;
        }

        private String removePaddingsFromFormattedNumber(String number, int diff) {
            String result = number + "";
            int i = number.length() - 1;
            while(i >= 0 && diff > 0) {
                if(isWhiteSpace(number.charAt(i))) {
                    result = number.substring(0, i);
                } else if(number.charAt(i) == '0') {
                    result = number.substring(0, i);
                    diff--;
                }
                i--;
            }
            return result;
        }

        private int getLastNonWhitespacePosition(String number) {
            for(int i = number.length() - 1; i >= 0; i--) {
                if(!isWhiteSpace(number.charAt(i))) return i + 1;
            }
            return 0;
        }

        private boolean isWhiteSpace(char c) {
            return c == '(' || c == ')' || c == ' ' || c == '-';
        }
    }

}
