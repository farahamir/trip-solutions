package tdr.solutions.model;

import java.util.Comparator;

public class TdrEndTimeComparator implements Comparator<TripDetailRecord> {

    @Override
    public int compare(TripDetailRecord firstCdr, TripDetailRecord secondCdr) {
       return firstCdr.endTime().compareTo(secondCdr.endTime());
    }

}