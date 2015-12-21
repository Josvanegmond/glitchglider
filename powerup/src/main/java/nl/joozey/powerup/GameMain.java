package nl.joozey.powerup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.TimeUtils;

import java.util.List;

public abstract class GameMain extends ApplicationAdapter {

    private Camera _camera;
    private ModelBatch _modelBatch;

    private Environment _environment;
    private Skybox _skybox;
    private Color _atmosphere = new Color( 0.7f, 0.88f, 0.95f, 1f );

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
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

        double daySpeed = 4d;
        float time = (float)((TimeUtils.millis()/100d * daySpeed) % 360);
        Log.d("Time: " + time);
        _skybox.render(time, ((ColorAttribute) (_environment.get(ColorAttribute.Fog))).color, _camera);

        ColorAttribute colorAttribute = (ColorAttribute)_environment.get( ColorAttribute.Fog );
        colorAttribute.color.r = (float)(Math.cos( Math.toRadians( time) ) + 1f ) / 2f * _atmosphere.r + 0.03f;
        colorAttribute.color.g = (float)(Math.cos( Math.toRadians( time) ) + 1f ) / 2f * _atmosphere.g;
        colorAttribute.color.b = (float)(Math.cos( Math.toRadians( time) ) + 1f ) / 2f * _atmosphere.b + 0.08f;
        colorAttribute.color.a = 1f;


        _modelBatch.begin(_camera);
        List<Asset<?, ModelInstance>> assetList = AssetService.getInstance().getAssetList(ModelInstance.class);
        for(Asset<?, ModelInstance> asset : assetList) {
            _modelBatch.render(asset.getModelInstance(), _environment, asset.getShader());
        }
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

    public void setSkybox(Skybox skybox) {
        _skybox = skybox;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }
}