import java.util.ArrayList;
import java.util.Collections;

class DeepSeaAdventure{
   public static int[] Field = {0,
                                1,1,1,1,1,1,1,1,
                                2,2,2,2,2,2,2,2,
                                3,3,3,3,3,3,3,3,
                                4,4,4,4,4,4,4,4};
   public static int[][] PointChip = {{0,0,1,1,2,2,3,3},
                                      {4,4,5,5,6,6,7,7},
                                      {8,8,9,9,10,10,11,11},
                                      {12,12,13,13,14,14,15,15}};
   public static Player[] PL = new Player[6];
   public static int OxyRest = 25;
   public static int PlayerNum = 6;

   public static void main(String args[]){
      System.out.println("Start");

      pointSet();
      for(int Round = 1; Round <= 3; Round++){
         fieldSet(PlayerNum);
         System.out.println(Round + " Round");
         for(int TurnPlayer = 0; OxyRest > 0; TurnPlayer++,TurnPlayer %= PlayerNum){
            System.out.println("PL" + (TurnPlayer+1) + "のターン  ");
            OxyRest -= PL[TurnPlayer].getTreasureNum();
            System.out.println(OxyRest + " ");
         }
         addPoint(PlayerNum);
      }
      System.out.println("Finish");
   }

   public static void pointSet(){ //ポイントの配列をシャッフル
      ArrayList<Integer> list = new ArrayList<Integer>();
      for(int Level = 0; Level < PointChip.length; Level++){
         list.clear();
         for(int i = 0; i < PointChip[Level].length; i++){
            list.add(PointChip[Level][i]);
         }
         Collections.shuffle(list);
         for(int i = 0; i < 8; i++){
            PointChip[Level][i] = list.get(i);
         }
      }

   }

   public static void fieldSet(int PlayNum){
      for(int i = 0; i < Field.length; i++){
         
      }

      OxyRest = 25;
   }

   public static void addPoint(int PlayNum){

   }
}