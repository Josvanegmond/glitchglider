package nl.joozey.powerup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public abstract class GameMain extends ApplicationAdapter {

    private Camera _camera;
    private ModelBatch _modelBatch;

    private Environment _environment;

    private CameraInputController _cameraInputController;

    @Override
    public void create() {
        _camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.position.set(10f, 10f, 10f);
        _camera.lookAt(0, 0, 0);
        _camera.near = 0.1f;
        _camera.far = 300f;
        _camera.update();

        _environment = new Environment();
        _environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        _environment.add(new DirectionalLight().set(.8f, .8f, .8f, -1f, -.8f, -.2f));

        _modelBatch = new ModelBatch();

        _cameraInputController = new CameraInputController(_camera);
        Gdx.input.setInputProcessor(_cameraInputController);

        initialise();
    }

    @Override
    public void dispose() {
        _modelBatch.dispose();
    }

    @Override
    public void render() {

        run();

        _cameraInputController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        _modelBatch.begin(_camera);
        _modelBatch.render(AssetService.getInstance().getAssetList(ModelInstance.class), _environment);
        _modelBatch.end();
    }

    public abstract void initialise();

    public abstract void run();

    public Camera getCamera() {
        return _camera;
    }

    public void setCamera(Camera camera) {
        _camera = camera;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }
}