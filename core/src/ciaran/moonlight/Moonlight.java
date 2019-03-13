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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ciaran.moonlight.shared.Character;


public class Moonlight implements Screen {
  private static final float TIME_STEP = 1 / 60f;
  private static final int NUMBER_OF_MONSTERS = 1;

  Random rand = new Random();
  private final Orchestrator parent;

  List<Monster> monsters = new ArrayList<>();

  Player player;
//  List<Player> otherPlayers = new ArrayList<>();

  SpriteBatch batch;
  SpriteBatch uiBatch;
  ShapeRenderer shapeRenderer;

  Sprite brick;
  Sprite background;

  float CAM_WIDTH;
  float CAM_HEIGHT;

  float BACKGROUND_WIDTH;
  float BACKGROUND_HEIGHT;

  private OrthographicCamera cam;

  BitmapFont font;

  private float physicsTimeAccumulator = 0;
  World world;
  Box2DDebugRenderer debugRenderer;

  private boolean paused;
  private float pauseDelta;

  public Moonlight(Orchestrator parent) {
    this.parent = parent;
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));

    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 18;

    world = new World(new Vector2(0, -98f), true);
    debugRenderer = new Box2DDebugRenderer();

    font = generator.generateFont(parameter);

    shapeRenderer = new ShapeRenderer();
    batch = new SpriteBatch();
    uiBatch = new SpriteBatch();
    brick = new Sprite(new Texture("images/brick.png"));

    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();

    CAM_WIDTH = 30;
    CAM_HEIGHT = 30 * (h / w);

    cam = new OrthographicCamera(CAM_WIDTH, CAM_HEIGHT);

    cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
    cam.update();
    createBackground();

    createPlayers();
    createDemons();
    createBlobs();
    createGroundBody();
    createMurderer();
    createBricks();
  }

  private void createBricks() {
    brick.setSize(5, 1);
    brick.setOriginCenter();
    brick.setOriginBasedPosition(10, 10);
    BodyDef groundBodyDef = new BodyDef();
    groundBodyDef.position.set(new Vector2(10, 10));
    Body brickBody = world.createBody(groundBodyDef);
    PolygonShape brickBox = new PolygonShape();
    brickBox.setAsBox(2.5f, 0.5f);
    brickBody.createFixture(brickBox, 0.0f);
    brickBox.dispose();
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

  private void createGroundBody() {
    BodyDef groundBodyDef = new BodyDef();
    groundBodyDef.position.set(new Vector2(-50, 0));

    Body groundBody = world.createBody(groundBodyDef);

    // Create a polygon shape
    PolygonShape groundBox = new PolygonShape();
    groundBox.setAsBox(100, 1.0f);
    // Create a fixture from our polygon shape and add it to our ground body
    groundBody.createFixture(groundBox, 0.0f);
    // Clean up after ourselves
    groundBox.dispose();
  }

  private void createPlayers() {
    player = new Player(world);
  }

  private void createDemons() {
    for (int i = 0; i < NUMBER_OF_MONSTERS; i++) {
      monsters.add(new Monster(2, 3, "images/demonSword.atlas", "demonRight", "demonLeft"));
    }
  }

  private void createBlobs() {
    monsters.add(new Monster(2, 3, "images/blob.atlas", "blobRight", "blobLeft"));
  }
  private void createMurderer() {
    monsters.add(new Monster(4, 6, "images/murder/murder.atlas", "murdererRight", "murdererLeft"));
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

    background.setPosition(-BACKGROUND_WIDTH * 1.5f + (player.getX() - player.getX() % BACKGROUND_WIDTH), -14.4f);
    background.draw(batch);

    if (!paused) {
      player.draw(batch, deltaTime);
//      otherPlayers.forEach(player -> player.getSprite().draw(batch));

      monsters.forEach(monster -> monster.getSprite().draw(batch));

//      for (int i = 0; i < monsters.size(); i++) {
//        Monster monster = monsters.get(i);
//        monster.getSprite().draw(batch);
//      }

      brick.draw(batch);
      pauseDelta = 0;
    } else {
      pauseDelta += deltaTime;
      if (pauseDelta >= 0.1) {
        parent.toggleMenu();
      }
    }
    batch.end();

    boolean overlappingAMonster = monsters
      .stream()
      .filter(monster -> !monster.isDead)
      .anyMatch(monster ->
        monster.getSprite().getBoundingRectangle().overlaps(player.getLogicalBoundingRectangle())
      );

    if (overlappingAMonster)
    {
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
    if (player.isDead()) {
      font.draw(uiBatch, "You Fucking Suck", 50, 50);
    }
    uiBatch.end();

    doPhysicsStep(deltaTime);
//    debugRenderer.render(world, cam.combined);
  }

  private void doPhysicsStep(float deltaTime) {
    // fixed time step
    // max frame time to avoid spiral of death (on slow devices)
    float frameTime = Math.min(deltaTime, 0.25f);
    physicsTimeAccumulator += frameTime;
    while (physicsTimeAccumulator >= TIME_STEP) {
      world.step(TIME_STEP, 6, 2);
      physicsTimeAccumulator -= TIME_STEP;
    }
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
    boolean isAPressed = Gdx.input.isKeyPressed(Input.Keys.A);
    boolean isWalkButtonHeld = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

    boolean escPressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    if (escPressed) {
      paused = true;
    }

    if (isLeftPressed && !player.isDead()) {
      player.rotateLeft();
      player.move();
    }

    if (isAPressed){
      player.currentlyPunching(true);

      Rectangle punchBox = player.getPunchBox();
      monsters.forEach(monster -> {
        if (punchBox.overlaps(monster.getSprite().getBoundingRectangle())) {
          float kickback = player.isFacingRight() ? 2 : - 2;
          monster.setPosition(monster.getX() + kickback, monster.getY());
          monster.takeDamage(20);
        }
      });

      // code here please
    }

    if (isRightPressed && !player.isDead()) {
      player.rotateRight();
      player.move();
    }
//    if (isAPressed){
//      player.punch();
//    }

    player.setWalking(isWalkButtonHeld);

//    if (player.getLogicalBoundingRectangle().overlaps(brick.getBoundingRectangle()) && !player.isDead()) {
////      player.setPosition(player.getX(), brick.getY() + brick.getHeight());
//      player.setySpeed(0);
//    }

    if (isSpacePressed) {
      player.jump();
    }

//    if (parent.myId != -1) {
//      parent.networkClient.sendMovement(
//        parent.myId, player.getX(), player.getY(), player.isFacingRight()
//      );
//    }

    monstersWalk(deltaTime);

    cam.position.set(player.getX(), player.getY(), 0);
  }

  private void monstersWalk(float deltaTime) {
    monsters.forEach(monster -> {
      monster.move(deltaTime);
      if (rand.nextInt(100) == 1) {
        monster.rotate();
      }
    });
  }
}

//  public void addCharacter(Character character) {
//    if (character.name.equals(Orchestrator.NAME)) {
//      parent.myId = character.id;
//      return;
//    }
//    Player otherPlayer = new Player();
//    otherPlayer.setId(character.id);
//    otherPlayer.setPosition(character.x, character.y);
//    otherPlayers.add(otherPlayer);
//  }
//
//  public void moveCharacter(int id, float x, float y, boolean isFacingRight) {
//    System.out.println(otherPlayers);
//    if (id == parent.myId) {
//      return;
//    }
//  }
//}
//    otherPlayers
//      .stream().filter(p -> p.getId() == id)
//      .findFirst()
//      .ifPresent(p -> {
//        p.setPosition(x, y);
//        if (isFacingRight) {
//          p.rotateRight();
//        } else {
//          p.rotateLeft();
//        }
//      });
//  }
//}