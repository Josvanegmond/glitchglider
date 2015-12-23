package nl.joozey.games.glitchglider.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import nl.joozey.powerup.ModelAsset;

/**
 * Created by josvanegmond on 12/12/15.
 */
public class CargoShip extends ModelAsset {

    @Override
    public void onInstantiated() {
        ModelInstance instance = getModelInstance();
        instance.nodes.get(0).scale.set(0.1f, 0.1f, 0.1f);
        instance.calculateTransforms();
    }

    @Override
    public String getFileName() {
        return "cargoship.g3db";
    }


}
