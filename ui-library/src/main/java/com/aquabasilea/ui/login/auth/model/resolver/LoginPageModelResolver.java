package com.aquabasilea.ui.login.auth.model.resolver;

import static java.util.Objects.nonNull;

import com.aquabasilea.ui.login.auth.model.LoginPageModel;
import com.aquabasilea.ui.core.model.resolver.impl.AbstractPageModelResolver;

public class LoginPageModelResolver extends AbstractPageModelResolver<LoginPageModel, LoginPageModel> {

   @Override
   protected LoginPageModel resolveNewPageModel(LoginPageModel dataModelIn) {
      return nonNull(currentPageModel) ? currentPageModel : new LoginPageModel();
   }
}
