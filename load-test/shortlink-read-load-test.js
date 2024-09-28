import http from "k6/http";
import { check, sleep } from "k6";
import { SharedArray } from 'k6/data';

const BASE_URL = "http://host.docker.internal:8080/link_conversions";


const data = new SharedArray('shortlinks', function () {
    const f = JSON.parse(open('./shortlinks.json'));
    return f;
});

export const options = {
    stages: [
        { duration: "2m", target: 1000 },
        { duration: "10m", target: 1000 },
        { duration: "2m", target: 0 },
    ],
};

export default () => {
    const shortlink = data[Math.floor(Math.random() * data.length)];
    const parts = shortlink.split("/");

    const res = http.get(`${BASE_URL}?hash=${parts[parts.length-1]}`, {
        headers: {
            "Content-Type": "application/json",
        },
        tags: { name: 'retrieveShortlink' },
    });

    check(res, {
        "is status 200": (r) => r.status === 200,
    });

    // Wait for a short time before the next iteration
    // Sleep: Pauses between iterations to simulate user pacing.
    sleep(1);
};
