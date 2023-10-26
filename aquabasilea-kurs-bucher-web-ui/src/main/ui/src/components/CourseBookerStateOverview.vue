<template>
  <div class="grid-container">
    <h2>Status der App</h2>
    <div
        v-bind:class="{ isRunning: courseBookingStateDto.state === 'IDLE' || courseBookingStateDto.state === 'BOOKING',
        isNotRunning: courseBookingStateDto.state !== 'IDLE'
        }">
      <label v-bind:class="{
        courseBookingStateNotOfflineLabel: courseBookingStateDto.state === 'IDLE' || courseBookingStateDto.state === 'BOOKING',
        courseBookingStateOfflineLabel: courseBookingStateDto.state !== 'IDLE'}">
        {{ courseBookingStateDto.stateMsg }} </label>
    </div>
    <CButton
        color="info"
        class="container-element-left"
        :disabled="courseBookingStateDto.state === 'BOOKING' || courseBookingStateDto.state === 'OFFLINE'"
        v-on:click="evalRefreshNecessaryAndPauseOrResumeAquabasileaCourseBookerAndRefresh()">
      {{ courseBookingStateDto.pauseOrResumeButtonText }}
    </CButton>
    <CButton
        color="info"
        class="container-element-left"
        :disabled="courseBookingStateDto.state === 'BOOKING' || !this.getCurrentCourse?.id"
        v-on:click="bookCurrentCourseDryRun(this.getCurrentCourse.id)">
      Starte Testlauf
    </CButton>
    <CAccordion>
      <CAccordionItem :item-key="1">
        <CAccordionHeader>
          <label class="statistic-title">Statistik</label>
        </CAccordionHeader>
        <CAccordionBody>
          <div class="grid-container-60-40">
            <label class="statistic-attr">Letztes Migros-Kurs-Update</label>
            <span>{{ statisticsDto.lastCourseDefUpdate }}</span>
            <label class="statistic-attr">Nächstes Migros-Kurs-Update</label>
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
    <ErrorBox ref="errorBox"/>
  </div>
</template>

<script>
import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';
import statisticsApi from '../mixins/StatisticsDefApi';
import ErrorBox from "@/components/error/ErrorBox.vue";
import '@coreui/coreui/dist/css/coreui.css';
import ErrorHandlingService from "@/services/error/error-handling.service";

export default {
  name: 'CourseBookerStateOverview',
  mixins: [aquabasileaCourseBookerApi, statisticsApi],
  components: {
    ErrorBox
  },
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
    evalRefreshNecessaryAndPauseOrResumeAquabasileaCourseBookerAndRefresh: function () {
      this.pauseOrResumeAquabasileaCourseBookerAndRefresh(this.needsWeeklyCoursesRefresh,
          error => ErrorHandlingService.handleError(this.$refs.errorBox, error),
          () => this.refresh());
    },
    refresh: function () {
      if (this.needsWeeklyCoursesRefresh) {
        this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
      } else {
        this.$emit('refreshCourseStateOverview');
      }
    },
  },
  mounted() {
    this.fetchCourseBookingStateDto(error => ErrorHandlingService.handleError(this.$refs.errorBox, error));
    this.fetchStatisticsDto(error => ErrorHandlingService.handleError(this.$refs.errorBox, error));
  }
}
</script>
<style scoped>

.courseBookingStateOfflineLabel {
  text-align: center;
  display: block;
}

.courseBookingStateNotOfflineLabel {
  text-align: left;
}

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