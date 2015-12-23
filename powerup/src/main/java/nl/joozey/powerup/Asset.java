package nl.joozey.powerup;

import com.badlogic.gdx.graphics.g3d.Shader;

/**
 * Created by josvanegmond on 12/12/15.
 */
public abstract class Asset<M, I> {

    private M[] _modelReference = (M[]) new Object[1];
    private I _instance;
    private Shader _shader;

    public void start() {
        AssetService.getInstance().setAssetModel(this);
        AssetService.getInstance().addInstanceAsset(this);
    }

    public void remove() {
        AssetService.getInstance().removeInstanceAsset(this);
    }

    /**
     * Package access only
     *
     * @param model
     */
    protected void _setModel(M model) {
        _modelReference[0] = model;
    }

    protected M _getModel() {
        return _modelReference[0];
    }

    protected abstract Class<M> _getModelClass();

    protected abstract Class<I> _getInstanceClass();

    public Shader getShader() {
        return _shader;
    }

    public void setShader(Shader shader) {
        _shader = shader;
    }

    public I getModelInstance() {
        M model = _getModel();
        if (model == null) {
            Log.d("Model not loaded ");
            return null;
        }

        if (_instance == null) {
            _instance = instantiate(model);
            onInstantiated();
        }
        return _instance;
    }

    public void onInstantiated() {}

    public abstract String getFileName();

    protected abstract I instantiate(M model);


}
