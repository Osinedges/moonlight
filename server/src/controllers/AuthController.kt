package ciaran.moonlight.server.controllers

import ciaran.moonlight.server.loadCharacter
import ciaran.moonlight.server.saveCharacter
import ciaran.moonlight.server.validateName
import ciaran.moonlight.shared.Network
import ciaran.moonlight.shared.Character
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Server

private fun loggedIn(loggedInCharacters: HashSet<Character>,
                     connection: Connection,
                     server: Server,
                     character: Character) {
  // Add existing characters to new logged in connection.
  loggedInCharacters.forEach { connection.sendTCP(Network.AddCharacter(it)) }

  loggedInCharacters.add(character)

  // Add logged in character to all connections.
  server.sendToAllTCP(Network.AddCharacter(character))
}

fun register(
  message: Network.Register,
  loggedInCharacters: HashSet<Character>,
  connection: Connection,
  server: Server
) {
  // Ignore if already logged in.
  // if (character != null) return;

  // Reject if the login is invalid.
  if (!validateName(message.name)) {
    connection.close()
    return
  }

  // Reject if character alread exists.
  if (loadCharacter(message.name) != null) {
    connection.close()
    return
  }

  val character = Character(message.name, message.otherStuff, 0, 0f, 0f, false)
  if (!saveCharacter(character)) {
    connection.close()
    return
  }

  loggedIn(loggedInCharacters, connection, server, character)
  return
}

fun login(
  message: Network.Login,
  loggedInCharacters: HashSet<Character>,
  connection: Connection,
  server: Server
) {
  // Ignore if already logged in.
  // if (character != null) return;

  // Reject if the name is invalid.
  if (!validateName(message.name)) {
    println("Rejected: name invalid")
    connection.close()
    return
  }

  // Reject if already logged in.
  if (loggedInCharacters.stream().anyMatch { it.name == message.name }) {
    println("Rejected: already logged in")
    connection.close()
    return
  }

  val character = loadCharacter(message.name)

  if (character == null) {
    connection.sendTCP(Network.RegistrationRequired())
  } else {
    loggedIn(loggedInCharacters, connection, server, character)
  }
}
