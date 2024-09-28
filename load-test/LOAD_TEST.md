# Load Test

The load tests are executed using [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/#docker).

Set the following environment variables before running the tests:

```bash
export SPRING_COUCHBASE_CONNECTION_STRING="couchbase://couchbase-db"
export SPRING_COUCHBASE_USERNAME="Administrator"
export SPRING_COUCHBASE_PASSWORD="password"
export SPRING_COUCHBASE_BUCKET_NAME="link-conversion"
export COUCHBASE_ADMINISTRATOR_USERNAME="Administrator"
export COUCHBASE_ADMINISTRATOR_PASSWORD="password"
export SPRING_PROFILES_ACTIVE="couchbase"
```

## `smoke-test.js`

### Steps

**1. Create the Couchbase instance.**

```bash
./start-with-couchbase.sh
```

**2. Run the test.**

```bash
docker run --rm -i grafana/k6 run - <load-test/smoke-test.js
```

**3. Remove the instance and volumes after the test completes.**

```bash
docker compose -f docker-compose-couchbase.yml --profile prod down -v
```

## `shortlink-conversion-and-retrieval-load-test.js`

### Description

Creates shortlinks for urls and deeplinks, and retrieves the generated shortlinks.

### Steps

**1. Create the Couchbase instance.**

```bash
./start-with-couchbase.sh
```

**2. Run the test.**

```bash
docker run --rm -i grafana/k6 run - <load-test/shortlink-conversion-and-retrieval-load-test.js
```

**3. Remove the instance and volumes after the test completes.**

```bash
docker compose -f docker-compose-couchbase.yml --profile prod down -v
```

## `shortlink-read-load-test.js`

### Description

Selects shortlinks from 10 million records.

### Steps

**1. Generate 10 JSON files, each containing 1 million random records, in the `load-test/data` folder.**

```bash
node load-test/random-record-generator.js
```

**2. Create the Couchbase instance.**

```bash
./start-with-couchbase.sh
```

**3. Import the 10 million records.**

```bash
./import-couchbase-data.sh
```

**4. Wait for index building to complete.**

```text
Open `localhost:8091` in your browser.

Log in with Username: Administrator, Password: password

Navigate to the "Indexes" tab and wait for the remaining mutations to finish. Otherwise, even though the records exist in the database, it may return as "no record found" due to ongoing index mutations.
```

**5. Run the test.**

```bash
docker run --rm -i -v "$PWD/load-test/data/shortlinks.json:/home/k6/shortlinks.json" grafana/k6 run - <load-test/shortlink-read-load-test.js
```

**6. Remove the instance and volumes after the test completes.**

```bash
docker compose -f docker-compose-couchbase.yml --profile prod down -v
```

**7. Remove the generated test data.**

```bash
rm -rf load-test/data
```

## Load Test Report

### Test Runner Info

**Host:** MacBook Air M2 - 16GB RAM, 1 TB SSD

**Docker Resources:** CPU Limit: 8, Memory Limit: 8GB, Swap: 1 GB, Virtual Disk Limit: 64 GB

### `shortlink-conversion-and-retrieval-load-test.js` Results

```text
    █ Step 1: Create Shortlink from url

    ✗ POST /link_conversions - status is 200
    ↳  99% — ✓ 999476 / ✗ 524

    █ Step 2: Resolve url and deeplink using shortlink

    ✗ GET /link_conversions - status is 200
    ↳  99% — ✓ 999177 / ✗ 299

    checks.........................: 99.95%  ✓ 1998653     ✗ 823
    data_received..................: 377 MB  212 kB/s
    data_sent......................: 401 MB  225 kB/s
    group_duration.................: avg=769.61ms min=60.77ms   med=1s      max=1m0s    p(90)=1s      p(95)=1s
    http_req_blocked...............: avg=752.3µs  min=0s        med=1.33µs  max=19.89s  p(90)=2.79µs  p(95)=4.16µs
    http_req_connecting............: avg=722.78µs min=0s        med=0s      max=19.89s  p(90)=0s      p(95)=0s
    http_req_duration..............: avg=5.99ms   min=0s        med=2.22ms  max=1m6s    p(90)=6.62ms  p(95)=9.2ms
    { expected_response:true }...: avg=5.87ms   min=17.29µs   med=2.23ms  max=1m6s    p(90)=6.62ms  p(95)=9.2ms
    http_req_failed................: 0.04%   ✓ 823         ✗ 1998653
    http_req_receiving.............: avg=33.37µs  min=-21806ns  med=13.62µs max=45.94ms p(90)=62.08µs p(95)=105.29µs
    http_req_sending...............: avg=11.69µs  min=-211347ns med=5.62µs  max=38.2ms  p(90)=13.62µs p(95)=21.58µs
    http_req_tls_handshaking.......: avg=0s       min=0s        med=0s      max=0s      p(90)=0s      p(95)=0s
    http_req_waiting...............: avg=5.95ms   min=0s        med=2.18ms  max=1m6s    p(90)=6.56ms  p(95)=9.12ms
    http_reqs......................: 1999476 1124.768265/s
    iteration_duration.............: avg=1.53s    min=60.83ms   med=1.5s    max=1m1s    p(90)=1.51s   p(95)=1.51s
    iterations.....................: 1000000 562.531516/s
    vus............................: 1       min=1         max=1000
    vus_max........................: 1000    min=1000      max=1000


running (0h29m37.7s), 0000/1000 VUs, 1000000 complete and 0 interrupted iterations
shortlinkConversionAndRetri... ✓ [ 100% ] 1000 VUs  0h28m06.5s/2h0m0s  1000000/1000000 iters, 1000 per VU
```

### `shortlink-read-load-test.js` Results

```text

    ✗ is status 200
    ↳  99% — ✓ 709409 / ✗ 241

    checks.........................: 99.96% ✓ 709409     ✗ 241
    data_received..................: 150 MB 178 kB/s
    data_sent......................: 117 MB 139 kB/s
    http_req_blocked...............: avg=2.04ms  min=0s       med=1.66µs max=19.75s   p(90)=3.83µs  p(95)=5.41µs
    http_req_connecting............: avg=2.03ms  min=0s       med=0s     max=19.75s   p(90)=0s      p(95)=0s
    http_req_duration..............: avg=2.57ms  min=0s       med=1.98ms max=159.88ms p(90)=4.75ms  p(95)=6.93ms
    { expected_response:true }...: avg=2.57ms  min=298.37µs med=1.98ms max=159.88ms p(90)=4.75ms  p(95)=6.93ms
    http_req_failed................: 0.03%  ✓ 241        ✗ 709409
    http_req_receiving.............: avg=31.42µs min=0s       med=15.7µs max=16.81ms  p(90)=62.87µs p(95)=98.25µs
    http_req_sending...............: avg=10.69µs min=0s       med=6.29µs max=80.24ms  p(90)=13.79µs p(95)=21.5µs
    http_req_tls_handshaking.......: avg=0s      min=0s       med=0s     max=0s       p(90)=0s      p(95)=0s
    http_req_waiting...............: avg=2.53ms  min=0s       med=1.94ms max=143.9ms  p(90)=4.69ms  p(95)=6.85ms
    http_reqs......................: 709650 844.198694/s
    iteration_duration.............: avg=1.01s   min=1s       med=1s     max=31s      p(90)=1s      p(95)=1s
    iterations.....................: 709649 844.197504/s
    vus............................: 5      min=5        max=1000
    vus_max........................: 1000   min=1000     max=1000


running (14m00.6s), 0000/1000 VUs, 709649 complete and 1 interrupted iterations
default ✓ [ 100% ] 0000/1000 VUs  14m0s
```

### Notes

- `time="2024-09-23T08:34:55Z" level=warning msg="Request Failed" error="Post \"http://host.docker.internal:8080/link_conversions/shortlinks\": dial: i/o timeout"`

    The failed requests are not related to the web application but to the performance of the host machine. This can be verified by checking for records in the `link-conversion-failure` bucket in Couchbase.