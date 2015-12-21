package nl.joozey.powerup;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Created by josvanegmond on 12/12/15.
 */
public abstract class ModelAsset extends Asset<Model, ModelInstance> {

    private float _yaw;
    private float _pitch;
    private float _roll;

    private float _x;
    private float _y;
    private float _z;

    protected ModelInstance instantiate(Model model) {
        return new ModelInstance(model);
    }

    protected Class<Model> _getModelClass() {
        return Model.class;
    }

    protected Class<ModelInstance> _getInstanceClass() {
        return ModelInstance.class;
    }

    public void addMovement(float x, float y, float z) {
        _x += x;
        _y += y;
        _z += z;
        _setMovement();
    }

    public void setMovement(float x, float y, float z) {
        _x = x;
        _y = y;
        _z = z;
        _setMovement();
    }

    private void _setMovement() {
        getModelInstance().transform.setTranslation(_x, _y, _z);
    }

    public void addEulerRotation(float yaw, float pitch, float roll) {
        _yaw += yaw;
        _pitch += pitch;
        _roll += roll;
        _setEulerRotation();
    }

    public void setEulerRotation(float yaw, float pitch, float roll) {
        _yaw = yaw;
        _pitch = pitch;
        _roll = roll;
        _setEulerRotation();
    }

    private void _setEulerRotation() {
        getModelInstance().transform.setFromEulerAngles(_yaw, _pitch, _roll);
    }
}
