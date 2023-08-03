package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService service;
    @InjectMocks
    private ItemController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    Item item1 = new Item();
    Item item2 = new Item();
    User user1 = new User();
    User user2 = new User();
    Comment comment = new Comment();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item2.setId(2);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        item2.setOwner(user2);
        user1.setId(1);
        user1.setName("Testman");
        user1.setEmail("testman12@test.com");
        user2.setId(2);
        user2.setName("Testman2");
        user2.setEmail("testman123@test.com");
        comment.setId(1);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now().plusMinutes(1));
        comment.setAuthor(user1);
    }

    @Test
    void testGetItems() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.toItemDto(item1));
        items.add(new ItemDto());
        Mockito.when(service.getItems(1, 1, 10)).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1))
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(items)));
    }

    @Test
    void testGetItemForSearch() throws Exception {
        List<ItemDto> searchResults = new ArrayList<>();
        String text = "desCRIPTION";
        searchResults.add(ItemMapper.toItemDto(item1));
        searchResults.add(ItemMapper.toItemDto(item2));
        Mockito.when(service.searchItem(text, 1, 10)).thenReturn(searchResults);

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(searchResults)));
    }

    @Test
    void testPostItem() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        Mockito.when(service.postItem(1, itemDto)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void testPatchItem() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        Mockito.when(service.patchItem(1, 1, itemDto)).thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", 1)
                        .header("X-Sharer-User-Id", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void testDeleteItem() throws Exception {
        ItemDto deletedItem = ItemMapper.toItemDto(item1);
        Mockito.when(service.deleteItem(1, 1)).thenReturn(deletedItem);

        mvc.perform(delete("/items/{id}", 1)
                        .header("X-Sharer-User-Id", String.valueOf(1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(deletedItem)));
    }

    @Test
    void testPostComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        Mockito.when(service.postComment(1, 1, commentDto)).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}