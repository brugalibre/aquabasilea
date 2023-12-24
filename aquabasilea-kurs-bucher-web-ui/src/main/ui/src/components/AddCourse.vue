<template>
  <div id=addCourseForm>
    <h2>Neuen Kurs hinzufügen</h2>
    <update-course-def
        ref="courseDefSelector"
        @refreshAddCourse="this.$emit('refreshAddCourse')">
    </update-course-def>
    <h5>Kurs auswählen</h5>
    <div class="grid-container">
      <multiselect
          class="course-def-selector"
          ref="courseDefDtosSelector"
          id="courseDefDtosSelector"
          :mode="'single'"
          v-model="selectedCourseDef"
          :options="courseDefDtos"
          :label="'courseRepresentation'"
          :valueProp="'courseRepresentation'"
          noOptionsText="Keine Kurse verfügbar"
          :placeholder="'Kurs auswählen (tippen zum Suchen)'"
          :maxHeight=900
          :searchable="true"
          :noResultsText="'Keine Ergebnisse'"
          :object=true
          @select="createCourseBodyAddAndRefresh"
      >
        <template v-slot:option="{ option }">
          <span class="course-def-option">{{ option.courseRepresentation }}</span>
        </template>
      </multiselect>
    </div>
    <ErrorBox ref="errorBox"/>
  </div>
</template>

<script>
import weeklyCoursesApi from '../mixins/WeeklyCoursesApi';
import UpdateCourseDef from "@/components/UpdateCourseDef";
import CourseDefApi from "../mixins/CourseDefApi";
import ErrorBox from "@/components/error/ErrorBox.vue";
import ErrorHandlingService from "@/services/error/error-handling.service";

export default {
  name: 'AddCourse',
  components: {
    UpdateCourseDef,
    ErrorBox
  },
  mixins: [weeklyCoursesApi, CourseDefApi],
  data() {
    return {
      selectedCourseDef: '',
    }
  },
  methods: {
    createCourseBodyAddAndRefresh: function () {
      const courseBody = JSON.stringify({
        courseName: this.selectedCourseDef.courseName,
        courseInstructor: this.selectedCourseDef.courseInstructor,
        courseDate: this.selectedCourseDef.courseDefDate,
        dayOfWeek: this.selectedCourseDef.dayOfWeek,
        courseLocationDto: this.selectedCourseDef.courseLocationDto,
        timeOfTheDay: this.selectedCourseDef.timeOfTheDay,
        isPaused: false,
        hasCourseDef: true,
        isCurrentCourse: false
      });
      this.addCourseAndRefresh(courseBody, error => ErrorHandlingService.handleError(this.$refs.errorBox, error),
          () => this.$emit('refreshCourseStateOverviewAndWeeklyCourses'));
      this.selectedCourseDef = null;
      this.courseDefFilter = '';
    }
  },
  computed: {
    courseDefDtos: function () {
      return this.$store.state.aquabasilea.courseDefDtos
    },
  },
  mounted() {
    this.fetchCourseDefDtos("", error => ErrorHandlingService.handleError(this.$refs.errorBox, error));
  }
}
</script>

<style src="@vueform/multiselect/themes/default.css">

.course-def-selector {
  grid-column-start: 1;
  grid-column-end: 3;
  grid-row-start: 2;
  grid-row-end: 2;
  font-size: 15px;
}

.course-def-option {
  height: auto;
  font-size: 14px;
}
</style>
