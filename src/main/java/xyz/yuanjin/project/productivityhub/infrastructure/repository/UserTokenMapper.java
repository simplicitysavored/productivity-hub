package xyz.yuanjin.project.productivityhub.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.yuanjin.project.productivityhub.domain.user.entity.UserToken;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 14:53</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {
}
