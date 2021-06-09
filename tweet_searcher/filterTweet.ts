import TwitterApi from "twitter-api-v2";
import * as dotenv from "dotenv";
import * as fs from "fs";
import { Kafka } from "kafkajs";
import { SchemaRegistry, readAVSCAsync } from "@kafkajs/confluent-schema-registry";

process.setMaxListeners(0);
dotenv.config();

const TOPIC = "tweets_livbur_2";
const QUERY: string = "liverpool OR burnley OR BURLIV";
//const QUERY: string = "liverpool";
const REQ_LIMIT: number = 400;
const TIME_LIMIT: number = 14 * 60 * 1000;

//let startDate = new Date(1621468800000); //20 maj 00:00:00 GMT;
let startDate = new Date(1621479000000); //20 maj 02:50:00 GMT;
let endDate = new Date(1621486200000); //20 maj 04:50:00 GMT
//let endDate = new Date(1621855680000);
console.log(startDate);

// Instanciate with desired auth type (here's Bearer v2 auth)
const twitterClient = new TwitterApi(process.env.ACCESS_TOKEN);
// configure Kafka broker
const kafka = new Kafka({
    brokers: ["localhost:9092"],
});
const registry = new SchemaRegistry({
    host: "http://localhost:8081",
});

const producer = kafka.producer();

const registerSchema = async () => {
    try {
        const schema = await readAVSCAsync("./tweet.avsc");
        const { id } = await registry.register(schema);
        return id;
    } catch (e) {
        console.log(e);
    }
};

/* twitterClient.v2
    .get(
        "tweets/search/recent?query=covid&max_results=10&expansions=author_id&tweet.fields=created_at,text&user.fields=id,username,description"
    )
    .then((res) => {
        res.data.forEach((element, index) => {
            console.log(typeof element.id);
            produce({
                text: element.text,
                user_id: 0,
                id: 0,
                reply_count: 0,
                retweet_count: 0,
                favorite_count: 0,
                created_at: element.created_at,
                username: res.includes.users[index].username,
                quote_count: 0,
            })
                .then()
                .catch((err) => {
                    console.log(err);
                });
        });
        console.log(res);
        console.log(res.includes.users);
    })
    .catch((err) => {
        console.log(err);
    }); */

const main = async () => {
    let reqCount = 0;
    let start = new Date().getTime();
    let elapsed = 0;
    while (true) {
        if (startDate > endDate) {
            console.log(startDate, endDate);
            console.log("brek");
            break;
        }
        if (elapsed > TIME_LIMIT + 1000) {
            fs.appendFileSync(
                "log.txt",
                `[${new Date().toLocaleString()}] Resetting request and elapsed, elapsed: ${
                    elapsed / 10000
                } request: ${reqCount}\n`
            );
            reqCount = 0;
            elapsed = 0;
        }
        if (reqCount < REQ_LIMIT && elapsed < TIME_LIMIT) {
            let endDate = new Date(startDate.getTime() + 60000);
            fs.appendFileSync(
                "log.txt",
                `[${new Date().toLocaleString()}] Making request: start time: [${startDate.toLocaleString()}], end time: ${endDate.toLocaleString()}\n`
            );
            await twitterRequest(startDate, endDate, QUERY)
                .then((res) => {
                    fs.appendFileSync(
                        "log.txt",
                        `[${new Date().toLocaleString()}] Twitter request successful\n`
                    );

                    if (res.data && res.data.length > 0 && res.includes.users) {
                        res.data.forEach(async (element, index) => {
                            //1514811400000000
                            //1381621901399834600
                            await produce({
                                text: element.text,
                                user_id: 0,
                                id: 0,
                                reply_count: 0,
                                retweet_count: 0,
                                favorite_count: 0,
                                created_at: element.created_at,
                                username: res.includes.users[index]
                                    ? res.includes.users[index].username
                                    : "",
                                quote_count: 0,
                            })
                                .then((_) => {
                                    fs.appendFileSync(
                                        "log.txt",
                                        `[${new Date().toLocaleString()}] Kafka write successful\n`
                                    );
                                })
                                .catch((err) => {
                                    fs.appendFileSync(
                                        "log.txt",
                                        `[${new Date().toLocaleString()}] Kafka write error: ${err}\n`
                                    );
                                });
                        });
                    }
                })
                .catch((err) => {
                    fs.appendFileSync(
                        "log.txt",
                        `[${new Date().toLocaleString()}] [ERR] Error twitter request: ${err}\n`
                    );
                });
            reqCount++;
            startDate = endDate;
        }
        elapsed += new Date().getTime() - start;
        start = new Date().getTime();
    }
};

function ISODateString(d) {
    function pad(n) {
        return n < 10 ? "0" + n : n;
    }
    return (
        d.getUTCFullYear() +
        "-" +
        pad(d.getUTCMonth() + 1) +
        "-" +
        pad(d.getUTCDate()) +
        "T" +
        pad(d.getUTCHours()) +
        ":" +
        pad(d.getUTCMinutes()) +
        ":" +
        pad(d.getUTCSeconds()) +
        "Z"
    );
}

// This will create an AVRO schema from an .avsc file

function twitterRequest(startDate: Date, endDate: Date, query: string): Promise<any> {
    const url = `tweets/search/recent?query=${query}&start_time=${ISODateString(
        startDate
    )}&end_time=${ISODateString(
        endDate
    )}&max_results=25&expansions=author_id&tweet.fields=created_at,text&user.fields=id,username`;

    return twitterClient.v2.get(url);
}

// push the actual message to kafka
async function produceToKafka(registryId: number, message: Tweet) {
    await producer.connect();

    // compose the message: the key is a string
    // the value will be encoded using the avro schema
    const outgoingMessage = {
        key: message.id.toString(),
        value: await registry.encode(registryId, message),
    };

    // send the message to the previously created topic
    await producer.send({
        topic: TOPIC,
        messages: [outgoingMessage],
    });

    // disconnect the producer
    await producer.disconnect();
}

async function produce(message: Tweet) {
    const registryId = await registerSchema();
    // push example message
    if (registryId) {
        return await produceToKafka(registryId, message);
    }
}

main();

declare type Tweet = {
    id: number;
    created_at: string;
    user_id: number;
    username: string;
    text: string;
    quote_count: number;
    reply_count: number;
    retweet_count: number;
    favorite_count: number;
};
