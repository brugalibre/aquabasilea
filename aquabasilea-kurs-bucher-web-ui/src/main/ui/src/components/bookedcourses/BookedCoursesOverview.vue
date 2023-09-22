<template>
  <div class="grid-container" v-if="bookedCourseDtos.length !== 0 || isBookedCoursesLoading">
    <h2>Gebuchte Kurse</h2>
    <DialogsWrapper/>
    <div>
      <div v-for="bookedCourse in bookedCourseDtos" :key="bookedCourse.bookingIdTac"
             class="tile grid-container-60-40" style="padding-bottom: 10px">
        <div v-c-tooltip="{content: bookedCourse.tooltipText, placement: 'top'}">
          {{ bookedCourse.courseName }} um {{ bookedCourse.timeOfTheDay }} Uhr
        </div>
        <div class="centered-flex-items-center">
          <CButton
              color="info"
              size="sm"
              :disabled="isBookedCoursesLoading"
              v-on:click="showDialog(bookedCourse)">
            Annulieren
          </CButton>
        </div>
      </div>
      <div v-show="isBookedCoursesLoading" class="centered-flex-items-center">
        <div color="info" class="centered-flex spinner-border spinner-border-sm"></div>
        <div style="padding-left: 5px">Daten werden geladen..</div>
      </div>
    </div>
  </div>
</template>
<script>
import aquabasileaCourseBookerApi from '../../mixins/AquabasileaCourseBookerApi';
import '@coreui/coreui/dist/css/coreui.css';
import {createConfirmDialog} from 'vuejs-confirm-dialog'
import ModalDialog from '../common/ModalDialog.vue'

export default {
  name: 'BookedCourseOverview',
  mixins: [aquabasileaCourseBookerApi],
  computed: {
    bookedCourseDtos: function () {
      return this.$store.state.aquabasilea.bookedCourseDtos
    },
    isBookedCoursesLoading: function () {
      return this.$store.state.aquabasilea.isBookedCoursesLoading
    },
  },
  methods: {
    showDialog(bookedCourse2Delete) {
      this.dialog = createConfirmDialog(ModalDialog, {title: "Kurs " + bookedCourse2Delete.courseName + " annullieren",
        question: "Bist du sicher, dass du diesen Kurs annullieren möchtest?", confirmTxt: 'Jä', cancelTxt: 'Nei, doch nit'})
      this.dialog.reveal();
      this.dialog.onConfirm(() => {
        this.dialog.close();
        this.cancelCourseAndRefresh(bookedCourse2Delete.bookingIdTac);
      });
      this.dialog.onCancel(() => this.dialog.close());
    },
    cancelCourseAndRefresh: function (bookingIdTac) {
      this.$store.dispatch('aquabasilea/setIsBookedCoursesLoading', true);
      this.cancelBookedCourseAndRefresh(bookingIdTac);
    }
  },
  mounted() {
    this.fetchBookedCourses();
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
</style>