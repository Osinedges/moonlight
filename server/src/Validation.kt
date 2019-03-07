package ciaran.moonlight.server

internal fun validateName(value: String?): Boolean {
  return value?.trim { it <= ' ' } ?.isNotEmpty() ?: false
}
