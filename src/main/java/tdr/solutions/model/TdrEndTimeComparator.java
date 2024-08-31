package tdr.solutions.model;

import java.util.Comparator;

public class TdrEndTimeComparator implements Comparator<TripDetailRecord> {

    @Override
    public int compare(TripDetailRecord firstTdr, TripDetailRecord secondTdr) {
       return firstTdr.endTime().compareTo(secondTdr.endTime());
    }

}