<template>
  <div id=addCourseForm>
    <h2>Neuen Kurs hinzufügen</h2>
    <h4>Kurs auswählen</h4>
    <div class="grid-container">
      <input
          id="courseDefFilter"
          v-model="courseDefFilter"
          type="text"
          name="courseDefFilter"
          placeholder="Kurs-filter"
      >
      <select
          id="courseDefDtosSelector"
          name="courseDefDtos"
          v-model="selectedCourseDef">
        <option
            v-for="courseDef in courseDefDtos" :key="courseDef"
            v-bind:value="courseDef"> {{ courseDef.courseRepresentation }}
        </option>
      </select>
      <div></div>
      <button :disabled="isSubmitButtonDisabled" v-on:click="addCourseAndRefresh">Kurs hinzufügen</button>
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
      handler: function (newCourseDefFilter) {
        this.fetchCourseDefDtos(newCourseDefFilter);
      },
    },
    courseDefDtos: {
      handler: function (newCourseDefDtos) {
        this.selectedCourseDef = newCourseDefDtos[0];
      },
    }
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
        isCurrentCourse: false
      });
      this.addCourse(courseBody);
      this.selectedCourseDef = null;
      this.courseDefFilter = null;
      this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
    }
  },
  computed: {
    isSubmitButtonDisabled: function () {
      return !this.selectedCourseDef;
    },
    courseDefDtos: function () {
      return this.$store.getters.courseDefDtos
    },
  },
  mounted() {
    this.fetchCourseDefDtos("");
  }
}
</script>

<style scoped>

.error-msg {
  font-weight: bold;
  color: red
}

</style>
