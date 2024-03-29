package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import ciaran.moonlight.shared.Character;


public class Moonlight implements Screen {
  private static final float TIME_STEP = 1 / 60f;
  private static final int NUMBER_OF_MONSTERS = 1;

  Random rand = new Random();
  private final Orchestrator parent;

  List<Monster> monsters = new ArrayList<>();

  List<StaticItem> items = new ArrayList<>();

  List<InventoryItem> playerItems = new ArrayList<>();

  List<Player> otherPlayers = new ArrayList<>();

  Player player;


  SpriteBatch batch;
  SpriteBatch uiBatch;
  ShapeRenderer shapeRenderer;

  Sprite brick;
  Sprite background;

  float CAM_WIDTH;
  float CAM_HEIGHT;
  private float UI_WIDTH;
  private float UI_HEIGHT;

  float BACKGROUND_WIDTH;
  float BACKGROUND_HEIGHT;

  private boolean inventoryOpened;
  private boolean mobileRendered = true;

  private OrthographicCamera cam;
  private OrthographicCamera uiCam;

  BitmapFont font;

  private float physicsTimeAccumulator = 0;
  World world;
  Box2DDebugRenderer debugRenderer;

  private boolean paused;
  private float pauseDelta;


  Texture inventoryTexture = new Texture(Gdx.files.internal("images/hud/InventoryTab.png"));
  Texture statsTexture = new Texture(Gdx.files.internal("images/hud/Stats_icon.png"));
  Texture equipmentTexture = new Texture(Gdx.files.internal("images/hud/Equipment_Stats.png"));
  Texture inventoryBackground = new Texture(Gdx.files.internal("images/hud/inventoryBackground.png"));

  Texture leftButton = new Texture(Gdx.files.internal("images/mobilecontrols/leftButton.png"));
  Texture rightButton = new Texture(Gdx.files.internal("images/mobilecontrols/rightButton.png"));
  Texture aButton = new Texture(Gdx.files.internal("images/mobilecontrols/aButton.png"));
  Texture bButton = new Texture(Gdx.files.internal("images/mobilecontrols/bButton.png"));
  Texture settingsButton = new Texture(Gdx.files.internal("images/mobilecontrols/settingsButton.png"));


  Sprite inventoryTab = new Sprite(inventoryTexture);
  Sprite statsTab = new Sprite(statsTexture);
  Sprite equipmentTab = new Sprite(equipmentTexture);
  Sprite inventoryBackdrop = new Sprite(inventoryBackground);

  Sprite leftbutton = new Sprite(leftButton);
  Sprite rightbutton = new Sprite(rightButton);
  Sprite abutton = new Sprite(aButton);
  Sprite bbutton = new Sprite(bButton);
  Sprite settingsbutton = new Sprite(settingsButton);

