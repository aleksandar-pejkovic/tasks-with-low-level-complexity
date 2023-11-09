package dev.task.low_level.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.task.low_level.model.Item;
import dev.task.low_level.repository.ItemRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ItemController.class})
class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    public void testCreateItem() {
        Item itemToCreate = new Item();
        when(itemRepository.save(itemToCreate)).thenReturn(itemToCreate);

        Item createdItem = itemController.createItem(itemToCreate);

        assertEquals(itemToCreate, createdItem);
    }

    @Test
    public void testGetAllItems() {
        List<Item> itemList = new ArrayList<>();
        when(itemRepository.findAll()).thenReturn(itemList);

        List<Item> result = itemController.getAllItems();

        assertEquals(itemList, result);
    }

    @Test
    public void testGetItemById() {
        Long itemId = 1L;
        Item item = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Optional<Item> result = itemController.getItemById(itemId);

        assertEquals(Optional.of(item), result);
    }

    @Test
    public void testGetItemByIdNotFound() {
        Item updatedItem = new Item();
        updatedItem.setId(1L); // Assuming the ID is 1
        when(itemRepository.existsById(updatedItem.getId())).thenReturn(false);

        // Assert that a NoSuchElementException is thrown when itemRepository.existsById returns false
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> itemController.updateItem(updatedItem));

        // Verify that the exception message is as expected
        assertEquals("Item with ID 1 not found.", exception.getMessage());

        // Verify that itemRepository.save() is not called
        verify(itemRepository, never()).save(updatedItem);
    }

    @Test
    public void testUpdateItem() {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        when(itemRepository.existsById(updatedItem.getId())).thenReturn(true);
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        Item result = itemController.updateItem(updatedItem);

        assertEquals(updatedItem, result);
    }

    @Test
    public void testUpdateItemNotFound() {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        when(itemRepository.existsById(updatedItem.getId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> itemController.updateItem(updatedItem));
    }

    @Test
    public void testDeleteItem() {
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);

        assertDoesNotThrow(() -> itemController.deleteItem(itemId));
    }

    @Test
    public void testDeleteItemNotFound() {
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> itemController.deleteItem(itemId));
    }
}