package nl.joozey.powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Skybox implements Disposable {

    private Matrix4 worldTrans;
    private final Pixmap[] data = new Pixmap[6];
    private ShaderProgram shader;

    private int u_worldTrans, u_viewTrans, u_fogColor, u_camPos;
    private Mesh quad;

    private String vertexShader =
            " attribute vec4 a_position; \n"+
            " attribute vec3 a_normal; \n"+
            " uniform mat4 u_worldTrans; \n"+
            " uniform mat4 u_viewTrans; \n"+
            " uniform vec4 u_camPos; \n"+
            " varying vec3 v_cubeMapUV; \n" +
            " varying vec3 v_atmosphere; \n" +
            " varying float v_fog; \n" +
            " void main() { \n" +
            "     vec3 flen = u_camPos.xyz - vec4(u_worldTrans * a_position).xyz;\n" +
            "     float fog = dot(flen, flen) * u_camPos.w;\n" +
            "     v_fog = min(fog, 1.0);" +
            "     vec4 g_position = u_worldTrans * a_position; \n" +
            "     v_cubeMapUV = normalize(g_position.xyz); \n" +
            "     v_atmosphere = vec4(u_viewTrans * a_position).xyz;\n" +
            "     gl_Position = a_position; \n" +
            " } \n";

    private String fragmentShader =
            "#ifdef GL_ES \n" +
            " precision mediump float; \n" +
            " #endif \n" +
            " uniform samplerCube u_environmentCubemap; \n" +
            " uniform vec4 u_fogColor; \n" +
            " varying vec2 v_texCoord0; \n" +
            " varying vec3 v_cubeMapUV; \n" +
            " varying vec3 v_atmosphere; \n" +
            " varying float v_fog; \n" +
            " void main() {      \n" +
            "   vec4 texColor = textureCube( u_environmentCubemap, v_cubeMapUV ); \n" +
            "   float atmosphereFactor = 1.0-max(v_atmosphere.y,0.0); \n" +
            "   gl_FragColor = mix( texColor, u_fogColor, atmosphereFactor ); \n"+
            " } \n";

    public String getDefaultVertexShader(){
        return vertexShader;
    }

    public String getDefaultFragmentShader(){
        return fragmentShader;
    }

    public Matrix4 getWorldTrans()
    {
        return this.worldTrans;
    }

    public Skybox(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY, Pixmap positiveZ, Pixmap negativeZ) {
        data[0]=positiveX;
        data[1]=negativeX;

        data[2]=positiveY;
        data[3]=negativeY;

        data[4]=positiveZ;
        data[5]=negativeZ;

        //String vert = Gdx.files.internal("shaders/cubemap.vertex.glsl").readString();
        //String frag = Gdx.files.internal("shaders/cubemap.fragment.glsl").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());

        u_worldTrans = shader.getUniformLocation("u_worldTrans");
        u_viewTrans = shader.getUniformLocation("u_viewTrans");
        u_camPos = shader.getUniformLocation("u_camPos");
        u_fogColor = shader.getUniformLocation("u_fogColor");

        quad = createQuad();

        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL20.GL_RGB, data[0].getWidth(), data[0].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[0].getPixels());
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL20.GL_RGB, data[1].getWidth(), data[1].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[1].getPixels());

        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL20.GL_RGB, data[2].getWidth(), data[2].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[2].getPixels());
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL20.GL_RGB, data[3].getWidth(), data[3].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[3].getPixels());

        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL20.GL_RGB, data[4].getWidth(), data[4].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[4].getPixels());
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL20.GL_RGB, data[5].getWidth(), data[5].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[5].getPixels());

        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MIN_FILTER,GL20.GL_LINEAR_MIPMAP_LINEAR );
        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MAG_FILTER,GL20.GL_LINEAR );
        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE );
        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE );

        Gdx.gl20.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);
    }

    public Skybox(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ, FileHandle horizon) {
        this(new Pixmap(positiveX), new Pixmap(negativeX), new Pixmap(positiveY), new Pixmap(negativeY), new Pixmap(positiveZ), new Pixmap(negativeZ));
    }

    private Quaternion q = new Quaternion();
    private Matrix4 m = new Matrix4();
    private Matrix4 fakeCam = new Matrix4().setTranslation(0,0,-1f);

    public void render( float time, Color fogColor, Camera camera ){

        shader.begin();
        this.worldTrans = m.idt().rotate(camera.view.cpy().rotate(.3f, .5f, 0, time).getRotation(q).conjugate()).mul(fakeCam);
        shader.setUniformMatrix(u_worldTrans, this.worldTrans);
        shader.setUniformMatrix(u_viewTrans, m.idt().rotate(camera.view.getRotation(q).conjugate()).mul(fakeCam));
        shader.setUniform4fv(u_fogColor, new float[]{fogColor.r, fogColor.g, fogColor.b, fogColor.a}, 0, 4);
        shader.setUniformf(u_camPos, camera.position.x, camera.position.y, camera.position.z, 1.1881f / (camera.far * camera.far) );

        //bind cubemap
        Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);

        quad.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }

    public Mesh createQuad(){
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.  ColorUnpacked(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[]
                {-1f, -1f, 0, 1, 1, 1, 1, 0, 1,
                        1f, -1f, 0, 1, 1, 1, 1, 1, 1,
                        1f, 1f, 0, 1, 1, 1, 1, 1, 0,
                        -1f, 1f, 0, 1, 1, 1, 1, 0, 0});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
        return mesh;
    }

    @Override
    public void dispose() {
        shader.dispose();
        quad.dispose();
        for(int i=0; i<6; i++)
            data[i].dispose();
    }

}