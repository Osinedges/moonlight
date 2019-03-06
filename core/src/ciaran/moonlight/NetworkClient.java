package ciaran.moonlight;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

import ciaran.moonlight.shared.Network;

public class NetworkClient {
  private Client client;
  private String name;

  public NetworkClient(String host, String name, String otherStuff) {
    client = new Client();
    client.start();

    Network.register(client);

    // ThreadedListener runs the listener methods on a different thread.
    client.addListener(new ThreadedListener(new Listener() {
      public void connected(Connection connection) {
      }

      public void received(Connection connection, Object object) {
        if (object instanceof Network.RegistrationRequired) {
          Network.Register register = new Network.Register();
          register.name = name;
          register.otherStuff = otherStuff;
          client.sendTCP(register);
        }

        if (object instanceof Network.AddCharacter) {
          Network.AddCharacter msg = (Network.AddCharacter) object;
//          ui.addCharacter(msg.character);
          return;
        }

        if (object instanceof Network.UpdateCharacter) {
//          ui.updateCharacter((Network.UpdateCharacter) object);
          return;
        }

        if (object instanceof Network.RemoveCharacter) {
          Network.RemoveCharacter msg = (Network.RemoveCharacter) object;
//          ui.removeCharacter(msg.id);
          return;
        }
      }

      public void disconnected(Connection connection) {
        System.out.println("DISCONNECTED");
      }
    }));

    try {
      client.connect(5000, host, Network.port);
      // Server communication after connection can go here, or in Listener#connected().
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    Network.Login login = new Network.Login();
    login.name = name;
    client.sendTCP(login);

    while (true) {
      int ch;
      try {
        ch = System.in.read();
      } catch (IOException ex) {
        ex.printStackTrace();
        break;
      }

      Network.MoveCharacter msg = new Network.MoveCharacter();
      switch (ch) {
        case 'w':
          msg.y = -1;
          break;
        case 's':
          msg.y = 1;
          break;
        case 'a':
          msg.x = -1;
          break;
        case 'd':
          msg.x = 1;
          break;
        default:
          msg = null;
      }
      if (msg != null) client.sendTCP(msg);
    }
  }

}
