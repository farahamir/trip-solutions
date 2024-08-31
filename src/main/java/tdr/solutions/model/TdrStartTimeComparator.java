package tdr.solutions.model;

import java.util.Comparator;

public class TdrStartTimeComparator implements Comparator<TripDetailRecord> {

    @Override
    public int compare(TripDetailRecord firstTdr, TripDetailRecord secondTdr) {
       return firstTdr.startTime().compareTo(secondTdr.startTime());
    }

}