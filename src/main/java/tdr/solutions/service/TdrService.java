package tdr.solutions.service;

import tdr.solutions.exception.TdrException;
import tdr.solutions.model.TdrEndTimeComparator;
import tdr.solutions.model.TdrStartTimeComparator;
import tdr.solutions.model.TripDetailRecord;
import tdr.solutions.model.TripDetailRecordEntity;
import tdr.solutions.repository.TripDetailRecordRepository;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class responsible for handling operations related to Trip Detail Records (TDRs).
 * This service interacts with the underlying database sharding service and repositories to
 * create, retrieve, and manage TDRs.
 */
@Service
public class TdrService {

    public static final String START_TIME = "startTime";
    private final TdrShardingService tdrShardingService;

    /**
     * Constructor for {@code TdrService}.
     *
     * @param tdrShardingService the service responsible for determining the appropriate shard
     *                           and providing access to the corresponding repository.
     */
    public TdrService(TdrShardingService tdrShardingService) {
        this.tdrShardingService = tdrShardingService;
    }

    /**
     * Creates a new Trip Detail Record (TDR) in the appropriate shard.
     *
     * @param tdr the TDR to be created, must be valid.
     * @return the created TDR.
     * @throws TdrException if there is an issue during the creation process, such as data integrity violations.
     */
    public TripDetailRecord createTdr(@Valid TripDetailRecord tdr) throws TdrException {
        var tdrRepository = tdrShardingService.determineShard(tdr.startTime());
        TripDetailRecordEntity tripDetailRecordEntity = getTripDetailRecordEntity(tdr);
        TripDetailRecordEntity result;
        try {
            result = tdrRepository.save(tripDetailRecordEntity);
        } catch (DataIntegrityViolationException e) {
            throw new TdrException(e.getMessage());
        }
        return getTripDetailRecord(result);
    }

    /**
     * Retrieves a Trip Detail Record (TDR) by its session ID.
     * This method uses caching to improve performance for frequently accessed session IDs.
     *
     * @param sessionId the session ID of the TDR to be retrieved.
     * @return the TDR associated with the given session ID.
     * @throws TdrException if no TDR is found for the given session ID.
     */
    @Cacheable(value = "sessionIds", key = "#sessionId")
    public TripDetailRecord getTdrBySessionId(String sessionId) throws TdrException {
        var map = tdrShardingService.getJpaRepositoryHashMap();
        for (TripDetailRecordRepository tripDetailRecordRepository : map.values()) {
            TripDetailRecordEntity result = tripDetailRecordRepository.findBySessionId(sessionId);
            if (result != null) {
                return getTripDetailRecord(result);
            }
        }
        throw new TdrException("sessionId " + sessionId + " not found");
    }

    /**
     * Retrieves a list of Trip Detail Records (TDRs) for a specific vehicle ID, with pagination and sorting.
     *
     * @param vehicleId the ID of the vehicle whose TDRs are to be retrieved.
     * @param pageable  the pagination and sorting information.
     * @return a list of TDRs for the specified vehicle, sorted by the specified criteria.
     */
    public List<TripDetailRecord> getTdrsByVehicleId(String vehicleId, Pageable pageable) {
        var map = tdrShardingService.getJpaRepositoryHashMap();
        List<TripDetailRecord> result = new ArrayList<>();
        for (TripDetailRecordRepository tripDetailRecordRepository : map.values()) {
            var vehicleIdOrderByStartTimeAsc = tripDetailRecordRepository.findAllByVehicleId(vehicleId, pageable);
            vehicleIdOrderByStartTimeAsc.stream().map(TdrService::getTripDetailRecord).forEach(result::add);
        }
        sortResult(pageable, result);
        return result.stream().limit(pageable.getPageSize()).toList();
    }

    /**
     * Converts a {@link TripDetailRecord} model to a {@link TripDetailRecordEntity} entity.
     *
     * @param tdr the TDR model to convert.
     * @return the corresponding TDR entity.
     */
    private static TripDetailRecordEntity getTripDetailRecordEntity(TripDetailRecord tdr) {
        TripDetailRecordEntity tripDetailRecordEntity = new TripDetailRecordEntity();
        tripDetailRecordEntity.setSessionId(tdr.sessionId());
        tripDetailRecordEntity.setVehicleId(tdr.vehicleId());
        tripDetailRecordEntity.setStartTime(tdr.startTime());
        tripDetailRecordEntity.setEndTime(tdr.endTime());
        tripDetailRecordEntity.setTotalCost(tdr.totalCost());
        return tripDetailRecordEntity;
    }

    /**
     * Converts a {@link TripDetailRecordEntity} entity to a {@link TripDetailRecord} model.
     *
     * @param tripDetailRecordEntity the TDR entity to convert.
     * @return the corresponding TDR model.
     */
    private static TripDetailRecord getTripDetailRecord(TripDetailRecordEntity tripDetailRecordEntity) {
        return new TripDetailRecord(
                tripDetailRecordEntity.getSessionId(),
                tripDetailRecordEntity.getVehicleId(),
                tripDetailRecordEntity.getStartTime(),
                tripDetailRecordEntity.getEndTime(),
                tripDetailRecordEntity.getTotalCost());
    }

    /**
     * Sort the combined results from the database according to pagable
     * @param pageable the sorting which was done in database
     * @param result for the function to modify
     */
    private static void sortResult(Pageable pageable, List<TripDetailRecord> result) {
        var sortByProperty = pageable.getSort().stream().findFirst().get().getProperty();
        var direction = pageable.getSort().stream().findFirst().get().getDirection();
        var sortBy = sortByProperty.equals(START_TIME) ? new TdrStartTimeComparator() : new TdrEndTimeComparator();
        result.sort(sortBy);
        if (direction.isDescending()) {
            result.sort(Collections.reverseOrder( sortBy));
        }
    }
}
