package com.ussd.mtn.repository;

import com.ussd.mtn.model.UssdMenu;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UssdMenuRepository extends MongoRepository<UssdMenu, String> {
    Optional<UssdMenu> findByMenuId(String menuId);

    boolean existsByMenuId(String menuId);
}
