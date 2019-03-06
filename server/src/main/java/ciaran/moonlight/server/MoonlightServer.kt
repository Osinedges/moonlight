package ciaran.moonlight.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.esotericsoftware.minlog.Log

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashSet

import ciaran.moonlight.shared.Network
import ciaran.moonlight.shared.Character

fun makeListener(server: Server, loggedInCharacters: HashSet<Character>) : Listener {
  return object : Listener() {
    override fun received(c: Connection, `object`: Any?) {

      if (`object` is Network.Login) {
        //          // Ignore if already logged in.
        //          if (character != null) return;

        // Reject if the name is invalid.
        val name = `object`.name
        if (!validateName(name)) {
          c.close()
          return
        }

        // Reject if already logged in.
        for (other in loggedInCharacters) {
          if (other.name == name) {
            c.close()
            return
          }
        }

        val character = loadCharacter(name)

        // Reject if couldn't load character.
        if (character == null) {
          c.sendTCP(Network.RegistrationRequired())
          return
        }

        loggedIn(server, loggedInCharacters, c, character)
        return
      }

      if (`object` is Network.Register) {
        //          // Ignore if already logged in.
        //          if (character != null) return;

        val register = `object` as Network.Register?

        // Reject if the login is invalid.
        if (!validateName(register!!.name)) {
          c.close()
          return
        }

        // Reject if character alread exists.
        if (loadCharacter(register.name) != null) {
          c.close()
          return
        }

        val character = Character()
        character.name = register.name
        character.otherStuff = register.otherStuff
        character.x = 0
        character.y = 0
        if (!saveCharacter(character)) {
          c!!.close()
          return
        }

        loggedIn(server, loggedInCharacters, c, character)
        return
      }

      if (`object` is Network.MoveCharacter) {
        // Ignore if not logged in.
        //          if (character == null) return;

        val msg = `object` as Network.MoveCharacter?

        // Ignore if invalid move.
        if (Math.abs(msg!!.x) != 1 && Math.abs(msg.y) != 1) return

        val character = loggedInCharacters
          .find { candidate -> candidate.id == 1 } ?: return

        character.x += msg.x
        character.y += msg.y
        if (!saveCharacter(character)) {
          c.close()
          return
        }

        val update = Network.UpdateCharacter()
        update.id = character.id
        update.x = character.x
        update.y = character.y
        server.sendToAllTCP(update)
        return
      }
    }
  }
}

internal fun saveCharacter(character: Character): Boolean {
  val file = File("characters", character.name.toLowerCase())
  file.parentFile.mkdirs()

  if (character.id == 0) {
    val children = file.parentFile.list() ?: return false
    character.id = children.size + 1
  }

  var output: DataOutputStream? = null
  try {
    output = DataOutputStream(FileOutputStream(file))
    output.writeInt(character.id)
    output.writeUTF(character.otherStuff)
    output.writeInt(character.x)
    output.writeInt(character.y)
    return true
  } catch (ex: IOException) {
    ex.printStackTrace()
    return false
  } finally {
    try {
      output!!.close()
    } catch (ignored: IOException) {
    }

  }
}

private fun loadCharacter(name: String): Character? {
  val file = File("characters", name.toLowerCase())
  if (!file.exists()) return null
  var input: DataInputStream? = null
  try {
    input = DataInputStream(FileInputStream(file))
    val character = Character()
    character.id = input.readInt()
    character.name = name
    character.otherStuff = input.readUTF()
    character.x = input.readInt()
    character.y = input.readInt()
    input.close()
    return character
  } catch (ex: IOException) {
    ex.printStackTrace()
    return null
  } finally {
    try {
      input?.close()
    } catch (ignored: IOException) {
    }

  }
}

internal fun loggedIn(server: Server,
                      loggedInCharacters: HashSet<Character>,
                      c: Connection,
                      character: Character) {
  //    c.character = character;

  // Add existing characters to new logged in connection.
  for (other in loggedInCharacters) {
    val addCharacter = Network.AddCharacter()
    addCharacter.character = other
    c!!.sendTCP(addCharacter)
  }

  loggedInCharacters.add(character)

  // Add logged in character to all connections.
  val addCharacter = Network.AddCharacter()
  addCharacter.character = character
  server.sendToAllTCP(addCharacter)
}

private fun validateName(value: String?): Boolean {
  var value: String = value ?: return false
  value = value.trim { it <= ' ' }
  return value.isNotEmpty()
}

fun main() {
  Log.set(Log.LEVEL_DEBUG)
  val server = Server()
  val loggedInCharacters = HashSet<Character>()
  Network.register(server)
  server.addListener(makeListener(server, loggedInCharacters))
  server.bind(Network.port)
  server.start()
}
