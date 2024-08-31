package tdr.solutions.service;

import tdr.solutions.repository.TripDetailRecordRepository;
import tdr.solutions.repository._2023.TripDetailRecordRepository2023;
import tdr.solutions.repository._2024.TripDetailRecordRepository2024;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for determining the appropriate data shard (i.e., the correct
 * {@link TripDetailRecordRepository}) based on the year of the Trip Detail Record's start time.
 *
 * <p>This service initializes and manages a map of repositories, where each repository handles data
 * for a specific year. The sharding is based on the year component of the {@link LocalDateTime}
 * of the record's start time.</p>
 *
 * <p>The service currently supports sharding for the years 2023 and 2024, with the corresponding
 * repositories {@link TripDetailRecordRepository2023} and {@link TripDetailRecordRepository2024}.</p>
 */
@Service
public class TdrShardingService {

    private final TripDetailRecordRepository2023 tdrRepository2023;
    private final TripDetailRecordRepository2024 tdrRepository2024;

    /**
     * A map that holds the relationship between a year (as an {@link Integer})
     * and its corresponding {@link TripDetailRecordRepository}.
     */
    @Getter
    private final Map<Integer, TripDetailRecordRepository> jpaRepositoryHashMap = new HashMap<>();

    /**
     * Constructor to inject the required repositories for sharding.
     *
     * @param tdrRepository2023 the repository handling Trip Detail Records for the year 2023
     * @param tdrRepository2024 the repository handling Trip Detail Records for the year 2024
     */
    public TdrShardingService(TripDetailRecordRepository2023 tdrRepository2023, TripDetailRecordRepository2024 tdrRepository2024) {
        this.tdrRepository2023 = tdrRepository2023;
        this.tdrRepository2024 = tdrRepository2024;
    }

    /**
     * Initializes the jpaRepositoryHashMap with the repositories corresponding to
     * the supported years. This method is executed after the constructor has been invoked
     * and all dependencies have been injected.
     */
    @PostConstruct
    public void init() {
        jpaRepositoryHashMap.put(2023, tdrRepository2023);
        jpaRepositoryHashMap.put(2024, tdrRepository2024);
    }

    /**
     * Determines the appropriate {@link TripDetailRecordRepository} based on the
     * year of the provided start time.
     *
     * @param startTime the {@link LocalDateTime} representing the start time of the Trip Detail Record
     * @return the corresponding {@link TripDetailRecordRepository} for the year of the start time
     */
    public TripDetailRecordRepository determineShard(LocalDateTime startTime) {
        return jpaRepositoryHashMap.get(startTime.getYear());
    }
}
