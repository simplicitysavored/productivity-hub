package xyz.yuanjin.project.productivityhub.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.yuanjin.project.productivityhub.domain.user.entity.User;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 09:53</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承了 BaseMapper 后，已自动具备 selectOne, insert, updateById 等方法
}
