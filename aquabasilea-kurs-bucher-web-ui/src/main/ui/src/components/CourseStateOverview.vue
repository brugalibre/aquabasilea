<template>
  <div>
    <h2>Status der App</h2>
    <div
        v-bind:class="{ isRunning: courseBookingStateDto.state === 'IDLE',
        isNotRunning: courseBookingStateDto.state !== 'IDLE'
        }">
      <label class="state-label"> {{ courseBookingStateDto.stateMsg }} </label>
    </div>
    <div>
      <button
          class="container-element-left"
          :disabled="courseBookingStateDto.state === 'PAUSED' || courseBookingStateDto.state === 'BOOKING'"
          v-on:click="pauseOrResumeAquabasileaCourseBookerAndRefresh()">
        App pausieren
      </button>
      <button
          class="container-element-right"
          :disabled="courseBookingStateDto.state === 'IDLE' || courseBookingStateDto.state === 'BOOKING'"
          v-on:click="pauseOrResumeAquabasileaCourseBookerAndRefresh()">
        App reaktivieren
      </button>
    </div>
  </div>
</template>

<script>
import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';

export default {
  name: 'CourseStateOverview',
  mixins: [aquabasileaCourseBookerApi],
  computed: {
    /**
     * If we are reactivating the app, and we have at least one non-paused course and none of those courses
     * is the current course, then we have to refresh the WeeklyCourses in order to display the current course
     */
    needsWeeklyCoursesRefresh: function () {
      return this.getCurrentCourse === null || this.getCurrentCourse === undefined
          && this.courseBookingStateDto.state === 'PAUSED'
          && this.weeklyCourses.courseDtos
              .filter(courseDto =>courseDto.isPaused !== undefined && courseDto.isPaused !== true).length > 0;
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
  }
}
</script>
<style scoped>

.state-label {
  line-break: auto;
}

.isNotRunning {
  background-color: #ffcccb;
  margin-bottom: 5px;
  padding: 3px;
}

.container-element-left {
  margin-right: 5px;
}

.container-element-right {
  margin-left: 5px;
}

.isRunning {
  background-color: #90EE90;
  margin-bottom: 5px;
  padding: 3px;
}
</style>