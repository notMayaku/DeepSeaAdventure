import java.util.ArrayList;

public class Player {
    private int Point;
    private int Depth; //どのくらい深く潜っているか
    private ArrayList<Integer> Treasure = new ArrayList<Integer>(); //手に入れた財宝
    private int State; //前進で0,後退で1,生還で2,沈没で3

    public Player(){
        Point = 0;
        Depth = 0;
        //Treasure.clear();
        Treasure.add(1);
        State = 0;
    }

    public void roundEnd(){ //round終了処理
        Depth = 0;
        Treasure.clear();
    }

    public void addTreasure(int PointChipLevel){
        Treasure.add(PointChipLevel);
    }

    public int getTreasureNum(){
        return Treasure.size();
    }

    public int getState(){
        return State;
    }
}