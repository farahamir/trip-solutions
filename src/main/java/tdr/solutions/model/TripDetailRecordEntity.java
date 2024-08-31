package tdr.solutions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_detail_record", indexes = {@Index(columnList = "vehicleId"),@Index(columnList = "startTime DESC, endTime")})
@Data
public class TripDetailRecordEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(unique=true)
    private String sessionId;

    @NotNull
    @Size(min = 5, max = 50)
    private String vehicleId;

    @NotNull
    @PastOrPresent
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @Min(0)
    private double totalCost;


    @PrePersist
    @PreUpdate
    private void validateEndTime() {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
    }
}
