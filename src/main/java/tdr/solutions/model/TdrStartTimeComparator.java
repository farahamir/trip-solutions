package tdr.solutions.model;

import java.util.Comparator;

public class TdrStartTimeComparator implements Comparator<TripDetailRecord> {

    @Override
    public int compare(TripDetailRecord firstCdr, TripDetailRecord secondCdr) {
       return firstCdr.startTime().compareTo(secondCdr.startTime());
    }

}