package se.myhappyplants.shared;
/**
 * Enum of different sorting options
 * Created by: Christopher O'Driscoll
 * Updated by: Christopher O'Driscoll
 */
public enum SortingOption {

    NICKNAME("  Nickname"),
    COMMON_NAME("  Common name"),
    SCIENTIFIC_NAME("  Scientific name"),
    FAVORITES("  Favorites"),
    WATER_NEED("  Water need");

    private final String name;

    /**
     * Constreuctor to set the text
     * @param name
     */
    SortingOption(String name) {
        this.name = name;
    }
    /**
     * ToString method with the text
     * @return
     */
    public String toString() {
        return name;
    }
}
