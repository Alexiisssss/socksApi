package com.example.demo;

import com.example.demo.entity.Sock;
import com.example.demo.repository.SockRepository;
import com.example.demo.service.SockService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class SockServiceTest {

    @Mock
    private SockRepository sockRepository;

    @InjectMocks
    private SockService sockService;

    @Test
    void testAddIncome() {
        Sock sock = new Sock(null, "Red", 80, 100);
        sockService.addIncome(sock);
        verify(sockRepository, times(1)).save(sock);
    }
}
