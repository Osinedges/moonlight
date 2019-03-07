package ciaran.moonlight.server

import java.io.*
import ciaran.moonlight.shared.Character

internal fun saveCharacter(character: Character): Boolean {
  val file = File("characters", character.name.toLowerCase())
  file.parentFile.mkdirs()

  if (character.id == 0) {
    val children = file.parentFile.list() ?: return false
    character.id = children.size + 1
  }

  return try {
    DataOutputStream(FileOutputStream(file)).use {
      it.writeInt(character.id)
      it.writeUTF(character.otherStuff)
      it.writeFloat(character.x)
      it.writeFloat(character.y)
      it.writeBoolean(character.facingRight)
    }
    true
  } catch (ex: IOException) {
    ex.printStackTrace()
    false
  }
}

internal fun loadCharacter(name: String): Character? {
  val file = File("characters", name.toLowerCase())
  if (!file.exists()) return null
  return try {
    val character = Character()
    DataInputStream(FileInputStream(file)).use {
      character.id = it.readInt()
      character.name = name
      character.otherStuff = it.readUTF()
      character.x = it.readFloat()
      character.y = it.readFloat()
      character.facingRight = it.readBoolean()
    }
    character
  } catch (ex: IOException) {
    ex.printStackTrace()
    null
  }
}
