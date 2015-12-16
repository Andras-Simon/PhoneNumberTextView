package co.cabsolutely.android.widgets.models;

import android.content.Context;

import co.cabsolutely.android.widgets.R;

@SuppressWarnings("unused")
public class Country {

    public static final String DRAWABLE = "drawable";
    private String name;
    private String isoName;
    private String phoneCode;

    public Country(String s, String iso, String s1) {
        name = s;
        isoName = iso;
        phoneCode = s1;
    }

    public int getFlagResource(Context context) {
        int resId = 0;
        if(isoName != null) {
            resId = context.getResources().getIdentifier("ic_flag_flat_" + isoName.toLowerCase(),
                    DRAWABLE,
                    context.getPackageName());
        }
        return resId == 0 ? R.drawable.ic_flag_flat_us : resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoName() {
        return isoName;
    }

    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}
