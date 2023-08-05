package com.aquabasilea.domain.userconfig.model.mapping;

import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.persistence.userconfig.UserConfigEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface UserConfigEntityMapper extends CommonDomainModelMapper<UserConfig, UserConfigEntity> {
   // no-op
}