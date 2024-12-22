package com.example.demo.controller;

import com.example.demo.entity.Sock;
import com.example.demo.exception.FileProcessingException;
import com.example.demo.service.SockService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
@Tag(name = "Socks API", description = "API для работы с носками на складе")
public class SockController {

    private final SockService sockService;


    @PostMapping("/income")
    @Operation(summary = "Регистрация прихода носков", description = "Регистрирует поступление носков на склад.")
    public ResponseEntity<String> registerIncome(@Valid @RequestBody Sock sock) {
        log.info("Получен запрос на приход носков: {}", sock);
        sockService.addIncome(sock);
        log.info("Носки добавлены в склад: {}", sock);
        return ResponseEntity.ok("Socks added to stock");
    }

    @PostMapping("/outcome")
    @Operation(summary = "Регистрация отпуска носков", description = "Регистрирует отпуск носков со склада.")
    public ResponseEntity<String> registerOutcome(@RequestParam String color,
                                                  @RequestParam int cottonPercentage,
                                                  @RequestParam int quantity) {
        log.info("Получен запрос на отпуск носков - Цвет: {}, Процент хлопка: {}, Количество: {}", color, cottonPercentage, quantity);
        try {
            sockService.registerOutcome(color, cottonPercentage, quantity);
            log.info("Носки успешно отпущены - Цвет: {}, Процент хлопка: {}, Количество: {}", color, cottonPercentage, quantity);
            return ResponseEntity.ok("Socks removed from stock");
        } catch (Exception ex) {
            log.error("Ошибка при отпуске носков: {}", ex.getMessage());
            return ResponseEntity.status(400).body("Error: " + ex.getMessage());
        }
    }


    @GetMapping
    @Operation(summary = "Получение носков", description = "Возвращает список носков, отфильтрованных по цвету и проценту хлопка, с возможностью сортировки.")
    public ResponseEntity<List<Sock>> getFilteredSocks(@RequestParam String color,
                                                       @RequestParam int minCotton,
                                                       @RequestParam int maxCotton,
                                                       @RequestParam(defaultValue = "color") String sortBy) {
        log.info("Получен запрос на получение носков - Цвет: {}, Мин. процент хлопка: {}, Макс. процент хлопка: {}, Сортировка по: {}", color, minCotton, maxCotton, sortBy);
        List<Sock> socks = sockService.getFilteredSocks(color, minCotton, maxCotton, sortBy);
        log.info("Общее количество носков: {}", socks.size());
        return ResponseEntity.ok(socks);
    }

    @PostMapping("/batch")
    @Operation(summary = "Загрузка партии носков", description = "Загружает носки в базу данных из CSV файла.")
    public ResponseEntity<String> uploadBatch(@RequestParam("file") MultipartFile file) {
        log.info("Получен запрос на загрузку партии носков из файла");
        try {
            sockService.processAndSaveBatch(file);
            log.info("Партия носков успешно загружена из файла: {}", file.getOriginalFilename());
            return ResponseEntity.ok("Batch uploaded successfully");
        } catch (Exception ex) {
            log.error("Ошибка при обработке файла: {}", ex.getMessage());
            throw new FileProcessingException("Error processing file");
        }
    }
}
