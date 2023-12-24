package com.aquabasilea.domain.coursebooker;

import com.brugalibre.domain.user.model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * The {@link AquabasileaCourseBookerHolder} contains a {@link AquabasileaCourseBooker} for each registered {@link User}
 */
public class AquabasileaCourseBookerHolder {
   private final Map<String, AquabasileaCourseBooker> userId2AquabasileaCourseBookerMap;

   public AquabasileaCourseBookerHolder() {
      this.userId2AquabasileaCourseBookerMap = new HashMap<>();
   }

   public void putForUserId(String userId, AquabasileaCourseBooker aquabasileaCourseBooker) {
      userId2AquabasileaCourseBookerMap.put(userId, requireNonNull(aquabasileaCourseBooker));
   }

   public AquabasileaCourseBooker getForUserId(String userId) {
      return requireNonNull(userId2AquabasileaCourseBookerMap.get(userId), "No AquabasileaCourseBooker registered for user id '" + userId + "'!");
   }

   public void removeForUserId(String userId) {
      userId2AquabasileaCourseBookerMap.remove(userId);
   }

   /**
    * @return an unmodifiable map of all user-ids and their {@link AquabasileaCourseBooker}
    */
   public Map<String, AquabasileaCourseBooker> getUserId2AquabasileaCourseBookerMap() {
      return Collections.unmodifiableMap(userId2AquabasileaCourseBookerMap);
   }
}
