package rocky.teatime.database.TeaStuff;

/**
 * A simple class meant to ease the serialisation of a tea object!
 */
public class JsonTea {

    private long id;                             // The id to quickly retrieve the entry in the database
    private String name;                         // Name of the tea
    private TeaType type;                        // What kind of tea is it?
    private int brewTime;                        // Brew Time (in seconds)
    private int brewTimeSub;                     // Brew Time for subsequent steepings! -1 If not set
    private int brewMin;                         // Min temp to brew the tea. -1 if not set
    private int brewMax;                         // Max temp to brew the tea. -1 If not set
    private String picLocation;                  // Location of the picture on disk.
    private float idealStrength;                 // Ideal strength of tea in oz. per US cup
    private boolean inStock;                     // True if the tea is in stock

    /**
     * Constructs a Json Tea object from a given tea object
     * @param teaClone The tea we wish to serialise in a json object.
     */
    public JsonTea(Tea teaClone) {
        this.id = teaClone.getId();
        this.name = teaClone.getName();
        this.type = teaClone.getType();
        this.brewTime = teaClone.getBrewTime();
        this.brewTimeSub = teaClone.getBrewTimeSub();
        this.brewMin = teaClone.getBrewMin();
        this.brewMax = teaClone.getBrewMax();
        this.picLocation = teaClone.getPicLocation();
        this.idealStrength = teaClone.getIdealStrength();
        this.inStock = teaClone.isInStock();
    }

    /**
     * Returns a Tea object equivalent to the Json tea object
     * @return Tea object which is equivalent the JsonTea object calling this method
     */
    public Tea makeTea() {
        Tea reconstructedTea = new Tea();
        reconstructedTea.setId(id);
        reconstructedTea.setName(name);
        reconstructedTea.setType(type);
        reconstructedTea.setBrewTime(brewTime);
        reconstructedTea.setBrewTimeSub(brewTimeSub);
        reconstructedTea.setBrewMin(brewMin);
        reconstructedTea.setBrewMax(brewMax);
        reconstructedTea.setPicLocation(picLocation);
        reconstructedTea.setIdealStrength(idealStrength);
        reconstructedTea.setInStock(inStock);
        return reconstructedTea;
    }
}
