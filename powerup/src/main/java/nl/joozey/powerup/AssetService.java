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
    private Map<Class<? extends Asset>, Map<String, Asset>> _loadedAssetMap;

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

    @SafeVarargs
    public final <M, I> void loadAssets(final OnLoadAssetCallback callback, final Class<? extends Asset<M, I>>... assetClasses) {
        final int[] loadedAssets = new int[]{0};
        for (final Class<? extends Asset<M, I>> assetClass : assetClasses) {
            loadAsset(new OnLoadAssetCallback() {
                @Override
                public void onAssetLoaded(Class<? extends Asset> assetClass) {
                    loadedAssets[0]++;
                    //all assets loaded?
                    if (loadedAssets[0] >= assetClasses.length) {
                        callback.onAssetLoaded(assetClass);
                    }
                }
            }, assetClass);
        }
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

        _loadAsset(callback, tempAsset);
    }

    @SafeVarargs
    public final <M, I> void loadAssets(final OnLoadAssetCallback callback, final Asset<M, I>... assets) {
        final int[] loadedAssets = new int[]{0};
        for (final Asset asset : assets) {
            _loadAsset(new OnLoadAssetCallback() {
                @Override
                public void onAssetLoaded(Class<? extends Asset> assetClass) {
                    loadedAssets[0]++;
                    //all assets loaded?
                    if (loadedAssets[0] >= assets.length) {
                        callback.onAssetLoaded(assetClass);
                    }
                }
            }, asset);
        }
    }

    private <M, I> void _loadAsset(final OnLoadAssetCallback callback, final Asset<M, I> asset) {
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

                    asset._setModel((M) _assetManager.get(asset.getFileName()));
                    _addLoadedAsset(asset);

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Asset loaded");
                            callback.onAssetLoaded(asset.getClass());
                        }
                    });
                }
            }).start();
        } else {
            Log.d("Asset already loaded " + asset.getClass().getName());
        }
    }

    private <M, I> void _addLoadedAsset(Asset<M, I> asset) {
        Map<String, Asset> assetMap = _loadedAssetMap.get(asset.getClass());
        if (assetMap == null) {
            assetMap = new HashMap<>();
            _loadedAssetMap.put(asset.getClass(), assetMap);
        }

        assetMap.put(asset.getFileName(), asset);
    }


    public <M, I, T extends Asset<M, I>> T instantiateAsset(Class<T> assetClass, String fileName) {
        T asset = null;
        try {
            asset = assetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        asset.setFileName(fileName);
        _setAssetModel(asset);
        _instantiatedAssetsList.add(asset);

        return asset;
    }

    public <M, I, T extends Asset<M, I>> T instantiateAsset(Class<T> assetClass) {
        T asset = null;
        try {
            asset = assetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        _setAssetModel(asset);
        _instantiatedAssetsList.add(asset);

        return asset;
    }

    private <M, I> void _setAssetModel(Asset<M, I> asset) {
        asset._setModel((M) _loadedAssetMap
                .get(asset.getClass())
                .get(asset.getFileName())
                ._getModel());
    }

    public <M, I> void removeInstanceAsset(Asset<M, I> asset) {
        _instantiatedAssetsList.remove(asset);
    }

    public <T> List<Asset<?, T>> getAssetList(Class<T> assetType) {
        List<Asset<?, T>> assetList = new ArrayList<>();
        for (Asset asset : _instantiatedAssetsList) {
            Object assetInstance = asset.getModelInstance();
            if (assetInstance != null) {
                if (assetInstance.getClass() == assetType) {
                    assetList.add(asset);
                }
            }
        }
        return assetList;
    }

    public interface OnLoadAssetCallback {
        void onAssetLoaded(Class<? extends Asset> assetClass);
    }
}
