package ciaran.moonlight.shared;

public class Character {
  public String name;
  public String otherStuff;
  public boolean facingRight;
  public int id;
  public float x, y;

  public Character() {
  }

  public Character(String name, String otherStuff, int id, float x, float y, boolean facingRight) {
    this.name = name;
    this.otherStuff = otherStuff;
    this.id = id;
    this.x = x;
    this.y = y;
    this.facingRight = facingRight;
  }

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