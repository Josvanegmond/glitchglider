package nl.joozey.games.glitchglider;

import com.badlogic.gdx.Gdx;

/**
 * Created by mint on 25-12-15.
 */
public class GameControlHelper {

    public enum ControlType {
        DESKTOP,
        MOBILE
    }

    private GameControl _control;
    private static GameControlHelper _instance;

    public static GameControlHelper getInstance() {
        if (_instance == null) {
            _instance = new GameControlHelper();
        }
        return _instance;
    }

    public GameControl getControl() {
        return _control;
    }

    public void setControl(ControlType controlType) {
        switch (controlType) {
            case DESKTOP:
                _control = _desktopControl;
                break;

            case MOBILE:
            default:
                _control = _mobileControl;
                break;
        }
    }

    private GameControl _desktopControl = new GameControl() {

        @Override
        public float getRoll() {
            return Gdx.input.getY() - Gdx.graphics.getWidth() / 2;
        }

        @Override
        public float getPitch() {
            return -Gdx.input.getX() + Gdx.graphics.getHeight() / 2;
        }
    };

    private GameControl _mobileControl = new GameControl() {

        @Override
        public float getRoll() {
            return Gdx.input.getRoll();
        }

        @Override
        public float getPitch() {
            return Gdx.input.getPitch();
        }
    };

    public interface GameControl {
        public float getRoll();

        public float getPitch();
    }
}
