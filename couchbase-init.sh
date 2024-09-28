#!/bin/bash

COUCHBASE_CONTAINER="couchbase-db"

# Wait for Couchbase Server to be available
until curl -s http://127.0.0.1:8091/ui/index.html > /dev/null; do
    echo "Waiting for Couchbase Server..."
    sleep 5
done

docker exec $COUCHBASE_CONTAINER couchbase-cli cluster-init -c 127.0.0.1 \
    --cluster-username Administrator \
    --cluster-password password \
    --services data,index,query \
    --cluster-ramsize 4096 \
    --cluster-index-ramsize 1024 \
    --index-storage-setting default
    # When there are millions of records in the db, memopt consumes a lot of RAM.
    # --index-storage-setting memopt

docker exec $COUCHBASE_CONTAINER couchbase-cli bucket-create \
    --cluster 127.0.0.1 \
    --username Administrator \
    --password password \
    --bucket link-conversion \
    --bucket-type couchbase \
    --bucket-ramsize 2048 \
    --bucket-replica 1

docker exec $COUCHBASE_CONTAINER couchbase-cli bucket-create \
    --cluster 127.0.0.1 \
    --username Administrator \
    --password password \
    --bucket link-conversion-failure \
    --bucket-type couchbase \
    --bucket-ramsize 512 \
    --bucket-replica 1

# Rebalance the server before generating indexes
docker exec $COUCHBASE_CONTAINER couchbase-cli rebalance -c 127.0.0.1:8091 --username Administrator --password password

docker exec $COUCHBASE_CONTAINER cbq -s "CREATE INDEX shortlink_idx ON \`link-conversion\`(\`shortlink\`);" -u Administrator -p password -e 127.0.0.1:8093
