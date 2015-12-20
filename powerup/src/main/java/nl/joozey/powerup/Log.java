package nl.joozey.powerup;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

/**
 * Created by josvanegmond on 12/12/15.
 */
public class Log {

    private static final String TAG = "Powerup";

    public Log() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    public static void e(String message) {
        Gdx.app.error(TAG, message);
        System.out.println("[Error] " + TAG + ": " + message);
    }

    public static void d(String message) {
        Gdx.app.debug(TAG, message);
        System.out.println("[Debug] " + TAG + ": " + message);
    }
}
