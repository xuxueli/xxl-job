# Docker Deployment Guide for XXL-JOB Admin

## Overview

The XXL-JOB Admin Docker image now supports automatic database initialization through environment variables, making deployment easier and more flexible.

## Features

- **Automatic Database Initialization**: Optionally create and initialize the database schema on first startup
- **Environment Variable Configuration**: Configure database connection using environment variables
- **Backward Compatible**: Still supports the traditional PARAMS-based configuration

## Environment Variables

### Database Initialization

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_INIT` | `false` | Enable automatic database initialization. Set to `true` to auto-create database and tables if they don't exist |
| `DB_HOST` | `127.0.0.1` | MySQL server hostname |
| `DB_PORT` | `3306` | MySQL server port |
| `DB_DATABASE` | `xxl_job` | Database name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root_pwd` | Database password |

### Application Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `PARAMS` | `""` | Additional Spring Boot parameters (optional) |
| `JAVA_OPTS` | `""` | JVM options (e.g., `-Xms128m -Xmx128m`) |
| `LOG_HOME` | `/data/applogs` | Log directory path |

## Usage Examples

### 1. Using Environment Variables (Recommended)

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: root_pwd
      MYSQL_DATABASE: xxl_job
    volumes:
      - mysql-data:/var/lib/mysql
    
  xxl-job-admin:
    image: ghcr.io/dev-zapi/xxl-job/xxl-job-admin:latest
    environment:
      # Enable automatic database initialization
      DB_INIT: "true"
      # Database connection settings
      DB_HOST: mysql
      DB_PORT: "3306"
      DB_DATABASE: xxl_job
      DB_USERNAME: root
      DB_PASSWORD: root_pwd
    ports:
      - "8080:8080"
    depends_on:
      - mysql

volumes:
  mysql-data:
```

### 2. Using PARAMS (Traditional Method)

```yaml
xxl-job-admin:
  image: ghcr.io/dev-zapi/xxl-job/xxl-job-admin:latest
  environment:
    PARAMS: >-
      --spring.datasource.url=jdbc:mysql://mysql:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
      --spring.datasource.username=root
      --spring.datasource.password=root_pwd
  ports:
    - "8080:8080"
```

### 3. Docker Run Command

```bash
docker run -d \
  --name xxl-job-admin \
  -p 8080:8080 \
  -e DB_INIT=true \
  -e DB_HOST=mysql \
  -e DB_PORT=3306 \
  -e DB_DATABASE=xxl_job \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root_pwd \
  ghcr.io/dev-zapi/xxl-job/xxl-job-admin:latest
```

## Database Initialization Process

When `DB_INIT=true`, the container will:

1. Wait for the database server to be ready
2. Check if the database exists; create it if it doesn't
3. Check if tables exist in the database
4. If no tables exist, run the SQL initialization script (`tables_xxl_job.sql`)
5. Start the XXL-JOB Admin application

**Note**: Database initialization only runs when the database or tables don't exist. Existing data will not be modified.

## GitHub Container Registry

The Docker image is automatically built and published to GitHub Container Registry (GHCR) when changes are pushed to the master branch.

### Pulling the Image

```bash
docker pull ghcr.io/dev-zapi/xxl-job/xxl-job-admin:latest
```

### Available Tags

- `latest` - Latest build from master branch
- `sha-<commit>` - Specific commit SHA
- `v*` - Version tags (e.g., `v3.3.2`)

## Building Locally

To build the Docker image locally:

```bash
# Build the application
mvn clean package -DskipTests

# Build the Docker image
cd xxl-job-admin
docker build -t xxl-job-admin:local .
```

## Troubleshooting

### Database Connection Issues

If the application fails to connect to the database:

1. Verify the database is running and accessible
2. Check the `DB_HOST`, `DB_PORT`, `DB_USERNAME`, and `DB_PASSWORD` values
3. Ensure the database user has proper permissions

### Database Initialization Fails

If database initialization fails:

1. Check that `DB_INIT=true` is set
2. Verify the database user has CREATE DATABASE and CREATE TABLE permissions
3. Review container logs: `docker logs <container-name>`

### Application Fails to Start

Check the logs for detailed error messages:

```bash
docker logs xxl-job-admin
```

## Security Considerations

- **Never expose the database password in version control**
- Use Docker secrets or environment variables from a secure source
- Limit database user permissions to only what's necessary
- Use strong passwords for production deployments

## Migration from Previous Versions

If you're upgrading from a previous version that used PARAMS for database configuration:

1. The old method still works - no immediate changes required
2. To migrate, replace PARAMS-based database config with environment variables
3. Set `DB_INIT=false` if you have an existing database

Example migration:

**Before:**
```yaml
environment:
  PARAMS: "--spring.datasource.url=jdbc:mysql://mysql:3306/xxl_job --spring.datasource.username=root --spring.datasource.password=root_pwd"
```

**After:**
```yaml
environment:
  DB_INIT: "false"  # Don't re-initialize existing database
  DB_HOST: mysql
  DB_PORT: "3306"
  DB_DATABASE: xxl_job
  DB_USERNAME: root
  DB_PASSWORD: root_pwd
```
