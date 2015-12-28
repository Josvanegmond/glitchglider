package nl.joozey.powerup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

public abstract class GameMain extends ApplicationAdapter {

    private Camera _camera;
    private Camera _leftEye, _rightEye;
    private ModelBatch _modelBatch;

    private Environment _environment;
    private Skybox _skybox;
    private Color _atmosphere = new Color(0.7f, 0.88f, 0.95f, 1f);

    private CameraInputController _cameraInputController;
    private boolean _render3D = false;
    private float _eyeWidth = 1f;

    private Stage _stage;
    private Viewport _viewport;

    @Override
    public void create() {
        _camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.position.set(10f, 10f, 10f);
        _camera.lookAt(0, 0, 0);
        _camera.near = 0.1f;
        _camera.far = 300f;
        _camera.update();

        _setup3DView();

        _environment = new Environment();
        _environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        _environment.add(new DirectionalLight().set(.8f, .8f, .8f, -1f, -.8f, -.2f));

        _modelBatch = new ModelBatch();

        _cameraInputController = new CameraInputController(_camera);

        _viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), _camera);
        _stage = new Stage(_viewport);

        Gdx.input.setInputProcessor(new InputMultiplexer(_cameraInputController, _stage));

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
        _update3DView();

        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

        if (_render3D) {
            int w = Gdx.graphics.getWidth() / 2;
            int h = Gdx.graphics.getHeight();

            _viewport.setWorldSize(w, h);

            _renderScreen(_leftEye, 0, 0, w, h);
            _renderScreen(_rightEye, w, 0, w, h);
        } else {
            int w = Gdx.graphics.getWidth();
            int h = Gdx.graphics.getHeight();

            _viewport.setWorldSize(w, h);

            _renderScreen(_camera, 0, 0, w, h);
        }
    }

    private void _renderScreen(Camera camera, int x, int y, int w, int h) {
        Gdx.gl.glViewport(x, y, w, h);

        double daySpeed = 1d;
        float time = (float) ((TimeUtils.millis() / 100d * daySpeed) % 360);
        _skybox.render(time, ((ColorAttribute) (_environment.get(ColorAttribute.Fog))).color, camera);

        ColorAttribute colorAttribute = (ColorAttribute) _environment.get(ColorAttribute.Fog);
        colorAttribute.color.r = (float) (Math.cos(Math.toRadians(time)) + 1f) / 2f * _atmosphere.r + 0.03f;
        colorAttribute.color.g = (float) (Math.cos(Math.toRadians(time)) + 1f) / 2f * _atmosphere.g;
        colorAttribute.color.b = (float) (Math.cos(Math.toRadians(time)) + 1f) / 2f * _atmosphere.b + 0.08f;
        colorAttribute.color.a = 1f;


        _modelBatch.begin(camera);
        List<Asset<?, ModelInstance>> assetList = AssetService.getInstance().getAssetList(ModelInstance.class);
        for (Asset<?, ModelInstance> asset : assetList) {
            _modelBatch.render(asset.getModelInstance(), _environment, asset.getShader());
        }
        _modelBatch.end();


        _stage.draw();
    }

    public abstract void initialise();

    public abstract void run();

    public Camera getCamera() {
        return _camera;
    }

    public Stage getStage() {
        return _stage;
    }

    public void setCamera(Camera camera) {
        _camera = camera;
    }

    public void setSkybox(Skybox skybox) {
        _skybox = skybox;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }

    private void _setup3DView() {
        _leftEye = new PerspectiveCamera(75, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
        _leftEye.lookAt(0, 0, 0);
        _leftEye.near = 0.1f;
        _leftEye.far = 300f;
        _leftEye.update();

        _rightEye = new PerspectiveCamera(75, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
        _rightEye.lookAt(0, 0, 0);
        _rightEye.near = 0.1f;
        _rightEye.far = 300f;
        _rightEye.update();
    }

    private void _update3DView() {

        int focus = 30;

        _leftEye.position.set(_camera.position);
        _leftEye.direction.set(_camera.direction);
        _leftEye.up.set(_camera.up);

        _leftEye.rotateAround(_camera.direction.cpy().scl(focus).add(_camera.position), _camera.up, -1.2f);

        _leftEye.update();


        _rightEye.position.set(_camera.position);
        _rightEye.direction.set(_camera.direction);
        _rightEye.up.set(_camera.up);

        _rightEye.rotateAround(_camera.direction.cpy().scl(focus).add(_camera.position), _camera.up, 1.2f);

        _rightEye.update();
    }
}