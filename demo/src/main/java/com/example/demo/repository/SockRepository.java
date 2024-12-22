package com.example.demo.repository;

import com.example.demo.entity.Sock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SockRepository extends JpaRepository<Sock, Long> {

    // Метод для поиска носков по цвету и процентному содержанию хлопка
    Optional<Sock> findByColorAndCottonPercentage(String color, int cottonPercentage);

    // Метод для суммирования количества носков по цвету и диапазону содержания хлопка
    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage BETWEEN :min AND :max")
    Integer findQuantityByColorAndCottonRange(@Param("color") String color, @Param("min") int min, @Param("max") int max);

    // Метод для фильтрации носков по цвету и диапазону процентного содержания хлопка
    List<Sock> findByColorAndCottonPercentageBetween(String color, int minCotton, int maxCotton, PageRequest pageRequest);
}


