# data-model.md

## Entities

### INFO
- Description: Canonical information record stored by Service B.
- Fields:
  - id: UUID (generated) [Optional primary key if multiple records needed in future]
  - name: string (required, not null)
  - description: string (nullable, max length 2000 recommended)
  - version: string (required, semantic version recommended)
  - updated_at: timestamp (auto-updated)

## Validation Rules
- name: non-empty, max length 255
- description: optional, max length 2000
- version: semantic version regex optional validation (e.g., ^\d+\.\d+\.\d+(-.+)?$)

## Relationships
- None for v1 — single canonical INFO record. If multi-tenant or multi-record is needed later, use a natural primary key or UUID and add tenant_id.

## Storage
- Table: info
- Columns: id (UUID PK) / name / description / version / updated_at

## Migration
- Use Flyway for v1 migration creating the `info` table with appropriate constraints.

## Notes
- The spec currently expects a single INFO record; implementation can store a single row and expose GET /info to return the most recent or the row with a reserved name (e.g., 'default').
