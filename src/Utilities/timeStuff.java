package Utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.*;

public class timeStuff {
    private static ObservableList<LocalDateTime> startAvailableTimes = FXCollections.observableArrayList();
    private static ObservableList<LocalDateTime> endAvailableTimes = FXCollections.observableArrayList();


    /**
     * get available times method
     * This returns a list of times from hour 0 to hour 23:30
     * @return
     */
    public static ObservableList<LocalDateTime> getAvailableTimes(){
        startAvailableTimes.clear();
        endAvailableTimes.clear();
        LocalDateTime localDateTimeStart = LocalDateTime.of(LocalDate.now(),LocalTime.of(00,00));
        LocalDateTime localDateTimeEnd = LocalDateTime.of(LocalDate.now(),LocalTime.of(23,59));
        while (localDateTimeStart.isBefore(localDateTimeEnd)){
            startAvailableTimes.add(localDateTimeStart);
            endAvailableTimes.add(localDateTimeStart);
            localDateTimeStart = localDateTimeStart.plusMinutes(30);
        }
        return startAvailableTimes;
    }

    /**
     * get end available times method
     * this returns a list of end times/ This method is only relevant when get available times is called before this one
     * @return
     */
    public static ObservableList<LocalDateTime> getEndAvailableTimes(){
        return endAvailableTimes;
    }

    public static ZonedDateTime getEST(){
        return ZonedDateTime.of(LocalDate.now(),LocalTime.now(),ZoneId.of("America/New_York"));
    }
}

