import http from "k6/http";
import { check, sleep, group } from "k6";

export let options = {
    stages: [
        { duration: '30s', target: 50 },

        { duration: '1m', target: 200 },
        { duration: '1m', target: 400 },
        { duration: '1m', target: 600 },

        { duration: '2m', target: 600 },

        { duration: '1m', target: 0 }
    ],
    tags: {
        architecture: 'monolith'
    }
};

const BASE_URL = 'http://banking-app-monolith:8080/v1/accounts';
const params = { headers: { 'Content-Type': 'application/json' } };

export function setup() {
    function createAccount(email) {
        const payload = JSON.stringify({
            name: 'User Test',
            email,
            phone: '+5516999999999'
        });

        const res = http.post(`${BASE_URL}`, payload, params);
        check(res, { 'OK': (r) => r.status === 200 });

        return JSON.parse(res.body).accountId;
    }

    const accountId1 = createAccount(`user.${Math.floor(Math.random() * 1000)}@example.com`);
    const accountId2 = createAccount(`user.${Math.floor(Math.random() * 1000)}@example.com`);

    http.post(`${BASE_URL}/transactions/deposit`, JSON.stringify({
        accountId: accountId1,
        amount: 100000
    }), params);

    return { accountId1, accountId2 };
}

export default function(data) {
    const { accountId1, accountId2 } = data;

    group('Deposit', function() {
        const res = http.post(`${BASE_URL}/transactions/deposit`, JSON.stringify({
            accountId: accountId1,
            amount: 50
        }), params);
        check(res, { 'OK': (r) => r.status === 200 });
    });

    group('Transfer', function() {
        const res = http.post(`${BASE_URL}/transactions/transfer`, JSON.stringify({
            from: accountId1,
            to: accountId2,
            amount: 10
        }), params);
        check(res, { 'OK': (r) => r.status === 200 });
    });

    group('Withdrawal', function() {
        const res = http.post(`${BASE_URL}/transactions/withdrawal`, JSON.stringify({
            accountId: accountId1,
            amount: 5
        }), params);
        check(res, { 'OK': (r) => r.status === 200 });
    });

    group('Payment DEBIT', function() {
        const res = http.post(`${BASE_URL}/payments`, JSON.stringify({
            accountId: accountId1,
            amount: 5,
            transactionType: 'DEBIT'
        }), params);
        check(res, { 'OK': (r) => r.status === 200 });
    });

    group('Payment CREDIT', function() {
        const res = http.post(`${BASE_URL}/payments`, JSON.stringify({
            accountId: accountId1,
            amount: 5,
            transactionType: 'CREDIT'
        }), params);
        check(res, { 'OK': (r) => r.status === 200 });
    });

    sleep(1);
}
