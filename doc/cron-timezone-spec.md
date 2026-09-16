# Spec: Cron task timezone

## Objective

Allow an administrator to select an IANA timezone when configuring a CRON task. The CRON expression keeps the wall-clock meaning of that timezone, including daylight-saving transitions.

## Contract

- `scheduleTimezone` is an optional task field containing an IANA timezone ID such as `Asia/Shanghai`.
- CRON tasks use `scheduleTimezone` when calculating trigger instants.
- A missing timezone retains the historical behavior and uses the scheduler JVM default timezone.
- Non-CRON schedule types ignore the field.
- Invalid timezone IDs are rejected at the web and OpenAPI service boundary.

## User interface

- Add, edit, and copy flows use a dropdown populated from the timezone IDs supported by the scheduler JVM.
- The add form defaults to the scheduler JVM timezone.
- The edit and copy forms preserve the task timezone, falling back to the scheduler JVM timezone for historical tasks.
- The next-trigger-time preview uses the task timezone.

## Persistence

- Add nullable column `xxl_job_info.schedule_timezone VARCHAR(64)`.
- Existing rows remain `NULL` and therefore retain scheduler-default behavior.

## Commands

- Unit tests: `mvn -pl xxl-job-admin -DskipTests=false test`
- Build: `mvn -DskipTests package`

## Project structure and style

- Java behavior and tests follow the existing `xxl-job-admin` package layout and JUnit 5 conventions.
- The FreeMarker UI remains within `templates/business/job.list.ftl` and uses the existing Bootstrap form styles.
- Database bootstrap and upgrade SQL remain under `doc/db`.

## Boundaries

- Always: validate externally supplied timezone IDs and preserve null compatibility.
- Ask first: change the meaning of existing CRON expressions or introduce dependencies.
- Never: silently replace invalid timezone IDs with GMT or rewrite CRON fields using a fixed UTC offset.

## Success criteria

- A CRON task configured for `09:00 America/New_York` fires at the corresponding instant in both standard time and daylight time.
- Invalid timezone IDs cannot be saved.
- Existing tasks with no timezone calculate exactly as before.
- The timezone dropdown is present in add and edit forms and is preserved by copy.

