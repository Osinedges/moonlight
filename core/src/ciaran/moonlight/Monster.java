package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Monster {
  private final float WALK_SPEED = 10;

  private Moonlight world;
  private final float width;
  private float height;
  TextureAtlas monsterAtlas;
  Sprite monsterRightRegion;
  Sprite monsterLeftRegion;
  Sprite monster;

  private TextureAtlas textureAtlas;
  private Animation<TextureRegion> animationWalk;
  private Animation<TextureRegion> animationIdle;
  private Animation<TextureRegion> animationAttack;
  private Animation<TextureRegion> animationDeath;
  private float stateTime;
  private Sprite sprite = new Sprite();

  private float x;
  private float y;
  private int hp = 100;
  boolean isDead;
  boolean isAttacking;
  private boolean walking;
  private boolean facingRight;

  public Monster(Moonlight world,
                 float width,
                 float height,
                 String atlas,
                 String sWalk,
                 String sIdle,
                 String sAttack,
                 String sDeath) {
    textureAtlas = new TextureAtlas(Gdx.files.internal(atlas));
    Array<TextureAtlas.AtlasRegion> walk = textureAtlas.findRegions(sWalk);
    Array<TextureAtlas.AtlasRegion> idle = textureAtlas.findRegions(sIdle);
    Array<TextureAtlas.AtlasRegion> attack= textureAtlas.findRegions(sAttack);
    Array<TextureAtlas.AtlasRegion> death = textureAtlas.findRegions(sDeath);

    animationAttack = new Animation<TextureRegion>(1/11.25f, attack);
    animationAttack.setPlayMode(Animation.PlayMode.LOOP);

    animationIdle = new Animation<TextureRegion>(1/15f, idle);
    animationIdle.setPlayMode(Animation.PlayMode.LOOP);

    animationWalk = new Animation<TextureRegion>(1/15f, walk);
    animationWalk.setPlayMode(Animation.PlayMode.LOOP);

    animationDeath = new Animation<TextureRegion>(1/15f, death);
    animationDeath.setPlayMode(Animation.PlayMode.NORMAL);

    this.world = world;
    this.width = width;
    this.height = height;
  }
  public void draw(SpriteBatch batch,float deltaTime) {
    stateTime += deltaTime;

    Animation<TextureRegion> animation;

    if (isDead) {
      animation = animationDeath;
    } else if (walking) {
      animation = animationWalk;
    }
    else {
      animation = animationIdle;
    }

    if (isAttacking && animation.isAnimationFinished(stateTime)) {
      isAttacking = false;
    }

    TextureRegion keyFrame = animation.getKeyFrame(stateTime);
    sprite = new Sprite(keyFrame);
    sprite.flip(!facingRight, false);
    sprite.setSize(width, height);
    sprite.setPosition(x, y);
    sprite.draw(batch);
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
      rotateRight();
    }
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

  public void takeDamage(int damage) {
    if (isDead) {
      return;
    }
    hp = hp - damage;
    if (hp <= 0) {
      die();
    }
  }

  public void die() {
    isDead = true;

    world.dropItem(new StaticItem(x, y, 1, 1, "images/items/skull.png"));
  }


  public void setPosition(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void move(float deltaTime) {
    if (isDead) {
      return;
    }
    float velocity = facingRight ? WALK_SPEED : - WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, getY());
    walking = true;
  }
}
