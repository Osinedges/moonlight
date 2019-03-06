//package ciaran.moonlight;
//
//import java.io.IOException;
//
//import com.badlogic.gdx.Gdx;
//import com.esotericsoftware.kryonet.Client;
//import com.esotericsoftware.kryonet.Connection;
//import com.esotericsoftware.kryonet.Listener;
//import com.esotericsoftware.kryonet.Listener.ThreadedListener;
//
//import ciaran.moonlight.shared.Network;
//
//public class NetworkClient {
//  private final Orchestrator orchestrator;
//  private Client client;
//  private String name;
//
//  public NetworkClient(Orchestrator orchestrator, String host, String name, String otherStuff) {
//    this.orchestrator = orchestrator;
//    client = new Client();
//    client.start();
//
//    Network.register(client);
//
//    // ThreadedListener runs the listener methods on a different thread.
//    client.addListener(new ThreadedListener(new Listener() {
//      public void connected(Connection connection) {
//      }
//
//      public void received(Connection connection, Object object) {
//        if (object instanceof Network.RegistrationRequired) {
//          Network.Register register = new Network.Register();
//          register.name = name;
//          register.otherStuff = otherStuff;
//          client.sendTCP(register);
//        }
//
//        if (object instanceof Network.AddCharacter) {
//          Network.AddCharacter msg = (Network.AddCharacter) object;
//          Gdx.app.postRunnable(() -> {
//            orchestrator.moonlight.addCharacter(msg.character);
//          });
//          return;
//        }
//
//        if (object instanceof Network.UpdateCharacter) {
//          Network.UpdateCharacter msg = (Network.UpdateCharacter) object;
//          Gdx.app.postRunnable(() -> {
//            System.out.println(msg.facingRight);
//            orchestrator.moonlight.moveCharacter(msg.id, msg.x, msg.y, msg.facingRight);
//          });
//          return;
//        }
//
//        if (object instanceof Network.RemoveCharacter) {
//          Network.RemoveCharacter msg = (Network.RemoveCharacter) object;
////          ui.removeCharacter(msg.id);
//          return;
//        }
//      }
//
//      public void disconnected(Connection connection) {
//        System.out.println("DISCONNECTED");
//      }
//    }));
//
//    try {
//      client.connect(5000, host, Network.port);
//      // Server communication after connection can go here, or in Listener#connected().
//    } catch (IOException ex) {
//      ex.printStackTrace();
//    }
//
//    Network.Login login = new Network.Login();
//    login.name = name;
//    client.sendTCP(login);
//  }
//
//  public void sendMovement(int id, float x, float y, boolean facingRight) {
//    Network.MoveCharacter msg = new Network.MoveCharacter();
//    msg.id  = id;
//    msg.x = x;
//    msg.y = y;
//    msg.facingRight = facingRight;
//    System.out.println(msg);
//    client.sendTCP(msg);
//  }
//
//}
