/**
 * This module is about the wiring of the domain-services with beans. Also, this package contains "api-services"
 * which may be called from other modules like the rest-module
 * <p>
 * There should be no domain logic here besides wrapping existing logic into services. But for now this is not always the
 * case. There exist a lot of services which contains domain logic, outside the actual domain
 */
package com.aquabasilea.service;