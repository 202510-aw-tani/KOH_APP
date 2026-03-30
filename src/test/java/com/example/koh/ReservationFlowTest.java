package com.example.koh;

import com.example.koh.entity.Reservation;
import com.example.koh.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void postReserveSavesDbAndListPageShowsSavedData() throws Exception {
        long beforeCount = reservationRepository.count();

        mockMvc.perform(post("/reserve")
                        .param("reservationDate", "2026-04-05")
                        .param("reservationTime", "11")
                        .param("partySize", "2")
                        .param("name", "Flow Test User")
                .param("email", "flow-test@example.com")
                .param("phone", "09011112222")
                .param("note", "web flow test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/thanks"));

        long afterCount = reservationRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount + 1);

        List<Reservation> savedReservations = reservationRepository.findAll();
        assertThat(savedReservations)
                .extracting(Reservation::getEmail)
                .contains("flow-test@example.com");

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ID")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("flow-test@example.com")));
    }

    @Test
    void reservationsListShowsNewestFirst() throws Exception {
        mockMvc.perform(post("/reserve")
                        .param("reservationDate", "2026-04-05")
                        .param("reservationTime", "11")
                        .param("partySize", "1")
                        .param("name", "Older User")
                        .param("email", "older@example.com")
                        .param("phone", "09000000001"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/reserve")
                        .param("reservationDate", "2026-04-06")
                        .param("reservationTime", "13")
                        .param("partySize", "2")
                        .param("name", "Newer User")
                        .param("email", "newer@example.com")
                        .param("phone", "09000000002"))
                .andExpect(status().is3xxRedirection());

        String html = mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int newerIndex = html.indexOf("newer@example.com");
        int olderIndex = html.indexOf("older@example.com");
        assertThat(newerIndex).isGreaterThan(-1);
        assertThat(olderIndex).isGreaterThan(-1);
        assertThat(newerIndex).isLessThan(olderIndex);
    }

    @Test
    void postReserveWithInvalidValuesReturnsReserveWithErrors() throws Exception {
        long beforeCount = reservationRepository.count();

        mockMvc.perform(post("/reserve")
                        .param("reservationDate", "")
                        .param("reservationTime", "99")
                        .param("partySize", "0")
                        .param("name", "   ")
                        .param("email", "invalid-email")
                        .param("phone", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("reserve"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("入力内容をご確認ください。")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("メールアドレスの形式が正しくありません。")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("人数は1名以上で入力してください。")));

        long afterCount = reservationRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);
    }
}
