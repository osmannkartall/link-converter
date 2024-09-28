import http from 'k6/http';
import { check, group, sleep } from 'k6';

function getRandomNumber(digits) {
    return Math.floor(Math.random() * (Math.pow(10, digits) - Math.pow(10, digits - 1)) + Math.pow(10, digits - 1));
}

const BASE_URL = "http://link-converter-app-service/link_conversions";

export const options = {
    scenarios: {
        shortlinkConversionAndRetrieval: {
            executor: 'per-vu-iterations',
            exec: 'shortlinkConversionAndRetrieval',
            vus: 50,
            iterations: 1,
        },
    },
};

export function shortlinkConversionAndRetrieval() {    
    let shortlink;

    group('Step 1: Create Shortlink from url', () => {
        const id = getRandomNumber(10);
        const url = `https://any.domain.com/item/${id}`;
        const deeplink = `app://item&id=${id}`;

        const createShortlinkResponse = http.post(
            BASE_URL,
            JSON.stringify({ url: url, deeplink: deeplink }),
            {
                headers: {
                    "Content-Type": "application/json",
                },
                tags: { name: 'createShortlink' },
            },
        );

        check(createShortlinkResponse, {
            "POST /link_conversions - status is 200": (r) => r.status === 200,
        });

        if (createShortlinkResponse.json()) {
            shortlink = createShortlinkResponse.json().shortlink;
        }

        // Document might not be immediately visible after a write operation, even though the API
        // responds successfully. Wait for a short time before the next iteration
        sleep(1);
    });

    group('Step 2: Resolve url and deeplink using shortlink', () => {
        let hash = "0";
        
        if (shortlink) {
            const parts = shortlink.split("/");
            hash = parts[parts.length-1];
        }
    
        const resolveShortlinkResponse = http.get(`${BASE_URL}?hash=${hash}`, {
            headers: {
                "Content-Type": "application/json",
            },
            tags: { name: 'retrieveShortlink' },
        });

        check(resolveShortlinkResponse, {
            "GET /link_conversions - status is 200": (r) => r.status === 200,
        });

        // Wait for a short time before the next iteration
        // Sleep: Pauses between iterations to simulate user pacing.
        sleep(0.5);
    });
}
