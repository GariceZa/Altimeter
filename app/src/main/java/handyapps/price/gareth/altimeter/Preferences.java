package handyapps.price.gareth.altimeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Gareth on 2015-02-08.
 * Package: handyapps.price.gareth.altimeter
 */
public class Preferences {

    Context context;

    public Preferences(Context cont){

        context = cont;
    }

    // returns the users height unit preference
    protected double getHeightUnit(){

        SharedPreferences get = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.parseDouble(get.getString("unit", "1"));
    }
}
