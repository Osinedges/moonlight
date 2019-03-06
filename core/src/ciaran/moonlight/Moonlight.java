package ciaran.moonlight;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class Moonlight extends ApplicationAdapter {
  private static final float WALK_SPEED = 20;
  Demon demon;

  SpriteBatch batch;
  SpriteBatch uiBatch;
  ShapeRenderer shapeRenderer;

  Texture playerSpriteRight;
  Texture playerSpriteLeft;
  Sprite player;
  Sprite brick;
  Sprite background;
  float ySpeed = 0;

  float CAM_WIDTH;
  float CAM_HEIGHT;

  float BACKGROUND_WIDTH;
  float BACKGROUND_HEIGHT;

  int playerXP = 0;
  int playerLvl = 1;

  private OrthographicCamera cam;

  Sound jumping;
  Sound stepping;

  BitmapFont font;

  private boolean walking;

  private void createBackground() {
    Texture backgroundTexture = new Texture(Gdx.files.internal("images/background.png"));
    backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    TextureRegion backgroundTextureRegion = new TextureRegion(backgroundTexture);
    background = new Sprite(backgroundTextureRegion, 0, 0, 1280 * 3, 1080);
    BACKGROUND_WIDTH = CAM_WIDTH * 1.5f;
    BACKGROUND_HEIGHT = CAM_HEIGHT * 1.5f;
    background.setSize(BACKGROUND_WIDTH * 3, BACKGROUND_HEIGHT);
  }

  private void createDemon() {
    demon = new Demon();
  }

  @Override
  public void create() {
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));

    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 18;

    font = generator.generateFont(parameter);

    shapeRenderer = new ShapeRenderer();
    batch = new SpriteBatch();
    uiBatch = new SpriteBatch();

    playerSpriteRight = new Texture("images/CharFaceRight.png");
    playerSpriteLeft = new Texture("images/CharFaceLeft.png");
    player = new Sprite(playerSpriteLeft);
    player.setSize(2, 3);


    brick = new Sprite(new Texture("images/brick.png"));

    brick.setSize(5, 1);
    brick.setPosition(10, 10);

    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();

    CAM_WIDTH = 30;
    CAM_HEIGHT = 30 * (h / w);

    cam = new OrthographicCamera(CAM_WIDTH, CAM_HEIGHT);

    cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
    cam.update();
    createBackground();


    jumping = Gdx.audio.newSound(Gdx.files.internal("images/jump.ogg"));
    stepping = Gdx.audio.newSound(Gdx.files.internal("images/stepping.ogg"));

    createDemon();
  }

  @Override
  public void render() {
    handleInput();
    cam.update();

    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0.84f, 0.93f, 0.98f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();

    background.setPosition(-BACKGROUND_WIDTH * 1.5f + (player.getX() - player.getX() % BACKGROUND_WIDTH), - 15);
    background.draw(batch);

    player.draw(batch);
    demon.getSprite().draw(batch);
    brick.draw(batch);
    batch.end();

    if (getLogicalPlayerRectangle().overlaps(demon.getSprite().getBoundingRectangle())) {
      Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(1, 0, 0, 0.2f);
      shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      shapeRenderer.end();
    }


    uiBatch.begin();
    font.draw(uiBatch, "Level: " + playerLvl, 10, Gdx.graphics.getHeight() - 8);
    font.draw(uiBatch, "Experience: " + playerXP, 10, Gdx.graphics.getHeight() - 32);

    uiBatch.end();

  }

  @Override
  public void dispose() {
    batch.dispose();
    player.getTexture().dispose();
  }

  public void handleInput() {

    float deltaTime = Gdx.graphics.getDeltaTime();
    boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
    boolean isSpacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    boolean isWalkButtonHeld = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);


    if (walking && !isWalkButtonHeld){
      stepping.stop();
      walking = false;
    }
    else if (!walking && isWalkButtonHeld) {
      stepping.loop();
      walking = true;
    }

    if (isLeftPressed) {
      player.setTexture(playerSpriteLeft);
      demon.rotateLeft();
      player.setX(player.getX() - WALK_SPEED * deltaTime);
    }

    if (isRightPressed) {
      player.setTexture(playerSpriteRight);
      demon.rotateRight();
      player.setX(player.getX() + WALK_SPEED * deltaTime);
    }


    player.setY(player.getY() - ySpeed * deltaTime);
    ySpeed += 80 * deltaTime;

    if (player.getY() <= 0) {
      player.setY(0);
      ySpeed = 0;
    }

    if (getLogicalPlayerRectangle().overlaps(brick.getBoundingRectangle())) {
      player.setX(brick.getY() + brick.getHeight());
      ySpeed = 0;
    }

    if (isSpacePressed && ySpeed == 0) {
      jumping.play();
      ySpeed -= 50;
    }


    demonWalk(deltaTime);

    cam.position.set(player.getX(), player.getY(), 0);

  }
  private void demonWalk(float deltaTime){
    demon.move(deltaTime);
  }
  private Rectangle getLogicalPlayerRectangle() {
    Rectangle visualRectangle = player.getBoundingRectangle();
    return new Rectangle(
      visualRectangle.x + 0.5f,
      visualRectangle.y,
      visualRectangle.width - 1,
      visualRectangle.height
    );
  }

}