package com.aquabasilea.app.initialize.coursebooker;

import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class AquabasileaCourseBookerInitializerTest {

   @Autowired
   private AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;

   @Autowired
   private AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;

   @Test
   void testInitializeAquabasileaCourseBookerInitializer() {
      // Given
      String userId = "1234";
      UserAddedEvent userAddedEvent = createUserAddedEvent("peter", userId);

      // When
      aquabasileaCourseBookerInitializer.initialize(userAddedEvent);

      // Then
      AquabasileaCourseBooker aquabasileaCourseBooker = aquabasileaCourseBookerHolder.getForUserId(userId);
      assertThat(aquabasileaCourseBooker, is(notNullValue()));
      String infoString4State = aquabasileaCourseBooker.getInfoString4State();
      assertThat(infoString4State, is(TextResources.INFO_TEXT_INIT));
   }

   private static UserAddedEvent createUserAddedEvent(String username, String userId) {
      char[] userPwd = "1234".toCharArray();
      return new UserAddedEvent(username, "123", userId, userPwd);
   }
}