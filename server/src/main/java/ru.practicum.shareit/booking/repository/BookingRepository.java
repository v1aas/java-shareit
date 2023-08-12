package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByBooker(User booker, Pageable pageable);

    Page<Booking> findByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemIn(List<Item> item, Pageable pageable);

    Page<Booking> findByItemInAndStatus(List<Item> item, BookingStatus status, Pageable pageable);

    Booking findFirstByItemAndStatusAndStartAfterOrderByStart(Item item, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(Item item, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemAndBookerAndStatusAndEndBefore(Item item, User booker, BookingStatus approved,
                                                          LocalDateTime now);

    Page<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Item> items, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
            List<Item> items, BookingStatus approved, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.start < current_timestamp and b.end > current_timestamp  " +
            "order by b.start desc")
    Page<Booking> getCurrentByUserId(Integer userId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc")
    Page<Booking> getBookingByUserIdAndFinishAfterNow(Integer userId, Pageable pageable);
}