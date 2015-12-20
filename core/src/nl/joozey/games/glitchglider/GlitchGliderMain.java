package nl.joozey.games.glitchglider;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import nl.joozey.games.glitchglider.entity.Ship;
import nl.joozey.powerup.Asset;
import nl.joozey.powerup.AssetService;
import nl.joozey.powerup.GameMain;
import nl.joozey.powerup.Log;

public class GlitchGliderMain extends GameMain implements AssetService.OnLoadAssetCallback {

    private AssetService _as;
    private Ship _playerShip;

    public void initialise() {
        _as = AssetService.getInstance();
        _as.loadAsset(this, Ship.class);
    }

    public void run() {
        if(_playerShip != null) {
            Matrix4 transform = _playerShip.getModelInstance().transform;
            transform.rotate(0, 1, 0, 1);
            setCamera(transform);
        }
    }

    @Override
    public void onAssetLoaded(Class<? extends Asset> assetClass) {
        Log.d(assetClass.getName());
        if (assetClass == Ship.class) {
            _playerShip = _as.instantiateAsset(Ship.class);
            setCamera(_playerShip.getModelInstance().transform);
        }
    }

    private void setCamera(Matrix4 transform) {
        Vector3 position = new Vector3(0, 1.2f, -1.7f);
        position.add(transform.getTranslation(new Vector3()));

        getCamera().up.set(0,1,0);
        getCamera().direction.set(0,0,1);
        getCamera().position.set(position);
        getCamera().rotate(new Quaternion().setFromMatrix(transform).conjugate());
        getCamera().update();
    }
}
