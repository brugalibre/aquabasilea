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
          <div>
            <label for="username">Username</label>
            <CFormInput v-model="username" id="username" name="username" type="text"/>
            <CAlert name="username" class="error-feedback"/>
          </div>
          <div style="border: darkred solid 3px">
            <label for="password">Password</label>
            <CFormInput v-model="password" id="password" name="password" type="password"/>
            <CAlert name="password" class="error-feedback"/>
          </div>

          <div style="border: darkred solid 3px">
            <CButton color="info" class="form-group register-button" :disabled="loading"
                     v-on:click="this.handleRegister()" style="justify-self: center">
              <span
                  v-show="loading"
                  class="spinner-border spinner-border-sm"
              ></span>
              Sign Up
            </CButton>
          </div>
        </div>
      </div>
      <div class="form-group">
        <CAlert v-if="successMessage" color="success" class="error-details" style="justify-self: center">
          {{ successMessage }}
        </CAlert>
        <CAlert v-if=errorMessage color="danger" class="error-details" style="justify-self: center">
          {{ errorMessage }}
        </CAlert>
      </div>
    </div>
  </div>
</template>

<script>
// import * as yup from "yup";

// import AuthService from "@/services/auth/auth.service";
import LoggingService from "@/services/log/logging.service";
// import axios from "axios";
import AuthService from "@/services/auth/auth.service";

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
      username: '',
      password: '',
      successful: false,
      loading: false,
      successMessage: '',
      errorMessage: '',
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
      this.$router.push('/manage');
    }
  },
  methods: {
    handleRegister: function () {
      this.successMessage = "";
      this.successful = false;
      this.loading = true;
      AuthService.register({
            username: this.username,
            password: this.password,
            roles: ['ROLE_USER']
          }
      ).then(response => {
            console.log('register response: ' + JSON.stringify(response));
            this.$store.dispatch('auth/registerSuccess');
            this.successful = true;
            this.loading = false;
            this.successMessage = response?.data?.data;
            this.errorMessage = null;
          }
      ).catch(error => {
            LoggingService.logError('Error while registering', JSON.stringify(error));
            this.errorMessage = LoggingService.extractErrorText(error);
            this.successMessage = null;
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

.register-button {
  justify-self: center;
  margin-bottom: 10px;
}

.error-feedback {
  color: red;
}
</style>
