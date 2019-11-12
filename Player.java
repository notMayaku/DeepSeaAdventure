import java.util.ArrayList;

public class Player {
    private int point;
    private int depth; //‚Ç‚Ì‚­‚ç‚¢[‚­ö‚Á‚Ä‚¢‚é‚©
    private ArrayList<Integer> treasure = new ArrayList<Integer>(); //è‚É“ü‚ê‚½à•ó
    private int state; //‘Oi‚Å0,Œã‘Ş‚Å1,¶ŠÒ‚Å2,’¾–v‚Å3

    public Player(){
        point = 0;
        depth = 0;
        state = 0;
        treasure.clear();
    }

    public void startRound(){ //roundŠJnˆ—
        depth = 0;
        state = 0;
        treasure.clear();
    }

    public void addTreasure(int PointChipLevel){
        treasure.add(PointChipLevel);
    }

    public void addPoint(int point){
        this.point += point;
    }

    public int getTreasureNum(){
        if(treasure.size() == 0)
            return 0;
        else
            return treasure.size();
    }

    public int getTreasureLevel(int treasureNum){
        return treasure.get(treasureNum);
    }

    public int getState(){
        return state;
    }

    public int getDepth(){
        return depth;
    }

    public int getPoint(){
        return point;
    }
}