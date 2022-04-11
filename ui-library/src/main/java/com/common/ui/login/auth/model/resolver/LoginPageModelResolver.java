package com.common.ui.login.auth.model.resolver;

import static java.util.Objects.nonNull;

import com.common.ui.login.auth.model.LoginPageModel;
import com.common.ui.core.model.resolver.impl.AbstractPageModelResolver;

public class LoginPageModelResolver extends AbstractPageModelResolver<LoginPageModel, LoginPageModel> {

   @Override
   protected LoginPageModel resolveNewPageModel(LoginPageModel dataModelIn) {
      return nonNull(currentPageModel) ? currentPageModel : new LoginPageModel();
   }
}
