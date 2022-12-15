<template>
  <div class="col-md-12">
    <div class="card card-container">
      <img
          id="profile-img"
          src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
          class="profile-img-card"
      />
      <!--      <form @submit="handleRegister" :validation-schema="schema">-->
      <!--      <form @submit="handleRegister">-->
      <div>
        <div v-if="!successful">
          <div class="form-group">
            <label for="username">Benutzername</label>
            <CFormInput v-model="username" id="username" placeholder="Dein Migros Fitness Benutzername" name="username"
                        plactype="text" class="form-control"/>
            <!--            <ErrorMessage name="username" class="error-feedback"/>-->
          </div>
          <div class="form-group">
            <label for="password">Passwort</label>
            <CFormInput v-model="password" id="password" placeholder="Dein Migros Fitness Passwort" name="password"
                        type="password" class="form-control"/>
            <!--            <ErrorMessage name="password" class="error-feedback"/>-->
          </div>
          <div class="form-group">
            <label for="phoneNr">Handy-Nr</label>
            <!-- XXX hier war es ursprünglich ein Field typ! aber der ist immer protected-->
            <CFormInput v-model="userPhoneNr" id="phoneNr" name="phoneNr" type="text" class="form-control"
                        onsubmit="this.handleRegister()"/>
            <CAlert name="phoneNr" class="error-feedback"/>
          </div>
          <div class="form-group">
            <CButton color="info" class="register-button" :disabled="loading || !this.inputValid()"
                     v-on:click="this.handleRegister()">
              <span
                  v-show="loading"
                  class="spinner-border spinner-border-sm"
              ></span>
              Sign Up
            </CButton>
          </div>
        </div>
      </div>

      <div
          v-if="message"
          class="alert"
          :class="successful ? 'alert-success' : 'alert-danger'"
      >
        {{ message }}
      </div>
      <div>
        <label>Bereits registriert?
          Hier gehts zum</label>
        <router-link :to=loginPath>
          Login
        </router-link>
      </div>

    </div>
  </div>
</template>

<script>
// import * as yup from "yup";

// import AuthService from "@/services/auth/auth.service";
import LoggingService from "@/services/log/logging.service";
import RouterConstants from "@/router-constants";
// import axios from "axios";

export default {
  name: 'Register',
  data() {
    // const schema = yup.object().shape({
    //   username: yup.string()
    //       .required('Username is required!')
    //       .min(3, 'Must be at least 3 characters!')
    //       .max(20, 'Must be maximum 20 characters!'),
    //   password: yup.string()
    //       .required('Password is required!')
    //       .min(6, 'Must be at least 6 characters!')
    //       .max(40, 'Must be maximum 40 characters!'),
    //   eMail: yup.string()
    //       .required('EMail is required!')
    //       .email(),
    // });

    return {
      loginPath: RouterConstants.LOGIN_PATH,
      courseOverviewPath: RouterConstants.COURSE_OVERVIEW_PATH,
      username: '',
      password: '',
      userPhoneNr: '',
      successful: false,
      loading: false,
      message: '',
      // schema
    };
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    },
  },
  mounted() {
    if (this.loggedIn) {
      this.$router.push(this.courseOverviewPath);
    }
  },
  methods: {
    inputValid: function () {
      return this.username && this.userPhoneNr && this.password;
    },
    handleRegister: function () {
      this.message = "";
      this.successful = false;
      this.loading = true;
      this.$store.dispatch("auth/register", {
        username: this.username,
        password: this.password,
        userPhoneNr: this.userPhoneNr,
        roles: ['USER']
      }).then(response => {
            console.log('Register success!');
            this.successful = true;
            this.loading = false;
            this.message = response?.data;
          }
      ).catch(error => {
            this.message = LoggingService.extractErrorText(error);
            LoggingService.logError('Error while registering', this.message);
            this.successful = false;
          }
      ).finally(() => {
        console.log('register done');
        this.loading = false;
      });
    },
  },
};
</script>

<style scoped>

.register-button {
  margin-bottom: 35px;
  width: 100%;
}

label {
  display: block;
  margin-top: 10px;
}

.card-container.card {
  max-width: 350px !important;
  padding: 40px 40px;
}

.card {
  background-color: #f7f7f7;
  padding: 20px 25px 30px;
  margin: 0 auto 25px;
  margin-top: 50px;
  -moz-border-radius: 2px;
  -webkit-border-radius: 2px;
  border-radius: 2px;
  -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
}

.profile-img-card {
  width: 96px;
  height: 96px;
  margin: 0 auto 10px;
  display: block;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  border-radius: 50%;
}

.error-feedback {
  color: red;
}
</style>
