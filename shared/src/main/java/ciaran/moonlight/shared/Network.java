package ciaran.moonlight.shared;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
  static public final int port = 54555;

  // This registers objects that are going to be sent over the network.
  static public void register (EndPoint endPoint) {
    Kryo kryo = endPoint.getKryo();
    kryo.register(Login.class);
    kryo.register(RegistrationRequired.class);
    kryo.register(Register.class);
    kryo.register(AddCharacter.class);
    kryo.register(UpdateCharacter.class);
    kryo.register(RemoveCharacter.class);
    kryo.register(Character.class);
    kryo.register(MoveCharacter.class);
  }

  static public class Login {
    public String name;
  }

  static public class RegistrationRequired {
  }

  static public class Register {
    public String name;
    public String otherStuff;
  }

  static public class UpdateCharacter {
    public int id;
    public float x, y;
    public boolean facingRight;
  }

  static public class AddCharacter {
    public Character character;

    @Override
    public String toString() {
      return "Add Character ---> " + character.toString();
    }
  }

  static public class RemoveCharacter {
    public int id;
  }

  static public class MoveCharacter {
    public int id;
    public float x, y;
    public boolean facingRight;

    @Override
    public String toString() {
      return "MoveCharacter{" +
        "id=" + id +
        ", x=" + x +
        ", y=" + y +
        ", facingRight=" + facingRight +
        '}';
    }
  }
}
