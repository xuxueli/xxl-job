#!/bin/bash
set -e

echo "Starting XXL-JOB Admin entrypoint script..."

# Default values
DB_INIT=${DB_INIT:-false}
DB_HOST=${DB_HOST:-127.0.0.1}
DB_PORT=${DB_PORT:-3306}
DB_DATABASE=${DB_DATABASE:-xxl_job}
DB_USERNAME=${DB_USERNAME:-root}
DB_PASSWORD=${DB_PASSWORD:-root_pwd}

echo "Database Configuration:"
echo "  DB_INIT: $DB_INIT"
echo "  DB_HOST: $DB_HOST"
echo "  DB_PORT: $DB_PORT"
echo "  DB_DATABASE: $DB_DATABASE"
echo "  DB_USERNAME: $DB_USERNAME"

# Construct JDBC URL if not provided
if [ -z "$SPRING_DATASOURCE_URL" ]; then
    export SPRING_DATASOURCE_URL="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai"
fi

# Set datasource username and password
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

# Database initialization logic
if [ "$DB_INIT" = "true" ]; then
    echo "Database initialization is enabled. Checking database existence..."
    
    # Wait for database to be ready
    echo "Waiting for database to be ready..."
    max_attempts=30
    attempt=0
    until MYSQL_PWD="$DB_PASSWORD" mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USERNAME" -e "SELECT 1" > /dev/null 2>&1; do
        attempt=$((attempt+1))
        if [ $attempt -eq $max_attempts ]; then
            echo "Failed to connect to database after $max_attempts attempts"
            exit 1
        fi
        echo "Database not ready yet, waiting... (attempt $attempt/$max_attempts)"
        sleep 2
    done
    echo "Database connection successful!"
    
    # Check if database exists and has tables
    DB_EXISTS=$(MYSQL_PWD="$DB_PASSWORD" mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USERNAME" -e "SELECT COUNT(*) FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='$DB_DATABASE';" -s -N)
    
    if [ "$DB_EXISTS" -eq "0" ]; then
        echo "Database '$DB_DATABASE' does not exist. Creating database..."
        MYSQL_PWD="$DB_PASSWORD" mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USERNAME" -e "CREATE DATABASE IF NOT EXISTS \`$DB_DATABASE\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    fi
    
    # Check if tables exist in the database
    TABLE_COUNT=$(MYSQL_PWD="$DB_PASSWORD" mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USERNAME" -D"$DB_DATABASE" -e "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='$DB_DATABASE';" -s -N 2>/dev/null || echo "0")
    
    if [ "$TABLE_COUNT" -eq "0" ]; then
        echo "Database '$DB_DATABASE' exists but has no tables. Running initialization script..."
        if [ -f /docker-entrypoint-initdb.d/tables_xxl_job.sql ]; then
            MYSQL_PWD="$DB_PASSWORD" mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USERNAME" "$DB_DATABASE" < /docker-entrypoint-initdb.d/tables_xxl_job.sql
            echo "Database initialization completed successfully!"
        else
            echo "ERROR: SQL initialization file not found at /docker-entrypoint-initdb.d/tables_xxl_job.sql"
            exit 1
        fi
    else
        echo "Database '$DB_DATABASE' already exists with $TABLE_COUNT tables. Skipping initialization."
    fi
else
    echo "Database initialization is disabled (DB_INIT=$DB_INIT). Skipping database checks."
fi

# Build PARAMS for Spring Boot application
SPRING_PARAMS="--spring.datasource.url=${SPRING_DATASOURCE_URL} --spring.datasource.username=${SPRING_DATASOURCE_USERNAME} --spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}"

# Append any additional PARAMS passed via environment variable
if [ -n "$PARAMS" ]; then
    SPRING_PARAMS="$SPRING_PARAMS $PARAMS"
fi

echo "Starting XXL-JOB Admin application..."
echo "Command: java ${LOG_HOME:+-DLOG_HOME=$LOG_HOME} -jar $JAVA_OPTS /app.jar $SPRING_PARAMS"

# Execute the Java application
exec java ${LOG_HOME:+-DLOG_HOME=$LOG_HOME} -jar $JAVA_OPTS /app.jar $SPRING_PARAMS
