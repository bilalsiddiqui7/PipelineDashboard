# Pipeline Dashboard - Architecture & Knowledge Transfer Documentation

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [System Architecture](#system-architecture)
4. [Database Design](#database-design)
5. [API Documentation](#api-documentation)
6. [Key Features](#key-features)
7. [Configuration](#configuration)
8. [Deployment Guide](#deployment-guide)
9. [Development Setup](#development-setup)
10. [Best Practices](#best-practices)
11. [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

**Pipeline Dashboard** is a Spring Boot application designed to manage and monitor data pipelines with an integrated workflow approval system. The application provides:

- **Pipeline Management**: Create, update, delete, and manage data pipelines
- **Configuration Management**: Store JSON/YAML configurations in AWS S3
- **Airflow Integration**: Fetch and display DAGs from Apache Airflow
- **Approval Workflow**: DE team approval system for pipeline configuration changes
- **Threshold Configuration**: Manage quality thresholds for pipeline monitoring

### Primary Use Case
The application serves as a control plane for data engineering teams to:
1. Define data pipelines with multiple steps
2. Store configurations (JSON/YAML) for each pipeline step
3. Integrate with Apache Airflow for pipeline orchestration
4. Manage quality thresholds and approval workflows

---

## 🛠 Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.10**
  - Spring Web
  - Spring Data JPA
  - Spring Validation
- **Hibernate 6.6.41.Final**

### Database
- **PostgreSQL** (Primary Database)
  - Database: `wids_platform_control`
  - Host: `restore-wids-postgres-dev.c9xgkwg1lrq2.ap-south-1.rds.amazonaws.com`
  - Port: `5432`

### Cloud Services
- **AWS S3** (Configuration Storage)
  - Bucket: `wids-dev-ap-south-1-config`
  - Region: `ap-south-1`
  - SDK: AWS SDK v2.25.20

### External Integrations
- **Apache Airflow**
  - URL: `http://aeb32d63ce5b2440088d1b4754b87f12-1254950887.ap-south-1.elb.amazonaws.com`
  - Authentication: JWT token-based

### Build & Dependencies
- **Maven** (Build tool)
- **Lombok** (Code generation)
- **Jackson** (JSON processing)

---

## 🏗 System Architecture

### High-Level Architecture

```
┌─────────────┐
│   Frontend  │
│   (React)   │
└──────┬──────┘
       │ REST API
       │
┌──────▼────────────────────────────────────┐
│     Spring Boot Application (Port 8081)   │
│                                            │
│  ┌──────────────┐  ┌──────────────┐      │
│  │ Controllers  │  │   Services   │      │
│  ├──────────────┤  ├──────────────┤      │
│  │ Pipeline     │  │ Pipeline     │      │
│  │ Configuration│  │ Configuration│      │
│  │ Approval     │  │ Approval     │      │
│  │ Airflow      │  │ Airflow      │      │
│  └──────┬───────┘  └──────┬───────┘      │
│         │                  │               │
│  ┌──────▼──────────────────▼───────┐      │
│  │      Repository Layer           │      │
│  └──────┬───────────────────────────┘     │
└─────────┼──────────────────────────────────┘
          │
    ┌─────┴─────┬──────────────┬────────────┐
    │           │              │            │
┌───▼────┐ ┌───▼───┐    ┌─────▼─────┐ ┌──▼──────┐
│PostgreSQL│ │AWS S3 │    │  Airflow  │ │External │
│   RDS    │ │Bucket │    │   API     │ │ Configs │
└──────────┘ └───────┘    └───────────┘ └─────────┘
```

### Layer Architecture

#### 1. **Controller Layer** (`com.pipeline.controller`)
- `PipelineController`: Pipeline CRUD operations
- `ConfigurationController`: Configuration parameter and Airflow DAG management
- `ApprovalController`: DE approval workflow management

#### 2. **Service Layer** (`com.pipeline.service`)
- `PipelineService`: Business logic for pipeline management
- `ConfigurationService`: Configuration parameter handling
- `ApprovalService`: Approval workflow processing
- `AirflowService`: Airflow integration
- `S3StorageService`: AWS S3 operations

#### 3. **Repository Layer** (`com.pipeline.repository`)
- `PipelineRepository`: Pipeline entity operations
- `ConfigurationParameterRepository`: Configuration parameters
- `UserConfigurationValueRepository`: User-defined values
- `DEApprovalRequestRepository`: Approval requests
- `WorkflowThresholdConfigRepository`: Threshold configurations

#### 4. **Model Layer** (`com.pipeline.model`)
- `Pipeline`: Main pipeline entity
- `PipelineStep`: Individual pipeline steps
- `ConfigurationParameter`: Available configuration parameters
- `UserConfigurationValue`: User-configured values
- `DEApprovalRequest`: Approval workflow requests
- `WorkflowThresholdConfig`: Quality thresholds

---

## 🗄 Database Design

### Entity Relationship Diagram

```
Pipeline (1) ──< (N) PipelineStep
    │
    ├─ id (PK)
    ├─ name
    ├─ description
    ├─ enabled
    ├─ scheduleType
    ├─ scheduleTime
    ├─ scheduleDay
    ├─ scheduleDayOfMonth
    ├─ cronExpression
    └─ created_at, updated_at

PipelineStep
    ├─ id (PK)
    ├─ stepName
    ├─ stepType (ENUM)
    ├─ stepOrder
    ├─ configType (JSON/YAML)
    ├─ configS3Key (S3 reference)
    ├─ configContent (TEXT - cached)
    ├─ pipeline_id (FK)
    └─ created_at, updated_at

ConfigurationParameter
    ├─ id (PK)
    ├─ parameterName
    ├─ displayName
    ├─ description
    ├─ dataType
    ├─ isActive
    └─ created_at, updated_at

UserConfigurationValue (N) ──> (1) ConfigurationParameter
    ├─ id (PK)
    ├─ pipelineId
    ├─ configurationParameter_id (FK)
    ├─ parameterValue
    └─ created_at, updated_at

DEApprovalRequest
    ├─ id (PK)
    ├─ pipelineId
    ├─ stage (BRONZE_TO_SILVER)
    ├─ requestTitle
    ├─ requestDescription
    ├─ configurationDetails (JSON)
    ├─ status (PENDING/APPROVED/REJECTED/PARTIAL_APPROVAL)
    ├─ reason
    ├─ requestedBy
    ├─ reviewedBy
    ├─ pipelineRunId
    ├─ slaDeadline
    ├─ datasetCount
    ├─ totalRecords
    └─ created_at, reviewed_at

WorkflowThresholdConfig
    ├─ id (PK)
    ├─ pipelineId
    ├─ recordCountVariancePercentThreshold
    ├─ volumeAnomalyVsBaselinePercentThreshold
    ├─ mandatoryColumnNullsPercentThreshold
    ├─ duplicateBusinessKeysPercentThreshold
    ├─ formatViolationsPercentThreshold
    ├─ breakingSchemaChangesAllowedThreshold
    ├─ dataFreshnessDelayMinutesThreshold
    └─ created_at
```

### Key Relationships

1. **Pipeline → PipelineStep**: One-to-Many (Cascade DELETE, Orphan Removal)
2. **UserConfigurationValue → ConfigurationParameter**: Many-to-One
3. **DEApprovalRequest → WorkflowThresholdConfig**: Related by pipelineId (not FK)

---

## 📡 API Documentation

### Base URLs
- Application: `http://localhost:8081`
- Pipeline APIs: `/api/pipelines`
- Configuration APIs: `/workflow-approval/threshold-config`
- Approval APIs: `/workflow-approval/approval-inbox`

### 1. Pipeline Management APIs

#### **GET** `/api/pipelines`
Get all pipelines (summary view)

**Response:**
```json
[
  {
    "id": 1,
    "name": "Daily Orders Pipeline",
    "description": "Ingest and transform orders",
    "enabled": true,
    "scheduleDescription": "Manual"
  }
]
```

#### **POST** `/api/pipelines`
Create a new pipeline

**Request:**
```json
{
  "name": "Daily Orders Pipeline",
  "description": "Ingest and transform orders",
  "enabled": true,
  "scheduleType": "MANUAL",
  "scheduleTime": null,
  "scheduleDay": null,
  "scheduleDayOfMonth": null,
  "steps": [
    {
      "stepName": "Ingest Orders",
      "stepType": "DATA_INGESTION",
      "stepOrder": 1,
      "configType": "JSON",
      "configContent": "{\"source\": \"s3\", \"bucket\": \"orders\"}"
    }
  ]
}
```

**Schedule Types:**
- `MANUAL`: No schedule
- `DAILY`: Runs daily at `scheduleTime`
- `WEEKLY`: Runs weekly on `scheduleDay` at `scheduleTime`
- `MONTHLY`: Runs monthly on `scheduleDayOfMonth` at `scheduleTime`
- `CRON`: Custom cron expression in `cronExpression`

**Step Types:**
- `DATA_INGESTION`
- `DATA_TRANSFORMATION`
- `DATA_QUALITY`
- `MACHINE_LEARNING`
- `DATA_EXPORT`

#### **GET** `/api/pipelines/{id}`
Get pipeline details

#### **PUT** `/api/pipelines/{id}`
Update pipeline

#### **DELETE** `/api/pipelines/{id}`
Delete pipeline (also deletes S3 configurations)

#### **PATCH** `/api/pipelines/{id}/toggle?enabled=true`
Enable/disable pipeline

#### **POST** `/api/pipelines/{id}/copy`
Copy/duplicate pipeline

---

### 2. Configuration Management APIs

#### **GET** `/workflow-approval/threshold-config/dags`
Get all Airflow DAGs (filtered by "silver" tag)

**Response:**
```json
[
  {
    "dagId": "csv_sales_data_ingestion",
    "displayName": "CSV Sales Data Ingestion",
    "isPaused": false,
    "tags": ["silver", "ingestion"]
  }
]
```

#### **GET** `/workflow-approval/threshold-config/parameters`
Get all active configuration parameters

**Response:**
```json
[
  {
    "id": 1,
    "parameterName": "recordCountVariancePercent",
    "displayName": "Record Count Variance %",
    "description": "Maximum allowed variance in record count",
    "dataType": "DOUBLE"
  }
]
```

#### **PUT** `/workflow-approval/threshold-config/{pipelineId}/values`
Save configuration values for a pipeline

**Request:**
```json
{
  "values": [
    {
      "parameterName": "recordCountVariancePercent",
      "parameterValue": "5.0"
    },
    {
      "parameterName": "mandatoryColumnNullsPercent",
      "parameterValue": "2.0"
    }
  ]
}
```

#### **GET** `/workflow-approval/threshold-config/{pipelineId}/values`
Get configuration for a pipeline

**Response:**
```json
{
  "pipelineId": "csv_sales_data_ingestion",
  "configuredValues": [
    {
      "parameterName": "recordCountVariancePercent",
      "displayName": "Record Count Variance %",
      "parameterValue": "5.0",
      "dataType": "DOUBLE"
    }
  ]
}
```

---

### 3. Approval Workflow APIs

#### **GET** `/workflow-approval/approval-inbox/pending`
Get pending approval requests

**Response:**
```json
[
  {
    "id": 1,
    "pipelineId": "csv_sales_data_ingestion",
    "requestTitle": "Sales Pipeline Config Review",
    "status": "PENDING",
    "requestedBy": "de_john@company.com",
    "createdAt": "2026-02-26T10:30:00Z"
  }
]
```

#### **GET** `/workflow-approval/approval-inbox/{id}`
Get approval request details

**Response:**
```json
{
  "id": 1,
  "pipelineId": "csv_sales_data_ingestion",
  "stage": "BRONZE_TO_SILVER",
  "requestTitle": "Sales Pipeline Configuration Review",
  "configurationDetails": "{\"recordCountVariancePercent\": \"7.5\"}",
  "actualThresholdValues": "{\"recordCountVariancePercentThreshold\": 5.0}",
  "status": "PENDING",
  "requestedBy": "de_john@company.com",
  "pipelineRunId": "RUN-20260226-083015",
  "slaDeadline": "2026-02-28T11:25:45Z",
  "datasetCount": 2,
  "totalRecords": 567890
}
```

#### **POST** `/workflow-approval/approval-inbox/{id}/approve`
Approve a request

**Request:**
```json
{
  "reviewedBy": "Bilal",
  "reason": "Configuration looks good, approved"
}
```

#### **POST** `/workflow-approval/approval-inbox/{id}/reject`
Reject a request

**Request:**
```json
{
  "reviewedBy": "Bilal",
  "reason": "Thresholds too high, needs revision"
}
```

#### **POST** `/workflow-approval/approval-inbox/{id}/partial-approve`
Partially approve a request

**Request:**
```json
{
  "reviewedBy": "Bilal",
  "reason": "Approved with conditions - monitor closely"
}
```

#### **GET** `/workflow-approval/approval-inbox/stats`
Get approval statistics

**Response:**
```json
{
  "pendingCount": 5,
  "approvedCount": 15,
  "rejectedCount": 3,
  "partialApprovalCount": 2
}
```

#### **GET** `/workflow-approval/approval-inbox/recent-decisions?filter=LAST_7_DAYS`
Get recent approval decisions

**Filters:**
- `LAST_24_HOURS`
- `LAST_7_DAYS` (default)
- `LAST_1_MONTH`

**Response:**
```json
[
  {
    "pipelineId": "csv_sales_data_ingestion",
    "status": "APPROVED",
    "reviewedAt": "2026-02-26T11:40:59Z",
    "reviewedBy": "Bilal"
  }
]
```

---

## 🎯 Key Features

### 1. **Pipeline Management**
- Create pipelines with multiple steps
- Support for various step types (Ingestion, Transformation, Quality, ML, Export)
- Flexible scheduling (Manual, Daily, Weekly, Monthly, Cron)
- Enable/disable pipelines
- Copy/duplicate pipelines

### 2. **Configuration Storage**
- **S3-based storage** for JSON/YAML configurations
- **Automatic content type detection**
- **Caching** in database for quick retrieval
- **S3 key format**: `pipelines/{pipelineId}/steps/{stepOrder}-{uuid}.{ext}`

### 3. **Airflow Integration**
- Fetch DAGs from Airflow API
- **JWT token authentication** (cached for 24 hours)
- Filter DAGs by tags (e.g., "silver")
- Display DAG status and metadata

### 4. **Approval Workflow**
- DE team can create approval requests
- Three approval actions: Approve, Reject, Partial Approval
- Track approval statistics
- Filter recent decisions by time period
- Compare configuration values with actual thresholds

### 5. **Threshold Configuration**
- Define quality thresholds per pipeline
- Parameterized configuration system
- Dynamic parameter addition
- Side-by-side comparison of configured vs. actual values

---

## ⚙ Configuration

### Application Properties (`application.properties`)

```properties
# Server
server.port=8081

# PostgreSQL
spring.datasource.url=jdbc:postgresql://<host>:5432/wids_platform_control
spring.datasource.username=wids-ui
spring.datasource.password=****

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# AWS S3
aws.region=ap-south-1
aws.s3.bucket=wids-dev-ap-south-1-config
aws.accessKeyId=****
aws.secretAccessKey=****

# Airflow
airflow.base.url=http://<airflow-host>
airflow.username=airflow
airflow.password=airflow
airflow.auth.type=jwt
```

### Environment Variables (Production)
For production, use environment variables instead of hardcoded values:

```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export SPRING_DATASOURCE_PASSWORD=your-db-password
export AIRFLOW_PASSWORD=your-airflow-password
```

---

## 🚀 Deployment Guide

### AWS Deployment Architecture

```
┌─────────────────────────────────────────────┐
│           Application Load Balancer         │
│         (Public-facing endpoint)            │
└──────────────────┬──────────────────────────┘
                   │
      ┌────────────┴────────────┐
      │                         │
┌─────▼─────┐           ┌──────▼──────┐
│ ECS Task 1│           │ ECS Task 2  │
│ (Container)│           │ (Container) │
└─────┬─────┘           └──────┬──────┘
      │                        │
      └────────────┬───────────┘
                   │
    ┌──────────────┼──────────────┬────────────┐
    │              │              │            │
┌───▼────┐    ┌───▼───┐    ┌─────▼─────┐ ┌──▼──────┐
│RDS(PG) │    │AWS S3 │    │  Airflow  │ │CloudWatch│
│Database│    │Bucket │    │   ELB     │ │  Logs   │
└────────┘    └───────┘    └───────────┘ └─────────┘
```

### Deployment Steps

#### 1. **Containerize Application**

**Dockerfile:**
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/PipelineDashboard-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build:**
```bash
mvn clean package
docker build -t pipeline-dashboard:latest .
```

#### 2. **Push to ECR**
```bash
aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.ap-south-1.amazonaws.com
docker tag pipeline-dashboard:latest <account-id>.dkr.ecr.ap-south-1.amazonaws.com/pipeline-dashboard:latest
docker push <account-id>.dkr.ecr.ap-south-1.amazonaws.com/pipeline-dashboard:latest
```

#### 3. **ECS Task Definition**
```json
{
  "family": "pipeline-dashboard",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "pipeline-dashboard",
      "image": "<ecr-image-uri>",
      "portMappings": [{"containerPort": 8081}],
      "environment": [
        {"name": "SPRING_PROFILES_ACTIVE", "value": "prod"}
      ],
      "secrets": [
        {"name": "AWS_ACCESS_KEY_ID", "valueFrom": "arn:aws:secretsmanager:..."},
        {"name": "AWS_SECRET_ACCESS_KEY", "valueFrom": "arn:aws:secretsmanager:..."}
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/pipeline-dashboard",
          "awslogs-region": "ap-south-1"
        }
      }
    }
  ]
}
```

#### 4. **ECS Service Configuration**
- **Cluster**: Create or use existing ECS cluster
- **Service**: Fargate launch type
- **Desired tasks**: 2 (for high availability)
- **Load balancer**: ALB with target group on port 8081
- **Auto-scaling**: Target tracking on CPU/memory

#### 5. **RDS PostgreSQL**
- Already configured: `restore-wids-postgres-dev.c9xgkwg1lrq2.ap-south-1.rds.amazonaws.com`
- Ensure security group allows ECS tasks
- Enable automated backups

#### 6. **S3 Bucket**
- Bucket: `wids-dev-ap-south-1-config`
- Enable versioning (optional)
- Configure lifecycle policies for old configs
- IAM policy for ECS task role:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:PutObject", "s3:GetObject", "s3:DeleteObject"],
      "Resource": "arn:aws:s3:::wids-dev-ap-south-1-config/*"
    }
  ]
}
```

---

## 💻 Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- AWS Account (for S3)
- Airflow instance (optional for testing)

### Local Setup

1. **Clone Repository**
```bash
git clone <repository-url>
cd PipelineDashboard
```

2. **Configure Database**
- Create PostgreSQL database: `wids_platform_control`
- Update `application.properties` with credentials

3. **Configure AWS**
- Set AWS credentials in `application.properties` or environment variables
- Ensure S3 bucket exists

4. **Build Application**
```bash
mvn clean install
```

5. **Run Application**
```bash
mvn spring-boot:run
```

Application will start on `http://localhost:8081`

### Development Tips

- **Hot reload**: Use Spring DevTools
- **Database migration**: Hibernate auto-update (ddl-auto=update)
- **Testing**: Use H2 in-memory database for unit tests
- **API testing**: Postman collection available

---

## 📚 Best Practices

### 1. **Coding Standards**
- Use Lombok for boilerplate reduction
- Follow REST API conventions
- Use DTOs for API contracts
- Implement proper exception handling

### 2. **Configuration Management**
- Store large configs in S3, not database
- Cache S3 content in database for performance
- Use enums for type safety (`ConfigType`, `StepType`, `ApprovalStatus`)

### 3. **Database**
- Use `@Transactional` for multi-step operations
- Enable cascade operations for parent-child relationships
- Use `orphanRemoval=true` for automatic cleanup

### 4. **S3 Storage**
- Generate unique keys with UUID
- Set appropriate content types (application/json, text/yaml)
- Implement cleanup on delete

### 5. **Security**
- Never commit AWS credentials to Git
- Use environment variables for sensitive data
- Implement proper CORS configuration
- Consider adding authentication/authorization

### 6. **API Design**
- Use consistent response structures
- Implement proper HTTP status codes
- Provide meaningful error messages
- Version APIs if needed (future)

---

## 🔧 Troubleshooting

### Common Issues

#### 1. **Database Permission Errors**
```
ERROR: must be owner of table pipeline_config_approval
```
**Solution:** Connect to PostgreSQL with owner account or grant permissions:
```sql
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "wids-ui";
```

#### 2. **AWS Credentials Error**
```
Unable to load credentials from any provider
```
**Solution:** 
- Set `aws.accessKeyId` and `aws.secretAccessKey` in properties
- Or use AWS environment variables
- Or configure AWS CLI profile

#### 3. **S3 Bucket Not Found**
```
The specified bucket does not exist (Status Code: 404)
```
**Solution:** Create bucket or update `aws.s3.bucket` property

#### 4. **Airflow Connection Issues**
```
AirflowConnectionException: Could not extract response
```
**Solution:**
- Verify Airflow URL is correct
- Check authentication credentials
- Ensure Airflow API is enabled
- Verify network connectivity

#### 5. **JSON/YAML Parsing Issues**
- Ensure content is properly formatted
- Backend stores as-is (no escaping needed from UI)
- Content type is set based on `ConfigType`

### Logging

Enable debug logging for troubleshooting:
```properties
logging.level.com.pipeline=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## 📝 Additional Notes

### Enum Display Names

The application uses a `getDisplayName()` method in enums to show user-friendly names:

```java
// StepType enum
DATA_INGESTION → "Data Ingestion"
MACHINE_LEARNING → "Machine Learning"
```

### Cascade and Orphan Removal

```java
@OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
private List<PipelineStep> steps;
```

- **Cascade**: When pipeline is deleted, all steps are deleted
- **Orphan Removal**: If a step is removed from list, it's deleted from DB

### Dual Database Architecture

The application uses PostgreSQL for both:
1. **Application data** (pipelines, steps, configurations)
2. **Workflow thresholds** (separate table for DE team)

Single database connection with multiple tables.

---

## 🎓 Knowledge Transfer Checklist

### For New Developers

- [ ] Understand Spring Boot architecture
- [ ] Review entity relationships (Pipeline → Steps)
- [ ] Understand S3 storage mechanism
- [ ] Review Airflow integration (JWT auth)
- [ ] Understand approval workflow states
- [ ] Know how to test APIs with Postman
- [ ] Understand deployment process
- [ ] Review error handling patterns
- [ ] Understand enum display name pattern
- [ ] Review configuration parameter system

### For DevOps

- [ ] Understand AWS infrastructure requirements
- [ ] Know ECS/Fargate deployment process
- [ ] Configure CloudWatch logging
- [ ] Set up secrets in AWS Secrets Manager
- [ ] Configure ALB and target groups
- [ ] Set up auto-scaling policies
- [ ] Configure RDS backups
- [ ] Set S3 bucket policies
- [ ] Monitor application metrics
- [ ] Understand rollback procedures

### For QA

- [ ] Test all API endpoints
- [ ] Validate S3 storage/retrieval
- [ ] Test Airflow integration
- [ ] Validate approval workflow states
- [ ] Test edge cases (empty configs, large files)
- [ ] Validate error handling
- [ ] Test concurrent operations
- [ ] Validate data consistency
- [ ] Performance testing
- [ ] Security testing

---

## 📞 Support & Contacts

### Key Contacts
- **Development Team**: [Team email/Slack channel]
- **DevOps Team**: [Team email/Slack channel]
- **Database Admin**: [DBA contact]

### Resources
- **Confluence**: [Documentation link]
- **Jira**: [Project link]
- **Git Repository**: [Repository URL]
- **Postman Collection**: [Collection link]

---

## 📄 License

[Your License Information]

---

**Document Version**: 1.0  
**Last Updated**: February 27, 2026  
**Maintained By**: Development Team
