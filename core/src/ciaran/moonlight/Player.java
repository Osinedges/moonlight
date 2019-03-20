package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Player {
  private static final float WIDTH = 2;
  private static final float HEIGHT = 3;
  private static final float MAX_VELOCITY = 20;
  private static final int[] experiences= {
    0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973,
    4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406,
    24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333,
    111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599,
    407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443,
    1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776,
    4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431
  };

  TextureAtlas playerAtlas;

  private int id;
  private int xp = 0;
  private int lvl = 1;
  private int hp = 100;
  private boolean dead = false;
  private boolean facingRight;
  private boolean walking;
  private boolean punching;
  private boolean punchCounter;
  private TextureAtlas textureAtlas;
  private Animation<TextureRegion> animationWalk;
  private Animation<TextureRegion> animationIdle;
  private Animation<TextureRegion> animationJump;
  private Animation<TextureRegion> animationPunchOne;
  private Animation<TextureRegion> animationPunchTwo;
  private Animation<TextureRegion> animationDeath;
  private float stateTime;
  private Sprite sprite = new Sprite();
  private Body body;

  Sound jumping;
  Sound stepping;
  Sound punchSoundOne;
  Sound punchSoundTwo;

  public Player(World world) {
    textureAtlas = new TextureAtlas(Gdx.files.internal("images/character1/player.atlas"));
    Array<TextureAtlas.AtlasRegion> idle = textureAtlas.findRegions("idle");
    Array<TextureAtlas.AtlasRegion> walk = textureAtlas.findRegions("walk");
    Array<TextureAtlas.AtlasRegion> jump = textureAtlas.findRegions("jump");
    Array<TextureAtlas.AtlasRegion> punchOne= textureAtlas.findRegions("punchOne");
    Array<TextureAtlas.AtlasRegion> punchTwo = textureAtlas.findRegions("punchTwo");
    Array<TextureAtlas.AtlasRegion> death = textureAtlas.findRegions("death");

    animationPunchOne = new Animation<TextureRegion>(1/11.25f, punchOne);
    animationPunchOne.setPlayMode(Animation.PlayMode.LOOP);

    animationPunchTwo = new Animation<TextureRegion>(1/11.25f, punchTwo);
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
    punchSoundOne = Gdx.audio.newSound(Gdx.files.internal("audio/punchOne.ogg"));
    punchSoundTwo = Gdx.audio.newSound(Gdx.files.internal("audio/punchTwo.ogg"));

    createBody(world);
  }

  private void createBody(World world) {
    BodyDef bodyDef = new BodyDef();
    bodyDef.fixedRotation = true;
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set(new Vector2(0, 10));

    body = world.createBody(bodyDef);

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(1, 2);

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = shape;
    fixtureDef.density = 0.5f;
    fixtureDef.friction = 0.6f;
    fixtureDef.restitution = 0.2f; // Make it bounce a little bit

    body.createFixture(fixtureDef);
    shape.dispose();
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

  public void addXP(int addxp) {
    xp = xp + addxp;
  }

  public int getLevelAtExperience(int experience) {
    int index;

    for (index = 0; index < experiences.length; index++) {
      if (experiences[index + 1] > experience)
        break;
    }

    return index;
  }

  public int getXp() {
    return xp;
  }
  public boolean isDead(){
    return dead;
  }

  public void currentlyPunching(boolean punchNow){
    if (punchNow && !punching) {
      stateTime = 0;
    }
    punching = punchNow;
  }
  public void updateLvl(){
    lvl = getLevelAtExperience(xp);
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
      if (!dead){
      dead = true;
      stateTime = 0;
    }
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
      rotateRight();
    }
  }

  public void draw(SpriteBatch batch, BitmapFont font, float deltaTime) {
    stateTime += deltaTime;

    Animation<TextureRegion> animation;

    if (dead) {
      animation = animationDeath;
    } else if (punching && punchCounter) {
      animation = animationPunchOne;
      punchSoundOne.play(0.1f);
      punchSoundTwo.stop();
    } else if (punching) {
      animation = animationPunchTwo;
      punchSoundTwo.play(0.1f);
      punchSoundOne.stop();
    } else if (Math.abs(body.getLinearVelocity().y) >= 2f) {
      animation = animationJump;
    } else if (walking) {
      animation = animationWalk;
    } else {
      animation = animationIdle;
      punchSoundOne.stop();
      punchSoundTwo.stop();
    }

    if (punching && animation.isAnimationFinished(stateTime)) {
      punching = false;
      punchCounter = !punchCounter;
    }

    TextureRegion keyFrame = animation.getKeyFrame(stateTime);
    sprite = new Sprite(keyFrame);
    sprite.flip(!facingRight, false);
    sprite.setSize(6, 6);
    sprite.setOrigin(3.5f, 3.4f);
    sprite.setOriginBasedPosition(body.getPosition().x, body.getPosition().y);
    sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
//    font.draw(batch, "Player 1", getX(), getY() + 2);
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
    if (isDead()) {
      return;
    }
    body.applyLinearImpulse(0, body.getMass() * 50, body.getPosition().x, body.getPosition().y, true);
    jumping.play(0.1f);
  }

  public float getY() {
    return body.getPosition().y;
  }

  public float getX() {
    return body.getPosition().x;
  }

  public boolean isFacingRight() {
    return facingRight;
  }

  public void move() {
    Vector2 vel = body.getLinearVelocity();
    Vector2 pos = body.getPosition();

    if (!facingRight && vel.x > -MAX_VELOCITY) {
      body.applyLinearImpulse(new Vector2(-30.00f, 0f), body.getWorldCenter(), true);
    }

    if (facingRight && vel.x < MAX_VELOCITY) {
      body.applyLinearImpulse(new Vector2(30.00f, 0f), body.getWorldCenter(), true);
    }

    System.out.println(body.getWorldCenter());
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

  public Rectangle getPunchBox() {
    float halfWidth = getLogicalBoundingRectangle().width / 2;

    Rectangle punchBox = new Rectangle();
    punchBox.x = getLogicalBoundingRectangle().x + (facingRight ? halfWidth : - halfWidth);
    punchBox.y = getLogicalBoundingRectangle().y;
    punchBox.width = getLogicalBoundingRectangle().width;
    punchBox.height = getLogicalBoundingRectangle().height;

    return punchBox;
  }

  public void dispose() {
    playerAtlas.dispose();
  }
}
