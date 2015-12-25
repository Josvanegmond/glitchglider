package nl.joozey.powerup;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.ArrayList;

/**
 * Created by mint on 25-12-15.
 */
public abstract class BaseButton extends ImageButton {

    public BaseButton() {
        super(new BaseDrawable());
    }

    public BaseButton(Skin skin) {
        super(skin);
    }

    public BaseButton(Skin skin, String styleName) {
        super(skin, styleName);
    }

    public BaseButton(ImageButtonStyle style) {
        super(style);
    }

    public BaseButton(Drawable imageUp) {
        super(imageUp);
    }

    public BaseButton(Drawable imageUp, Drawable imageDown) {
        super(imageUp, imageDown);
    }

    public BaseButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
    }
}
