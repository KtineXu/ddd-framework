package io.ddd.framework.infrastructure.repositoryimpl.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.ddd.framework.coreclient.dto.common.page.Page;
import io.ddd.framework.coreclient.dto.sys.user.UserListQry;
import io.ddd.framework.coreclient.dto.sys.user.UserPageQry;
import io.ddd.framework.domain.factory.DomainFactory;
import io.ddd.framework.domain.domain.sys.User;
import io.ddd.framework.domain.repository.sys.UserRepository;
import io.ddd.framework.infrastructure.repositoryimpl.sys.database.convertor.User2DOConvector;
import io.ddd.framework.infrastructure.repositoryimpl.sys.database.dataobject.UserDO;
import io.ddd.framework.infrastructure.repositoryimpl.sys.database.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * UserRepositoryImpl
 *
 * @date 2020-07-02 12:32 PM
 */
@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private UserMapper userMapper;
    @Resource
    private User2DOConvector user2DOConvector;

    public void create(User user){
        userMapper.insert(user2DOConvector.toDO(user));
    }

    public void update(User user){
        userMapper.updateById(user2DOConvector.toDO(user));
    }

    public User getByUserId(String userId){
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        UserDO userDO = userMapper.selectOne(wrapper);
        Assert.notNull(userDO,"There is no User for : "+userId);
        User user = DomainFactory.getUser();
        BeanUtils.copyProperties(userDO, user);
        return user;
    }

    public Page<User> page(UserPageQry qry) {
        QueryWrapper<UserDO> wp = new QueryWrapper<>();
        wp.eq("deleted",0);//未删除
        if (Objects.nonNull(qry.getUserDTO())&&StringUtils.isNotBlank(qry.getUserDTO().getUserName())) {
            wp.and(w->w.eq("user_name", qry.getUserDTO().getUserName()).or().eq("user_id", qry.getUserDTO().getUserName()));
        }
        IPage<UserDO> page = userMapper.selectPage(new PageDTO<>(qry.getPageIndex(),qry.getPageSize()), wp);
        List<User> list = new ArrayList<>(page.getRecords().size());
        page.getRecords().forEach(v-> list.add(user2DOConvector.toDomain(v)));
        return Page.of(page.getCurrent(),page.getSize(),page.getTotal(),list);
    }

    @Override
    public List<User> list(UserListQry qry) {
        return user2DOConvector.toDomainList(userMapper.selectList(new QueryWrapper<>()));
    }

    public User getById(Long id){
        UserDO userDO = userMapper.selectById(id);
        User user = DomainFactory.getUser();
        BeanUtils.copyProperties(userDO, user);
        return user;
    }
}
