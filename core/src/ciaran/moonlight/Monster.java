package ciaran.moonlight;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Monster {
  private final float WALK_SPEED = 10;

  private final float width;
  private final float height;
  TextureAtlas monsterAtlas;
  Sprite monsterRightRegion;
  Sprite monsterLeftRegion;
  Sprite monster;

  private float x;
  private float y;
  private int hp;

  public Monster(float width,
                 float height,
                 String atlas,
                 String rightSprite,
                 String leftSprite) {
    this.width = width;
    this.height = height;
    monsterAtlas = new TextureAtlas(atlas);
    monsterRightRegion = monsterAtlas.createSprite(rightSprite);
    monsterRightRegion.setSize(width, height);
    monsterLeftRegion = monsterAtlas.createSprite(leftSprite);
    monsterLeftRegion.setSize(width, height);
    monster = monsterRightRegion;
  }

  public void rotateLeft() {
    monster = monsterLeftRegion;
  }

  public void rotateRight() {
    monster = monsterRightRegion;
  }

  public void rotate() {
    if (monster == monsterLeftRegion) {
      rotateRight();
    } else {
      rotateLeft();
    }
  }

  public Sprite getSprite() {
    return monster;
  }

  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    monsterLeftRegion.setPosition(x, y);
    monsterRightRegion.setPosition(x, y);
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void move(float deltaTime) {
    float velocity = monster == monsterLeftRegion ? - WALK_SPEED : WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, getY());
  }
}
