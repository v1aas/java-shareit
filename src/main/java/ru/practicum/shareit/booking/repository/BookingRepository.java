package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBooker(User booker);

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status);

    List<Booking> findByItemIn(List<Item> item);

    List<Booking> findByItemInAndStatus(List<Item> item, BookingStatus status);

    Booking findFirstByItemAndStatusAndStartAfterOrderByStart(Item item, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(Item item, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemAndBookerAndStatusAndEndBefore(Item item, User booker, BookingStatus approved,
                                                          LocalDateTime now);

    List<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Item> items, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
            List<Item> items, BookingStatus approved, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.start < current_timestamp and b.end > current_timestamp  " +
            "order by b.start desc")
    List<Booking> getCurrentByUserId(Integer id);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc")
    List<Booking> getBookingByUserIdAndFinishAfterNow(Integer userId);
}