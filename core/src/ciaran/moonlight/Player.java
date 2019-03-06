package ciaran.moonlight;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

public class Player {
  private final float WALK_SPEED = 20;

  private static final float WIDTH = 2;
  private static final float HEIGHT = 3;

  TextureAtlas playerAtlas;
  Sprite playerRightRegion;
  Sprite playerLeftRegion;
  Sprite player;

  private int xp = 0;
  private int lvl = 1;
  private int hp = 100;
  private float x;
  private float y;
  float ySpeed = 0;

  public Player() {
    playerAtlas = new TextureAtlas("images/player.atlas");
    playerRightRegion = playerAtlas.createSprite("playerRight");
    playerRightRegion.setSize(WIDTH, HEIGHT);
    playerLeftRegion = playerAtlas.createSprite("playerLeft");
    playerLeftRegion.setSize(WIDTH, HEIGHT);
    player = playerRightRegion;
  }

  public void setXP(int xp) {
    xp = this.xp;
  }

  public int getXp() {
    return xp;
  }

  public void setLvl(int lvl) {
    this.lvl = lvl;
  }

  public int getLvl() {
    return lvl;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public int getHp() {
    return hp;
  }

  private void applyCoordinates() {
    player.setPosition(x, y);
  }

  public void rotateLeft() {
    player = playerLeftRegion;
  }

  public void rotateRight() {
    player = playerRightRegion;
  }

  public void jump() {

  }

  public void rotate() {
    if (player == playerLeftRegion) {
      rotateRight();
    } else {
      rotateLeft();
    }
  }

  public Sprite getSprite() {
    return player;
  }


  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    playerLeftRegion.setPosition(x, y);
    playerRightRegion.setPosition(x, y);
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void move(float deltaTime) {
    float velocity = player == playerLeftRegion ? -WALK_SPEED : WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, -0.7f);
  }

  public Rectangle getLogicalBoundingRectangle() {
    Rectangle visualRectangle = player.getBoundingRectangle();
    return new Rectangle(
      visualRectangle.x + 0.5f,
      visualRectangle.y,
      visualRectangle.width - 1,
      visualRectangle.height
    );
  }

  public void dispose() {
    playerAtlas.dispose();
  }
}
