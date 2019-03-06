package ciaran.moonlight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Orchestrator extends Game {
  Screen moonlight;
  Screen menu;

  @Override
  public void create() {
    moonlight = new Moonlight(this);
    menu = new MenuScreen(this);
    setScreen(moonlight);
  }

  @Override
  public void render() {
    super.render();
  }

  public void toggleMenu() {
    if (screen == moonlight) {
      setScreen(menu);
    } else {
      setScreen(moonlight);
    }
  }
}
