package nl.joozey.games.glitchglider.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import nl.joozey.games.glitchglider.shader.TerrainShader;
import nl.joozey.powerup.Log;
import nl.joozey.powerup.ModelAsset;

/**
 * Created by mint on 20-12-15.
 */
public class Terrain extends ModelAsset {

    @Override
    public void onInstantiated() {

        Log.d("Terrain instantiated");
        Renderable renderable = new Renderable();
        renderable.environment = null;
        renderable.worldTransform.idt();
        getModelInstance().nodes.first().parts.first().setRenderable(renderable);

        String[] textureNames = {"drydesert.bmp", "whitedesert.bmp", "terrainblend.bmp",
                "blackrock.bmp", "redstone.bmp", "terrain2blend.bmp"};

        Texture[] terrainTextures = new Texture[textureNames.length];

        for (int i = 0; i < textureNames.length; i++) {
            terrainTextures[i] = new Texture(Gdx.files.internal(textureNames[i]), true);
            terrainTextures[i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            terrainTextures[i].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }

        TerrainShader terrainShader = new TerrainShader(
                TextureAttribute.createDiffuse(
                        new TextureRegion(terrainTextures[0], 0f, 0f, 1f, 1f)),
                TextureAttribute.createDiffuse(
                        new TextureRegion(terrainTextures[1], 0f, 0f, 1f, 1f)),
                TextureAttribute.createDiffuse(
                        new TextureRegion(terrainTextures[2], 0f, 0f, 1f, 1f)),
                TextureAttribute.createDiffuse(
                        new TextureRegion(terrainTextures[3], 0f, 0f, 1f, 1f)),
                TextureAttribute.createDiffuse(
                        new TextureRegion(terrainTextures[4], 0f, 0f, 1f, 1f)),
                TextureAttribute.createDiffuse(
                        new TextureRegion(terrainTextures[5], 0f, 0f, 1f, 1f))
        );

        terrainShader.init();
        setShader(terrainShader);
    }

    @Override
    public String getFileName() {
        return "terrain2.g3db";
    }
}
