package com.aquabasilea.model.userconfig.repository.mapping;

import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.persistence.entity.userconfig.UserConfigEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface UserConfigEntityMapper extends CommonDomainModelMapper<UserConfig, UserConfigEntity> {
   // no-op
}