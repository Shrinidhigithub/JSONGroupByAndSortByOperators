package com.jsondataset.json_operators.controller;

import com.jsondataset.json_operators.dto.DatasetRecordDTO;
import com.jsondataset.json_operators.model.DatasetRecord;
import com.jsondataset.json_operators.service.DatasetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    // Insert Data
    @PostMapping("/{datasetName}/record")
    public ResponseEntity<Map<String, Object>> insertRecord(@PathVariable String datasetName,
                                          @RequestBody @Valid DatasetRecordDTO dto) {
        // Validate datasetName
        if (datasetName == null || datasetName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message","Dataset name cannot be null or empty."));
        }

        // Validate jsonData
        if (dto.getJsonData() == null || dto.getJsonData().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message","JSON data cannot be null or empty."));
        }

        try {
            DatasetRecord savedRecord = datasetService.saveRecord(datasetName, dto.getJsonData());
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Record added successfully");
            response.put("dataset", datasetName);
            response.put("recordId", savedRecord.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }

    // Get Data
    @GetMapping("/{datasetName}/query")
    public ResponseEntity<Map<String, Object>> queryDataset(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order) {
        try {
            Map<String, Object> response = new LinkedHashMap<>();

            if (groupBy != null) {
                Map<String, List<Map<String, Object>>> groupedRecords = datasetService.groupBy(datasetName, groupBy);

                // Reorder fields in each record
                Map<String, List<Map<String, Object>>> orderedGroupedRecords = new LinkedHashMap<>();
                groupedRecords.forEach((key, records) -> {
                    orderedGroupedRecords.put(key, records.stream()
                            .map(this::reorderFields)
                            .toList());
                });

                response.put("groupedRecords", orderedGroupedRecords);
            } else if (sortBy != null) {
                List<Map<String, Object>> sortedRecords = datasetService.sortBy(datasetName, sortBy, order);

                // Reorder fields in each record
                List<Map<String, Object>> orderedSortedRecords = sortedRecords.stream()
                        .map(this::reorderFields)
                        .toList();

                response.put("sortedRecords", orderedSortedRecords);
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Either groupBy or sortBy must be provided"));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    private Map<String, Object> reorderFields(Map<String, Object> record) {
        Map<String, Object> orderedRecord = new LinkedHashMap<>();

        // Define the correct order
        orderedRecord.put("id", record.get("id"));
        orderedRecord.put("name", record.get("name"));
        orderedRecord.put("age", record.get("age"));
        orderedRecord.put("department", record.get("department"));

        return orderedRecord;
    }

}
