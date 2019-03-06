package ciaran.moonlight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.Random;

public class Orchestrator extends Game {
  private static Random rand = new Random();
  public static final String NAME = "Player " + rand.nextInt(10000);
  public int myId = -1;

  public NetworkClient networkClient;
  public Moonlight moonlight;
  Screen menu;

  @Override
  public void create() {
    networkClient = new NetworkClient(this, "localhost", Orchestrator.NAME, "test1");
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
