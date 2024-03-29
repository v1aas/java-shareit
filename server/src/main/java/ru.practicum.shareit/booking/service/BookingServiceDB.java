package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceDB implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingFullDto createRequest(int userId, BookingDto booking) {
        if (!itemRepository.getById(booking.getItemId()).getAvailable()) {
            throw new ValidationException("Вещь занята!");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет!");
        }
        if (itemRepository.getById(booking.getItemId()).getOwner().getId() == userId) {
            throw new NullPointerException("Владелец не может забронировать свою вещь!");
        }
        if (booking.getEnd() == null || booking.getStart() == null
                || booking.getEnd().equals(booking.getStart())
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неправильно указано время!");
        }
        Booking book = new Booking();
        book.setStart(booking.getStart());
        book.setEnd(booking.getEnd());
        book.setItem(itemRepository.getById(booking.getItemId()));
        book.setBooker(userRepository.getById(userId));
        book.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingFullDto(repository.save(book));
    }

    @Override
    public BookingFullDto postApproveBooking(int bookingId, int userId, boolean approve) {
        if (repository.getById(bookingId).getItem().getOwner().getId() != userId) {
            throw new NullPointerException("Менять статус бронирования может только владелец");
        }
        if (repository.getById(bookingId).getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус уже подтвержден!");
        }
        Booking book = repository.getById(bookingId);
        if (approve) {
            book.setStatus(BookingStatus.APPROVED);
        } else {
            book.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingFullDto(repository.save(book));
    }

    @Override
    public BookingFullDto getBookingRequest(int bookingId, int userId) {
        Booking book = repository.getById(bookingId);
        if (book.getBooker().getId() != userId) {
            if (book.getItem().getOwner().getId() != userId) {
                throw new NullPointerException("Смотреть может владелец или арендующий");
            }
        }
        return BookingMapper.toBookingFullDto(book);
    }

    @Override
    public List<BookingFullDto> getAllBookingRequestForUser(int userId, String state, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет");
        }
        BookingState bookState;
        try {
            bookState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        List<BookingFullDto> bookingsDto = new ArrayList<>();
        List<Booking> bookings;
        switch (bookState) {
            case CURRENT:
                bookings = repository.getCurrentByUserId(userId, getPage(from, size,
                        Sort.by(Sort.Direction.ASC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case PAST:
                bookings = repository.getBookingByUserIdAndFinishAfterNow(userId, getPage(from, size,
                        Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case FUTURE:
                List<Booking> bookingsApprove = repository.findByBookerAndStatus(userRepository.getById(userId),
                        BookingStatus.APPROVED, getPage(from, size)).getContent();
                List<Booking> bookingsWaiting = repository.findByBookerAndStatus(userRepository.getById(userId),
                        BookingStatus.WAITING, getPage(from, size)).getContent();
                for (Booking book : bookingsApprove) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                for (Booking book : bookingsWaiting) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                bookingsDto.sort(Comparator.comparing(BookingFullDto::getStart).reversed());
                return bookingsDto;
            case WAITING:
                bookings = repository.findByBookerAndStatus(userRepository.getById(userId),
                        BookingStatus.WAITING, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case REJECTED:
                bookings = repository.findByBookerAndStatus(userRepository.getById(userId),
                        BookingStatus.REJECTED, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case ALL:
                bookings = repository.findByBooker(userRepository.getById(userId), getPage(from, size,
                        Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingFullDto> getAllBookingRequestForOwner(int userId, String state, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет");
        }
        BookingState bookState;
        try {
            bookState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        List<Item> ownerItems = itemRepository.findAllByOwnerOrderById(userRepository.getById(userId),
                getPage(0, 10)).toList();
        List<BookingFullDto> bookingsDto = new ArrayList<>();
        List<Booking> bookings;
        switch (bookState) {
            case CURRENT:
                bookings = repository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerItems, LocalDateTime.now(), LocalDateTime.now(), getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case PAST:
                bookings = repository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(ownerItems,
                        BookingStatus.APPROVED, LocalDateTime.now(), getPage(from, size,
                                Sort.by(Sort.Direction.ASC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case FUTURE:
                List<Booking> bookingsApprove = repository.findByItemInAndStatus(ownerItems,
                        BookingStatus.APPROVED, getPage(from, size)).getContent();
                List<Booking> bookingsWaiting = repository.findByItemInAndStatus(ownerItems,
                        BookingStatus.WAITING, getPage(from, size)).getContent();
                for (Booking book : bookingsApprove) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                for (Booking book : bookingsWaiting) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                bookingsDto.sort(Comparator.comparing(BookingFullDto::getStart).reversed());
                return bookingsDto;
            case WAITING:
                bookings = repository.findByItemInAndStatus(ownerItems,
                        BookingStatus.WAITING, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case REJECTED:
                bookings = repository.findByItemInAndStatus(ownerItems,
                        BookingStatus.REJECTED, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case ALL:
                bookings = repository.findByItemIn(ownerItems, PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private PageRequest getPage(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }

    private PageRequest getPage(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}