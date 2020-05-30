package it.uniba.di.sms.laricchia.cannon;

import android.os.Bundle;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import it.uniba.di.sms.laricchia.R;

public class CannonApp extends AppCompatActivity {
    private GestureDetector gestureDetector; // listens for double taps
    private CannonView cannonView; // custom view to display the game
    // listens for touch events sent to the GestureDetector
    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        // called when the user double taps the screen
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            cannonView.fireCannonball(e); // fire the cannonball
            return true; // the event was handled
        } // end method onDoubleTap
    }; // end gestureListener

    // called when the app first launches
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // call super's onCreate method
        setContentView(R.layout.activity_cannone); // inflate the layout

        // get the CannonView
        cannonView = (CannonView) findViewById(R.id.cannonView);

        // initialize the GestureDetector
        gestureDetector = new GestureDetector(this, gestureListener);

        // allow volume keys to set game volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    } // end method onCreate

    // when the app is pushed to the background, pause it
    @Override
    public void onPause()
    {
        super.onPause(); // call the super method
        cannonView.stopGame(); // terminates the game
    } // end method onPause

    // release resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cannonView.releaseResources();
    } // end method onDestroy

    // called when the user touches the screen in this Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get int representing the type of action which caused this event
        int action = event.getAction();

        // the user user touched the screen or dragged along the screen
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE)
        {
            cannonView.alignCannon(event); // align the cannon
        } // end if

        // call the GestureDetector's onTouchEvent method
        return gestureDetector.onTouchEvent(event);
    } // end method onTouchEvent
} // end class CannonApp
