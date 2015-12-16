package co.cabsolutely.android.widgets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import co.cabsolutely.android.widgets.Constants;
import co.cabsolutely.android.widgets.R;
import co.cabsolutely.android.widgets.models.Country;

public class CountryAdapter extends ArrayAdapter<Country> {

    private final static int RESOURCE = R.layout.item_country;

    private final List<Country> mItems;
    private final Spinner mParent;

    public CountryAdapter(Context ctx, Spinner parent, List<Country> countries) {
        super(ctx, RESOURCE, countries);
        mItems = countries;
        mParent = parent;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }
    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    @SuppressWarnings("UnusedParameters")
    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        LayoutInflater infalter = LayoutInflater.from(getContext());
        View spinner = infalter.inflate(RESOURCE, parent, false);
        Country item = mItems.get(position);

        String value = item.getIsoName() + " (+" + item.getPhoneCode() + ")";
        TextView text = (TextView) spinner.findViewById(R.id.text1);
        text.setText(value);

        int resId = item.getFlagResource(getContext());
        ImageView icon = (ImageView) spinner.findViewById(R.id.flag);
        icon.setImageResource(resId);

        return spinner;
    }


    public void selectCountry(String initCountry) {
        String query = initCountry.toUpperCase();
        for(int i = 0; i < Constants.COUNTRIES.length; i++) {
            if(Constants.COUNTRIES[i].equals(query)) {
                mParent.setSelection(i);
                break;
            }
        }
    }
}