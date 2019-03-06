package ciaran.moonlight;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Blob {


  private final float WALK_SPEED = 10;

  private static final float WIDTH = 2;
  private static final float HEIGHT = 3;

  TextureAtlas blobAtlas;
  Sprite blobRightRegion;
  Sprite blobLeftRegion;
  Sprite blob;

  private float x;
  private float y;

  public Blob() {
    blobAtlas = new TextureAtlas("images/blob.atlas");
    blobRightRegion = blobAtlas.createSprite("blobRight");
    blobRightRegion.setSize(WIDTH, HEIGHT);
    blobLeftRegion = blobAtlas.createSprite("blobLeft");
    blobLeftRegion.setSize(WIDTH, HEIGHT);
    blob = blobRightRegion;
  }

  public void rotateLeft() {
    blob = blobLeftRegion;
  }

  public void rotateRight() {
    blob = blobRightRegion;
  }

  public void rotate() {
    if (blob == blobLeftRegion) {
      rotateRight();
    } else {
      rotateLeft();
    }
  }

  public Sprite getSprite() {
    return blob;
  }

  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    blobLeftRegion.setPosition(x, y);
    blobRightRegion.setPosition(x, y);
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void move(float deltaTime) {
    float velocity = blob == blobLeftRegion ? - WALK_SPEED : WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, getY());
  }
}
