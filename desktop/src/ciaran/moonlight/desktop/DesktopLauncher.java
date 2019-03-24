package ciaran.moonlight.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esotericsoftware.minlog.Log;

import ciaran.moonlight.Orchestrator;

public class DesktopLauncher {

	public static void main (String[] arg) {
    Log.set(Log.LEVEL_DEBUG);
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.fullscreen = false;
    config.width = 1280;
    config.height = 800;
    new LwjglApplication(new Orchestrator(), config);
	}
}
