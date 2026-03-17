# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 2.7.2 application that generates Word documents for educational training schemes using xdocreport with FreeMarker template engine.

## Build & Run

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Run tests
mvn test
```

Server runs on port 8080 with context path `/flowable-demo`.

## Architecture Highlights

### Training Scheme Document Generation (`/cscy` package)

Uses **xdocreport** library (version 2.0.2) with FreeMarker template engine to generate Word documents from `scheme_document.docx` template.

**Key Classes:**

- `WordUtils` - Utility class for Word document operations (creates titles, headings, paragraphs, tables)
- `TrainingSchemeGenerator` - Core document generator using FreeMarker templating
- `ExportDate` - Wrapper for xdocreport processing, handles table data and context
- `SoMap` - Extends HashMap, converts Java objects to Map for template population
- `MapToObjectUtil` - Converts Map data to Java objects with date/time type support

**Data Models:**
- `TrainingSchemeVo` - Main data model containing:
  - `planName` - Training scheme name
  - `totalHours` - Summary of total hours and credits
  - `standardGraduations` - List of graduation requirements
  - `courses` - List of courses with detailed info
- `TotalHours` -学制、学位、总学时、总学分、浮动比例、各模块学时
- `StandardGraduation` - Graduation standard with hierarchy ( parentId, level, order, leaf)
- `TrainingSchemeCourseVo` - Course details including name, hours, type, attr, mode, etc.

**Template System:**
- Template location: `src/main/resources/template/scheme.docx`
- Replaces `${placeholder}` patterns in the DOCX template
- Table generation requires `FieldsMetadata` registration via `ExportDate.setTable()` or `setTableWithMetadata()`
- Supports nested fields in table columns

### Utilities

- `ExportDate.java` - xdocreport wrapper for document processing
- `SoMap.java` - Object-to-Map converter for template data
- `MapToObjectUtil.java` - Map-to-Object converter with date/time parsing
- `WordUtils.java` - Apache POI XWPF document helper methods

## Configuration

`application.yml` configures:
- MySQL datasource (Spring Boot HikariCP)
- Flowable engine (disabled App/DMN/Form modules for lightweight setup)
- Logging levels for Flowable, JDBC, and Hikari

## Database

Requires MySQL database `flowable_demo` with tables:
- Flowable tables (prefixed with `ACT_`)
- `audit_flow`, `audit_node`, `audit_instance`, `audit_instance_node`, `course`

Note: Audit-related packages mentioned in some docs don't currently exist in the codebase.