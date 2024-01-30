package drew.pag.ww_data_parsing.fish;

import java.util.ArrayList;

/**
 *
 * @author drewpag
 */
public class Fish {
    
    private int fishId;
    private String name;
    private int size;
    private ArrayList<FishSpawnWeight> spawnWeights = new ArrayList<FishSpawnWeight>();
    
    public Fish(int fishId, String name, int size){
        this.fishId = fishId;
        this.name = name;
        this.size = size;
    }
    
    public int getFishId(){
        return fishId;
    }
    
    public String getName(){
        return name;
    }
    
    public int getSize(){
        return size;
    }
    
    public ArrayList<FishSpawnWeight> getSpawnWeights(){
        return spawnWeights;
    }
    
    public void addSpawnWeight(FishSpawnWeight w){
        this.spawnWeights.add(w);
    }
}
