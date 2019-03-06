package ciaran.moonlight;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Demon {

  private final float WALK_SPEED = 10;

  private static final float WIDTH = 2;
  private static final float HEIGHT = 3;

  TextureAtlas demonAtlas;
  Sprite demonRightRegion;
  Sprite demonLeftRegion;
  Sprite demon;

  private float x;
  private float y;

  public Demon() {
    demonAtlas = new TextureAtlas("images/demonSword.atlas");
    demonRightRegion = demonAtlas.createSprite("demonRight");
    demonRightRegion.setSize(WIDTH, HEIGHT);
    demonLeftRegion = demonAtlas.createSprite("demonLeft");
    demonLeftRegion.setSize(WIDTH, HEIGHT);
    demon = demonRightRegion;
  }

  private void applyCoordinates() {
    demon.setPosition(x, y);
  }

  public void rotateLeft() {
    demon = demonLeftRegion;
  }

  public void rotateRight() {
    demon = demonRightRegion;
  }

  public Sprite getSprite() {
    return demon;
  }

  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    demonLeftRegion.setPosition(x, y);
    demonRightRegion.setPosition(x, y);
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void move(float deltaTime) {
    float velocity = demon == demonLeftRegion ? - WALK_SPEED : WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, -0.7f);
  }
}
