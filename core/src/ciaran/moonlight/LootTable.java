package ciaran.moonlight;

import java.util.Random;

public class LootTable {
  Random rand = new Random();
  int randomNumber = rand.nextInt(100);



  public int LootRoll(){
    if (randomNumber >= 95){
      return 1;
    }
    else if (randomNumber >= 75){
      return 2;
    }
    else if (randomNumber >= 30){
      return 4;
    }
    else{
      return 5;
    }
  }
}
