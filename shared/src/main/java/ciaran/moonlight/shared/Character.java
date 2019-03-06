package ciaran.moonlight.shared;

public class Character {
  public String name;
  public String otherStuff;
  public boolean facingRight;
  public int id;
  public float x, y;

  @Override
  public String toString() {
    return "Character{" +
      "name='" + name + '\'' +
      ", otherStuff='" + otherStuff + '\'' +
      ", id=" + id +
      ", x=" + x +
      ", y=" + y +
      '}';
  }
}