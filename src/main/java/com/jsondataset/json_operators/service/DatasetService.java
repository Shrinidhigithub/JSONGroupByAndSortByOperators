package com.jsondataset.json_operators.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsondataset.json_operators.model.DatasetRecord;
import com.jsondataset.json_operators.repository.DatasetRecordRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetService {

    private final DatasetRecordRepository repository;
    private final ObjectMapper objectMapper;

    public DatasetService(DatasetRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public DatasetRecord saveRecord(String datasetName, Map<String, Object> jsonData) throws Exception {
        if (datasetName == null || datasetName.trim().isEmpty() || jsonData == null) {
            throw new IllegalArgumentException("Dataset name and JSON data cannot be null.");
        }

        DatasetRecord record = new DatasetRecord();
        record.setDatasetName(datasetName);
        record.setJsonData(objectMapper.writeValueAsString(jsonData));
        return repository.save(record);
    }

    public List<Map<String, Object>> getRecords(String datasetName) throws Exception {
        List<DatasetRecord> records = repository.findByDatasetName(datasetName);
        List<Map<String, Object>> result = new ArrayList<>();
        for (DatasetRecord record : records) {
            Map<String, Object> jsonData = objectMapper.readValue(
                record.getJsonData(), new TypeReference<Map<String, Object>>() {
            });
            result.add(jsonData);
        }
        return result;
    }

    public Map<String, List<Map<String, Object>>> groupBy(String datasetName, String groupByKey) throws Exception {
        List<Map<String, Object>> records = getRecords(datasetName);

        return records.stream()
                .filter(record -> record.containsKey(groupByKey))
                .collect(Collectors.groupingBy(record -> String.valueOf(record.get(groupByKey)), LinkedHashMap::new, Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> sortBy(String datasetName, String sortByKey, String order) throws Exception {
        List<Map<String, Object>> records = getRecords(datasetName);

        Comparator<Map<String, Object>> comparator = (record1, record2) -> {
            Object val1 = record1.get(sortByKey);
            Object val2 = record2.get(sortByKey);

            if (val1 instanceof Comparable && val2 instanceof Comparable) {
                return ((Comparable<Object>) val1).compareTo(val2);
            }
            return 0; // If values are not comparable, treat them as equal
        };

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return records.stream().sorted(comparator).collect(Collectors.toList());
    }

}
