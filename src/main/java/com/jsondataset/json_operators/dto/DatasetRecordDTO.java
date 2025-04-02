package com.jsondataset.json_operators.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class DatasetRecordDTO {

    private String datasetName;

    @NotNull(message = "JSON data cannot be null")
    private Map<String, Object> jsonData;
}
