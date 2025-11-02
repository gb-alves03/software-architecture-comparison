import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
	vus: 20,
	duration: '1m'
}

const params = { headers: { 'Content-Type': 'application/json' } };

// Fazer um setup para subir as contas e de preferencia adicionar um saldo bem alto a conta
// Como ficará a parte do id da conta?

export default function() {
	group('register', function() {
		const payload = JSON.stringify({
			name: 'User Test',
			email: `user.${Math.floor(Math.random() * 1000)}@example.com`,
			"phone": "+5516999999999"
		})

		const res = http.post('http://localhost:8081/v1/accounts', payload, params);
		check(res, { 'Success': (r) => r.status === 200 });
	});

	group('deposit', function() {
		const payload = JSON.stringify({
			accountId: 1,
			amount: 500
		})

		const res = http.post('http://localhost:8081/v1/accounts/transactions/deposit', payload, params);
		check(res, { 'Success': (r) => r.status === 200 });
	})

	group('transfer', function() {
		const payload = JSON.stringify({
			from: 1,
			to: 2,
			amount: 10
		})

		const res = http.post('http://localhost:8081/v1/accounts/transactions/transfer', payload, params);
		check(res, { 'Success': (r) => r.status === 200 });
	});

	group('withdrawal', function() {
		const payload = JSON.stringify({
			accountId: 1,
			amount: 5
		})

		const res = http.post('http://localhost:8081/v1/accounts/transactions/withdrawal', payload, params);
		check(res, { 'Success': (r) => r.status === 200 });
	});

	group('payment DEBIT', function() {
		const payload = JSON.stringify({
			accountId: 1,
			amount: 5,
			type: 'DEBIT'
		})

		const res = http.post('http://localhost:8081/v1/accounts/payments', payload, params);
		check(res, { 'Success': (r) => r.status === 200 });
	});

	group('payment CREDIT', function() {
		const payload = JSON.stringify({
			accountId: 1,
			amount: 5,
			type: 'CREDIT'
		})

		const res = http.post('http://localhost:8081/v1/accounts/payments', payload, params);
		check(res, { 'Success': (r) => r.status === 200 });
	});

	sleep(1);


}
