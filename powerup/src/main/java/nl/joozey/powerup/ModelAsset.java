package nl.joozey.powerup;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Created by josvanegmond on 12/12/15.
 */
public abstract class ModelAsset extends Asset<Model, ModelInstance> {

    protected ModelInstance instantiate(Model model) {
        return new ModelInstance(model);
    }

    protected Class<Model> _getModelClass() {
        return Model.class;
    }

    protected Class<ModelInstance> _getInstanceClass() {
        return ModelInstance.class;
    }
}
