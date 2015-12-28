package nl.joozey.games.glitchglider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

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
    private Camera _camera;

    public static GameControlHelper getInstance(Camera camera) {
        if (_instance == null) {
            _instance = new GameControlHelper();
        }
        _instance._camera = camera;
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
            return (_camera.viewportWidth / 2f - Gdx.input.getX()) / 270f;
        }

        @Override
        public float getPitch() {
            return (Gdx.input.getY() - _camera.viewportHeight / 2f) / 270f;
        }
    };

    private GameControl _mobileControl = new GameControl() {

        @Override
        public float getRoll() {
            return Gdx.input.getRoll() / 90f + 1f;
        }

        @Override
        public float getPitch() {
            return Gdx.input.getPitch() / 90f;
        }
    };

    public interface GameControl {
        public float getRoll();

        public float getPitch();
    }
}
