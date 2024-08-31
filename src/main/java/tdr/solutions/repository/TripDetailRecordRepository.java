package tdr.solutions.repository;

import tdr.solutions.model.TripDetailRecordEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Trip Detail Records.
 */
@Repository
public interface TripDetailRecordRepository extends JpaRepository<TripDetailRecordEntity, Long> {

    /**
     * Find all Trip Detail Records by vehicle ID.
     *
     * @param vehicleId the ID of the vehicle
     * @return the list of Trip Detail Records for the specified vehicle
     */
    List<TripDetailRecordEntity> findAllByVehicleId(String vehicleId, Pageable pageable);

    /**
     * Find all Trip Detail Records by vehicle ID.
     *
     * @param sessionId the sessionId of the TDR
     * @return TripDetailRecord the of Trip Detail Records for the specified sessionId
     */
    TripDetailRecordEntity findBySessionId(String sessionId);
}
