package com.huy.service.productType;

import com.huy.model.ProdType;
import com.huy.model.enums.EProdType;
import com.huy.service.IGeneralService;

public interface IProductTypeService extends IGeneralService<ProdType> {
    ProdType findByName(EProdType bestSeller);
    ProdType findByName(String name);
}
