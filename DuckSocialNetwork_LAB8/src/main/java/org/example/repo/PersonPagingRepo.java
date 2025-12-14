package org.example.repo;

import org.example.domain.User;
import org.example.dto.PersonFilterDTO;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface PersonPagingRepo extends PagingRepo<User>{
    Page<User> findAllOnPage(Pageable pageable, PersonFilterDTO filter);
}
