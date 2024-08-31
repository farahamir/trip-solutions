package tdr.solutions.controller;

import tdr.solutions.exception.TdrException;
import tdr.solutions.model.TripDetailRecord;
import tdr.solutions.service.TdrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling Trip Detail Records (TDR).
 * Provides endpoints for creating, retrieving, and managing TDRs.
 */
@RestController
@RequestMapping("/tdr")
@Tag(name = "Trip Detail Records", description = "APIs related to Trip Detail Records management")
public class TdrController {

    private final TdrService tdrService;

    public TdrController(TdrService tdrService) {
        this.tdrService = tdrService;
    }

    /**
     * Create a new Trip Detail Record.
     *
     * @param tdr the Trip Detail Record to create
     * @return the ResponseEntity with status 200 (OK) and the created Trip Detail Record,
     * or with status 400 (Bad Request) if the input data is invalid
     * @throws TdrException if there is an error while creating the TDR
     */
    @Operation(summary = "Create a new Trip Detail Record", description = "Creates a new Trip Detail Record (TDR) and returns the created record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TDR created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripDetailRecord.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<TripDetailRecord> createTdr(@Valid @RequestBody TripDetailRecord tdr) throws TdrException {
        return ResponseEntity.ok(tdrService.createTdr(tdr));
    }

    /**
     * Get a Trip Detail Record by Session ID.
     *
     * @param sessionId the session ID of the Trip Detail Record to retrieve
     * @return the ResponseEntity with status 200 (OK) and the found Trip Detail Record,
     * or with status 404 (Not Found) if the record does not exist
     * @throws TdrException if there is an error while retrieving the TDR
     */
    @Operation(summary = "Get a Trip Detail Record by Session ID", description = "Retrieves a Trip Detail Record (TDR) by its session ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TDR found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripDetailRecord.class))}),
            @ApiResponse(responseCode = "404", description = "TDR not found",
                    content = @Content)
    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<TripDetailRecord> getTdrBySessionId(@PathVariable String sessionId) throws TdrException {
        return ResponseEntity.ok(tdrService.getTdrBySessionId(sessionId));
    }

    /**
     * Get all Trip Detail Records for a specific vehicle, sorted by start time or end time.
     *
     * @param vehicleId the ID of the vehicle
     * @param sortBy    the field to sort by (default is start time)
     * @param sortOrder the order to sort by, either "asc" for ascending or "desc" for descending (default is ascending)
     * @param page      the page number to retrieve (default is 0)
     * @param size      the number of records per page (default is 3)
     * @return the ResponseEntity with status 200 (OK) and the sorted list of Trip Detail Records for the specified vehicle
     */
    @Operation(summary = "Get all Trip Detail Records for a specific vehicle with sorting", description = "Retrieves all Trip Detail Records (TDRs) for a specific vehicle, sorted by a specified field.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TDRs retrieved and sorted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TripDetailRecord.class))})
    })
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<TripDetailRecord>> getTdrsByVehicleId(
            @PathVariable String vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Pageable pageable = PageRequest.of(page, size, sortOrder.equals("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending());
        return ResponseEntity.ok(tdrService.getTdrsByVehicleId(vehicleId, pageable));
    }


}
