package org.example.repo;

import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface PagingRepo<E> extends Repo<E> {
    Page<E> findAllOnPage(Pageable pageable);
}
