import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 10,
    duration: '30s',
};

export default function () {
    const res = http.get('http://localhost:8090/tpfoyer/reservation/retrieve-all-reservations');

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}
