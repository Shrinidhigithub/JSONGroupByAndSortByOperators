package com.jsondataset.json_operators.service;

import com.jsondataset.json_operators.model.DatasetRecord;
import com.jsondataset.json_operators.repository.DatasetRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DatasetServiceTest {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private DatasetRecordRepository datasetRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        if (datasetRepository.count() > 0) {
            datasetRepository.deleteAll(); // Only delete if records exist
        }

        // Insert test data
        DatasetRecord record1 = new DatasetRecord();
        record1.setDatasetName("employee_dataset");
        record1.setJsonData("{\"id\": 1, \"name\": \"John Doe\", \"age\": 30, \"department\": \"Engineering\"}");

        DatasetRecord record2 = new DatasetRecord();
        record2.setDatasetName("employee_dataset");
        record2.setJsonData("{\"id\": 2, \"name\": \"Jane Smith\", \"age\": 25, \"department\": \"Engineering\"}");

        DatasetRecord record3 = new DatasetRecord();
        record3.setDatasetName("employee_dataset");
        record3.setJsonData("{\"id\": 3, \"name\": \"Alice Brown\", \"age\": 28, \"department\": \"Marketing\"}");

        datasetRepository.saveAll(List.of(record1, record2, record3));
    }

    // Test for Group-By Department
    @Test
    public void testGroupByDepartment() throws Exception {
        Map<String, List<Map<String, Object>>> groupedRecords = datasetService.groupBy("employee_dataset", "department");

        assertNotNull(groupedRecords);
        assertEquals(2, groupedRecords.size()); // 2 departments: Engineering & Marketing

        assertTrue(groupedRecords.containsKey("Engineering"));
        assertTrue(groupedRecords.containsKey("Marketing"));

        assertEquals(2, groupedRecords.get("Engineering").size());
        assertEquals(1, groupedRecords.get("Marketing").size());
    }

    // Test for Sort-By Age (Ascending)
    @Test
    public void testSortByAgeAscending() throws Exception {
        List<Map<String, Object>> sortedRecords = datasetService.sortBy("employee_dataset", "age", "asc");

        assertNotNull(sortedRecords);
        assertEquals(3, sortedRecords.size());

        assertEquals("Jane Smith", sortedRecords.get(0).get("name")); // Youngest (25)
        assertEquals("Alice Brown", sortedRecords.get(1).get("name")); // Middle (28)
        assertEquals("John Doe", sortedRecords.get(2).get("name")); // Oldest (30)
    }

    // Test for Sort-By Age (Descending)
    @Test
    public void testSortByAgeDescending() throws Exception {
        List<Map<String, Object>> sortedRecords = datasetService.sortBy("employee_dataset", "age", "desc");

        assertNotNull(sortedRecords);
        assertEquals(3, sortedRecords.size());

        assertEquals("John Doe", sortedRecords.get(0).get("name")); // Oldest (30)
        assertEquals("Alice Brown", sortedRecords.get(1).get("name")); // Middle (28)
        assertEquals("Jane Smith", sortedRecords.get(2).get("name")); // Youngest (25)
    }
}
