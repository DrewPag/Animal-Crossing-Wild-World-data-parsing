package drew.pag.ww_data_parsing;

import drew.pag.ww_data_parsing.fish.Fish;
import drew.pag.ww_data_parsing.fish.FishSpawnWeight;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author drewpag
 */
public class Main {
    
    final static String[] bugs = new String[]{"Common Butterfly", "Yellow Butterfly", "Tiger Butterfly", "Peacock Butterfly",
        "Monarch Butterfly", "Emperor Butterfly", "Agrias Butterfly", "Raja Brooke", "Birdwing", "Moth", "Oak Silk Moth", "Honeybee",
        "Bee", "Long Locust", "Migratory Locust", "Mantis", "Orchid Mantis", "Brown Cicada", "Robust Cicada", "Walker Cicada",
        "Evening Cicada", "Lantern Fly", "Red Dragonfly", "Darner Dragonfly", "Banded Dragonfly", "Giant Petaltail", "Ant",
        "Pondskater", "Diving Beetle", "Snail", "Cricket", "Bell Cricket", "Grasshopper", "Mole Cricket", "Walking Leaf",
        "Walkingstick", "Bagworm", "Ladybug", "Violin Beetle", "Longhorn Beetle", "Dung Beetle", "Firefly", "Fruit Beetle",
        "Scarab Beetle", "Jewel Beetle", "Miyama Stag", "Saw Stag", "Giant Stag", "Rainbow Stag", "Cyclommatus", "Golden Stag",
        "Dynastid Beetle", "Atlas Beetle", "Elephant Beetle", "Hercules Beetle", "Goliath Beetle", "Flea", "Pill Bug", "Mosquito",
        "Fly", "Centipede", "Spider", "Tarantula", "Scorpion"};
    
    // fins size 7, eel size 8 for distinguishing purposes
    final static Fish[] fish = new Fish[]{
        new Fish(0, "Bitterling", 1),
        new Fish(1, "Pale Chub", 1),
        new Fish(2, "Crucian Carp", 2),
        new Fish(3, "Dace", 2),
        new Fish(4, "Barbel Steed", 3),
        new Fish(5, "Carp", 4),
        new Fish(6, "Koi", 4),
        new Fish(7, "Goldfish", 1),
        new Fish(8, "Popeyed Goldfish", 1),
        new Fish(9, "Killifish", 1),
        new Fish(10, "Crawfish", 1),
        new Fish(11, "Frog", 1),
        new Fish(12, "Freshwater Goby", 1),
        new Fish(13, "Loach", 1),
        new Fish(14, "Catfish", 3),
        new Fish(15, "Eel", 8),
        new Fish(16, "Giant Snakehead", 5),
        new Fish(17, "Bluegill", 2),
        new Fish(18, "Yellow Perch", 2),
        new Fish(19, "Black Bass", 3),
        new Fish(20, "Pond Smelt", 1),
        new Fish(21, "Sweetfish", 2),
        new Fish(22, "Cherry Salmon", 2),
        new Fish(23, "Char", 3),
        new Fish(24, "Rainbow Trout", 4),
        new Fish(25, "Stringfish", 6),
        new Fish(26, "Salmon", 5),
        new Fish(27, "King Salmon", 6),
        new Fish(28, "Guppy", 1),
        new Fish(29, "Angelfish", 1),
        new Fish(30, "Piranha", 2),
        new Fish(31, "Arowana", 4),
        new Fish(32, "Dorado", 5), 
        new Fish(33, "Gar", 6), 
        new Fish(34, "Arapaima", 6),
        new Fish(35, "Sea Butterfly", 1),
        new Fish(36, "Jellyfish", 2),
        new Fish(37, "Seahorse", 1), 
        new Fish(38, "Clownfish", 1),
        new Fish(39, "Zebra Turkeyfish", 2), 
        new Fish(40, "Puffer Fish", 2),
        new Fish(41, "Horse Mackerel", 2),
        new Fish(42, "Barred Knifejaw", 3),
        new Fish(43, "Sea Bass", 5),
        new Fish(44, "Red Snapper", 4),
        new Fish(45, "Dab", 3),
        new Fish(46, "Olive Flounder", 4),
        new Fish(47, "Squid", 2),
        new Fish(48, "Octopus", 3),
        new Fish(49, "Football Fish", 3),
        new Fish(50, "Tuna", 6),
        new Fish(51, "Blue Marlin", 6),
        new Fish(52, "Ocean Sunfish", 7),
        new Fish(53, "Hammerhead Shark", 7),
        new Fish(54, "Shark", 7),
        new Fish(55, "Coelacanth", 6),
        new Fish(56, "Can", 2), 
        new Fish(57, "Boot", 3), 
        new Fish(58, "Tire", 4)
    };
    
