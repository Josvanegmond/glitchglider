package nl.joozey.games.glitchglider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import nl.joozey.games.glitchglider.entity.Ship;
import nl.joozey.games.glitchglider.entity.Terrain;
import nl.joozey.powerup.Asset;
import nl.joozey.powerup.AssetService;
import nl.joozey.powerup.GameMain;
import nl.joozey.powerup.Log;
import nl.joozey.powerup.Skybox;

public class GlitchGliderMain extends GameMain implements AssetService.OnLoadAssetCallback {

    private AssetService _as;
    private Ship _playerShip;
    private Terrain _terrain;

    public void initialise() {
        _as = AssetService.getInstance();
        _as.loadAsset(this, Ship.class);
        _as.loadAsset(this, Terrain.class);

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
    }

    @Override
    public void onAssetLoaded(Class<? extends Asset> assetClass) {
        Log.d(assetClass.getName());

        if (assetClass == Ship.class) {
            _playerShip = _as.instantiateAsset(Ship.class);
            _playerShip.setEulerRotation(0, 0, 25);
            _playerShip.setMovement(0, 400, 0);
            setCamera(_playerShip.getModelInstance().transform);
        }

        if (assetClass == Terrain.class) {
            _terrain = _as.instantiateAsset(Terrain.class);
            _terrain.setMovement(0, -7, 0);
        }
    }

    public void run() {
        if (_playerShip != null) {
            //_playerShip.addMovement(0, 0, 40);
            _playerShip.addEulerRotation(-1, 0, 0);
            setCamera(_playerShip.getModelInstance().transform);
        }
    }

    private void setCamera(Matrix4 transform) {
        getCamera().up.set(0, 1, 0);
        getCamera().direction.set(0, 0, 1);
        getCamera().position.set(new Vector3(0, 1.3f, -2.2f));
        getCamera().transform(transform);
        getCamera().lookAt(transform.cpy().translate(0, 0, 10).getTranslation(new Vector3()));
        getCamera().update();
    }
}
