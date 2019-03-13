package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StaticItem {
  private Sprite sprite;

  public StaticItem(float x, float y, float width, float height, String textureFile) {
    Texture texture = new Texture(Gdx.files.internal(textureFile));
    sprite = new Sprite(texture);
    sprite.setSize(width, height);
    sprite.setPosition(x, y);
  }

  public Sprite getSprite() {
    return sprite;
  }
}
