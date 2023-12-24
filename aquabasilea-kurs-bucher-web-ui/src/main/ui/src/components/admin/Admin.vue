<template>
  <div class="centered-flex">
    <div class="tile grid-container">
      <h2>Kurs-Bucher Übersicht</h2>
      <div class="grid-container-60-40" style="max-width: 500px">
        <label class="statistic-attr">Registrierte Kursbucher</label>
        <span>{{ adminOverview.totalAquabasileaCourseBooker }}</span>
        <label class="statistic-attr">Uptime</label>
        <span>{{ adminOverview.uptimeRepresentation }}</span>
        <label class="statistic-attr">Anzahl Buchungen</label>
        <span>{{ adminOverview.totalBookingCounter }}</span>
        <label class="statistic-attr">Erfolgsrate</label>
        <span>{{ adminOverview.bookingSuccessRate }}%</span>
      </div>
    </div>
    <div class="tile grid-container">
      <h2>Anstehende Buchungen</h2>
      <div class="table">
        <table>
          <tr>
            <th id="courseName">Kurs Name</th>
            <th id="dayOfWeek">Wochentag</th>
            <th id="timeOfTheDay">Uhrzeit</th>
            <th id="courseLocation">Kurs Ort</th>
            <th id="user">Benutzer</th>
            <th id="userId">Benutzer-Id</th>
          </tr>
          <tr v-for="course in adminOverview.nextCurrentCourses" :key="course.id"
              v-bind:class="{ isPaused: course.isPaused, isAppPaused: course.isAppPaused, hasNoCourseDef: !course.hasCourseDef}"
          >
            <td class="table-cell">
              <div style="display: grid">
                <div>
                  <label>{{ course.courseName }}</label>
                </div>
              </div>
            </td>
            <td class="table-cell">
              <label
                  v-c-tooltip="{content: formatDate(course.courseDate), placement: 'top'}"
              >
                {{ course.dayOfWeek }}
              </label>
            </td>
            <td class="table-cell">
              <label>{{ course.timeOfTheDay }}</label>
            </td>
            <td class="table-cell">
              <label>
                {{ course.courseLocationDto.name }}
              </label>
            </td>
            <td class="table-cell">
              <label>{{ course.username }}</label>
            </td>
            <td class="table-cell">
              <label>{{ course.userId }}</label>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import LoggingService from "@/services/log/logging.service";
import AdminService from "@/services/admin/admin.service";
import CommonAquabasileaRestApi from "@/mixins/CommonAquabasileaRestApi";
import RouterConstants from "@/router-constants";
import AuthService from "@/services/auth/auth.service";

export default {
  name: 'Admin',
  mixins: [CommonAquabasileaRestApi],
  data() {
    return {
      loginPath: RouterConstants.LOGIN_PATH,
    };
  },
  computed: {
    adminOverview() {
      return this.$store.state.aquabasilea.adminOverview;
    },
  },
  methods: {
    getAdminOverview() {
      this.loading = true;
      AdminService.getAdminOverview(this.$store)
          .catch(error => {
            this.message = LoggingService.extractErrorText(error);
            if (AuthService.isAuthenticationFailed(error)) {
              this.$store.dispatch('auth/logout');
              this.$router.push(this.loginPath);
            }
          })
          .finally(() => this.loading = false);
    }
  },
  mounted() {
    this.getAdminOverview();
  }
}
</script>

<style scoped>

.table {
  overflow-x: auto;
}

.table-cell {
  padding-left: 13px;
}

label {
  word-break: break-all;
}

.isPaused {
  background: lightslategray;
}

.isAppPaused {
  background: lightgrey;
}

.hasNoCourseDef {
  background: #ffcccb;
  border: firebrick solid 2px !important;
  border-radius: 5px;
}

.statistic-attr {
  font-weight: bold;
  word-wrap: anywhere;
}

</style>