package tn.esprit.tpfoyer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Date;
import tn.esprit.tpfoyer.entity.Reservation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // ✅ Utiliser le fichier application-test.properties ou celui de test
class ReservationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateReservationWithEtudiants() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setIdReservation("test123");
        reservation.setEstValide(true);
        reservation.setAnneeUniversitaire(new Date());


        mockMvc.perform(post("/reservation/add-reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk()); // ou isCreated() si tu veux renvoyer 201
    }
}
