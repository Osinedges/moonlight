package ciaran.moonlight;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Moonlight extends ApplicationAdapter {
  private static final float WALK_SPEED = 20;

  SpriteBatch batch;
  Sprite playerSpriteRight;
  Sprite playerSpriteLeft;
  Sprite player;
  Sprite brick;
  Sprite background;
  float xChar = 0;
  float yChar = 0;
  float ySpeed = 0;

  float CAM_WIDTH;
  float CAM_HEIGHT;

  private OrthographicCamera cam;

  private void createBackground() {
    Texture backgroundTexture = new Texture(Gdx.files.internal("images/background.jpg"));
    backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    TextureRegion backgroundTextureRegion = new TextureRegion(backgroundTexture);
    background = new Sprite(backgroundTextureRegion, 0, 0, 1280 * 3, 720);
    background.setSize(CAM_WIDTH * 3, CAM_HEIGHT);
  }

  @Override
  public void create() {
    batch = new SpriteBatch();

    playerSpriteRight = new Sprite(new Texture("images/CharFaceRight.png"));
    playerSpriteRight.setSize(2, 3);
    playerSpriteLeft = new Sprite(new Texture("images/CharFaceLeft.png"));
    playerSpriteLeft.setSize(2, 3);
    brick = new Sprite(new Texture("images/brick.png"));
    brick.setSize(5, 1);
    brick.setPosition(10, 10);

    player = playerSpriteRight;

    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();

    CAM_WIDTH = 30;
    CAM_HEIGHT = 30 * (h / w);

    cam = new OrthographicCamera(CAM_WIDTH, CAM_HEIGHT);

    cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
    cam.update();
    createBackground();
  }

  @Override
  public void render() {
    handleInput();
    cam.update();
    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();

    background.setPosition(-CAM_WIDTH / 2 + (xChar - xChar % CAM_WIDTH), - 4);
    background.draw(batch);

    player.draw(batch);
    brick.draw(batch);
    batch.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    player.getTexture().dispose();
  }

  public void handleInput() {
    Sound sound = Gdx.audio.newSound(Gdx.files.internal("images/jump.ogg"));
    Sound walksound1 = Gdx.audio.newSound(Gdx.files.internal("images/step_cloth1.ogg"));
    Sound walksound2 = Gdx.audio.newSound(Gdx.files.internal("images/step_cloth2.ogg"));
    Sound walksound3 = Gdx.audio.newSound(Gdx.files.internal("images/step_cloth3.ogg"));
    Sound walksound4 = Gdx.audio.newSound(Gdx.files.internal("images/step_cloth4.ogg"));
    float deltaTime = Gdx.graphics.getDeltaTime();
    boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
    boolean isSpacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);


    if (isLeftPressed) {
      player = playerSpriteLeft;
      xChar -= WALK_SPEED * deltaTime;
      walksound1.play();
      walksound2.play();
      walksound3.play();
      walksound4.play();
    }

    if (isRightPressed) {
      player = playerSpriteRight;
      xChar += WALK_SPEED * deltaTime;
    }


    yChar -= ySpeed * deltaTime;
    ySpeed += 80 * deltaTime;

    if (yChar < 0) {
      yChar = 0;
      ySpeed = 0;
    }


    if (isSpacePressed && yChar == 0) {
      sound.play();
      ySpeed -= 50;
    }


    player.setPosition(xChar, yChar);
    cam.position.set(xChar, yChar, 0);

  }

}