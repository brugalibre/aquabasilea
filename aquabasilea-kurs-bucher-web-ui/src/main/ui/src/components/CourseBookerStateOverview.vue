<template>
  <div class="grid-container">
    <h2>Status der App</h2>
    <div
        v-bind:class="{ isRunning: courseBookingStateDto.state === 'IDLE' || courseBookingStateDto.state === 'BOOKING',
        isNotRunning: courseBookingStateDto.state !== 'IDLE'
        }">
      <label> {{ courseBookingStateDto.stateMsg }} </label>
    </div>
    <CButton
        color="info"
        class="container-element-left"
        :disabled="courseBookingStateDto.state === 'BOOKING' || courseBookingStateDto.state === 'OFFLINE'"
        v-on:click="pauseOrResumeAquabasileaCourseBookerAndRefresh()">
      {{ courseBookingStateDto.pauseOrResumeButtonText }}
    </CButton>
    <CAccordion>
      <CAccordionItem :item-key="1">
        <CAccordionHeader>
          <label class="statistic-title">Statistik</label>
        </CAccordionHeader>
        <CAccordionBody>
          <div class="grid-container-60-40">
            <label class="statistic-attr">Letztes Aquabasilea-Kurs-Update</label>
            <span>{{ statisticsDto.lastCourseDefUpdate }}</span>
            <label class="statistic-attr">Nächstes Aquabasilea-Kurs-Update</label>
            <span>{{ statisticsDto.nextCourseDefUpdate }}</span>
            <label class="statistic-attr">Uptime</label>
            <span>{{ statisticsDto.uptimeRepresentation }}</span>
            <label class="statistic-attr">Anzahl Buchungen</label>
            <span>{{ statisticsDto.totalBookingCounter }}</span>
            <label class="statistic-attr">Erfolgsrate</label>
            <span>{{ statisticsDto.bookingSuccessRate }}%</span>
          </div>
        </CAccordionBody>
      </CAccordionItem>
    </CAccordion>
  </div>
</template>

<script>
import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';
import statisticsApi from '../mixins/StatisticsDefApi';
import '@coreui/coreui/dist/css/coreui.css';

export default {
  name: 'CourseBookerStateOverview',
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
      return this.$store.state.aquabasilea.weeklyCourses;
    },
    courseBookingStateDto: function () {
      return this.$store.state.aquabasilea.courseBookingStateDto
    },
    statisticsDto: function () {
      return this.$store.state.aquabasilea.statisticsDto
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

.isNotRunning {
  background-color: #ffcccb;
  border: firebrick solid 2px;
  border-radius: 5px;
  padding: 3px;
}

.isRunning {
  background-color: #90EE90;
  border: green solid 2px;
  border-radius: 5px;
  padding: 3px;
}

.statistic-attr {
  font-weight: bold;
  word-wrap: anywhere;
}

.statistic-title {
  font-weight: bold;
}

</style>