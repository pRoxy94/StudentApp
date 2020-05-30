package it.uniba.di.sms.laricchia.dummy;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.laricchia.sensor.SensorListActivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        SensorListActivity sla = new SensorListActivity();
        //return list of sensors
        List<Sensor> sensors = sla.getSensors();

        // Add details
        for (int i = 1; i < sensors.size(); i++) {
            String info = "Name: " + sensors.get(i).getName() + "\n" +
                    "Vendor: " + sensors.get(i).getVendor() + "\n" +
                    "Type: " + sensors.get(i).getType() + "\n" +
                    "Version: " + sensors.get(i).getVersion() + "\n" +
                    "Maximum Range: " + sensors.get(i).getMaximumRange() + "\n" +
                    "Min Delay: " + sensors.get(i).getMinDelay() + "\n" +
                    "Resolution: " + sensors.get(i).getResolution() + "\n" +
                    "Power: " + sensors.get(i).getPower();
            addItem(new DummyItem(String.valueOf(i), sensors.get(i).getName(), info));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
