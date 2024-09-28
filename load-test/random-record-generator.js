const fs = require("fs");
const path = require("path");

const baseUrl = "https://any.domain.com/item";
const baseDeeplink = "app://item&id="
const baseShortlink = "https://li.con"

function getRandomNumber(digits) {
    return Math.floor(Math.random() * (Math.pow(10, digits) - Math.pow(10, digits - 1)) + Math.pow(10, digits - 1));
}

function generateRecord() {
    const id = getRandomNumber(10);
    const record = {
        _class: "com.osmankartal.link_converter.adapter.persistence.document.LinkConversionDocument",
        url: `${baseUrl}/${id}`,
        deeplink: `${baseDeeplink}${id}`,
        shortlink: `${baseShortlink}/${getRandomNumber(20)}`,
        _created: "2024-09-21T14:30:00",
        _updated: "2024-09-21T14:30:00",
    }

    return record;
}

function extractShortlinks() {
    const recordsFilePath = path.join(dataFolder, "records_1.json");
    const shortlinksFilePath = path.join(dataFolder, "shortlinks.json");

    const readStream = fs.createReadStream(recordsFilePath, { encoding: "utf-8" });
    let fileData = "";

    readStream.on("data", (chunk) => {
        fileData += chunk;
    });

    readStream.on("end", () => {
        try {
            const records = JSON.parse(fileData);
            const shortlinks = records.map((record) => record.shortlink);

            const writeStream = fs.createWriteStream(shortlinksFilePath);

            writeStream.write(JSON.stringify(shortlinks, null, 2));

            writeStream.end(() => {
                console.log(`Shortlinks extracted and saved to ${shortlinksFilePath}`);
            });
        } catch (err) {
            console.error("Error parsing or writing shortlinks:", err);
        }
    });

    readStream.on("error", (err) => {
        console.error("Error reading records_1.json:", err);
    });
}

function writeBatch(batchNum) {
    const records = [];
    const start = batchNum * batchSize;
    const end = Math.min(start + batchSize, count);

    for (let i = start; i < end; i++) {
        records.push(generateRecord());
    }

    console.log("writing batch " + (batchNum + 1) + "...");

    const batchFilePath = path.join(dataFolder, `records_${batchNum + 1}.json`);
    const writeStream = fs.createWriteStream(batchFilePath);

    writeStream.write(JSON.stringify(records, null, 2));

    writeStream.end(() => {
        console.log(`Batch ${batchNum + 1} saved to ${batchFilePath}`);
        
        if (end < count) {
            setImmediate(() => writeBatch(batchNum + 1));
        } else {
            console.log("All batches saved.");
            extractShortlinks();
        }
    });
}

const count = 10_000_000;
const batchSize = 1_000_000;

console.log("number of total examples:", count);
console.log("batch size:", batchSize);
console.log("number of batches:", count / batchSize);

const dataFolder = path.join(__dirname, "data");

if (!fs.existsSync(dataFolder)) {
    fs.mkdirSync(dataFolder);
}

writeBatch(0);