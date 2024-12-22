package com.example.demo;

import com.example.demo.controller.SockController;
import com.example.demo.entity.Sock;
import com.example.demo.service.SockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SockControllerTest {

    @Mock
    private SockService sockService;

    @InjectMocks
    private SockController sockController;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testRegisterIncome() {
        Sock sock = new Sock(null, "Red", 80, 100);

        doNothing().when(sockService).addIncome(sock);

        ResponseEntity<String> response = sockController.registerIncome(sock);

        assertEquals("Socks added to stock", response.getBody());

        verify(sockService, times(1)).addIncome(sock);
    }

    @Test
    void testRegisterOutcome() throws ChangeSetPersister.NotFoundException {
        String color = "Red";
        int cottonPercentage = 80;
        int quantity = 50;

        doNothing().when(sockService).registerOutcome(color, cottonPercentage, quantity);

        ResponseEntity<String> response = sockController.registerOutcome(color, cottonPercentage, quantity);

        assertEquals("Socks removed from stock", response.getBody());

        verify(sockService, times(1)).registerOutcome(color, cottonPercentage, quantity);
    }

    @Test
    void testGetFilteredSocks() {
        String color = "Red";
        int minCotton = 50;
        int maxCotton = 90;
        String sortBy = "color";

        List<Sock> socks = new ArrayList<>();
        socks.add(new Sock(1L, "Red", 80, 100));

        when(sockService.getFilteredSocks(color, minCotton, maxCotton, sortBy)).thenReturn(socks);

        ResponseEntity<List<Sock>> response = sockController.getFilteredSocks(color, minCotton, maxCotton, sortBy);

        assertEquals(socks, response.getBody());

        verify(sockService, times(1)).getFilteredSocks(color, minCotton, maxCotton, sortBy);
    }

    @Test
    void testUploadBatch() {
        MultipartFile mockFile = mock(MultipartFile.class);

        doNothing().when(sockService).processAndSaveBatch(mockFile);

        ResponseEntity<String> response = sockController.uploadBatch(mockFile);

        assertEquals("Batch uploaded successfully", response.getBody());

        verify(sockService, times(1)).processAndSaveBatch(mockFile);
    }
}
