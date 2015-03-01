package handyapps.price.gareth.altimeter;

import android.content.Context;
import android.location.Location;

/**
 * Created by Gareth on 2015-02-08.
 * Package: handyapps.price.gareth.altimeter
 */
public class LocationInfo {

    private Location location;
    private Context context;

    public LocationInfo(Location loc,Context con){

        location = loc;
        context = con;
    }

    // Returns a speed value based on the height unit preference
    protected int getHeight(){

        Preferences prefs = new Preferences(context);
        return (int)(location.getAltitude() * prefs.getHeightUnit());
    }

    // Returns the accuracy of the location data
    protected int getAccuracy(){

        return (int)location.getAccuracy();
    }
}
