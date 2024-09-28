import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = "http://host.docker.internal:8080/link_conversions";

function getRandomNumber(digits) {
    return Math.floor(Math.random() * (Math.pow(10, digits) - Math.pow(10, digits - 1)) + Math.pow(10, digits - 1));
}

const httpHeaders = {
    headers: {
        "Content-Type": "application/json",
    },
};

export const options = {
    vus: 3,
    duration: '1m',
};

export default () => {
    const url = `https://any.domain.com/item/${getRandomNumber(7)}`;

    const res = http.post(
        BASE_URL,
        JSON.stringify({ url: url }),
        httpHeaders,
    );

    check(res, {
        "is status 200": (r) => r.status === 200,
    });

    // Wait for a short time before the next iteration
    // Sleep: Pauses between iterations to simulate user pacing.
    sleep(1);
};
