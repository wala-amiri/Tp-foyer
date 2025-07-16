import http from 'k6/http';
import { check, sleep } from 'k6'; // Assure-toi que 'sleep' est bien importé


export const options = {
    // Simule 10 utilisateurs virtuels pendant 1 minute
    vus: 10,
    duration: '1m',
};

export default function () {
    // Ajoute une pause de 30 secondes avant de commencer le test
    // pour laisser le temps à l'application de démarrer.
    // Tu peux augmenter cette valeur si ton application est lente à démarrer.
    sleep(80);
    const res =   http.get('http://app-timesheet:8089/tpfoyer/reservation/retrieve-all-reservations' );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}
