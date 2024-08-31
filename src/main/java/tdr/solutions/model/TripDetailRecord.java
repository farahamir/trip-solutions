package tdr.solutions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.ParameterScriptAssert;

import java.time.LocalDateTime;

public record TripDetailRecord(
        @NotNull
        @Size(min = 5, max = 50)
        @Column(unique = true)
        String sessionId,

        @NotNull
        @Size(min = 5, max = 50)
        String vehicleId,

        @NotNull
        @PastOrPresent
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime,

        @Min(0) double totalCost
) {

    @ParameterScriptAssert(lang = "javascript", script = "startTime > endTime")
    public TripDetailRecord {
    }
}
