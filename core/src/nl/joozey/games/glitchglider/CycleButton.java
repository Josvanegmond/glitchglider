package nl.joozey.games.glitchglider;

import java.util.ArrayList;
import java.util.Collections;

import javax.xml.soap.Text;

import nl.joozey.powerup.BaseButton;
import nl.joozey.powerup.TextureAsset;

/**
 * Created by mint on 25-12-15.
 */
public class CycleButton extends BaseButton {

    private ArrayList<TextureAsset> _cycleTextureList = new ArrayList<TextureAsset>();

    public void addTextures(TextureAsset... textureAssets) {
        Collections.addAll(_cycleTextureList, textureAssets);
    }
}
