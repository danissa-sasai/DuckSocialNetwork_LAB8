package org.example.repo;

import org.example.domain.User;
import org.example.domain.duck.Duck;
import org.example.dto.DuckFilterDTO;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface DuckPagingRepo extends PagingRepo<User>{
    Page<User> findAllOnPage(Pageable pageable, DuckFilterDTO filter);
}
