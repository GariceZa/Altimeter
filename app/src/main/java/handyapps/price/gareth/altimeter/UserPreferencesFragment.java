package handyapps.price.gareth.altimeter;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Gareth on 2015-02-08.
 * Package: handyapps.price.gareth.altimeter
 */
public class UserPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
