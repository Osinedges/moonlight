package ciaran.moonlight.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import ciaran.moonlight.shared.Network;
import ciaran.moonlight.shared.Character;

public class MoonlightServer {

  private final Server server;
  private final HashSet<Character> loggedIn = new HashSet<>();

  public MoonlightServer() throws IOException {
    server = new Server();

    Network.register(server);

    server.addListener(new Listener() {
      public void received(Connection c, Object object) {

        if (object instanceof Network.Login) {
//          // Ignore if already logged in.
//          if (character != null) return;

          // Reject if the name is invalid.
          String name = ((Network.Login) object).name;
          if (!validateName(name)) {
            c.close();
            return;
          }

          // Reject if already logged in.
          for (Character other : loggedIn) {
            if (other.name.equals(name)) {
              c.close();
              return;
            }
          }

          Character character = loadCharacter(name);

          // Reject if couldn't load character.
          if (character == null) {
            c.sendTCP(new Network.RegistrationRequired());
            return;
          }

          loggedIn(c, character);
          return;
        }

        if (object instanceof Network.Register) {
//          // Ignore if already logged in.
//          if (character != null) return;

          Network.Register register = (Network.Register) object;

          // Reject if the login is invalid.
          if (!validateName(register.name)) {
            c.close();
            return;
          }
          if (!validateName(register.otherStuff)) {
            c.close();
            return;
          }

          // Reject if character alread exists.
          if (loadCharacter(register.name) != null) {
            c.close();
            return;
          }

          Character character = new Character();
          character.name = register.name;
          character.otherStuff = register.otherStuff;
          character.x = 0;
          character.y = 0;
          if (!saveCharacter(character)) {
            c.close();
            return;
          }

          loggedIn(c, character);
          return;
        }

        if (object instanceof Network.MoveCharacter) {
          // Ignore if not logged in.
//          if (character == null) return;

          Network.MoveCharacter msg = (Network.MoveCharacter) object;

          // Ignore if invalid move.
          if (Math.abs(msg.x) != 1 && Math.abs(msg.y) != 1) return;

          Optional<Character> maybeCharacter = loggedIn.stream()
            .filter(candidate -> candidate.id == 1)
            .findFirst();

          if (!maybeCharacter.isPresent()) return;
          Character character = maybeCharacter.get();

          character.x += msg.x;
          character.y += msg.y;
          if (!saveCharacter(character)) {
            c.close();
            return;
          }

          Network.UpdateCharacter update = new Network.UpdateCharacter();
          update.id = character.id;
          update.x = character.x;
          update.y = character.y;
          server.sendToAllTCP(update);
          return;
        }
      }
    });
    server.bind(Network.port);
    server.start();
  }

  boolean saveCharacter(Character character) {
    File file = new File("characters", character.name.toLowerCase());
    file.getParentFile().mkdirs();

    if (character.id == 0) {
      String[] children = file.getParentFile().list();
      if (children == null) return false;
      character.id = children.length + 1;
    }

    DataOutputStream output = null;
    try {
      output = new DataOutputStream(new FileOutputStream(file));
      output.writeInt(character.id);
      output.writeUTF(character.otherStuff);
      output.writeInt(character.x);
      output.writeInt(character.y);
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    } finally {
      try {
        output.close();
      } catch (IOException ignored) {
      }
    }
  }

  private Character loadCharacter(String name) {
    File file = new File("characters", name.toLowerCase());
    if (!file.exists()) return null;
    DataInputStream input = null;
    try {
      input = new DataInputStream(new FileInputStream(file));
      Character character = new Character();
      character.id = input.readInt();
      character.name = name;
      character.otherStuff = input.readUTF();
      character.x = input.readInt();
      character.y = input.readInt();
      input.close();
      return character;
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    } finally {
      try {
        if (input != null) input.close();
      } catch (IOException ignored) {
      }
    }
  }


  void loggedIn(Connection c, Character character) {
//    c.character = character;

    // Add existing characters to new logged in connection.
    for (Character other : loggedIn) {
      Network.AddCharacter addCharacter = new Network.AddCharacter();
      addCharacter.character = other;
      c.sendTCP(addCharacter);
    }

    loggedIn.add(character);

    // Add logged in character to all connections.
    Network.AddCharacter addCharacter = new Network.AddCharacter();
    addCharacter.character = character;
    server.sendToAllTCP(addCharacter);
  }

  private boolean validateName(String value) {
    if (value == null) return false;
    value = value.trim();
    if (value.length() == 0) return false;
    return true;
  }


  public static void main(String[] args) throws IOException {
    Log.set(Log.LEVEL_DEBUG);
    new MoonlightServer();
  }
}
