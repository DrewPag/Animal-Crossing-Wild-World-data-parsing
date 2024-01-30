package drew.pag.ww_data_parsing.fish;

/**
 * Each  object denotes the spawn weight of a single fish at a single month and time of day
 * 
 * @author drewpag
 */
public class FishSpawnWeight {
    private int fishId;
    private int monthId;
    private int timeOfDayId;
    private int acreId;
    private int spawnWeight;
    
    public FishSpawnWeight(int fishId, int monthId, int timeOfDayId, int acreId, int spawnWeight){
        this.fishId = fishId;
        this.monthId = monthId;
        this.timeOfDayId = timeOfDayId;
        this.acreId = acreId;
        this.spawnWeight = spawnWeight;
    }
    
    public int getFishId(){
        return fishId;
    }
    
    public int getMonthId(){
        return monthId;
    }
    
    public int getTimeOfDayId(){
        return timeOfDayId;
    }
    
    public int getAcreId(){
        return acreId;
    }
    
    public int getSpawnWeight(){
        return spawnWeight;
    }
}
