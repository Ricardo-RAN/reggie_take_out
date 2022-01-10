package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.dao.AddressBookDao;
import com.ricardo.reggie.domain.AddressBook;
import com.ricardo.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {
}
