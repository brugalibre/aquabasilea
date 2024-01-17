package com.aquabasilea.rest.model.coursebooker.cancel;

import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResult;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResultDetails;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record CourseCancelResultDto(CourseCancelResult courseCancelResult, String errorMsg) {

   /**
    * Creates a new {@link CourseCancelResultDto} from the given {@link CourseCancelResultDetails}
    *
    * @param courseCancelResultDetails the {@link CourseCancelResultDetails}
    * @return a new {@link CourseCancelResultDto}
    */
   public static CourseCancelResultDto of(CourseCancelResultDetails courseCancelResultDetails) {
      return new CourseCancelResultDto(courseCancelResultDetails.courseCancelResult(), courseCancelResultDetails.errorMsg());
   }
}
