package tdr.solutions.service;

import tdr.solutions.exception.TdrException;
import tdr.solutions.model.TripDetailRecord;
import tdr.solutions.model.TripDetailRecordEntity;
import tdr.solutions.repository.TripDetailRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TdrServiceTest {

    @Mock
    private TdrShardingService tdrShardingService;

    @Mock
    private TripDetailRecordRepository tripDetailRecordRepository;

    @InjectMocks
    private TdrService tdrService;

    @BeforeEach
    void setUp() {
        // Mock behavior for the sharding service
        when(tdrShardingService.determineShard(any(LocalDateTime.class))).thenReturn(tripDetailRecordRepository);
    }

    @Test
    void testCreateTdr_Success() throws TdrException {
        TripDetailRecord tdr = new TripDetailRecord("sessionId123", "vehicleId123",
                LocalDateTime.parse("2023-11-24T14:15:00"), LocalDateTime.parse("2023-11-24T14:15:00").plusHours(1), 15.0);

        TripDetailRecordEntity savedEntity = new TripDetailRecordEntity();
        savedEntity.setSessionId(tdr.sessionId());
        savedEntity.setVehicleId(tdr.vehicleId());
        savedEntity.setStartTime(tdr.startTime());
        savedEntity.setEndTime(tdr.endTime());
        savedEntity.setTotalCost(tdr.totalCost());

        when(tripDetailRecordRepository.save(any(TripDetailRecordEntity.class))).thenReturn(savedEntity);

        TripDetailRecord result = tdrService.createTdr(tdr);

        assertNotNull(result);
        assertEquals(tdr.sessionId(), result.sessionId());
        verify(tripDetailRecordRepository, times(1)).save(any(TripDetailRecordEntity.class));
    }

    @Test
    void testCreateTdr_DataIntegrityViolationException() {
        TripDetailRecord tdr = new TripDetailRecord("sessionId123", "vehicleId123",
                LocalDateTime.parse("2023-11-24T14:15:00"), LocalDateTime.parse("2023-11-24T14:15:00").plusHours(1), 15.0);

        when(tripDetailRecordRepository.save(any(TripDetailRecordEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        TdrException exception = assertThrows(TdrException.class, () -> tdrService.createTdr(tdr));

        assertEquals("Data integrity violation", exception.getMessage());
        verify(tripDetailRecordRepository, times(1)).save(any(TripDetailRecordEntity.class));
    }
 }
