package com.jsondataset.json_operators.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dataset_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String datasetName;

    @Column(columnDefinition = "JSON")
    private String jsonData;
}
