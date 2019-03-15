package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

  public Monster(Moonlight world,
                 float width,
                 float height,
                 String atlas,
                 String walk,
                 String idle,
                 String attack,
                 String death) {
    textureAtlas = new TextureAtlas(Gdx.files.internal(atlas));
    Array<TextureAtlas.AtlasRegion> Walk = textureAtlas.findRegions(walk);
    Array<TextureAtlas.AtlasRegion> Idle = textureAtlas.findRegions(idle);
    Array<TextureAtlas.AtlasRegion> Attack= textureAtlas.findRegions(attack);
    Array<TextureAtlas.AtlasRegion> Death = textureAtlas.findRegions(death);

    animationAttack = new Animation<TextureRegion>(1/11.25f, Attack);
    animationAttack.setPlayMode(Animation.PlayMode.LOOP);

    animationIdle = new Animation<TextureRegion>(1/15f, Idle);
    animationIdle.setPlayMode(Animation.PlayMode.LOOP);

    animationWalk = new Animation<TextureRegion>(1/15f, Walk);
    animationWalk.setPlayMode(Animation.PlayMode.LOOP);

    animationDeath = new Animation<TextureRegion>(1/15f, Death);
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

  public Sprite getSprite() {
    return monster;
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
