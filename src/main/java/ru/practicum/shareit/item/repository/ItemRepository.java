package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerOrderById(User owner);

    @Query("SELECT i.owner " +
            "FROM Item i " +
            "WHERE i.id = :id")
    User getOwnerById(@Param("id") int id);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchItems(@Param("text") String text);
}
