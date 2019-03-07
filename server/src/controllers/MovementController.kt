package ciaran.moonlight.server.controllers

import ciaran.moonlight.server.saveCharacter
import ciaran.moonlight.shared.Network
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server
import ciaran.moonlight.shared.Character

fun moveCharacter(
  message: Network.MoveCharacter,
  loggedInCharacters: HashSet<Character>,
  connection: Connection,
  server: Server
) {
  // Ignore if not logged in.
  // if (character == null) return;

  // Ignore if invalid move.
  // if (Math.abs(msg!!.x) != 1 && Math.abs(msg.y) != 1) return

  val character = loggedInCharacters.find { it.id == message.id } ?: return

  character.x = message.x
  character.y = message.y
  character.facingRight = message.facingRight

  if (!saveCharacter(character)) {
    println("Error: couldn't save")
    connection.close()
    return
  }

  println("Going to send out an update")
  val update = Network.UpdateCharacter(character.id, character.x, character.y, character.facingRight)
  server.sendToAllTCP(update)
}