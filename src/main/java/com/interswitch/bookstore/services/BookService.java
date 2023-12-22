package com.interswitch.bookstore.services;

import com.interswitch.bookstore.exceptions.InconsistentException;
import com.interswitch.bookstore.models.Author;
import com.interswitch.bookstore.models.Book;
import com.interswitch.bookstore.models.Genre;
import com.interswitch.bookstore.repositories.BookRepository;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import com.interswitch.bookstore.utils.api.PaginationBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class BookService {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private BookRepository bookRepository;

    public Book saveBook(Book book){
        if(ISBNInUse(book.getIsbn())){
            log.error("isbn already in used while persisting book {} ", book.getIsbn());
            throw new InconsistentException("ISBN IN USE", "9000");
        }
        book = bookRepository.save(book);
        log.info("saved book {} ", book.getIsbn());
        return  book;
    }

    public boolean ISBNInUse(String isbn){
        return bookRepository.countByIsbn(isbn) > 0;
    }



    public PaginateApiResponse searchBook(HttpServletRequest request){
        Map<String, Object> paramMap = new HashMap<>();

        String baseQuery = " FROM BOOKS b" +
                "  JOIN AUTHORS a ON b.author_id = a.id" +
                "  WHERE ";
        String countQuery = "SELECT COUNT(*) ";//title,genre,isbn, year_of_pub:name
        String dataQuery = "SELECT b.id as bid, b.created as bcreated, b.isbn, b.title, b.year_of_pub,b.price, b.genre, " +
                " a.id as aid, a.name, a.created as acreated";

        String predicates = "";
        String orderClause = "";

        int page = 0;
        int pageSize = 10;

        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String,String[]> entry : params.entrySet()){
            String key = entry.getKey();
            String[] values = entry.getValue();
            if(values.length > 0){
                String value = values[0].trim();
                if(value.contains(":") && value.split(":").length == 2){
                    String content = value.split(":")[1].trim();
                    String operator = value.split(":")[0].trim();
                    if(key.equalsIgnoreCase("pub_year")){
                        Integer year = Integer.valueOf(content);
                        paramMap.put("pub_year", year);
                        if(operator.equalsIgnoreCase("eq")){
                            predicates += (" AND ( pub_year = :pub_year) ");
                        }else if(operator.equalsIgnoreCase("lte")){
                            predicates += (" AND ( pub_year <= :pub_year) ");

                        }else if (operator.equalsIgnoreCase("gte")){
                            predicates += (" AND ( pub_year >= :pub_year) ");
                        }else if(operator.equalsIgnoreCase("lt")){
                            predicates += (" AND ( pub_year < :pub_year) ");
                        }else if(operator.equalsIgnoreCase("gt")){
                            predicates += (" AND ( pub_year > :pub_year) ");
                        }else if(operator.equalsIgnoreCase("ne")){
                            predicates += (" AND ( pub_year <> :pub_year) ");
                        }
                    }else if(key.equalsIgnoreCase("title")){
                        paramMap.put("pub_year", content);
                        if(operator.equalsIgnoreCase("eq")){
                            predicates += (" AND ( title = :title) ");
                        }
                        if(operator.equalsIgnoreCase("ne")){
                            predicates += (" AND ( title <> :title) ");
                        }
                    }else if(key.equalsIgnoreCase("price")){
                        Double price = Double.valueOf(content);
                        paramMap.put("price", price);
                        if(operator.equalsIgnoreCase("eq")){
                            predicates += (" AND ( price = :price) ");
                        }else if(operator.equalsIgnoreCase("lte")){
                            predicates += (" AND ( price <= :price) ");
                        }else if (operator.equalsIgnoreCase("gte")){
                            predicates += (" AND ( price >= :price) ");
                        }else if(operator.equalsIgnoreCase("lt")){
                            predicates += (" AND ( price < :price) ");
                        }else if(operator.equalsIgnoreCase("gt")){
                            predicates += (" AND ( price > :price) ");
                        }else if(operator.equalsIgnoreCase("ne")){
                            predicates += (" AND ( price <> :price) ");
                        }else if(key.equalsIgnoreCase("genre")){
                            paramMap.put("genre", content);
                            if(operator.equalsIgnoreCase("eq")){
                                predicates += (" AND ( genre = :genre) ");
                            }
                            if(operator.equalsIgnoreCase("ne")){
                                predicates += (" AND ( genre <> :genre) ");
                            }
                        }else if(key.equalsIgnoreCase("author")){
                            paramMap.put("name", content);
                            if(operator.equalsIgnoreCase("eq")){
                                predicates += (" AND ( name = :name) ");
                            }
                            if(operator.equalsIgnoreCase("ne")){
                                predicates += (" AND ( name <> :name) ");
                            }
                        }
                    }

                }else{

                    if(key.equalsIgnoreCase("sort_by") && values.length > 0){
                        String orderColumn = values[0].trim();
                        String sortDirection = orderColumn.contains("-") ? "desc" : "asc";

                        orderColumn = orderColumn.replace("+","").replace("-","");
                        orderClause = " ORDER BY "+ orderColumn +" "+sortDirection;

                    }

                    if(key.equalsIgnoreCase("page") && values.length > 0){
                        String pg = values[0].trim();
                        page = (Integer.valueOf(pg)) - 1;
                        if(page < 0){page = 0;}

                    }

                    if(key.equalsIgnoreCase("size") && values.length > 0){
                        String size = values[0].trim();
                        pageSize = Integer.valueOf(size);
                    }
                }

            }
        }

        Pageable paging = PageRequest.of(page, pageSize);

        if(BasicUtil.validString(predicates)){
            predicates = predicates.startsWith(" AND ") ? predicates.replaceFirst(" AND ", "") : predicates;
            baseQuery += ""+predicates;
        }
        countQuery += baseQuery;
        String pagination = " LIMIT :pageSize OFFSET :offset";
        paramMap.put("pageSize", pageSize);
        paramMap.put("offset", paging.getOffset());

        dataQuery += (baseQuery +orderClause + pagination) ;

        System.out.println("data query is "+dataQuery);
        System.out.println("Count query is "+countQuery);
        System.out.println("Parameters "+paramMap);

        //price=eq:700
        Long count = namedParameterJdbcTemplate.queryForObject(countQuery, paramMap, Long.class);

        List<Book> books = namedParameterJdbcTemplate.query(dataQuery, paramMap, new BookRowMapper());

        Page<Book> result = new PageImpl<>(books, paging,count);

        return new PaginateApiResponse(new PaginationBody(result, pageSize));


    }

    private class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            //SELECT b.id as bid, b.created as bcreated, b.isbn, b.title, b.year_of_pub,b.price," +
            //                " a.id as aid, a.name, a.created as acreate
            Book book = new Book();
            book.setTitle(rs.getString("title"));
            book.setYearOfPublication(rs.getInt("year_of_pub"));
            book.setIsbn(rs.getString("isbn"));
            book.setPrice(rs.getDouble("price"));
            book.setGenre(Genre.fixGenreFromName(rs.getString("genre")));
            book.setCreated(rs.getDate("bcreated"));
            book.setId(rs.getLong("bid"));

            book.setAuthor(fixAuthor(rs.getString("name"), rs.getLong("aid"), rs.getDate("acreated")));
            return book;
        }

        private Author fixAuthor(String name, Long id, Date created){
            Author author = new Author();
            author.setName(name);
            author.setId(id);
            author.setCreated(created);

            return author;
        }
    }
}
