package ciaran.moonlight;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Murderer {



  private final float WALK_SPEED = 10;

  private static final float WIDTH = 4;
  private static final float HEIGHT = 6;

  TextureAtlas murdererAtlas;
  Sprite murdererRightRegion;
  Sprite murdererLeftRegion;
  Sprite murderer;

  private float x;
  private float y;

  public Murderer() {
    murdererAtlas = new TextureAtlas("images/murder/murder.atlas");
    murdererRightRegion = murdererAtlas.createSprite("murdererRight");
    murdererRightRegion.setSize(WIDTH, HEIGHT);
    murdererLeftRegion = murdererAtlas.createSprite("murdererLeft");
    murdererLeftRegion.setSize(WIDTH, HEIGHT);
    murderer = murdererRightRegion;
  }

  public void rotateLeft() {
    murderer = murdererLeftRegion;
  }

  public void rotateRight() {
    murderer = murdererRightRegion;
  }

  public void rotate() {
    if (murderer == murdererLeftRegion) {
      rotateRight();
    } else {
      rotateLeft();
    }
  }

  public Sprite getSprite() {
    return murderer;
  }

  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    murdererLeftRegion.setPosition(x, y);
    murdererRightRegion.setPosition(x, y);
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void move(float deltaTime) {
    float velocity = murderer == murdererLeftRegion ? - WALK_SPEED : WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, getY());
  }
}
