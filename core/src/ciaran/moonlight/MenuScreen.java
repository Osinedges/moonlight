package ciaran.moonlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

  private final Orchestrator parent;
  private Stage stage;

  public MenuScreen(Orchestrator parent){
    this.parent = parent;
    /// create stage and set it as input processor
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);
    Table table = new Table();
    table.setFillParent(true);
//    table.setDebug(true);
    stage.addActor(table);

    Skin skin = new Skin(Gdx.files.internal("C:\\moonlight\\android\\assets\\skin\\skin.json"));

    //create buttons
    TextButton newGame = new TextButton("New Game", skin);
    TextButton preferences = new TextButton("Preferences", skin);
    TextButton exit = new TextButton("Exit", skin);

    //add buttons to table
    table.add(newGame).fillX().uniformX();
    table.row().pad(10, 0, 10, 0);
    table.add(preferences).fillX().uniformX();
    table.row();
    table.add(exit).fillX().uniformX();
  }

  @Override
  public void show() {
  }

  @Override
  public void render(float delta) {
    stage.draw();
    handleInput();
  }

  private void handleInput() {
    boolean escPressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    if (escPressed) {
      parent.toggleMenu();
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

  }
}