    // the index of each time range corresponds to its internal ID
    final static String[] bugTimes = new String[]{"11PM - 4AM", "4AM - 8AM", "8AM - 4PM", "4PM - 5PM", "5PM - 7PM", "7PM - 11PM"};
    final static String[] fishTimes = new String[]{"4AM - 9AM, 4PM - 9PM", "9AM - 4PM", "9PM - 4AM"};
    
    // In Ghidra export, there is no 0x02 at the beginning... otherwise it lines up exactly
    final static int fishTopLevelAddr = 0xdb254;
    final static int addrOffset = 0x2000000;
    
    final static String[] fishMonthNames = new String[] {"January", "February", "March", "April", "May", "June", "July", "August (1-15)",
                                                        "August (16-31)", "September (1-15)", "September (16-30)", "October", "November",
                                                        "December"};
    
    static String[] fishAcreIds = new String[] {"River", "Lake", "Waterfall", "Pond", "River Mouth", "Ocean (rain/snow)", "Ocean"};
    
    static Map<Integer, Map<Integer, List<FishSpawnWeight>>> riverFishSpawnWeightMap = new HashMap<>();
    static Map<Integer, Map<Integer, List<FishSpawnWeight>>> oceanFishSpawnWeightMap = new HashMap<>();
    
    static String[][] fishSpawnWeightStringArray = new String[60][42];
    static String[][] fishShadowBasedStringArray = new String[60][42];
    static Double[][] fishSpawnWeightDoubleArray = new Double[60][42];
    static Double[][] fishShadowBasedDoubleArray = new Double[60][42];
    
    static String binPathStr = "C:/Users/drewp/ww.bin";
    static String fishPercentagesStringCsv = "C:/Users/drewp/Desktop/ww_fish_percentages_string.csv";
    static String fishShadowPercentagesStringCsv = "C:/Users/drewp/Desktop/ww_fish_shadow_percentages_string.csv";
    static String fishPercentagesDoubleCsv = "C:/Users/drewp/Desktop/ww_fish_percentages_double.csv";
    static String fishShadowPercentagesDoubleCsv = "C:/Users/drewp/Desktop/ww_fish_shadow_percentages_double.csv";

