import React, { useState } from "react";
import { Box, Typography, TextField, Button, Divider, FormControlLabel, Switch, Snackbar, Alert } from "@mui/material";
import { Link, useLocation, useNavigate } from "react-router-dom"; // To access passed props
import "./Personalize.scss";
import axios from "axios";
import useStore from "../../stores";
import backendHost from "../../backendHost";

interface UserData {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

export const Personalize: React.FC = () => {
  const { state } = useLocation(); // Access the state passed via navigation
  const [userData, setUserData] = useState({
    ...state.userData, // Spread the previous data (username, password)
    firstName: "",
    lastName: "",
    email: "",
    role: "ROLE_EMPLOYEE",
  });
  const navigate = useNavigate();
  const setSnackbar = useStore((state) => state.setSnackbar);
  const [errorAlert, setErrorAlert] = useState({ open: false, message: "" });
  const [manager, setManager] = useState(false);

  const registerUser = async (data: UserData) => {
    try {
      const response = await axios.post(
        `${backendHost}/users/register`,
        data,
        { withCredentials: true },
      ); // Replace with your API endpoint
      console.log("User registered successfully:", response.data);
      // Redirect or show success message here
      // alert("Registration successful!");
      setSnackbar(true, "Registration successful!");
      navigate("/"); // Example: navigate to login
    } catch (error) {
      console.error("Error during registration:", error);
      // Handle error (e.g., show error message)
      setErrorAlert({ open: true, message: "Error during registration!" });
    }
  };

  const handlePersonalizeSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate the data
    if (userData.firstName === "" || userData.lastName === "" || userData.email === "") {
      setErrorAlert({ open: true, message: "Please fill out all fields!" });
      return;
    }

    // Combine data from both steps (Register and Personalize)
    const completeData = {
      username: userData.username,
      password: userData.password,
      firstName: userData.firstName,
      lastName: userData.lastName,
      email: userData.email,
      role: userData.role,
    };
    // Call API to submit the complete registration data
    await registerUser(completeData);
    // After successful registration, navigate to the dashboard or another page
    // navigate("/dashboard");
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.name === "manager") {
      setManager(e.target.checked);
      setUserData((prev: UserData) => ({ ...prev, role: e.target.checked ? "ROLE_MANAGER" : "ROLE_EMPLOYEE" }));
    }
    else{
      const { name, value } = e.target;
      setUserData((prev: UserData) => ({ ...prev, [name]: value }));
    }
  };

  return (
    <Box className="personalize-container">
      {/* Left Side: Text */}
      <Box className="personalize-text">
        <Typography variant="h3" gutterBottom>
          Get To Know You!
        </Typography>
        <Typography variant="body1">
          We’d love to get to know you better! By sharing your first name, last
          name, and email, we can
          <span className="highlight-text"> personalize </span> our experience
          on our site and keep you in the loop with
          <span className="highlight-text"> updates</span> and info you’ll love.
          Don’t worry— we’ll always
          <span className="highlight-text"> respect your privacy</span> and keep
          your
          <span className="highlight-text"> details safe!</span>
        </Typography>
      </Box>

      {/* Right Side: Form */}
      <Box className="personalize-form">
        <Typography variant="h4" align="center" gutterBottom>
          Personalize
        </Typography>
        <Divider />
        <form onSubmit={handlePersonalizeSubmit}>
          <TextField
            className="textfield"
            fullWidth
            label="First Name"
            variant="outlined"
            margin="normal"
            name="firstName"
            value={userData.firstName}
            onChange={handleChange}
          />
          <TextField
            className="textfield"
            fullWidth
            label="Last Name"
            variant="outlined"
            margin="normal"
            name="lastName"
            value={userData.lastName}
            onChange={handleChange}
          />
          <TextField
            className="textfield"
            fullWidth
            label="Email"
            variant="outlined"
            margin="normal"
            name="email"
            value={userData.email}
            onChange={handleChange}
          />
          
          <FormControlLabel sx={{marginTop: 2, display: "block"}}
          control={
            <Switch checked={manager} onChange={handleChange} name="manager" />
          }
          label="Make Manager"
        />
          <Button
            className="button3"
            type="submit"
            fullWidth
            variant="contained"
            sx={{ marginTop: 2 }}
          >
            Save & Continue
          </Button>
        </form>

        {/* Go Back Link */}
        <Box mt={2}>
          <Typography variant="h6" align="center">
            <Link to="/register" className="go-back-link">
              Go Back
            </Link>
          </Typography>
        </Box>
      </Box>
          
                {/* Error Snackbar */}
                      <Snackbar
                              open={errorAlert.open}
                              autoHideDuration={3000}
                              onClose={()=>setErrorAlert({open: false, message: ""})}
                              anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                            >
                              <Alert onClose={()=>setErrorAlert({open:false,message:""})} severity="error" sx={{ width: '100%' }}>
                                {errorAlert.message}
                              </Alert>
                      </Snackbar>
    </Box>
  );
};
