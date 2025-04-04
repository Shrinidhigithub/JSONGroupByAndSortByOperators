Backend - JSON group by and Sort By Operators

Overview:
This is a Spring Boot application that provides JSON processing APIs with group-by and sort-by operators.
It supports H2 (test environment) and MySQL (production environment) databases.

Tech Stack:
Backend: Java, Spring Boot, Spring Data JPA
Database: H2 (for tests), MySQL (for production)
API Documentation: OpenAPI (Springdoc)
Testing: JUnit and Postman

Features:
Insert JSON records into a database
Query datasets with group-by and sort-by operators
OpenAPI documentation via /api-docs
Supports both H2 (tests) and MySQL (production)

Project Structure:
json-operators/
│── src/
│   ├── main/java/com/com/jsondataset/json_operators)/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   └── dto/
│   ├── test/java/com/jsondataset/json_operators/
        ├── controller/
│   │   ├── service/
│── application.properties
│── application-test.properties
│── pom.xml
│── README.md

Running Tests:
mvn test OR Right Click On Class Run - for Testing
Run as JsonOperatorsApplication - for Local/Production

Document : http://localhost:8080/api-docs
{
    "openapi": "3.0.1",
    "info": {
        "title": "OpenAPI definition",
        "version": "v0"
    },
    "servers": [
        {
            "url": "http://localhost:8080",
            "description": "Generated server url"
        }
    ],
    "paths": {
        "/api/dataset/{datasetName}/record": {
            "post": {
                "tags": [
                    "dataset-controller"
                ],
                "operationId": "insertRecord",
                "parameters": [
                    {
                        "name": "datasetName",
                        "in": "path",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "requestBody": {
                    "content": {
                        "application/json": {
                            "schema": {
                                "$ref": "#/components/schemas/DatasetRecordDTO"
                            }
                        }
                    },
                    "required": true
                },
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "type": "object",
                                    "additionalProperties": {
                                        "type": "object"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        "/api/dataset/{datasetName}/query": {
            "get": {
                "tags": [
                    "dataset-controller"
                ],
                "operationId": "queryDataset",
                "parameters": [
                    {
                        "name": "datasetName",
                        "in": "path",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    },
                    {
                        "name": "groupBy",
                        "in": "query",
                        "required": false,
                        "schema": {
                            "type": "string"
                        }
                    },
                    {
                        "name": "sortBy",
                        "in": "query",
                        "required": false,
                        "schema": {
                            "type": "string"
                        }
                    },
                    {
                        "name": "order",
                        "in": "query",
                        "required": false,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "content": {
                            "*/*": {
                                "schema": {
                                    "type": "object",
                                    "additionalProperties": {
                                        "type": "object"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    "components": {
        "schemas": {
            "DatasetRecordDTO": {
                "required": [
                    "jsonData"
                ],
                "type": "object",
                "properties": {
                    "datasetName": {
                        "type": "string"
                    },
                    "jsonData": {
                        "type": "object",
                        "additionalProperties": {
                            "type": "object"
                        }
                    }
                }
            }
        }
    }
}

API Endpoints:
Method: POST	                  
Endpoint: /api/datasets/employee_dataset/record	                              
Description: Insert JSON data 

Headers : Key - Content-Type 
          Value - application/json

Example: http://localhost:8080/api/dataset/employee_dataset/record
Request:
{
    "jsonData": {
        "id": 3,
        "name": "Alice Brown",
        "age": 28,
        "department": "Marketing"
    }
}

Response:
{
    "message": "Record added successfully",
    "dataset": "employee_dataset",
    "recordId": 3
}

Method: GET	       
Endpoint: /api/datasets/employee_dataset/query?groupBy=department
Description: Group by department

Example: http://localhost:8080/api/dataset/employee_dataset/query?groupBy=department

{
    "groupedRecords": {
    "Engineering": [
    {
        "id": 1,
        "name": "John Doe",
        "age": 30,
        "department": "Engineering"
    },
    {
        "id": 2,
        "name": "Jane Smith",
        "age": 25,
        "department": "Engineering"
    }
    ],
    "Marketing": [
    {
        "id": 3,
        "name": "Alice Brown",
        "age": 28,
        "department": "Marketing"
    }
    ]
    }
}

Method: GET	       
Endpoint: /api/datasets/employee_dataset/query?sortBy=age&order=asc  
Description: Sort by age (asc)

Example: http://localhost:8080/api/dataset/employee_dataset/query?sortBy=age&order=asc

{
    "sortedRecords": [
    {
        "id": 2,
        "name": "Jane Smith",
        "age": 25,
        "department": "Engineering"
    },
    {
        "id": 3,
        "name": "Alice Brown",
        "age": 28,
        "department": "Marketing"
    },
    {
        "id": 1,
        "name": "John Doe",
        "age": 30,
        "department": "Engineering"
    }
    ]
}
