package com.aquabasilea.coursebooker.model.userconfig.repository.mapping;

import com.aquabasilea.coursebooker.model.userconfig.UserConfig;
import com.aquabasilea.coursebooker.persistence.userconfig.UserConfigEntity;
import com.brugalibre.common.domain.mapper.CommonDomainModelMapper;
import org.mapstruct.Mapper;

@Mapper
public interface UserConfigEntityMapper extends CommonDomainModelMapper<UserConfig, UserConfigEntity> {
   // no-op
}