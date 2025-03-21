package se.myhappyplants.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.myhappyplants.client.view.PlantPane;
import se.myhappyplants.shared.SortingOption;

import java.util.Comparator;

/**
 * Class that sorts lists of plant panes according to different options
 * Created by: Christopher O'Driscoll
 * Updated by: Christopher O'Driscoll
 */
public class ListSorter {

    private static ObservableList<PlantPane> listToBeSorted;

    /**
     * Creates a list of sorting options for search results
     *
     * @return ObservableList<SortingOption>
     */
    public static ObservableList<SortingOption> sortOptionsSearch() {
        ObservableList<SortingOption> sortOptions = FXCollections.observableArrayList();
        for (SortingOption option : SortingOption.values()) {
            if (option != SortingOption.NICKNAME && option != SortingOption.WATER_NEED && option != SortingOption.FAVORITES) //null on search results
                sortOptions.add(option);
        }
        return sortOptions;
    }

    /**
     * Creates a list of sorting options for a user's library
     *
     * @return ObservableList<SortingOption>
     */
    public static ObservableList<SortingOption> sortOptionsLibrary() {
        ObservableList<SortingOption> sortOptions = FXCollections.observableArrayList();
        for (SortingOption option : SortingOption.values()) {
            if (option != SortingOption.COMMON_NAME && option != SortingOption.SCIENTIFIC_NAME) //null on library plants
                sortOptions.add(option);
        }
        return sortOptions;
    }

    /**
     * calls a different sorting technique based on sorting option selected
     *
     * @param sortOption
     * @param plantList
     * @return ObservableList<PlantPane>
     */
    public static ObservableList<PlantPane> sort(SortingOption sortOption, ObservableList<PlantPane> plantList) {
        System.out.println("Sorting by: " + sortOption);
        switch (sortOption) {
            case NICKNAME -> plantList.sort(Comparator.comparing(pane -> pane.getPlant().getNickname(), String.CASE_INSENSITIVE_ORDER));
            case COMMON_NAME -> plantList.sort(Comparator.comparing(pane -> pane.getPlant().getCommonName(), String.CASE_INSENSITIVE_ORDER));
            case SCIENTIFIC_NAME -> plantList.sort(Comparator.comparing(pane -> pane.getPlant().getScientificName(), String.CASE_INSENSITIVE_ORDER));
            case WATER_NEED -> plantList.sort(Comparator.comparingDouble(pane -> pane.getPlant().getProgress()));
            case FAVORITES -> plantList.sort(Comparator.comparing(pane -> !((PlantPane) pane).getPlant().getIsFavorite()).thenComparing(pane -> ((PlantPane) pane).getPlant().getNickname(), String.CASE_INSENSITIVE_ORDER));
            default -> {} //no sorting
        }
        return plantList;
    }
}
