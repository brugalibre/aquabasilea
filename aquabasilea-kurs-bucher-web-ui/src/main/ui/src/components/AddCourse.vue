<template>
  <div id=addCourseForm>
    <h2>Neuen Kurs hinzufügen</h2>
    <h4>Kurs auswählen</h4>
    <div>
      <input
          id="courseDefFilter"
          v-model="courseDefFilter"
          type="text"
          name="courseDefFilter"
          placeholder="Kurs-filter"
          style="margin-bottom: 10px"
      >
      <multiselect
          class="course-def-selector"
          ref="courseDefDtosSelector"
          id="courseDefDtosSelector"
          mode="single"
          v-model="selectedCourseDef"
          :options="courseDefDtos"
          :label="'courseRepresentation'"
          :valueProp="'courseRepresentation'"
          noOptionsText="Keine Kurse verfügbar"
          :placeholder="'Kurs auswählen..'"
          :maxHeight=900
          :object=true
          @select="addCourseAndRefresh"
      >
        <template v-slot:option="{ option }">
          <span class="course-def-option">{{ option.courseRepresentation }}</span>
        </template>
      </multiselect>
    </div>
    <update-course-def
        ref="courseDefSelector"
        @refreshAddCourse="refreshAddCourse">
    </update-course-def>
    <div class="error-msg" v-if="postErrorDetails"> Fehler beim Hinzufügen des Kurses: {{ postErrorDetails }}</div>
  </div>
</template>

<script>
import weeklyCoursesApi from '../mixins/WeeklyCoursesApi';
import UpdateCourseDef from "@/components/UpdateCourseDef";
import CourseDefApi from "@/mixins/CourseDefApi";

export default {
  name: 'AddCourse',
  components: {UpdateCourseDef},
  mixins: [weeklyCoursesApi, CourseDefApi],
  data() {
    return {
      selectedCourseDef: '',
      courseDefFilter: '',
      postErrorDetails: null,
    }
  },
  watch: {
    courseDefFilter: {
      handler: function (newCourseDefFilter, oldCourseDefFilter) {
        if (oldCourseDefFilter !== newCourseDefFilter) {
          this.fetchCourseDefDtos(newCourseDefFilter);
          this.$refs.courseDefDtosSelector.open();
        }
      },
    },
  },
  methods: {
    refreshAddCourse: function () {
      this.$emit('refreshAddCourse');
    },
    addCourseAndRefresh: function () {
      const courseBody = JSON.stringify({
        courseName: this.selectedCourseDef.courseName,
        dayOfWeek: this.selectedCourseDef.dayOfWeek,
        courseLocationDto: this.selectedCourseDef.courseLocationDto,
        timeOfTheDay: this.selectedCourseDef.timeOfTheDay,
        isPaused: false,
        hasCourseDef: true,
        isCurrentCourse: false
      });
      this.addCourse(courseBody);
      this.selectedCourseDef = null;
      this.courseDefFilter = null;
      this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
    }
  },
  computed: {
    courseDefDtos: function () {
      return this.$store.getters.courseDefDtos
    },
  },
  mounted() {
    this.fetchCourseDefDtos("");
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

.error-msg {
  font-weight: bold;
  color: red
}
</style>
