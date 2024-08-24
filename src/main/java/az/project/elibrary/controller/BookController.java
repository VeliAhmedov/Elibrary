package az.project.elibrary.controller;

import az.project.elibrary.dto.request.ReqBook;
import az.project.elibrary.dto.response.RespBook;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/list")
    public Response<List<RespBook>> getBookList(@RequestHeader String token) {
        return bookService.getBookList(token);
    }

    @GetMapping("/{id}")
    public Response<RespBook> getBookById(@PathVariable Long id,@RequestHeader String token) {
        return bookService.getById(id,token);
    }

    @PostMapping("/create")
    public Response<RespBook> createBook(@RequestBody ReqBook reqBook,@RequestHeader String token) {
        return bookService.create(reqBook,token);
    }

    @PutMapping("/update")
    public Response<RespBook> updateBook(@RequestBody ReqBook reqBook,@RequestHeader String token) {
        return bookService.update(reqBook,token);
    }

    @DeleteMapping("/delete/{id}")
    public Response deleteBook(@PathVariable Long id,@RequestHeader String token) {
        return bookService.delete(id,token);
    }
}
