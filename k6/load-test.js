import http from 'k6/http';
import { check } from 'k6';

export const options = {
    // Simule 10 utilisateurs virtuels pendant 1 minute
    vus: 10,
    duration: '1m',
};

export default function () {
    const res =   http.get('http://app-timesheet:8089/tpfoyer/reservation/retrieve-all-reservations' );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}
