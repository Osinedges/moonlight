package ciaran.moonlight;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class InventoryItem {

  private Sprite sprite;
  private ItemType type;

  public Sprite getSprite() {
    return sprite;
  }

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
  }

  public ItemType getType() {
    return type;
  }

  public void setType(ItemType type) {
    this.type = type;
  }
}
