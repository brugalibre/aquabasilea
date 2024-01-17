<template>
  <div class="grid-container" v-show="this.isBookedCoursesTileVisible()">
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
        <div class="centered-flex spinner-border spinner-border-sm"></div>
        <span style="padding-left: 5px">{{ loadingMessage}}</span>
      </div>
    </div>
    <ErrorBox ref="errorBox"/>
  </div>
</template>
<script>
import aquabasileaCourseBookerApi from '../../mixins/AquabasileaCourseBookerApi';
import '@coreui/coreui/dist/css/coreui.css';
import {createConfirmDialog} from 'vuejs-confirm-dialog'
import ModalDialog from '../common/ModalDialog.vue'
import ErrorBox from "@/components/error/ErrorBox.vue";
import ErrorHandlingService from "@/services/error/error-handling.service";
import store from "@/store";

export default {
  name: 'BookedCourseOverview',
  mixins: [aquabasileaCourseBookerApi],
  data() {
    return {
      initLoadingMessage: 'Daten werden geladen..',
      loadingMessage: 'Daten werden geladen..',
    }
  },
  components: {
    ErrorBox,
  },
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
      this.dialog = createConfirmDialog(ModalDialog, {
        title: "Kurs " + bookedCourse2Delete.courseName + " annullieren",
        question: "Bist du sicher, dass du diesen Kurs annullieren möchtest?",
        confirmTxt: 'Jä',
        cancelTxt: 'Nai, doch nid'
      })
      this.dialog.reveal();
      this.dialog.onConfirm(() => {
        this.dialog.close();
        this.cancelCourseAndRefresh(bookedCourse2Delete.bookingIdTac,
            error => ErrorHandlingService.handleError(this.$refs.errorBox, error),
            result => {
          console.log(JSON.stringify(result));
              if (result.courseCancelResult === 'COURSE_CANCELED') {
                this.$emit('refreshBookedCourses');
              } else {
                store.dispatch('aquabasilea/setIsBookedCoursesLoading', false);
                ErrorHandlingService.handleError(this.$refs.errorBox, result.errorMsg);
              }
            });
      });
      this.dialog.onCancel(() => this.dialog.close());
    },
    cancelCourseAndRefresh: function (bookingIdTac, onErrorCallback, onSuccessCallback) {
      this.$store.dispatch('aquabasilea/setIsBookedCoursesLoading', true);
      this.cancelBookedCourseAndRefresh(bookingIdTac, onErrorCallback, onSuccessCallback);
    },
    isBookedCoursesTileVisible: function () {
      return this.bookedCourseDtos.length !== 0
          || this.isBookedCoursesLoading
          || this.$refs.errorBox?.hasErrors();
    },
    resetLoadingMessage: function () {
      this.loadingMessage = this.initLoadingMessage;
    },
    getLoadingText4Counter(counter) {
      if (counter === 0) {
        return this.initLoadingMessage;
      } else if (counter === 1) {
        return '\nMhh.. das dauert wohl etwas länger als geplant.';
      } else if (counter === 2) {
        return '\nOkey, wird sind gleich fertig, etwas Geduld noch..';
      } else if (counter >= 3) {
        return 'Waaaas, es lädt immer noch?! Jetzt aber nicht aufgeben, es ist bald geschafft!';
      } else {
        return '';
      }
    },
    setLoadingMessageWithTimeout: function (counter) {
      this.loadingMessage = this.getLoadingText4Counter(counter);
      setTimeout(() => {
        let newCounter = counter + 1;
        this.setLoadingMessageWithTimeout(newCounter);
      }, 10000);
    }
  },
  mounted() {
    this.fetchBookedCourses(error => ErrorHandlingService.handleError(this.$refs.errorBox, error), () => this.resetLoadingMessage());
    this.setLoadingMessageWithTimeout(0);
  }
}
</script>