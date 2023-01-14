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
          <add-course
              class="tile"
              :key="addCourseRefreshKey"
              @error-occurred="errorOccurred"
              @refreshAddCourse="refreshAddCourse()"
              @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses">
          </add-course>
        </div>
        <weekly-courses-overview
            class="tile"
            :key="weeklyCoursesRefreshKey"
            @error-occurred="errorOccurred"
            @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
            @refreshWeeklyCourses="refreshWeeklyCourses()">
        </weekly-courses-overview>
      </div>
      <CAlert v-show="this.errorDetails" color="danger" class="error-details tile" style="justify-self: center">
        {{ this.errorDetails }}
      </CAlert>
    </div>
  </div>
</template>

<script>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
import CourseBookerStateOverview from "@/components/CourseBookerStateOverview";
import WeeklyCoursesOverview from "@/components/WeeklyCoursesOverview";
import AddCourse from "@/components/AddCourse";
import CommonAquabasileaRestApi from "@/mixins/CommonAquabasileaRestApi";
import RouterConstants from "@/router-constants";
import AuthService from "@/services/auth/auth.service";

export default {
  name: 'App',
  mixins: [CommonAquabasileaRestApi],
  components: {
    AddCourse,
    Loading,
    WeeklyCoursesOverview,
    CourseBookerStateOverview
  },
  data() {
    return {
      loginPath: RouterConstants.LOGIN_PATH,
      errorDetails: '',
      applicationTitle: 'Migros-Kurs Bucher',
      stagingMsg: 'Migros-Kurs-Bucher Webapplikation',
      courseStateOverviewRefreshKey: 0,
      weeklyCoursesRefreshKey: 0,
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
        this.errorDetails = null;
      }, 1000);
      console.log('courseStateOverview refreshed: ' + this.courseStateOverviewRefreshKey);
    },
    refreshWeeklyCourses: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
        this.$store.dispatch('aquabasilea/setIsLoading', false);
        this.errorDetails = null;
      }, 1000);
      console.log('weeklyCourses refreshed: ' + this.weeklyCoursesRefreshKey);
    },
    errorOccurred: function (error) {
      this.errorDetails = error;
      if (AuthService.isAuthenticationFailed(error)) {
        this.$store.dispatch('auth/logout');
        this.$router.push(this.loginPath);
        return;
      }
      console.log('AquabasileaDashboard.vue: errorOccurred : \'' + this.errorDetails + '\'');
    },
    refreshCourseStateOverviewAndWeeklyCourses: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
        this.courseStateOverviewRefreshKey += 1;
        this.$store.dispatch('aquabasilea/setIsLoading', false);
        this.errorDetails = null;
      }, 1000);
      console.log('weeklyCourses & courseStateOverview refreshed: ' + this.weeklyCoursesRefreshKey + ', ' + this.courseStateOverviewRefreshKey);
    },
    refreshAddCourse: function () {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      setTimeout(() => {
        console.log('refreshAddCourse refreshed: ' + this.addCourseRefreshKey);
        this.addCourseRefreshKey += 1;
        this.weeklyCoursesRefreshKey += 1;// after the course-defs are refreshed -> refresh current courses, may be one of them is without course-def now
        this.$store.dispatch('aquabasilea/setIsLoading', false);
        this.errorDetails = null;
      }, 2000);
    },
  },
  mounted() {
    this.errorDetails = null;
    this.$store.dispatch('aquabasilea/setIsLoading', false);
  }
}
</script>
<style scoped>
.course-state-overview {
  flex-grow: 2;
}
</style>