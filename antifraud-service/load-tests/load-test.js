import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
  stages: [
    { duration: "30s", target: 20 },
    { duration: "1m", target: 20 },
    { duration: "30s", target: 0 },
  ],
};

export default function () {
  const url = "http://fraud-service:8081/api/fraud/validate";
  const payload = JSON.stringify({
    transactionId: `${__VU}-${__ITER}`,
    amount: Math.floor(Math.random() * 20000),
    transactionType: "TRANSFER",
    sourceAccount: "ACC123",
    destinationAccount: "ACC456",
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  let res = http.post(url, payload, params);

  check(res, {
    "status é 200": (r) => r.status === 200,
    "resposta contém transactionId": (r) =>
      r.json("transactionId") !== undefined,
  });

  sleep(1);
}
