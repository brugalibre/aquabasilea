<template>
  <div id=addCourseForm>
    <h2>Neuen Kurs hinzufügen</h2>
    <div class="add-course-form">
      <input
          id="courseName"
          v-model="courseName"
          type="text"
          name="courseName"
          placeholder="Name des Kurses"
      >
      <days-of-week-selector
          ref="daysOfWeekSelector"
          v-model="dayOfWeek"
          v-bind:init-course-name="courseName"
          v-bind:initDayOfTheWeek="dayOfWeek"
      />
      <input
          id="timeOfTheDay"
          v-model="timeOfTheDay"
          type="time"
          name="timeOfTheDay"
      >
    </div>
    <p>
      <button :disabled="isSubmitButtonDisabled" v-on:click="addCourseAndRefresh">Kurs hinzufügen</button>
    </p>
    <div class="errorMsg" v-if="postErrorDetails"> Fehler beim Hinzufügen des Kurses: {{ postErrorDetails }}</div>
  </div>
</template>

<script>
import weeklyCoursesApi from '../mixins/WeeklyCoursesApi';
import DaysOfWeekSelector from "@/components/DaysOfWeekSelector";

export default {
  name: 'AddCourse',
  components: {DaysOfWeekSelector},
  mixins: [weeklyCoursesApi],
  data() {
    return {
      postErrorDetails: null,
      courseName: null,
      dayOfWeek: null,
      timeOfTheDay: '10:15',
    }
  },
  methods: {
    addCourseAndRefresh: function () {
      this.addCourse();
      this.courseName = null;
      this.dayOfWeek = null;
      this.timeOfTheDay = null;
      this.$refs.daysOfWeekSelector.reset();
      this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
    }
  },
  computed: {
    isSubmitButtonDisabled: function () {
      return !this.courseName || !this.dayOfWeek || !this.timeOfTheDay;
    }
  }
}
</script>

<style scoped>

.add-course-form {
  display: flex;
  justify-content: space-between;
}

.errorMsg {
  font-weight: bold;
  color: red
}

</style>
