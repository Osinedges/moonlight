package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ciaran.moonlight.shared.Character;


public class Moonlight implements Screen {
  private static final float WALK_SPEED = 20;

  Random rand = new Random();
  private final Orchestrator parent;
  Demon demon;
  Player player;
  List<Player> otherPlayers = new ArrayList<>();

  SpriteBatch batch;
  SpriteBatch uiBatch;
  ShapeRenderer shapeRenderer;

  Sprite brick;
  Sprite background;
  float ySpeed = 0;

  float CAM_WIDTH;
  float CAM_HEIGHT;

  float BACKGROUND_WIDTH;
  float BACKGROUND_HEIGHT;

  private OrthographicCamera cam;

  Sound jumping;
  Sound stepping;

  BitmapFont font;

  private boolean walking;
  private boolean paused;
  private float pauseDelta;

  public Moonlight(Orchestrator parent) {
    this.parent = parent;
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));

    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 18;

    font = generator.generateFont(parameter);

    shapeRenderer = new ShapeRenderer();
    batch = new SpriteBatch();
    uiBatch = new SpriteBatch();
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

    createPlayers();
    createDemons();
  }

  private void createBackground() {
    Texture backgroundTexture = new Texture(Gdx.files.internal("images/background.png"));
    backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    TextureRegion backgroundTextureRegion = new TextureRegion(backgroundTexture);
    background = new Sprite(backgroundTextureRegion, 0, 0, 1280 * 3, 1080);
    BACKGROUND_WIDTH = CAM_WIDTH * 1.5f;
    BACKGROUND_HEIGHT = CAM_HEIGHT * 1.5f;
    background.setSize(BACKGROUND_WIDTH * 3, BACKGROUND_HEIGHT);
  }

  private void createPlayers() {
    player = new Player();
  }

  private void createDemons() {
    demon = new Demon();
  }

  @Override
  public void show() {
    paused = false;
  }

  @Override
  public void render(float deltaTime) {
    if (!paused) {
      handleInput(deltaTime);
      cam.update();
    }

    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0.84f, 0.93f, 0.98f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();

    background.setPosition(-BACKGROUND_WIDTH * 1.5f + (player.getX() - player.getX() % BACKGROUND_WIDTH), - 15);
    background.draw(batch);

    if (!paused) {
      player.getSprite().draw(batch);
      otherPlayers.forEach(player -> player.getSprite().draw(batch));
      demon.getSprite().draw(batch);
      brick.draw(batch);
      pauseDelta = 0;
    } else {
      pauseDelta += deltaTime;
      if (pauseDelta >= 0.1) {
        parent.toggleMenu();
      }
    }
    batch.end();

    if (player.getLogicalBoundingRectangle().overlaps(demon.getSprite().getBoundingRectangle())) {
      Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(1, 0, 0, 0.2f);
      shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      shapeRenderer.end();
      player.damage(1);
    }
      uiBatch.begin();
    font.draw(uiBatch, "Level: " + player.getLvl(), 10, Gdx.graphics.getHeight() - 8);
    font.draw(uiBatch, "Experience: " + player.getXp(), 10, Gdx.graphics.getHeight() - 32);
    font.draw(uiBatch, "Hitpoints: " + player.getHp() + "/100", 10, Gdx.graphics.getHeight() - 56);
    if(player.getDead()){
      font.draw(uiBatch, "You Fucking Suck", 50, 50);
    }
    uiBatch.end();

  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {
    batch.dispose();
    player.dispose();
  }

  public void handleInput(float deltaTime) {

    boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
    boolean isSpacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    boolean isWalkButtonHeld = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

    boolean escPressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    if (escPressed) {
      paused = true;
    }

    if (walking && !isWalkButtonHeld){
      stepping.stop();
      walking = false;
    }
    else if (!walking && isWalkButtonHeld) {
      stepping.loop();
      walking = true;
    }

    if (isLeftPressed) {
      player.rotateLeft();
      player.move(deltaTime);
    }

    if (isRightPressed) {
      player.rotateRight();
      player.move(deltaTime);
    }


    player.setPosition(player.getX(), player.getY() - ySpeed * deltaTime);
    ySpeed += 80 * deltaTime;

    if (player.getY() <= 0) {
      player.setPosition(player.getX(), 0);
      ySpeed = 0;
    }

    if (player.getLogicalBoundingRectangle().overlaps(brick.getBoundingRectangle())) {
      player.setPosition(player.getX(), brick.getY() + brick.getHeight());
      ySpeed = 0;
    }

    if (isSpacePressed && ySpeed == 0) {
      jumping.play(0.1f);
      ySpeed -= 50;
    }

    if (parent.myId != -1) {
      parent.networkClient.sendMovement(
        parent.myId, player.getX(), player.getY(), player.isFacingRight()
      );
    }

    demonWalk(deltaTime);
    cam.position.set(player.getX(), player.getY(), 0);
  }

  private void demonWalk(float deltaTime){
    demon.move(deltaTime);
    if (rand.nextInt(100) == 1) {
      demon.rotate();
    }
  }

  public void addCharacter(Character character) {
    if (character.name.equals(Orchestrator.NAME)) {
      parent.myId = character.id;
      return;
    }
    Player otherPlayer = new Player();
    otherPlayer.setId(character.id);
    otherPlayer.setPosition(character.x, character.y);
    otherPlayers.add(otherPlayer);
  }

  public void moveCharacter(int id, float x, float y, boolean isFacingRight) {
    System.out.println(otherPlayers);
    if (id == parent.myId) {
      return;
    }

    otherPlayers
      .stream().filter(p -> p.getId() == id)
      .findFirst()
      .ifPresent(p -> {
        p.setPosition(x, y);
        if (isFacingRight) {
          p.rotateRight();
        } else {
          p.rotateLeft();
        }
      });
  }
}