package com.huy.service.productCategory;

import com.huy.model.ProdCategory;
import com.huy.model.enums.EProdCategory;
import com.huy.service.IGeneralService;

import java.util.Optional;

public interface IProdCategoryService extends IGeneralService<ProdCategory> {
    Optional<ProdCategory> findByName(EProdCategory name);
}
