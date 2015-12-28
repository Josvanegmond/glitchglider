package nl.joozey.games.glitchglider;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.Collections;

import nl.joozey.powerup.Asset;
import nl.joozey.powerup.BaseButton;
import nl.joozey.powerup.TextureAsset;

/**
 * Created by mint on 25-12-15.
 */
public class CycleButton extends BaseButton {

    private ArrayList<TextureAsset> _cycleTextureList = new ArrayList<TextureAsset>();

    public void addTextures(TextureAsset... textureAssets) {
        Collections.addAll(_cycleTextureList, textureAssets);
        getImage().setDrawable(new TextureRegionDrawable(textureAssets[0].getModelInstance()));
    }

    public void cycle() {
        TextureAsset textureAsset = _cycleTextureList.remove(0);
        _cycleTextureList.add(textureAsset);
        getImage().setDrawable(new TextureRegionDrawable(textureAsset.getModelInstance()));
    }
}