    public static void main(String[] args) {
        
        for(int i=0; i < 60; i++){
            for(int j = 0; j < 42; j++){
                fishSpawnWeightStringArray[i][j] = "-";
                fishShadowBasedStringArray[i][j] = "-";
                fishSpawnWeightDoubleArray[i][j] = 0.0;
                fishShadowBasedDoubleArray[i][j] = 0.0;
            }
        }
        
        // display the pane, and get the file path + options (?) back
//        String dolPathStr = LoadDolDialog.display();
        
        if(binPathStr.equals("")){
            // user canceled... gg
            System.exit(0);
        }
        
        // lol
//        boolean parseBugs = false;
//        String result = parseBugData(dolPathStr);

        boolean isOcean = true;

        String result = parseFishData(binPathStr);
//        System.out.println(result);
        
        // process all of the fish spawn weights
        String riverSpawnWeights = processRiverFishSpawnWeights();
        String oceanSpawnWeights = processOceanFishSpawnWeights();
        System.out.println(riverSpawnWeights);
        System.out.println(oceanSpawnWeights);
        
        // write %s and shadow-based% to .csv files
        writeFishToCsv();
    }
    
//    private static String parseBugData(String dolPathStr){
//        StringBuilder result = new StringBuilder();
//        
//        // parse main.dol!
//        try{
//            Path dolPath = Paths.get(dolPathStr);
//            
//            byte[] data = null;
//            try {
//                data = Files.readAllBytes(dolPath);
//            } catch (IOException ex) {
//                System.out.println("Exception while reading main.dol byte array:\n" + ex);
//                System.exit(0);
//            }
//            
//            ByteBuffer bb = ByteBuffer.wrap(data);
//            // BIG
//            bb.order(ByteOrder.BIG_ENDIAN);
//            
//            // month counter
//            int monthId = 0;
//            
//            for(MonthPair m: bugAddrs){
//                StringBuilder sb = new StringBuilder();
//                sb.append("\n").append(m.getMonth()).append(":\n");
//                int startOffset = Integer.parseInt(String.valueOf(m.getAddress()), 16);
//                int endOffset = (monthId < 11) ? Integer.parseInt(bugAddrs[monthId+1].getAddress(), 16) : Integer.parseInt("4E9350", 16);
//                int lastId = 999;
//                int lastSpawnRange = 0;
//                int timeOfDayId = -1;
//                while(startOffset < endOffset){
//                    
//                    // no unsigned ints... this is necessary to read an int from 2 bytes
//                    byte[] bytes = new byte[4];
//                    bb.get(startOffset, bytes, 0, 4);
//                  
//                    // first two bytes are the bug ID
//                    int bugId = ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
//                    
//                    // second two bytes are the spawn range upper value
//                    int upperSpawnRange = ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
//                    
//                    if(bugId <= lastId){
//                        sb.append("\n");
//                        lastId = -1;
//                        lastSpawnRange = 0;
//                        timeOfDayId++;
//                        if(timeOfDayId < 6){
//                            sb.append(bugTimes[timeOfDayId]).append("\n");
//                        }
//                    }
//                    
//                    String bugName = bugs[bugId];
//                    int spawnWeight = upperSpawnRange - lastSpawnRange;
//
//                    // sometimes, there are 4 bytes of 0x0 that this is picking up as common butterfly... so ignore anything with weight 0
//                    if(spawnWeight != 0){
//                        sb.append(String.format("%1$18s", bugName)).append("\t").append(spawnWeight).append("\n");
//                    }
//                    
//                    lastId = bugId;
//                    lastSpawnRange = upperSpawnRange;
//                    
//                    startOffset += 4;
//                }
//                
////                System.out.println(sb);
//                result.append(sb);
//                
//                monthId++;
//            }
//            
//        } catch(Exception ex){
//            System.out.println("Exception " + ex);
//        }
//        
//        return result.toString();
//    }
    
