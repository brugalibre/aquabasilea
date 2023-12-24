<template>
  <div>
    <h1> {{ stagingMsg }} </h1>
    <loading v-model:active="isLoading"
             :is-full-page="true"/>
    <div>
      <div class="centered-flex">
        <div class="content-left-side">
          <course-booker-state-overview
              class="tile course-state-overview"
              :key="courseStateOverviewRefreshKey"
              @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
              @refreshCourseStateOverview="refreshCourseStateOverview()">
          </course-booker-state-overview>
          <booked-courses-overview
              class="tile course-state-overview"
              :key="bookedCoursesRefreshKey"
              @refreshBookedCourses="refreshBookedCourses()">
          </booked-courses-overview>
          <add-course
              class="tile"
              :key="addCourseRefreshKey"
              @refreshAddCourse="refreshAddCourse()"
              @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses">
          </add-course>
        </div>
        <weekly-courses-overview
            class="tile"
            :key="weeklyCoursesRefreshKey"
            @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
            @refreshWeeklyCourses="refreshWeeklyCourses()">
        </weekly-courses-overview>
      </div>
    </div>
  </div>
</template>

<script>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
import CourseBookerStateOverview from "@/components/CourseBookerStateOverview";
import WeeklyCoursesOverview from "@/components/WeeklyCoursesOverview";
import BookedCoursesOverview from "@/components/bookedcourses/BookedCoursesOverview";
import AddCourse from "@/components/AddCourse";
import CommonAquabasileaRestApi from "@/mixins/CommonAquabasileaRestApi";
import RouterConstants from "@/router-constants";

export default {
  name: 'App',
  mixins: [CommonAquabasileaRestApi],
  components: {
    AddCourse,
    Loading,
    WeeklyCoursesOverview,
    BookedCoursesOverview,
    CourseBookerStateOverview
  },
  data() {
    return {
      loginPath: RouterConstants.LOGIN_PATH,
      applicationTitle: 'ACTIV Fitness Bucher',
      stagingMsg: 'ACTIV Fitness Kurs-Bucher Webapplikation',
      courseStateOverviewRefreshKey: 0,
      weeklyCoursesRefreshKey: 0,
      bookedCoursesRefreshKey: 0,
      addCourseRefreshKey: 0,
    };
  },
  computed: {
    isLoading: function () {
      return this.$store.state.aquabasilea.isLoading;
    },
  },
  watch: {
    applicationTitle: {
      immediate: true,
      handler() {
        document.title = this.applicationTitle;
      }
    }
  },
  methods: {
    refreshCourseStateOverview: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        this.courseStateOverviewRefreshKey += 1;
        this.$store.dispatch('aquabasilea/setIsLoading', false);
      }, 1000);
      console.log('courseStateOverview refreshed: ' + this.courseStateOverviewRefreshKey);
    },
    refreshWeeklyCourses: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
        this.$store.dispatch('aquabasilea/setIsLoading', false);
      }, 1000);
      console.log('weeklyCourses refreshed: ' + this.weeklyCoursesRefreshKey);
    },
    refreshCourseStateOverviewAndWeeklyCourses: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
        this.courseStateOverviewRefreshKey += 1;
        this.$store.dispatch('aquabasilea/setIsLoading', false);
      }, 1000);
      console.log('weeklyCourses & courseStateOverview refreshed: ' + this.weeklyCoursesRefreshKey + ', ' + this.courseStateOverviewRefreshKey);
    },
    refreshBookedCourses: function () {
      setTimeout(() => {
        this.bookedCoursesRefreshKey += 1;
      }, 2000);
      console.log('bookedCoursesRefreshKey refreshed: ' + this.bookedCoursesRefreshKey);
    },
    refreshAddCourse: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        console.log('refreshAddCourse refreshed: ' + this.addCourseRefreshKey);
        this.addCourseRefreshKey += 1;
        this.weeklyCoursesRefreshKey += 1;// after the course-defs are refreshed -> refresh current courses, may be one of them is without course-def now
        this.$store.dispatch('aquabasilea/setIsLoading', false);
      }, 2000);
    },
  },
  mounted() {
    this.$store.dispatch('aquabasilea/setIsLoading', false);
  }
}
</script>
<style scoped>
.course-state-overview {
  flex-grow: 2;
}
</style>