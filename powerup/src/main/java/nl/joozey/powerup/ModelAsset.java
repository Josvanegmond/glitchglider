package nl.joozey.powerup;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

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

    public void addPosition(float x, float y, float z) {
        Vector3 position = new Matrix4()
                .translate(_x, _y, _z)
                .rotate(0, 1, 0, _yaw)
                .rotate(1, 0, 0, _pitch)
                .rotate(0, 0, 1, _roll)
                .translate(x, y, z)
                .getTranslation(new Vector3());

        _x = position.x;
        _y = position.y;
        _z = position.z;

        getModelInstance().transform.setTranslation(_x, _y ,_z);
    }

    public void setPosition(float x, float y, float z) {
        _x = x;
        _y = y;
        _z = z;

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

    public void setEulerRoll(float roll) {
        _roll = roll;
        _setEulerRotation();
    }

    public void setEulerPitch(float pitch) {
        _pitch = pitch;
        _setEulerRotation();
    }

    private void _setEulerRotation() {
        getModelInstance().transform.setFromEulerAngles(_yaw, _pitch, _roll);
    }

    public Vector3 getPosition() {
        return new Vector3(_x, _y, _z);
    }
}
