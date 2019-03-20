package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StaticItem {

  private Sprite sprite;
  private ItemType type;

  public StaticItem(float x, float y, float width, float height, ItemType type) {
    this.type = type;
    Texture texture = new Texture(Gdx.files.internal(getImage()));
    sprite = new Sprite(texture);
    sprite.setSize(width, height);
    sprite.setPosition(x, y);
  }

  private String getImage() {
    switch (this.type) {
      case SWORD:
        return "images/items/sword.png";
      case ORANGE_GEM:
        return "images/items/orangeGem.png";
      case GOLD_GEM:
        return "images/items/goldGem.png";
      case MUSHROOM:
        return "images/items/mushroom.png";
      case FISH:
        return "images/items/fish.png";
      case SKULL:
        return "images/items/skull.png";
    }

    throw new RuntimeException("Shouldn't happen, muahaha");
  }

  public Sprite getSprite() {
    return sprite;
  }

  public ItemType getType() {
    return type;
  }
}