    private static String parseFishData(String dolPathStr){
        
        StringBuilder result = new StringBuilder();
        
        try{
            Path dolPath = Paths.get(dolPathStr);
            
            byte[] data = null;
            try {
                data = Files.readAllBytes(dolPath);
            } catch (IOException ex) {
                System.out.println("Exception while reading ww.bin byte array:\n" + ex);
                System.exit(0);
            }
            
            ByteBuffer bb = ByteBuffer.wrap(data);
            // little
            bb.order(ByteOrder.LITTLE_ENDIAN);
            
            for(int monthId = 0; monthId < 14; monthId ++){
                StringBuilder sb = new StringBuilder();
                sb.append("\n").append(fishMonthNames[monthId]).append(":\n");
                
                // maps containing the 3 spawn weight lists for each time of day
                // one for river, one for ocean
                Map<Integer, List<FishSpawnWeight>> riverMonthlySpawnWeightsMap = new HashMap<>();
                Map<Integer, List<FishSpawnWeight>> oceanMonthlySpawnWeightsMap = new HashMap<>();
                
                ArrayList<FishSpawnWeight> riverEveningWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> riverDayWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> riverNightWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> oceanEveningWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> oceanDayWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> oceanNightWeights = new ArrayList<>();
                
                riverMonthlySpawnWeightsMap.put(0, riverEveningWeights);
                riverMonthlySpawnWeightsMap.put(1, riverDayWeights);
                riverMonthlySpawnWeightsMap.put(2, riverNightWeights);
                oceanMonthlySpawnWeightsMap.put(0, oceanEveningWeights);
                oceanMonthlySpawnWeightsMap.put(1, oceanDayWeights);
                oceanMonthlySpawnWeightsMap.put(2, oceanNightWeights);
                
                // Level 1: get the PTR PTR for this month
                long monthPtrPtr = ((long) bb.getInt(fishTopLevelAddr + (4*monthId)) & 0xffffffffL) - addrOffset;
//                System.out.println(fishMonthNames[monthId] + " monthPtrPtr " + Integer.toHexString((int) monthPtrPtr));
                
                // Level 2: 3 time of day pointers for this month
                for(int timeOfDayId = 0; timeOfDayId < 3; timeOfDayId++){
                    
                    sb.append("\n").append(fishTimes[timeOfDayId]).append("\n");
                    
                    long timeOfDayPtr = ((long) bb.getInt(((int) monthPtrPtr) + (timeOfDayId*4))) - addrOffset;
//                    System.out.println("For time of day ID " + timeOfDayId + " got timeOfDayPtr " + Integer.toHexString((int)timeOfDayPtr));
                    
                    // Level 3: river + ocean pointers
                    for(int isOcean = 0; isOcean < 2; isOcean++){
                        
                        sb.append((isOcean == 0) ? "River" : "Ocean").append("\n");
                        
                        // Level 4: Parse the fish spawn data
                        // Note: the lowest level pointers have 4 bytes of [something] in between them - hence *8
                        long fishDataAddr = ((long) bb.getInt(((int) timeOfDayPtr) + (isOcean*8))) - addrOffset;
//                        System.out.println("For isOcean " + isOcean + " got fishDataAddr " + Integer.toHexString((int)fishDataAddr));
                        
                        int fishId = -1;
                        int fishIndex = 0;
                        // conveniently, ID 0x3a (tire) is always the final ID in the spawn range.
                        while(fishId < 0x3a){
                            
                            byte[] fishBytes = new byte[3];
                            bb.get((int) (fishDataAddr + (fishIndex * 3)), fishBytes, 0, 3);
                            
                            // first byte is the fish ID
                            fishId = Byte.toUnsignedInt(fishBytes[0]);

                            // second byte is the acre type (?)
                            int acreId = Byte.toUnsignedInt(fishBytes[1]);

                            // final byte is the spawn weight!
                            int spawnWeight = Byte.toUnsignedInt(fishBytes[2]);
                            
                            String fishName = fish[fishId].getName();
                            
                            // add the spawn weight for this month and time of day to this fish's list
                            FishSpawnWeight weight = new FishSpawnWeight(fishId, monthId, timeOfDayId, acreId, spawnWeight);

                            // add this spawn weight to the specific fish (not currently used)
                            fish[fishId].addSpawnWeight(weight);

                            // add this spawn weight to the corresponding spawn weight list
                            if(isOcean == 0){
                                riverMonthlySpawnWeightsMap.get(timeOfDayId).add(weight);
                            } else{
                                oceanMonthlySpawnWeightsMap.get(timeOfDayId).add(weight);
                            }
                            
                            sb.append(String.format("%1$18s", fishName)).append("\t").append(spawnWeight);
                            if(( (isOcean == 0) && acreId != 0) || ((isOcean == 1) && acreId != 6)){
                                sb.append("\t").append(fishAcreIds[acreId]);
                            }
                            sb.append("\n");
                            
                            fishIndex++;
                        }
                    }
                }
                
                result.append(sb);
                
                // add the spawn weight maps for this month to the master maps
                riverFishSpawnWeightMap.put(monthId, riverMonthlySpawnWeightsMap);
                oceanFishSpawnWeightMap.put(monthId, oceanMonthlySpawnWeightsMap);
            }
            
//            System.out.println(result);
            
            // TODO process fish and ocean spawn weights somewhere
            
            return result.toString();
            
        } catch(Exception ex){
            System.out.println("Exception " + ex);
            ex.printStackTrace();
            
            return result.toString();
        }
    }
    
