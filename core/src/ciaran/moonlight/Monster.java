package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
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
  private Body body;

  private float x;
  private float y;
  private int hp = 100;
  boolean isDead;
  boolean isAttacking;

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
//    monsterAtlas = new TextureAtlas(atlas);
////    monsterRightRegion = monsterAtlas.createSprite(rightSprite);
//    monsterRightRegion.setSize(width, height);
////    monsterLeftRegion = monsterAtlas.createSprite(leftSprite);
//    monsterLeftRegion.setSize(width, height);
//    monster = monsterRightRegion;
  }
  public void draw(SpriteBatch batch,float deltaTime) {
    stateTime += deltaTime;

    Animation<TextureRegion> animation;

    if (isDead) {
      animation = animationDeath;
    }
    else {
      animation = animationIdle;
    }

    if (isAttacking && animation.isAnimationFinished(stateTime)) {
      isAttacking = false;
    }

    TextureRegion keyFrame = animation.getKeyFrame(stateTime);
    sprite = new Sprite(keyFrame);
//    sprite.flip(!facingRight, false);
    sprite.setSize(6, 6);
    sprite.setOrigin(3.5f, 3.4f);
    sprite.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
    sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
//    font.draw(batch, "Player 1", getX(), getY() + 2);
    sprite.draw(batch);
  }

  public void rotateLeft() {
    monster = monsterLeftRegion;
  }

  public void rotateRight() {
    monster = monsterRightRegion;
  }

  public void rotate() {
    if (isDead) {
      return;
    }
    if (monster == monsterLeftRegion) {
      rotateRight();
    } else {
      rotateLeft();
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
    monsterLeftRegion.setSize(monsterLeftRegion.getWidth(), monsterLeftRegion.getHeight() / 2);
    monsterRightRegion.setSize(monsterRightRegion.getWidth(), monsterRightRegion.getHeight() / 2);
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
    float velocity = monster == monsterLeftRegion ? -WALK_SPEED : WALK_SPEED;
    setPosition(getX() + velocity * deltaTime, getY());
  }
}
