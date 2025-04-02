package com.jsondataset.json_operators.controller;

import com.jsondataset.json_operators.model.DatasetRecord;
import com.jsondataset.json_operators.repository.DatasetRecordRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatasetRecordRepository datasetRepository;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        if (datasetRepository.count() > 0) {
            datasetRepository.deleteAll(); // Only delete if records exist
        }

        // Insert sample records
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
    public void testQueryWithGroupBy() throws Exception {
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                        .param("groupBy", "department")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering", hasSize(2)))
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].name").value("John Doe"))
                .andExpect(jsonPath("$.groupedRecords.Engineering[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$.groupedRecords.Marketing", hasSize(1)))
                .andExpect(jsonPath("$.groupedRecords.Marketing[0].name").value("Alice Brown"));
    }

    // Test for Sort-By Age (Ascending)
    @Test
    public void testQueryWithSortByAgeAscending() throws Exception {
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                        .param("sortBy", "age")
                        .param("order", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords", hasSize(3)))
                .andExpect(jsonPath("$.sortedRecords[0].name").value("Jane Smith"))
                .andExpect(jsonPath("$.sortedRecords[1].name").value("Alice Brown"))
                .andExpect(jsonPath("$.sortedRecords[2].name").value("John Doe"));
    }

    @Test
    void testFetchJsonData() {
        List<DatasetRecord> records = datasetRepository.findAll();
        records.forEach(record -> System.out.println("Fetched JSON: " + record.getJsonData()));
        assertFalse(records.isEmpty(), "No JSON data found!");
    }
}
