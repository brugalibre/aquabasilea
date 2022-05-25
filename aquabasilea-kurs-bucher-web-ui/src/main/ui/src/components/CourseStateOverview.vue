<template>
  <div class="app-status">
    <h2>Status der App</h2>
    <div
        v-bind:class="{ isRunning: courseBookingStateDto.state === 'IDLE',
        isNotRunning: courseBookingStateDto.state !== 'IDLE'
        }">
      <label class="state-label"> {{ courseBookingStateDto.stateMsg }} </label>
    </div>
    <button
        class="container-element-left"
        :disabled="courseBookingStateDto.state === 'BOOKING'"
        v-on:click="pauseOrResumeAquabasileaCourseBookerAndRefresh()">
      {{ courseBookingStateDto.pauseOrResumeButtonText }}
    </button>
    <div class="grid-container-60-40">
      <label>Letztes Aquabasilea-Kurs-Update</label>
      <span>{{ statisticsDto.lastCourseDefUpdate }}</span>
      <label>Nächstes Aquabasilea-Kurs-Update</label>
      <span>{{ statisticsDto.nextCourseDefUpdate }}</span>
    </div>
  </div>
</template>

<script>
import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';
import statisticsApi from '../mixins/StatisticsDefApi';

export default {
  name: 'CourseStateOverview',
  mixins: [aquabasileaCourseBookerApi, statisticsApi],
  computed: {
    /**
     * If we are reactivating the app, and we have at least one non-paused course and none of those courses
     * is the current course, then we have to refresh the WeeklyCourses in order to display the current course
     */
    needsWeeklyCoursesRefresh: function () {
      return this.getCurrentCourse === null || this.getCurrentCourse === undefined
          && this.courseBookingStateDto.state === 'PAUSED'
          && this.weeklyCourses.courseDtos
              .filter(courseDto => courseDto.isPaused !== undefined && courseDto.isPaused !== true).length > 0;
    },
    getCurrentCourse: function () {
      return this.weeklyCourses.courseDtos
          .find(courseDto => courseDto.isCurrentCourse && (courseDto.isPaused !== undefined && courseDto.isPaused === false));
    },
    weeklyCourses: function () {
      return this.$store.getters.weeklyCourses;
    },
    courseBookingStateDto: function () {
      return this.$store.getters.courseBookingStateDto
    },
    statisticsDto: function () {
      return this.$store.getters.statisticsDto
    },
  },
  methods: {
    pauseOrResumeAquabasileaCourseBookerAndRefresh: function () {
      this.pauseOrResumeAquabasileaCourseBooker();
      if (this.needsWeeklyCoursesRefresh) {
        this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
      } else {
        this.$emit('refreshCourseStateOverview');
      }
    },
  },
  mounted() {
    this.fetchCourseBookingStateDto();
    this.fetchStatisticsDto();
  }
}
</script>
<style scoped>

.state-label {
  line-break: auto;
}

.isNotRunning {
  background-color: #ffcccb;
  border: firebrick solid 2px;
  border-radius: 5px;
  margin-bottom: 5px;
  padding: 3px;
}

.isRunning {
  background-color: #90EE90;
  border: green solid 2px;
  border-radius: 5px;
  margin-bottom: 5px;
  padding: 3px;
}

</style>