    private static String processRiverFishSpawnWeights(){
        
        StringBuilder sb = new StringBuilder();
        
        for(int monthId = 0; monthId < 14; monthId++){
            
            sb.append("\n").append(fishMonthNames[monthId]).append(":\n");
            
            Map<Integer, List<FishSpawnWeight>> monthMap = riverFishSpawnWeightMap.get(monthId);
            
            for(int timeOfDayId = 0; timeOfDayId < 3; timeOfDayId++){
                
                sb.append("\n").append(String.format("%-20s", fishTimes[timeOfDayId]))
                        .append("\tBase %\t\tShadow Based %\n");                
                
                ArrayList<FishSpawnWeight> weights = (ArrayList) monthMap.get(timeOfDayId);
                
                // first, get the total spawn weights
                double totalRiverSpawnWeight = 0;
                double riverSpawnWeight = 0;
                double lakeSpawnWeight = 0;
                double waterfallSpawnWeight = 0;
                double pondSpawnWeight = 0;
                
                // also the shadow-based spawn weights
                double SSRiverSpawnWeight = 0;
                double SSLakeSpawnWeight = 0;
                double SSPondSpawnWeight = 0;
                double SRiverSpawnWeight = 0;
                double MRiverSpawnWeight = 0;
                double MLakeSpawnWeight = 0;
                double MWaterfallSpawnWeight = 0;
                double LRiverSpawnWeight = 0;
                double LLRiverSpawnWeight = 0;
                double LLLakeSpawnWeight = 0;
                double LLLRiverSpawnWeight = 0;
                double LLLLakeSpawnWeight = 0;
                
                for(FishSpawnWeight fsw: weights){
                    int w = fsw.getSpawnWeight();
//                    System.out.println("weight " + w + " for fish ID " + fsw.getFishId());
                    
                    switch(fsw.getAcreId()){
                        
                        // river
                        case 0:
                            riverSpawnWeight += w;
                            break;
                        
                        // Lake
                        case 1:
                            lakeSpawnWeight += w;
                            break;
                            
                        // Waterfall
                        case 2:
                            waterfallSpawnWeight += w;
                            break;
                            
                        // Pond
                        case 3:
                            pondSpawnWeight += w;
                            break;
                    }
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                        switch (fsw.getAcreId()) {
                            case 0:
                            default:
                                SSRiverSpawnWeight += w;
                                break;
                            case 1:
                                SSLakeSpawnWeight += w;
                                break;
                            case 3:
                                SSPondSpawnWeight += w;
                                break;
                            
                            }
                            break;
                            
                        // Small (S)
                        case 2:
                            SRiverSpawnWeight += w;
                            break;
                            
                        // Medium (M)
                        case 3:
                        switch (fsw.getAcreId()) {
                            case 0:
                            default:
                                MRiverSpawnWeight += w;
                                break;
                            case 1:
                                MLakeSpawnWeight += w;
                                break;
                            case 2:
                                MWaterfallSpawnWeight += w;
                                break;
                            }
                            break;
                            
                        // Large (L)
                        case 4:
                            LRiverSpawnWeight += w;
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            if(fsw.getAcreId() == 0){
                                LLRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 1){
                                LLLakeSpawnWeight += w;
                            }
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 0){
                                LLLRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 1){
                                LLLLakeSpawnWeight += w;
                            }
                            break;
                    }
                }
                
                // seems like all spawn weights matter when determining river fish... so why did I separate them...
                totalRiverSpawnWeight = riverSpawnWeight + lakeSpawnWeight + waterfallSpawnWeight + pondSpawnWeight;
//                System.out.println("totalRiverSpawnWeight: " + totalRiverSpawnWeight);
                
                // then, calculate the percentage for each individual fish
                for(FishSpawnWeight fsw: weights){
                    sb.append(String.format("%1$20s", fish[fsw.getFishId()].getName())).append("\t")
                            .append(String.format("%.1f", (100.0 * (fsw.getSpawnWeight() / totalRiverSpawnWeight))));
//                            .append("%");
                    
                    // handle the shadow-based %
                    double totalWeightToUse = 0;
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                        switch (fsw.getAcreId()) {
                            case 0:
                            default:
                                totalWeightToUse = SSRiverSpawnWeight;
                                break;
                            case 1:
                                totalWeightToUse = SSRiverSpawnWeight + SSLakeSpawnWeight;
                                break;
                            case 3:
                                totalWeightToUse = SSPondSpawnWeight;
                                break;
                            }
                            break;

                            
                        // Small (S)
                        case 2:
                            totalWeightToUse = SRiverSpawnWeight;
                            break;
                            
                        // Medium (M)
                        case 3:
                        switch (fsw.getAcreId()) {
                            case 0:
                            default:
                                totalWeightToUse = MRiverSpawnWeight;
                                break;
                            case 1:
                                totalWeightToUse = MRiverSpawnWeight + MLakeSpawnWeight;
                                break;
                            case 2:
                                totalWeightToUse = MRiverSpawnWeight + MWaterfallSpawnWeight;
                                break;
                        }
                            break;

                            
                        // Large (L)
                        case 4:
                            totalWeightToUse = LRiverSpawnWeight;
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = LLRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 1){
                                totalWeightToUse = LLRiverSpawnWeight + LLLakeSpawnWeight;
                            }
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = LLLRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 1){
                                totalWeightToUse = LLLRiverSpawnWeight + LLLLakeSpawnWeight;
                            }
                            break;
                            
