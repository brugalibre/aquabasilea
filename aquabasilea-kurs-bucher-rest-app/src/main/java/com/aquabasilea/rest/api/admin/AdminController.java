package com.aquabasilea.rest.api.admin;

import com.aquabasilea.rest.model.admin.AdminOverviewDto;
import com.aquabasilea.rest.service.admin.AdminRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/activfitness/v1/admin")
@RestController
public class AdminController {

   private final AdminRestService adminRestService;

   @Autowired
   public AdminController(AdminRestService adminRestService) {
      this.adminRestService = adminRestService;
   }

   @GetMapping(path = "/overview")
   public AdminOverviewDto getAdminOverview() {
      return adminRestService.getAdminOverviewDto();
   }
}
