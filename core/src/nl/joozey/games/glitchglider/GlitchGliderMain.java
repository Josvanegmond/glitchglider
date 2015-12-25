package nl.joozey.games.glitchglider;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import nl.joozey.games.glitchglider.entity.CargoShip;
import nl.joozey.games.glitchglider.entity.ReconShip;
import nl.joozey.games.glitchglider.entity.Terrain;
import nl.joozey.powerup.Asset;
import nl.joozey.powerup.AssetService;
import nl.joozey.powerup.GameMain;
import nl.joozey.powerup.Log;
import nl.joozey.powerup.Skybox;
import nl.joozey.powerup.TextureAsset;

public class GlitchGliderMain extends GameMain implements AssetService.OnLoadAssetCallback {

    private AssetService _as;
    private ReconShip _playerShip;
    private Terrain _terrain;

    public void initialise() {
        _as = AssetService.getInstance();
        _as.loadAsset(this, CargoShip.class);
        _as.loadAsset(this, ReconShip.class);
        _as.loadAsset(this, Terrain.class);

        _as.loadAsset(this, new TextureAsset("eye_cross.png"));
        _as.loadAsset(this, new TextureAsset("eye_stereo.png"));
        _as.loadAsset(this, new TextureAsset("eye_normal.png"));

        Environment environment = new Environment();
        environment.add(new DirectionalLight().set(1f, .9f, .8f, .75f, -.65f, .25f));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0f, 1f, 1f, 1f));
        setEnvironment(environment);

        Skybox skybox = new Skybox(
                Gdx.files.internal("envmaps/left2.jpg"),
                Gdx.files.internal("envmaps/right2.jpg"),
                Gdx.files.internal("envmaps/top2.jpg"),
                Gdx.files.internal("envmaps/bottom2.jpg"),
                Gdx.files.internal("envmaps/front2.jpg"),
                Gdx.files.internal("envmaps/back2.jpg"),
                Gdx.files.internal("envmaps/horizon.bmp"));
        setSkybox(skybox);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            GameControlHelper.getInstance().setControl(GameControlHelper.ControlType.MOBILE);
        }

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            GameControlHelper.getInstance().setControl(GameControlHelper.ControlType.DESKTOP);
        }
    }

    @Override
    public void onAssetLoaded(Class<? extends Asset> assetClass) {
        Log.d(assetClass.getName());

        if (assetClass == CargoShip.class) {
            for (int i = 0; i < 4; i++) {
                CargoShip ship = _as.instantiateAsset(CargoShip.class);
                ship.setEulerRotation((float) Math.random() * 360, (float) Math.random() * 160 - 80, (float) Math.random() * 70 - 35);
                ship.setPosition((float) Math.random() * 300 - 150, (float) Math.random() * 120 + 20, (float) Math.random() * 300 - 150);
            }
        }

        if (assetClass == ReconShip.class) {
            _playerShip = _as.instantiateAsset(ReconShip.class);
            _playerShip.setEulerRotation(0, 0, 0);
            _playerShip.setPosition(0, 0, 0);
            setCamera(_playerShip.getModelInstance().transform);
        }

        if (assetClass == Terrain.class) {
            _terrain = _as.instantiateAsset(Terrain.class);
            _terrain.setPosition(0, -7, 0);
        }
    }

    float roll, pitch;

    public void run() {
        if (_playerShip != null) {

            GameControlHelper.GameControl gameControl = GameControlHelper.getInstance().getControl();

            pitch *= 0.9f;
            pitch += gameControl.getRoll() / 180f + .45f;

            roll *= 0.8f;
            roll += gameControl.getPitch() / 180f;

            _playerShip.setEulerRoll(-roll * 45);
            _playerShip.addEulerRotation(roll, pitch, 0);
            _playerShip.addPosition(0, 0, .5f);
            setCamera(_playerShip.getModelInstance().transform);

            if (_playerShip.getPosition().len2() > 70000) {
                _playerShip.setPosition(0, 0, 0);
            }
        }
    }

    private void setCamera(Matrix4 transform) {
        getCamera().up.set(0, 1, 0);
        getCamera().direction.set(0, 0, 1);
        getCamera().position.set(new Vector3(0, 2.3f, -4.2f));
        getCamera().transform(transform);
        getCamera().lookAt(transform.cpy().translate(0, 0, 10).getTranslation(new Vector3()));
        getCamera().update();
    }
}
