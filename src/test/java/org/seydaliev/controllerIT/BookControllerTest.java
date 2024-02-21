package org.seydaliev.controllerIT;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seydaliev.Service.impl.BookServiceImpl;
import org.seydaliev.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@WebMvcTest(BookControllerTest.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookServiceImpl bookServiceImpl;

    @Test
    public void testGetAllBooksWithPagination() throws Exception {
        PageRequest pageRequest = PageRequest.of(0,  5);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(new Book(), new Book()), pageRequest,  2);

        when(bookServiceImpl.findAll(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/books?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void testFindBookByIdNotFound() {
        Long nonExistentBookId =   99999L;

        try {
            restTemplate.getForObject("/books/{id}", Book.class, nonExistentBookId);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    public void testGetBookById() throws Exception {
        Long bookId =  1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.author").isNotEmpty());
    }

    @Test
    public void testCreateBook() throws Exception {
        String bookJson = "{\"title\":\"Test Book\",\"isbn\":\"1234567890\",\"author\":{\"id\":1}}";

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.author.id").value(1));
    }

    @Test
    public void testUpdateBook() throws Exception {
        Long bookId =  1L;
        String updatedBookJson = "{\"title\":\"Updated Book Title\",\"isbn\":\"9876543210\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Updated Book Title"))
                .andExpect(jsonPath("$.isbn").value("9876543210"));
    }

    @Test
    public void testDeleteBook() throws Exception {
        Long bookId =  1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }
}
