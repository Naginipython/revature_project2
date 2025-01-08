import React, { useState } from "react";
import { Box, Typography, TextField, Button, Divider } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import "./Register.scss";

interface UserData {
  username: string;
  password: string;
  confirmPassword: string;
}

export const Register: React.FC = () => {
  const [userData, setUserData] = useState({
    username: "",
    password: "",
    confirmPassword: "",
  });
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Pass the user data to Personalize component
    if (userData.password !== userData.confirmPassword) {
      alert("Passwords do not match!");
    }else{
      navigate("/personalize", { state: { userData } });
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUserData((prev: UserData) => ({ ...prev, [name]: value }));
  };

  return (
    <Box className="register-container">
      {/* Left Side: Text */}
      <Box className="register-text">
        <Typography variant="h3" gutterBottom>
          We’re Here To Help!
        </Typography>
        <Typography variant="body1">
          We know that managing money isn’t always easy—because
          <span className="highlight-text"> we’ve been there too.</span> That’s
          why we developed tools to make handling your finances
          <span className="highlight-text"> simpler </span>
          and <span className="highlight-text"> less stressful.</span> Whether
          you’re saving, spending smarter, or just trying to figure it all out,
          we’re here to support you every step of the way.
          <span className="highlight-text"> Let’s tackle this together!</span>
        </Typography>
      </Box>

      {/* Right Side: Form */}
      <Box className="register-form">
        <Typography variant="h4" align="center" gutterBottom>
          Register
        </Typography>
        <Divider />
        <form onSubmit={handleSubmit}>
          <TextField
            className="textfield"
            fullWidth
            label="Username"
            variant="outlined"
            margin="normal"
            name="username"
            value={userData.username}
            onChange={handleChange}
          />
          <TextField
            className="textfield"
            fullWidth
            label="Password"
            variant="outlined"
            margin="normal"
            type="password"
            name="password"
            value={userData.password}
            onChange={handleChange}
          />
          <TextField
            className="textfield"
            fullWidth
            label="Confirm Password"
            variant="outlined"
            margin="normal"
            type="password"
            name="confirmPassword"
            value={userData.confirmPassword}
            onChange={handleChange}
          />
          <Button
            className="button2"
            type="submit"
            fullWidth
            variant="contained"
            sx={{ marginTop: 2 }}
          >
            Continue
          </Button>
        </form>
        {/* Link to Login */}
        <Box mt={2}>
          <Typography variant="h6" align="center">
            Already have an account?
            <Link to="/" className="login-link">
              Log in Now!
            </Link>
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};
