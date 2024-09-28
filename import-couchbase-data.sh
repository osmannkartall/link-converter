#!/bin/bash

COUCHBASE_CONTAINER="couchbase-db"
HOST_DATA_DIR="load-test/data"
CONTAINER_DATA_DIR="/opt/couchbase/import-data"

echo "> Importing data from JSON files in $CONTAINER_DATA_DIR/..."

for FILE in $HOST_DATA_DIR/records_*.json; do
    echo "> Importing $CONTAINER_DATA_DIR/$FILE into Couchbase..."

    docker exec $COUCHBASE_CONTAINER cbimport json \
        -c couchbase://127.0.0.1 \
        -u Administrator \
        -p password \
        -b link-conversion \
        -d file://$CONTAINER_DATA_DIR/$FILE \
        -f list \
        -g key::#UUID# \
        --threads 4
        # "This parameter defaults to 1 if it is not specified and it is recommended that
        # this parameter is not set to be higher than the number of CPUs on the machine where
        # the import is taking place."
        # https://docs.couchbase.com/server/current/tools/cbimport-json.html
done

echo "> Data import complete."
