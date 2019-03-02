package ciaran.moonlight;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Moonlight extends ApplicationAdapter {
  private static final float WALK_SPEED = 20;

  SpriteBatch batch;
  Sprite playerSpriteRight;
  Sprite playerSpriteLeft;
  Sprite player;
  Sprite brick;
  Texture background;
  float xChar = 0;
  float yChar = 0;
  float ySpeed = 0;

  static final int WORLD_WIDTH = 100;
  static final int WORLD_HEIGHT = 100;

  private OrthographicCamera cam;

  @Override
  public void create() {
    batch = new SpriteBatch();
    //Texture backgroundTexture = new Texture(Gdx.files.internal("images/background.jpg"));
    //backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
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

    cam = new OrthographicCamera(30, 30 * (h / w));

    cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
    cam.update();

    batch = new SpriteBatch();
  }

  @Override
  public void render() {
    handleInput();
    cam.update();
    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();
    //batch.draw(background, 0, 0, WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
    player.draw(batch);
    brick.draw(batch);
    batch.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    player.getTexture().dispose();
  }

  @Override
  public void resize(int width, int height) {
    cam.viewportWidth = 30f;
    cam.viewportHeight = 30f * height / width;
    cam.update();
  }

  public void handleInput() {
    float deltaTime = Gdx.graphics.getDeltaTime();
    boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
    boolean isSpacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

    if (isLeftPressed) {
      player = playerSpriteLeft;
      xChar -= WALK_SPEED * deltaTime;
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
      ySpeed -= 50;
    }


    player.setPosition(xChar, yChar);
    cam.position.set(xChar, yChar, 0);

  }

}
