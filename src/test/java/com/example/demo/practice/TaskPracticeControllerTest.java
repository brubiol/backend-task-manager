package com.example.demo.practice;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Practice controller tests (skeleton only).
 */
@WebMvcTest(TaskPracticeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskPracticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskPracticeService taskPracticeService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getDummyTask_ReturnsOk() throws Exception {
        // TODO: implement using MockMvc
    }
}
