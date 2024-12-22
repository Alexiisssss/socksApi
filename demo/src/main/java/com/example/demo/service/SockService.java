package com.example.demo.service;

import com.example.demo.entity.Sock;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repository.SockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SockService {

    private final SockRepository sockRepository;

    // Метод для добавления прихода носков
    public void addIncome(Sock sock) {
        log.info("Регистрация прихода носков: {}", sock);
        sockRepository.save(sock);
        log.info("Носки успешно добавлены на склад: {}", sock);
    }


    // Метод для регистрации отпуска носков
    public void registerOutcome(String color, int cottonPercentage, int quantity) throws ChangeSetPersister.NotFoundException {
        log.info("Обработка отпуска носков - Цвет: {}, Процент хлопка: {}, Количество: {}", color, cottonPercentage, quantity);
        Sock sock = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage)
                .orElseThrow(() -> {
                    log.error("Носки не найдены для цвета: {} и процента хлопка: {}", color, cottonPercentage);
                    return new ChangeSetPersister.NotFoundException();
                });

        if (sock.getQuantity() < quantity) {
            log.error("Недостаточно носков на складе. Доступно: {}, Запрашиваемое количество: {}", sock.getQuantity(), quantity);
            throw new InsufficientStockException("Недостаточно носков на складе");
        }

        sock.setQuantity(sock.getQuantity() - quantity);
        sockRepository.save(sock);
        log.info("Носки списаны со склада: Цвет: {}, Процент хлопка: {}, Количество: {}", color, cottonPercentage, quantity);
    }

    // Метод для получения общего количества носков по фильтру
    public Integer getTotalQuantity(String color, int minCotton, int maxCotton) {
        log.info("Получение общего количества носков - Цвет: {}, Диапазон процента хлопка: {}-{}", color, minCotton, maxCotton);
        Integer totalQuantity = sockRepository.findQuantityByColorAndCottonRange(color, minCotton, maxCotton);
        log.info("Общее количество найдено: {}", totalQuantity);
        return totalQuantity;
    }


    // Метод для фильтрации носков по диапазону процентного содержания хлопка и сортировке
    public List<Sock> getFilteredSocks(String color, int minCotton, int maxCotton, String sortBy) {
        log.info("Получение носков по фильтру: цвет={}, диапазон хлопка={} - {}", color, minCotton, maxCotton);

        // Создаем сортировку по указанному полю
        Sort sort = Sort.by(Sort.Order.asc(sortBy)); // сортировка по умолчанию по возрастанию
        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE, sort);

        // Получаем носки из репозитория с фильтрацией и сортировкой
        List<Sock> socks = sockRepository.findByColorAndCottonPercentageBetween(color, minCotton, maxCotton, pageRequest);
        log.info("Найдено носков: {}", socks.size());

        return socks;
    }

    // Метод для обработки CSV файла и сохранения носков в базу данных
    public void processAndSaveBatch(MultipartFile file) {
        List<Sock> socks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine();  // Пропуск заголовка
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String color = fields[0];
                int cottonPercentage = Integer.parseInt(fields[1]);
                int quantity = Integer.parseInt(fields[2]);
                socks.add(new Sock(null, color, cottonPercentage, quantity));
                log.info("Processed sock: color={}, cottonPercentage={}, quantity={}", color, cottonPercentage, quantity);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing file", e);
        }

        // Сохраняем носки в базу данных
        if (socks.isEmpty()) {
            log.warn("No socks to save!");
        } else {
            sockRepository.saveAll(socks);
            log.info("Saved {} socks to the database", socks.size());
        }
    }
}
