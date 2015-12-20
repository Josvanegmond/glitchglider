package nl.joozey.powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by josvanegmond on 12/12/15.
 */
public class AssetService {

    private static final int MAX_SYNC_ASSET_LOADING = 10;
    private static AssetService _instance;

    private AssetManager _assetManager;

    private List<Asset> _instantiatedAssetsList;
    private Map<Class<? extends Asset>, Asset> _loadedAssetMap;

    public static AssetService getInstance() {
        if (_instance == null) {
            _instance = new AssetService();
        }

        return _instance;
    }

    private AssetService() {
        _assetManager = new AssetManager();
        _instantiatedAssetsList = new ArrayList<>();
        _loadedAssetMap = new HashMap<>();
    }

    public <M, I> void loadAsset(final OnLoadAssetCallback callback, final Class<? extends Asset<M, I>> assetClass) {

        Asset tempAsset = null;
        try {
            tempAsset = assetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        final Asset asset = tempAsset;
        if (asset._getModel() == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    _assetManager.load(asset.getFileName(), asset._getModelClass());

                    while (!_assetManager.isLoaded(asset.getFileName())) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                _assetManager.update();
                            }
                        });

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    asset._setModel(_assetManager.get(asset.getFileName()));
                    _loadedAssetMap.put(assetClass, asset);
                    Log.d("Asset loaded");
                    callback.onAssetLoaded(assetClass);
                }
            }).start();
        } else {
            Log.d("Asset already loaded " + asset.getClass().getName());
        }
    }

    public <T extends Asset<?, ?>> T instantiateAsset(Class<T> assetClass) {
        T asset = null;
        try {
            asset = assetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        asset.start();

        return asset;
    }

    public <M> void setAssetModel(Asset<M, ?> asset) {
        asset._setModel((M)_loadedAssetMap.get(asset.getClass())._getModel());
    }

    public void addInstanceAsset(Asset asset) {
        _instantiatedAssetsList.add(asset);
    }

    public void removeInstanceAsset(Asset asset) {
        _instantiatedAssetsList.remove(asset);
    }

    public <T> List<T> getAssetList(Class<T> assetType) {
        List<T> assetList = new ArrayList<>();
        for (Asset asset : _instantiatedAssetsList) {
            Object assetInstance = asset.getModelInstance();
            if (assetInstance != null) {
                if (assetInstance.getClass() == assetType) {
                    assetList.add((T)assetInstance);
                }
            }
        }
        return assetList;
    }

    public interface OnLoadAssetCallback {
        void onAssetLoaded(Class<? extends Asset> assetClass);
    }
}