                        // Eel (lol)
                        case 8:
                            totalWeightToUse = fsw.getSpawnWeight();
                            break;
                    }
                    
                    double shadowBasedPercent = (100.0 * (fsw.getSpawnWeight() / totalWeightToUse));
                    
                    sb.append("\t\t").append(String.format("%.2f", shadowBasedPercent));
                            
                    if(fsw.getAcreId() != 0){
                        sb.append("\t").append(fishAcreIds[fsw.getAcreId()]);
                    }
                    sb.append("\n");
                    
                    // add the fish spawn weight and shadow based % to the appropriate entry in the 2d arrays
                    int colIndex = (monthId * 3) + timeOfDayId;
                    
                    fishSpawnWeightStringArray[fsw.getFishId()][colIndex] = String.format("%.1f", 1.0*fsw.getSpawnWeight())+"%";
                    fishShadowBasedStringArray[fsw.getFishId()][colIndex] = String.format("%.2f", shadowBasedPercent)+"%";
                    
                    fishSpawnWeightDoubleArray[fsw.getFishId()][colIndex] = 1.0*fsw.getSpawnWeight();
                    fishShadowBasedDoubleArray[fsw.getFishId()][colIndex] = shadowBasedPercent;
                }
            }
        }
        
        return sb.toString();
    }
    
    private static String processOceanFishSpawnWeights(){
        
        StringBuilder sb = new StringBuilder();
        
        for(int monthId = 0; monthId < 14; monthId++){
            
            sb.append("\n").append(fishMonthNames[monthId]).append(":\n");
            
            Map<Integer, List<FishSpawnWeight>> monthMap = oceanFishSpawnWeightMap.get(monthId);
            
            for(int timeOfDayId = 0; timeOfDayId < 3; timeOfDayId++){
                
                sb.append("\n").append(String.format("%-20s", fishTimes[timeOfDayId]))
                        .append("\tBase %\t\tShadow Based %\n");                
                
                ArrayList<FishSpawnWeight> weights = (ArrayList) monthMap.get(timeOfDayId);
                
                // first, get the total spawn weights
                double totalOceanSpawnWeight = 0;
                double oceanSpawnWeight = 0;
                double riverMouthSpawnWeight = 0;
                
                // also the shadow-based spawn weights
                double SSSpawnWeight = 0;
                double SSpawnWeight = 0;
                double MSpawnWeight = 0;
                double LOceanSpawnWeight = 0;
                double LRiverMouthSpawnWeight = 0;
                double LLSpawnWeight = 0;
                double LLLOceanSpawnWeight = 0;
                double LLLRiverMouthSpawnWeight = 0;
                double finSpawnWeight = 0;
                
                for(FishSpawnWeight fsw: weights){
                    int w = fsw.getSpawnWeight();
//                    System.out.println("weight " + w + " for fish ID " + fsw.getFishId());
                    
                    switch(fsw.getAcreId()){
                        
                        // River Mouth
                        case 4:
                            riverMouthSpawnWeight += w;
                            break;
                        
                        // Ocean
                        case 6:
                            oceanSpawnWeight += w;
                            break;
                    }
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                            SSSpawnWeight += w;
                            break;
                            
                        // Small (S)
                        case 2:
                            SSpawnWeight += w;
                            break;
                            
                        // Medium (M)
                        case 3:
                            MSpawnWeight += w;
                            break;
                            
                        // Large (L)
                        case 4:
                            if(fsw.getAcreId() == 4){
                                LRiverMouthSpawnWeight += w;
                            } else{
                                LOceanSpawnWeight += w;
                            }
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            LLSpawnWeight += w;
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 4){
                                LLLRiverMouthSpawnWeight += w;
                            } else{
                                LLLOceanSpawnWeight += w;
                            }
                            break;
                            
                        // Sharks (finned)
                        case 7:
                            finSpawnWeight += w;
                            break;
                    }
                }
                
                // combine the spawn weights
                totalOceanSpawnWeight = oceanSpawnWeight + riverMouthSpawnWeight;
                System.out.println("totalOceanSpawnWeight: " + totalOceanSpawnWeight);
                
                // then, calculate the percentage for each individual fish
                for(FishSpawnWeight fsw: weights){
                    sb.append(String.format("%1$20s", fish[fsw.getFishId()].getName())).append("\t")
                            .append(String.format("%.1f", (1.0 * fsw.getSpawnWeight())));
                    // in WW, the total spawn weight does not always add up to 100 - but the game can roll a "no spawn".
//                            .append(String.format("%.1f", (100.0 * (fsw.getSpawnWeight() / totalOceanSpawnWeight))));
//                            .append("%");
                    
                    // handle the shadow-based %
                    double totalWeightToUse = 0;
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                            totalWeightToUse = SSSpawnWeight;
                            break;
                            
                        // Small (S)
                        case 2:
                            totalWeightToUse = SSpawnWeight;
                            break;
                            
                        // Medium (M)
                        case 3:
                            totalWeightToUse = MSpawnWeight;
                            break;
                            
                        // Large (L)
                        case 4:
                            if(fsw.getAcreId() == 4){
                                totalWeightToUse = LRiverMouthSpawnWeight + LOceanSpawnWeight;
                            } else{
                                totalWeightToUse = LOceanSpawnWeight;
                            }
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            totalWeightToUse = LLSpawnWeight;
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 4){
                                totalWeightToUse = LLLRiverMouthSpawnWeight + LLLOceanSpawnWeight;
                            } else{
                                totalWeightToUse = LLLOceanSpawnWeight;
                            }
                            break;
                            
                        // Sharks (finned)
                        case 7:
                            totalWeightToUse = finSpawnWeight;
                            break;
                    }
                    
                    double shadowBasedPercent = (100.0 * (fsw.getSpawnWeight() / totalWeightToUse));
                    
                    sb.append("\t\t").append(String.format("%.2f", shadowBasedPercent));
                            
                    if(fsw.getAcreId() == 4){
                        sb.append("\t").append(fishAcreIds[fsw.getAcreId()]);
                    } else if(fsw.getAcreId() == 5){
                        sb.append("\t").append("Rain/Snow");
                    }
                    sb.append("\n");
                    
                    // add the fish spawn weight and shadow based % to the appropriate entry in the 2d arrays
                    int colIndex = (monthId * 3) + timeOfDayId;
                    
                    fishSpawnWeightStringArray[fsw.getFishId()][colIndex] = String.format("%.1f", 1.0*fsw.getSpawnWeight())+"%";
                    fishShadowBasedStringArray[fsw.getFishId()][colIndex] = String.format("%.2f", shadowBasedPercent)+"%";
                    
                    fishSpawnWeightDoubleArray[fsw.getFishId()][colIndex] = 1.0*fsw.getSpawnWeight();
                    fishShadowBasedDoubleArray[fsw.getFishId()][colIndex] = shadowBasedPercent;
                }
            }
        }
        
        return sb.toString();
    }
    
    static class MonthPair{
        String month;
        String address;
        
        public MonthPair(String m, String addr){
            this.month = m;
            this.address = addr;
        }
        
        public String getMonth(){
            return month;
        }
        
        public String getAddress(){
            return address;
        }
    }
    
    private static void writeFishToCsv(){
        
        // Regular percentages
        // string
        try (PrintWriter pw = new PrintWriter(fishPercentagesStringCsv)) {
            for(int fishIndex = 0; fishIndex < 60; fishIndex++){
                String csvLine = getCsvLine(fishSpawnWeightStringArray[fishIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception printing fish percentages");
            ex.printStackTrace();
        }
        
        // double
        try (PrintWriter pw = new PrintWriter(fishPercentagesDoubleCsv)) {
            for(int fishIndex = 0; fishIndex < 60; fishIndex++){
                String csvLine = getCsvLineFromDoubles(fishSpawnWeightDoubleArray[fishIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception printing fish percentages");
            ex.printStackTrace();
        }
        
        // Shadow-based percentages
        // string
        try (PrintWriter pw = new PrintWriter(fishShadowPercentagesStringCsv)) {
            for(int fishIndex = 0; fishIndex < 60; fishIndex++){
                String csvLine = getCsvLine(fishShadowBasedStringArray[fishIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception printing fish shadow-based percentages");
            ex.printStackTrace();
        }
        
        // double
        try (PrintWriter pw = new PrintWriter(fishShadowPercentagesDoubleCsv)) {
            for(int fishIndex = 0; fishIndex < 60; fishIndex++){
                String csvLine = getCsvLineFromDoubles(fishShadowBasedDoubleArray[fishIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception printing fish percentages");
            ex.printStackTrace();
        }
    }
    
    private static String getCsvLine(String[] data){
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }
    
    private static String getCsvLineFromDoubles(Double[] data){
        return Stream.of(data)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
