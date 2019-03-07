package ciaran.moonlight.server

import ciaran.moonlight.server.controllers.login
import ciaran.moonlight.server.controllers.moveCharacter
import ciaran.moonlight.server.controllers.register
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.esotericsoftware.minlog.Log

import java.util.HashSet

import ciaran.moonlight.shared.Network
import ciaran.moonlight.shared.Character

fun main() {
  Log.set(Log.LEVEL_DEBUG)
  val server = Server()
  val loggedInCharacters = HashSet<Character>()

  Network.register(server)

  server.addListener(object : Listener() {
    override fun received(connection: Connection, obj: Any?) {
      when (obj) {
        is Network.Login -> login(obj, loggedInCharacters, connection, server)
        is Network.Register -> register(obj, loggedInCharacters, connection, server)
        is Network.MoveCharacter -> moveCharacter(obj, loggedInCharacters, connection, server)
      }
    }
  })

  server.bind(Network.port)
  server.start()
}