  public Moonlight(Orchestrator parent) {
    Gdx.input.setInputProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {
      @Override
      public boolean longPress(float x, float y) {
        onLongPress(x, y);
        return false;
      }
    }));
    this.parent = parent;
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("C:\\moonlight\\android\\assets\\Montserrat-Bold.ttf"));

    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 18;

    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();

    System.out.println("\n\n Resolution: " + w + ", " + h + "\n\n");

    CAM_WIDTH = 40;
    CAM_HEIGHT = 40 * (h / w);

    UI_WIDTH = 1280;
    UI_HEIGHT = 1280 * (h / w);

    inventoryTab.setSize(100, 100);
    equipmentTab.setSize(100, 100);
    statsTab.setSize(100, 100);
    inventoryBackdrop.setSize(UI_WIDTH / 2, UI_HEIGHT / 2);

    leftbutton.setSize(160, 160);
    rightbutton.setSize(160, 160);
    abutton.setSize(150, 150);
    bbutton.setSize(150, 150);
    settingsbutton.setSize(100, 100);

    world = new World(new Vector2(0, -98f), true);
    debugRenderer = new Box2DDebugRenderer();

    font = generator.generateFont(parameter);

    shapeRenderer = new ShapeRenderer();
    batch = new SpriteBatch();
    uiBatch = new SpriteBatch();
    brick = new Sprite(new Texture("images/brick.png"));


    cam = new OrthographicCamera(CAM_WIDTH, CAM_HEIGHT);
    uiCam = new OrthographicCamera(UI_WIDTH, UI_HEIGHT);

    cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
    cam.update();


    uiCam.position.set(uiCam.viewportWidth / 2f, uiCam.viewportHeight / 2f, 0);
    uiCam.update();

    inventoryTab.setOriginBasedPosition(UI_WIDTH / 2 + 200, UI_HEIGHT + 130);
    statsTab.setPosition(inventoryTab.getX() - 150, inventoryTab.getY());
    equipmentTab.setPosition(inventoryTab.getX() + 150, inventoryTab.getY());
    inventoryBackdrop.setCenter(inventoryTab.getX(),
      inventoryTab.getY() - 5 - inventoryBackdrop.getHeight() / 2);

    leftbutton.setPosition(20, 20);
    rightbutton.setPosition(190, 20);
    abutton.setPosition(950, 20);
    bbutton.setPosition(1100, 120);
    settingsbutton.setPosition(equipmentTab.getX() + 450, inventoryTab.getY() - 20);

    createBackground();

    createPlayers();
    createGroundBody();
    createBricks();

    createItems();

    createZombie();
    createSkeleton();
    createDemon();
    createDemon();
    createZombie();
    createSkeleton();
    createDemon();
    createDemon();
    createZombie();
    createSkeleton();
    createDemon();
    createDemon();
    createDemon();
    createDemon();
    createZombie();
    createSkeleton();
    createDemon();
    createDemon();
    createZombie();
  }

  private Vector2 getTouchPos() {
    return getTouchPos(0);
  }

  private Vector2 getTouchPos(int index) {
    Vector3 unprojected = uiCam.unproject(new Vector3(Gdx.input.getX(index), Gdx.input.getY(index), 0));
    return new Vector2(unprojected.x, unprojected.y);
  }

  private void onLongPress(float x, float y) {
    Vector2 touchPos = getTouchPos();
    if (inventoryOpened) {
      playerItems
        .stream()
        .filter(item -> item.getSprite().getBoundingRectangle().contains(touchPos))
        .findAny()
        .ifPresent(item -> {
          playerItems.remove(item);
        });
    }
  }

  private void createItems() {
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
    Texture backgroundTexture = new Texture(Gdx.files.internal("images/background/levelOneBackground.png"));
    background = new Sprite(backgroundTexture);
    background.setSize(1148, 53);
    background.setPosition(-100, -10);
  }

  private void createGroundBody() {
    BodyDef groundBodyDef = new BodyDef();
    groundBodyDef.position.set(new Vector2(-50, 0));

    Body groundBody = world.createBody(groundBodyDef);

    // Create a polygon shape
    PolygonShape groundBox = new PolygonShape();
    groundBox.setAsBox(500, 1.0f);
    // Create a fixture from our polygon shape and add it to our ground body
    groundBody.createFixture(groundBox, 0.0f);
    // Clean up after ourselves
    groundBox.dispose();
  }

  private void createPlayers() {
    player = new Player(world);
  }

  private void createZombie() {
    monsters.add(new Monster(this, 6, 6, "images/zombie/zombie.atlas", "walk", "idle", "attack", "death"));
  }

  private void createSkeleton() {
    monsters.add(new Monster(this, 6, 6, "images/skeleton/skeleton.atlas", "walk", "idle", "attack", "death"));
  }

  private void createDemon() {
    monsters.add(new Monster(this, 6, 6, "images/demon/demon.atlas", "walk", "idle", "attack", "death"));
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
      uiCam.update();
    }

    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0.84f, 0.93f, 0.98f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();

    background.draw(batch);

    if (!paused) {
      player.draw(batch, font, deltaTime);
//      otherPlayers.forEach(player -> player.getSprite().draw(batch));

      monsters.forEach(monster -> monster.draw(batch, deltaTime));
      items.forEach(item -> item.getSprite().draw(batch));

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
        monster.getLogicalBoundingRectangle().overlaps(player.getLogicalBoundingRectangle())
      );

    if (overlappingAMonster) {

      Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(1, 0, 0, 0.2f);
      shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      shapeRenderer.end();
      //set monster attack animation true here.
      player.damage(1);
    }

    uiBatch.begin();
    uiBatch.setProjectionMatrix(uiCam.combined);
    Vector3 projectedLevel = uiCam.unproject(new Vector3(30, 10, 0));
    font.draw(uiBatch, "Level: " + player.getLvl(), projectedLevel.x, projectedLevel.y);
    Vector3 projectedExperience = uiCam.unproject(new Vector3(30, 50, 0));
    font.draw(uiBatch, "Experience: " + player.getXp(), projectedExperience.x, projectedExperience.y);
    Vector3 projectedHitpoints = uiCam.unproject(new Vector3(30, 90, 0));
    font.draw(uiBatch, "Hitpoints: " + player.getHp() + "/100", projectedHitpoints.x, projectedHitpoints.y);
    Vector3 projectedCoins = uiCam.unproject(new Vector3(30, 140, 0));
    font.draw(uiBatch, "Coins: " + player.getCoins(), projectedCoins.x, projectedCoins.y);

    if (mobileRendered) {
      leftbutton.draw(uiBatch);
      rightbutton.draw(uiBatch);
      abutton.draw(uiBatch);
      bbutton.draw(uiBatch);
      settingsbutton.draw(uiBatch);
    }

    inventoryTab.draw(uiBatch);
    statsTab.draw(uiBatch);
    equipmentTab.draw(uiBatch);


    if (inventoryOpened) {
      inventoryBackdrop.draw(uiBatch);
      playerItems.forEach(item -> {
        System.out.println("Drawing one player item");
        item.getSprite().draw(uiBatch);
      });
    }
