package nl.joozey.games.glitchglider.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by mint on 14-2-15.
 */
public class TerrainShader extends BaseShader {
    private int u_camPos, u_worldTrans, u_viewTrans, u_invViewTrans, u_projTrans, u_fogColor,
            u_groundTexture, u_groundOverlay, u_groundMask,
            u_wallTexture, u_wallOverlay, u_wallMask;

    private TextureAttribute groundTexture, groundOverlay, groundMask,
            wallTexture, wallOverlay, wallMask;

    public TerrainShader(TextureAttribute groundTexture, TextureAttribute groundOverlay, TextureAttribute groundMask,
                         TextureAttribute wallTexture, TextureAttribute wallOverlay, TextureAttribute wallMask) {
        this.groundTexture = groundTexture;
        this.groundOverlay = groundOverlay;
        this.groundMask = groundMask;

        this.wallTexture = wallTexture;
        this.wallOverlay = wallOverlay;
        this.wallMask = wallMask;
    }


    public void init() {
        program = new ShaderProgram(
                Gdx.files.internal("shaders/terrainshader.vertex.glsl").readString(),
                Gdx.files.internal("shaders/terrainshader.fragment.glsl").readString());

        if (!program.isCompiled()) {
            System.out.println(program.getLog());
        }

        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_viewTrans = program.getUniformLocation("u_viewTrans");
        u_invViewTrans = program.getUniformLocation("u_invViewTrans");
        u_projTrans = program.getUniformLocation("u_projTrans");
        u_camPos = program.getUniformLocation("u_camPos");
        u_groundTexture = program.getUniformLocation("u_groundTexture");
        u_groundOverlay = program.getUniformLocation("u_groundOverlay");
        u_groundMask = program.getUniformLocation("u_groundMask");
        u_wallTexture = program.getUniformLocation("u_wallTexture");
        u_wallOverlay = program.getUniformLocation("u_wallOverlay");
        u_wallMask = program.getUniformLocation("u_wallMask");
        u_fogColor = program.getUniformLocation("u_fogColor");
    }

    private Quaternion q = new Quaternion();
    private Matrix4 m = new Matrix4();
    private Matrix4 fakeCam = new Matrix4().setTranslation(0, 0, -1f);

    public void begin(Camera camera, RenderContext context) {
        this.context = context;

        program.begin();

        program.setUniformMatrix(u_projTrans, camera.combined);
        program.setUniformMatrix(u_viewTrans, camera.view);

        Matrix4 viewCam = m.idt().rotate(
                camera.view.cpy().
                        rotate(.3f, .5f, 0f, TimeUtils.millis()).
                        getRotation(q).
                        conjugate()).
                mul(fakeCam);

        program.setUniformMatrix(u_invViewTrans, viewCam);

        program.setUniformf(u_camPos, camera.position.x, camera.position.y, camera.position.z,
                1.1881f / (camera.far * camera.far));

        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);

        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);

        program.setUniformf(u_fogColor, ((ColorAttribute) renderable.environment.get(ColorAttribute.Fog)).color);

        int groundTextureId = context.textureBinder.bind(groundTexture.textureDescription.texture);
        program.setUniformi(u_groundTexture, groundTextureId);

        int groundOverlayId = context.textureBinder.bind(groundOverlay.textureDescription.texture);
        program.setUniformi(u_groundOverlay, groundOverlayId);

        int groundMaskId = context.textureBinder.bind(groundMask.textureDescription.texture);
        program.setUniformi(u_groundMask, groundMaskId);

        int wallTextureId = context.textureBinder.bind(wallTexture.textureDescription.texture);
        program.setUniformi(u_wallTexture, wallTextureId);

        int wallOverlayId = context.textureBinder.bind(wallOverlay.textureDescription.texture);
        program.setUniformi(u_wallOverlay, wallOverlayId);

        int wallMaskId = context.textureBinder.bind(wallMask.textureDescription.texture);
        program.setUniformi(u_wallMask, wallMaskId);

        renderable.mesh.render(program,
                renderable.primitiveType,
                renderable.meshPartOffset,
                renderable.meshPartSize);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }
}
