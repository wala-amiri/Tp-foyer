package tn.esprit.tpfoyer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.entity.Reservation;
import tn.esprit.tpfoyer.service.IEtudiantService;
import tn.esprit.tpfoyer.service.IReservationService;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ReservationIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            // Forcer l’authentification compatible avec le driver JDBC
            .withCommand("--default-authentication-plugin=mysql_native_password")
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @Autowired
    IEtudiantService etudiantService;

    @Autowired
    IReservationService reservationService;

    @Test
    void testCreateReservationWithRealDb() {
        // Créer un étudiant
        Etudiant e = new Etudiant();
        e.setNomEtudiant("Amiri");
        e.setPrenomEtudiant("Wala");
        e.setCinEtudiant(12345678L);
        e.setDateNaissance(new Date());

        Etudiant savedEtudiant = etudiantService.addEtudiant(e);

        // Créer une réservation avec cet étudiant
        Reservation r = new Reservation();
        r.setIdReservation("R001");
        r.setEstValide(true);
        r.setAnneeUniversitaire(new Date());

        Set<Etudiant> etudiants = new HashSet<>();
        etudiants.add(savedEtudiant);
        r.setEtudiants(etudiants);

        Reservation savedRes = reservationService.addReservation(r);

        assertThat(savedRes.getEtudiants()).isNotEmpty();
        assertThat(savedRes.getEtudiants().iterator().next().getNomEtudiant()).isEqualTo("Amiri");
    }
}
