#!/bin/bash

# Configurable variables
MYSQL_PORT=3306
DOCKER_COMPOSE_DIR="/vagrant/tp-foyer"
MYSQL_CONTAINER_NAME="tp-foyer-mysqldb-1"
DB_NAME="achat_db"
MYSQL_ROOT_PASSWORD="WalaAmiri20!"

echo "===> Step 1: Kill any process blocking port $MYSQL_PORT"
sudo lsof -ti tcp:$MYSQL_PORT | xargs -r sudo kill -9

echo "===> Step 2: Go to Docker Compose directory"
cd $DOCKER_COMPOSE_DIR || { echo "Directory not found! Exiting."; exit 1; }

echo "===> Step 3: Take down Docker Compose, remove volumes"
docker compose down -v

echo "===> Step 4: Clean dangling containers and volumes"
docker container prune -f
docker volume prune -f

echo "===> Step 5: Build Docker images (no cache)"
docker compose build --no-cache

echo "===> Step 6: Start Docker Compose stack in detached mode"
docker compose up -d

echo "===> Step 7: Wait for MySQL port to be open (max 120s)"
timeout=120
while ! docker exec "$MYSQL_CONTAINER_NAME" mysqladmin ping -uroot -p$MYSQL_ROOT_PASSWORD --silent &>/dev/null; do
  echo "Waiting for MySQL to be ready... ($timeout)"
  sleep 5
  ((timeout -= 5))
  if [[ $timeout -le 0 ]]; then
    echo "ERROR: MySQL is not responding in time. Check container logs:"
    docker logs $MYSQL_CONTAINER_NAME
    exit 1
  fi
done

echo "MySQL is ready."

echo "===> Step 8: Verify if database '$DB_NAME' exists, create if missing"
DB_EXISTS=$(docker exec -i $MYSQL_CONTAINER_NAME mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "SHOW DATABASES LIKE '$DB_NAME';" | grep "$DB_NAME" || true)

if [ -z "$DB_EXISTS" ]; then
  echo "Database $DB_NAME does not exist, creating..."
  docker exec -i $MYSQL_CONTAINER_NAME mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
  echo "Database $DB_NAME created."
else
  echo "Database $DB_NAME already exists."
fi

echo "===> Step 9: List databases inside MySQL to verify"
docker exec -i $MYSQL_CONTAINER_NAME mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "SHOW DATABASES;"

echo "===> Step 10: Run Maven tests with 'test' profile"
mvn clean test -Ptest

echo "===> All done! Check test results above."
