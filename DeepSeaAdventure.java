import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class DeepSeaAdventure{
   final static String CLR_COMMAND = "\033\143";
   final static int SUBMARINE = 0;
   final static int LEVEL1_CHIP = 2;
   final static int LEVEL2_CHIP = 3;
   final static int LEVEL3_CHIP = 5;
   final static int LEVEL4_CHIP = 7;
   final static int BRANK_CHIP = 1;
   final static int NONE = -1;
   final static int ALIVE  = 2;
   final static int DEAD   = 3;
   final static int DIVE   = 1;
   final static int RETURN = 0;

   public static int[] field = {SUBMARINE,              //0:潜水艦,-1:盤外,各レベルのチップが8個づつ
                                LEVEL1_CHIP,LEVEL1_CHIP,LEVEL1_CHIP,LEVEL1_CHIP,LEVEL1_CHIP,LEVEL1_CHIP,LEVEL1_CHIP,LEVEL1_CHIP,
                                LEVEL2_CHIP,LEVEL2_CHIP,LEVEL2_CHIP,LEVEL2_CHIP,LEVEL2_CHIP,LEVEL2_CHIP,LEVEL2_CHIP,LEVEL2_CHIP,
                                LEVEL3_CHIP,LEVEL3_CHIP,LEVEL3_CHIP,LEVEL3_CHIP,LEVEL3_CHIP,LEVEL3_CHIP,LEVEL3_CHIP,LEVEL3_CHIP,
                                LEVEL4_CHIP,LEVEL4_CHIP,LEVEL4_CHIP,LEVEL4_CHIP,LEVEL4_CHIP,LEVEL4_CHIP,LEVEL4_CHIP,LEVEL4_CHIP,
                                NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE}; 
   public static int[][] pointChip = {{0,0,1,1,2,2,3,3},
                                      {4,4,5,5,6,6,7,7},
                                      {8,8,9,9,10,10,11,11},
                                      {12,12,13,13,14,14,15,15}};
   public static Player[] PL = new Player[6];
   public static int[] playerDepth = {0,0,0,0,0,0}; //PLの現在地
   public static int oxyRest = 25;
   public static int deepestPosition = 32; //チップがある最後尾(field[deepestPosition+1]は必ずNONE)

   public static void main(String args[]){
      int numberOfPL;
      int lastTurnPlayer = 0;
      int diceA;
      int diceB;
      int diceTotal;

      System.out.print(CLR_COMMAND);
      System.out.println("Start");
      numberOfPL = decideNumberOfPlayer();
      preparePL(numberOfPL);
      shufflePoint();
      System.out.print(CLR_COMMAND);

      for(int round = 1; round <= 3; round++){
         setField(numberOfPL, round);
         setPL(numberOfPL);
         System.out.println(round + " Round");
         for(int turnPlayer = lastTurnPlayer, returned = 0; oxyRest > 0 && returned != numberOfPL; turnPlayer++,turnPlayer %= numberOfPL){
            System.out.println("PL" + (turnPlayer+1) + "のターン  ");
            switch(PL[turnPlayer].getState()){
               case DIVE:
               case RETURN:
                  oxyDecrease(turnPlayer);
                  drawField(numberOfPL);
                  if(PL[turnPlayer].getDepth() != SUBMARINE){
                     displayTreasure(turnPlayer);
                     declareDiveOrReturn(turnPlayer);
                  }
                  diceA = diceRoll();
                  diceB = diceRoll();
                  diceTotal = diceA + diceB;
                  System.out.println("サイコロ: " + diceA  + " + " + diceB + " = " + diceTotal);
                  if(diceTotal > PL[turnPlayer].getTreasureNum()){
                     diceTotal -= PL[turnPlayer].getTreasureNum();
                     for(int i = 0; i < numberOfPL; i++){
                        playerDepth[i] = PL[i].getDepth();
                     }
                     PL[turnPlayer].diveSeaOrReturnSubmarine(diceTotal, playerDepth, numberOfPL, deepestPosition);
                     drawField(numberOfPL);
                     switch(field[PL[turnPlayer].getDepth()]){
                        case SUBMARINE:
                           PL[turnPlayer].addState(ALIVE);
                           returned++;
                           System.out.println("PL" + (turnPlayer+1) + "は無事潜水艦に帰還しました");
                           break;
   
                        case NONE:
                           System.out.println("盤外に出ています。強制的に潜水艦に帰還します");
                           PL[turnPlayer].addDepth(SUBMARINE);
                           PL[turnPlayer].addState(ALIVE);
                           break;
   
                        case BRANK_CHIP:
                           System.out.println("ブランクチップの上です");
                           leaveTreasure(turnPlayer);
                           break;
   
                        default:
                           getTreasure(turnPlayer, PL[turnPlayer].getDepth());
                           displayTreasure(turnPlayer);
                     }
                  }
                  else{
                     System.out.println("PL" + (turnPlayer+1) + "は宝が重くて動けませんでした");
                  }
                  break;

               case ALIVE:
                  System.out.println("PL" + (turnPlayer+1) + "は帰還済みです。");
                  break;
               
               default:
                  System.out.println("error");
            }
            lastTurnPlayer = turnPlayer;
            System.out.println("");
         }
         decideDeadOrAlive(numberOfPL);
         endProcess(numberOfPL);
      }
      resultAnnounce(numberOfPL);
      System.out.println("Finish");
   }

   public static int decideNumberOfPlayer(){
      int numberOfPL = 0;
      int inputNumberOfPL;
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isr);
      String str;
      System.out.print("プレイ人数を決めてください（2~6人）");
      while(numberOfPL == 0){
         str = null;
         System.out.println("-> ");
         try{
            str = br.readLine();
            try{
               inputNumberOfPL = Integer.parseInt(str);
            }
            catch(NumberFormatException e){
               inputNumberOfPL = -1;
            }
            switch(inputNumberOfPL){
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
                  numberOfPL = Integer.parseInt(str);
                  break;
               default:
                  System.out.println("入力エラーです。もう一度入力してください");
            }
         }
         catch(IOException e){
            e.printStackTrace();
            break;
         }
      }
      return numberOfPL;
   }

   public static void preparePL(int numberOfPL){
      Player player1 = new Player(); 
      Player player2 = new Player(); 
      Player player3 = new Player(); 
      Player player4 = new Player(); 
      Player player5 = new Player(); 
      Player player6 = new Player(); 
      PL[0] = player1;
      PL[1] = player2;
      PL[2] = player3;
      PL[3] = player4;
      PL[4] = player5;
      PL[5] = player6;
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

   public static void setField(int numberOfPL, int round){ //盤面を並べる
      if(round != 1){ //round1は初期状態で並べなおす必要がない
         for(int position = 1; position <= deepestPosition; position++){ //ブランクチップを整理し盤面を更新する
            while(field[position] == BRANK_CHIP){
               for(int i = position; i <= deepestPosition; i++){
                  field[i] = field[i+1];
               }
               deepestPosition--;
            }
         }
         System.out.println(deepestPosition);
         oxyRest = 25; //酸素を初期状態にする
      }
   }

   public static void setPL(int numberOfPL){
      for(int num = 0; num < numberOfPL; num++){
         PL[num].startRound();
      }
   }

   public static void drawField(int numberOfPL){
      StringBuilder str = new StringBuilder("");
      System.out.print("SUBMARINE【 ");
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         if(PL[PLnum].getDepth() == SUBMARINE){
            System.out.print((PLnum+1) + " ");
         }
      }
      System.out.println("】");

      System.out.print("【】");
      for(int position = 1; position <= deepestPosition; position++){
         str.append("+");
      }
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         if(PL[PLnum].getDepth() != 0){
            str.deleteCharAt(PL[PLnum].getDepth()-1);
            str.insert(PL[PLnum].getDepth()-1, PLnum+1);
         }
      }
      System.out.println(str.toString());

      System.out.print("【】");
      for(int position = 1; position <= deepestPosition; position++){
         switch(field[position]){
            case LEVEL1_CHIP: System.out.print("1"); break;
            case LEVEL2_CHIP: System.out.print("2"); break;
            case LEVEL3_CHIP: System.out.print("3"); break;
            case LEVEL4_CHIP: System.out.print("4"); break;
            case BRANK_CHIP:  System.out.print("0"); break;
            default:
               int level = field[position];
               String overlapChip = "";
               while(level % LEVEL1_CHIP == 0){
                  level /= LEVEL1_CHIP;
                  overlapChip = overlapChip + "1×";
               }
               while(level % LEVEL2_CHIP == 0){
                  level /= LEVEL2_CHIP;
                  overlapChip = overlapChip + "2×";
               }
               while(level % LEVEL3_CHIP == 0){
                  level /= LEVEL3_CHIP;
                  overlapChip = overlapChip + "3×";
               }
               while(level % LEVEL4_CHIP == 0){
                  level /= LEVEL4_CHIP;
                  overlapChip = overlapChip + "4×";
               }
               System.out.print("(" + overlapChip.substring(0, overlapChip.length()-1) + ")");
         }
      }
      System.out.println("");
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

   public static void declareDiveOrReturn(int turnPlayer){
      int inputState;
      if(PL[turnPlayer].getState() == DIVE){
         if(PL[turnPlayer].getDepth() == deepestPosition){
            PL[turnPlayer].addState(RETURN);
         }
         else{
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String str;
            System.out.println("進むか戻るかを宣言してください（進む：1, 戻る：0）");
            while(true){
               str = null;
               System.out.print("-> ");
               try{
                  str = br.readLine();
                  try{
                     inputState = Integer.parseInt(str);
                  }
                  catch(NumberFormatException e){
                     inputState = -1;
                  }
                  if(inputState == RETURN){
                     PL[turnPlayer].addState(RETURN);
                     break;
                  }
                  else if(inputState == DIVE){
                     break;
                  }
                  else{
                     System.out.println("入力エラーです。もう一度入力してください");
                  }
               }
               catch(IOException e){
                  e.printStackTrace();
                  break;
               }

            }
         }
      }
   }

   public static int diceRoll(){
      Random rnd = new Random();
      return rnd.nextInt(3) + 1;
   }

   public static void leaveTreasure(int turnPlayer){
      int selectNum;
      if(PL[turnPlayer].getTreasureNum() > 0){
         InputStreamReader isr = new InputStreamReader(System.in);
         BufferedReader br = new BufferedReader(isr);
         String str;
         displayTreasure(turnPlayer);
         System.out.println("今持っている宝を置いていきますか?（はい：1, いいえ：0）");
         while(true){
            str = null;
            System.out.print("-> ");
            try{
               str = br.readLine();
               try{
                  selectNum = Integer.parseInt(str);
               }
               catch(NumberFormatException e){
                  selectNum = -1;
               }
               if(selectNum == 1){
                  displayTreasure(turnPlayer);
                  selectLeaveTreasure(turnPlayer);
                  break;
               }
               else if(selectNum == 0){
                  break;
               }
               else{
                  System.out.println("入力エラーです。もう一度入力してください");
               }
            }
            catch(IOException e){
               e.printStackTrace();
               break;
            }
         }
      }
   }

   public static void displayTreasure(int PLnum){
      ArrayList<Integer> treasure = PL[PLnum].getTreasurList();
      String str;

      if(PL[PLnum].getTreasureNum() > 0){
         System.out.println("現在所持している宝は以下の通りです。");
         for(int treasureNum = 0, level; treasureNum < treasure.size(); treasureNum++){
            level = treasure.get(treasureNum);
            if(level == LEVEL1_CHIP){
               System.out.print("Level1,");
            }
            else if(level == LEVEL2_CHIP){
               System.out.print("Level2,");
            }
            else if(level == LEVEL3_CHIP){
               System.out.print("Level3,");
            }
            else if(level == LEVEL4_CHIP){
               System.out.print("Level4,");
            }
            else{
               str = "";
               while(level % LEVEL1_CHIP == 0){
                  level /= LEVEL1_CHIP;
                  str = str + "1×";
               }
               while(level % LEVEL2_CHIP == 0){
                  level /= LEVEL2_CHIP;
                  str = str + "2×";
               }
               while(level % LEVEL3_CHIP == 0){
                  level /= LEVEL3_CHIP;
                  str = str + "3×";
               }
               while(level % LEVEL4_CHIP == 0){
                  level /= LEVEL4_CHIP;
                  str = str + "4×";
               }
               System.out.print("Level(" + str.substring(0, str.length()-1) + "),");
            }
         }
         System.out.println("");
      }
   }

   public static void selectLeaveTreasure(int PLnum){
      ArrayList<Integer> treasure = PL[PLnum].getTreasurList();
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isr);
      String str;
      int selectNum;

      System.out.println("置いていく宝を決めてください。");
      for(int treasureNum = 0, level; treasureNum < treasure.size(); treasureNum++){
         level = treasure.get(treasureNum);
         if(level == LEVEL1_CHIP){
            System.out.println("Level1 " + treasureNum + ", ");
         }
         else if(level == LEVEL2_CHIP){
            System.out.println("Level2 " + treasureNum + ", ");
         }
         else if(level == LEVEL3_CHIP){
            System.out.println("Level3 " + treasureNum + ", ");
         }
         else if(level == LEVEL4_CHIP){
            System.out.println("Level4 " + treasureNum + ", ");
         }
         else{
            str = "";
            while(level % LEVEL1_CHIP == 0){
               level /= LEVEL1_CHIP;
               str = str + "1×";
            }
            while(level % LEVEL2_CHIP == 0){
               level /= LEVEL2_CHIP;
               str = str + "2×";
            }
            while(level % LEVEL3_CHIP == 0){
               level /= LEVEL3_CHIP;
               str = str + "3×";
            }
            while(level % LEVEL4_CHIP == 0){
               level /= LEVEL4_CHIP;
               str = str + "4×";
            }
            System.out.println("Level(" + str.substring(0, str.length()-1) + ") " + treasureNum + ", ");
         }
      }
      System.out.println("やめる " + treasure.size() + ",");

      while(true){
         str = null;
         System.out.print("-> ");
         try{
            str = br.readLine();
            try{
               selectNum = Integer.parseInt(str);
            }
            catch(NumberFormatException e){
               selectNum = -1;
            }
            if(selectNum == treasure.size()){
               break;
            }
            else if(0 <= selectNum && selectNum < treasure.size()){
               field[PL[PLnum].getDepth()] = treasure.get(selectNum);
               treasure.remove(selectNum);
               PL[PLnum].addTreasureList(treasure);
               displayTreasure(PLnum);
               break;
            }
            else{
               System.out.println("入力エラーです。もう一度入力してください");
            }
         }
         catch(IOException e){
            e.printStackTrace();
            break;
         }
      }
   }

   public static void getTreasure(int turnPlayer, int depth){
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isr);
      String str;
      int selectNum;

      if(PL[turnPlayer].getTreasureNum() != 6){
         switch(field[depth]){
            case LEVEL1_CHIP: System.out.println("LEVEL1の宝を見つけました"); break;
            case LEVEL2_CHIP: System.out.println("LEVEL2の宝を見つけました"); break;
            case LEVEL3_CHIP: System.out.println("LEVEL3の宝を見つけました"); break;
            case LEVEL4_CHIP: System.out.println("LEVEL4の宝を見つけました"); break;
            default:
               int level = field[depth];
               str = "";
               while(level % LEVEL1_CHIP == 0){
                  level /= LEVEL1_CHIP;
                  str = str + "1×";
               }
               while(level % LEVEL2_CHIP == 0){
                  level /= LEVEL2_CHIP;
                  str = str + "2×";
               }
               while(level % LEVEL3_CHIP == 0){
                  level /= LEVEL3_CHIP;
                  str = str + "3×";
               }
               while(level % LEVEL4_CHIP == 0){
                  level /= LEVEL4_CHIP;
                  str = str + "4×";
               }
               System.out.println("Level(" + str.substring(0, str.length()-1) + ")の宝を見つけました");
         }
         System.out.println("獲得しますか?（yes 1, no 0）");
         while(true){
            str = null;
            System.out.print("-> ");
            try{
               str = br.readLine();
               try{
                  selectNum = Integer.parseInt(str);
               }
               catch(NumberFormatException e){
                  selectNum = -1;
               }
               if(selectNum == 1){
                  PL[turnPlayer].addTreasure(field[depth]);
                  field[depth] = BRANK_CHIP;
                  break;
               }
               else if(selectNum == 0){
                  break;
               }
               else{
                  System.out.println("入力エラーです。もう一度入力してください");
               }
            }
            catch(IOException e){
               e.printStackTrace();
               break;
            }
         }
      }
   }

   public static void decideDeadOrAlive(int numberOfPL){
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         if(PL[PLnum].getDepth() != SUBMARINE){
            PL[PLnum].addState(DEAD);
         }
      }
   }

   public static void endProcess(int numberOfPL){ //ALIVE:ポイントを加算する, DEAD:宝を沈める
      ArrayList<Integer> sinkTreasureList = new ArrayList<Integer>();
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         switch(PL[PLnum].getState()){
            case ALIVE:
               String str = "(";
               int roundTotal = 0;
               ArrayList<Integer> levelList = new ArrayList<Integer>();
               for(int treasureNum = 0, level = 0; treasureNum < PL[PLnum].getTreasureNum(); treasureNum++){
                  level = PL[PLnum].getTreasureLevel(treasureNum);
                  while(level != 1){
                     if(level % LEVEL1_CHIP == 0){
                         level /= LEVEL1_CHIP;
                         levelList.add(LEVEL1_CHIP);
                     }
                     if(level % LEVEL2_CHIP == 0){
                         level /= LEVEL2_CHIP;
                         levelList.add(LEVEL2_CHIP);
                     }
                     if(level % LEVEL3_CHIP == 0){
                         level /= LEVEL3_CHIP;
                         levelList.add(LEVEL3_CHIP);
                     }
                     if(level % LEVEL4_CHIP == 0){
                         level /= LEVEL4_CHIP;
                         levelList.add(LEVEL4_CHIP);
                     }
                  }
               }
               for(int i = 0, pointChipListLevel = 0; i < levelList.size(); i++){
                  switch(levelList.get(i)){
                     case LEVEL1_CHIP: pointChipListLevel  = 0; break;
                     case LEVEL2_CHIP: pointChipListLevel  = 1; break;
                     case LEVEL3_CHIP: pointChipListLevel  = 2; break;
                     case LEVEL4_CHIP: pointChipListLevel  = 3; break;
                     default: System.out.println("pointChipListLevel error");
                  }
                  for(int j = 0; ;j++){
                     if(pointChip[pointChipListLevel][j] != NONE){
                        PL[PLnum].addPoint(pointChip[pointChipListLevel][j]);
                        roundTotal += pointChip[pointChipListLevel][j];
                        str = str + pointChip[pointChipListLevel][j] + ",";
                        pointChip[pointChipListLevel][j] = NONE;
                        break;
                     }
                  }
               }
               str = str + ")";
               System.out.print("PL"+ (PLnum+1) +"は生還しました。このラウンドの獲得Pは" + roundTotal);
               System.out.println(str + ", 現在のトータルPは" + PL[PLnum].getPoint() + "です");
               break;

            case DEAD: //沈む宝を集める
               System.out.println("PL"+ (PLnum+1) +"は生還できませんでした。獲得した宝は深海に沈みます");
               for(int treasureNum = 0; treasureNum < PL[PLnum].getTreasureNum(); treasureNum++){
                  sinkTreasureList.add(PL[PLnum].getTreasureLevel(treasureNum));
               }
               break;

            default:
               System.out.println("endProcess error");
         }
      }
      if(sinkTreasureList.size() != 0){
         while(sinkTreasureList.size() % 3 != 0){ //sinkTreasureList.size()を3で割り切れるよう調整する
            sinkTreasureList.add(1);
         }
         for(int i = sinkTreasureList.size()-1; i >= 0; i = i - 3){
            deepestPosition++;
            field[deepestPosition] = sinkTreasureList.get(i) * sinkTreasureList.get(i-1) * sinkTreasureList.get(i-2);
         }
      }
      System.out.println("");
   }

   public static void resultAnnounce(int numberOfPL){
      for(int PLnum = 0; PLnum < numberOfPL; PLnum++){
         System.out.print("PL" + (PLnum+1) + "の得点:" + PL[PLnum].getPoint() + ", ");
      }
      System.out.println("");
   }
}