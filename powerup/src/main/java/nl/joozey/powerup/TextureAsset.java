package nl.joozey.powerup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by josvanegmond on 12/12/15.
 */
public class TextureAsset extends Asset<Texture, Sprite> {

    private String _fileName;

    public TextureAsset() {}

    public TextureAsset(String fileName) {
        _fileName = fileName;
    }

    protected Sprite instantiate(Texture texture) {
        return new Sprite(texture);
    }

    protected Class<Texture> _getModelClass() {
        return Texture.class;
    }

    protected Class<Sprite> _getInstanceClass() {
        return Sprite.class;
    }

    @Override
    public String getFileName() {
        return _fileName;
    }

    @Override
    public void setFileName(String fileName) {
        _fileName = fileName;
    }
}
