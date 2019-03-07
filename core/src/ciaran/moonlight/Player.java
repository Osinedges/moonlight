package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
  private final float WALK_SPEED = 20;

  private static final float WIDTH = 2;
  private static final float HEIGHT = 3;

  TextureAtlas playerAtlas;

  private int id;
  private int xp = 0;
  private int lvl = 1;
  private int hp = 100;
  private boolean dead = false;
  private float x;
  private float y;
  private float ySpeed;
  private boolean facingRight;
  private boolean walking;
  private TextureAtlas textureAtlas;
  private Animation<TextureRegion> animationWalk;
  private Animation<TextureRegion> animationIdle;
  private Animation<TextureRegion> animationJump;
  private Animation<TextureRegion> animationPunchOne;
  private Animation<TextureRegion> animationPunchTwo;
  private Animation<TextureRegion> animationDeath;
  private float stateTime;
  private Sprite sprite = new Sprite();

  Sound jumping;
  Sound stepping;

  public Player() {
    textureAtlas = new TextureAtlas(Gdx.files.internal("images/character1/player.atlas"));
    Array<TextureAtlas.AtlasRegion> idle = textureAtlas.findRegions("idle");
    Array<TextureAtlas.AtlasRegion> walk = textureAtlas.findRegions("walk");
    Array<TextureAtlas.AtlasRegion> jump = textureAtlas.findRegions("jump");
    Array<TextureAtlas.AtlasRegion> punchOne= textureAtlas.findRegions("punchOne");
    Array<TextureAtlas.AtlasRegion> punchTwo = textureAtlas.findRegions("punchTwo");
    Array<TextureAtlas.AtlasRegion> death = textureAtlas.findRegions("death");

    animationPunchOne = new Animation<TextureRegion>(1/7.5f, punchOne);
    animationPunchOne.setPlayMode(Animation.PlayMode.LOOP);

    animationPunchTwo = new Animation<TextureRegion>(1/7.5f, punchTwo);
    animationPunchTwo.setPlayMode(Animation.PlayMode.LOOP);

    animationJump = new Animation<TextureRegion>(1/11.25f, jump);
    animationJump.setPlayMode(Animation.PlayMode.LOOP);

    animationIdle = new Animation<TextureRegion>(1/15f, idle);
    animationIdle.setPlayMode(Animation.PlayMode.LOOP);

    animationWalk = new Animation<TextureRegion>(1/15f, walk);
    animationWalk.setPlayMode(Animation.PlayMode.LOOP);

    animationDeath = new Animation<TextureRegion>(1/15f, death);
    animationDeath.setPlayMode(Animation.PlayMode.NORMAL);

    animationWalk = new Animation<TextureRegion>(1/15f, walk);
    animationWalk.setPlayMode(Animation.PlayMode.LOOP);


    jumping = Gdx.audio.newSound(Gdx.files.internal("images/jump.ogg"));
    stepping = Gdx.audio.newSound(Gdx.files.internal("images/stepping.ogg"));
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setXP(int xp) {
    xp = this.xp;
  }

  public int getXp() {
    return xp;
  }
  public boolean isDead(){
    return dead;
  }

  public void setLvl(int lvl) {
    this.lvl = lvl;
  }

  public int getLvl() {
    return lvl;
  }
  public void damage(int damage) {
    if (hp > 0) {
      hp = hp - damage;
    }
    else{
      dead = true;
      stateTime = 0;
    }
  }

  public int getHp() {
    return hp;
  }

  public void rotateLeft() {
    facingRight = false;
  }

  public void rotateRight() {
    facingRight = true;
  }

  public void rotate() {
    if (facingRight) {
      rotateLeft();
    } else {
      rotateLeft();
    }
  }

  private void handleMovement(float deltaTime) {
    ySpeed += 80 * deltaTime;
    setPosition(getX(), getY() - ySpeed * deltaTime);

    if (getY() <= 0) {
      setPosition(getX(), 0);
      ySpeed = 0;
    }
  }

  public void draw(SpriteBatch batch, float deltaTime) {
    handleMovement(deltaTime);

    stateTime += deltaTime;

    Animation<TextureRegion> animation;

    if (dead) {
      animation = animationDeath;
    } else if (ySpeed != 0) {
      animation = animationJump;
    } else if (walking) {
      animation = animationWalk;
    } else {
      animation = animationIdle;
    }

    TextureRegion keyFrame = animation.getKeyFrame(stateTime);
    sprite = new Sprite(keyFrame);
    sprite.flip(!facingRight, false);
    sprite.setSize(6, 6);
    sprite.setPosition(x, y);
    sprite.draw(batch);
  }

  public void setWalking(boolean walkingRequested) {
    if (walking && !walkingRequested && !isDead()) {
      stepping.stop();
      walking = false;
    } else if (!walking && walkingRequested && !isDead()) {
      stepping.loop();
      walking = true;
    }
    if (isDead()) {
      stepping.stop();
      walking = false;
    }
  }

  public void jump() {
    if (ySpeed != 0 || isDead()) {
      return;
    }
    jumping.play(0.1f);

    ySpeed -= 50;
  }

  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public void setySpeed(float ySpeed) {
    this.ySpeed = ySpeed;
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public boolean isFacingRight() {
    return facingRight;
  }

  public void move(float deltaTime) {
    float velocity = facingRight ? WALK_SPEED : - WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, getY());
  }

  public Rectangle getLogicalBoundingRectangle() {
    Rectangle visualRectangle = sprite.getBoundingRectangle();
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