//    if (Gdx.input.)
    if (Gdx.input.justTouched()) {
      Vector2 touchPos = getTouchPos();
      System.out.println("touched: " + touchPos);
      System.out.println(inventoryTab.getBoundingRectangle().getY());
      System.out.println(inventoryTab.getBoundingRectangle().getHeight());
      System.out.println();
      if (inventoryTab.getBoundingRectangle().contains(touchPos) && inventoryOpened == false) {
        inventoryOpened = true;
        System.out.println("YES YOU CLICKED THE BOX, WELL DONE...");

      } else if (inventoryTab.getBoundingRectangle().contains(touchPos) && inventoryOpened == true) {
        inventoryOpened = false;
      }
      playerItems
        .stream()
        .filter(item -> item.getSprite().getBoundingRectangle().contains(touchPos))
        .findAny()
        .ifPresent(item -> {
          switch (item.getType()) {
            case FISH:
              player.addHp(30);
              playerItems.remove(item);
              break;

            case MUSHROOM:
              player.addHp(40);
              playerItems.remove(item);
              break;

            case GOLD_GEM:
              player.addCoins(400);
              playerItems.remove(item);
              break;

            case ORANGE_GEM:
              player.addCoins(100);
              playerItems.remove(item);
              break;

            case SWORD:
              player.equipSword();
              playerItems.remove(item);
              break;

            case SKULL:
              player.addXP(150);
              playerItems.remove(item);
              break;
          }
        });
    }

    if (player.isDead()) {
      font.draw(uiBatch, "You Suck", 270, 50);
    }

    uiBatch.end();


    doPhysicsStep(deltaTime);
//    debugRenderer.render(world, cam.combined);
  }

//  public void openInventory(Sprite background){
//    background.draw(uiBatch);
//  }

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

  private boolean overlapsPlayer(Sprite sprite) {
    return sprite.getBoundingRectangle().overlaps(player.getLogicalBoundingRectangle());
  }

  public void handleInput(float deltaTime) {

    boolean screenButtonLeftPressed = false;
    boolean screenButtonRightPressed = false;
    boolean screenButtonAPressed = false;
    boolean screenButtonBPressed = false;
    boolean settingsButtonPressed = false;

    for (int index = 0; index <= 10; index++) {
      if (mobileRendered && Gdx.input.isTouched(index)) {
        Vector2 touchPos = getTouchPos(index);
        if (leftbutton.getBoundingRectangle().contains(touchPos)) {
          screenButtonLeftPressed = true;
        }
        if (rightbutton.getBoundingRectangle().contains(touchPos)) {
          screenButtonRightPressed = true;
        }
        if (abutton.getBoundingRectangle().contains(touchPos)) {
          screenButtonAPressed = true;
        }
        if (bbutton.getBoundingRectangle().contains(touchPos) && Gdx.input.justTouched()) {
          screenButtonBPressed = true;
        }
        if (settingsbutton.getBoundingRectangle().contains(touchPos) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
          settingsButtonPressed = true;
        }
      }
    }

    boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || screenButtonRightPressed;
    boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || screenButtonLeftPressed;
    boolean isSpacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || screenButtonBPressed;
    boolean isAPressed = Gdx.input.isKeyPressed(Input.Keys.A) || screenButtonAPressed;
    boolean isWalkButtonHeld = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    boolean escPressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || settingsButtonPressed;
    if (escPressed) {
      paused = true;
    }

    if (isLeftPressed && !player.isDead()) {
      player.rotateLeft();
      player.move();
    }

    if (isAPressed && !player.isDead()) {
      player.currentlyPunching(true);
      player.updateLvl();

      Rectangle punchBox = player.getPunchBox();
      monsters.forEach(monster -> {
        if (!monster.isDead && punchBox.overlaps(monster.getLogicalBoundingRectangle())) {
          float kickback = player.isFacingRight() ? 2 : -2;
          monster.setPosition(monster.getX() + kickback, monster.getY());
          if (player.isSwordEquipped()) {
            player.addXP((monster.takeDamage(20)));
          } else player.addXP((monster.takeDamage(4)));
        }
      });
    }

    if (isRightPressed && !player.isDead()) {
      player.rotateRight();
      player.move();
    }

    player.setWalking(isWalkButtonHeld);
    if (playerItems.size() >= player.getInventorySize()) {
      System.out.println("Your bag is full!");
    } else {
      items
        .stream()
        .filter(item -> overlapsPlayer(item.getSprite()))
        .findAny()
        .ifPresent(item -> {
          InventoryItem newItem = new InventoryItem();
          newItem.setType(item.getType());
          newItem.setSprite(new Sprite(item.getSprite()));
          newItem.getSprite().setSize(85, 85);
          newItem.getSprite().setPosition(
            UI_WIDTH / 3 - (1.4f * 45) + 85 * (playerItems.size() % 6),
            UI_HEIGHT - 200 - 85 * (playerItems.size() / 6)
          );
          playerItems.add(newItem);
          items.remove(item);
        });
    }


//    if (player.getLogicalBoundingRectangle().overlaps(brick.getBoundingRectangle()) && !player.isDead()) {
////      player.setPosition(player.getX(), brick.getY() + brick.getHeight());
//      player.setySpeed(0);
//    }

    if (isSpacePressed && !player.isDead()) {
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

  public void dropItem(StaticItem newItem) {
    items.add(newItem);
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