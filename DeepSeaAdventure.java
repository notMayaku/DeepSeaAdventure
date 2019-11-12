import java.util.ArrayList;
import java.util.Collections;

class DeepSeaAdventure{
   public static int[] field = {0,
                                1,1,1,1,1,1,1,1,
                                2,2,2,2,2,2,2,2,
                                3,3,3,3,3,3,3,3,
                                4,4,4,4,4,4,4,4};
   public static int[][] pointChip = {{0,0,1,1,2,2,3,3},
                                      {4,4,5,5,6,6,7,7},
                                      {8,8,9,9,10,10,11,11},
                                      {12,12,13,13,14,14,15,15}};
   public static Player[] PL = new Player[6];
   public static int oxyRest = 25;
   public static int numberOfPL = 6;

   public static void main(String args[]){
      System.out.println("Start");

      preparePL(numberOfPL);
      shufflePoint();

      for(int round = 1; round <= 3; round++){
         setFieldAndPL(numberOfPL);
         System.out.println(round + " Round");
         for(int turnPlayer = 0; oxyRest > 0; turnPlayer++,turnPlayer %= numberOfPL){
            System.out.print("PL" + (turnPlayer+1) + "のターン  ");
            oxyDecrease(turnPlayer); 
         }
         addPoint(numberOfPL);
      }
      resultAnnounce(numberOfPL);
      System.out.println("Finish");
   }

   public static void preparePL(int numberOfPL){
      Player players = new Player(); 
      for(int i = 0; i < numberOfPL; i++){
         PL[i] = players;
      }
   }

   public static void shufflePoint(){ //ポイントの配列をシャッフル
      ArrayList<Integer> list = new ArrayList<Integer>();
      for(int level = 0; level < pointChip.length; level++){
         list.clear();
         for(int i = 0; i < pointChip[level].length; i++){
            list.add(pointChip[level][i]);
         }
         Collections.shuffle(list);
         for(int i = 0; i < 8; i++){
            pointChip[level][i] = list.get(i);
         }
      }
   }

   public static void setFieldAndPL(int numberOfPL){ //盤面を並べ、PLを配置する
      for(int i = 0; i < field.length; i++){
         
      }
      oxyRest = 25;

      for(int num = 0; num < numberOfPL; num++){
         PL[num].startRound();
      }
   }

   public static void oxyDecrease(int turnPlayer){ //酸素を減らす処理
      oxyRest -= PL[turnPlayer].getTreasureNum();
      if(oxyRest > 0){
         System.out.println("残り酸素:" + oxyRest);
      }
      else{
         System.out.println("残り酸素:0");
      }
   }

   public static void addPoint(int numberOfPL){ //ポイントを加算する
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         for(int treasureNum = 0, level = 0; treasureNum < PL[PLnum].getTreasureNum(); treasureNum++){
            level = PL[PLnum].getTreasureLevel(treasureNum);
            for(int i = 0; ; i++){
               if(pointChip[level][i] != -1){
                  PL[PLnum].addPoint(pointChip[level][i]);
                  pointChip[level][i] = -1;
                  break;
               }
            }
         }
      }
   }

   public static void resultAnnounce(int numberOfPL){
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         System.out.print("PL" + (PLnum+1) + "の得点:" + PL[PLnum].getPoint());
      }
   }
}