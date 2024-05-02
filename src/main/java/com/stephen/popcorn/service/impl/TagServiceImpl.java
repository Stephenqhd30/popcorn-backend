package com.stephen.popcorn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.stephen.popcorn.mapper.TagMapper;
import com.stephen.popcorn.model.domain.Tag;
import com.stephen.popcorn.service.TagService;
import org.springframework.stereotype.Service;

/**
 * @author stephen qiu
 * @description 针对表【tag(标签表)】的数据库操作Service实现
 * @createDate 2023-12-09 19:39:37
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




