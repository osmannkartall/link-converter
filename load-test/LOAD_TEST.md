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
         ↳  99% — ✓ 999074 / ✗ 926

     █ Step 2: Resolve url and deeplink using shortlink

        ✗ GET /link_conversions - status is 200
         ↳  99% — ✓ 999067 / ✗ 16

    checks.........................: 99.95%  ✓ 1998141    ✗ 942
    data_received..................: 378 MB  225 kB/s
    data_sent......................: 402 MB  239 kB/s
    group_duration.................: avg=768.2ms  min=103.73ms   med=1s     max=30.5s    p(90)=1s      p(95)=1.01s
    http_req_blocked...............: avg=892.07µs min=0s         med=1.08µs max=19.74s   p(90)=2.87µs  p(95)=3.95µs
    http_req_connecting............: avg=859.04µs min=0s         med=0s     max=19.74s   p(90)=0s      p(95)=0s
    http_req_duration..............: avg=3.79ms   min=0s         med=2.07ms max=999.65ms p(90)=6.86ms  p(95)=10.27ms
    { expected_response:true }...: avg=3.79ms   min=265.66µs   med=2.07ms max=999.65ms p(90)=6.86ms  p(95)=10.27ms
    http_req_failed................: 0.04%   ✓ 942        ✗ 1998141
    http_req_receiving.............: avg=29.47µs  min=-2231348ns med=9.37µs max=173.25ms p(90)=42.33µs p(95)=80.62µs
    http_req_sending...............: avg=15.36µs  min=-2096096ns med=4.75µs max=48.41ms  p(90)=16.87µs p(95)=44.33µs
    http_req_tls_handshaking.......: avg=0s       min=0s         med=0s     max=0s       p(90)=0s      p(95)=0s
    http_req_waiting...............: avg=3.74ms   min=0s         med=2.03ms max=999.48ms p(90)=6.79ms  p(95)=10.18ms
    http_reqs......................: 1999083 1189.48315/s
    iteration_duration.............: avg=1.53s    min=103.86ms   med=1.5s   max=31.51s   p(90)=1.51s   p(95)=1.52s
    iterations.....................: 1000000 595.014389/s
    vus............................: 6       min=6        max=1000
    vus_max........................: 1000    min=1000     max=1000


running (0h28m00.6s), 0000/1000 VUs, 1000000 complete and 0 interrupted iterations
shortlinkConversionAndRetri... ✓ [ 100% ] 1000 VUs  0h27m22.8s/2h0m0s  1000000/1000000 iters, 1000 per VU
```

### `shortlink-read-load-test.js` Results

```text
    ✗ is status 200
     ↳  99% — ✓ 711651 / ✗ 179

    checks.........................: 99.97% ✓ 711651     ✗ 179
    data_received..................: 150 MB 179 kB/s
    data_sent......................: 117 MB 140 kB/s
    http_req_blocked...............: avg=287.76µs min=0s       med=1.66µs  max=19.71s  p(90)=3.29µs  p(95)=4.75µs
    http_req_connecting............: avg=284.8µs  min=0s       med=0s      max=19.71s  p(90)=0s      p(95)=0s
    http_req_duration..............: avg=3.62ms   min=0s       med=2.34ms  max=2.18s   p(90)=5.4ms   p(95)=7.74ms
    { expected_response:true }...: avg=3.62ms   min=288.83µs med=2.34ms  max=2.18s   p(90)=5.4ms   p(95)=7.74ms
    http_req_failed................: 0.02%  ✓ 179        ✗ 711651
    http_req_receiving.............: avg=32.28µs  min=0s       med=16.41µs max=20.61ms p(90)=64.75µs p(95)=99.41µs
    http_req_sending...............: avg=10.33µs  min=0s       med=6.37µs  max=19.34ms p(90)=13.5µs  p(95)=20.54µs
    http_req_tls_handshaking.......: avg=0s       min=0s       med=0s      max=0s      p(90)=0s      p(95)=0s
    http_req_waiting...............: avg=3.58ms   min=0s       med=2.3ms   max=2.18s   p(90)=5.34ms  p(95)=7.66ms
    http_reqs......................: 711830 846.912887/s
    iteration_duration.............: avg=1.01s    min=1s       med=1s      max=31.01s  p(90)=1s      p(95)=1s
    iterations.....................: 711830 846.912887/s
    vus............................: 4      min=4        max=1000
    vus_max........................: 1000   min=1000     max=1000


running (14m00.5s), 0000/1000 VUs, 711830 complete and 0 interrupted iterations
default ✓ [ 100% ] 0000/1000 VUs  14m0s
```

### Notes

- `time="2024-09-23T08:34:55Z" level=warning msg="Request Failed" error="Post \"http://host.docker.internal:8080/link_conversions/shortlinks\": dial: i/o timeout"`

    The failed requests are not related to the web application but to the performance of the host machine. This can be verified by checking for records in the `link-conversion-failure` bucket in Couchbase.